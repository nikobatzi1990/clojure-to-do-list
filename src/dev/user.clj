(ns user
  (:require [app.server :as server]
            [ring.adapter.jetty :as jetty] 
            [shadow.cljs.devtools.api :as shadow]))

(defonce server (atom nil))

(defn start-server
  {:shadow/requires-server true}
  []
  (shadow/watch :frontend)
   (reset! server
           (jetty/run-jetty #'server/handler
                            {:port 3001
                             :join? false})))

(defn stop-server []
  (when-some [s @server]
    (.stop s)
    (reset! server nil)))

(defn go []
  (stop-server) 
  (start-server))

(comment
  (go)
  )