(ns pb.routes.api
  (:require [pb.db.core :refer [*db*] :as db])
  (:require [pb.layout :as layout]
            [compojure.core :refer [context defroutes GET POST]]
            [compojure.coercions :refer [as-int]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [buddy.hashers :as hashers]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [twilio.core :as twilio]))

(defonce PB_TWILIO_AUTH_TOKEN (System/getenv "PB_TWILIO_AUTH_TOKEN"))
(defonce PB_TWILIO_ACCOUNT_SID (System/getenv "PB_TWILIO_ACCOUNT_SID"))
(defonce PB_TWILIO_PHONE_NUMBER (System/getenv "PB_TWILIO_PHONE_NUMBER"))

(defn db-tx [f & [args]]
  (try
    (jdbc/with-db-transaction [t-conn *db*]
      (jdbc/db-set-rollback-only! t-conn)
      (f args))
    (catch Exception e (str "caught exception: " (.getMessage e) "\ncaused by: " (.getCause e)))))

(defn send-code [phone-number voting-code]
  (twilio/with-auth PB_TWILIO_ACCOUNT_SID PB_TWILIO_AUTH_TOKEN
    @(twilio/send-sms
       {:From PB_TWILIO_PHONE_NUMBER
        :To phone-number
        :Body voting-code})))

(defn check-voter-code
  "Checks if voter code is valid and has not already voted"
  [req]
  (let [{:keys [voter-code election]} (:params req)]
    (if-let [voter (db-tx db/get-voter-by-code {:code (str/lower-case (str "pbkdf2+sha3_256$" voter-code "%"))
                                                :election election})]
      (if (nil? (db-tx db/get-voter-vote {:id (:id voter)}))
        (response/ok {:id (:id voter)})
        (response/conflict))
      (response/not-found))))

(defn handle-voter-code
  "Creates voter code for a new phone number, or returns existing voter ID"
  [additional-id phone-number election]
  (if-let [voter (db-tx db/get-voter-by-phone {:phone phone-number
                                               :election election})]
    (let [code (:code voter)
          voting-code (subs (str/replace (str/replace code "pbkdf.2+sha3_256" "") "$" "") 0 8)]
      (response/ok {:ok (send-code phone-number voting-code)}))
    (let [code (hashers/derive phone-number {:alg :pbkdf2+sha3_256})
          voting-code (subs (str/replace (str/replace code "pbkdf2+sha3_256" "") "$" "") 0 8)]
      (db-tx db/create-voter! {:additional_id additional-id
                               :phone phone-number
                               :admin false
                               :is_active true
                               :code code
                               :election election})
      (response/ok {:ok (send-code phone-number voting-code)}))))

(defn handle-voter-code-from-ui
  [req]
  (let [{:keys [additional-id phone-number election]} (:body req)]
    (handle-voter-code additional-id phone-number election)))

(defn handle-vote
  "Creates voter-vote from voter id and selection"
  [req]
  (let [{:keys [voter-id vote election]} (:params req)
        voter-id (int (as-int voter-id))]
    (if (nil? (db-tx db/get-voter-vote {:id voter-id}))
      (let [vote-id (:id (db-tx db/create-vote! {:vote vote
                                                 :election election}))]
        (db-tx db/create-voter-vote! {:voter_id voter-id
                                      :vote_id vote-id
                                      :election election})
        (response/ok {:vote vote}))
      (response/conflict))))


(defroutes api-routes
  (context "/api" []
    (GET "/checkcode" [] check-voter-code)
    (POST "/votercode" [] handle-voter-code-from-ui)
    (POST "/vote" [] handle-vote)))
