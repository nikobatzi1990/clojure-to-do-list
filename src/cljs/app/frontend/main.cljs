(ns app.frontend.main
  (:require [clojure.pprint :as pp]
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(rf/reg-event-db
 :initialize-db
 (fn [_ _]     
   {:tasks nil}))

(rf/reg-event-db
 :tasks/list-fetched
 (fn [db [_ {tasks :tasks}]]
   (js/console.log tasks)
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
    :db (assoc db :loading? true :tasks nil)}))


;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;


(defn main-ui []
  [:div "Hello World!"
   [:p [:button {:on-click #(rf/dispatch [:tasks/get-task-list])} "Load tasks"]]
   ])

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn ^:dev/after-load mount-ui []
  (rdc/render app-root [main-ui]))

(defn init []
  (rf/dispatch-sync [:initialize-db])
  (rf/clear-subscription-cache!)
  (mount-ui))