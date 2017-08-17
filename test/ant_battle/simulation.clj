(ns ant-battle.simulation
  (:require [clojure.test :refer :all]
            [ant-battle.simulation.board :as b]
            [ant-battle.simulation.simulation :as s]
            [ant-battle.simulation.ant-controller :as ac]
            [ant-battle.simulation.ant :as a]

            [ant-battle.board :as bt]))

(def test-ant (get (:ants bt/test-board) [0 0]))

(def sim-state (s/->Simulation-State bt/test-board
                                     {0 (constantly {:move-to? 7})
                                      1 (constantly {:move-to? 0})}))





