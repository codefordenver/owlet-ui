(ns owlet-ui.components.upload-image-modal
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [owlet-ui.firebase :as firebase]
    [cljsjs.jquery]
    [re-com.core :refer [v-box h-box modal-panel button alert-box]]))

(def show-upload-error (reagent/atom false))

(defn handle-firebase-upload
  [element-id close-modal]
  (let [el (.getElementById js/document element-id)
        file (aget (.-files el) 0)]
    (try
      (firebase/upload-file
        file
        :into-dir "user-background-images"
        :next #(println "Uploaded" (.-bytesTransferred %)
                        "of" (.-totalBytes %) "bytes.")
        :complete-with-snapshot #(let [url (.-downloadURL %)]
                                  (close-modal)
                                  (re-frame/dispatch [:update-user-background! url])
                                  (println "URL:" url)))
      (catch js/Object e
        (reset! show-upload-error true)))))


(defn upload-form [close-modal]
  (let [error-msg "Please select a file to be uploaded."]
    [:form
     [:b "Select Image: "]
     [:br]
     [:br]
     [:input#upload-file
      {:type "file"
       :name "upload-file"
       :on-change #(reset! show-upload-error false)}]
     [:br]
     [:br]
     [:button {:class    "btn btn-primary" :type "button"
               :on-click #(handle-firebase-upload "upload-file" close-modal)}
      "UPLOAD " [:span.fa.fa-upload]]
     (when @show-upload-error
       [alert-box
        :alert-type :warning
        :body       error-msg
        :padding    "6px"])]))

(defn upload-image-component [show? close-modal]
  (fn []
    (when @show?
      [v-box
       :children [[modal-panel
                   :backdrop-color "grey"
                   :backdrop-opacity 0.4
                   :child [h-box
                           :children [[upload-form close-modal]
                                      [button
                                       :class "btn-secondary"
                                       :label "x"
                                       :on-click close-modal]]]]]])))



