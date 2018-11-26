(ns pb.contentful
  (:require [org.httpkit.client :as http]
            [ring.util.http-response :as response]
            [jsonista.core :as json]
            [clojure.pprint :refer [pprint]]))

(def mapper
  (json/object-mapper
    {:decode-key-fn (comp keyword)
     :encode-key-fn name}))

(defonce PB_CONTENTFUL_CD_API_TOKEN (System/getenv "PB_CONTENTFUL_CD_API_TOKEN"))

(defn get-entries []
  (let [space-id "0gtzstczow4j"
        en-query-params {"locale" "en-US"}
        es-query-params {"locale" "es"}
        opts {:headers {"Authorization" (str "Bearer " PB_CONTENTFUL_CD_API_TOKEN)}}
        en-request (http/get (format "https://cdn.contentful.com/spaces/%1s/entries" space-id)
                             (assoc opts :query-params en-query-params))
        es-request (http/get (format "https://cdn.contentful.com/spaces/%1s/entries" space-id)
                             (assoc opts :query-params es-query-params))
        {en-status :status en-body :body} @en-request
        {es-status :status es-body :body} @es-request]
    (if (and (= 200 en-status) (= 200 es-status))
      ;; process body response
      (let [en-json-body (json/read-value en-body mapper)
            es-json-body (json/read-value es-body mapper)
            en-items (:items en-json-body)
            es-items (:items es-json-body)
            en-proposals (filter #(= (-> % :sys :contentType :sys :id) "proposalA") en-items)
            en-elections (filter #(= (-> % :sys :contentType :sys :id) "election") en-items)
            es-proposals (filter #(= (-> % :sys :contentType :sys :id) "proposalA") es-items)
            es-elections (filter #(= (-> % :sys :contentType :sys :id) "election") es-items)]

        (response/ok {:en-US {:elections en-elections
                              :proposals en-proposals}
                      :es-US {:elections es-elections
                              :proposals es-proposals}}))
      (response/internal-server-error (str "Unable to retrieve contentful data for space: " space-id)))))