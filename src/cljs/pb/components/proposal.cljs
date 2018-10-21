(ns pb.components.proposal
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [re-com.core :as rc]
            [re-com.modal-panel :refer [modal-panel-args-desc]]
            [pb.helpers :refer [render-markdown]]))

(defn detail [show? field title]
  [:div
   [:h5 {:on-click #(swap! show? not)
         :class (when @show? "b")}
    (str "+ " title)]
   (when @show? [render-markdown field])])

(defn proposal-component [proposal]
  (let [{:keys [title
                shortDescription
                impact
                budget
                timeline
                images]} (first (val proposal))
        id (name (first proposal))
        maxSelection (:maxSelection @(rf/subscribe [:election-in-view]))
        images (map #(:url %) images)
        thumbnail-image (first images)
        show-impact? (rg/atom false)
        show-budget? (rg/atom false)
        show-timeline? (rg/atom false)
        selected? (rg/atom (some #(= id %) @(rf/subscribe [:selected-proposals])))
        expand-image? (rg/atom false)]
    (fn []
      [:div.proposal-component.pa4.col-xs-12.col-md-6.col-lg-4
       {:style {:background-color (if @selected? "rgba(115,159,62,0.15)" "white")}}
       [:h2.fw7 title]
       [:p.f4 shortDescription]

       ;; select/remove buttons
       [:div.tc.mt2.mb3
        [:button.mv2 {:on-click (fn []
                                  (when (< (count @(rf/subscribe [:selected-proposals])) maxSelection)
                                    (rf/dispatch [:set-selected-proposals id :add])))
                      :class (if @selected? "selected" "select")}
         (if @selected? "Selected" "Select")]
        (when @selected?
          [:button.remove.mv2.ml3
           {:on-click (fn []
                        (rf/dispatch [:set-selected-proposals id :remove]))}
           "Remove"])]

       ;; expandable details
       [detail show-impact? impact "Community Impact"]
       [detail show-budget? budget "Budget Breakdown"]
       [detail show-timeline? timeline "Timeline"]

       ;; thumbnail image
       [:div.thumbnail-wrapper
        {:on-click #(reset! expand-image? true)}
        [:img.w-100.mt2 {:src thumbnail-image}]
        (when (< 1 (count images))
          [:i.more-images-icon {:class "far fa-images"}])]

       ;; expanded image gallery modal
       (when @expand-image?
         [rc/modal-panel
          :backdrop-on-click #(reset! expand-image? false)
          :wrap-nicely? false
          :child
          [rc/v-box
           :class "image-gallery-wrapper ph5-m ph6-l"
           :children
           [[:div.image-gallery
             [:i {:class "fa fa-times-circle f3 f2-ns pointer"
                  :aria-hidden true
                  :on-click #(reset! expand-image? false)}]
             [:h2 {:class "mt0 mb3 mh1 f3 f2-ns"}
                  title]
             (if (= 1 (count images))
               ;; if single image
               [:img {:class "mb1 w-100"
                      :src thumbnail-image}]
               ;; if multiple images
               [:div {:id "image-gallery" :class "carousel slide" :data-ride "carousel" :data-interval false}
                ; indicators
                [:ol.carousel-indicators
                 (map (fn [image]
                        (let [i (.indexOf images image)]
                          ^{:key (gensym "indicator-")}
                          [:li {:data-target "#image-gallery" :data-slide-to (str i) :class (when (= i 0) "active")}]))
                    images)]
                ; wrapper for slides
                [:div.carousel-inner
                 (for [image images
                       :let [i (.indexOf images image)]]
                   ^{:key (gensym "image-")}
                   [:div {:class (str "carousel-item" (when (= i 0) " active"))}
                    [:img {:src image}]])]
                ; left and right controls
                [:a.carousel-control-prev
                 {:href "#image-gallery" :role "button" :data-slide "prev"}
                 [:span.carousel-control-prev-icon {:aria-hidden "true"}]
                 [:span.sr-only "Previous"]]
                [:a.carousel-control-next
                 {:href "#image-gallery" :role "button" :data-slide "next"}
                 [:span.carousel-control-next-icon {:aria-hidden "true"}]
                 [:span.sr-only "Next"]]])]]]])])))
