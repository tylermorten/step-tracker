(ns step-tracker.db.steps
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "step_tracker/db/sql/steps.sql")


