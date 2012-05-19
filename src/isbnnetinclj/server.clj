(ns isbnnetinclj.server
  (:gen-class)
  (:use noir.core)
  (:require [noir.server]
            [noir.request]
            [isbnnetinclj.models.mongodb :as mongodb]
            [isbnnetinclj.models.stores :as stores]
            [isbnnetinclj.models.requestlog :as requestlog]
            [isbnnetinclj.models.priceslog :as priceslog]))

(noir.server/load-views "src/isbnnetinclj/views/")

(defpage isbn-page  "/:isbn"
  {:keys [isbn]}
  (let [prices (stores/sorted-search-all isbn)]
    (requestlog/add-log (dissoc (noir.request/ring-request) :body))
    (priceslog/add-prices prices)
    prices))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (mongodb/init-db)
    (noir.server/start port {:mode mode
                        :ns 'isbnnetinclj})))
