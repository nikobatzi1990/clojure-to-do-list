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

   [app.handlers :as handlers]
   [reitit.coercion.spec :as rcs]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]))

(def routes
  ["/"
   ["" (merge
        {:name :app/main}
        {:no-doc true
         :get #'handlers/main-ui})]
   ["add" (merge
           {:name :app/add}
           {:no-doc true
            :get #'handlers/main-ui})]
   ["api"
    ["" {:no-doc true}
     ["/swagger.json" {:get (swagger/create-swagger-handler)}]
     ["/api-docs/*" {:get (swagger-ui/create-swagger-ui-handler {:url "/api/swagger.json"})}]]

    ["/task-list"
     {:name :api/task-list
      :summary "Gets task list from database"
      :coercion rcs/coercion
      :get {:responses {200 {:body {:tasks vector?}}}
            :handler #'handlers/task-list}}]

    ["/add-task"
     {:name :api/add-task
      :summary "Adds a new task to To-Do List"
      :coercion rcs/coercion
      :post {:parameters {:body {:task string?}}
             :responses {200 {:body {:message string?}}}
             :handler #'handlers/save-task}}]
    
    ["/delete-task"
     {:name :api/delete-task
      :summary "Deletes a task from To-Do List"
      :coercion rcs/coercion
      :delete {:parameters {:body {:task-id int?}}
               :responses {200 {:body {:message string?}}}
               :handler #'handlers/delete-task}}]]])

(def app
  (ring/ring-handler
   (ring/router
    routes
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