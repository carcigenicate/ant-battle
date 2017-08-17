(ns ant-battle.simulation
  (:require [clojure.test :refer :all]
            [ant-battle.simulation.board :as b]
            [ant-battle.simulation.simulation :as s]
            [ant-battle.simulation.ant-controller :as ac]
            [ant-battle.simulation.ant :as a]))

(def board (-> (b/new-board)
               (b/add-ant [0 0] 0 0)
               (b/add-ant [2 2] 0 0)
               (b/add-ant [1 1] 1 1)
               (b/update-ant [0 0] a/give-food)))

(def test-ant (get (:ants board) [0 0]))

(def sim-state (s/->Simulation-State board
                                     {0 (constantly {:move-to? 7})
                                      1 (constantly {:move-to? 0})}))




