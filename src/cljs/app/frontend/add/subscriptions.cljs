(ns app.frontend.add.subscriptions
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :add/task
  (fn [db _]
    (get db :add "")))