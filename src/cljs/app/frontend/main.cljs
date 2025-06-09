(ns app.frontend.main
  (:require [reagent.dom.client :as rdc]
            [re-frame.core :as rf]))

(rf/reg-event-db
 :initialize-db              ;; usage: (dispatch [:initialize-db])
 (fn [_ _]                   ;; Ignore both params (db and event)
   {:add/task-input ""       ;; return a new value for app-db
    }))

(defn main-ui []
  [:div "Hello World!"])

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn ^:dev/after-load mount-ui []
  (rdc/render app-root [main-ui]))

(defn init []
  (rf/dispatch-sync [:initialize-db])
  (rf/clear-subscription-cache!)
  (mount-ui))