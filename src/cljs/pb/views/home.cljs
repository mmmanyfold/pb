(ns pb.views.home
  (:require [re-frame.core :as rf]
            [pb.components.election :refer [election-component]]))

(def query
  "{ elections {
     title
     shortTitle
     startOnline
     endOnline
     startInPerson
     endInPerson
     eligibility
     votingInPerson
   }}")

(defn home-view [admin-election]
  ;; 0. declare unique db-key
  ;; 1. register subscriber db-key
  ;; 2. retrieve contentful data & pass key for assoc in db
  (let [db-key :elections]
    (rf/dispatch [:get-contentful-data db-key query :election])
    (let [elections @(rf/subscribe [db-key])]
      [:div
       [:h1 "Find Your Election:"]
       [:div.flex-l.justify-between
        (for [election elections
              :let [shortTitle (:shortTitle election)]
              :when (= shortTitle admin-election)]
          ^{:key (gensym "election-")}
          [election-component election])]])))
