(ns pb.components.proposal
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [pb.helpers :refer [showdown]]))

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
             [:button.remove.mv2.ml3 {:on-click (fn []
                                                  (swap! selected? not)
                                                  (rf/dispatch [:set-selected-proposals title :remove]))}
                "Remove"])]

       [:h5 {:on-click #(swap! show-impact? not)
             :class (when @show-impact? "b")}
          "+ Community Impact & Supporting Statistics"]
       (when @show-impact?
         [:div {"dangerouslySetInnerHTML"
                #js{:__html (.makeHtml showdown impact)}}])

       [:h5 {:on-click #(swap! show-budget? not)
             :class (when @show-budget? "b")}
          "+ Budget Breakdown"]
       (when @show-budget?
          [:div {"dangerouslySetInnerHTML"
                 #js{:__html (.makeHtml showdown budget)}}])

       [:h5 {:on-click #(swap! show-timeline? not)
             :class (when @show-timeline? "b")}
          "+ Timeline"]
       (when @show-timeline?
          [:div {"dangerouslySetInnerHTML"
                 #js{:__html (.makeHtml showdown timeline)}}])
       [:img.w-100.mt2 {:src "img/temp.jpg"}]])))
