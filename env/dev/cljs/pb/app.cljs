(ns ^:figwheel-no-load pb.app
  (:require [pb.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
