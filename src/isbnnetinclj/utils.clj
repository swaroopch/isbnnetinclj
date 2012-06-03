(ns isbnnetinclj.utils
  (:require [net.cgrand.enlive-html :as html]
            [monger.collection :as mc]
            [monger.query :as mq]
            [monger.joda-time]
            [clj-time.core :as time]))


(defn fetch-page
  [url]
  (html/html-resource (java.net.URL. url)))


(defn twenty-four-hours-ago
  []
  (time/minus (time/now) (time/days 1)))


(defn get-fresh-db-data
  [collection-name isbn]
  (first (mq/with-collection
           collection-name
           (mq/find {:isbn isbn :when {"$gt" (twenty-four-hours-ago)}})
           (mq/sort {:when -1})
           (mq/limit 1))))
