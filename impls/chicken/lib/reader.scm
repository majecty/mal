(module lib.reader (foo)
  (import scheme)
  (import comparse)
  (define (foo)
    (display "This is foo\n"))
  (define (read-str)
    (error "not implemented yet"))
  (define (symbol char) ('symbol . char))
  (define (int num) ('int . num))
  ;; receive string and return token array
  (define (tokenize str)
    (error "not implemented yet")))
