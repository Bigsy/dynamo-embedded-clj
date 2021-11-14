(defproject org.clojars.bigsy/dynamo-embedded-clj "0.1.0"
  :description "Embedded dynamodb for clojure"
  :url "https://github.com/Bigsy/dynamo-embedded-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [integrant "0.8.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/tools.namespace "1.1.0"]
                 [org.slf4j/slf4j-jdk14 "1.7.30"]
                 [net.lingala.zip4j/zip4j "1.3.2"]
                 [http-kit "2.5.3"]]

  :profiles {:dev {:dependencies [[com.taoensso/faraday "1.11.2"]]}})
