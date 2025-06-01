(ns app.frontend.main
  (:require [app.frontend.add.views :as add]
            [app.frontend.add.events :as events]
            [app.frontend.add.db :as db]
            
            ;; [reitit.frontend :as reititf]
            [re-frame.core :as rf]))

(defn init []
  (println "Hello World!"))
(init)