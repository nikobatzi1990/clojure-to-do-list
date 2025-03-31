(ns db
  (:require [next.jdbc :as jdbc]
            [clojure.pprint :as pp]
            [migratus.core :as migratus]
            [conman.core :as conman]))

;; conman config
(def datasource-options {:adapter "h2"
                         :url     "jdbc:h2:~/to-do-list"})

(def ds
  (let [conn (conman/connect! datasource-options)]
    conn))

(pp/pprint ds)

(conman/bind-connection "queries.sql")

(def config {:store :database
             :migration-dir "resources/migrations"
             :db {:dbtype "h2"
                  :dbname "resources/db/to-do-list"
                  :host "localhost"
                  :user "user"
                  :password "pass"}})

(defn migrate []
  (migratus/migrate config))

(migrate)

(defn disconnect! [conn]
  (conman/disconnect! conn))

(defn create-migration [name]
  (migratus.core/create config name))

(defn rollback-migration []
  (migratus/rollback config))

(migratus/pending-list config)

(jdbc/execute! ds ["select * from task"])