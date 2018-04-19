(ns pb.components.header
  (:require [re-com.core :as rc]))

(defn header-component [active-view election count]
  (let [max-count 3
        nav-item-classes "nav-item pl0 pr2 pr3-ns pb1"]
    [:header {:class "fixed w-100 pt2 pt3-ns tracked bg-white"}
     [rc/h-box
      :class "nav-wrapper mh3 mh4-ns bb bw1 pb2 pb3-ns ttu"
      :justify :between
      :children [[:div {:class "flex"}
                  [:a {:href "/#/"
                       :class nav-item-classes}
                   "PB Vote"]
                  (when election
                    [:div {:class nav-item-classes} ">"])
                  (when election
                    [:div {:class nav-item-classes} election])]
                 [:div {:class "nav-right flex items-center"}
                  [:div {:class "subtitle f7 tr mr3"} "Participatory Budgeting"
                   [:br] "in Denver, CO"]
                  [:a {:href "http://thismachinehasasoul.com"
                       :target "_blank"}
                   [:img {:src "img/TMHAS_Logo_600.jpg"}]]]]]
     (when (= active-view :proposals-view)
       [:div.count-component.tc.mh3.mh4-ns.pa2
        [:span (str count " / " max-count " selected")]])]))
