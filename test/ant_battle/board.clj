(ns ant-battle.board
  (:require [clojure.test :refer :all]
            [ant-battle.simulation.board :as b]
            [ant-battle.simulation.ant :as a]
            [ant-battle.animation.colony-functions :as cf]
            [helpers.general-helpers :as g]))

(def grid-width 100)
(def grid-height 100)

(def global-rand-gen (g/new-rand-gen 99))

(def test-board
  (-> (b/new-board grid-width grid-height)
      (b/add-random-ants (* grid-width grid-height 0.2) (keys cf/test-f-map) global-rand-gen)
      (b/add-food-to-grid)))