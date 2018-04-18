(ns pb.subs
  (:require [re-frame.core :as rf]))

;; Subscriptions

(rf/reg-sub
 :active-view
 (fn [db _]
   (:active-view db)))

(rf/reg-sub
 :election-in-view
 (fn [db _]
   (:election-in-view db)))
