(ns owlet-ui.components.upload-image-modal
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]
    [owlet-ui.firebase :as fb]
    [re-com.core :refer [v-box h-box modal-panel button alert-box progress-bar]]))


(defn upload-form
  []
  (let [upload-error (r/atom nil)
        progress-pct (r/atom 0)]
    (fn []
      [:form
       [:b "Upload a background image file"]
       [:br]
       [:br]
       [:input#upload-file {:type "file"}]
       [:br]
       [:br]
       [:button
        {:class    "btn btn-primary"
         :type     "button"
         :on-click #(fb/ez-upload-file "upload-file"
                                       "user-background-images"
                                       progress-pct
                                       upload-error
                                       :update-user-background!)}
        "UPLOAD "
        [:span.fa.fa-upload]]
       [:br]
       [:br]

       (if @upload-error
         [alert-box                 ; Have error. Show its message.
          :alert-type :warning
          :body       @upload-error
          :padding    "12px"]
         [:div                      ; No error. Show progress bar.
          [:br]
          [progress-bar
           :striped? true
           :model    progress-pct
           :width    "350px"]])])))


(defn upload-image-component
  []
  (when @(rf/subscribe [:showing-bg-img-upload])
    [v-box

     :children
     [[modal-panel
       :backdrop-color "grey"
       :backdrop-opacity 0.4

       :child
       [h-box

        :children
        [[upload-form]
         [button
          :class "btn-secondary"
          :label "x"
          :on-click #(rf/dispatch [:show-bg-img-upload false])]]]]]]))
