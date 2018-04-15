(ns pb.main
  (:require [re-frame.core :as rf]
            [re-com.core :as rc]
            [pb.home :refer [home-view]]))

(defn- show-view [view-name]
  (case view-name
        :home-view [home-view]
        :404 [:div "404"]
        [:div]))

(defn view []
  (let [active-view (rf/subscribe [:active-view])]
    (fn []
      [rc/v-box
       :class "w-100 h-100 mb0"
       :children [[show-view @active-view]]])))
