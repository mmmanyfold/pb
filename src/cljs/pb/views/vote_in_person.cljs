(ns pb.views.vote-in-person
  (:require [re-frame.core :as rf]
            [pb.helpers :refer [render-markdown]]
            [pb.components.loading :refer [loading-component]]
            [pb.helpers :refer [render-markdown]]))

(defn vote-in-person-view []
  (if-let [{{startInPerson :startInPerson
             endInPerson :endInPerson
             votingInPerson :votingInPerson} :fields} @(rf/subscribe [:election-in-view-2])]
    [:div.vote-in-person
     [:h1 "How to vote in person"]
     [:h5.f5.f4-ns
      (when startInPerson
        [:div
         (let [startDay (.format (js/moment startInPerson) "MMM D, YYYY")
               endDay (.format (js/moment endInPerson) "MMM D, YYYY")]
           (if (= startDay endDay)
             [:h3.b startDay]
             [:h3.b (str startDay " – " endDay)]))])]
     [render-markdown votingInPerson]]
    [loading-component]))