(ns pb.components.election
  (:require [pb.helpers :refer [render-markdown]]
            [cljsjs.moment]))

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
     [:div.links.flexcolumn-wrap.w-30-m.w-20-l.f3.f2-ns
      [:div
       [:a.pa3 {:href (str "/#/" shortTitle)}
        [:span.tc
         "online"
         [:br]
         [:h5.f5.f4-ns
          (let [startDay (.format (js/moment startOnline) "M/D/YY")
                endDay (.format (js/moment endOnline) "M/D/YY")]
            (if (= startDay endDay)
              (str startDay)
              (str startDay " – " endDay)))]]]]
      [:div
       [:a.pa3 {:href (str "/#/" shortTitle "/in-person")}
        [:span.tc
         "in person"
         [:br]
         [:h5.f5.f4-ns
          (let [startDay (.format (js/moment startInPerson) "M/D/YY")
                endDay (.format (js/moment endInPerson) "M/D/YY")]
            (if (= startDay endDay)
              (str startDay)
              (str startDay " – " endDay)))]]]]]
     [:div.w-70-m.w-80-l.pv3.ph4
      [:h2.mt2.fw7 title]
      [:h3.fw7.mt3 "Eligibility:"]
      [render-markdown eligibility]]]))
