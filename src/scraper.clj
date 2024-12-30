(ns scraper
  (:gen-class)
  (:require [clj-http.client :as http-client]
            [clojure.string :as str]
            [clojure.tools.logging :refer [info]]
            [hickory.core :as hickory]
            [views.recipe]

            [parsers.blog-parsers.tasty-recipes :as tasty-recipes]
            [parsers.blog-parsers.wp-recipe-maker :as wp-recipe-maker]
            [parsers.delish :as delish]
            [parsers.kingarthurbaking :as kingarthurbaking]
            [parsers.recept-se :as recept-se]
            [parsers.serious-eats :as serious-eats]))

(defn download-doc [url]
  (info (str "Downloading: " url))
  (-> (http-client/get url)
      (get :body)
      str/trim
      hickory/parse
      hickory/as-hickory))

(def parser-host-mapping
  {"www.seriouseats.com" serious-eats/extract-recipe
   "www.kingarthurbaking.com" kingarthurbaking/extract-recipe
   "www.delish.com" delish/extract-recipe
   "recept.se" recept-se/extract-recipe})

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
      (info (str "Using a blog parser: " (:name parser) "for url: " url))
      ((:parse-fn parser) doc url))
    (do
      (info (str "Unable to find a blog parser"))
      nil)))

(defn parser-for-url [url]
  (let [jurl (new java.net.URI url)
        host (. jurl getHost)]
    (get parser-host-mapping host blog-parser)))

(defn extract-recipe [url]
  (if-let [parse-fn (parser-for-url url)]
    (if-let [recipe (parse-fn (download-doc url) url)]
      {:title (:title recipe)
       :ingredients (:ingredients recipe)
       :directions (:directions recipe)
       :source (:source recipe)}
      nil)
    nil))
