(ns pb.components.header
  (:require [re-com.core :as rc]))

(defn nav-link [label to]
  [rc/hyperlink-href
   :label label
   :class "nav-link pl0 pr2 pr3-ns pb1"
   :href (str "#" to)])

(defn nav-item [label]
  [:div {:class "nav-link pl0 pr2 pr3-ns pb1"} label])

(defn header-component [active-view election count]
  (let [max-count 3]
    [:header {:class "fixed w-100 pt3 ttu tracked bg-white"}
     [rc/h-box
      :class "nav-wrapper mh3 mh4-ns bb bw1 pb3"
      :justify :between
      :children [[:div {:class "flex"}
                  [nav-link "Vote" "/"]
                  (when election
                    [nav-item ">"])
                  (when election
                    [nav-item election])]]]
     (when (= active-view :proposals-view)
       [:div.count-component.tc.mh3.mh4-ns.pa2
        [:span (str count " / " max-count " selected")]])]))
