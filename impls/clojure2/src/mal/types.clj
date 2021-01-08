(ns mal.types
  (:require [clojure.test :refer [is]])
  (:gen-class))

(defn test-destructure [[type value & tail :as all]]
  (printf "type %s \n value %s \n tail %s \n all %s \n\n" type value tail all))

(test-destructure [])
(test-destructure [1])
(test-destructure [1 2])
(test-destructure [1 2 3])

(defn mal-type? [[type value & tail]]
  (cond
    (nil? type) false
    (nil? value) false
    (not (nil? tail)) false
    :else true))

(defn mal-inner
  "The caller of mal-inner must know the type before use the inner value"
  [[_type value :as all]]
  {:pre (mal-type? all)}
  value)

(defn mal-map [[type val :as mal-val] mapper]
  {:pre (mal-type? mal-val)}
  [type (mapper val)])

(defn make-mal-num [num]
  {:pre (number? num)}
  [:number num])

(defn mal-num? [[type val :as all]]
  (cond
    (not (mal-type? all)) false
    (not= type :number) false
    ;; Should we throw error?
    ;; It is a programmer mistake.
    (not (number? val)) false
    :else true))

(defn make-mal-num-biop [biop]
  (fn [a b]
    {:pre [(is (mal-num? a))
           (is (mal-num? b))]}
    (let [va (mal-inner a)
          vb (mal-inner b)]
      (make-mal-num (biop va vb)))))

(is (mal-num? [:number 3]))
(is (not (mal-num? [:number "3"])))

(defn make-mal-symbol [str]
  {:pre [(string? str)]}
  [:symbol str])

(defn mal-symbol? [[type val :as all]]
  (cond
    (not (mal-type? all)) false
    (not= type :symbol) false
    ;; Should we throw error?
    ;; It is a programmer mistake.
    (not (string? val)) false
    :else true))

(defn mal-symbol->str [[_type value :as symbol]]
  {:pre [(mal-symbol? symbol)]}
  value)

(defn make-mal-list [val]
  {:pre [(or (vector? val)
             (seq? val))]}
  [:list val])

(defn mal-list? [[type val :as all]]
  (cond
    (not (mal-type? all)) false
    (not= type :list) false
    ;; Should we throw error?
    ;; It is a programmer mistake.
    (not (or (vector? val)
             (seq? val)))
    false

    :else true))

(defn mal-list-map [mapper mal-list]
  {:pre [(is (mal-list? mal-list))]}
  (mal-map mal-list #(map mapper %)))

(defn mal-list-f-args [mal-list]
  {:pre (mal-list? mal-list)}
  (let [[_type inner-arr] mal-list
        [f & args] inner-arr]
    [f args]))


