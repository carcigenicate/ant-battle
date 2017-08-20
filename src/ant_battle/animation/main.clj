(ns ant-battle.animation.main
  (:require [quil.core :as q]

            [ant-battle.simulation.simulation :as s]
            [ant-battle.animation.colony-functions :as cf]
            [ant-battle.simulation.ant :as a]

            [helpers.general-helpers :as g]
            [helpers.quil-helpers :as qh]
            [helpers.point-helpers :as ph]

            [ant-battle.simulation.board :as b]
            [ant-battle.simulation.ant-controller :as ac])

  (:import [java.awt Color]))

(def display-width 1000)
(def display-height 1000)

(def grid-width 50)
(def grid-height 50)

(def grid-side-length (double (/ (min display-width display-height)
                                 (min grid-width grid-height))))
(def half-grid-length (/ grid-side-length 2))

(def ant-height (* grid-side-length 1))
(def ant-width (* grid-side-length 0.6))

(def food-color [200 125 50])
(def food-radius ant-width)

(def global-colony-fs cf/test-f-map)

(def global-rand-gen (g/new-rand-gen 99))

(defrecord Animation-State [sim-state])

(defn starting-queens [colony-identifiers rand-gen]
  (let [r-pos #(mapv int (ph/random-point 0 grid-width 0 grid-height rand-gen))]
    (mapv #(a/new-ant (r-pos) ac/queen-type %)
          colony-identifiers)))

(defn starting-board [width height colonies rand-gen]
  (-> (b/new-board width height)
      (b/add-ants (starting-queens colonies rand-gen))))

(defn starting-sim-state [board-dimensions colony-f-map rand-gen]
  (let [[w h] board-dimensions
        board (starting-board w h (keys colony-f-map) rand-gen)]

    (s/->Simulation-State board colony-f-map)))

(defn setup-state []
  (q/frame-rate 1000)

  (let [sim-state (starting-sim-state [grid-width grid-heights]
                                      global-colony-fs global-rand-gen)
        test-board (-> (b/new-board grid-width grid-height)
                       (b/add-random-ants (* 0.05 grid-width grid-height) (keys global-colony-fs) global-rand-gen))
        test-sim (assoc sim-state :board test-board)]
    (->Animation-State test-sim)))

(defn update-state [state]
  (-> state
      (update :sim-state s/simulate-frame)))

(defn grid-coord-to-screen [[x y]]
  [(int (g/map-range x 0 grid-width 0 display-width))
   (int (g/map-range y 0 grid-height 0 display-height))])

(defn color-type-to-color-vec [color-type]
  (let [c (Color. (mod (hash color-type) 65025))]
    [(.getRed c) (.getGreen c) (.getBlue c)]))

(defn invert-color [color]
  (mapv #(g/wrap (+ 128 %) 0 256)
        color))

(defn food-marker-color [ant-color]
  (let [half-intensity 384
        sum (reduce + 0 ant-color)]
    (if (<= sum half-intensity)
      [150 200 150]
      [10 50 10])))

(defn draw-ant [ant]
  (let [ant-color (color-type-to-color-vec (a/get-colony ant))
        grid-pos (a/get-position ant)
        [x y] (grid-coord-to-screen grid-pos)
        adj-x (+ x half-grid-length)
        adj-y (+ y half-grid-length)]

    (q/with-fill ant-color
      ; TODO: Expensive? Make q/point?
      (q/ellipse adj-x adj-y ant-width ant-height))

    (when (a/has-food? ant)
      (q/with-stroke (food-marker-color ant-color)
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

(defn draw-tiles-colors [tiles]
  (doseq [[pos color] tiles
          :let [v-color (color-type-to-color-vec color)
                [x y] (grid-coord-to-screen pos)]]
    (q/with-fill v-color
      (q/rect x y
              grid-side-length grid-side-length))))

; TODO: Only draw over cells that have changed.
; TODO: Take the difference of the previous board and the new board, and only redraw those cells
; TODO: (mapv first (set/difference (set old-board) (set new-board))
; TODO: Failed miserably. Try again.
(defn draw-state [state]
  (q/background 200 200 200)

  (let [board (:board (:sim-state state))
        {ants :ants, food :food, tile-colors :colors} board]

    (draw-tiles-colors tile-colors)

    (draw-grid)

    (doseq [a (map second ants)]
      (draw-ant a))

    (draw-food food)))



