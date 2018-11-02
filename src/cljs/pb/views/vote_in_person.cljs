(ns pb.views.vote-in-person
  (:require [re-frame.core :as rf]
            [pb.helpers :refer [render-markdown]]
            [pb.components.loading :refer [loading-component]]
            [pb.helpers :refer [render-markdown]]))

(defn vote-in-person-view [election-slug]
  (let [query (str "{ elections(q: \"fields.shortTitle=" election-slug
                   "\") {
                     startInPerson
                     endInPerson
                     eligibility
                     votingInPerson
                   }}")]
    (rf/dispatch [:get-contentful-data :election-in-view query :election])
    (fn []
      (if-let [{startInPerson :startInPerson
                endInPerson :endInPerson
                votingInPerson :votingInPerson} @(rf/subscribe [:election-in-view])]
        [:div.vote-in-person
         [:h1 "How to vote in person"]
         [:h5.f5.f4-ns
          (when startInPerson
            [:div
             "Voting Open: "
             (let [startDay (.format (js/moment startInPerson) "MMM D, YYYY")
                   endDay (.format (js/moment endInPerson) "MMM D, YYYY")]
               (if (= startDay endDay)
                 (str startDay)
                 (str startDay " – " endDay)))])]
         [render-markdown votingInPerson]]
        [loading-component]))))