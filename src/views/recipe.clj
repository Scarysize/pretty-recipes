(ns views.recipe
  (:require [views.common]
            [clojure.string :as str]))

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
     [:p {:class "recipe-source-text"} "From " [:a {:href (:source recipe) :id "recipe-source"} (remove-protocol (:source recipe))]]

     [:div.heading-with-button
      [:h2 "Ingredients"]
      [:button {:class "save-btn" :onclick "saveRecipe(this)"} "Save"]]

     [:ul {:id "recipe-ingredients"}
      (for [i (:ingredients recipe)]
        [:li i])]

     [:h2 "Directions"]
     [:ol {:id "recipe-directions"}
      (for [d (:directions recipe)]
        [:li d])]]

    (if-let [slug (:slug recipe)]
      [:div {:data-slug slug :style "display: none;"}]
      nil)
    views.common/scripts
    views.common/footer]])