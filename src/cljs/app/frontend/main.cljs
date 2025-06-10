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

(rf/reg-event-db
 :tasks/update-task
 (fn [db [_ value]]
   (assoc db :tasks/new value)))

(rf/reg-sub
 :tasks/all-tasks
 (fn [db _]
   (get db :tasks [])))

(rf/reg-sub
 :tasks/new-task
 (fn [db _]
   (get db :tasks/new "")))

;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;

(defn task-input []
  #_ (let [task (r/atom nil)])
  [:form
   {:on-submit (fn [e]
                 (.preventDefault e))}
   [:label {:for "task-input"} "New Task: "]
   [:input#task-input
    {:type "text"
     :name "task"
     ;:value @task
     #_#_:on-change #(reset! task (-> % .-target .-value))}]
   [:button {:type "submit"
             :on-click #(rf/dispatch [:tasks/submit-task (-> "task-input" js/document.getElementById .-value)])} "+"]])

(defn tasks-ui []
  (let [tasks @(rf/subscribe [:tasks/all-tasks])]
    [:div
     [:ul
      (for [task tasks]
        ^{:key (gensym (:task/id task))} [:li (:task/description task)])]]))

(defn main-ui []
  [:div "Hello World!"
   [:p [:button {:on-click #(rf/dispatch [:tasks/get-task-list])} "Reload tasks"]]
   [:div [task-input]]
   [:div [tasks-ui]]
   ])

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn ^:dev/after-load mount-ui []
  (rdc/render app-root [main-ui]))

(defn init []
  (rf/dispatch-sync [:initialize-app])
  (rf/clear-subscription-cache!)
  (mount-ui))