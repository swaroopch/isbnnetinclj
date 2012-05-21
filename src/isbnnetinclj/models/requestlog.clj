(ns isbnnetinclj.models.requestlog
  [:require [isbnnetinclj.models.mongodb]
   [monger.collection :as mc]
   [monger.joda-time]])

(defn add-details
  [request]
  (assoc request :isbn (.substring (:uri request) 1)))

(def REQUEST_LOG "requestlog")

(defn add-log
  [request]
  (mc/insert REQUEST_LOG (add-details request)))

(defn get-logs
  []
  (keep :isbn (mc/find-maps REQUEST_LOG)))
