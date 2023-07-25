(ns labels.view-model-test
  (:require [clojure.test :refer :all]
            [labels.view-model :refer [find-first]]))

(deftest find-first-occurrence
  (is (= 3 (find-first #(= %1 3) identity [1 2 3 4 5])))
  (is (= nil (find-first #(= %1 10) identity [1 2 3 4 5]))))
