(ns isbnnetinclj.views.book
  (:require [noir.request]
            [stencil.core :as mus]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.models.requestlog :as requestlog]
            [isbnnetinclj.models.priceslog :as priceslog])
  (:use [noir.core]
        [isbnnetinclj.models.info :only [flipkart-book-info]]))

(defn prices-for-isbn
  [isbn]
  (let [stored-price
        (priceslog/get-stored-price isbn)]
    (if-not (nil? stored-price)
      stored-price
      (do (future (let [prices-for-isbn
            (priceslog/prices-to-log-entry isbn (stores/sorted-search-store-all isbn))]
        (priceslog/save-prices-log prices-for-isbn)
        prices-for-isbn)) {}))))

(defpage "/:isbn" {:keys [isbn]}
  (let [prices-log (prices-for-isbn isbn)
        request-to-save (dissoc (noir.request/ring-request) :body)
        book-info (flipkart-book-info isbn)]
    (requestlog/save-request-log-entry request-to-save)
    (println (format "ISBN %s : %s : prices are %s" isbn book-info prices-log))
    (mus/render-file "book.mustache" {:prices prices-log :isbn isbn :info book-info})))
