(ns app.frontend.add.db
  (:require [re-frame.core :as rf])
  )

;; store tasklist in app.db


(rf/reg-event-db
 :add/add-task
 (fn [db [_ {:keys [result]}]]
   (assoc db :add/result result)))

{:tasks []}