(ns labels.clean-test
  (:require [clojure.test :refer :all]))

(defn check-window [win sub]
  (= win sub))

(defn filter-subseq [col sub]
  (let [reduced (reduce
                 (fn [{:keys [out win]} next]
                   (if (= (count sub) (count win))
                     (if (= win sub)
                       ;; window matches subsequence, fully discard
                       {:out out :win [next]}
                       ;; window doesn't match, move one element into output
                       {:out (conj out (first win)) :win (conj (vec (rest win)) next)})
                     {:out out :win (conj win next)}))

                 ;; initial accumulator
                 {:out [] :win []}

                 col)]
    (concat (:out reduced) (:win reduced))))

(deftest subseq-filter
  (is (= ["A","B","C","D","(","O","O",")"]
         (filter-subseq ["A","B","(","I","O",")","C","D","(","O","O", ")"] ["(","I","O",")"]))))
