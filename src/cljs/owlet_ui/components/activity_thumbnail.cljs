(ns owlet-ui.components.activity-thumbnail)

; function camelize(str) {}
;  return str.replace(/(?:^\w|[A-Z]|\b\w)/g, function(letter, index) {
;    return index == 0 ? letter.toLowerCase() : letter.toUpperCase();
;  }).replace(/\s+/g, '');
; }

(defn camelize [string]
  (.replace
    (.replace string (js/RegExp. "/(?:^\\\w|[A-Z]|\b\\\w)/g") (fn [letter, index]
                                                                (if (= index 0)
                                                                  (.toLowerCase letter)
                                                                  (.toUpperCase letter)))) (js/RegExp. "/\s+/g") ""))

(defn activity-thumbnail [fields]
  (let [image (get-in fields [:preview :sys :url])
        {:keys [title track-id]} fields]
    (fn []
      [:div
       [:a {:href (str "#/tracks/" track-id "/" (camelize title))}
        [:h3 title]
        [:img {:src image :width "40%"}]]])))
