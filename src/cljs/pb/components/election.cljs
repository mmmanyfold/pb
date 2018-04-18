(ns pb.components.election)

(defn election-component []
  [:div.election.flexrow-wrap.ba.bw1.mb4
   [:div.links.flexcolumn-wrap.w-20-ns.f3.f2-l
    [:div
     [:a.pa3 {:href "/#/cole"}
      "online"]]
    [:div
     [:a.pa3 {:href "/#/cole/in-person"}
      "in person"]]]
   [:div.w-80-ns.pv3.ph4
    [:h2.mt2.fw7 "Cole & surrounding areas"]
    [:h3.fw7 "May 3, 2018 - May 15, 2018"]
    [:h4.mt3.fw7 "Eligibility:"]
    [:ul
     [:li "13+"]
     [:li "Anyone who lives, works, or has kids in school in:"]]
    [:p "Globeville, Elyria-Swansea, Five Points, Cole, Clayton, Whittier, Skyland, City Park West, City Park, North Park Hill, Northeast Park Hill, Stapleton, Montbello"]]])
