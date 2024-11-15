# dynamo-embedded-clj

Embedded dynamo-local for clojure with support for apple M1 macs
# Note

Version compatibility guide:

- 3.x.x: Uses in-process server with Jetty 12 (required by DynamoDBLocal 2.5.x)
  - Best for projects already using Jetty 12
  
- 2.x.x: Uses in-process server with Jetty 9 (required by DynamoDBLocal 1.x.x) 
  - Broad compatibility with existing projects
  
- 1.x.x: Launches DynamoDBLocal jar in separate process
  - Maximum compatibility across all projects
  - Slightly longer startup time
  - Latest DynamoDBLocal version

```
org.clojars.bigsy/dynamo-embedded-clj {:mvn/version "1.1.8"}
org.clojars.bigsy/dynamo-embedded-clj {:mvn/version "2.0.2"}
org.clojars.bigsy/dynamo-embedded-clj {:mvn/version "3.0.0"} 
 
```

## Usage
If using Cognitect aws dynamodb client you may find these useful for type conversions https://github.com/Bigsy/clj-dynamo-helpers

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.bigsy/dynamo-embedded-clj.svg)](https://clojars.org/org.clojars.bigsy/dynamo-embedded-clj)

### Development:

```clojure
(require 'dynamo-embedded-clj.core)

;; Start a local dynamo with default port:
(init-dynamo)

;; another call will halt the previous system:
(init-dynamo)

;; When you're done:
(halt-dynamo!)
```

### Testing:

**NOTE**: these will halt running dynamo instances

config vector is optional - if you don't pass it default it is inMemory and port 8000 - you can pass an array which supports any switches supported by dynamo-local

```clojure
(require 'clojure.test)

(use-fixtures :once with-dynamo-fn) ;; no config defaults

(defn around-all
  [f]
  (with-dynamo-fn ["-inMemory" "-port" "8010"] f)) ;; custom config

(use-fixtures :once around-all)

;;; You can also wrap ad-hoc code in init/halt:
(with-dynamo default-config ;; default-config from lib core
	,,, :do-something ,,,)
```

### Other useful clojure wrapped embedded testing libs
* https://github.com/Bigsy/pg-embedded-clj
* https://github.com/Bigsy/redis-embedded-clj
* https://github.com/Bigsy/s3-clj
* https://github.com/Bigsy/elasticmq-clj
* https://github.com/Bigsy/sns-clj