(ns isbnnetinclj.server
  (:gen-class)
  (:require [isbnnetinclj.models.mongodb :as mongodb])
  (:use [noir.core]
         [isbnnetinclj.views.prices]))

(noir.server/load-views "src/isbnnetinclj/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (println (str "Connecting to " (mongodb/db-uri)))
    (mongodb/init-db)
    (noir.server/start port {:mode mode
                        :ns 'isbnnetinclj})))
