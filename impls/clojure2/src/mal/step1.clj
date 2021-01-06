(ns mal.step1
  (:import (java.io StringReader
                    BufferedReader
                    StringWriter))
  (:require [mal.reader :as reader]
            [mal.printer :as printer]
            [clojure.test :refer [deftest is]])
  (:gen-class))

(defn READ
  "Read a line and parse the syntax"
  [line]
  (reader/read-str line))

(defn EVAL
  "Eval a sexp tree"
  [sexp]
  sexp)

(defn PRINT
  "Print returned value"
  [value]
  (printer/mal-pr-str value))

(defn rep
  "REP once"
  [line]
  (PRINT (EVAL (READ line))))

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
  "Step1 of mal"
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
