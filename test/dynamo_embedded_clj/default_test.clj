(ns dynamo-embedded-clj.default-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [taoensso.faraday :as far]
            [dynamo-embedded-clj.core :as sut]
            [dynamo-embedded-clj.dynamo :as dy]))

(use-fixtures :once sut/with-dynamo-fn)

(defn delete-dir [dir]
  (let [dir (java.io.File. dir)]
    (when (.exists dir)
      (doseq [file (.listFiles dir)]
        (if (.isDirectory file)
          (delete-dir (.getAbsolutePath file))
          (.delete file)))
      (.delete dir))))

(defn around-all
  [f]
  (delete-dir dy/dynamo-directory)
  (sut/with-dynamo-fn f))

(use-fixtures :once around-all)

(def client-opts
  {:access-key "eqweqwewqeqweqwe"
   :secret-key "qewqeqwewqeqweqw"
   :endpoint   "http://localhost:8000"})


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