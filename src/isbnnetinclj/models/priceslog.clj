(ns isbnnetinclj.models.priceslog
  (:require [monger.collection :as mc]
            [monger.query :as mq]
            [monger.joda-time]
            [clj-time.core :as time]
            [isbnnetinclj.utils :as utils]))

(def PRICES_LOG "priceslog")

(defn prices-to-log-entry
  [isbn prices]
  {:timestamp (time/now) :prices prices :isbn isbn})

(defn save-prices-log
  [entry]
  (mc/insert PRICES_LOG entry))

(defn get-stored-price
  [isbn]
  (first (mq/with-collection
           PRICES_LOG
           (mq/find { :isbn isbn :timestamp {"$gt" (utils/twenty-four-hours-ago)} })
           (mq/sort { :timestamp -1 })
           (mq/limit 1))))
