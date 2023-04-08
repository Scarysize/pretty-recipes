(ns views.ingredients
  (:require [labels.label-helper :refer [is-close-parens is-comma
                                         is-open-parens is-unit]]
            [labels.labelling :as l]
            [labels.unit :as u]))

(defn unit-name [unit]
  (case unit
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
    [(name unit)]))

(defn render-single [token]
  (let [token-str (case (:label token)
                    ::l/qty (:value token)
                    ::l/unit (unit-name (:value token))
                    ::l/other (:value token)
                    ::l/parens-open "("
                    ::l/parens-close ")"
                    "token")]
    (if (vector? token-str)
      token-str
      [token-str])))

(defn token-text [cur next]
  (cond
    (nil? next) (render-single cur)
    (is-open-parens cur) (render-single cur)
    (is-close-parens next) (render-single cur)
    (is-comma next) (render-single cur)
    (is-unit next) (render-single cur)
    :else (conj (render-single cur) :space)))

(defn labelled-ingredient [[cur next]]
  (if (some? cur)
    (for [txt (token-text cur next)]
      (if (= txt :space)
        [:span " "]
        [:span {:data-label (:label cur)} txt]))
    nil))

(defn ingredient-list [recipe]
  (if-let [ingredients (:labelled-ingredients recipe)]
    [:ul {:id "recipe-ingredients" :class "ingredient-list--labelled"}
     (for [ingredient-tokens ingredients]
       [:li (map labelled-ingredient (partition 2 1 nil ingredient-tokens))])]

    [:ul {:id "recipe-ingredients"}
     (for [i (:ingredients recipe)]
       [:li i])]))
