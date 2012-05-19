(ns isbnnetinclj.server
  (:gen-class)
  (:use noir.core)
  (:require [noir.server]
            [noir.request]
            [isbnnetinclj.models.requestlog :as requestlog]))

(noir.server/load-views "src/isbnnetinclj/views/")

(defpage isbn-page  "/:isbn"
  {:keys [isbn]}
  (requestlog/add-log (dissoc (noir.request/ring-request) :body))
  (str (noir.request/ring-request)))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (mongodb/init-db)
    (noir.server/start port {:mode mode
                        :ns 'isbnnetinclj})))
