(ns user
  (:require [pb.config :refer [env]]
            [mount.core :as mount]
            [pb.figwheel :refer [start-fw stop-fw cljs]]
            [pb.core :refer [start-app]]
            [luminus-migrations.core :as migrations]))

(defn start []
  (mount/start-without #'pb.core/repl-server))

(defn stop []
  (mount/stop-except #'pb.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))
