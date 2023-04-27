(ns labels.preprocess-test
  (:require [clojure.test :refer :all]
            [labels.preprocess :as prep]))

(deftest fractions
  (let [replaced (prep/replace-fractions "1 1/2 tsp, 1/3 slices")]
    (is (= replaced "1.5 tsp, 0.33 slices"))))
