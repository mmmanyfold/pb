(ns pb.components.election
  (:require [pb.helpers :refer [showdown]]))

(defn election-component [election]
  (let [{:keys [title
                shortTitle
                startOnline
                endOnline
                startInPerson
                endInPerson
                eligibility
                votingInPerson]} election]
    [:div.election-component.flexrow-wrap.ba.bw1.mb4
     [:div.links.flexcolumn-wrap.w-20-ns.f3.f2-l
      [:div
       [:a.pa3 {:href (str "/#/" shortTitle)}
        "online"]]
      [:div
       [:a.pa3 {:href (str "/#/" shortTitle "/in-person")}
        "in person"]]]
     [:div.w-80-ns.pv3.ph4
      [:h2.mt2.fw7 title]
      [:h3.fw7 (str startOnline " - " endOnline)]
      [:h4.mt3.fw7 "Eligibility:"]
      [:div {"dangerouslySetInnerHTML"
             #js{:__html (.makeHtml showdown eligibility)}}]]]))
