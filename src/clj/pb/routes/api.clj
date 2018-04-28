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
  (let [{:keys [voter-code]} (:params req)]
    (if-let [voter (db-tx db/get-voter-by-code {:code (str/lower-case (str "pbkdf2+sha3_256$" voter-code "%"))})]
      (response/ok {:id (:id voter)})
      (response/not-found))))

(defn handle-voter-code
  "Creates voter code for a new phone number, or returns existing voter ID"
  [phone-number & [body]]
  (if-let [voter (db-tx db/get-voter-by-phone {:phone phone-number})]
    (let [code (:code voter)
          voting-code (subs (str/replace (str/replace code "pbkdf2+sha3_256" "") "$" "") 0 8)]
      {:ok (send-code phone-number voting-code)})
    (let [code (hashers/derive phone-number {:alg :pbkdf2+sha3_256})
          voting-code (subs (str/replace (str/replace code "pbkdf2+sha3_256" "") "$" "") 0 8)]
      (db-tx db/create-voter! {:phone phone-number
                               :admin false
                               :is_active true
                               :code code})
      {:ok (send-code phone-number voting-code)})))

(defn handle-voter-code-from-ui
  [req]
  (let [phone-number (get-in req [:params :phone-number])]
    (handle-voter-code phone-number)))

(defn handle-voter-code-from-sms
  [req]
  (let [{:keys [From Body]} (:params req)]
    ;TODO: use election name from Body ?
    (handle-voter-code From)))

(defn handle-voter-vote
  "Creates voter-vote from voter id and selection"
  [req]
  (let [{:keys [voter-id vote]} (:params req)
        vote-id (:id (db-tx db/create-vote! {:vote vote}))]
    (db-tx db/create-voter-vote! {:voter_id (int (as-int voter-id))
                                  :vote_id vote-id})
    (response/ok)))


(defroutes api-routes
  (context "/api" []
    (GET "/checkcode/:voter-code" [] check-voter-code)
    (POST "/votercode/:phone-number" [] handle-voter-code-from-ui)
    (POST "/votercode" [] handle-voter-code-from-sms)
    (POST "/registervote/:voter-id/:vote" [] handle-voter-vote)))
