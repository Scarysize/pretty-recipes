(ns dom)

(def text-ignore-list #{:figure :aside})

(defn text-content [{content :content}]
  (->> content
       (filter #(not (contains? text-ignore-list (:tag %1))))
       (reduce #(if (string? %2)
                  (str %1 %2)
                  (str %1 (text-content %2)))
               "")))
