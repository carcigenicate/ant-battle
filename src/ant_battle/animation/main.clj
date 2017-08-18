(ns ant-battle.animation.main
  (:require [quil.core :as q]

            [ant-battle.simulation.simulation :as s]
            [ant-battle.animation.colony-functions :as cf]
            [ant-battle.simulation.ant :as a]

            [helpers.general-helpers :as g]
            [helpers.quil-helpers :as qh]

            [ant-battle.simulation.board :as b]
            [ant-battle.simulation.ant-controller :as ac])

  (:import [java.awt Color]))

(def display-width 1000)
(def display-height 1000)

(def grid-width 10)
(def grid-height 10)

(def grid-side-length (double (/ (min display-width display-height)
                                 (max grid-width grid-height))))
(def half-grid-length (/ grid-side-length 2))

(def ant-height (* grid-side-length 1))
(def ant-width (* grid-side-length 0.5))

(def food-color [200 125 50])
(def food-radius ant-width)

(defrecord Animation-State [sim-state])

#_
(def test-board
  (-> (b/new-board grid-width grid-height)
      (b/add-ant [7 7] 1 :up-right)
      (b/add-ant [9 9] 0 :up-right)
      (b/add-ant [10 11] 1 :down)
      (b/add-food [5 5])
      (b/add-food [3 3])
      (b/add-food [1 1])))

(def test-board
  (-> (b/new-board grid-width grid-height)
      (b/add-ant [5 5] 0 :nothing)
      (b/add-ant [5 0] 1 :down)))

(defn setup-state []
  (q/frame-rate 1)

  (->Animation-State (s/->Simulation-State test-board cf/test-f-map)))

(defn update-state [state]
  (-> state
      (update :sim-state s/simulate-frame)))

(defn grid-coord-to-screen [[x y]]
  [(g/map-range x 0 grid-width 0 display-width)
   (g/map-range y 0 grid-height 0 display-height)])

(defn color-type-to-color-vec [color-type]
  (let [c (Color. (hash color-type))]
    [(.getRed c) (.getGreen c) (.getBlue c)]))

(defn draw-ant [ant]
  (let [ant-color (color-type-to-color-vec (hash (a/get-colony ant)))
        grid-pos (a/get-position ant)
        [x y] (grid-coord-to-screen grid-pos)
        adj-x (+ x half-grid-length)
        adj-y (+ y half-grid-length)]

    (q/with-fill ant-color
      (q/ellipse adj-x adj-y ant-width ant-height))

    (when (a/has-food? ant)
      (q/with-stroke [0 0 0]
        (qh/with-weight (* ant-width 0.5)
          (q/point adj-x adj-y))))

    (when (ac/queen? ant)
      (qh/with-weight (* food-radius 0.5)
         (q/with-stroke [200 200 0]
           (q/point adj-x (- adj-y (* half-grid-length 0.5))))))))

(defn draw-food [food]
  (qh/with-weight food-radius
    (q/with-stroke food-color

      (doseq [f food
              :let [[x' y'] (grid-coord-to-screen f)]]

        (q/point (+ x' half-grid-length) (+ y' half-grid-length))))))

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

  (let [{ants :ants, food :food} (:board (:sim-state state))]
    (draw-grid)

    (doseq [a (map second ants)]
      (draw-ant a))

    (draw-food food)))



