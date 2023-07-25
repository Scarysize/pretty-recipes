(ns labels.preprocess
  (:require [clojure.string :as str]))

(java.util.Locale/setDefault java.util.Locale/US)

(def qty-unit-split-expr #"(\d)([a-zA-Z])")

(defn split-qty-and-unit
  "Puts a space between quantity and unit in a phrase. For example: 
   '1g salt' will become '1 g salt'. The unit isn't a verified unit at that
   point."
  [phrase]
  (str/replace phrase qty-unit-split-expr "$1 $2"))
