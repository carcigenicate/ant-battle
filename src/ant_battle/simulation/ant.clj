(ns ant-battle.simulation.ant)

(defrecord Ant [position type food colony])

(defn new-ant [starting-pos ant-type colony]
  (->Ant starting-pos ant-type 0 colony))

; ----- Movement -----

(defn set-position [ant new-position]
  (assoc ant :position new-position))

(defn get-position [ant]
  (:position ant))

; ----- Food -----

(defn give-food [ant]
  (update ant :food inc))

(defn take-food [ant]
  (update ant :food dec))

(defn carrying-n-food [ant]
  (:food ant))

(defn has-food? [ant]
  (pos? (carrying-n-food ant)))

; ----- Misc -----

(defn get-type [ant]
  (:type ant))

(defn get-colony [ant]
  (:colony ant))