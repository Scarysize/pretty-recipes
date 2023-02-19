(ns parsers.kingarthurbaking
  (:require [clojure.string :as str]
            [dom :refer [text-content]]
            [hickory.select :as s]))

(defn extract-title [doc]
  (text-content (first (s/select (s/tag :title) doc))))

(defn extract-ingredients [doc]
  (->> (first (s/select (s/class "ingredients-list") doc))
       (s/select (s/tag :ul))
       (map #(s/select (s/tag :li) %1))
       flatten
       (map text-content)
       (map str/trim)))

(defn extract-directions [doc]
  (->> (first (s/select (s/class "recipe__instructions") doc))
       (s/select (s/tag :ol))
       first
       (s/select (s/tag :li))
       (map text-content)
       (map str/trim)))

(defn extract-recipe [doc url]
  {:title (extract-title doc)
   :ingredients (extract-ingredients doc)
   :directions (extract-directions doc)
   :source url})
