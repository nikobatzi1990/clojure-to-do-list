(ns clj.app.server
  (:require
   [shadow.cljs.devtools.server]
   [clojure.pprint :as pp]))

(defn app [{:keys [query-string] :as req}]
  (pp/pprint "Request: " req)
  {:status 200 :body "Hello World!" :headers {"Content-Type" "text/html"}})