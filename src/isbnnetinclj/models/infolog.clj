(ns isbnnetinclj.models.infolog
  (:require [monger.collection :as mc]
            [monger.query :as mq]
            [monger.joda-time]
            [clj-time.core :as time]
            [isbnnetinclj.utils :as utils]))

(def INFO_LOG "infolog")

(defn info-to-log-entry
  [isbn info]
  {:timestamp (time/now) :info info :isbn isbn})

(defn save-info-log
  [entry]
  (mc/insert INFO_LOG entry))

(defn get-stored-info
  [isbn]
  (first (mq/with-collection
           INFO_LOG
           (mq/find {:isbn isbn :timestamp {"$gt" (utils/twenty-four-hours-ago)}})
           (mq/sort {:timestamp -1})
           (mq/limit 1))))
