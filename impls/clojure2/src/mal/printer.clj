(ns mal.printer
  (:require [clojure.test :refer [is]]
            [clojure.string :as str])
  (:gen-class))

(defn mal-pr-str [exp]
  (cond
    (number? exp) (str exp)
    (string? exp) exp

    (vector? exp)
    (str "("
         (str/join " " (map mal-pr-str exp))
         ")")

    :else nil))

(is (= "3" (mal-pr-str 3)))
(is (= "3" (mal-pr-str "3")))
(is (= "(3 as)" (mal-pr-str [3 "as"])))
(is (= "(3 as (1 2))" (mal-pr-str [3 "as" [1 2]])))
(is (nil? (mal-pr-str nil)))

