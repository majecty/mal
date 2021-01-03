(ns mal.step0
  (:gen-class))

(defn READ
  "Read a line and parse the syntax"
  [line]
  line)

(defn EVAL
  "Eval a sexp tree"
  [sexp]
  sexp)

(defn PRINT
  "Print returned value"
  [value]
  value)

(defn rep
  "REP once"
  [line]
  (PRINT (EVAL (READ line))))

(defn repl
  "REPL"
  []
  (loop []
    (println "user> ")
    (let [line (read-line)]
      (if (nil? line)
        nil
        (do (println (rep line))
            (recur))))))

(defn -main
  "Step0 of mal"
  [& args]
  (repl))
