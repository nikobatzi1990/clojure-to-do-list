(ns app.handlers
  (:require [app.db :as db]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [hiccup2.core :as h]))

(defn main-ui [_req]
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
                          [:script {:src "/js/main.js"}]]]))})

(defn task-list [_req]
  (let [task-list
        (jdbc/execute! db/ds 
                       ["select * from task"]
                       {:builder-fn rs/as-lower-maps})]
    {:status 200
     :body {:tasks task-list}}))

(defn save-task [req]
  (let [task (get-in req [:parameters :body :task])]
    (jdbc/execute! db/ds
                   ["insert into task (description) values (?)" task])
    {:status 200
     :body {:message "Task saved!"}}))