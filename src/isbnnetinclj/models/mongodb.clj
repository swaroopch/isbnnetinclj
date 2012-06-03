(ns isbnnetinclj.models.mongodb
  [:require [monger.core :as mg]])


(defn db-uri
  []
  (System/getenv "MONGOHQ_URL"))


(defn init-db
  []
  (mg/connect-via-uri! (db-uri)))
