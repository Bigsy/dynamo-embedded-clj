(ns dynamo-embedded-clj.default-test
  (:require [clojure.test :refer :all]
            [taoensso.faraday :as far]
            [dynamo-embedded-clj.core :as sut]))

(use-fixtures :once sut/with-dynamo-fn)

(defn around-all
  [f]
  (sut/with-dynamo-fn f))

(use-fixtures :once around-all)

(def client-opts
  {:access-key "eqweqwewqeqweqwe"
   :secret-key "qewqeqwewqeqweqw"
   :endpoint "http://localhost:8000"})



(deftest can-wrap-around
  (testing "using defaults"
      (is (= ()
             (far/list-tables client-opts)))))