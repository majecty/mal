(ns mal.reader
  (:require [blancas.kern.core :as kern]
            [blancas.kern.lexer.basic :as lexer]
            [clojure.test :refer [is]])
  (:gen-class))

(declare p-s-expr)

(def p-list (->> p-s-expr
                 lexer/lexeme
                 kern/many0
                 lexer/parens
                 (kern/<$> #(vector :list %))))

(defn symbol-char? [c]
  (and
   (not (Character/isWhitespace c))
   (not= c \()
   (not= c \))))

(is (symbol-char? \a))
(is (not (symbol-char? \()))

(def p-symbol-char
  (kern/<?> (kern/satisfy symbol-char?)
            "symbol character"))

(is (= \a (kern/value p-symbol-char "as")))
(is (not (kern/value p-symbol-char "(")))

(def p-symbol
  (->> p-symbol-char
       (kern/many1)
       (kern/<+>)
       (kern/<$> #(vector :symbol %))))

(is (= [:symbol "as"] (kern/value p-symbol "as")))

(def p-number
  (kern/<$> #(vector :number %) lexer/dec-lit))

(def p-s-expr (kern/<|> p-list p-number p-symbol))

(is (= [:list [[:number 1] [:symbol "as"]]] (kern/value p-s-expr "(1 as)")))

(defn read-str [str]
  (kern/value p-s-expr str))

(is (= [:list [[:number 1] [:symbol "as"]]] (read-str "(1 as)")))
