(ns parsers.blog-parsers.wp-recipe-maker
  (:require  [clojure.string :as str]
             [dom :refer [text-content]]
             [hickory.select :as s]))

(defn cleanup-item [s]
  (str/trim (str/replace s #"▢" "")))

(defn extract-title [doc]
  (text-content (first (s/select (s/class "wprm-recipe-name") doc))))

(defn extract-ingredients [doc]
  (if-let [container (first (s/select (s/class "wprm-recipe-ingredients-container") doc))]
    (->> (s/select (s/tag :li) container)
         (map text-content)
         (map cleanup-item))
    []))

(defn extract-directions [doc]
  (if-let [container (first (s/select (s/class "wprm-recipe-instructions-container") doc))]
    (->> (s/select (s/tag :li) container)
         (map text-content)
         (map cleanup-item))
    []))

(defn can-parse? [doc]
  (not-empty (extract-title doc)))

(defn extract-recipe [doc url]
  {:title (extract-title doc)
   :ingredients (extract-ingredients doc)
   :directions (extract-directions doc)
   :source url})

