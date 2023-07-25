(ns labels.view-model
  (:require [labels.label-helpers :refer :all]
            [labels.unit :as u]
            [clojure.string :as str]))

(defn remove-first [pred coll]
  (let [[before non-pred-and-after] (split-with (complement pred) coll)]
    (concat before (rest non-pred-and-after))))

(defn find-first [pred extract coll]
  (->> coll
       (filter pred)
       (map extract)
       first))

(defn qty-singular? [qty]
  (and (some? qty) (<= qty 1)))

(defn singularize [txt]
  (str/replace txt #"s$" ""))

(defn unit-name
  ([unit] (unit-name unit false))
  ([unit singular?]
   (->> (case unit
          ::u/cup [:space "cups"]
          ::u/deg-f ["°F"]
          ::u/gram ["g"]
          ::u/l [:space "litres"]
          ::u/lb [:space "pounds"]
          ::u/ml ["ml"]
          ::u/oz [:space "ounces"]
          ::u/qt [:space "quarts"]
          ::u/tbsp [:space "tablespoons"]
          ::u/tsp [:space "teaspoons"]
          ::u/slice [:space "slices"]
          [(name unit)])
        (mapv #(cond
                 (= %1 :space) {:text " "}
                 singular? {:text (singularize %1) :label :unit}
                 :else {:text %1 :label :unit})))))

(defn render [token]
  (case (:label token)
    :qty          [{:text (qty token) :label :qty}]
    :unit         (unit-name (unit token))
    :other        [{:text (:value token)}]
    :parens-open  [{:text "("}]
    :parens-close [{:text ")"}]
    []))

(defn token-text [[cur next]]
  (if (or (nil? next)
          (open-parens? cur)
          (close-parens? next)
          (comma? next)
          (unit? next))
    (render cur)
    (conj (render cur) {:text " "})))

(defn build-label [ingredient-tokens]
  (->> ingredient-tokens
       (remove-first qty?)
       (remove-first unit?)
       (partition 2 1 nil)
       (mapv token-text)
       flatten
       (drop-while #(= (:text %1) " "))
       vec))

(defn to-view-model [ingredient-tokens idx]
  (let [qty (find-first qty? qty ingredient-tokens)
        unit (find-first unit? unit ingredient-tokens)
        ingredient-label (build-label ingredient-tokens)]
    {:qty {:value qty}
     :unit (if (some? unit)
             {:value (->> (unit-name unit (qty-singular? qty))
                          (filter unit?)
                          first)}
             nil)
     :label ingredient-label
     :index idx}))
