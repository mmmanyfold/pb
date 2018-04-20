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


(rf/reg-event-fx
  :get-contentful-data
  (fn [{db :db} [_ db-key query space]]
    (when-not (db-key db)
      (let [endpoint "http://45.55.175.107:5000/graphql/"]
        {:db         db
         :http-xhrio {:method          :get
                      :format          (ajax/json-request-format)
                      :params          {:query query}
                      :uri             (str endpoint (name space))
                      :response-format (ajax/json-response-format {:keywords? true})
                      :on-failure      [:get-contentful-data-failed]
                      :on-success      [:get-contentful-data-success db-key]}}))))
                      
(rf/reg-event-db
  :get-contentful-data-failed
  (fn [db _]
    (js/console.error ":get-contentful-data event failed, is the GraphQL server running ?")
    db))

(rf/reg-event-db
  :get-contentful-data-success
  (fn [db [_ db-key & [{data :data}]]]
    (assoc db db-key data)))


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
