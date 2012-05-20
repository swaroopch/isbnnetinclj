(ns isbnnetinclj.server
  (:gen-class)
  (:require [noir.server]
            [isbnnetinclj.models.mongodb :as mongodb]
            [hiccup.bootstrap.middleware])
  (:use [noir.core]
         [isbnnetinclj.views.book]))

(noir.server/load-views "src/isbnnetinclj/views/")
(noir.server/add-middleware hiccup.bootstrap.middleware/wrap-bootstrap-resources)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (println (str "Connecting to " (mongodb/db-uri)))
    (mongodb/init-db)
    (noir.server/start port {:mode mode
                        :ns 'isbnnetinclj})))
