(ns isbnnetinclj.models.info
  (:require [net.cgrand.enlive-html :as html]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.models.infolog :as infolog]
            [isbnnetinclj.utils :as utils]))

(defn flipkart-page-content
  [isbn]
  (utils/fetch-url (format (get-in stores/sites [:flipkart :url]) isbn)))

(defn flipkart-image
  [content]
  (or (get-in (first (html/select content [:div#main-image-id :img#visible-image-small])) [:attrs :src])
      (get-in (first (html/select content [:div#mprodimg-id :img])) [:attrs :src])))

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

(defn get-book-info
  [isbn]
  (let [stored-info (infolog/get-stored-info isbn)]
    (if stored-info stored-info
        (do (let [new-info (flipkart-book-info isbn)
                  new-info-entry (infolog/info-to-log-entry isbn new-info)]
              (infolog/save-info-log new-info-entry)
              new-info-entry)))))
