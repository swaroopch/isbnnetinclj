(ns isbnnetinclj.views.stats
  (:require [timbre.core :as log]
            [clojure.core.cache :as cache]
            [noir.core :refer :all]
            [stencil.core :as mus]
            [monger.collection :as mc]
            [monger.query :as mq]
            [isbnnetinclj.models.constants :refer :all]
            [isbnnetinclj.models.info :refer [book-info]]
            [isbnnetinclj.utils :refer [twenty-four-hours-ago]]))


(defn raw-isbns-in-past-24-hours
  []
  (mq/with-collection request-collection
    (mq/find {:when {"$gt" (twenty-four-hours-ago)}})
    (mq/fields [:isbn])))


(defn isbns-in-past-24-hours
  []
  (->> (raw-isbns-in-past-24-hours)
       (remove #(re-find #".json$" (:isbn %)))
       (map :isbn)
       (frequencies)
       (sort-by val)
       (reverse)
       (take 20)
       (remove #(= "9781449394707" (key %))))) ; remove the example isbn on the front page


(defn get-db-books-in-past-24-hours
  []
  (map
   #(conj (hash-map :isbn (first %)) (:info (book-info (first %))))
   (isbns-in-past-24-hours)))


(defonce book-stats-cache (atom (cache/ttl-cache-factory (* 60 60) {})))


(defn books-in-past-24-hours
  []
  (let [stats-in-memory-already (get @book-stats-cache :latest)]
    (if-not stats-in-memory-already
      (let [stats (get-db-books-in-past-24-hours)]
        (swap! book-stats-cache assoc :latest stats)
        stats)
      stats-in-memory-already)))


(defpage "/stats" []
  (mus/render-file "stats" {:books (books-in-past-24-hours)}))
