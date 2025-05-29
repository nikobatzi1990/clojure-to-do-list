(ns app.frontend.add.events
   (:require [clojure.pprint :as pp]
             [re-frame.core :as rf]
             [day8.re-frame.http-fx] 
             [ajax.core :as ajax]))

(rf/reg-event-fx
 :add/submit-task
 (fn [{:keys [db]} [_ task]]
   (let [task (if-not (string? (js/String task))
                (js/String task) "")]
     {:db (assoc db :task task)
      :http-xhrio {:method          :post
                   :uri             "/api/add-task"
                   :params          {:task task}
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:add/add-task]
                   :on-failure      [:add/submit-failed]}})
   
   (pp/pprint "Task Added!")))

(rf/reg-event-fx
 :add/submit-failed
 (fn [_ [_ error]]
   (pp/pprint error)))
