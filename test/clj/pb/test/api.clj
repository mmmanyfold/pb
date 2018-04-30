(ns pb.test.api
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [pb.handler :refer :all]
            [mount.core :as mount]
            [pb.routes.api :as api]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'pb.config/env
                 #'pb.handler/app)
    (f)))

(deftest test-twilio-sig-middleware-fn
  (testing "checksum signature match"
    (let [response (app (request :post "/api/voter/code/generate" {}))]
      (is (= 200 (:status response))))))
