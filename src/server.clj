(ns server
  (:require [clojure.tools.logging :refer [info]]
            [clojure.tools.cli :refer [parse-opts]]
            [compojure.core :refer [GET POST routes]]
            [hiccup.page :refer [html5]]
            [labels.labelling :refer [label-ingredients]]
            [labels.rules.rules :refer [apply-rules]]
            [org.httpkit.server :refer [run-server]]
            [ring.logger :as logger]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :as reload]
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

(defn handle-recipe-query [recipe-url]
  (if-let [url recipe-url]
    (if-let [recipe (extract-recipe url)]
      (serve-html (html5 (views.recipe/html-tree (enriched-recipe recipe))))
      (not-found "Could not parse recipe"))
    (not-found "No recipe url supplied")))

(def router
  (routes
   (GET "/" [] (serve-html (html5 views.index/html-tree)))
   (GET  "/recipe/:slug" [slug] (serve-html (html5 (views.recipe/html-tree (assoc empty-recipe :slug slug)))))
   (GET "/recipe" [recipe-url] (handle-recipe-query recipe-url))
   (POST "/recipe" [recipe-url] (handle-recipe-query recipe-url))
   (GET "/collection" [] (serve-html (html5 views.collection/html-tree)))))

(def request-handler
  (-> router
      (logger/wrap-log-response)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-params)
      (reload/wrap-reload)))

(def port 8888)

(def cli-options
  [["-e" "--env ENVIRONMENT" "The environment, prod or dev." :default "prod"]])

(defn environment [env-option]
  (case env-option
    "prod" :prod
    "dev" :dev
    :prod))

(defn -main [& args]
  (let [env-option (get-in (parse-opts args cli-options) [:options :env])
        env (environment env-option)
        handler (if (= env :dev) (reload/wrap-reload request-handler) request-handler)]
    (run-server handler {:port port})
    (info (str "Started server on port: " port))
    (info (str "Environment is: " env))))
