(ns isbnnetinclj.server
  (:gen-class)
  (:require [timbre.core :as log]
            [noir.server]
            [isbnnetinclj.models.mongodb :as mongodb]
            [isbnnetinclj.middleware.block :refer [block-bots]]))


(noir.server/add-middleware block-bots)
(noir.server/load-views "src/isbnnetinclj/views/")


(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (log/info "Connecting to" (mongodb/db-uri))
    (mongodb/init-db)
    (noir.server/start port {:mode mode :ns 'isbnnetinclj})))
