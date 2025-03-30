(ns db
  (:require [next.jdbc :as jdbc]
            [migratus.core :as migratus]
            [conman.core :as conman]))

(def config {:store :database
             :migration-dir "resources/migrations"
             :db {:dbtype "h2"
                  :dbname "resources/db/to-do-list"
                  :host "localhost"
                  :user "user"
                  :password "pass"}})

(def db {:dbtype "h2" :dbname "to-do-list"})
(def ds (jdbc/get-datasource db))

(migratus/init config)

(defn migrate []
  (migratus/migrate config))

(def datasource-options {:adapter "h2"
                         :url     "jdbc:h2:~/to-do-list"
                         :username      "user"
                         :password      "pass"})

(conman/connect! [datasource-options])

(defn create-migration [name]
  (migratus.core/create config name))

(defn rollback-migration []
  (migratus/rollback config))

(migrate)

(migratus/pending-list config)

(jdbc/execute! ds ["select * from task"])