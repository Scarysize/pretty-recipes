(ns labels.fractions-test
  (:require [clojure.test :refer :all]
            [labels.fractions :refer [replace-fractions to-fraction]]))

(deftest fractions-to-decimals
  (let [replaced (replace-fractions "1 1/2 tsp, 1/3 slices")]
    (is (= "1.5 tsp, 0.33 slices" replaced)))
  (let [pairs [["1" "1"]
               ["1 1/4" "1.25"]
               ["3/4" "0.75"]
               ["2/3" "0.67"]
               ["1/2" "0.5"]]]
    (doseq [[in out] pairs]
      (is (= out (replace-fractions in))))))

(deftest decimals-to-fractions
  (let [pairs [[1       {:integer 1 :fractional []}]
               [1.25    {:integer 1 :fractional [1 4]}]
               [0.75    {:integer 0 :fractional [3 4]}]
               [0.67    {:integer 0 :fractional [2 3]}]
               [0.33    {:integer 0 :fractional [1 3]}]
               [0.5     {:integer 0 :fractional [1 2]}]
               [0.12345 {:integer 0 :fractional [2469 20000]}]]]
    (doseq [[in out] pairs]
      (is (= out (to-fraction in))))))
