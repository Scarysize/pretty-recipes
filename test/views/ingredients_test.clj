(ns views.ingredients-test
  (:require [clojure.test :refer :all]
            [views.ingredients :refer [format-qty]]))

(deftest qty-formatting
  (is (= [:span 1 " " [:span [:sup 1] "&frasl;" [:sub 4]]]
         (format-qty 1.25)))
  (is (= [:span [:sup 1] "&frasl;" [:sub 2]]
         (format-qty 0.5)))
  (is (= [:span 3]
         (format-qty 3))))
