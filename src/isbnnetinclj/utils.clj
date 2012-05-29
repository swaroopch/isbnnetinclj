(ns isbnnetinclj.utils
  (:require [net.cgrand.enlive-html :as html]
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
