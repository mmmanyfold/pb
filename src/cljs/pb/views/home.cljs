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

(defn home-view []
  ;; 0. declare unique db-key
  ;; 1. register subscriber db-key
  ;; 2. retrieve contentful data & pass key for assoc in db
  (let [db-key :elections]
    (rf/reg-sub db-key #(db-key %))
    (rf/dispatch [:get-contentful-data db-key query :election])
    (let [elections (:elections @(rf/subscribe [db-key]))]
      [:div
       [:h1 "Find Your Election:"]
       (for [election elections]
         ^{:key (gensym "election-")}
         [election-component election])])))
