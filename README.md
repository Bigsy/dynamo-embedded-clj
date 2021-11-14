# dynamo-embedded-clj

Embedded dynamo-local for clojure

## Usage

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.bigsy/dynamo-embedded-clj.svg)](https://clojars.org/org.clojars.bigsy/dynamo-embedded-clj)
### Development:

```clojure
(require 'dynamo-embedded-clj.core)

;; Start an embedded pg with default port:
(init-dynamo)

;; another call will halt the previous system:
(init-dynamo)

;; When you're done:
(halt-dynamo!)
```

### Testing:

**NOTE**: these will halt running pg-embedded instances

```clojure
(require 'clojure.test)

(use-fixtures :once with-dynamo-fn)

(defn around-all
  [f]
  (with-dynamo-fn (merge default-config
                           {:port 8000
                            :in-memory? true
                            :shared-db? true
                            :db-path "some/path"
                            :jvm-opts ["opt1" "opt2"]})
                    f))

(use-fixtures :once around-all)

;;; You can also wrap ad-hoc code in init/halt:
(with-dynamo default-config
	,,, :do-something ,,,)
```

