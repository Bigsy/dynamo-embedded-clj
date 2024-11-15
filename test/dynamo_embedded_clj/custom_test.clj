(ns dynamo-embedded-clj.custom-test
  (:require [clojure.test :refer :all]
            [taoensso.faraday :as far]
            [dynamo-embedded-clj.core :as sut]))

(use-fixtures :once sut/with-dynamo-fn)


(defn around-all
  [f]
  (sut/with-dynamo-fn ["-inMemory" "-port" "8010" "-disableTelemetry"] f))

(use-fixtures :once around-all)

(def client-opts
  {:access-key "eqweqwewqeqweqwe"
   :secret-key "qewqeqwewqeqweqw"
   :endpoint   "http://localhost:8010"})


(deftest can-wrap-around
  (testing "using defaults"
    (far/list-tables client-opts)
    (far/create-table client-opts :my-table
                      [:id :n]
                      {:throughput {:read 1 :write 1}
                       :block? true})
    (is (= '(:my-table) (far/list-tables client-opts)))
    (far/put-item client-opts
                  :my-table
                  {:id 0
                   :name "Steve"})

    (is (= {:id 0N, :name "Steve"} (far/get-item client-opts :my-table {:id 0})))
    (far/delete-table client-opts :my-table)))