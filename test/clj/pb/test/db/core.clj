(ns pb.test.db.core
  (:require [pb.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [pb.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'pb.config/env
      #'pb.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-voter!
               t-conn
               {:phone      "1234567890"
                :admin      false
                :is_active  true
                :code       "a8903u48r"})))
    (is (= "1234567890"
           (:phone (db/get-voter t-conn {:phone "1234567890"}))))))
