(ns dynamo-embedded-clj.core
  (:require [integrant.core :as ig]
            [dynamo-embedded-clj.state :as state])
  (:import (clojure.lang ExceptionInfo)))

(def default-config
  ["-inMemory" "-port" "8000"])

(defn ->ig-config [config]
  {:dynamo-embedded-clj.dynamo-local/dynamo config})

(defn halt-dynamo! []
  (when @state/state
    (swap! state/state
           (fn [s]
             (ig/halt! (:system s))
             nil))))

(defmacro retry
  [cnt expr]
  (letfn [(go [cnt]
            (if (zero? cnt)
              expr
              `(try ~expr
                    (catch Exception e#
                      (retry ~(dec cnt) ~expr)))))]
    (go cnt)))

(defn init-dynamo
  ([] (init-dynamo default-config))
  ([config]
   (let [ig-config (->ig-config config)]
     (try
       (halt-dynamo!)
       (ig/load-namespaces ig-config)
       (reset! state/state
               {:system (ig/init ig-config)
                :config ig-config})
       (catch ExceptionInfo ex
         (ig/halt! (:system (ex-data ex)))
         (throw (.getCause ex)))))))

(defn with-dynamo-fn
  "Startup with the specified configuration; executes the function then shuts down."
  ([config f]
   (try
     (init-dynamo config)
     (f)
     (finally
       (halt-dynamo!))))
  ([f]
   (with-dynamo-fn default-config f)))

(defmacro with-dynamo
  "Startup with the specified configuration; executes the body then shuts down."
  [config & body]
  `(with-dynamo-fn ~config (fn [] ~@body)))

(comment (init-dynamo)
         (halt-dynamo!))