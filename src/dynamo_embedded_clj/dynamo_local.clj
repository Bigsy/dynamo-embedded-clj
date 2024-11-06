(ns dynamo-embedded-clj.dynamo-local
  (:require [integrant.core :as ig])
  (:import [com.amazonaws.services.dynamodbv2.local.main ServerRunner]))

(defn start-server!
  "Start DynamoDB Local server on specified port"
  [config]
  (let [local-args (into-array String config)
        server (ServerRunner/createServerFromCommandLineArgs local-args)]
    (.start server)
    server))

(defn stop-server!
  "Stop DynamoDB Local server"
  [dyn]
  (when dyn
    (.stop dyn)))

(defmethod ig/init-key ::dynamo [_ config]
  (start-server! config))

(defmethod ig/halt-key! ::dynamo [_ dyn]
  (stop-server! dyn))