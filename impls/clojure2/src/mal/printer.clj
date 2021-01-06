(ns mal.printer
  (:require [clojure.test :refer [is]]
            [clojure.string :as str])
  (:gen-class))

(defn mal-pr-str [[type value]]
  (cond
    (= type :number) (str value)
    (= type :symbol) value
    (= type :list)
    (str "("
         (str/join " " (map mal-pr-str value))
         ")")
    :else nil))

(is (= "3" (mal-pr-str [:number 3])))
(is (= "a" (mal-pr-str [:symbol "a"])))
(is (= "(3 as)" (mal-pr-str [:list [[:number 3] [:symbol "as"]]])))
(is (= "(3 as (1 2))" (mal-pr-str [:list [[:number 3]
                                          [:symbol "as"]
                                          [:list
                                           [[:number 1]
                                            [:number 2]]]]])))
(is (nil? (mal-pr-str nil)))

