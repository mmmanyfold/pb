(ns pb.views.voting-code
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [cljsjs.moment]
            [pb.components.loading :refer [loading-component]]
            [pb.components.captcha :refer [captcha-component]]
            [ajax.core :as ajax :refer [GET]]))

(def phone-number (rg/atom nil))
(def code (rg/atom nil))
(def additionalId (rg/atom nil))
(def error-code (rg/atom nil))

(defn error-handler [response]
  (reset! error-code (:status response)))

(defn success-handler [response]
  (set! (.. js/window -location -href) (str (.. js/window -location -href) "/proposals"))
  (rf/dispatch [:set-voter-id (:id response)]))

(defn check-code [code]
  (GET (str "/api/checkcode") {:handler success-handler
                               :error-handler error-handler
                               :response-format (ajax/json-response-format {:keywords? true})
                               :format :raw
                               :params {:voter-code code}}))



(defn voting-code-view [election-slug]
  (rg/create-class
    {:component-will-unmount
     (fn []
       (reset! error-code nil)
       (reset! code nil))
     :reagent-render
     (fn []
       (let [now (js/Date.)
             query (str "{ elections(q: \"fields.shortTitle=" election-slug
                        "\") {
                          title
                          additionalIdLabel
                          startOnline
                          endOnline
                          maxSelection
                          proposalRefs {
                            sys { id }}
                        }}")]
         (rf/dispatch [:get-contentful-data :election-in-view query :election])
         (fn [election-slug]
           (if-let [{:keys [additionalIdLabel startOnline endOnline]} @(rf/subscribe [:election-in-view])]
             (if (> (js/Date. endOnline) now)
               (if (> (js/Date. startOnline) now)
                 ;; if online voting hasn't started
                 [:div.voting-code-view
                  [:h1 (str "Voting opens on "
                            (.format (js/moment startOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]]
                 ;; if online voting is open
                 [:div.voting-code-view
                  [:h1 "Generate your unique voting code:"]
                  [:form#voter-auth-form
                   (when-not (nil? additionalIdLabel)
                     [:div.input-group.flexrow-wrap
                      [:div.input-group-prepend
                       [:select {:id "campus" :name "campus" :placeholder "Campus"}
                        [:option {:selected :disabled} "Campus"]
                        [:option {:value "cudenver"} "CU Denver"]
                        [:option {:value "ccd"} "CCD"]
                        [:option {:value "msu"} "MSU"]]
                       [:input.form-control
                        {:type "text"
                         :placeholder "Student ID"
                         :maxLength 10
                         :value @additionalId
                         :on-change (fn [e]
                                      (let [input (-> e .-target .-value)]
                                        (reset! additionalId input)))}]]])
                   [:br]
                   [:input.form-control
                    {:type "text"
                     :placeholder "Phone Number"
                     :maxLength 10
                     :value @phone-number
                     :on-change (fn [e]
                                  (let [input (-> e .-target .-value)]
                                    (reset! phone-number input)))}]
                   [:h4 "A text message with an 8-digit voting code will be sent to this phone number."]
                   [:p [:small "Your phone number will NEVER be shared and will automatically get deleted after this election."]]
                   [:a {:on-click #(check-code @phone-number)}
                    [:input#send-code
                     {:type "submit"
                      :value "SEND CODE"
                      :disabled (or (< (count @phone-number) 10)
                                    (nil? @(rf/subscribe [:captcha-passed])))}]]
                   [captcha-component]
                   [:div.flex-row-wrap
                    [:input.form-control
                     {:type "text"
                      :placeholder "Enter the code"
                      :maxLength 8
                      :value @code
                      :on-change (fn [e]
                                   (let [input (-> e .-target .-value)]
                                     (reset! code input)))}]
                    [:a {:on-click #(check-code @code)}
                     [:input#submit-code
                      {:type "submit"
                       :value "CONTINUE"
                       :disabled (< (count @code) 8)}]]]
                    (when-not (nil? @error-code)
                      (if (= @error-code 404)
                        [:div.error.not-found
                         "The voting code you entered is not valid. Please ensure the code is entered correctly, or follow the steps above to get your unique code."]
                        [:div.error.already-voted
                         "We already got your vote!"]))]])
               ;; if online voting has ended
               [:div.voting-code-view
                [:h1 (str "Voting ended on "
                          (.format (js/moment endOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]])
             [loading-component]))))}))
