(ns ant-battle.simulation.simulation
  (:require [ant-battle.simulation.board :as b]
            [ant-battle.simulation.advance-f-io :as io]
            [ant-battle.simulation.ant :as a]
            [ant-battle.simulation.ant-controller :as ac]))

(defrecord Simulation-State [board colony-advance-f-map])

(defn print-violation-warning [& message]
  (println
    (str "Warning: " (apply str message) " - Turn skipped.")))

(defn new-simulation
  "Creates a new simulation state.
   colony-advance-func-map should be a map mapping colony identifiers to functions that will advance each ant."
  [colony-advance-func-map]
  (->Simulation-State (b/new-board)
                      colony-advance-func-map))

(defn parse-move [raw-move]
  (io/map->Ant-Move raw-move))

(defn handle-ant-spawn [board spawner-ant spawn-position type]
  (if (ac/has-food? spawner-ant)
    (-> board
        (b/add-ant spawn-position type (a/get-colony spawner-ant))
        (b/update-ant (a/get-position spawner-ant) a/take-food))

    (print-violation-warning "Ant " (into {} spawner-ant)
                             " tried to spawn an ant without carrying food.")))

(defn advance-board-with-move [board ant surrounding-coords move]
  (try
    (let [{:keys [move-to? new-ant-at? new-tile-color?]} move
          [spawn-pos spawn-type] new-ant-at?
          ant-pos (a/get-position ant)
          v-coords (vec surrounding-coords)

          advanced-board
          (cond
            (= true move-to? new-ant-at?)
            (do
              (print-violation-warning "Cannot move and spawn in the same turn: " (into {} move))
              board)

            move-to?
            ; TODO: Will overwrite any ants at the target coord
            ; TODO: Create a handle- function to handle collisions
            (b/move-ant board ant-pos (v-coords move-to?))

            new-ant-at?
            (handle-ant-spawn board ant (v-coords spawn-pos) spawn-type))]

      (if new-tile-color?
        (b/set-color advanced-board ant-pos new-tile-color?)
        advanced-board))

    (catch Exception e
      (print-violation-warning "Move: " (into {} move) " caused an exception:\n" e)
      board)))

; TODO: Test!!!
(defn simulate-frame [sim]
  (let [{board :board fm :colony-advance-f-map} sim
        initial-ants (seq (:ants board))]

    (loop [[ant & rest-ants] initial-ants
           acc-board board]

      (if ant
        (let [advance-f (fm (a/get-colony ant))

              neigh-coords (vec (b/coords-surrounding (a/get-position ant)))
              neigh-tiles (vec (b/tiles-for-positions acc-board neigh-coords))

              input (io/->Ant-State neigh-tiles)
              move (parse-move (advance-f ant))
              advanced-board (advance-board-with-move acc-board ant neigh-coords move)]

          (recur rest-ants advanced-board))

        (assoc sim :board acc-board)))))

