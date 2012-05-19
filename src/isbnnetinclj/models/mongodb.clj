(ns isbnnetinclj.models.mongodb
  [:require [monger.core :as mg]
   [monger.joda-time]])

(defn init-db
  []
  (mg/connect-via-uri! (System/getenv "MONGOHQ_URL")))
