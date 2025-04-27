;; (ns clj.app.seed
;;   (:require [next.jdbc :as jdbc]
;;             [clojure.pprint :as pp]
;;             [db/ds :as ds]))

;; (jdbc/execute! {:datasource ds} ["insert into task(description)
;;                                   values('do laundry')"])

;; (jdbc/execute! {:datasource ds} ["insert into task(description)
;;                                   values('study Clojure')"])

;; (jdbc/execute! {:datasource ds} ["select * from task"])