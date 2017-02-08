(ns owlet-ui.components.activity.title
  (:require [re-frame.core :as rf]
            [owlet-ui.components.back :refer [back]]))

(defn activity-title [title author]
  [:div.activity-title-wrap
   [:h1 [back]
        [:mark.white.box-shadow title]]
   [:h5.author "Posted by: " author]])
