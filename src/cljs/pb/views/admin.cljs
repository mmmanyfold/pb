(ns pb.views.admin
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as rf]
            [pb.views.proposals :as proposals]
            [cljs-http.client :as http]))

(defn admin-voting-component []
  [:div#admin
   [:h1.ttu "Admin: Enter votes manually"]
   [proposals/view]])

(defn unauthorized-component []
  [:div#admin
   [:h1 "401: unauthorized"]])

(defn view [election]
  ;; TODO: update to rest call
  (let [query (str "{ elections(q: \"fields.shortTitle=" election
                   "\") {
                     title
                     additionalIdLabel
                     startOnline
                     endOnline
                     maxSelection
                     surveyUrl
                     displayFormat
                     sys { id }
                     proposalRefs {
                       sys { id }}
                   }}")]
    (rf/dispatch [:get-contentful-data :election-in-view query :election])
    (if @(rf/subscribe [:admin])
      [admin-voting-component]
      (let [admin-secret (js/prompt "admin password")]
        (go
          (let [resp-chann (http/get "/api/checkadmin" {:query-params {"secret" admin-secret}})
                {{admin :admin} :body} (<! resp-chann)]
            (rf/dispatch-sync [:set-admin admin])))
        (if @(rf/subscribe [:admin])
          [admin-voting-component]
          [unauthorized-component])))))