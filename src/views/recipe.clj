(ns views.recipe
  (:require [clojure.string :as str]
            [views.common]
            [views.ingredients :refer [ingredient-list]]))

(def protocol-pattern #"(http|https):\/\/")

(defn remove-protocol [url]
  (str/replace-first url protocol-pattern ""))


(defn html-tree [recipe]
  [:html {:lang "en"}
   [:head
    [:title (:title recipe)]
    views.common/head]
   [:body
    views.common/header
    [:main {:class "recipe"}
     [:h1 {:id "recipe-title"} (:title recipe)]

     [:div.ingredients
      [:h2 "Ingredients"]
      (ingredient-list recipe)]

     [:div.directions
      [:h2 "Directions"]
      [:ul#recipe-directions.direction-list
       (for [[index, step] (map-indexed vector (:directions recipe))]
         [:li.direction
          [:div.direction__index (format "%02d" (inc index))]
          [:div.direction__text step]])]]

     [:p.recipe-source-text "&rarr; " [:a {:href (:source recipe) :id "recipe-source"} (remove-protocol (:source recipe))]]

     views.common/scripts]
    views.common/footer]])
