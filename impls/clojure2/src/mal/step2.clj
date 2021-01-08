(ns mal.step2
  (:import (java.io StringReader
                    BufferedReader
                    StringWriter))
  (:require [mal.reader :as reader]
            [mal.printer :as printer]
            [clojure.test :refer [deftest is]]
            [failjure.core :as f]
            [mal.types :refer [mal-type? mal-list? mal-symbol? mal-list?
                               mal-list-map mal-list-f-args
                               mal-symbol->str
                               make-mal-num-biop
                               make-mal-num make-mal-list make-mal-symbol]])
  (:gen-class))

(def repl-env
  {"+" (make-mal-num-biop +)
   "-" (make-mal-num-biop -)
   "*" (make-mal-num-biop *)
   "/" (make-mal-num-biop /)})

(defn READ
  "Read a line and parse the syntax"
  [line]
  (reader/read-str line))

(defn eval-ast [ast env]
  {:pre [(mal-type? ast)
         (some? env)]}
  (cond
    (mal-symbol? ast)
    (if-let [op (env (mal-symbol->str ast))]
      op
      (f/fail "Not found symbol (%s) in env" ast))

    (mal-list? ast)
    (mal-list-map #(eval-ast % env) ast)  ; map eval-ast on each elems
    :else ast))

(is (= (repl-env "+") (eval-ast (make-mal-symbol "+") repl-env)))
(is (f/failed? (eval-ast (make-mal-symbol "&") repl-env)))
(is (= (make-mal-num 3) (eval-ast (make-mal-num 3) repl-env)))
(is (= (make-mal-list []) (eval-ast (make-mal-list []) repl-env)))
(is (= (make-mal-list [(repl-env "+")])
       (eval-ast (make-mal-list [(make-mal-symbol "+")]) repl-env)))
(is (= (make-mal-list [(make-mal-num 3)])
       (eval-ast (make-mal-list [(make-mal-num 3)]) repl-env)))
(is (= (make-mal-list [(make-mal-list [])])
       (eval-ast (make-mal-list [(make-mal-list [])]) repl-env)))
(is (thrown? AssertionError (eval-ast [] repl-env)))

(defn EVAL
  "Eval a sexp tree"
  [[type value :as exp] env]
  {:pre [(some? type)
         (some? value)]}
  (cond
    (and (mal-list? exp) (empty? value)) exp

    (mal-list? exp)
    (let [evaled-1 (mal-list-map #(EVAL % env) exp)
          [f args] (mal-list-f-args evaled-1)]
      (apply f args))

    :else (eval-ast exp env)))

(deftest test-eval
  (is (= (make-mal-num 3) (EVAL (make-mal-num 3) repl-env)))
  (EVAL (make-mal-list []) repl-env)
  (EVAL (make-mal-list [(make-mal-symbol "+")
                        (make-mal-num 3)
                        (make-mal-num 5)])
        repl-env))

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

(defn test-repl [input expected]
  (let [test-input (BufferedReader. (StringReader. input))
        test-output (StringWriter.)
        expected-repl-out (format "user> %s\nuser> " expected)]
    (with-bindings {#'*in* test-input
                    #'*out* test-output}
      (repl)
      (is (= expected-repl-out (str test-output))))))

(deftest repltest
  (is (= 1 1))
  (test-repl "(+ 1 2)" "3")
  (test-repl "(- 1 2)" "-1")
  (test-repl "(* 1 2)" "2")
  (test-repl "(/ 2 1)" "2")
  (test-repl "(/ (* 2 3) 2)" "3"))
