(ns pb.views.voting-code
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [cljsjs.moment]))

(def code (rg/atom nil))

(defn voting-code-view [election-slug]
  (let [now (js/Date.)
        db-key :election-in-view
        query (str "{ elections(q: \"fields.shortTitle=" election-slug
                   "\") {
                     title
                     startOnline
                     endOnline
                     maxSelection
                     proposalRefs {
                       sys { id }}
                   }}")]
    (rf/dispatch [:get-contentful-data db-key query :election])
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
                {:type "number"
                 :placeholder "000000"
                 :min 1
                 :max 999999
                 :value @code
                 :on-change (fn [e]
                              (let [input (-> e .-target .-value)]
                                (reset! code input)))}]]
              [:a {:href (str "/#/" election-slug "/proposals")}
               [:input
                {:type "submit"
                 :value "VOTE"}]]]])
          ;; if online voting has ended
          [:div.voting-code-view
           [:h1 (str "Voting ended on "
                     (.format (js/moment endOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]])
        [:div "Loading"]))))
