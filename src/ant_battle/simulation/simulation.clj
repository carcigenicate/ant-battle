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
  (if (a/has-food? spawner-ant)
    (-> board
        (b/add-ant spawn-position type (a/get-colony spawner-ant))
        (b/update-ant (a/get-position spawner-ant) a/take-food))

    (print-violation-warning "Ant " (into {} spawner-ant)
                             " tried to spawn an ant without carrying food.")))

(defn handle-move-to-square [board moving-ant new-position]
  (let [{ant? :ant?, food? :food?} (b/get-tile board new-position)
        ant-pos (a/get-position moving-ant) ; TODO: Remove?
        move-ant #(b/move-ant % ant-pos new-position)]

    (cond
      ant?
      (do
        ; TODO: Move to same square as queen to deposit/steal?
        (print-violation-warning "Tried to move onto an already occupied square: "
                                 (into {} moving-ant) " to " new-position)
        board)

      (and food? (not (ac/full-of-food? moving-ant)))
      (-> board
          (move-ant)
          (b/update-ant new-position a/give-food)
          (b/remove-food new-position))

      :else
      (move-ant board))))

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
              (print-violation-warning "Cannot move and spawn in the same turn: "
                                       (into {} move))
              board)

            move-to?
            (handle-move-to-square board ant (v-coords move-to?))

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
        initial-ants (map second (:ants board))]

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

