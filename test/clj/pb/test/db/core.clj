(ns pb.test.db.core
  (:require [pb.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.jdbc :as jdbc]
            [pb.config :refer [env]]
            [mount.core :as mount]
            [buddy.hashers :as hashers]
            [user]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'pb.config/env
      #'pb.db.core/*db*)
    (user/reset-db)
    (user/migrate)
    (f)))

(deftest test-users
    (jdbc/with-db-transaction [t-conn *db*]
      (jdbc/db-set-rollback-only! t-conn)
      (let [code (hashers/derive "1234567890" {:alg :pbkdf2+sha3_256})]
        (is (= 1 (db/create-voter!
                   {:phone      "1234567890"
                    :admin      false
                    :is_active  true
                    :code code})))
        (is (= "1234567890"
               (:phone (db/get-voter-by-code {:code (str "pbkdf2+sha3_256$" (subs (str/replace code "pbkdf2+sha3_256$" "") 0 8) "%")}))))
        (is (= "1234567890"
               (:phone (db/get-voter-by-phone {:phone "1234567890"}))))
        (is (= "1234567890"
               (:phone (db/get-voter-by-id {:id 1})))))))
