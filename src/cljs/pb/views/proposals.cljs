(ns pb.views.proposals
  (:require [re-frame.core :as rf]
            [pb.components.proposal :refer [proposal-component]]
            [pb.components.loading :refer [loading-component]]
            [clojure.string :as string]
            [ajax.core :as ajax :refer [POST]]))

(defn query [ids]
  (let [queries (for [id ids]
                  (str "p" id ": proposalAS(q: \"sys.id="
                       id
                       "\") {
                         title
                         objective
                         impact
                         budget
                         timeline
                         images { url }
                       }"))]
      (str "{" (string/join queries) "}")))

(defn submit-vote []
  (POST "/api/vote" {:response-format (ajax/json-response-format {:keywords? true})
                     :format :raw
                     :params {:voter-id @(rf/subscribe [:voter-id])
                              :vote @(rf/subscribe [:selected-proposals])}}))

(defn proposals-view [election-slug]
  (if-let [election-in-view @(rf/subscribe [:election-in-view])]
    (let [{:keys [proposalRefs maxSelection]} @(rf/subscribe [:election-in-view])
          ids (map #(get-in % [:sys :id]) proposalRefs)
          query (query ids)]
      (rf/dispatch [:get-contentful-data :proposals-in-view query :election])
      (if-let [proposals @(rf/subscribe [:proposals-in-view])]
        [:div.proposals-view.mt5
         [:h2 "Instructions:"]
         [:ol
          [:li "Choose the projects you want to support by clicking on the 'Select' buttons."]
          [:li "You can vote for up to " maxSelection (if (> maxSelection 1)
                                                        " projects."
                                                        " project.")]
          [:li "Click the \"Submit My Vote\" button when you're ready to submit."]]
         [:div.tc {:on-click #(submit-vote)}
          [:button.submit.mt3 "Submit My Vote"]]
         [:div.proposals.row
          (for [proposal proposals]
            ^{:key (gensym "p-")}
            [proposal-component proposal])]]
        [loading-component]))
    (do
      (set! (-> js/window .-location .-href) (str "/#/" election-slug))
      [:div.proposals-view "Redirecting..."])))
