(ns isbnnetinclj.views.book
  (:require [isbnnetinclj.views.common :as common]
            [noir.request]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.models.requestlog :as requestlog]
            [isbnnetinclj.models.priceslog :as priceslog])
  (:use [noir.core]
        [hiccup.core]
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

(defpartial format-price-store
    [[store price]]
  [:li [:strong store] [:span " : "] [:span.amount price]])

(defpartial format-book-info
    [{:keys [image title author publishing-date publisher]}]
  [:div#book-info
   [:p#image [:img {:src image}]]
   [:p#title "Title: " title]
   [:p#author "Author: " author]
   [:p#publishing-date "Year: " publishing-date]
   [:p#publisher "Publisher: " publisher]
   ])

(defpartial format-prices-present
    [prices timestamp]
  [:ul#prices (map format-price-store prices)]
  [:p#when (str "Note: Prices as of " timestamp)])

(defpartial format-prices-not-present
    [& rest]
  [:p (str "Please refresh in 60 seconds.")])

(defn format-prices
    [{:keys [prices timestamp]}]
    (if-not (empty? prices)
      (format-prices-present prices timestamp)
      (format-prices-not-present)))

(defpage isbn-page  "/:isbn"
  {:keys [isbn]}
  (let [prices-log (prices-for-isbn isbn)
        request-to-save (dissoc (noir.request/ring-request) :body)
        book-info (flipkart-book-info isbn)]
    (requestlog/save-request-log-entry request-to-save)
    (println (format "ISBN %s : %s : prices are %s" isbn book-info prices-log))
    (common/layout (format-book-info book-info) (format-prices prices-log))))
