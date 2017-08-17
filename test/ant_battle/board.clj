(ns ant-battle.board
  (:require [clojure.test :refer :all]
            [ant-battle.simulation.board :as b]
            [ant-battle.simulation.ant :as a]))

(def test-board
  (-> (b/new-board)
      (b/add-ant [0 0] 0 0)
      (b/add-ant [2 2] 0 0)
      (b/add-ant [1 1] 1 1)
      (b/update-ant [0 0] a/give-food)))