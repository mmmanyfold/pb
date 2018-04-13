(ns pb.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [pb.layout :refer [error-page]]
            [pb.routes.home :refer [home-routes]]
            [pb.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [pb.env :refer [defaults]]
            [mount.core :as mount]
            [pb.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats))
          #'service-routes
      (route/not-found
        (:body
          (error-page {:status 404
                       :title "page not found"}))))))
