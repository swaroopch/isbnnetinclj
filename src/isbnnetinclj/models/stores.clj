(ns isbnnetinclj.models.stores
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]
            [timbre.core :as log]
            [isbnnetinclj.utils :as utils]
            [isbnnetinclj.models.priceslog :as priceslog]))

; http://enlive.cgrand.net/syntax.html
(def sites
  {:flipkart {:url "http://www.flipkart.com/search.php?query=%s"
              :price-path [:span#fk-mprod-our-id html/content]}
   :homeshop18 {:url "http://www.homeshop18.com/search:%s/categoryid:10000"
                :price-path [:span#productLayoutForm:OurPrice html/text]}
   :infibeam {:url "http://www.infibeam.com/Books/search?q=%s"
              :price-path [:span.infiPrice html/text]}})

(defn book-store-url
  [store-name isbn]
  (format (get-in sites [store-name :url]) isbn))

(defn parse-price-from-page
  [content path]
  (let [nodes (html/select content path)
        text (last nodes)]
    (if (empty? text) (str "not available")
      (try (Float/valueOf (last (re-seq #"\d+(?:\.\d+)?" (string/trim (string/replace (str text) "," "")))))
           (catch Exception x (str x))))))

(defn fetch-price-from-store
  [isbn {:keys [url price-path]}]
  (let [url (format url isbn)
        content (utils/fetch-url url)]
    (try (parse-price-from-page content price-path)
         (catch Exception x (str x)))))

(defn price-entry-for-store
  [isbn [name data]]
  {:name name
   :url (book-store-url name isbn)
   :price (fetch-price-from-store isbn (name sites))})

(defn fetch-prices-from-all
  [isbn]
  (sort-by :price (map (partial price-entry-for-store isbn) sites)))

(defn prices-for-isbn
  [isbn]
  (let [stored-price
        (priceslog/get-stored-price isbn)]
    (if-not (nil? stored-price)
      (do (log/info (str "Using already stored price for " isbn))
          stored-price)
      (do (log/info (str "Fetching prices for " isbn))
          (future (let [prices-for-isbn
                        (priceslog/prices-to-log-entry isbn (fetch-prices-from-all isbn))]
                    (priceslog/save-prices-log prices-for-isbn)
                    prices-for-isbn))
          {}))))
