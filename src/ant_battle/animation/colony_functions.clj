(ns ant-battle.animation.colony-functions
  (:require [helpers.general-helpers :as g]))

(def smooth-a (atom 0))

(def test-f-map
  {:down-left     (constantly {:move-to? 6})
   :down          (constantly {:move-to? 7})
   :left          (constantly {:move-to? 3})
   #_ (:nothing       (constantly {}))

   :smooth-random (fn [_]
                    (let [b (- (rand-int 3) 0)
                          a (g/wrap (+ @smooth-a b) 0 8)]
                      (reset! smooth-a a)
                      {:move-to? a}))})