(ns isbnnetinclj.views.flush
  (:require [timbre.core :as log]
            [noir.core :refer :all]
            [isbnnetinclj.models.info :refer [book-info-cache]]
            [isbnnetinclj.models.stores :refer [book-data-cache book-in-progress-lock]]
            [isbnnetinclj.views.stats :refer [book-stats-cache]]))


(defpage "/flush/:password" {:keys [password]}
  (if (= (System/getenv "FLUSH_PASSWORD") password)
    (do
      (reset! book-info-cache {})
      (reset! book-data-cache {})
      (reset! book-in-progress-lock {})
      (reset! book-stats-cache {})
      "Flushed!")
    "You don't have permission"))
