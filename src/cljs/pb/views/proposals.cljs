(ns pb.views.proposals
  (:require [re-frame.core :as rf]
            [pb.components.proposal :refer [proposal-component]]
            [pb.components.loading :refer [loading-component]]
            [clojure.string :as string]))

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

(defn proposals-view [election-slug]
  (if-let [election-in-view @(rf/subscribe [:election-in-view])]
    (let [{:keys [proposalRefs]} @(rf/subscribe [:election-in-view])
          ids (map #(get-in % [:sys :id]) proposalRefs)
          query (query ids)]
      (rf/dispatch [:get-contentful-data :proposals-in-view query :election])
      (if-let [proposals @(rf/subscribe [:proposals-in-view])]
        [:div.proposals-view.mt5
         [:h2 "Instructions:"]
         [:ol
          [:li "Choose the projects you want to support by clicking on the 'Select' buttons."]
          [:li "You can vote for up to 3 projects."]
          [:li "Click the \"Submit My Vote\" button when you're ready to submit."]]
         [:div.tc
          [:button.submit.mt3 "Submit My Vote"]]
         [:div.proposals.row
          (for [proposal proposals]
            ^{:key (gensym "p-")}
            [proposal-component (first (val proposal))])]]
        [loading-component]))
    (do
      (set! (-> js/window .-location .-href) (str "/#/" election-slug))
      [:div.proposals-view "Redirecting..."])))
