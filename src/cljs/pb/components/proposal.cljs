(ns pb.components.proposal
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [pb.helpers :refer [render-markdown]]))

(defn detail [show? field title]
  [:div
   [:h5 {:on-click #(swap! show? not)
         :class (when @show? "b")}
    (str "+ " title)]
   (when @show? [render-markdown field])])

(defn proposal-component [proposal]
  (let [{:keys [title
                objective
                impact
                budget
                timeline
                images]} proposal
        show-impact? (rg/atom false)
        show-budget? (rg/atom false)
        show-timeline? (rg/atom false)
        selected? (rg/atom false)]
    (fn []
      [:div.proposal-component.pa4.col-xs-12.col-md-6.col-lg-4
       {:style {:background-color (if @selected? "rgba(115,159,62,0.15)" "white")}}

       [:h2.fw7 title]
       [:p.f4 objective]
       [:div.tc.mt2.mb3
        [:button.mv2 {:on-click (fn []
                                  (swap! selected? not)
                                  (rf/dispatch [:set-selected-proposals title :add]))
                      :class (if @selected? "selected" "select")}
         (if @selected? "Selected" "Select")]
        (when @selected?
          [:button.remove.mv2.ml3
           {:on-click (fn []
                        (swap! selected? not)
                        (rf/dispatch [:set-selected-proposals title :remove]))}
           "Remove"])]

       [detail show-impact? impact "Community Impact & Supporting Statistics"]
       [detail show-budget? budget "Budget Breakdown"]
       [detail show-timeline? timeline "Timeline"]

       [:img.w-100.mt2 {:src "img/temp.jpg"}]])))
