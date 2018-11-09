(ns pb.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [pb.ajax :refer [load-interceptors!]]
            [pb.events]
            [pb.subs]
            [pb.main :refer [view]]
            [pb.routes :as routes]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rf/dispatch [:get-election-var])
  (rf/clear-subscription-cache!)
  (r/render [view] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (routes/app-routes)
  (mount-root))
