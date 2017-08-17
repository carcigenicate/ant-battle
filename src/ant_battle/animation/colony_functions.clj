(ns ant-battle.animation.colony-functions)

(def test-f-map
  {100 #(do % (println "Tested!") 0)
   200 (constantly 7)})