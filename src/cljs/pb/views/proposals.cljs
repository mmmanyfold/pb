(ns pb.views.proposals
  (:require [re-frame.core :as rf]
            [reagent.core :as rg]
            [re-com.core :as rc]
            [pb.components.proposal :refer [proposal-component]]
            [pb.components.loading :refer [loading-component]]
            [clojure.string :as string]
            [ajax.core :as ajax :refer [POST]]))

(def show-confirmation? (rg/atom false))

(defn query [ids]
  (let [queries
        (for [id ids]
          (str "p" id ": proposalAS(q: \"sys.id="
               id "\") { title shortDescription longDescription impact budget timeline images { url }}"))]
    (str "{" (string/join queries) "}")))

(defn submit-vote []
  (POST "/api/vote"
        {:response-format (ajax/json-response-format {:keywords? true})
         :handler         (fn []
                            (let [survey-url (:surveyUrl @(rf/subscribe [:election-in-view]))]
                              (reset! show-confirmation? true)
                              (rf/dispatch [:update-selected-proposals :reset])
                              (when-not @(rf/subscribe [:admin])
                                (js/setTimeout #(set! (.. js/window -location) survey-url) 3000))))
         :error-handler   #(rf/dispatch [:update-selected-proposals :reset])

         :format          :raw
         :params          {:voter-id @(rf/subscribe [:voter-id])
                           :vote     @(rf/subscribe [:selected-proposals])
                           :election (-> @(rf/subscribe [:election-in-view]) :sys :id)}}))

(defn confirmation-component []
  (when @show-confirmation?
   (let [survey-url (:surveyUrl @(rf/subscribe [:election-in-view]))]
    [rc/modal-panel
     :child [:div.confirmation.f3.f2-m.f1-l.pa2-m.pa3-l.tc
             (if @(rf/subscribe [:admin])
               [:div
                [:p.fw7 "Ballot recorded!"]
                [:small [:button#submit-another.f2
                         {:type     "button"
                          :on-click #(swap! show-confirmation? not)}
                         "Enter another one"]]]
               [:div
                [:p.fw7 "Thanks for voting!" [:br] "Your ballot has been submitted."]
                [:p.mb1 "Redirecting to survey..."]
                [:small "or " [:a {:href survey-url} "go to survey now"]]])]])))

(defn view []
  (let [election-in-view @(rf/subscribe [:election-in-view])
        {:keys [proposalRefs maxSelection displayFormat]} election-in-view
        ids (map #(-> % :sys :id) proposalRefs)
        query (query ids)]
    ;; TODO: update to rest call
    (rf/dispatch [:get-contentful-data :proposals-in-view query :election])
    (if-let [proposals @(rf/subscribe [:proposals-in-view])]
      (let [selected-proposals @(rf/subscribe [:selected-proposals])]
        [:div.proposals-view.mt5
         [confirmation-component]
         [:h2 "Instructions:"]
         [:ol
          [:li "Choose the projects you want to support by clicking the checkbox."]
          [:li "You can vote for up to " maxSelection (if (> maxSelection 1)
                                                        " projects."
                                                        " project.")]
          [:li "Click \"Submit My Ballot\" when you're ready to submit."]]
         [:div.tc
          [:input.submit.mt3 {:on-click submit-vote
                              :disabled (or (nil? selected-proposals)
                                            (empty? selected-proposals))
                              :type     "submit"
                              :value    "Submit My Ballot"}]]
         [:div.proposals.row
          (for [proposal proposals]
            ^{:key (gensym "p-")}
            [proposal-component proposal displayFormat])]])
      [loading-component])))
