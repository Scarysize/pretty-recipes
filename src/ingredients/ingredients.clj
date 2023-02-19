(ns ingredients.ingredients
  (:require [clojure.java.jdbc :as j]
            [hickory.core :as hickory]
            [parsers.serious-eats :refer [extract-ingredients]]))

;; input,name,qty,range_end,unit,comment

(def db {:classname   "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname     "recipe-links.db"})

#_(do
    (println "Recreating ingredients table from scratch")
    (j/db-do-commands db "CREATE TABLE IF NOT EXISTS ingredients(
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          input TEXT,
                          name TEXT,
                          qty DECIMAL,
                          range_end DECIMAL,
                          unit TEXT,
                          comment TEXT,
                          recipe_id INTEGER,
                          FOREIGN KEY(recipe_id) REFERENCES urls(id)
                        );"))
#_(j/delete! db :ingredients ["1 = 1"])

(defn parse-html [html-string]
  (-> html-string
      hickory/parse
      hickory/as-hickory))

(defn row-to-doc [row]
  (-> row
      (assoc :doc (parse-html (:html_content row)))
      (dissoc :html_content)))

(defn enrich-with-ingredients [doc]
  (-> doc
      (assoc :ingredients (extract-ingredients (:doc doc)))
      (dissoc :doc :url)))

(defn ingredient-rows [recipe-with-ingrs]
  (map (fn [ingr] {:recipe_id (:id recipe-with-ingrs) :input ingr})
       (:ingredients recipe-with-ingrs)))

(defn fetch-recipe-batch [offset]
  (j/query db
           ["SELECT
                          id,
                          html_content,
                          url
                        FROM
                          urls
                        WHERE
                          html_content IS NOT NULL
                        LIMIT 100 OFFSET ?"
            offset]))

(defn fetch-ingrs-batch [offset]
  (j/query db
           ["SELECT
               id,
               input
             FROM
               ingredients
             LIMIT 100 OFFSET ?"
            offset]))

(defn convert-batch [rows]
  (->> rows
       (map row-to-doc)
       (map enrich-with-ingredients)
       (filter #(seq (:ingredients %1)))))

(defn store-ingrs [ingrs]
  (j/insert-multi! db :ingredients ingrs))

(defn import-ingredients []
  (loop [offset 0]
    (prn "Fetching rows at offset:" offset)
    (let [batch (fetch-recipe-batch offset)
          recipe-ingrs (convert-batch batch)
          ingredient-rows (flatten (map ingredient-rows recipe-ingrs))]
      (store-ingrs ingredient-rows)
      (if (seq batch)
        (recur (+ offset (count batch)))
        (prn "Done, inserted ingredients" (+ offset (count batch)))))))

(defn normalize-ingredients [])

(defn -main [& _args])
