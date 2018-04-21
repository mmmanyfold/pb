(ns pb.subs
  (:require [re-frame.core :as rf]))

;; Subscriptions

(rf/reg-sub
 :active-view
 (fn [db _]
   (:active-view db)))

(rf/reg-sub
 :election-slug
 (fn [db _]
   (:election-slug db)))

(rf/reg-sub
 :selected-proposals
 (fn [db _]
   (:selected-proposals db)))
