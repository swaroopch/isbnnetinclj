(ns isbnnetinclj.views.book
  (:require [clojure.string :as string]
            [noir.request]
            [stencil.core :as mus]
            [monger.collection :as mc]
            [isbnnetinclj.utils :as utils])
  (:use [noir.core]
        [isbnnetinclj.models.info :only [book-info]]
        [isbnnetinclj.models.stores :only [book-data]]))


(def request-collection "request")

(defn core-details-of-request
  [request]
  {:ip (get-in request [:headers :x-forwarded-for])
   :user-agent (get-in request [:headers :user-agent])
   :isbn (string/replace (get request :uri) "/" "")
   :when (java.util.Date.)})


(defpage "/:isbn" {:keys [isbn]}
  (let [prices-log (book-data isbn)
        info (book-info isbn)]
    (mc/insert request-collection (core-details-of-request (noir.request/ring-request)))
    (mus/render-file "book.mustache" {:prices (:prices prices-log)
                                      :when-prices (utils/format-timestamp (:timestamp prices-log))
                                      :isbn isbn
                                      :info (:info info)
                                      :title (or (get-in book-info [:info :title]) "isbn.net.in")})))
