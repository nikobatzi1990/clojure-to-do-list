(ns app.frontend.add.views
  (:require [re-frame.core :as rf]))

(defn task-input []
  (let [task @(rf/subscribe [:add/task])]
    [:form
     {:on-submit (fn [e]
                   (.preventDefault e)
                   (rf/dispatch [:add/submit-task task]))}
     [:label {:for "task-input"} "New Task: "]
     [:input#task-input
      {:type "text"
       :name "task"
       :value task
       :on-change #(rf/dispatch [:add/update-task (-> % .-target .-value)])}]
     [:button {:type "submit"} "+"]]))