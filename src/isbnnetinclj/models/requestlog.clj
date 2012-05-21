(ns isbnnetinclj.models.requestlog
  [:require [isbnnetinclj.models.mongodb]
   [monger.collection :as mc]
   [monger.joda-time]])

(def REQUEST_LOG "requestlog")

(defn add-details-to-log-entry
  [request]
  (assoc request :isbn (.substring (:uri request) 1)))

(defn save-request-log-entry
  [request]
  (mc/insert REQUEST_LOG (add-details-to-log-entry request)))
