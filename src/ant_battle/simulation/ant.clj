(ns ant-battle.simulation.ant
  (:require [ant-battle.simulation.ant-p :as ap]))

(defrecord Ant [position type food])

(defn new-ant [starting-pos ant-type]
  (->Ant starting-pos ant-type 0))

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

; ----- Misc -----

(defn get-type [ant]
  (:type ant))