(ns views.common)

(def head
  (seq
   [[:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width,initial-scale=1.0"}]
    [:meta {:name "description" :content "Pretty Recipes is a helper to clean-up online recipes. Paste a link to enjoy."}]

    [:link {:rel "shortcut icon" :type "image/png" :href "/chef.png" :sizes "256x256"}]
    [:link {:rel "apple-touch-icon" :type "image/png" :href "/chef.png" :sizes "256x256"}]
    [:link {:href "/writ.min.css" :rel "stylesheet"}]
    [:link {:href "/index.css" :rel "stylesheet"}]]))

(def header
  [:header
   [:nav [:a {:href "/"} "Pretty Recipes"]]])

(def footer
  [:footer
   [:a {:href "/imprint.html" :style "color: #222"} "Imprint"]])

(def scripts
  (seq [#_[:script {:src "/recipe.js"}]
        [:script {:src "//gc.zgo.at/count.js" :async "true" :data-goatcounter "https://pretty-recipes.goatcounter.com/count"}]]))
