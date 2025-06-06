(ns app.main)

(defn hello []
  #?(:clj (println "Hello, Clojure!")
          :cljs (println "Hello, ClojureScript!")))