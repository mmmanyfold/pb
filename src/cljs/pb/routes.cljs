(ns pb.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

;; History

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; Routes

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
            (rf/dispatch [:set-active-view :home-view]))

  (defroute "/404" []
            (rf/dispatch [:set-active-view :404]))

  ;; --------------------
  (hook-browser-navigation!))
