(ns isbnnetinclj.views.frontpage
  (:use [noir.core])
  (:require [isbnnetinclj.views.common :as common]))

(defpartial front-page-content
    []
  [:h1 "Front page"])

(defpage front-page "/"
  [& args]
  (common/layout (front-page-content)))

(defpage front-page-alt "/front"
  [& args]
  "FIXME Remove this : For debugging only, for some reason, on my Mac, my localhost:8080 redirects to a fixed website"
  (front-page args))
