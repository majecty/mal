(import (chicken io))
(import (chicken port))
(import (chicken format))
(import miscmacros)

(define (READ input)
  input)

(define (EVAL input)
  input)

(define (PRINT input)
  input)

(define rep 
  (compose READ EVAL PRINT))

(define (main)
  (define (iter)
    (define line)
    (let/cc ret
      (display "user>")
      (set! line (read-line))
      (when (eof-object? line)
        (printf "I found a EOF\n")
        (ret '())
        (rep line)
        (printf "rep is ~A~N" (rep line)))
      (iter)))
  (iter))

;; (display "hi")
;; (main)
(with-input-from-string "a\nb\nc"
  (cut main))

