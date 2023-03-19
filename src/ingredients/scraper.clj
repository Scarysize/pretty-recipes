(ns ingredients.scraper
  (:require [clojure.java.jdbc :as j]
            [clojure.string :as str]
            [hickory.core :as hickory]
            [hickory.render :refer [hickory-to-html]]
            [hickory.select :as s]
            [scraper :refer [download-doc]]
            [java-time.api :as jt]))

(def db {:classname   "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname     "recipe-links.db"})

(def empty-doc
  (-> "<html></html>"
      hickory/parse
      hickory/as-hickory))

(defn try-download-doc [url]
  (try
    (download-doc url)
    (catch Exception _
      (println (str "Error when scraping: " url))
      empty-doc)))

(defn get-next-url []
  (first
   (j/query db
            ["SELECT
               id, url
             FROM
               urls
             WHERE scraped_at IS NULL AND scrape_start_at IS NULL
             LIMIT 1"])))

(defn lock-url [id]
  (j/update! db :urls
             {:scrape_start_at (jt/format :iso-instant (jt/instant))}
             ["id = ?" id]))

(defn store-doc [id url doc]
  (println (str "Storing recipe: " url))
  (let [html (hickory-to-html doc)
        timestamp (jt/format :iso-instant (jt/instant))]
    (j/update! db :urls
               {:scraped_at timestamp :html_content html}
               ["id = ?" id])))
(defn mark-scraped [id]
  (j/update! db :urls
             {:scraped_at (jt/format :iso-instant (jt/instant))}
             ["id = ?" id]))

(defn store-new-urls [urls]
  (if (seq urls)
    (j/insert-multi! db :urls [:url] (map #(conj [] %1) urls))
    0))

(defn recipe-page? [doc]
  (seq (s/select (s/and (s/tag :button) (s/class "recipe-jump-button")) doc)))

(defn extract-urls [doc]
  (->> (s/select (s/tag :a) doc)
       (map #(get-in %1 [:attrs :href]))
       (filter #(and
                 (not (nil? %1))
                 (or (str/starts-with? %1 "https://www.seriouseats.com")
                     (str/starts-with? %1 "www.seriouseats.com"))))
       (map #(str/trim %1))))

;; Run this to open an url up for scraping
#_(j/update! db :urls {:scraped_at nil :html_content nil} ["id = 1"])

#_(do
    (println "Recreating urls table from scratch")
    (j/db-do-commands db (j/drop-table-ddl :urls))
    (j/db-do-commands db "CREATE TABLE IF NOT EXISTS urls(
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          url TEXT,
                          scraped_at TEXT,
                          scrape_start_at TEXT,
                          html_content TEXT,
                          UNIQUE(url) ON CONFLICT IGNORE
                        );")
    (j/insert! db :urls [:url] ["https://www.seriouseats.com"]))

(defn -main [& _args]
  (println "Starting scraper loop")
  (loop [next (get-next-url)]
    (if (nil? next)
      (println "No more urls to scrape.")
      (do
        (println (str "Scraping: " (:url next)))
        (let [id (:id next)
              url (:url next)
              _ (lock-url id)
              html-doc (try-download-doc url)
              urls (extract-urls html-doc)]
          (store-new-urls urls)
          (if (recipe-page? html-doc)
            (store-doc id url html-doc)
            (mark-scraped id)))
        (recur (get-next-url))))))
