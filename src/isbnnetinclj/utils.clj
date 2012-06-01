(ns isbnnetinclj.utils
  (:require [net.cgrand.enlive-html :as html]
            [monger.collection :as mc]
            [monger.query :as mq]
            [monger.joda-time]
            [clj-time.core :as time]
            [clj-time.format :as timeformat]))

(defn fetch-url
  [url]
  (html/html-resource (java.net.URL. url)))

(defn twenty-four-hours-ago
  []
  (time/minus (time/now) (time/days 1)))

(defn format-timestamp
  [timestamp]
  (timeformat/unparse (timeformat/formatters :rfc822) timestamp))

(defn get-fresh-db-data
  [db_collection isbn]
  (first (mq/with-collection
           db_collection
           (mq/find {:isbn isbn :timestamp {"$gt" (twenty-four-hours-ago)}})
           (mq/sort {:timestamp -1})
           (mq/limit 1))))
