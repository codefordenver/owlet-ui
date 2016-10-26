(ns owlet-ui.components.upload-image-modal
  (:require
    [reagent.core :as reagent]
    [owlet-ui.firebase :as firebase]
    [cljsjs.jquery]
    [re-com.core :refer [v-box h-box modal-panel button]]))

(defn handle-firebase-upload
  [element-id]
  (let [el (.getElementById js/document element-id)
        file (aget (.-files el) 0)]
    (firebase/upload-file file
                          :into-dir "user-background-images"
                          :next #(println "Uploaded"
                                          (.-bytesTransferred %)
                                          "of"
                                          (.-totalBytes %)
                                          "bytes.")
                          :complete-with-snapshot #(println
                                                    "URL:"
                                                    (.-downloadURL %)))))

(defn upload-button []
  [:button {:class    "btn btn-primary" :type "button"
            :on-click #(handle-firebase-upload "upload-file")}
   "upload" [:span {:class "fa fa-upload"}]])

(defn upload-form []
  [:form
   [:label "Upload Filename: "]
   [:input#upload-file
    {:type "file"
     :name "upload-file"}]
   [upload-button]])

(defn upload-image-component []
  (let [_ (.filestyle (js/$ ":file"))
        show? (reagent/atom true)]
    (fn []
      (when @show?
        [v-box
         :children [[modal-panel
                     :backdrop-color "grey"
                     :backdrop-opacity 0.4
                     :child [h-box
                             :children [[upload-form]
                                        [button
                                         :label    "x"
                                         :on-click #(reset! show? false)]]]]]]))))



