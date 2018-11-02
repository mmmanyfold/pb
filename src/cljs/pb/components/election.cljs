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
    [:div.election-component.ba.bw1.mb4
     [:div.pv3.ph4
      [:h2.mt2.pt1.fw7 title]
      [:h3.b [render-markdown eligibility]]
      [:div.links.f3.f2-ns
       [:a.pa3 {:href (str "/#/" shortTitle)}
        [:span.tc
         "Vote now online*"
         [:br]
         [:h5.f5.f4-ns
          (let [startDay (.format (js/moment startOnline) "MMM D, YYYY")
                endDay (.format (js/moment endOnline) "MMM D, YYYY")]
            (if (= startDay endDay)
              (str startDay)
              (str startDay " – " endDay)))]]]]
      [:p.lh-solid.mt3 "*In order to vote online, you will need a mobile phone with SMS (text messaging)"]
      [:div.links.f3.f2-ns.mb3
       [:a.pa3 {:href (str "/#/" shortTitle "/in-person")}
        [:span.tc
         "How to vote in person"
         [:br]
         [:h5.f5.f4-ns
          (let [startDay (.format (js/moment startOnline) "MMM D, YYYY")
                endDay (.format (js/moment endOnline) "MMM D, YYYY")]
            (if (= startDay endDay)
              (str startDay)
              (str startDay " – " endDay)))]]]]]]))
