(ns mal.step2
  (:import (java.io StringReader
                    BufferedReader
                    StringWriter))
  (:require [mal.reader :as reader]
            [mal.printer :as printer]
            [clojure.test :refer [deftest is]]
            [failjure.core :as f])
  (:gen-class))

(def repl-env
  {"+" (fn [a, b] (+ a b))
   "-" (fn [a, b] (- a b))
   "*" (fn [a, b] (* a b))
   "/" (fn [a, b] (/ a b))})

(defn READ
  "Read a line and parse the syntax"
  [line]
  (reader/read-str line))

(defn EVAL
  "Eval a sexp tree"
  [sexp _env]
  sexp)

(defn eval-ast [[type value :as input] env]
  {:pre [(some? type)
         (some? value)
         (some? env)]}
  (condp = type
    :symbol (if-let [op (env value)]
              op
              (f/fail "Not found symbol (%s) in env" value))
    :list [:list (map #(eval-ast % env) value)] ; map eval-ast on each elems
    input))

(is (= (repl-env "+") (eval-ast [:symbol "+"] repl-env)))
(is (f/failed? (eval-ast [:symbol "&"] repl-env)))
(is (= [:number 3] (eval-ast [:number 3] repl-env)))
(is (= [:list []] (eval-ast [:list []] repl-env)))
(is (= [:list [(repl-env "+")]]
       (eval-ast [:list [[:symbol "+"]]] repl-env)))
(is (= [:list [[:number 3]]]
       (eval-ast [:list [[:number 3]]] repl-env)))
(is (= [:list [[:list []]]]
       (eval-ast [:list [[:list []]]] repl-env)))
(is (thrown? AssertionError (eval-ast [] repl-env)))

(defn PRINT
  "Print returned value"
  [value]
  (printer/mal-pr-str value))

(defn rep
  "REP once"
  [line]
  (PRINT (EVAL (READ line) repl-env)))

(defn repl
  "REPL"
  []
  (loop []
    (print "user> ")
    (flush)
    (when-let [line (read-line)]
      (println (rep line))
      (recur))))

(defn -main
  "Step2 of mal"
  [& _args]
  (repl))

(deftest repltest
  (is (= 1 1))
  (let [test-input (BufferedReader. (StringReader. "(+ 1 2)"))
        test-output (StringWriter.)]
    (with-bindings {#'*in* test-input
                    #'*out* test-output}
      (repl)
      (is (= "user> (+ 1 2)\nuser> " (str test-output))))))
