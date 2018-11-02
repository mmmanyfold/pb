(ns pb.views.voting-code
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [cljsjs.moment]
            [pb.components.loading :refer [loading-component]]
            [pb.components.captcha :refer [captcha-component]]
            [ajax.core :as ajax :refer [GET POST]]
            [pb.config :as config]))

(def code (rg/atom nil))
(def error-code (rg/atom nil))
(def code-sent? (rg/atom false))

(declare send-code-component)
(declare check-code-component)

(defn error-handler [response]
  (reset! error-code (:status response)))

(defn check-code-success-handler [response]
  (rf/dispatch [:set-voter-id (:id response)])
  (reset! error-code nil)
  (set! (.. js/window -location -href) (str (.. js/window -location -href) "/proposals")))

(defn check-code [code election]
  (GET "/api/checkcode"
       {:handler check-code-success-handler
        :error-handler error-handler
        :response-format (ajax/json-response-format {:keywords? true})
        :format :raw
        :params {:voter-code code
                 :election election}}))

(defn send-code-success-handler []
  (reset! code-sent? true))

(defn send-code [additional-id phone-number election]
  (POST "/api/votercode"
       {:handler send-code-success-handler
        :error-handler error-handler
        :format (ajax/json-request-format)
        :params {:additional-id additional-id
                 :election election
                 :phone-number phone-number}}))

(defn error-component []
  (when-not (nil? @error-code)
    (if (= @error-code 404)
      [:div.error.not-found
       "The voting code you entered is not valid. Please ensure the code is entered correctly, or follow the steps above to get your unique code."]
      [:div.error.already-voted
       "We already got your vote!"])))

(defn voting-code-view [election-slug]
  (reset! error-code nil)
  (let [now (js/Date.)
        query (str "{ elections(q: \"fields.shortTitle=" election-slug
                   "\") {
                     title
                     additionalIdLabel
                     startOnline
                     endOnline
                     maxSelection
                     sys { id }
                     proposalRefs {
                       sys { id }}
                   }}")]
    (rf/dispatch [:get-contentful-data :election-in-view query :election])
    (reset! code-sent? false)
    (fn []
      (if-let [{additionalIdLabel :additionalIdLabel
               startOnline :startOnline
               endOnline :endOnline
               {id :id} :sys} @(rf/subscribe [:election-in-view])]
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
                  (.format (js/moment endOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]])
        [loading-component]))))


(defn send-code-component [additionalIdLabel id]
  (let [phone-number (rg/atom "")
        phone-number-match (rg/atom "")
        additionalId (rg/atom "")
        campus (rg/atom "Campus:")]
    (fn []
      [:div
       [:h1 "Create unique voting code"]
       [:form.voter-auth-form
        (when-not (nil? additionalIdLabel)
          [:div
           [:div.flexrow.input-group-prepend
            [:select {:id "campus" :class "form-control" :name "campus"
                      :on-change (fn [e]
                                   (let [input (-> e .-target .-value)]
                                     (reset! campus input)))}
             [:option {:default-value :disabled} "Campus:"]
             [:option {:value "cudenver"} "CU Denver"]
             [:option {:value "ccd"} "CCD"]
             [:option {:value "msu"} "MSU"]]
            [:div.required "*"]
            [:input.form-control
             {:type "text"
              :placeholder "Student ID"
              :maxLength 10
              :value @additionalId
              :on-change (fn [e]
                           (let [input (-> e .-target .-value)]
                             (reset! additionalId input)))}]
            [:div.required "*"]]
           [:p [:small "Student IDs will be verified by each campus after the election, before the final vote count. Any votes associated with an invalid ID will not be counted."]]])
        [:div.flexrow.input-group-prepend
         [:input.form-control
          {:type "text"
           :placeholder "Enter Phone Number"
           :maxLength 10
           :value @phone-number
           :on-change (fn [e]
                        (let [input (-> e .-target .-value)]
                          (reset! phone-number input)))}]
         [:div.required "*"]]

        [:div.flexrow.input-group-prepend
         [:input.form-control
          {:type "text"
           :placeholder "Verify Phone Number"
           :maxLength 10
           :value @phone-number-match
           :on-change (fn [e]
                        (let [input (-> e .-target .-value)]
                          (reset! phone-number-match input)))}]
         [:div.required "*"]]

        [:h4 [:b "A text message with an 8-digit voting code will be sent to this phone number."]]
        [:p [:small "Your phone number will NEVER be shared and will be deleted automatically after this election."]]
        (when-not config/debug?
          [captcha-component])
        [:a {:on-click #(send-code @additionalId @phone-number-match id)}
         [:input#send-code
          {:type "button"
           :value "SEND MY CODE"
           :disabled (or (< (count @phone-number-match) 10)
                         (not= @phone-number @phone-number-match)
                         (when-not config/debug?
                           (nil? @(rf/subscribe [:captcha-passed])))
                         (when-not (nil? additionalIdLabel)
                           (< (count @additionalId) 9)
                           (= @campus "Campus:")))}]]]
       [error-component]])))

(defn check-code-component [id]
  [:div
   [:br]
   [:h2 "Check your text messages!"]
   [:h1 "Enter the 8-digit code:"]
   [:form.voter-auth-form
    [:div.flex-row-wrap
     [:input.form-control
      {:type "text"
       :placeholder "12345678"
       :maxLength 8
       :value @code
       :on-change (fn [e]
                    (let [input (-> e .-target .-value)]
                      (reset! code input)))}]

     [:a {:on-click #(check-code @code id)}
      [:input#submit-code
       {:type "button"
        :value "CONTINUE"
        :disabled (< (count @code) 8)}]]]]
   [error-component]])
