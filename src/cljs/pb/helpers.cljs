(ns pb.helpers
  (:require [cljsjs.showdown]))

(defn render-markdown [markdown]
  [:div {"dangerouslySetInnerHTML"
         #js{:__html (.makeHtml (js/showdown.Converter.) markdown)}}])
