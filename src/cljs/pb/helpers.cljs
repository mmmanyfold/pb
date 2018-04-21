(ns pb.helpers
  (:require [cljsjs.showdown]))

(def showdown (js/showdown.Converter.))
