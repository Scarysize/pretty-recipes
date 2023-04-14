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
      [:ol {:id "recipe-directions"}
       (for [d (:directions recipe)]
         [:li d])]]]

    [:p.recipe-source-text "&rarr; " [:a {:href (:source recipe) :id "recipe-source"} (remove-protocol (:source recipe))]]

    views.common/scripts
    views.common/footer]])
