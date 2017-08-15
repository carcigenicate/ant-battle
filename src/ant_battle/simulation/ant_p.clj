(ns ant-battle.simulation.ant-p)

; TODO: Getter/setting creep?

(defprotocol AntP
  (get-position [ant])
  (set-position [ant new-position])

  (give-food [ant])
  (take-food [ant])
  (has-food? [ant])

  (get-type [ant])
  (set-type [ant type]))

(defn transfer-food
  "Returns a pair of [source target], where a piece of food was transfered
   from the source ant to the target ant."
  [source-ant target-ant]
  (let [taken-source (take-food source-ant)]
    [taken-source (give-food target-ant)]))

(defn update-position [ant f]
  (set-position ant
    (f (get-position ant))))