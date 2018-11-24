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
  :get-contentful-entries
  (fn [{db :db} [_]]
    {:db         db
     :http-xhrio {:method          :get
                  :format          (ajax/json-request-format)
                  :uri             "api/contentful/entries"
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-failure      [:get-contentful-entries-failed]
                  :on-success      [:get-contentful-entries-success]}}))

(rf/reg-event-db
  :get-contentful-entries-failed
  (fn [db [_ {{err-msg :error} :response}]]
    (js/console.error err-msg)
    db))

(rf/reg-event-db
  :get-contentful-entries-success
  (fn [db [_ entries]]
    (assoc db :entries entries)))

(rf/reg-event-fx
  :get-election-var
  (fn [{db :db} [_]]
    {:db         db
     :http-xhrio {:method          :get
                  :format          (ajax/json-request-format)
                  :uri             "/api/election"
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-failure      [:get-election-var-failed]
                  :on-success      [:get-election-var-success]}}))

(rf/reg-event-db
  :get-election-var-failed
  (fn [db [_ {{err-msg :error} :response}]]
    (js/console.error err-msg)
    db))

(rf/reg-event-db
  :get-election-var-success
  (fn [db [_ {election :election}]]
    (assoc db :admin-election election)))

(rf/reg-event-fx
  :get-contentful-data
  (fn [{db :db} [_ db-key query space]]
    (when-not (db-key db)
      (let [endpoint "https://mmmanyfold.tech/graphql/"]
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
  :clear
  (fn [db [_ db-key]]
    (dissoc db db-key)))

(rf/reg-event-db
  :set-active-view
  (fn [db [_ active-view election-slug]]
    (if (or (nil? (:election-slug db)) (= active-view :home-view))
      (assoc db :election-slug election-slug
                :active-view active-view)
      (assoc db :active-view active-view))))


(rf/reg-event-db
  :update-selected-proposals
  (fn [db [_ op proposal]]
    (case op
      :reset (assoc db :selected-proposals [])
      :add (update db :selected-proposals conj proposal)
      :remove (update db :selected-proposals #(remove #{proposal} %)))))

(rf/reg-event-db
  :set-voter-id
  (fn [db [_ code]]
    (assoc db :voter-id code)))

(rf/reg-event-db
  :set-captcha-passed
  (fn [db [_ _]]
    (assoc db :captcha-passed true)))

(rf/reg-event-db
  :set-admin
  (fn [db [_ state]]
    (assoc db :admin state)))

(rf/reg-event-db
  :update-language
  (fn [db [_ lang]]
    (assoc db :language-in-view lang)))
