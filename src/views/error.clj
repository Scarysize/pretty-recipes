(ns views.error
  (:require [views.common]))

(defn html-tree [message status]
  [:html {:lang "en"}
   [:head
    views.common/head
    [:title "Pretty Recipes - Error"]]
   [:body
    views.common/header
    [:main
     [:h1 "Oops - " status]
     [:p message]]
    views.common/scripts
    views.common/footer]])
