(ns views.ingredients
  (:require [labels.fractions :refer [to-fraction]]))

(defn frac [[nom denom]]
  [:span [:sup nom] "&frasl;" [:sub denom]])

(defn format-qty [qty]
  (let [{:keys [integer fractional]} (to-fraction qty)]
    (cond
      ;; both parts
      (and (seq fractional) (not (zero? integer))) [:span integer " " (frac fractional)]
      ;; only fractional
      (and (seq fractional) (zero? integer)) (frac fractional)
      ;; only integer
      (and (not (seq fractional)) (not (zero? integer))) [:span integer]
      :else qty)))

(defn format-quantities [txt]
  (if (= (:label txt) :qty)
    (update txt :text format-qty)
    txt))


(defn ingredient-list-item [ingredient]
  (let [qty (get-in ingredient [:qty :value])
        unit (get-in ingredient [:unit :value])
        label (->> (:label ingredient)
                   (mapv format-quantities))]
    [:li.ingredient

     [:div.ingredient__quantity
      (if (some? qty)
        (format-qty qty)
        "")]

     [:div.ingredient__label
      (if (some? unit)
        [:span {:data-label (:label unit)} (:text unit) " "]
        "")
      (for [txt label]
        (if (= " " (:text txt))
          " "
          [:span (if-let [txt-label (:label txt)] {:data-label txt-label} {}) (:text txt)]))]]))

(defn ingredient-list [recipe]
  (let [ingredients (:labelled-ingredients recipe)]
    [:ul#recipe-ingredients.ingredient-list
     (for [ingredient-tokens ingredients]
       (ingredient-list-item ingredient-tokens))]))
