(ns app.handlers
  (:require [app.db :as db]
            [next.jdbc :as jdbc]))

(defn save-task [req]
  (let [task (get-in req [:params :task])]
    (jdbc/execute! {:datasource db/ds} [(str 
                                         "insert into task(description)
                                          values(" task ")")])
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body {:message "Task saved!"}}))