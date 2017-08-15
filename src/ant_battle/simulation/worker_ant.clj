(ns ant-battle.simulation.worker-ant
  (:require [ant-battle.simulation.ant-p :as ap]))

(defrecord Worker-Ant [position food?]
  ap/AntP
  (give-food [ant] (assoc ant :food? true))
  (take-food [ant] (assoc ant :food? false))
  (has-food? [ant] (:food? ant))

  (get-position [ant] (:position ant))
  (set-position [ant new-pos] (assoc ant :position new-pos)))