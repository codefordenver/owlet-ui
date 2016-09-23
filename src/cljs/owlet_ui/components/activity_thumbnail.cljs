(ns owlet-ui.components.activity-thumbnail)

(defn activity-thumbnail [fields]
  (let [image (get-in fields [:preview :sys :url])
        {:keys [title track-id]} fields]
    (fn []
      [:div
       [:a {:href (str "#/track/" track-id "/" title)}
        [:h3 title]
        [:img {:src image :width "40%"}]]])))