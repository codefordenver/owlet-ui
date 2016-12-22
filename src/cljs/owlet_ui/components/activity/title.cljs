(ns owlet-ui.components.activity.title)

(defn activity-title [activity-data]
  (fn []
    (let [title (get-in @activity-data [:fields :title])
          author (get-in @activity-data [:fields :author])]
      [:div.activity-title-wrap
       [:h1 [:mark.white.box-shadow title]]
       [:p.author [:mark.black.box-shadow "created by: " author]]])))
