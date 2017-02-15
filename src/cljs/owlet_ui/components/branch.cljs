(ns owlet-ui.components.branch
  (:require [re-frame.core :as re]
            [clojure.string :as str]
            [reagent.core :as reagent :refer [atom]]
            [camel-snake-kebab.core :refer [->kebab-case]]))


(defn branch [[color branch-name] branch-key]
  (let [lines       (str/split (str/upper-case branch-name) " ")
        name-line1  (first lines)
        name-line2  (rest lines)
        hover-image (reagent/atom "")
        set-hover!  (fn [images]
                      ; Changes hover-image to a new, random URL, if avail.
                      (reset! hover-image
                              (-> images
                                  (disj @hover-image)
                                  seq
                                  rand-nth
                                  (or @hover-image))))
        activities-by-branch
                    (re/subscribe [:activities-by-branch])]
    (fn []
      ; Form-2 component needed so hover-image atom is not redefined on
      ; every render.
      (let [counter        (-> @activities-by-branch
                               branch-key
                               :count)
            preview-images (-> @activities-by-branch
                               branch-key
                               :preview-urls
                               set)]
        [:div.branchwrapper.col-xs-12.col-md-6.col-lg-4
         [:div.branchwrap {:style {:background-image (str "url('"
                                                          @hover-image
                                                          "')")}
                           :on-mouse-enter #(set-hover! preview-images)}
          [:div.branch-bg.box-shadow
           {:style {:background-color color
                    :background-image (str "linear-gradient(to right, "
                                           color
                                           " 25%, rgba(0,0,0,0) 75%")}}
           [:a.branch {:on-click #(re/dispatch-sync
                                   [:set-activities-by-branch-in-view
                                    (->kebab-case branch-name)])
                       :href     (->kebab-case branch-name)}
            [:h2 [:mark name-line1]
             (when (<= 1 (count name-line2))
               [:span
                [:br]
                [:mark (str/join " " name-line2)]])]
            [:div.counter
             [:p counter]]]]]]))))
