(ns owlet.async
  "Utilities for running asynchronous processes."
  (:require [cljs.core.async :refer [put!]]))

(def run-after-next-annimation-frame
  (if (exists? reagent.core/after-render)
    (.-after-render reagent.core)                ;; reagent >= 0.6.0
    (.-do-later reagent.impl.batching)))          ;; reagent < 0.6.0

(def repeatedly-running
  "Each member of the set in this atom should be a function of no arguments
  that terminates quickly relative to the animation frame interval (~16 ms).
  Once (repeatedly-run) has been executed, each such function will be
  repeatedly queued up to run after the next animation frame, then the next,
  etc. You can swap in a different set at will, even an empty one. To shut down
  the process (as opposed to allowing the process to continue repeatedly
  checking an empty set), just execute (reset! repeatedly-running nil). To
  start it again, run (repeatedly-run).
  "
  (atom))


(defn repeatedly-run
  "Runs each function in the set in atom repeatedly-running or a provided atom.
  If the atom contains nil, it will be populated with an empty set. If the atom
  does not contain nil (i.e., contains a possibly-empty set of functions), then
  this function does nothing but return nil. Hence, you can only start up the
  cycle of running functions BEFORE you populate the set in repeatedly-running
  or the given atom. This prevents unnecessary runs if this function were
  accidentally called more than once."

  ([]
   (repeatedly-run repeatedly-running))

  ([fns-to-run-atom]
   (letfn [(schedule-functions []
             (when-let [running @fns-to-run-atom]
               (doseq [f running]
                 (run-after-next-annimation-frame f))
               (run-after-next-annimation-frame schedule-functions)))]
     (when (nil? @fns-to-run-atom)
       (reset! fns-to-run-atom #{})
       (schedule-functions)))))


(defn continue-chan!
  "Tells the given channel to allow any code blocked by it to continue. This
  function will not block.
  "
  [ch]
  (put! ch :continue))


(defn continue-after-pause!
  "Tells the given channel to allow any code blocked by it to continue after
  waiting for the end of the next animation frame, then the one after that.
  In this way, code that itself first queues code X to run after the next
  animation frame, then immediately calls this function, will have a full
  animation frame to execute X. Typically, X also queues some change to the
  DOM, so we should see the change after the second frame.
  "
  [ch]
  (run-after-next-annimation-frame
    (fn [] (run-after-next-annimation-frame #(continue-chan! ch)))))

