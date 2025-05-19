(ns app.handlers
  (:require [clojure.pprint :as pp]))

(defn add-task [_req]
  (pp/pprint "Task Added!"))