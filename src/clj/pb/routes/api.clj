(ns pb.routes.api
  (:require [pb.db.core :refer [*db*] :as db])
  (:require [pb.layout :as layout]
            [compojure.core :refer [context defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [buddy.hashers :as hashers]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [twilio.core :as twilio]))

(defonce PB_TWILIO_AUTH_TOKEN (System/getenv "PB_TWILIO_AUTH_TOKEN"))
(defonce PB_TWILIO_SERVICE_SID (System/getenv "PB_TWILIO_SERVICE_SID"))
(defonce PB_TWILIO_PHONE_NUMBER (System/getenv "PB_TWILIO_PHONE_NUMBER"))

(defn db-tx [f & [args]]
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (f args)))

(defn handle-voter-id
  "Creates voter ID for a new phone number, or returns existing voter ID"
  [req]
  (let [phone-number (get-in req [:params :phone-number])]
    (if-let [voter (db-tx db/get-voter {:phone phone-number})]
      (let [code (:code voter)
            voting-code (subs (str/replace (str/replace code "pbkdf2+sha3_256" "") "$" "") 0 8)]
        (twilio/with-auth PB_TWILIO_SERVICE_SID PB_TWILIO_AUTH_TOKEN
          @(twilio/send-sms
             {:From PB_TWILIO_PHONE_NUMBER
              :To phone-number
              :Body voting-code})))
      (let [code (hashers/derive phone-number {:alg :pbkdf2+sha3_256})
            voting-code (subs (str/replace (str/replace code "pbkdf2+sha3_256" "") "$" "") 0 8)]
        (db-tx db/create-voter! {:phone phone-number
                                 :admin false
                                 :is_active true
                                 :code code})
        (twilio/with-auth PB_TWILIO_SERVICE_SID PB_TWILIO_AUTH_TOKEN
          @(twilio/send-sms
             {:From PB_TWILIO_PHONE_NUMBER
              :To phone-number
              :Body voting-code}))))))

(defroutes api-routes
  (context "/api" []
    (GET "/voterid/:phone-number" [] handle-voter-id)))
