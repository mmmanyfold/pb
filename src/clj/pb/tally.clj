(ns pb.tally
  (:require [pb.model :as model]
            [pb.contentful :as contentful]
            [ring.util.http-response :as response]))

(defn tally [election]
  (let [{{{proposals :proposals elections :elections} :en-US} :body} (contentful/get-entries)
        election (some #(when (= (-> % :fields :shortTitle) election) %) elections)
        proposalRefs (-> election :fields :proposalRefs)
        proposalIds (map (comp :id :sys) proposalRefs)
        proposalsById (map
                        (fn [id]
                          (let [match (some #(when (= id (-> % :sys :id)) %) proposals)]
                            [(keyword id) (hash-map :title (-> match :fields :title))]))
                        proposalIds)
        proposalsById (into (sorted-map) proposalsById)
        votes (model/get-votes)
        allVotesStr (clojure.string/join (map :vote votes))
        proposalsById (map
                        (fn [[k v]]
                          (let [c (count (re-seq (re-pattern (name k)) allVotesStr))]
                            (hash-map k (assoc v :count c))))
                        proposalsById)]
    (response/ok proposalsById)))