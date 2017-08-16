(ns ant-battle.simulation.advance-f-io)

(def alliances #{:friendly :enemy})

(defrecord Tile-State [color ant? food?])

(defrecord Ant-State [ant surrounding-tiles])

(defrecord Ant-Move [move-to? new-ant-at? new-tile-color])

