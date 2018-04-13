(ns pb.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [pb.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[pb started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[pb has shut down successfully]=-"))
   :middleware wrap-dev})
