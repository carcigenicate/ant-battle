(ns ant-battle.simulation.board
  (:require [ant-battle.simulation.ant :as a]
            [ant-battle.simulation.ant-controller :as ac]
            [ant-battle.simulation.advance-f-io :as io]

            [helpers.general-helpers :as g]

            [clojure.set :as set]))

; TODO: Default color?
; TODO: Overwrite checks for ants?
; TODO: Boundry checking?
(comment
  (defmacro inbounds? [board & positions]))

(defrecord Board [ants colors food])

(defn new-board []
  (->Board {} {} #{}))

(defn add-ant [board position type colony]
  (let [new-ant (a/new-ant position type colony)]

    (update board :ants
            #(assoc % position new-ant))))

(defn update-ant
  "Updates the ant at the given position using the given (f)unction.
  Should not be used to update position.
  If no ant exists at the position, nil is returned."
  [board position f]
  (when-let [ant (get-in board [:ants position] nil)]
    (update board :ants #(assoc % position (f ant)))))

(defn move-ant
  "If an ant can be found at old-position, it's moved to new-position
  else, nil is returned."
  [board old-position new-position]
  (when-let [ant (get-in board [:ants old-position] nil)]
    (-> board
        (update :ants #(dissoc % old-position))
        (update :ants #(assoc % new-position
                               (a/set-position ant new-position))))))

(defn add-food [board position]
  (update board :food
          #(conj % position)))

(defn remove-food [board position]
  (update board :food
          #(disj % position)))

(defn set-color [board position new-color]
  (assoc board :colors position new-color))

(defn get-tile [board position]
  (let [gb #(get-in board [% position] nil)]
    (io/->Tile-State (gb :colors)
                     (gb :ants)
                     (gb :food))))

(defn coords-surrounding [[x y]]
  (for [y (range (dec y) (+ y 2))
        x (range (dec x) (+ x 2))]
    [x y]))

(defn tiles-for-positions [board positions]
  (map #(get-tile board %) positions))

(defn tiles-surrounding [board position]
  (tiles-for-positions board
    (coords-surrounding position)))


