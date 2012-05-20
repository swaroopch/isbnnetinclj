(ns isbnnetinclj.views.prices
  (:require [isbnnetinclj.views.common :as common]
            [noir.server]
            [noir.request]
            [isbnnetinclj.models.mongodb :as mongodb]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.models.requestlog :as requestlog]
            [isbnnetinclj.models.priceslog :as priceslog])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage isbn-page  "/:isbn"
  {:keys [isbn]}
  (let [prices (stores/sorted-search-all isbn)]
    (println (format "ISBN %s : prices are %s" isbn prices))
    (requestlog/add-log (dissoc (noir.request/ring-request) :body))
    (priceslog/add-prices isbn prices)
    prices))

(defn format-prices
  [prices]
  )
