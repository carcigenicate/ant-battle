(ns ant-battle.simulation.colony)

(defrecord Colony [ants ant-advance-f])

(defn new-colony [ant-advance-f]
  (->Colony [] ant-advance-f))

(defn spawn-and [colony x y]
                ())