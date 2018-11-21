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
                votingInPerson
                eligibility]} election]
    [:div.election-component.tc.center.mv3.pa1
     [:h1.mt2.pt1.fw7 title]
     [:h3.b [render-markdown eligibility]]
     [:div.links.f3.f2-ns.center
      [:a.pa3 {:href (str "/#/vote/" shortTitle)}
       [:span.tc
        "Vote online now*"
        [:br]
        [:h5.f5.f4-ns
         (let [startDay (.format (js/moment startOnline) "MMM D, YYYY")
               endDay (.format (js/moment endOnline) "MMM D, YYYY")]
           (if (= startDay endDay)
             (str startDay)
             (str startDay " – " endDay)))]]]]

     [:p.lh-title.mt3
      "*To vote online, you will need a mobile phone with SMS (text messaging) to receive your unique voting code."]
     (when votingInPerson
       [:div.links.f3.f2-ns.mb3.center
        [:a.pa3 {:href (str "/#/" shortTitle "/vote-in-person")}
         [:span.tc
          "How to vote in person"
          [:br]
          [:h5.f5.f4-ns
           (if startInPerson
             (let [startDay (.format (js/moment startInPerson) "MMM D, YYYY")
                   endDay (.format (js/moment endInPerson) "MMM D, YYYY")]
               (if (= startDay endDay)
                startDay
                (str startDay " – " endDay)))
             "Dates Vary")]]]])]))
