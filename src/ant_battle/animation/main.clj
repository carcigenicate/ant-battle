(ns ant-battle.animation.main
  (:require [quil.core :as q]

            [ant-battle.simulation.simulation :as s]
            [ant-battle.animation.colony-functions :as cf]
            [ant-battle.simulation.ant :as a]

            [helpers.general-helpers :as g]
            [ant-battle.simulation.board :as b])

  (:import [java.awt Color]))

(def display-width 1000)
(def display-height 1000)

(def grid-width 25)
(def grid-height 25)

(def grid-side-length (double (/ (min display-width display-height)
                                 (max grid-width grid-height))))

(def ant-height (* grid-side-length 1))
(def ant-width (* grid-side-length 0.5))

(defrecord Animation-State [sim-state])

(def test-board
  (-> (b/new-board)
      (b/add-ant [5 5] 0 100)
      (b/add-ant [10 10] 0 100)
      (b/add-ant [20 20] 1 200)
      (b/update-ant [10 10] a/give-food)))

(defn setup-state []
  (->Animation-State (s/->Simulation-State test-board cf/test-f-map)))

(defn update-state [state]
  #_
  (clojure.pprint/pprint (:board (:sim-state state)))

  (-> state
      (update state :sim-state s/simulate-frame)))

(defn grid-coord-to-screen [[x y]]
  [(g/map-range x 0 grid-width 0 display-width)
   (g/map-range y 0 grid-height 0 display-height)])

(defn color-type-to-color-vec [color-type]
  (let [c (Color. (hash color-type))]
    [(.getRed c) (.getGreen c) (.getBlue c)]))

(defn draw-ant [ant]
  (let [color (color-type-to-color-vec (hash (a/get-colony ant)))
        grid-pos (a/get-position ant)
        [x y] (grid-coord-to-screen grid-pos)]

    (q/with-fill color
      (q/ellipse (- x ant-width) (- y (/ ant-height 2))
                 ant-width ant-height))))

(defn draw-grid []
  (doseq [y (range 0 display-height grid-side-length)
          x (range 0 display-width grid-side-length)]

    (q/no-fill)
    (q/rect x y grid-side-length grid-side-length)))

; TODO: Only draw over cells that have changed.
; TODO: Take the difference of the previous board and the new board, and only redraw those cells
; TODO: (mapv first (set/difference (set old-board) (set new-board))
(defn draw-state [state]
  (q/background 200 200 200)

  (let [{ants :ants} (:board (:sim-state state))]
    (draw-grid)

    (doseq [a (map second ants)]
      (draw-ant a))))



