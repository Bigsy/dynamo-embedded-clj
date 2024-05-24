(ns dynamo-embedded-clj.cognitect-helpers
  (:import (java.nio ByteBuffer)))

(defn map->attributevalue [m]
  (into {}
        (for [[k v] m]
          [(keyword k)
           (cond
             (map? v) {:M (map->attributevalue v)}
             (vector? v) {:L (mapv #(if (map? %) {:M (map->attributevalue %)} %) v)}
             (integer? v) {:N (str v)}
             (float? v) {:N (str v)}
             (boolean? v) {:BOOL v}
             (nil? v) {:NULL true}
             (bytes? v) {:B v}
             (instance? ByteBuffer v) {:B v}
             (and (set? v) (every? string? v)) {:SS (vec v)}
             (and (set? v) (every? number? v)) {:NS (vec (map str v))}
             :else {:S (str v)})])))

(defn dynamo->clojure [m]
  (into {} (for [[k v] m]
             (let [[type value] (first v)]
               [k (case type
                    :S value
                    :N (Integer/parseInt value)
                    :BOOL value
                    :NULL nil
                    :B value
                    :SS value
                    :NS (mapv #(Integer/parseInt %) value)
                    :BS value
                    :L (mapv #(dynamo->clojure (val (first %))) value)
                    :M (into {} (for [[mk mv] value]
                                  [mk (dynamo->clojure mv)]))
                    value)]))))