(ns ant-battle.simulation.board
  (:require [ant-battle.simulation.ant :as a]
            [ant-battle.simulation.ant-controller :as ac]
            [ant-battle.simulation.advance-f-io :as io]

            [helpers.general-helpers :as g]

            [clojure.set :as set]
            [helpers.point-helpers :as ph]))

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

(defn add-ant
  ([board position type colony]
   (let [wrapped-pos (wrap-position board position)
         new-ant (a/new-ant wrapped-pos type colony)]

     (add-ant board new-ant)))

  ([board ant]
   (update board :ants
           #(assoc % (a/get-position ant) ant))))

(defn add-ants [board ants]
  (reduce add-ant board ants))

(defn update-ant
  "Updates the ant at the given position using the given (f)unction.
  If no ant exists at the position, nil is returned.
  WARNING: Should not be used to update position of an ant."
  [board position f]
  (let [wrapped-pos (wrap-position board position)]
    (when-let [ant (get-in board [:ants wrapped-pos] nil)]
      (update board :ants #(assoc % wrapped-pos (f ant))))))

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
  (update board :colors #(assoc % (wrap-position board position) new-color)))

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

(defn add-random-ants [board n-ants colonies rand-gen]
  (let [[w h] (:dimensions board)]
    (reduce (fn [acc-b _]
              (add-ant acc-b (mapv int (ph/random-point 0 w 0 h rand-gen))
                       0
                       (g/random-from-collection colonies rand-gen)))
            board
            (range n-ants))))

(defn add-food-to-grid [board]
  (let [[w h] (:dimensions board)]
    (reduce add-food board
            (for [y (range h)
                  x (range w)]
              [x y]))))


