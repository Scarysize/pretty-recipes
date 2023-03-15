(ns structured-ingredients
  (:require [clj-http.client :as http-client]
            [clojure.data.json :as json]))

(def api-url "http://localhost:8081/ingredients")


(defn to-labeled-ingredient [{:keys [labels tokens]}]
  (map (fn [token label] {:text token :label label})
       tokens
       labels))

(defn match-all-labels [all-labels]
  (->> all-labels
       (map (fn [[labels tokens]] {:labels labels :tokens tokens}))
       (map to-labeled-ingredient)))

(defn fetch-structured-data [ingredient-phrases]
  (-> (http-client/post api-url {:headers {:content-type "application/json"}
                                 :body (json/write-str {:phrases ingredient-phrases})})
      :body
      (json/read-str :key-fn keyword)
      :labels
      match-all-labels))

