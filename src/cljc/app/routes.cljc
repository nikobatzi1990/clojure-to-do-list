(ns app.routes
  (:require
   #?@(:clj  [[reitit.coercion.spec :as rcs]
              [reitit.swagger :as swagger]
              [reitit.swagger-ui :as swagger-ui]]
       :cljs [[re-frame.core :as rf]
              [app.main :as main] 
              [app.frontend.add.views :as add]])))

(def routes
  ["/"
   ["" (merge
        {:name :app/main}
        #?(:cljs {:view main/hello
                  :controllers [{:start #(js/console.log "Welcome to Clojure To-Do List!")
                                  :stop #(js/console.log "Goodbye from Clojure To-Do List!")}]}))]
   ["add" (merge
           {:name :app/add}
           #?(:cljs {:view add/task-input
                     :controllers [{:start #(js/console.log "Adding task...")
                                     :stop #(js/console.log "Stopped adding task...")}]}))]])