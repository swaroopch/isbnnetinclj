(ns isbnnetinclj.views.book
  (:require [isbnnetinclj.views.common :as common]
            [noir.request]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.models.requestlog :as requestlog]
            [isbnnetinclj.models.priceslog :as priceslog])
  (:use [noir.core]
        [hiccup.core]
        [isbnnetinclj.models.info :only [flipkart-book-info]]))

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

(defpartial format-prices
  [prices]
  [:ul#prices (map format-price-store prices)])

(defpage isbn-page  "/:isbn"
  {:keys [isbn]}
  (let [prices (stores/sorted-search-all isbn)
        request-to-save (dissoc (noir.request/ring-request) :body)
        book-info (flipkart-book-info isbn)]
    (println (format "ISBN %s : %s : prices are %s" isbn book-info prices))
    (requestlog/add-log request-to-save)
    (priceslog/add-prices isbn prices)
    (common/layout (format-book-info book-info) (format-prices prices))))
