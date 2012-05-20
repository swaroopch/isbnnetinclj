(ns isbnnetinclj.models.stores
  [:require [net.cgrand.enlive-html :as html]
   [clojure.string :as string]])

; http://enlive.cgrand.net/syntax.html
(def sites
  {:flipkart
   {:url "http://www.flipkart.com/search.php?query=%s"
    :price-path [:span#fk-mprod-our-id html/content]}
   :infibeam
   {:url "http://www.infibeam.com/Books/search?q=%s"
    :price-path [:span.infiPrice html/text]}
   :homeshop18
   {:url "http://www.homeshop18.com/search:%s/categoryid:10000"
    :price-path [:span#productLayoutForm:OurPrice html/text]}
   })

(defn fetch-url
  [url]
  (html/html-resource (java.net.URL. url)))

(defn find-price-at-end
  [text]
  (if (empty? text) (str "not available")
      (try (Float/valueOf (last (re-seq #"\d+(?:\.\d+)?" (string/trim (string/replace (str text) "," "")))))
           (catch Exception x (str x)))))

(defn parse-page
  [content path]
  (let [nodes (html/select content path)
        node (last nodes)]
    (find-price-at-end node)))

(defn search
  [isbn {:keys [url price-path]}]
  (let [url (format url isbn)
        content (fetch-url url)]
    (try (parse-page content price-path)
         (catch Exception x (str x)))))

(defn search-all
  [isbn]
  (zipmap
   (keys sites)
   (map (partial search isbn) (vals sites))))

(defn sorted-search-all
  [isbn]
  (sort-by val (search-all isbn)))
