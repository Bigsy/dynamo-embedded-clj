(ns dynamo-embedded-clj.dynamo
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [integrant.core :as ig]
            [clojure.tools.logging :as log])
  (:import [java.io File]
           [java.nio.file Files Paths LinkOption Path]
           [java.nio.file.attribute FileAttribute]
           [net.lingala.zip4j.core ZipFile]))

(def ^:private download-url "https://s3-us-west-2.amazonaws.com/dynamodb-local/dynamodb_local_latest.zip")

(def ^:private dynamo-directory (str (System/getProperty "user.home") File/separator ".clj-dynamodb-local"))

(def ^:private host {:name (System/getProperty "os.name")
                     :version (System/getProperty "os.version")
                     :arch (System/getProperty "os.arch")})

(defn- ->path
  "Create a path from the given strings."
  [str & strs]
  {:pre [(string? str) (every? string? strs)]}
  (Paths/get str (into-array String strs)))

(defn- path?
  "Is the given argument a path?"
  [x]
  (instance? Path x))

(defn- exists?
  "Does the given path exist?"
  [path]
  {:pre [(path? path)]}
  (Files/exists path (into-array LinkOption [])))

(defn- ensure-dynamo-directory
  "Make sure the directory that DynamoDB Local will be downloaded to
  exists."
  []
  (let [path (->path dynamo-directory)]
    (when-not (exists? path)
      (-> (Files/createDirectory path (make-array FileAttribute 0))
          (.toString)))))

(defn- build-dynamo-command
  "Build a java command to start DynamoDB Local with the required
  options."
  [config]
  (let [{:keys [port in-memory? shared-db? db-path jvm-opts]} config
        lib-path (str (io/file dynamo-directory "DynamoDBLocal_lib"))
        jar-path (str (io/file dynamo-directory "DynamoDBLocal.jar"))]
    (cond-> (format "java %s -Djava.library.path=%s -jar %s -port %s" (str/join " " jvm-opts) lib-path jar-path port)
            in-memory? (str " -inMemory")
            shared-db? (str " -sharedDb")
            (and (seq db-path) (not in-memory?)) (str " -dbPath " db-path))))

(defn start-dynamo
  "Start DynamoDB Local with the desired options."
  [config]
  (let [dynamo (->> (build-dynamo-command config)
                    (.exec (Runtime/getRuntime)))]
    (log/info "Started DynamoDB Local")
    dynamo))

(defn- download-dynamo
  "Download DynamoDB Local from Amazon."
  [url]
  (log/info "Downloading DynamoDB Local" {:dynamo-directory dynamo-directory})
  (ensure-dynamo-directory)
  (io/copy (io/input-stream (io/as-url url)) (io/as-file (str dynamo-directory "/" "dynamo.zip"))))

(defn- unpack-dynamo
  "Unzip a DynamoDB Local download."
  []
  (log/info "Unpacking DynamoDB Local")
  (let [zip-file (->path dynamo-directory "dynamo.zip")]
    (.extractAll (ZipFile. (str zip-file)) dynamo-directory)))


(defn- isM1Mac? []
  (and (= "Mac OS X" (:name host)) (= "aarch64" (:arch host))))

(defn ensure-installed
  "Download and unpack DynamoDB Local if it hasn't been already."
  []
  (when-not (exists? (->path dynamo-directory "dynamo.zip"))
    (download-dynamo download-url)
    (unpack-dynamo)))


(defn handle-shutdown
  "Kill the DynamoDB Local process on JVM shutdown."
  [dynamo-process]
  (doto dynamo-process (.destroy) (.waitFor))
  (log/info (str "Exited" {:exit-value (.exitValue dynamo-process)})))

(defn create-dynamo-db-logger
  [log]
  (fn [& message]
    (apply log "dynamodb-local:" message)))

(defn halt! [dyn]
  (when dyn
    (handle-shutdown dyn)))

(defmethod ig/init-key ::dynamo [_ config]
  (ensure-installed)
  (start-dynamo config))

(defmethod ig/halt-key! ::dynamo [_ dyn]
  (halt! dyn))