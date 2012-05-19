(defproject isbnnetinclj "0.1.0-SNAPSHOT"
            :description "A quick way to find the online prices for a book in India"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [org.clojure/data.json "0.1.3"]
                           [noir "1.3.0-beta7"]
                           [hiccup "1.0.0"]
                           [com.novemberain/monger "1.0.0-beta6"]
                           [joda-time "2.1"]
                           [enlive "1.0.0"]]
            :main isbnnetinclj.server)
