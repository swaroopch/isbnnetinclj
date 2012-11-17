(ns isbnnetinclj.models.info
  (:require [timbre.core :as log]
            [clojure.core.cache :as cache]
            [net.cgrand.enlive-html :as html]
            [monger.collection :as mc]
            [isbnnetinclj.models.constants :refer :all]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.utils :as utils]))


(defonce book-info-cache (atom (cache/ttl-cache-factory (* 60 60 24) {})))


(defn get-in-memory-book-info
  [isbn]
  (get @book-info-cache isbn))


(defn store-in-memory-book-info
  [isbn book-info]
  (get (swap! book-info-cache assoc isbn book-info) isbn))


(defn get-db-book-info
  [isbn]
  (utils/get-fresh-db-data book-info-collection isbn))


(defn get-stored-book-info
  [isbn]
  (or (get-in-memory-book-info isbn)
      (get-db-book-info isbn)))


(defn flipkart-page-content
  [isbn]
  (utils/fetch-page (format (get-in stores/sites [:flipkart :url]) isbn)))


(defn flipkart-image
  [content]
  (or (get-in (first (html/select content [:div#mprodimg-id :img])) [:attrs :data-src])
      (get-in (first (html/select content [:div#main-image-id :img#visible-image-small])) [:attrs :src])))


(defn flipkart-row-1
  [content row-number]
  (html/text (first (html/select content [:div#details
                                          :table.fk-specs-type1
                                          [:tr (html/nth-of-type row-number)]
                                          [:td (html/nth-of-type 2)]
                                          first
                                          html/text]))))


(defn flipkart-row-2
  [content row-number]
  (html/text (first (html/select content [:div#specifications
                                          :table.fk-specs-type2
                                          [:tr (html/nth-of-type row-number)]
                                          [:td (html/nth-of-type 2)]
                                          first]))))


(defn flipkart-details-1
  [isbn content]
  (let [book-row (partial flipkart-row-1 content)
        book-details {:isbn isbn
                      :when (java.util.Date.)
                      :info {:image (flipkart-image content)
                             :title (book-row 1)
                             :author (book-row 2)
                             :publishing-date (book-row 6)
                             :publisher (book-row 7)}}]
    (if (not (empty? (get-in book-details [:info :title])))
      book-details
      nil)))


(defn flipkart-details-2
  [isbn content]
  (let [book-row (partial flipkart-row-2 content)
        book-details {:isbn isbn
                      :when (java.util.Date.)
                      :info {:image (flipkart-image content)
                             :title (book-row 2)
                             :author (book-row 3)
                             :publishing-date (book-row 7)
                             :publisher (book-row 8)}}]
    (if (not (empty? (get-in book-details [:info :image])))
      book-details
      nil)))


(defn flipkart-details
  "Looks like there are two different page templates."
  [isbn content]
  (or (flipkart-details-1 isbn content)
      (flipkart-details-2 isbn content)))


(defn fetch-book-info
  [isbn]
  (log/debug isbn "Fetching info")
  (try
    (let [info (flipkart-details isbn (flipkart-page-content isbn))]
      (log/debug isbn "is" (get-in info [:info :title]) "by" (get-in info [:info :author]))
      (store-in-memory-book-info isbn info)
      (future (mc/insert book-info-collection info))
      info)
    (catch Exception x (do (log/error isbn (str x) nil)))))


(defn book-info
  [isbn]
  (or (get-stored-book-info isbn)
      (fetch-book-info isbn)))
