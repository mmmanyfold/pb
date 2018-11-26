(ns pb.views.proposals
  (:require [re-frame.core :as rf]
            [reagent.core :as rg]
            [re-com.core :as rc]
            [pb.helpers :refer [render-markdown]]
            [pb.components.proposal :refer [proposal-component]]
            [pb.components.loading :refer [loading-component]]
            [ajax.core :as ajax :refer [POST]]
            [pb.db :refer [translations-db]]))

(def show-confirmation? (rg/atom false))

(defn submit-vote []
  (POST "/api/vote"
        {:response-format (ajax/json-response-format {:keywords? true})
         :handler         (fn []
                            (let [{{survey-url :surveyUrl} :fields} @(rf/subscribe [:election-in-view-2])]
                              (reset! show-confirmation? true)
                              (rf/dispatch [:update-selected-proposals :reset])
                              (when-not @(rf/subscribe [:admin])
                                (js/setTimeout #(set! (.. js/window -location) survey-url) 3000))))
         :error-handler   #(rf/dispatch [:update-selected-proposals :reset])

         :format          :raw
         :params          {:voter-id @(rf/subscribe [:voter-id])
                           :vote     @(rf/subscribe [:selected-proposals])
                           :election (-> @(rf/subscribe [:election-in-view-2]) :sys :id)}}))

(defn confirmation-component []
  (when @show-confirmation?
   (let [{{survey-url :surveyUrl} :fields} @(rf/subscribe [:election-in-view-2])
         lang @(rf/subscribe [:language-in-view])]
    [rc/modal-panel
     :child [:div.confirmation.f3.f2-m.f1-l.pa2-m.pa3-l.tc
             (if @(rf/subscribe [:admin])
               [:div
                [:p.fw7 (-> translations-db :ballot-recorded lang)]
                [:small [:button#submit-another.f2
                         {:type     "button"
                          :on-click #(swap! show-confirmation? not)}
                         "Enter another one"]]]
               [:div
                [:p.fw7 (-> translations-db :thanks-for-voting lang)
                 [:br] (-> translations-db :your-ballot-has-been-recorded lang)]
                [:p.mb1 (-> translations-db :redirecting-to-survey lang)]
                [:small [:a {:href survey-url} (-> translations-db :or-goto-surver-now lang)]]])]])))

(defn view []
  (let [{:keys [displayFormat
                submitBallotButtonText
                instructions]} (:fields @(rf/subscribe [:election-in-view-2]))]
    (if-let [proposals @(rf/subscribe [:proposals-in-view])]
      (let [selected-proposals @(rf/subscribe [:selected-proposals])]
        [:div.proposals-view.mt5
         [confirmation-component]
         [render-markdown instructions]
         [:div.tc
          [:input.submit.mt3 {:on-click submit-vote
                              :disabled (or (nil? selected-proposals)
                                            (empty? selected-proposals))
                              :type     "submit"
                              :value    submitBallotButtonText}]]
         [:div.proposals.row
          (for [proposal proposals]
            ^{:key (gensym "p-")}
            [proposal-component proposal displayFormat])]])
      [loading-component])))
