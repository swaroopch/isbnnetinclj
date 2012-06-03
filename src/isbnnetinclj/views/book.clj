(ns isbnnetinclj.views.book
  (:require [timbre.core :as log]
            [clojure.string :as string]
            [noir.request]
            [stencil.core :as mus]
            [monger.collection :as mc]
            [isbnnetinclj.utils :as utils])
  (:use [noir.core]
        [isbnnetinclj.models.info :only [book-info]]
        [isbnnetinclj.models.stores :only [book-data sites]]
        [clojure.pprint :only [pprint]]))


(def request-collection "request")

(defn core-details-of-request
  "NOTE This function should change if deployed outside Heroku"
  [request]
  {:ip (get-in request [:headers "x-forwarded-for"])
   :user-agent (get-in request [:headers "user-agent"])
   :isbn (string/replace (:uri request) "/" "")
   :when (java.util.Date.)})


(defn convert-prices-for-display
  [isbn prices]
  (map (fn [[store-name price]]
         {:name (name store-name)
          :price price
          :url (format (get-in sites [store-name :url]) isbn)}) prices))


(defn is-isbn-valid
  [isbn]
  (or (re-matches #"^[0-9]{9}[0-9xX]$" isbn)
      (re-matches #"^[0-9]{13}$" isbn)))


(defpage book-page [:get ["/:isbn" :isbn #"[\d-]+[xX]?"]] {:keys [isbn]}
  (let [isbn (string/replace isbn "-" "")]
    (if (is-isbn-valid isbn)
      ;; NOTE Launching price fetchers in background before starting info fetcher
      (let [data (book-data isbn)
            info (book-info isbn)]
        (mc/insert request-collection (core-details-of-request (noir.request/ring-request)))
        (mus/render-file "book.mustache" {:prices (convert-prices-for-display isbn (:price data))
                                          :when-prices (utils/format-timestamp (or (:when data) (java.util.Date.)))
                                          :isbn isbn
                                          :info (:info info)
                                 :title (or (get-in info [:info :title]) "isbn.net.in")})))))
