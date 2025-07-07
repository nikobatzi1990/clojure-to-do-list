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
                 :on-success      [:tasks/get-task-list]
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
                 :on-success      [:tasks/get-task-list]
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

;; remember to synchronize backend and frontend
;; frontend should reflect whatever is in the backend db
;; "best practice" isn't always best practice in the context of your app
;; add status update message to display on frontend on success
(rf/reg-event-fx
 :tasks/complete-task
 (fn [{:keys [db]} [_ task-id]]
   {:http-xhrio {:method          :patch
                 :uri             "/api/complete-task"
                 :params          {:task-id task-id}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:tasks/get-task-list]
                 :on-failure      [:tasks/failed]}
     ;; bonus - loading attribute is not currently being used anywhere
     ;; display loading on frontend with a loading ring/hourglass
     ;; loading might be too quick on local, so add a delay in backend (Thread/sleep functions)
     ;;  don't forget to remove (Thread/sleep) when done testing
    :db (assoc db :loading? true)}))

(rf/reg-event-db
 :tasks/toggle-completed
 (fn [db [_ {:keys [task-id]}]]
   (-> db
       (update :tasks
               (fn [tasks]
                 (mapv (fn [task]
                         (if (= (:task/id task) task-id)
                           (update task :task/completed not)
                           task))
                       tasks))) 
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
   [:div {:class "is-flex"}
    [:input#task-input
     {:type "text"
      :name "task"
      :class "input"}]
    [:button.button {:type "submit"} "+"]]])

(defn delete-button [task-id]
  [:button.delete {:type "button" 
            :on-click #(rf/dispatch [:tasks/delete-task task-id])}])

(defn checkbox [task task-id]
  [:input {:type "checkbox"
           :checked (:task/completed task)
           :on-change #(rf/dispatch [:tasks/complete-task task-id])}])

(defn tasks-ui []
  (let [tasks @(rf/subscribe [:tasks/all-tasks])]
    [:div {:class "is-flex"}
     [:ul.grid
      (for [task tasks]
        ^{:key (:task/id task)}
        [:li.is-flex {:id (:task/id task)}
         [:label.is-flex {:style {:gap "0.5rem"}}
         [checkbox task (:task/id task)]
          (:task/description task)]
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