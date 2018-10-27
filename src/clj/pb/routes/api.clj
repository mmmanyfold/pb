(ns pb.routes.api
  (:require [pb.db.core :refer [*db*] :as db])
  (:require [compojure.core :refer [context defroutes GET POST]]
            [compojure.coercions :refer [as-int]]
            [ring.util.http-response :as response]
            [buddy.hashers :as hashers]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as string]
            [pb.twilio :as twilio]
            [clojure.spec.alpha :as s]))

(defonce PB_TWILIO_AUTH_TOKEN (System/getenv "PB_TWILIO_AUTH_TOKEN"))

(defonce PB_TWILIO_ACCOUNT_SID (System/getenv "PB_TWILIO_ACCOUNT_SID"))

(defonce PB_TWILIO_PHONE_NUMBER (System/getenv "PB_TWILIO_PHONE_NUMBER"))

(defn check-and-throw
  "throw an exception if value doesn't match the spec"
  [a-spec val]
  (if (s/valid? a-spec val)
    val
    (throw (ex-info (str "spec failed because: " (s/explain-str a-spec val)) {}))))

(s/def ::check-code #(let [{:keys [voter-code election]} (:params %)]
                       (not (or (nil? voter-code)
                                (nil? election)))))

(s/def ::handle-code #(let [{:keys [phone-number election]} (:body %)]
                        (not (or (nil? phone-number)
                                 (nil? election)))))

(s/def ::handle-vote #(let [{:keys [voter-id vote election]} (:params %)]
                        (not (or (nil? voter-id)
                                 (nil? vote)
                                 (nil? election)))))

(defn db-tx [f & [args]]
  (try
    (jdbc/with-db-transaction [t-conn *db*]
      (jdbc/db-set-rollback-only! t-conn)
      (f args))
    (catch Exception e (str "caught exc.eption: " (.getMessage e) "\ncaused by: " (.getCause e)))))

(defn send-code [phone-number voting-code]
  (twilio/with-auth PB_TWILIO_ACCOUNT_SID PB_TWILIO_AUTH_TOKEN
    @(twilio/send-sms
       {:From PB_TWILIO_PHONE_NUMBER
        :To phone-number
        :Body voting-code})))

(defn check-voter-code
  "Checks if voter code is valid and has not already voted"
  [req]
  (try
    (check-and-throw ::check-code req)
    (let [{:keys [voter-code election]} (:params req)]
      (if-let [voter (db-tx db/get-voter-by-code {:code (string/lower-case (str "pbkdf2+sha3_256$" voter-code "%"))
                                                  :election election})]
        (if (nil? (db-tx db/get-voter-vote {:id (:id voter)}))
          (response/ok {:id (:id voter)})
          (response/conflict {:message "Already voted"}))
        (response/not-found {:message "Voting code does not exist"})))
    (catch Exception e (response/bad-request {:message "Bad parameters"}))))

(defn handle-voter-code
  "Creates voter code for a new phone number, or returns existing voter ID"
  [additional-id phone-number election]
  (if-let [voter (db-tx db/get-voter-by-phone {:phone phone-number
                                               :election election})]
    (let [code (:code voter)
          voting-code (subs (string/replace (string/replace code "pbkdf2+sha3_256" "") "$" "") 0 8)]
      (send-code phone-number voting-code)
      (response/ok {:message "Voting code sent"}))
    (let [code (hashers/derive phone-number {:alg :pbkdf2+sha3_256})
          voting-code (subs (string/replace (string/replace code "pbkdf2+sha3_256" "") "$" "") 0 8)]
      (db-tx db/create-voter! {:additional_id additional-id
                               :phone phone-number
                               :admin false
                               :is_active true
                               :code code
                               :election election})
      (send-code phone-number voting-code)
      (response/ok {:message "Voting code sent"}))))

(defn handle-voter-code-from-ui
  [req]
  (try
    (check-and-throw ::handle-vote req)
    (let [{:keys [additional-id phone-number election]} (:body req)]
      (handle-voter-code additional-id phone-number election))
    (catch Exception e (response/bad-request {:message "Bad parameters"}))))

(s/fdef handle-voter-code-from-ui
        :args (fn [a]
                (let [{:keys [phone-number election]} (:body a)]
                  (not (and (nil? phone-number)
                            (nil? election))))))

(defn handle-vote
  "Creates voter-vote from voter id and selection"
  [req]
  (try
    (check-and-throw ::handle-vote req)
    (let [{:keys [voter-id vote election]} (:params req)
          voter-id (int (as-int voter-id))]
      (if (nil? (db-tx db/get-voter-vote {:id voter-id}))
        (let [vote-id (:id (db-tx db/create-vote! {:vote vote
                                                   :election election}))]
          (db-tx db/create-voter-vote! {:voter_id voter-id
                                        :vote_id vote-id
                                        :election election})
          (response/ok {:message "Vote registered"}))
        (response/conflict {:message "Already voted"})))
    (catch Exception e (response/bad-request {:message "Bad parameters"}))))


(s/fdef handle-vote
        :args (fn [a]
                (let [{:keys [voter-id vote election]} (:params a)]
                  (not (and (nil? voter-id)
                            (nil? vote)
                            (nil? election))))))

(defroutes api-routes
  (context "/api" []
    (GET "/checkcode" [] check-voter-code)
    (POST "/votercode" [] handle-voter-code-from-ui)
    (POST "/vote" [] handle-vote)))
