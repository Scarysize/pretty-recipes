(ns labels.tokenize)

(def token-expr #"[a-zA-Z0-9_À-ÿ\.\-\']+|\(|\)|,|\"")

(defn tokenize [phrase]
  (vec (re-seq token-expr phrase)))
