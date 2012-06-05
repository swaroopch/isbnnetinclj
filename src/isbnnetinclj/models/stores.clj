(ns isbnnetinclj.models.stores
  (:require [timbre.core :as log]
            [clojure.string :as string]            
            [clojure.core.cache :as cache]
            [net.cgrand.enlive-html :as html]
            [monger.collection :as mc]
            [monger.joda-time]
            [clj-time.core :as time]
            [isbnnetinclj.utils :as utils]))


;; http://enlive.cgrand.net/syntax.html
;; NOTE about other stores:
;; - Pustak.co.in is slow
;; - LandmarkOnTheNet.com times out
;; - Uread.com times out
(def sites
  {:flipkart {:url "http://www.flipkart.com/search.php?query=%s&affid=INSwaroCom"
              :price-path [:span#fk-mprod-our-id html/content]}
   :homeshop18 {:url "http://www.homeshop18.com/search:%s/categoryid:10000"
                :price-path [:span#productLayoutForm:OurPrice html/text]}
   :infibeam {:url "http://www.infibeam.com/Books/search?q=%s"
              :price-path [:span.infiPrice html/text]}
   :indiaplaza {:url "http://www.indiaplaza.com/searchproducts.aspx?sn=books&affid=110550&q=%s"
                :price-path [:div.ourPrice :span.blueFont html/text]}
   :pustak {:url "http://pustak.co.in/pustak/books/search?searchType=book&q=%s&page=1&type=genericSearch"
            :price-path [:span.prod_pg_prc_font html/text]}
   :crossword {:url "http://www.crossword.in/books/search?q=%s"
               :price-path [:span.variant-final-price html/text]}})


(defonce book-data-cache (atom (cache/ttl-cache-factory (* 60 60 24) {})))
(defonce book-in-progress-lock (atom {}))
(def book-data-collection "book_data")


(defn get-book-in-progress
  [isbn]
  (get @book-in-progress-lock isbn))


(defn set-book-in-progress
  [isbn]
  (swap! book-in-progress-lock assoc isbn true))


(defn done-book-in-progress
  [isbn]
  (swap! book-in-progress-lock dissoc isbn))


(defn get-in-memory-book-data
  [isbn]
  (get @book-data-cache isbn))


(defn store-in-memory-book-data
  [isbn book-data]
  (get (swap! book-data-cache assoc isbn book-data) isbn))


(defn get-db-book-data
  [isbn]
  (utils/get-fresh-db-data book-data-collection isbn))


(defn get-stored-book-data
  [isbn]
  (or (get-in-memory-book-data isbn)
      (get-db-book-data isbn)))


(defn parse-price-from-content
  [content path]
  (let [nodes (html/select content path)
        text (last nodes)]
    (if (empty? text)
      (Integer/MAX_VALUE)
      (try (Float/parseFloat (last (re-seq #"\d+(?:\.\d+)?" (string/trim (string/replace (str text) "," "")))))
           (catch Exception x (do (log/error (str x)) (str x)))))))


(defn fetch-book-data-from-one-store
  [isbn [site-name {:keys [url price-path]}]]
  (log/debug isbn "Launching fetcher" site-name)
  (try
    (let [address (format url isbn)
          content (utils/fetch-page address)
          price-data (parse-price-from-content content price-path)]
      (log/debug isbn "Finished fetching" site-name)
      (swap! book-data-cache assoc-in [isbn :price site-name] price-data))
    (catch Exception x (do
                         (log/error isbn (str x))
                         (swap! book-data-cache assoc-in [isbn :price site-name] (Integer/MAX_VALUE))))))


(defn fetch-book-data
  {:test #(assert (= {:price {:flipkart 2011.0
                              :homeshop18 1542.0
                              :infibeam 1959.0
                              :indiaplaza 1856.0
                              :pustak 1299.0
                              :crossword 1926.0}}
                     (dissoc (fetch-book-data "9781449394707") :when :isbn)))}
  [isbn]
  (if-not (get-book-in-progress isbn)
    (do
      (set-book-in-progress isbn)
      (log/debug isbn "Launching fetchers")
      (doseq [f (mapv #(future (fetch-book-data-from-one-store isbn %)) sites)]
        (deref f))
      (swap! book-data-cache assoc-in [isbn :when] (java.util.Date.))
      ;; There is a race condition here, but for this project, it is not critical.
      (done-book-in-progress isbn)
      (log/debug isbn "Done")
      (let [data (get-in-memory-book-data isbn)
            data (assoc data :isbn isbn)]
        (future (mc/insert book-data-collection data))
        data))
    (do
      (log/debug isbn "already in progress")
      nil)))


(defn book-data
  [isbn]
  (or (get-stored-book-data isbn)
      (do (future (fetch-book-data isbn)) {:when (java.util.Date.)})))
