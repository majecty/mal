(ns mal.reader
  (:require [blancas.kern.core :as kern]
            [blancas.kern.lexer.basic :as lexer]
            [clojure.test :refer :all])
  (:gen-class))

(declare p-s-expr)

(def p-list (-> p-s-expr
                lexer/lexeme
                kern/many0
                lexer/parens))

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
  (kern/<+> (kern/many1 p-symbol-char)))

(is (= "as" (kern/value p-symbol "as")))

(def p-s-expr (kern/<|> p-list lexer/dec-lit p-symbol))

(is (= [1 "as"] (kern/value p-s-expr "(1 as)")))

(defn parse [str]
  (kern/value p-s-expr str))

(is (= [1 "as"] (parse "(1 as)")))
