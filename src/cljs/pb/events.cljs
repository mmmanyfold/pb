(ns pb.events
  (:require [pb.db :as db]
            [re-frame.core :as rf]
            [ajax.core :as ajax :refer [GET]]
            [day8.re-frame.http-fx]))

;; Dispatchers

(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(rf/reg-event-db
  :set-active-view
  (fn [db [_ active-view election]]
    (if (or (nil? (:election-in-view db)) (= active-view :home-view))
      (assoc db :election-in-view election
                :active-view active-view)
      (assoc db :active-view active-view))))

(rf/reg-event-db
  :set-selected-proposals
  (fn [db [_ proposal set]]
    (case set
        :add (update db :selected-proposals conj proposal)
        :remove (update db :selected-proposals #(remove #{proposal} %)))))

(rf/reg-event-db
  :clear-selected-proposals
  (fn [db _]
    (assoc db :selected-proposals nil)))
