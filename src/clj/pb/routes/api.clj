(ns pb.routes.api
  (:require [pb.db.core :refer [*db*] :as db])
  (:require [compojure.core :refer [context defroutes GET POST]]
            [twilio.core :as twilio]
            [ring.util.http-response :as response]
            [buddy.hashers :as hashers]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [buddy.core.mac :as mac]
            [buddy.core.codecs :as codecs]
            [pandect.algo.sha1 :refer [sha1-hmac]]
            [buddy.core.codecs.base64 :as b64]
            [clojure.pprint :refer [pprint]]))

(defonce PB_TWILIO_AUTH_TOKEN (System/getenv "PB_TWILIO_AUTH_TOKEN"))

(defonce PB_TWILIO_ACCOUNT_SID (System/getenv "PB_TWILIO_ACCOUNT_SID"))

(defonce PB_TWILIO_PHONE_NUMBER (System/getenv "PB_TWILIO_PHONE_NUMBER"))

(defn sanatize-code [raw]
  (subs (str/replace (str/replace raw "pbkdf2+sha3_256" "") "$" "") 0 8))

(defn db-tx [f & [args]]
  (try
    (jdbc/with-db-transaction [t-conn *db*]
      (jdbc/db-set-rollback-only! t-conn)
      (f args))
    (catch Exception e (str "caught exception: " (.getMessage e)))))

(defn send-code [phone code]
  (twilio/with-auth PB_TWILIO_ACCOUNT_SID PB_TWILIO_AUTH_TOKEN
    @(twilio/send-sms
       {:From PB_TWILIO_PHONE_NUMBER
        :To phone
        :Body code})))

(defn validate-request-from-twilio
  "Uses signature provided by twilio and auth-token to verify that the request are coming from twilio API"
  [req]
  (when-let [x-twilio-sig (get (:headers req) "x-twilio-signature")]
    (let [scheme (name (:scheme req))
          host (get (:headers req) "host")
          uri (:uri req)
          sorted-params (into (sorted-map) (:params req))
          params->as-url (ring.util.codec/form-encode sorted-params)
          url (str scheme "://" host uri params->as-url)
          final-url (str/join (remove #(or (= \& %) (= \= %)) (seq url)))
          ;resulting-hash (mac/hash final-url {:key PB_TWILIO_AUTH_TOKEN :alg :hmac+sha256})
          resulting-hash (-> (b64/encode (sha1-hmac final-url PB_TWILIO_AUTH_TOKEN) true)
                             (codecs/bytes->str))]
      ;(prn (mac/verify final-url (codecs/hex->bytes resulting-hash)
      ;                 {:key PB_TWILIO_AUTH_TOKEN :alg :hmac+sha256}))
      ;; TODO: fix comparison of resulting hash vs x-twilio-sig; for now lets simply return true
      ;; (= x-twilio-sig resulting-hash)
      (prn resulting-hash)
      (prn x-twilio-sig)
      true)))

(defn validate-voter-code
  "Checks if voter code is valid and has not already voted"
  [req]
  (let [code (-> req :params :code)]
    (if-let [voter (db-tx db/get-voter-by-code {:code (str/lower-case (str "pbkdf2+sha3_256$" code "%"))})]
      (response/ok {:id (:id voter)})
      (response/not-found))))

;TODO: use election name from Body ?
(defn handle-voter-code-from-sms
  "Creates voter code for a new phone number, or returns existing voter ID"
  [req]
  (if (validate-request-from-twilio req)
    (let [phone (-> req :params :From)]
      (if-let [voter (db-tx db/get-voter-by-phone {:phone phone})]
        (let [raw-code (:code voter)
              code (sanatize-code raw-code)]
          {:ok (send-code phone code)})
        (let [raw-code (hashers/derive phone {:alg :pbkdf2+sha3_256})
              code (sanatize-code raw-code)]
          (db-tx db/create-voter! {:phone phone
                                   :admin false
                                   :is_active true
                                   :code raw-code})
          {:ok (send-code phone code)})))
    (response/unauthorized)))

(defroutes api-routes
  (context "/api" []
    (context "/voter" []
      (context "/code" []
        (POST "/generate" [] handle-voter-code-from-sms)
        (GET "/validate/:code" [] validate-voter-code)))
    (context "/vote" []
      ,,,)))