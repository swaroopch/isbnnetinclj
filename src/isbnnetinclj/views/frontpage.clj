(ns isbnnetinclj.views.frontpage
  (:use [noir.core]
        [isbnnetinclj.models.stores :only [sites]])
  (:require [isbnnetinclj.views.common :as common]))

(defn store-names
    []
  (sort (map #(clojure.string/replace (str %1) ":" "") (keys sites))))

(defpartial store-list-entry
    [name]
  [:li name])

(defpartial stores-list
    []
  [:ul
   (map store-list-entry (store-names))]
  )

(defpartial front-page-content
    []
  [:div.page-header
   [:h1#heading "isbn.net.in"]
   [:h2#subheading "A quick way to find the online prices for a book in India"]]
  [:div.marketing
   [:dl
    [:dt "How to use"]
    [:dd [:p "Suffix this website address with a "
          [:a {:href "http://en.wikipedia.org/wiki/ISBN"} "ISBN"]
          " or "
          [:a {:href "http://en.wikipedia.org/wiki/European_Article_Number"} "EAN"]
          ". That's it."
          [:br]
          "Example: "
          [:a {:href "/9781449394707"} "http://isbn.net.in/9781449394707"]]]
    [:dt "But I want to search for the book, I don't have an ISBN number..."]
    [:dd [:p "When I created this, I had an explicit goal of " [:em "not"]
          " creating another search engine, I only wanted to compare prices for a book I already wanted to buy."
          [:br]
          "Use "
          [:a {:href "http://www.mysmartprice.com/book/"} "MySmartPrice"]
          " or "
          [:a {:href "http://www.mydiscountbay.com/"} "MyDiscountBay"]
          " or "
          [:a {:href "http://www.indiabookstore.net/"} "IndiaBookStore"]
          " or "
          [:a {:href "http://thisyathat.com/"} "ThisYaThat"]
          " instead."]]
    [:dt "So why isbn.net.in?"]
    [:dd [:p "Because I built this before there were any good price comparison shops for India (like the list above). In fact, isbn.net.in inspired the "
          [:a {:href "http://mysmartprice.com/blog/2010/08/the-inspirations-of-this-price-search-engine/"} "creation of some of the sites listed above."]
          [:br]
          "Besides, I built this for myself and for other book lovers. I did not build it for commercial purposes, so I have no vested interest in you using this site other than the reason of delight of using it."]]
    [:dt "Which stores do you search?"]
    [:dd (stores-list) [:p "Note that I've had to remove " [:a {:href "https://twitter.com/#!/swaroopch/status/157432515274096641"} "some stores"] " because isbn.net.in was sending them \"too much traffic\"."]]
    [:dt "News"]
    [:dd [:p "Read "
          [:a {:href "http://www.swaroopch.com/blog/tag/isbnnetin/"} "latest updates on my blog."]]]
    [:dt "Feedback from Users"]
    [:dd [:blockquote "Thanks very much for the books search site http://isbn.net.in , I just saved Rs 1000 by searching for the best price !!" " -- " [:a {:href "http://twitter.com/vpsingh/status/13225509113"} "Praveen Singh"]]
     [:blockquote "great spiffy product :) " " -- " [:a {:href "http://twitter.com/allwinagnel/statuses/13076826472"} "Allwin, founder of PagalGuy.com"]]
     [:blockquote "isbn.net.in saves a ton of time when trying to buy books online. Indian e commerce needs more of them. Thank you @swaroopch." " -- " [:a {:href "http://twitter.com/shashanknd/status/24654201316"} "Shashank ND"]]
     [:blockquote "isbn.net.in is awesome #recommended #ftw #awesomeness" " -- " [:a {:href "http://twitter.com/saurabh/statuses/10530236565"} "Saurabh Garg"]]
     [:blockquote "isbn.net.in is actually quite bloody awesome!" " -- " [:a {:href "http://twitter.com/kranium256/statuses/10316468429"} "Kartikay Sahay"]]
     [:blockquote "Excellent! I'll use it every time I need to buy a book" " -- " [:a {:href "http://www.swaroopch.com/blog/india-book-price-comparison/#comment-131736"} "Sharath M S"]]
     [:blockquote "Super App. Try http://isbn.net.in to compare book prices on different online books portals." " -- " [:a {:href "http://twitter.com/s4sukhdeep/statuses/14210750762"} "SukhDeep Singh"]]
     [:blockquote "Got to love isbn.net.in, Finds best prices on books over multiple online stores." " -- " [:a {:href "http://twitter.com/vishalmanohar/status/28970031668"} "Vishal Manohar"]]
     [:blockquote "My sis and her friends are planning to search for their next sem books through your isbn.net.in site and then buy from the best source." " -- " "Azmi Ahmad"]
     [:blockquote "Awesome program. Before buying a book online I first check the price on isbn.net.in. You keep adding new sites which is also good." " -- " "Kishore"]
     [:blockquote "Thanks for writing a very useful tool (isbn.net.in), and making it so simple and user friendly. Kudos!" " -- " "Praveen"]
     [:blockquote "http://isbn.net.in plays a huge role when buying books, thank you @swaroopch." " -- " [:a {:href "https://twitter.com/#!/harikt/status/172241899833405440"} "Hari K T"]]
     [:blockquote "I was using \"turrg.com\" but \"isbn.net.in\" rocks! Simply superb. @swaroopch" " -- " [:a {:href "https://twitter.com/#!/parin_mscit/status/172740093562466305"} "Parin Shah"]]
     [:blockquote "Thejesh talks about " [:a {:href "http://thejeshgn.com/2011/04/13/using-barcode-scanner-for-buying-books-in-india/"} "using a Barcode scanner on Android and looking up the price of a book via isbn.net.in"]]
     [:blockquote "geek-cetas has written a " [:a {:href "https://github.com/geek-cetas/node.isbn.net.in"} "node.js api for isbn.net.in"]]
     [:blockquote "Ved Antani has reused the source code behind isbn.net.in to create " [:a {:href "http://cheapr.me/"} "cheapr.me"] " (see " [:a {:href "http://cheapr.me/about"} "their About page"] ")"]
     [:blockquote "Tejinder Singh Mehta has released a voice application using isbn.net.in - try calling 011-66020230"]
     [:blockquote "Pritesh Jain has reused the source code behind isbn.net.in to create " [:a {:href "http://price-checker.in/"} "price-checker.in"] " and it's companion " [:a {:href "https://play.google.com/store/apps/details?id=org.priteshjain.PriceChecker"} "Android app"]]
     [:blockquote "Manu J has reused the source code behind isbn.net.in to create " [:a {:href "http://isbn.startupgang.com/"} "isbn.startupgang.com"] " to show prices from each store as soon as possible. (See tweets " [:a {:href "https://twitter.com/#!/_manu_j/status/199823016048336896"} "1"] ", " [:a {:href "https://twitter.com/#!/_manu_j/status/199823019416363009"} "2"] ", " [:a {:href "https://twitter.com/#!/_manu_j/status/199823442189627392"} "3"] " for details)"]]]])

(defpage front-page "/"
  [& args]
  (common/layout (front-page-content)))

(defpage front-page-alt "/front"
  [& args]
  "FIXME Remove this : For debugging only, for some reason, on my Mac, my localhost:8080 redirects to a fixed website"
  (front-page args))
