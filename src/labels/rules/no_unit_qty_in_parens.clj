(ns labels.rules.no-unit-qty-in-parens
  (:require [labels.label-helper :refer [is-unit is-vol-or-weight labels unit]]
            [labels.labelling :as l]))

(def parens-pattern [::l/parens-open ::l/qty ::l/unit ::l/parens-close])

(defn is-label-pattern [ingredients]
  (and (= (labels ingredients) parens-pattern)
       (is-vol-or-weight (unit (nth ingredients 2)))))

(defn has-multiple-units [ingredients]
  (> (count (filter is-unit ingredients)) 1))

(defn drop-unit-qty-in-parens [ingredients]
  (let [filtered (reduce

                  (fn [{:keys [out win]} next]
                    (cond
                      (is-label-pattern win)
                      ;; window matches pattern, discard it
                      {:out out :win [next]}

                      (= (count win) (count parens-pattern))
                      ;; window doesn't match, move one item from window to output
                      {:out (conj out (first win)) :win (conj (vec (rest win)) next)}

                      :else
                      ;; fill up the window
                      {:out out :win (conj win next)}))

                  {:out [] :win []}

                  ingredients)
        out (:out filtered)
        win (:win filtered)]
    (if (is-label-pattern win)
      out
      (concat out win))))

(def rule-no-unit-qty-in-parens
  {:condition has-multiple-units
   :rule drop-unit-qty-in-parens})
