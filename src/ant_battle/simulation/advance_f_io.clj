(ns ant-battle.simulation.advance-f-io)

(defrecord Tile-State [color ant? food?])

; TODO: Colony-State
(defrecord Ant-State [surrounding-tiles])

; new-ant-at? is a pair of [new-ant-pos ant-type]
(defrecord Ant-Move [move-to? new-ant-at? new-tile-color?])
