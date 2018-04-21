(ns pb.views.proposals
  (:require [re-frame.core :as rf]
            [pb.components.proposal :refer [proposal-component]]))

(defn proposals-view [election-slug]
  (if-let [election-in-view @(rf/subscribe [:election-in-view])]
    [:div.proposals-view.mt5
     [:h2 "Instructions:"]
     [:ol
      [:li "Choose the projects you want to support by clicking on the 'Select' buttons."]
      [:li "You can vote for up to 3 projects."]
      [:li "Click the \"Submit My Vote\" button when you're ready to submit."]]
     [:div.tc
      [:button.submit.mt3 "Submit My Vote"]]
     [:div.proposals.row
      (for [i (range 4)]
        ^{:key (gensym "p-")}
        [proposal-component])]]
    (do
      (set! (-> js/window .-location .-href) (str "/#/" election-slug))
      [:div.proposals-view "Redirecting..."])))
