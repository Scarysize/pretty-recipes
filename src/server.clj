(ns server
  (:require [clojure.tools.logging :refer [info]]
            [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [routes GET POST]]
            [hiccup.page :refer [html5]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [not-found]]
            [ring.logger :as logger]

            [scraper :refer [extract-recipe]]
            [structured-ingredients :refer [fetch-structured-data]]
            [views.index]
            [views.recipe]
            [views.collection]))

(defn serve-html [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(def empty-recipe {:title "" :source "" :ingredients [] :directions []})

(defn enriched-recipe [recipe]
  (let [labelled-ingredients (fetch-structured-data (:ingredients recipe))]
    (assoc recipe :labelled-ingredients labelled-ingredients)))

(def all-routes
  (routes
   (GET "/" [] (serve-html (html5 views.index/html-tree)))
   (GET  "/recipe/:slug" [slug] (serve-html (html5 (views.recipe/html-tree (assoc empty-recipe :slug slug)))))
   (POST "/recipe" [recipe-url] (if-let [url recipe-url]
                                  ;; Guard against an empty recipe url
                                  (if-let [recipe (extract-recipe url)]
                                    (serve-html (html5 (views.recipe/html-tree (enriched-recipe recipe))))
                                    (not-found "Could not parse recipe"))
                                  (not-found "No recipe url supplied")))
   (GET "/collection" [] (serve-html (html5 views.collection/html-tree)))))

(def handler
  (-> all-routes
      (logger/wrap-log-response)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-params)))

(def port 8888)

(defn -main [& _args]
  (run-server handler {:port port})
  (info (str "Started server on port: " port)))
