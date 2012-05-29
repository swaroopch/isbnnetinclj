(ns isbnnetinclj.views.book
  (:require [noir.request]
            [stencil.core :as mus]
            [isbnnetinclj.models.requestlog :as requestlog]
            [isbnnetinclj.utils :as utils])
  (:use [noir.core]
        [isbnnetinclj.models.info :only [get-book-info]]
        [isbnnetinclj.models.stores :only [prices-for-isbn]]))

(defpage "/:isbn" {:keys [isbn]}
  (let [prices-log (prices-for-isbn isbn)
        request-to-save (dissoc (noir.request/ring-request) :body)
        book-info (get-book-info isbn)]
    (requestlog/save-request-log-entry request-to-save)
    (mus/render-file "book.mustache" {:prices (:prices prices-log)
                                      :when-prices (utils/format-timestamp (:timestamp prices-log))
                                      :isbn isbn
                                      :info (:info book-info)
                                      :title (or (get-in book-info [:info :title]) "isbn.net.in")})))
