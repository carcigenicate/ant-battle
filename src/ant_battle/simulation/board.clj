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

(defrecord Board [dimensions ants colors food])

(defn new-board [width height]
  (->Board [width height] {} {} #{}))

(defn wrap-position [board position]
  (let [[w h] (:dimensions board)
        [x y] position]
    ; TODO: Need to inc the dimensions?
    [(g/wrap x 0 w)
     (g/wrap y 0 h)]))

(defn add-ant [board position type colony]
  (let [wrapped-pos (wrap-position board position)
        new-ant (a/new-ant wrapped-pos type colony)]

    (update board :ants
            #(assoc % wrapped-pos new-ant))))

(defn update-ant
  "Updates the ant at the given position using the given (f)unction.
  If no ant exists at the position, nil is returned.
  WARNING: Should not be used to update position of an ant."
  [board position f]
  (when-let [ant (get-in board [:ants position] nil)]
    (update board :ants #(assoc % position (f ant)))))

(defn move-ant
  "If an ant can be found at old-position, it's moved to new-position
  else, nil is returned."
  [board old-position new-position]
  (when-let [ant (get-in board [:ants old-position] nil)]
    (let [wrapped-pos (wrap-position board new-position)]
      (-> board
          (update :ants #(dissoc % old-position))
          (update :ants #(assoc % wrapped-pos
                                 (a/set-position ant wrapped-pos)))))))

(defn add-food [board position]
  (update board :food
          #(conj % (wrap-position board position))))

(defn remove-food [board position]
  ; TODO: Need to wrap the position?
  (update board :food
          #(disj % position)))

(defn set-color [board position new-color]
  (assoc board :colors (wrap-position board position) new-color))

(defn get-tile [board position]
  (let [gb #(get-in board [% (wrap-position board position)] nil)]
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


