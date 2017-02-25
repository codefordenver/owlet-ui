(ns owlet-ui.components.upload-image-modal
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [owlet-ui.firebase :as firebase]
    [cljsjs.jquery]
    [re-com.core :refer [v-box h-box modal-panel button alert-box progress-bar]]))


(defn handle-firebase-upload
  "Initiates the upload of the file selected in an
  <input type=\"file\" id=input-elem-id> element in the current document,
  where input-elem-id is the string given in the first argument. The given
  destination directory string must be relative to the root of Firebase
  Storage. Atom progress-pct-atom is periodically updated with the percentage
  of uploaded bytes. Atom error-atom may be updated with a js/Error object if
  the user did not provide a file in the input element or if there was some
  other error in the upload.
  "
  [input-elem-id dest-dir close-modal progress-pct-atom error-atom]

  (firebase/upload-file
    ; JS File object, or nil if none selected:
    (-> input-elem-id js/document.getElementById (aget "files" 0))

    ; Upload options:
    :into-dir dest-dir
    :next     (fn [task-snapshot]
                ; What to do after each batch of bytes has been transfered.
                ; Argument is a firebase.storage.UploadTaskSnapshot. See
                ; https://firebase.google.com/docs/reference/js/firebase.storage.UploadTaskSnapshot
                (let [total      (.-totalBytes task-snapshot)
                      transfered (.-bytesTransferred task-snapshot)]
                  (reset! progress-pct-atom
                          (js/Math.round (* 100 (/ transfered total))))))
    :complete-with-snapshot
              (fn [snapshot]
                (let [url (.-downloadURL snapshot)]
                  (re-frame/dispatch [:update-user-background! url])
                  (close-modal)))
    :error    (fn [error]
                (reset! error-atom (.-message error)))))


(defn upload-form
  [close-modal]
  (let [upload-error (reagent/atom nil)
        progress-pct (reagent/atom 0)]
    (fn []
      [:form
       [:b "Upload a background image file"]
       [:br]
       [:br]
       [:input#upload-file
        {:type      "file"
         :name      "upload-file"
         :on-change #(reset! upload-error nil)}]
       [:br]
       [:br]
       [:button
        {:class    "btn btn-primary"
         :type     "button"
         :on-click #(handle-firebase-upload "upload-file"
                                            "user-background-images"
                                            close-modal
                                            progress-pct
                                            upload-error)}
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
