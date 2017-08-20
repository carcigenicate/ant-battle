(ns ant-battle.animation.colony-functions
  (:require [helpers.general-helpers :as g]))

(def smooth-a (atom 0))

:move-to?

(def test-f-map
  {:down-left     (constantly {:move-to 6, :tile-color 5})
   :right          (constantly {:move-to 5, :tile-color 3})
   #_ (:nothing       (constantly {}))

   :smooth-random (fn [_]
                    (let [b (- (rand-int 2) 0)
                          a (g/wrap (+ @smooth-a b) 0 8)]
                      (reset! smooth-a a)
                      {:move-to a
                       :tile-color 10}))

   :down          (fn [{tiles :surrounding-tiles :as state}]

                    (let [current-color (or (get-in tiles [4 :color])
                                            (rand-int 255))]

                      {:move-to 7,
                       :tile-color (inc current-color)}))})