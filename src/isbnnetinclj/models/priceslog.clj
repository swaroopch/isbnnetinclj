(ns isbnnetinclj.models.priceslog
  (:require [isbnnetinclj.models.mongodb]
            [monger.collection :as mc]
            [monger.joda-time]
            [monger.query :as mq]
            [clj-time.core :as time]))

(def PRICES_LOG "priceslog")

(defn prices-to-log
  [isbn prices]
  {:timestamp (time/now) :prices prices :isbn isbn})

(defn add-prices
  "TODO Store under :latest key and current timestamp keys"
  [entry]
  (mc/insert PRICES_LOG entry))

(defn twenty-four-hours-ago
  []
  (time/minus (time/now) (time/days 1)))

(defn get-stored-price
  [isbn]
  (first (mq/with-collection
           PRICES_LOG
           (mq/find { :isbn isbn :timestamp {"$gt" (twenty-four-hours-ago)} })
           (mq/sort { :timestamp -1 })
           (mq/limit 1))))
