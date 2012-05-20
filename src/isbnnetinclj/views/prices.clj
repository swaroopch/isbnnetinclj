(ns isbnnetinclj.views.prices
  (:require [isbnnetinclj.views.common :as common]
            [noir.request]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.models.requestlog :as requestlog]
            [isbnnetinclj.models.priceslog :as priceslog])
  (:use [noir.core]
        [hiccup.core]))

(defpartial format-price-store
    [[store price]]
  [:li [:strong store] [:span " : "] [:span.amount price]])

(defpartial format-prices
  [prices]
  [:ul#prices (map format-price-store prices)])

(defpage isbn-page  "/:isbn"
  {:keys [isbn]}
  (let [prices (stores/sorted-search-all isbn)
        request_to_save (dissoc (noir.request/ring-request) :body)]
    (println (format "ISBN %s : prices are %s" isbn prices))
    (requestlog/add-log request_to_save)
    (priceslog/add-prices isbn prices)
    (common/layout (format-prices prices))))
