(ns ingredients.label-server
  (:require
   [org.httpkit.server :refer [run-server]]
   [compojure.core :refer [routes GET POST]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
   [ring.logger :as logger]
   [clojure.java.jdbc :as j]))

(def db {:classname   "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname     "recipe-links.db"})

(defn empty-most-frequent-ingr []
  (first
   (j/query db
            ["SELECT
               *,
               COUNT(*) AS frequency
             FROM
               ingredients
             WHERE
               name IS NULL
             GROUP BY
               input
             ORDER BY frequency DESC
             LIMIT 1"])))

(defn serve-html [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(def all-routes
  (routes
   (GET "/" [] (serve-html "hi"))
   (GET "/ingredients.nextEmpty" [])))

(def handler
  (-> all-routes
      (logger/wrap-log-response)
      (wrap-resource "public-label")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-params)
      (wrap-json-body)
      (wrap-json-response)))

(defn -main [& _args]
  (run-server handler {:port 8888})
  (println "Started server"))
