(ns mal.step2
  (:import (java.io StringReader
                    BufferedReader
                    StringWriter))
  (:require [mal.reader :as reader]
            [mal.printer :as printer]
            [clojure.test :refer [deftest is]]
            [failjure.core :as f]
            [mal.types :refer [mal-type? mal-num? mal-list? mal-symbol? mal-list?
                               mal-inner
                               mal-list-map mal-list-f-args
                               mal-symbol->str
                               make-mal-num make-mal-list make-mal-symbol]])
  (:gen-class))

(def repl-env
  {"+" (fn [a b]
         {:pre [(mal-num? a)
                (mal-num? b)]}
         (let [va (mal-inner a)
               vb (mal-inner b)]
           (make-mal-num (+ va vb))))
   "-" (fn [a, b] (- a b))
   "*" (fn [a, b] (* a b))
   "/" (fn [a, b] (/ a b))})

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
    #_(mal-list-map #(eval-ast % env) ast)  ; map eval-ast on each elems
    (mal-list-map (fn [inner]
                    (printf "inner: %s \n" inner)
                    (eval-ast inner env))
                  ast)  ; map eval-ast on each elems    

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
    (let [evaled-ast (eval-ast exp env)
          [f args] (mal-list-f-args evaled-ast)]
      (apply f args))

    :else (eval-ast exp env)))

(is (= (make-mal-num 3) (EVAL (make-mal-num 3) repl-env)))
(EVAL (make-mal-list []) repl-env)
(EVAL (make-mal-list [(make-mal-symbol "+")
                      (make-mal-num 3)
                      (make-mal-num 5)])
      repl-env)

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
      (is (= "user> 3\nuser> " (str test-output))))))
