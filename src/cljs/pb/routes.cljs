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

  (defroute "/:election" {:as params}
            (rf/dispatch [:set-active-view :voting-code-view (:election params)])
            (rf/dispatch [:clear :selected-proposals])
            (rf/dispatch [:clear :voter-id])
            (rf/dispatch [:clear :captcha-passed]))

  (defroute "/:election/proposals" {:as params}
            (if (nil? @(rf/subscribe [:voter-id]))
              (set! (.. js/window -location -hash) (str "/" (:election params)))
              (rf/dispatch [:set-active-view :proposals-view (:election params)])))

  (defroute "/404" []
            (rf/dispatch [:set-active-view :404]))

  ;; --------------------
  (hook-browser-navigation!))
