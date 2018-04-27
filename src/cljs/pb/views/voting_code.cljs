(ns pb.views.voting-code
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [cljsjs.moment]
            [pb.components.loading :refer [loading-component]]
            [ajax.core :as ajax :refer [GET POST PUT]]))

(def code (rg/atom nil))

(defn code-response [response]
  (prn response))

(defn check-code [code]
  (GET (str "/api/checkcode/" code) {:handler code-response
                                     :response-format (ajax/json-response-format {:keywords? true})}))

(defn voting-code-view [election-slug]
  (let [now (js/Date.)
        query (str "{ elections(q: \"fields.shortTitle=" election-slug
                   "\") {
                     title
                     startOnline
                     endOnline
                     maxSelection
                     proposalRefs {
                       sys { id }}
                   }}")]
    (rf/dispatch [:get-contentful-data :election-in-view query :election])
    (fn [election-slug]
      (if-let [{:keys [startOnline endOnline]} @(rf/subscribe [:election-in-view])]
        (if (> (js/Date. endOnline) now)
          (if (> (js/Date. startOnline) now)
            ;; if online voting hasn't started
            [:div.voting-code-view
             [:h1 (str "Voting opens on "
                       (.format (js/moment startOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]]
            ;; if online voting is open
            [:div.voting-code-view
             [:h1 "Text " [:span#text-keyword election-slug] " to 1234567890 to get your voting code"]
             [:p "Your phone number will NEVER be shared and will automatically get deleted after the election."
              [:br]
              "Your voting code is specific to this election and won’t be used for anything else."]
             [:div.input-group.flexrow-wrap
              [:div.input-group-prepend
               [:input.form-control
                {:type "text"
                 :placeholder "00000000"
                 :maxLength 8
                 :value @code
                 :on-change (fn [e]
                              (let [input (-> e .-target .-value)]
                                (reset! code input)))}]]
              [:a {:on-click #(check-code @code)}
               [:input#submit-code
                {:type "submit"
                 :value "VOTE"
                 :disabled (< (count @code) 8)}]]]])
          ;; if online voting has ended
          [:div.voting-code-view
           [:h1 (str "Voting ended on "
                     (.format (js/moment endOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]])
        [loading-component]))))
