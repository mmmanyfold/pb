(ns pb.views.voting-code
  (:require [reagent.core :as rg]))

(def code (rg/atom nil))

(defn voting-code-view [election]
  [:div.voting-code-view
   [:h1 "Text ‘hi’ to 1234567890 to get your voting code"]
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
    [:a {:href (str "/#/" election "/proposals")}
     [:input
      {:type "submit"
       :value "VOTE"}]]]])
