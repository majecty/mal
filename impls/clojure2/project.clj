(defproject mal "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.blancas/kern "1.1.0"]]
  :main ^:skip-aot mal.core
  :target-path "target/%s"
  :plugins [[lein-cljfmt "0.7.0"]]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :step0 {:aot :all
                     :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                     :main mal.step0}
             :step1 {:aot :all
                     :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                     :main mal.step1}}
  :aliases {"step0" ["with-profile" "step0" "run"]
            "step1" ["with-profile" "step1" "run"]})
