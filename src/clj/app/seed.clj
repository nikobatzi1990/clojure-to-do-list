(ns app.seed
  (:require [next.jdbc :as jdbc] 
            [app.db :as db]))

(jdbc/execute! {:datasource db/ds} ["insert into task(description)
                                  values('do laundry')"])

(jdbc/execute! {:datasource db/ds} ["insert into task(description)
                                  values('study Clojure')"])

(jdbc/execute! {:datasource db/ds} ["select * from task"])