(ns isbnnetinclj.views.frontpage
  (:use [noir.core]
        [isbnnetinclj.models.stores :only [sites]])
  (:require [stencil.core :as mus]))


(defn store-names
  []
  (sort (map name (keys sites))))

(defn front-page-content
  []
  (mus/render-file "frontpage" {:title "isbn.net.in" :stores (store-names)}))


(defpage front-page "/"
  [& args]
  (front-page-content))

;; FIXME Remove this : For debugging only, for some reason, on my Mac, my localhost:8080 redirects to a fixed website
(defpage front-page-alt "/front"
  [& args]
  (front-page-content))
