(ns ant-battle.main
  (:require [ant-battle.animation.main :as d]
            [quil.core :as q]
            [quil.middleware :as m])

  (:gen-class))

(defn -main []
  (q/defsketch Ant-Battle
    :size [d/display-width d/display-height]

    :setup d/setup-state
    :update d/update-state
    :draw d/draw-state

    :middleware [m/fun-mode]))