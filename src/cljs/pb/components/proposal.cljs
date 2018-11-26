(ns pb.components.proposal
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [re-com.core :as rc]
            [re-com.modal-panel :refer [modal-panel-args-desc]]
            [pb.helpers :refer [render-markdown]]
            [pb.db :refer [translations-db]]))

(defn detail [title content]
  (let [show? (rg/atom false)]
    (fn []
      [:div.mt1.proposal-detail
       [:h4 {:on-click #(swap! show? not)
             :class (when @show? "b")}
        (str "+ " title)]
       (when @show?
         [:div.ml3.mb4
          [render-markdown content]])])))

(defn proposal-component [proposal displayFormat]
  (let [{:keys [title
                longDescription
                shortDescription
                impact
                budget
                timeline
                images]} (:fields proposal)
        id (-> proposal :sys :id)
        {maxSelection :maxSelection} (:fields @(rf/subscribe [:election-in-view-2]))
        images (map #(:url %) images)
        thumbnail-image (first images)
        selected? (rg/atom (some #(= id %) @(rf/subscribe [:selected-proposals])))
        expand-image? (rg/atom false)
        lang (if @(rf/subscribe [:if-english?]) :en-US :es-US)]

    (fn []

      ;; display in rows

      (if (= displayFormat "Rows")
        [:div.proposal-component.pa3.pa4-ns.col-12
         {:style {:background-color (if @selected? "rgba(115,159,62,0.15)" "white")}}
         [:div.flexrow.mb3
          [:div.w-90
           [:h2.fw7.mr3 (str title)]
           [:p.f4 shortDescription]]
          [:div
           [:span.checkbox
            [:input {:id id
                     :type "checkbox"
                     :checked (if @selected? @selected? false)
                     :on-change #(if @selected?
                                   (rf/dispatch [:update-selected-proposals :remove id])
                                   (when (< (count @(rf/subscribe [:selected-proposals])) maxSelection)
                                     (rf/dispatch [:update-selected-proposals :add id])))}]
            [:label {:class "pl4 mr2 pl5-l mr0-l"
                     :for id}]]]]

         ; expandable details
         [detail (-> translations-db :budget lang) budget]
         [detail (-> translations-db :timeline lang) timeline]
         [detail (-> translations-db :description lang) longDescription]
         [detail (-> translations-db :community-impact lang) impact]]


        ;; display in a grid

        (if (= displayFormat "Grid")
          [:div.proposal-component.pa4.col-xs-12.col-md-6.col-lg-4
           {:style {:background-color (if @selected? "rgba(115,159,62,0.15)" "white")}}
           [:h2.fw7 title]
           [:p.f4 shortDescription]

           ; select/remove buttons
           [:div.tc.mt2.mb3
            [:button.mv2 {:on-click #(when (< (count @(rf/subscribe [:selected-proposals])) maxSelection)
                                       (rf/dispatch [:update-selected-proposals :add id]))
                          :class (if @selected? "selected" "select")}
             (if @selected? "Selected" "Select")]
            (when @selected?
              [:button.remove.mv2.ml3
               {:on-click #(rf/dispatch [:update-selected-proposals :remove id])}
               "Remove"])]

           ; expandable details
           [detail "Community Impact" impact]
           [detail "Budget Breakdown" budget]
           [detail "Timeline" timeline]

           ; thumbnail image
           [:div.thumbnail-wrapper
            {:on-click #(reset! expand-image? true)}
            [:img.w-100.mt2 {:src thumbnail-image}]
            (when (< 1 (count images))
              [:i.more-images-icon {:class "far fa-images"}])]

           ; expanded image gallery modal
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
                   ; if single image
                   [:img {:class "mb1 w-100"
                          :src thumbnail-image}]
                   ; if multiple images
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
                     [:span.sr-only "Next"]]])]]]])])))))
