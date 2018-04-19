(ns pb.components.proposal
  (:require [reagent.core :as rg]))

(defn proposal-component []
  (let [show?a (rg/atom false)
        show?b (rg/atom false)
        show?c (rg/atom false)]
    (fn []
      [:div.proposal-component.pa4.col-xs-12.col-md-6.col-lg-4
       [:h2.fw7 "Walking Art Gallery"]
       [:p.f4 "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."]
       [:h5 {:on-click #(swap! show?a not)
             :class (when @show?a "b")}
        "+ Community Impact & Supporting Statistics"]
       [:div {:class (if @show?a "expand" "hide")}
        [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."]]
       [:h5 {:on-click #(swap! show?b not)
             :class (when @show?b "b")}
        "+ Budget Breakdown"]
       [:div {:class (if @show?b "expand" "hide")}
        [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."]]
       [:h5 {:on-click #(swap! show?c not)
             :class (when @show?c "b")}
        "+ Timeline"]
       [:div {:class (if @show?c "expand" "hide")}
        [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."]]
       [:div.tc
        [:button.select.mt2.mb3 "Select"]]
       [:img.w-100.mt2 {:src "img/temp.jpg"}]])))
