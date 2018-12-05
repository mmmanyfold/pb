(ns pb.model
  (:require [pb.db.core :refer [*db*] :as db])
  (:require [clojure.java.jdbc :as jdbc]))

(defn db-tx [f & [args]]
  (try
    (jdbc/with-db-transaction [t-conn *db*]
                              (jdbc/db-set-rollback-only! t-conn)
                              (f args))
    (catch Exception e (str "caught exception: " (.getMessage e) "\ncaused by: " (.getCause e)))))

(defn get-votes []
  (try
    (jdbc/with-db-transaction [t-conn *db*]
                              (jdbc/db-set-rollback-only! t-conn)
                              (db/get-votes))
    (catch Exception e (str "caught exception: " (.getMessage e) "\ncaused by: " (.getCause e)))))