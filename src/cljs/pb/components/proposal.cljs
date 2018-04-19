(ns pb.components.proposal
  (:require [reagent.core :as rg]))

(defn proposal-component []
  (let [show-impact? (rg/atom false)
        show-budget? (rg/atom false)
        show-timeline? (rg/atom false)
        selected? (rg/atom false)]
    (fn []
      [:div.proposal-component.pa4.col-xs-12.col-md-6.col-lg-4
       {:style {:background-color (if @selected? "rgba(115,159,62,0.15)" "white")}}

       [:h2.fw7 "Walking Art Gallery"]
       [:p.f4 "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."]
       [:div.tc.mt2.mb3
          [:button.mv2 {:on-click #(swap! selected? not)
                        :class (if @selected? "selected" "select")}
             (if @selected? "Selected" "Select")]
          (when @selected?
             [:button.remove.mv2.ml3 {:on-click #(swap! selected? not)}
                "Remove"])]

       [:h5 {:on-click #(swap! show-impact? not)
             :class (when @show-impact? "b")}
          "+ Community Impact & Supporting Statistics"]
       (when @show-impact?
          [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."])

       [:h5 {:on-click #(swap! show-budget? not)
             :class (when @show-budget? "b")}
          "+ Budget Breakdown"]
       (when @show-budget?
          [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."])

       [:h5 {:on-click #(swap! show-timeline? not)
             :class (when @show-timeline? "b")}
          "+ Timeline"]
       (when @show-timeline?
          [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."])

       [:img.w-100.mt2 {:src "img/temp.jpg"}]])))
