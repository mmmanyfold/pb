(ns pb.views.home
  (:require [re-frame.core :as rf]
            [pb.components.election :refer [election-component]]))

(defn home-view [admin-election]
  (let [lang (rf/subscribe [:language-in-view])
        entries (rf/subscribe [:entries])]
    (when @entries
      (doall
        [:div
         (for [election (:elections (get @entries @lang))
               :let [shortTitle (-> election :fields :shortTitle)]
               :when (= shortTitle admin-election)]
           ^{:key (gensym "election-")}
           [election-component (:fields election)])]))))
