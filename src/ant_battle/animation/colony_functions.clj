(ns ant-battle.animation.colony-functions)

(def test-f-map
  {:up-right (constantly {:move-to? 0})
   :down (constantly {:move-to? 7})
   :nothing (constantly {})})