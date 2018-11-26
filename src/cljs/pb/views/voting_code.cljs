(ns pb.views.voting-code
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [cljsjs.moment]
            [clojure.string :as string]
            [pb.components.loading :refer [loading-component]]
            [pb.components.captcha :refer [captcha-component]]
            [ajax.core :as ajax :refer [GET POST]]
            [pb.config :as config]
            [pb.db :refer [translations-db]]))

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

(defn send-code [phone-number election]
  (POST "/api/votercode"
        {:handler       send-code-success-handler
         :error-handler error-handler
         :format        (ajax/json-request-format)
         :params        {:election     election
                         :phone-number phone-number}}))

(defn error-component []
  (when-not (nil? @error-code)
    (if (= @error-code 404)
      [:div.error.not-found
       "The voting code you entered is not valid. Please ensure the code is entered correctly."]
      [:div.error.already-voted
       "We already got your vote!"])))


(defn student-id-component [campus additionalId]
  (let [empty-campus (rg/atom false)
        empty-id (rg/atom false)]
    [:div
     [:div.flexrow.input-group-prepend
      [:select {:id        "campus"
                :class     (str "form-control" (when @empty-campus " input-error"))
                :name      "campus"
                :on-change (fn [e]
                             (reset! campus (-> e .-target .-value))
                             (reset! empty-campus (= (count @campus) 0)))}
       [:option {:default-value :disabled
                 :value         ""} "School:"]
       [:option {:value "cudenver"} "CU Denver"]
       [:option {:value "ccd"} "CCD"]
       [:option {:value "msu"} "MSU"]]
      [:div.required "*"]
      [:input
       {:type        "text"
        :class       (str "form-control" (when @empty-id " input-error"))
        :placeholder "xxxxxxxxx"
        :maxLength   9
        :value       @additionalId
        :on-change   (fn [e]
                       (reset! additionalId (-> e .-target .-value))
                       (reset! empty-id (not= (count @additionalId) 9)))
        :on-blur     (fn []
                       (reset! empty-campus (= (count @campus) 0))
                       (reset! empty-id (not= (count @additionalId) 9)))}]
      [:div.required "*"]]]))


