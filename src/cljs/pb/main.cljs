(ns pb.main
  (:require [re-frame.core :as rf]
            [re-com.core :as rc]
            [pb.views.home :refer [home-view]]
            [pb.views.voting-code :refer [voting-code-view]]
            [pb.views.proposals :refer [proposals-view]]
            [pb.components.header :refer [header-component]]))

(defn- show-view [view-name election]
  (case view-name
        :home-view [home-view]
        :voting-code-view [voting-code-view election]
        :proposals-view [proposals-view election]
        :404 [:div "404"]
        [:div]))

(defn view []
  (let [active-view (rf/subscribe [:active-view])
        election (rf/subscribe [:election-in-view])]
    (fn []
      [rc/v-box
       :class "w-100 h-100 mb0"
       :children [[header-component @active-view @election]
                  [:div {:class "content-panel mh3 mh4-ns mv5 pt3 pt4-ns"}
                   [show-view @active-view @election]]]])))
