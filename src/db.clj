(ns db
  (:require [next.jdbc :as jdbc]
            [migratus.core :as migratus]))

(def config {:store :database
             :migration-dir "resources/migrations"
             :db {:dbtype "h2"
                  :dbname "to-do-list"
                  :host "localhost"
                  :user "username"
                  :password "password"}})

(def db {:dbtype "h2" :dbname "to-do-list"})
(def ds (jdbc/get-datasource db))

(defn migrate []
  (migratus/migrate config))

(defn create-migration [name]
  (migratus.core/create config name))

(defn rollback-migration []
  (migratus/rollback config))

(migrate)

(jdbc/execute! ds ["select * from task"])