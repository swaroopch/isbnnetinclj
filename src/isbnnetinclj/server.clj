(ns isbnnetinclj.server
  (:use noir.core)
  (:require [noir.server :as server]))

(server/load-views "src/isbnnetinclj/views/")

(defpage "/:isbn"
  {:keys [isbn]}
  (str "ISBN:" isbn))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'isbnnetinclj})))

