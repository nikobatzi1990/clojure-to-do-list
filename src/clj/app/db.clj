(ns clj.app.db
  (:require [migratus.core :as migratus]
            [conman.core :as conman]))

;; conman config
(def db {:adapter "h2" 
         :url "jdbc:h2:./resources/db/to-do-list"})

(def ds (conman/connect! db))

(conman/bind-connection "queries.sql")

(defn disconnect! []
  (conman/disconnect! ds))

;; migratus config
(def config {:store :database
             :migration-dir "resources/migrations"
             :db {:dbtype "h2"
                  :dbname "./resources/db/to-do-list"}})

(defn migrate []
  (migratus/migrate config))

(migrate)

(defn create-migration [name]
  (migratus.core/create config name))

(defn rollback-migration []
  (migratus/rollback config))