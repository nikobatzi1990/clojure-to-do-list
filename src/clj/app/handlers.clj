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
                          [:meta {:charset "UTF-8"}]
                          [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
                          [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/bulma@1.0.4/css/bulma.min.css"}]
                          [:link {:rel "stylesheet" :href "/css/styles.css"}]
                          [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
                          [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin "true"}]
                          [:link {:href "https://fonts.googleapis.com/css2?family=Huninn&display=swap"
                                  :rel "stylesheet"}]]
                         [:body.huninn-regular
                          [:div#app]
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

(defn delete-task [req]
  (let [task (get-in req [:parameters :body :task-id])]
    (jdbc/execute! db/ds
                   ["delete from task where id = ?" task])
    {:status 200
     :body {:message "Task deleted!"}}))

(defn complete-task [req] 
  (print "HELLO")
  (let [task (get-in req [:parameters :body :task-id])]
    (jdbc/execute! db/ds
                   ["update task set completed = true where id = ?" task])
    {:status 200
     :body {:message "Task completed!"}}))