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

(rf/reg-sub
 :tasks/all-tasks
 (fn [db _]
   (get db :tasks [])))

;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;

(defn tasks-ui []
  (let [tasks @(rf/subscribe [:tasks/all-tasks])]
    [:div
     [:ul
      (for [task tasks]
        ^{:key (gensym (:TASK/ID task))} [:li (:TASK/DESCRIPTION task)])]]))

(defn main-ui []
  [:div "Hello World!"
   [:p [:button {:on-click #(rf/dispatch [:tasks/get-task-list])} "Reload tasks"]]
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