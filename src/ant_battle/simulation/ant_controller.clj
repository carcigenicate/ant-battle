(ns ant-battle.simulation.ant-controller
  (:require [ant-battle.simulation.ant :as a]))

(def queen-type 0)

(defn new-worker [starting-pos worker-type]
  (if-not (= worker-type queen-type)
    (a/new-ant starting-pos worker-type)

    (throw (RuntimeException.
             (str "Tried to create a queen as a worker at " starting-pos)))))

(defn new-queen [starting-pos]
  (a/new-ant starting-pos queen-type))

(defn queen? [ant]
  (= queen-type
     (a/get-type ant)))

(defn has-food? [ant]
  (pos? (a/carrying-n-food ant)))

(defn ant-modified? [before-ant after-ant]
  (= before-ant after-ant))

(defn full-of-food? [ant]
  (if (queen? ant)
    false
    (has-food? ant)))

(defn give-food
  "Gives an ant food only if it's able to hold more food.
  If the ant is unable to hold more food, nil is returned."
  [ant]
  (when-not (full-of-food? ant)
    (a/give-food ant)))

(defn take-food
  "Takes food from the ant if it has food.
  If the ant isn't carrying any food, nil is returned."
  [ant]
  (when (has-food? ant)
    (take-food ant)))

(defn transfer-food
  "Gives food from the source ant to the target ant.
  If either the source has no food, or the target is full of food, nil is returned, else,
   a pair of [taken-source-ant given-target-ant] is returned."
  [source-ant target-ant]
  (when (and (has-food? source-ant)
           (not (full-of-food? target-ant)))

    (let [taken-source (take-food source-ant)]
      [taken-source (give-food target-ant)])))
