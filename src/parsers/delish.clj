(ns parsers.delish
  (:require [clojure.string :as str]
            [dom :refer [text-content]]
            [hickory.select :as s]))

(defn ingredient-container [root]
  (first (s/select (s/class "ingredients-body") root)))

(defn ingredient-list [container]
  (first (s/select (s/tag :ul) container)))

(defn extract-title [doc]
  (->
   (text-content (first (s/select (s/tag :title) doc)))
   (str/replace "- Delish.com" "")))

(defn extract-ingredients [doc]
  (->> doc
       ingredient-container
       ingredient-list
       (s/select (s/tag :li))
       (map text-content)
       (map str/trim)
       vec))

(defn direction-list [doc]
  (let [outer-list (first (s/select (s/and
                                     (s/tag :ul)
                                     (s/class "directions"))
                                    doc))]
    (first (s/select (s/tag :ol) outer-list))))

(defn tap [arg]
  (prn arg)
  arg)

(defn strip-step [s]
  (prn s)
  (str/replace-first s #"Step\s\d{0,10}" ""))

(defn replace-nbsp [s]
  (str/replace s #"\u00A0" " "))


(defn extract-directions [doc]
  (->> doc
       direction-list
       (s/select (s/tag :li))
       (map text-content)
       (map str/trim)
       (map replace-nbsp)
       tap
       (map strip-step)
       vec))

(defn extract-recipe [doc url]
  {:title (extract-title doc)
   :ingredients (extract-ingredients doc)
   :directions (extract-directions doc)
   :source url})
