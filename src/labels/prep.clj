(ns labels.prep
  (:require [clojure.string :as str]))

(java.util.Locale/setDefault java.util.Locale/US)

(defn parse-fraction
  "Parses a fraction string into a double. Handles fractions made up of an
   integer and a fraction part. For example: '1 1/4' become '1.25' "
  [fraction-str]
  (->> (str/split (str/triml fraction-str) #" ")
       (map (fn [t] (map #(Integer/parseInt %1) (str/split t #"/"))))
       (map #(reduce / 1 %1))
       (reduce + 0)
       double))

(defn replace-fraction [[match]]
  (let [fraction (parse-fraction match)
        space (if (str/starts-with? match " ") " " "")]
    (str space (format "%.2g" fraction))))

(def fraction-part-expr #"(\d*\s*\d/\d+)")

(defn replace-fractions
  "Replaces fractions with decimals. For example: '1 1/2 teaspoons' will be
   replaced with '1.5 teaspoons'."
  [phrase]
  (str/replace phrase fraction-part-expr replace-fraction))

(def qty-unit-split-expr #"(\d)([a-zA-Z])")

(defn split-qty-and-unit
  "Puts a space between quantity and unit in a phrase. For example: 
   '1g salt' will become '1 g salt'. The unit isn't a verified unit at that
   point."
  [phrase]
  (str/replace phrase qty-unit-split-expr "$1 $2"))
