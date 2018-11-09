(ns pb.views.admin
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as rf]
            [cljs-http.client :as http]))

(defn- admin-voting-component []
  [:div#admin
   [:h1 "admin view"]
   [:p "..."]])

(defn- unauthorized-component []
  [:div#admin
   [:h1 "404: unauthorized"]])

(defn view []
  (if @(rf/subscribe [:admin])
    [admin-voting-component]
    (let [admin-secret (js/prompt "admin password")]
      (go
        (let [resp-chann (http/get "/api/checkadmin" {:query-params {"secret" admin-secret}})
              {{admin? :admin} :body} (<! resp-chann)]
          (rf/dispatch [:set-admin admin?])))
      (if @(rf/subscribe [:admin])
        [admin-voting-component]
        [unauthorized-component]))))