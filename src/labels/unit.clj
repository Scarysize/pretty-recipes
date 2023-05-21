(ns labels.unit
  (:require [clojure.string :as str]))

(defn try-unit [token]
  (case (str/lower-case token)

    "cup" ::cup
    "cups" ::cup
    "c." ::cup

    "g" ::gram
    "gr" ::gram
    "gram" ::gram
    "grams" ::gram

    "kg" ::kilogram
    "kilo" ::kilogram

    "teaspoons" ::tsp
    "teaspoon" ::tsp
    "tsp" ::tsp
    "tsp." ::tsp

    "tablespoons" ::tbsp
    "tablespoon" ::tbsp
    "tbsp" ::tbsp
    "tbsp." ::tbsp

    "pounds" ::lb
    "pound" ::lb
    "lbs" ::lb
    "lb" ::lb

    "ounces" ::oz
    "ounce" ::oz
    "oz" ::oz

    "quarts" ::qt
    "quart" ::qt
    "qts" ::qt
    "qt" ::qt

    "gallon" ::gal
    "gallons" ::gal
    "gal" ::gal

    "pint" ::pint
    "pints" ::pint
    "pt" ::pint

    "ml" ::ml
    "millilitre" ::ml
    "millilitres" ::ml

    "l" ::l
    "litre" ::l
    "litres" ::l

    "f" ::deg-f

    "slices" ::slices

    nil))

(defn unit-type [unit]
  (case unit
    ::lb       ::weight
    ::gram     ::weight
    ::kilogram ::weight

    ::cup   ::volume
    ::tsp   ::volume
    ::tbsp  ::volume
    ::oz    ::volume
    ::qt    ::volume
    ::gal   ::volume
    ::pint  ::volume
    ::ml    ::volume
    ::l     ::volume

    ::deg-f ::temperature))
