(defproject org.clojars.bigsy/dynamo-embedded-clj "0.1.6"
  :description "Embedded dynamodb for clojure"
  :url "https://github.com/Bigsy/dynamo-embedded-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [integrant "0.13.1"]
                 [com.amazonaws/DynamoDBLocal "2.5.3"]
                 [org.clojure/tools.namespace "1.5.0"]]

  :profiles {:dev {:dependencies [[com.taoensso/faraday "1.12.3"]]}})