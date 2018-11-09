(ns pb.main
  (:require [re-frame.core :as rf]
            [re-com.core :as rc]
            [reagent.core :as rg]
            [pb.views.home :refer [home-view]]
            [pb.views.voting-code :refer [voting-code-view]]
            [pb.views.proposals :refer [proposals-view]]
            [pb.views.vote-in-person :refer [vote-in-person-view]]
            [pb.views.admin :as admin]
            [pb.components.header :refer [header-component]]))

(def not-found-view
  [:div [:h1 "404: Page not found"]])

(defn- show-view [view-name election-slug admin-election]
  (case view-name
        :admin-view [admin/view]
        :home-view [home-view admin-election]
        :voting-code-view [voting-code-view election-slug]
        :proposals-view [proposals-view election-slug]
        :vote-in-person-view [vote-in-person-view election-slug]
        :404 not-found-view
        [:div not-found-view]))

(defn view []
  (let [admin-election (rf/subscribe [:admin-election])
        active-view (rf/subscribe [:active-view])
        election-slug (rf/subscribe [:election-slug])
        selected-proposals (rf/subscribe [:selected-proposals])]
    (fn []
      [rc/v-box
       :class "w-100 h-100 mb0"
       :children [[header-component @active-view @election-slug (count @selected-proposals)]
                  [:div {:class "content-panel mh3 mh4-ns mv5 pt3 pt4-ns"}
                   [show-view @active-view @election-slug @admin-election]]]])))
