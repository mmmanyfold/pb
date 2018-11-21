(ns pb.views.admin
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as rf]
            [pb.views.proposals :as proposals]
            [cljs-http.client :as http]))

(defn- admin-voting-component []
  [:div#admin
   [:h1 "admin view"]
   [proposals/view @(rf/subscribe [:admin-election])]])

(defn- unauthorized-component []
  [:div#admin
   [:h1 "401: unauthorized"]])

(defn view []
  (if @(rf/subscribe [:admin])
    [admin-voting-component]
    (let [admin-secret (js/prompt "admin password")]
      (go
        (let [resp-chann (http/get "/api/checkadmin" {:query-params {"secret" admin-secret}})
              {{admin :admin} :body} (<! resp-chann)]
          (rf/dispatch [:set-admin admin])))
      (if @(rf/subscribe [:admin])
        [admin-voting-component]
        [unauthorized-component]))))