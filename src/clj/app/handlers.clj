(ns app.handlers
  (:require [app.db :as db]
            [next.jdbc :as jdbc]
            [hiccup2.core :as h]))

(defn task-list [req]
  (let [task-list 
        (jdbc/execute! db/ds 
                       ["select * from task"])]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str (h/html   [:html
                         [:head
                          [:title "To-Do List"]
                          [:link {:rel "stylesheet" :href "/css/styles.css"}]]
                         [:body
                          [:div#app
                           [:h1 "To-Do List"]
                           [:p "Welcome to the Clojure To-Do List web application."]]
                          [:script {:src "/js/main.js"}]]]))}))

(defn save-task [req]
  (let [task (get-in req [:params :task])]
    (jdbc/execute! db/ds
                   ["insert into task (description) values (?)" task])
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body {:message "Task saved!"}}))