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
        opts {:query-params {"locale" "*"} ;; => every locale available
              :headers      {"Authorization" (str "Bearer " PB_CONTENTFUL_CD_API_TOKEN)}}
        request (http/get (format "https://cdn.contentful.com/spaces/%1s/entries" space-id) opts)
        {status :status body :body} @request]
    (if (= 200 status)
      ;; process body response
      (let [json-body (json/read-value body mapper)
            items (:items json-body)
            proposals (filter #(= (-> % :sys :contentType :sys :id) "proposalA") items)
            elections (filter #(= (-> % :sys :contentType :sys :id) "election") items)]
        {:elections elections
         :proposals proposals})

      (response/internal-server-error (str "Unable to retreive contentful data for space: " space-id)))))