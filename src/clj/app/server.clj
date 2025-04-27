(ns app.server(:require
               [shadow.cljs.devtools.server]
               [ring.adapter.jetty :as jetty]
               [ring.middleware.file :as ring-file]
               [ring.middleware.file-info :as ring-file-info]
               
               [clojure.pprint :as pp]))

(defn app [{:keys [query-string] :as req}]
  {:status 200 :body "Hello World!" :headers {"Content-Type" "text/html"}})

(def handler
  (->
   app
   (ring-file/wrap-file "public")
   (ring-file-info/wrap-file-info)))

(defn -main [& _args]
  (jetty/run-jetty handler {:port 3000}))