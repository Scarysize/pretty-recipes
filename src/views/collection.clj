(ns views.collection
  (:require [views.common]))

(def html-tree
  [:html {:lang "en"}
   [:head
    [:title "Your Collection - Pretty Recipes"]
    views.common/head]
   [:body
    views.common/header
    [:main {:class "collection"}
     [:h1 "Saved Recipes"]
     [:section {:id "recipe-collection"}]]
    views.common/scripts
    views.common/footer]])
