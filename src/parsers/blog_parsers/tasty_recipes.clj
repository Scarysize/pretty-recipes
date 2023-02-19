(ns parsers.blog-parsers.tasty-recipes
  (:require [dom :refer [text-content]]
            [hickory.select :as s]))

(defn extract-title [doc]
  (text-content (first (s/select (s/class "tasty-recipes-title") doc))))

(defn extract-ingredients [doc]
  (if-let [container (first (s/select (s/class "tasty-recipes-ingredients") doc))]
    (map text-content (s/select (s/tag :li) container))
    []))

(defn extract-directions [doc]
  (if-let [container (first (s/select (s/class "tasty-recipes-instructions") doc))]
    (map text-content (s/select (s/tag :li) container))
    []))

(defn can-parse? [doc]
  (not-empty (extract-title doc)))

(defn extract-recipe [doc url]
  {:title (extract-title doc)
   :ingredients (extract-ingredients doc)
   :directions (extract-directions doc)
   :source url})
