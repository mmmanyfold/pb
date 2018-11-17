(ns pb.views.student-id
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [cljsjs.moment]
            [clojure.string :as string]
            [pb.components.loading :refer [loading-component]]
            [pb.components.captcha :refer [captcha-component]]
            [ajax.core :as ajax :refer [GET POST]]
            [pb.config :as config]))

(def error-code (rg/atom nil))
(def code-sent? (rg/atom false))

(declare send-code-component)
(declare check-code-component)

(defn error-handler [response]
  (reset! error-code (:status response)))

(defn check-code-success-handler [response]
  (rf/dispatch [:set-voter-id (:id response)])
  (reset! error-code nil)
  (rf/dispatch [:set-active-view :proposals-view
                @(rf/subscribe [:election-slug])]))

(defn check-code [code election]
  (GET "/api/checkcode"
       {:handler         check-code-success-handler
        :error-handler   error-handler
        :response-format (ajax/json-response-format {:keywords? true})
        :format          :raw
        :params          {:voter-code code
                          :election   election}}))

(defn send-code-success-handler []
  (reset! code-sent? true)
  (reset! error-code nil))

(defn send-code [campus additional-id phone-number election]
  (POST "/api/votercode"
        {:handler       send-code-success-handler
         :error-handler error-handler
         :format        (ajax/json-request-format)
         :params        {:campus        campus
                         :additional-id additional-id
                         :election      election
                         :phone-number  phone-number}}))

(defn error-component []
  (when-not (nil? @error-code)
    (if (= @error-code 404)
      [:div.error.not-found
       "The voting code you entered is not valid. Please ensure the code is entered correctly."]
      [:div.error.already-voted
       "We already got your vote!"])))

(defn student-id-view [election-slug]
  (reset! error-code nil)
  (let [now (js/Date.)
        query (str "{ elections(q: \"fields.shortTitle=" election-slug
                   "\") {
                     title
                     additionalIdLabel
                     startOnline
                     endOnline
                     maxSelection
                     surveyUrl
                     displayFormat
                     sys { id }
                     proposalRefs {
                       sys { id }}
                   }}")]
    (rf/dispatch [:get-contentful-data :election-in-view query :election])
    (reset! code-sent? false)
    (fn []
      (if-let [{additionalIdLabel :additionalIdLabel
                startOnline       :startOnline
                endOnline         :endOnline
                {id :id}          :sys} @(rf/subscribe [:election-in-view])]
        (if (> (js/Date. endOnline) now)
          (if (> (js/Date. startOnline) now)
            ;; if online voting hasn't started
            [:div.voting-code-view
             [:h1 (str "Voting opens on "
                       (.format (js/moment startOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]]

            ;; if online voting is open
            [:div.voting-code-view
             (if (= @code-sent? false)
               ;; if voting code hasn't been sent
               [send-code-component additionalIdLabel id]
               ;; if voting code has been sent
               [check-code-component id])])

          ;; if online voting has ended
          [:div.voting-code-view
           [:h1 (str "Voting ended on "
                     (-> (js/moment endOnline)
                         (.format "dddd, MMMM Do YYYY [at] h:mm a.")))]])
        [loading-component]))))


(defn send-code-component [additionalIdLabel id]
  (let [additionalId (rg/atom "")
        empty-campus (rg/atom false)
        wrong-id (rg/atom false)
        campus (rg/atom "")]
    (fn []
      [:div
       [:h1 "Enter your student ID"]
       [:form.voter-auth-form
        {:on-submit (fn [e]
                      (.preventDefault e)
                      (.stopPropagation e))}
        (when additionalIdLabel
          [:div
           [:div.flexrow.input-group-prepend
            [:select {:id        "campus"
                      :class     (str "form-control" (when @empty-campus " input-error"))
                      :name      "campus"
                      :on-change (fn [e]
                                   (reset! campus (-> e .-target .-value))
                                   (reset! empty-campus (= (count @campus) 0)))}
             [:option {:default-value :disabled
                       :value         ""} "Campus:"]
             [:option {:value "cudenver"} "CU Denver"]
             [:option {:value "ccd"}      "CCD"]
             [:option {:value "msu"}      "MSU"]]
            [:div.required "*"]
            [:input
             {:type        "text"
              :pattern     "/^[a-z0-9]+$/i"
              :class       (str "form-control" (when @wrong-id " input-error"))
              :placeholder "Student ID"
              :maxLength   9
              :value       @additionalId
              :on-change   (fn [e]
                             (reset! additionalId (-> e .-target .-value))
                             (reset! wrong-id (or (not (re-matches #"(?i)[a-z0-9]{9}" @additionalId)))))
              :on-blur     (fn []
                             (reset! empty-campus (= (count @campus) 0))
                             (reset! wrong-id (or (not (re-matches #"(?i)[a-z0-9]{9}" @additionalId)))))}]
            [:div.required "*"]]
           [:p [:small "Student IDs will be verified by each campus after the election, before the final vote count. Any votes associated with an invalid ID will not be counted."]]

           (when-not config/debug?
             [captcha-component])
           [:a {:on-click nil}
            [:button#send-code
             {:type     "submit"
              :disabled (or (when-not config/debug?
                              (nil? @(rf/subscribe [:captcha-passed])))
                            (when-not (nil? additionalIdLabel)
                              (or (not (re-matches #"(?i)[a-z0-9]{9}" @additionalId))
                                  (= (count @campus) 0))))}
             "CONTINUE"]]])
        [error-component]]])))