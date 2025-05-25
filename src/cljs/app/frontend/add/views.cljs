(ns app.frontend.add.views
  (:require [clojure.pprint :as pp]
            [re-frame.core :as rf]))

(defn task-input [{:keys [query-string] :as req}]
  [:form {:method "get" :action "/"}
   [:label {:for "task-input"} "New Task: "]
   [:input#task-input {:type "text" :name "task" :value query-string}]
   [:button {:type "submit"} "+"]]
  )

(defn add-task [_req]
  (pp/pprint "Task Added!"))