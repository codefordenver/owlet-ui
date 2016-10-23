(ns owlet-ui.components.activity-thumbnail)

(defn activity-thumbnail [fields url]
  (let [image (get-in fields [:preview :sys :url])
        {:keys [title track-id]} fields]
    (fn []
      [:div.activity-thumbnail
       [:a {:href (str "#/tracks/" track-id "/" url)}
        [:h3 title]
        [:img {:src image :width "100%"}]]])))
