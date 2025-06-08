(ns app.frontend.add.subscriptions
  (:require [re-frame.core :as rf]))

;; Triggered when database is modified

(rf/reg-sub
  :add/task
  (fn [db _]
    (get db :add "")))