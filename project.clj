(defproject org.clojars.bigsy/dynamo-embedded-clj "2.0.5"
  :description "Embedded dynamodb for clojure"
  :url "https://github.com/Bigsy/dynamo-embedded-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [integrant "0.13.1"]
                 [org.clojure/tools.namespace "1.5.0"]
                 [com.amazonaws/DynamoDBLocal "1.25.1"]
                 [software.amazon.awssdk/url-connection-client "2.16.46"]]

  :profiles {:dev {:dependencies [[com.taoensso/faraday "1.12.3"]]}})