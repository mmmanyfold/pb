(ns pb.components.captcha
  (:require [re-frame.core :as rf]
            [reagent.core :as rg]
            [cljs.spec.alpha :as s]))

(defn data-callback []
  (rf/dispatch [:set-captcha-passed]))

(s/fdef re-captcha
        :args (s/cat :dom-id string?
                     :callback ifn?))

(defn re-captcha [dom-id callback]
  (when (.getElementById js/document dom-id)
    (.render js/grecaptcha dom-id
             #js {:sitekey "6Lfj31UUAAAAABAFeTYJgCdLKXVMDze9bOMIVwSs"
                  :callback callback})))

(defn captcha-component []
  (rg/create-class
       {:component-did-mount
        #(re-captcha "g-recaptcha" data-callback)
        :reagent-render
         (fn []
          [:div#g-recaptcha])}))
