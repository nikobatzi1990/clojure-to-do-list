(ns app.frontend.main
  (:require [clojure.pprint :as pp]
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(rf/reg-event-fx
 :initialize-app
 (fn [_ _]
   {:fx [[:dispatch [:tasks/get-task-list]]]
    :db
    {:tasks nil
     :loading? true}}))

(rf/reg-event-db
 :tasks/list-fetched
 (fn [db [_ {tasks :tasks}]]
   (assoc db :tasks tasks :loading? false)))

(rf/reg-event-fx
 :tasks/failed
 (fn [_ [_ error]]
   (pp/pprint error)))

(rf/reg-event-fx
 :tasks/get-task-list
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             "/api/task-list"
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:tasks/list-fetched]
                 :on-failure      [:tasks/failed]}
    :db (assoc db :tasks nil :loading? true)}))

(rf/reg-event-fx
 :tasks/submit-task
 (fn [{:keys [db]} [_ task]]
   (js/console.log task)
   {:db (assoc db :task task)
    :http-xhrio {:method          :post
                 :uri             "/api/add-task"
                 :params          {:task task}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:tasks/task-saved]
                 :on-failure      [:tasks/failed]}}))

(rf/reg-event-db
 :tasks/task-saved
 (fn [db _]
   db))

(rf/reg-event-fx
 :tasks/delete-task
 (fn [{:keys [db]} [_ task]]
   (js/console.log "Task deleted:" task)
   {:db (assoc db :task task)
    :http-xhrio {:method          :delete
                 :uri             "/api/delete-task"
                 :params          {:task-id (:task/id task)}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:tasks/task-deleted]
                 :on-failure      [:tasks/failed]}}))

(rf/reg-event-db
 :tasks/task-deleted
 (fn [db _]
   db))

(rf/reg-sub
 :tasks/all-tasks
 (fn [db _]
   (get db :tasks [])))

;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;

(defn task-input []
  [:form
   {:on-submit #(rf/dispatch [:tasks/submit-task (-> "task-input" js/document.getElementById .-value)])}
   [:label {:for "task-input"} "New Task: "]
   [:input#task-input
    {:type "text"
     :name "task"}]
   [:button {:type "submit"} "+"]])

(defn tasks-ui []
  (let [tasks @(rf/subscribe [:tasks/all-tasks])]
    [:div
     [:ul
      (for [task tasks]
        ^{:key (:task/id task)} 
        [:li {:id (:task/id task)} (:task/description task)])]]))

(defn main-ui []
  [:div
   [:h1 "To-Do List"]
   [:div [task-input]]
   [:div [tasks-ui]]])

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn ^:dev/after-load mount-ui []
  (rdc/render app-root [main-ui]))

(defn init []
  (rf/dispatch-sync [:initialize-app])
  (rf/clear-subscription-cache!)
  (mount-ui))