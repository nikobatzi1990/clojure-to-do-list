(ns app.frontend.add.db
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 :add/add-task
 (fn [db [_ {:keys [result]}]]
   (assoc db :add/result result)))
