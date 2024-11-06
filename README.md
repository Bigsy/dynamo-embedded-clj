# dynamo-embedded-clj

Embedded dynamo-local for clojure with support for apple M1 macs

## Usage

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

(use-fixtures :once with-dynamo-fn) ;; no config defaulkts

(defn around-all
  [f]
  (with-dynamo-fn ["-inMemory" "-port" "8010"] f)) ;; custom config

(use-fixtures :once around-all)

;;; You can also wrap ad-hoc code in init/halt:
(with-dynamo default-config ;; default-config from lib core
	,,, :do-something ,,,)
```