(ns user
  (:require [ring.adapter.jetty :as jetty]))

(defonce server (atom nil))

(defn app [req]
  {:status 200 :body "Hello World!" :headers {"Content-Type" "text/html"}})

(defn start-server []
  (reset! server
          (jetty/run-jetty (fn [req] (app req))
                           {:port 3001
                            :join? false}))) 

(defn stop-server []
  (when-some [s @server]
    (.stop s)
    (reset! server nil)))

(comment
 (start-server)

(stop-server))