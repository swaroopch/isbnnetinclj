(ns isbnnetinclj.models.priceslog
  [:require [isbnnetinclj.models.mongodb]
   [monger.collection :as mc]])

(def PRICES_LOG "priceslog")

(defn add-prices
  "TODO Store under :latest key and current timestamp keys"
  [prices]
  (mc/insert PRICES_LOG (assoc prices :timestamp (java.util.Date.))))

(defn get-prices
  [isbn]
  (mc/find-one-as-map PRICES_LOG {:isbn isbn}))
