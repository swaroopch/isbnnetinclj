(ns isbnnetinclj.middleware.block
  (:require [timbre.core :as log]
            [noir.core :refer :all]))


(def bots
  '(#"kitabcompare"))


(defn should-block?
  [user-agent]
  (some #(re-find % user-agent) bots))


;; http://webnoir.org/tutorials/middleware
(defn block-bots
  [handler]
  (fn [request]
    (let [user-agent (get-in request [:headers "user-agent"])]
      (if (should-block? user-agent)
        nil
        (handler request)))))
