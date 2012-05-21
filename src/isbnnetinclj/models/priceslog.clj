(ns isbnnetinclj.models.priceslog
  [:require [isbnnetinclj.models.mongodb]
   [monger.collection :as mc]
   [monger.joda-time]])

(def PRICES_LOG "priceslog")

(defn add-prices
  "TODO Store under :latest key and current timestamp keys"
  [isbn prices]
  (mc/insert PRICES_LOG {:timestamp (java.util.Date.) :prices prices :isbn isbn}))

(defn get-prices
  [isbn]
  (mc/find-one-as-map PRICES_LOG {:isbn isbn}))
