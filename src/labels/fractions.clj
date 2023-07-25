(ns labels.fractions
  (:require [clojure.string :as str])
  (:import [java.text DecimalFormat DecimalFormatSymbols]
           [java.util Locale]))

(def double-format (new DecimalFormat "0.##" (new DecimalFormatSymbols (Locale/US))))

(defn- to-decimal
  "Parses a fraction string into a double. Handles fractions made up of an
   integer and a fraction part. For example: '1 1/4' becomes '1.25' "
  [fraction-str]
  (->> (str/split (str/triml fraction-str) #" ")
       (map (fn [t] (map #(Integer/parseInt %1) (str/split t #"/"))))
       (map #(reduce / %1))
       (reduce + 0)
       double))

(defn- replace-with-decimal [[fraction-string]]
  (let [fraction (to-decimal fraction-string)
        formatted (.format double-format fraction)]
    (if (str/starts-with? fraction-string " ")
      (str " " formatted)
      formatted)))

(defn replace-fractions
  "Replaces fractions with decimals. For example: '1 1/2 teaspoons' will be
   replaced with '1.5 teaspoons'."
  [phrase]
  (str/replace phrase
               #"(\d*\s*\d/\d+)"
               replace-with-decimal))


(defn to-fraction
  "Parses a decimal number into an fraction object."
  [decimal]
  {:integer (int decimal)
   :fractional (case (mod decimal 1)
                 0     []
                 0.125 [1 8]
                 0.25  [1 4]
                 0.33  [1 3]
                 0.67  [2 3]
                 0.5   [1 2]
                 0.75  [3 4]
                 (let [ratio (rationalize (mod decimal 1))]
                   [(numerator ratio) (denominator ratio)]))})
