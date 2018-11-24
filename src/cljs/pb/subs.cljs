(ns pb.subs
  (:require [re-frame.core :as rf]))

;; Subscriptions

(rf/reg-sub
 :active-view
 (fn [db _]
   (:active-view db)))

(rf/reg-sub
  :captcha-passed
  (fn [db _]
    (:captcha-passed db)))

(rf/reg-sub
 :election-slug
 (fn [db _]
   (:election-slug db)))

(rf/reg-sub
 :elections
 (fn [{elections :elections} _]
   (:elections elections)))

(rf/reg-sub
 :election-in-view
 (fn [db _]
   (first (:elections (:election-in-view db)))))

(rf/reg-sub
 :proposals-in-view
 (fn [db _]
   (:proposals-in-view db)))

(rf/reg-sub
 :selected-proposals
 (fn [db _]
   (:selected-proposals db)))

(rf/reg-sub
  :voter-id
  (fn [db _]
    (:voter-id db)))

(rf/reg-sub
  :admin-election
  (fn [db _]
    (:admin-election db)))

(rf/reg-sub
  :admin
  (fn [db _]
    (:admin db)))

(rf/reg-sub
  :language-in-view
  (fn [db _]
    (:language-in-view db)))

(rf/reg-sub
  :entries
  (fn [db _]
    (:entries db)))
