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
            (rf/dispatch [:set-active-view :home-view])
            (rf/dispatch [:clear :election-in-view])
            (rf/dispatch [:clear :proposals-in-view])
            (rf/dispatch [:clear :voter-code]))

  (defroute "/admin" []
            (rf/dispatch [:set-active-view :admin-view]))

  (defroute "/vote/:election" {:as params}
            (rf/dispatch [:clear :selected-proposals])
            (rf/dispatch [:clear :voter-id])
            (rf/dispatch [:clear :captcha-passed])
            (rf/dispatch [:set-active-view :voting-code-view (:election params)]))

  (defroute "/vote/:election/vote-in-person" {:as params}
            (rf/dispatch [:set-active-view :vote-in-person-view (:election params)]))

  ;; --------------------
  (hook-browser-navigation!))
