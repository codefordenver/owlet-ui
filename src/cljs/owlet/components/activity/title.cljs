(ns owlet.components.activity.title
  (:require [re-frame.core :as rf]
            [owlet.components.back :refer [back]]))

(defn activity-title [title author]
  [:div.activity-title-wrap
   [:h1 [:mark.white.box-shadow [back] title]]
   [:h5.author "Posted by: " author]])
