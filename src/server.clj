(ns server
  (:require [clojure.tools.logging :refer [info]]
            [compojure.core :refer [GET POST routes]]
            [hiccup.page :refer [html5]]
            [labels.labelling :refer [label-ingredients]]
            [labels.rules.rules :refer [apply-rules]]
            [org.httpkit.server :refer [run-server]]
            [ring.logger :as logger]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [not-found]]
            [scraper :refer [extract-recipe]]
            [views.collection]
            [views.index]
            [views.recipe]))

(defn serve-html [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(def empty-recipe {:title "" :source "" :ingredients [] :directions []})

(defn enriched-recipe [recipe]
  (let [labelled-ingredients (label-ingredients (:ingredients recipe))
        cleaned (apply-rules labelled-ingredients)]
    (assoc recipe :labelled-ingredients cleaned)))

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
