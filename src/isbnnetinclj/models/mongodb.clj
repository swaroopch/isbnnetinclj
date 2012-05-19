(ns isbnnetinclj.models.mongodb
  [:require [monger.core :as mg]
   [monger.joda-time]])

(defn db-uri
  []
  (System/getenv "MONGOHQ_URL"))

(defn init-db
  []
  (mg/connect-via-uri! (db-uri)))
