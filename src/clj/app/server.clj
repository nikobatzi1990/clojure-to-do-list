(ns app.server(:require
               [shadow.cljs.devtools.server]
               [ring.adapter.jetty :as jetty]
               [ring.middleware.file :as ring-file]
               [ring.middleware.file-info :as ring-file-info]
               
              ;;  [clojure.pprint :as pp]
               [hiccup2.core :as h]))
               

(defn app [{:keys [query-string] :as req}]
  ;; (pp/pprint req)
  {:status 200 :headers {"Content-Type" "text/html"}
   :body (str (h/html
               [:html 
                [:head
                 [:title "To-Do List"]
                 [:script {:src "/public/js/main.js"}]
                 [:link {:rel "stylesheet" :href "/public/css/styles.css"}]] 
                [:body 
                 [:h1 "To-Do List"]
                 [:p "Welcome to the Clojure To-Do List web application."]
                 [:form {:method "get" :action "/"}
                  [:label {:for "task-input"} "New Task: "]
                  [:input#task-input {:type "text" :name "task" :value query-string}]
                  [:button {:type "submit"} "+"]]]]))})

(def handler
  (->
   app
   (ring-file/wrap-file "public")
   (ring-file-info/wrap-file-info)))

(defn -main [& _args]
  (jetty/run-jetty handler {:port 3000}))