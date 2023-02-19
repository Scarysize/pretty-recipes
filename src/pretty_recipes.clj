(ns pretty-recipes
  (:gen-class)
  (:require [clojure.string :as str]
            [hickory.core :as hickory]
            [clj-http.client :as http-client]
            [clojure.tools.logging :refer [info]]
            [parsers.kingarthurbaking :as kingarthurbaking]
            [parsers.serious-eats :as serious-eats]
            [parsers.blog-parsers.tasty-recipes :as tasty-recipes]
            [parsers.blog-parsers.wp-recipe-maker :as wp-recipe-maker]
            [views.recipe]))

(defn download-doc [url]
  (info (str "Downloaing: " url))
  (-> (http-client/get url)
      (get :body)
      str/trim
      hickory/parse
      hickory/as-hickory))

(def parser-lookup
  {"www.seriouseats.com" serious-eats/extract-recipe
   "www.kingarthurbaking.com" kingarthurbaking/extract-recipe})

(def blog-parsers [{:can-parse? tasty-recipes/can-parse?
                    :parse-fn tasty-recipes/extract-recipe
                    :name "tasty-recipes"}
                   {:can-parse? wp-recipe-maker/can-parse?
                    :parse-fn wp-recipe-maker/extract-recipe
                    :name "wp-recipe-maker"}])

(defn find-blog-parser [doc]
  (->> blog-parsers
       (filter #((:can-parse? %1) doc))
       first))

(defn blog-parser [doc url]
  (if-let [parser (find-blog-parser doc)]
    (do
      (println (str "Chose a blog parser: " (:name parser)))
      ((:parse-fn parser) doc url))
    nil))

(defn parser-for-url [url]
  (let [jurl (new java.net.URI url)
        host (. jurl getHost)]
    (get parser-lookup host blog-parser)))

(defn extract-recipe [url]
  (if-let [parse-fn (parser-for-url url)]
    (parse-fn (download-doc url) url)
    nil))
