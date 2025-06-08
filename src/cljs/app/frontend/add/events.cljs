(ns app.frontend.add.events
   (:require [clojure.pprint :as pp]
             [re-frame.core :as rf]
             [day8.re-frame.http-fx] 
             [ajax.core :as ajax]))

(rf/reg-event-db
 :add/update-task
 (fn [db [_ value]]
   (assoc-in db [:add/task] value)))

(rf/reg-event-fx
 :add/get-task-list
 (fn [{:keys [db]} [_ task]]
   {:db (assoc db :task task)
    :http-xhrio {:method          :get
                 :uri             "/api/get-task-list"
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:add/task-list-fetched]
                 :on-failure      [:add/task-failed]}}))

(rf/reg-event-db
 :add/task-list-fetched
 (fn [db data]
   (assoc db :tasks data)))

(rf/reg-event-fx
 :add/submit-task
 (fn [{:keys [db]} [_ task]]
     {:db (assoc db :task task)
      :http-xhrio {:method          :post
                   :uri             "/api/add-task"
                   :params          {:task task}
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:add/task-saved]
                   :on-failure      [:add/task-failed]}}))

(rf/reg-event-db
 :add/task-saved
 (fn [db _]
   (assoc-in db [:add/task] "")))

(rf/reg-event-fx
 :add/task-failed
 (fn [_ [_ error]]
   (pp/pprint error)))
