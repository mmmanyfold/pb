(ns pb.views.proposals
  (:require [pb.components.proposal :refer [proposal-component]]))

(defn proposals-view [election]
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
      [proposal-component])]])
