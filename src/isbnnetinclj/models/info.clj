(ns isbnnetinclj.models.info
  (:require [isbnnetinclj.models.stores :as stores]
            [net.cgrand.enlive-html :as html]))

(defn flipkart-book-url
  [isbn]
  (format (get-in stores/sites [:flipkart :url]) isbn))

(defn flipkart-page-content
  [isbn]
  (stores/fetch-url (flipkart-book-url isbn)))

(defn flipkart-image
  [content]
  (get-in (first (html/select content [:div#main-image-id :img#visible-image-small])) [:attrs :src]))

(defn flipkart-row
  [content row-number]
  (first (html/select content [:div#details
                               :table.fk-specs-type1
                               [:tr (html/nth-of-type row-number)]
                               [:td (html/nth-of-type 2)]
                               first
                               html/content])))

(defn flipkart-details
  [content]
  (let [book-row (partial flipkart-row content)]
    {:image (flipkart-image content)     
     :title (book-row 1)
     :author (book-row 2)
     :publishing-date (book-row 6)
     :publisher (book-row 7)
     }))

(defn flipkart-book-info
  [isbn]
  (try
    (flipkart-details (flipkart-page-content isbn))
    (catch Exception _)))
