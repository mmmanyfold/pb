(ns pb.views.home
  (:require [pb.components.election :refer [election-component]]))

(defn home-view []
  [:div
   [:h1 "Find Your Election:"]
   [election-component]
   [election-component]])
