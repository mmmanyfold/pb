(ns ^:figwheel-no-load pb.app
  (:require [pb.core :as core]
            [devtools.core :as devtools]
            [re-frisk.core :refer [enable-re-frisk!]]))

(enable-console-print!)

(devtools/install!)

(enable-re-frisk!)

(println "dev mode")

(core/init!)
