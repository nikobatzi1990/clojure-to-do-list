(ns app.routes
  (:require
   #?@(:clj  [[app.handlers :as handlers]
              [reitit.coercion.spec :as rcs]
              [reitit.swagger :as swagger]
              [reitit.swagger-ui :as swagger-ui]]
       :cljs [[app.frontend.views :as main]
              [app.frontend.add.views :as add]])))

(def routes
  ["/"
   ["" (merge
        {:name :app/main}
        #?(:cljs {:view main/main-ui
                  :controllers [{:start #(js/console.log "Welcome to Clojure To-Do List!")
                                 :stop #(js/console.log "Goodbye from Clojure To-Do List!")}]}
            :clj {:no-doc true
                  :get #'handlers/task-list}))]
   ["add" (merge
           {:name :app/add}
           #?(:cljs {:view add/task-input
                     :controllers [{:start #(js/console.log "Adding task...")
                                    :stop #(js/console.log "Stopped adding task...")}]}
             :clj {:no-doc true
                   :get #'handlers/task-list}))]
   
   #?@(:clj
       [["api"
         ["" {:no-doc true}
           ["/swagger.json" {:get (swagger/create-swagger-handler)}]
           ["/api-docs/*" {:get (swagger-ui/create-swagger-ui-handler {:url "/api/swagger.json"})}]]
         
         "/add-task"
         {:name :api/add-task
          :summary "Adds a new task to To-Do List"
          :coercion rcs/coercion
          :post {:parameters {:body {:task string?}}
                 :responses {200 {:body {:result string?}}}
                 :handler #'handlers/save-task}}]])])