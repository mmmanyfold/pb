(ns pb.components.header
  (:require [re-com.core :as rc]
            [re-frame.core :as rf]))

(defn header-component [active-view election-slug count]
  (let [nav-item-classes "nav-item pl0 pr2 pr3-ns pb1"]
    [:header {:class "fixed w-100 pt2 pt3-ns tracked bg-white"}
     [rc/h-box
      :class "nav-wrapper mh3 mh4-ns bb bw1 pb2 ttu"
      :justify :between
      :children [[:div {:class "flex"}
                  [:a {:href "/#/"
                       :class nav-item-classes}
                   "PB Vote"]
                  (when election-slug
                    [:div {:class nav-item-classes} ">"])
                  (when election-slug
                    [:div {:class nav-item-classes} election-slug])]
                 [:div {:class "nav-right flex items-center"}
                  [:div {:class "subtitle f7 tr mr3"} "Participatory Budgeting"
                   [:br] "in Denver, CO"]
                  [:a {:href "http://thismachinehasasoul.com/#/about"
                       :target "_blank"
                       :rel "noopener noreferrer"}
                   [:img {:src "img/TMHAS_Logo_600.jpg"}]]]]]
     [:div.language-btn.tc.pa1
      {:on-click #(rf/dispatch [:toggle-language])}
      (if @(rf/subscribe [:if-english?])
        "Español"
        "English")]
     (when (= active-view :proposals-view)
       (when-let [{maxSelection :maxSelection} (:fields @(rf/subscribe [:election-in-view-2]))]
         [:div.count-component.tc.mh3.mh4-ns.pa2
          [:span (str count " / " maxSelection " selected")]]))]))
