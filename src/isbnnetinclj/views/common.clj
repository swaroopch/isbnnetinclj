(ns isbnnetinclj.views.common
  (:use [noir.core]
        [hiccup.core]
        [hiccup.page]
        [hiccup.bootstrap.page]))

; http://twitter.github.com/bootstrap/scaffolding.html

(defpartial layout [& content]
            (html5
              [:head
               [:title "isbnnetinclj"]
               (include-bootstrap)
               [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]]
              [:body
               [:div#content.container-fluid
                content]]))
