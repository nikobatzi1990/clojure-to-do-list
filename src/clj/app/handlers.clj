(ns app.handlers
  (:require [hiccup2.core :as h]
            [clojure.pprint :as pp]))

(defn add-task [_req]
  (pp/pprint "Task Added!"))