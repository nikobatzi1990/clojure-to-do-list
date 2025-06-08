(ns app.server
  (:require
   [ring.adapter.jetty :as jetty]
   [ring.middleware.file :as ring-file]
   [ring.middleware.file-info :as ring-file-info]
   [reitit.ring :as ring]
   [ring.middleware.params :as params]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [muuntaja.core :as m]
   [reitit.ring.coercion :as rrc]
   [app.routes :as routes]))

(def app
  (ring/ring-handler
   (ring/router
    routes/routes
    {:data {:muuntaja m/instance
            :middleware [params/wrap-params
                         muuntaja/format-middleware
                         rrc/coerce-exceptions-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]}})))

(def handler
  (->
   app
   (ring-file/wrap-file "public")
   (ring-file-info/wrap-file-info)))

(defn -main [& _args]
  (jetty/run-jetty handler {:port 3000}))