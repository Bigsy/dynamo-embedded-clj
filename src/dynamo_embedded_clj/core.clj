(ns dynamo-embedded-clj.core
  (:require [clojure.pprint :as pprint]
            [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [org.httpkit.client :as http]
            [dynamo-embedded-clj.state :as state]))

(def default-config
  {:port 8000
   :in-memory? true})

(defn ->ig-config [config]
  {:dynamo-embedded-clj.dynamo/dynamo (merge default-config config)})

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
   (let [ig-config (->ig-config config)
         config-pp (with-out-str (pprint/pprint config))]
     (log/info "starting dynamo with config:" config-pp)
     (try
       (halt-dynamo!)
       (ig/load-namespaces ig-config)
       (reset! state/state
               {:system (ig/init ig-config)
                :config ig-config})
       (retry 40 (when (:error @(http/get (format "http://localhost:%s/shell/" (:port config))))
                   (do (Thread/sleep 200) (throw (Exception.)))))
       (catch clojure.lang.ExceptionInfo ex
         (ig/halt! (:system (ex-data ex)))
         (throw (.getCause ex)))))))

(defn with-dynamo-fn
  "Startup with the specified configuration; executes the function then shuts down."
  ([config f]
   {:pre [(map? config) (fn? f)]}
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