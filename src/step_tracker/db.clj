(ns step-tracker.db
  (:require [environ.core :refer [env]]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))

(def db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname (str "//"(get env :host "localhost") "/" (get env :database "step-tracker"))
   :user (get env :db-user "step-tracker")
   :password (get env :db-pass "Buddy1234!")})

(def database-url
  (str "jdbc:postgresql:" (:subname db) "?user=" (:user db) "&password=" (:password db)))

;; Migrations config
(def config
  {:datastore (jdbc/sql-database database-url)
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate config))

(defn rollback []
  (repl/rollback config))