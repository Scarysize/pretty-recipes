(ns labels.tokenize)

(def token-expr #"[\w\.\-\']+|\(|\)|,|\"")

(defn tokenize [phrase]
  (vec (re-seq token-expr phrase)))
