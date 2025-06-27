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
   {:db (assoc db :task task)
    :http-xhrio {:method          :post
                 :uri             "/api/add-task"
                 :params          {:task task}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:tasks/task-saved]
                 :on-failure      [:tasks/failed]}}))

(rf/reg-event-fx
 :tasks/task-saved
 (fn []
   (js/console.log "Task saved!")))

(rf/reg-event-fx
 :tasks/delete-task
 (fn [{:keys [db]} [_ task-id]] 
   {:http-xhrio {:method          :delete
                 :uri             "/api/delete-task"
                 :params          {:task-id task-id}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:tasks/task-deleted {:task-id task-id}]
                 :on-failure      [:tasks/failed]}
    :db (assoc db :loading? true)}))

(rf/reg-event-db
 :tasks/task-deleted
 (fn [db [_ {:keys [task-id]}]]
   (-> db
       (update :tasks
               (fn [tasks]
                 (filterv (fn [task]
                            (not= (:task/id task) task-id)) tasks)))
       (assoc :loading? false))))

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
   [:label.label {:for "task-input"} "New Task: "]
   [:input#task-input
    {:type "text"
     :name "task"
     :class "input"}]
   [:button.button {:type "submit"} "+"]])

(defn delete-button [task-id]
  [:button {:type "button" 
            :on-click #(rf/dispatch [:tasks/delete-task task-id])} "Delete Task"])

(defn tasks-ui []
  (let [tasks @(rf/subscribe [:tasks/all-tasks])]
    [:div {:class "is-flex"}
     [:ul.grid
      (for [task tasks]
        ^{:key (:task/id task)} 
        [:li.is-flex {:id (:task/id task)}
         (:task/description task) 
         [delete-button (:task/id task)]])]]))

(defn main-ui []
  [:div.container
   [:h1.title "To-Do List"]
   [:div.level 
    [task-input]]
   [tasks-ui]])

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn ^:dev/after-load mount-ui []
  (rdc/render app-root [main-ui]))

(defn init []
  (rf/dispatch-sync [:initialize-app])
  (rf/clear-subscription-cache!)
  (mount-ui))