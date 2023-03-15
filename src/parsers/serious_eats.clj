(ns parsers.serious-eats
  (:require [clojure.string :as str]
            [dom :refer [text-content]]
            [hickory.select :as s]))


(defn ingredients-container [root]
  (or
   (first (s/select (s/class "structured-ingredients") root))
   (first (s/select (s/class "section--ingredients") root))))

(defn ingredients-list [container]
  (first (s/select (s/tag :ul) container)))

(defn ingredient-list-items [list]
  (s/select (s/tag :li) list))

(defn ingredient-items [items]
  (->> items
       (map text-content)
       (map str/trim)
       (vec)))

(defn extract-ingredients [doc]
  (-> doc
      ingredients-container
      ingredients-list
      ingredient-list-items
      ingredient-items))

(defn directions-container [root]
  (first (s/select (s/class "structured-project__steps") root)))

(defn directions-list [container]
  (first (s/select (s/tag :ol) container)))

(defn direction-list-items [list]
  (s/select (s/tag :li) list))

(defn direction-items [items]
  (->> items
       (map text-content)
       (map str/trim)
       (vec)))

(defn extract-directions [doc]
  (-> doc
      directions-container
      directions-list
      direction-list-items
      direction-items))


(defn extract-title [doc]
  (text-content (first (s/select (s/tag :title) doc))))

(defn extract-recipe [doc url]
  {:title (extract-title doc)
   :ingredients (extract-ingredients doc)
   :directions (extract-directions doc)
   :source url})