(defn voting-code-view []
  (let [now (js/Date.)
        election-in-view (rf/subscribe [:election-in-view-2])]
    (if @election-in-view
      (let [_ (prn @election-in-view)
            {startOnline :startOnline
             endOnline   :endOnline
             {id :id}    :sys} @election-in-view]
        ;; TODO: undo these two conditionals
        (if true
          (if false
            ;; if online voting hasn't started
            [:div.voting-code-view
             [:h1 (str "Voting opens on "
                       (.format (js/moment startOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]]

            ;; if online voting is open
            [:div.voting-code-view
             (if (= @code-sent? false)
               ;; if voting code hasn't been sent
               [send-code-component @election-in-view]
               ;; if voting code has been sent
               [check-code-component id])])

          ;; if online voting has ended
          [:div.voting-code-view
           [:h1 (str "Voting ended on "
                     (-> (js/moment endOnline)
                         (.format "dddd, MMMM Do YYYY [at] h:mm a.")))]]))
      [loading-component])))


(defn send-code-component [election]
  (let [input-phone1 (rg/atom "")
        input-phone2 (rg/atom "")
        additionalId (rg/atom "")
        empty-phone1 (rg/atom false)
        empty-phone2 (rg/atom false)
        phone-mismatch (rg/atom false)
        campus (rg/atom "")]
    (rg/create-class
      {:component-did-mount
       (fn []
         (new js/Cleave "#input-phone1" #js {:phone true :phoneRegionCode "US"})
         (new js/Cleave "#input-phone2" #js {:phone true :phoneRegionCode "US"}))
       :reagent-render
       (fn [election]
         (let [{{additionalIdLabel    :additionalIdLabel
                 phonePlaceholder1    :phonePlaceholder1
                 phonePlaceholder2    :phonePlaceholder2
                 votingCodeFieldLabel :votingCodeFieldLabel
                 votingCodePageTitle  :votingCodePageTitle} :fields
                {id :id}                                    :sys} election
               is-auraria? (boolean additionalIdLabel)]
           [:div
            [:h1 votingCodePageTitle]
            [:form.voter-auth-form
             {:on-submit (fn [e]
                           (.preventDefault e)
                           (.stopPropagation e))}
             (if is-auraria?
               [student-id-component campus additionalId]
               [:div
                [:div.flexrow.input-group-prepend
                 [:input
                  {:id          "input-phone1"
                   :type        "text"
                   :class       (str "form-control" (when (or @phone-mismatch
                                                              @empty-phone1) " input-error"))
                   :placeholder phonePlaceholder1
                   :maxLength   12
                   :value       @input-phone1
                   :on-change   (fn [e]
                                  (reset! input-phone1 (-> e .-target .-value))
                                  (when (and (= (count @input-phone1) 12)
                                             (= (count @input-phone2) 12))
                                    (reset! phone-mismatch (not= @input-phone1 @input-phone2)))
                                  (reset! empty-phone1 (not= (count @input-phone1) 12)))
                   :on-blur     #(reset! empty-phone1 (not= (count @input-phone1) 12))}]
                 [:div.required "*"]]

                [:div.flexrow.input-group-prepend
                 [:input
                  {:id          "input-phone2"
                   :type        "text"
                   :class       (str "form-control" (when (or @phone-mismatch
                                                              @empty-phone2) " input-error"))
                   :placeholder phonePlaceholder2
                   :maxLength   12
                   :value       @input-phone2
                   :on-change   (fn [e]
                                  (reset! input-phone2 (-> e .-target .-value))
                                  (when (and (= (count @input-phone1) 12)
                                             (= (count @input-phone2) 12))
                                    (reset! phone-mismatch (not= @input-phone1 @input-phone2)))
                                  (reset! empty-phone2 (not= (count @input-phone2) 12)))
                   :on-blur     #(reset! empty-phone2 (not= (count @input-phone2) 12))}]
                 [:div.required "*"]]
                [:h4 [:b (if @(rf/subscribe [:if-english?])
                           (-> translations-db :text-message :en-US)
                           (-> translations-db :text-message :es-US))]]])
             [:p [:small votingCodeFieldLabel]]
             (when-not config/debug?
               [captcha-component])
             [:a {:on-click #(if is-auraria?
                               (prn "TODO: see auraria branch for how to handle student ID submit")
                               (send-code (string/replace @input-phone2 #" " "") id))}
              [:button#send-code
               {:type     "submit"
                :disabled (or (when-not config/debug?
                                (nil? @(rf/subscribe [:captcha-passed])))
                              (if is-auraria?
                                (or (< (count @additionalId) 9)
                                    (= @campus "Campus:"))
                                (or (< (count @input-phone2) 12)
                                    (not= @input-phone1 @input-phone2))))}
               (if @(rf/subscribe [:if-english?])
                 "CONTINUE"
                 "CONTINUAR")]]]
            [error-component]]))})))


(defn check-code-component [id]
  (let [code (rg/atom nil)]
    (fn []
      [:div
       [:br]
       [:h2 "Check your text messages!"]
       [:h1 "Enter the 8-digit code:"]
       [:form.voter-auth-form
        {:on-submit (fn [e]
                      (.preventDefault e)
                      (.stopPropagation e))}
        [:div.flex-row-wrap
         [:input.form-control
          {:type        "text"
           :placeholder "xxxxxxxx"
           :maxLength   8
           :value       @code
           :on-change   #(reset! code (-> % .-target .-value))}]
         [:a {:on-click #(check-code @code id)}
          [:button#submit-code
           {:type     "submit"
            :disabled (< (count @code) 8)}
           "CONTINUE"]]]]
       [error-component]])))
