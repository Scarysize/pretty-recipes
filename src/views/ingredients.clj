(ns views.ingredients
  (:require [clojure.string :as str]))

(defn frac [num denom]
  [:span [:sup num] "&frasl;" [:sub denom]])

(defn format-qty [qty]
  (if (= 0 (mod qty 1))
    qty
    (let [whole-part (int qty)
          whole-part? (not= whole-part 0)
          fraction (case (mod qty 1)
                     0.125 (frac 1 8)
                     0.25  (frac 1 4)
                     0.33  (frac 1 3)
                     0.5 (frac 1 2)
                     0.75  (frac 3 4)
                     nil)]
      (cond
        (and whole-part? (some? fraction)) [:span whole-part " " fraction]
        (and (= whole-part 0) (some? fraction)) fraction
        :else qty))))

(defn format-quantities [txt]
  (if (= (:label txt) :qty)
    (update txt :text format-qty)
    txt))

(defn singularize [txt]
  (prn txt)
  (if (= (:label txt) :unit)
    (update txt :text #(str/replace %1 #"s$" ""))
    txt))

(defn singularize-unit [qty-singular? label]
  (if (and qty-singular? (> (count label) 0))
    (update label 0 singularize)
    label))

(defn ingredient-list-item [ingredient]
  (let [qty (get-in ingredient [:qty :value])
        singular? (get-in ingredient [:qty :singular?])
        label (->> (:label ingredient)
                   (mapv format-quantities)
                   (singularize-unit singular?))]
    [:li.ingredient
     [:div.ingredient__quantity
      (if (some? qty)
        (format-qty qty)
        "")]
     [:div.ingredient__label
      (for [txt label]
        (if (= " " (:text txt))
          " "
          [:span (if-let [txt-label (:label txt)] {:data-label txt-label} {}) (:text txt)]))]]))

(defn ingredient-list [recipe]
  (let [ingredients (:labelled-ingredients recipe)]
    [:ul#recipe-ingredients.ingredient-list
     (for [ingredient-tokens ingredients]
       (ingredient-list-item ingredient-tokens))]))
