(ns server
  (:require [clojure.tools.logging :refer [info]]
            [clojure.tools.cli :refer [parse-opts]]
            [compojure.core :refer [GET POST routes]]
            [hiccup.page :refer [html5]]
            [labels.labelling :refer [label-ingredients]]
            [org.httpkit.server :refer [run-server]]
            [ring.logger :as logger]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :as reload]
            [ring.middleware.resource :refer [wrap-resource]]
            [scraper :refer [extract-recipe]]
            [views.index]
            [views.recipe]
            [views.error]))

(defn serve-html
  ([body] (serve-html body 200))
  ([body status]
   {:status status
    :headers {"Content-Type" "text/html"}
    :body body}))

(defn not-found [error-msg]
  (serve-html (html5 (views.error/html-tree error-msg 404))))

(defn enriched-recipe [recipe]
  (let [labelled-ingredients (label-ingredients (:ingredients recipe))]
    (assoc recipe :labelled-ingredients labelled-ingredients)))

(defn handle-recipe-query [recipe-url]
  (if-let [url recipe-url]
    (if-let [recipe (extract-recipe url)]
      (serve-html (html5 (views.recipe/html-tree (enriched-recipe recipe))))
      (not-found "Could not parse recipe"))
    (not-found "No recipe url supplied")))

(def router
  (routes
   (GET "/" [] (serve-html (html5 views.index/html-tree)))
   (GET "/recipe" [recipe-url] (handle-recipe-query recipe-url))
   (POST "/recipe" [recipe-url] (handle-recipe-query recipe-url))))

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
