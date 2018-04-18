(ns pb.main
  (:require [re-frame.core :as rf]
            [re-com.core :as rc]
            [pb.views.home :refer [home-view]]
            [pb.views.voting-code :refer [voting-code-view]]
            [pb.views.proposals :refer [proposals-view]]))

(defn- show-view [view-name]
  (case view-name
        :home-view [home-view]
        :voting-code-view [voting-code-view]
        :proposals-view [proposals-view]
        :404 [:div "404"]
        [:div]))

(defn nav-link [label to]
  [rc/hyperlink-href
   :label label
   :class "nav-link pl0 pr2 pr3-ns pb1"
   :href (str "#" to)])

(defn nav-item [label]
  [:div {:class "nav-link pl0 pr2 pr3-ns pb1"} label])

(defn view []
  (let [active-view (rf/subscribe [:active-view])
        election (rf/subscribe [:election-in-view])]
    (fn []
      [rc/v-box
       :class "w-100 h-100 mb0"
       :children [[:header {:class "fixed w-100 pt3 ttu tracked bg-white"}
                   [rc/h-box
                    :class "nav-wrapper mh3 mh4-ns bb bw1 pb3"
                    :justify :between
                    :children [[:div {:class "flex"}
                                [nav-link "Vote" "/"]
                                (when @election
                                  [nav-item ">"])
                                (when @election
                                  [nav-item @election])]]]]
                  [:div {:class "content-panel mh3 mh4-ns mt5 pt3 pt4-ns"}
                   [show-view @active-view]]]])))
