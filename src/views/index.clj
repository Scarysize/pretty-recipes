(ns views.index
  (:require [views.common]))

(def html-tree
  [:html {:lang "en"}
   [:head
    views.common/head
    [:title "Pretty Recipes - Clean up your favorite recipes"]
    [:link {:href "/home.css" :rel "stylesheet"}]]
   [:body
    [:main
     [:h1 "Pretty Recipes"]
     [:form {:method "get" :action "/recipe" :onsubmit "submitRecipe(event)"}
      [:input {:type "url" :name "recipe-url" :placeholder "https://www.seriouseats.com/hoppin-john" :required "true"}]
      [:input {:type "submit" :value "Make it pretty" :onsubmit "submitRecipe(event)"}]]
     [:p {:style "text-align: center;"} "Paste a link to a recipe to get a cleaned up version."]
     [:section.supported-sites
      [:p "Supported sites"]
      [:ul
       [:li [:a {:href "https://www.kingarthurbaking.com"} "King Arthur Baking"]]
       [:li [:a {:href "https://www.seriouseats.com"} "Serious Eats"]]
       [:li [:a {:href "https://www.delish.com"} "Delish"]]
       [:li "Blogs using " [:a {:href "https://www.wptasty.com/tasty-recipes"} "WP Tasty"]]
       [:li "Blogs using " [:a {:href "https://wordpress.org/plugins/wp-recipe-maker/"} "WP Recipe Maker"]]]]]
    views.common/scripts
    views.common/footer]])
