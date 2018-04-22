(ns pb.routes.home
  (:require [pb.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [buddy.hashers :as hashers]
            [pb.db.core :refer [*db*] :as db]
            [clojure.java.jdbc :as jdbc]))

(defn db-tx [f & [args]]
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (f t-conn args)))

(defn home-page []
  (layout/render "home.html"))

(defn handle-voter-id
  "Creates voter ID for a new phone number, or returns existing voter ID"
  [req]
  (let [phone-number (get-in req [:params :phone-number])]
    (if-let [voter (jdbc/with-db-transaction [t-conn *db*]
                     (jdbc/db-set-rollback-only! t-conn)
                     (db/get-voter t-conn {:phone phone-number}))]
      (let [code (:code voter)]
        (prn code)) ;return pre-existing code via twilio)
      (let [code (hashers/derive phone-number {:alg :pbkdf2+sha3_256})]
        (jdbc/with-db-transaction [t-conn *db*]
                       (jdbc/db-set-rollback-only! t-conn)
                       (prn (db/all-voters t-conn)))
        (jdbc/with-db-transaction [t-conn *db*]
          (jdbc/db-set-rollback-only! t-conn)
          (db/create-voter! t-conn {:phone phone-number
                                    :admin false
                                    :is_active true
                                    :code code}))
        (prn code))))) ;return new code via twilio



(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/graphiql" [] (layout/render "graphiql.html"))
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8")))
  (GET "/voterid/:phone-number" [] handle-voter-id))
