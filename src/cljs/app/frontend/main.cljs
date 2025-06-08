(ns app.frontend.main
  (:require [app.frontend.views :as main]
            [app.frontend.add.views :as add]
            [app.frontend.add.events :as events]
            [app.frontend.add.db :as db]
            [app.routes :as routes]
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf]
            
            [reitit.frontend :as reititf]
            [reitit.frontend.easy :as rfe]))

(rf/reg-event-db
 :initialize-db              ;; usage: (dispatch [:initialise-db])
 (fn [_ _]                   ;; Ignore both params (db and event)
   {:add/task-input ""       ;; return a new value for app-db
    }))

(def routes
  (reititf/router
   routes/routes))

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn ^:dev/after-load mount-ui []
  (rdc/render app-root [main/main-ui]))

(defn init []
   (rf/dispatch-sync [:initialize-db])
   (rf/clear-subscription-cache!))

(rfe/start!
  routes
  (fn [new-match]
    (rf/dispatch [:app/panel new-match]))
  {:use-fragment false})

 (mount-ui)