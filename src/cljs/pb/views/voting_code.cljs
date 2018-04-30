(ns pb.views.voting-code
  (:require [reagent.core :as rg]
            [re-frame.core :as rf]
            [cljsjs.moment]
            [pb.components.loading :refer [loading-component]]
            [pb.components.captcha :refer [captcha-component]]
            [ajax.core :as ajax :refer [GET]]))

(def code (rg/atom nil))
(def error-code (rg/atom nil))

(defn error-handler [response]
  (reset! error-code (:status response)))

(defn success-handler [response]
  (set! (.. js/window -location -href) (str (.. js/window -location -href) "/proposals"))
  (rf/dispatch [:set-voter-id (:id response)]))

(defn check-code [code]
  (GET (str "/api/checkcode/" code) {:handler success-handler
                                     :error-handler error-handler
                                     :response-format (ajax/json-response-format {:keywords? true})}))

(defn voting-code-view [election-slug]
  (rg/create-class
    {:component-will-unmount
     (fn []
       (reset! error-code nil)
       (reset! code nil))
     :reagent-render
     (fn []
       (let [now (js/Date.)
             query (str "{ elections(q: \"fields.shortTitle=" election-slug
                        "\") {
                          title
                          startOnline
                          endOnline
                          maxSelection
                          proposalRefs {
                            sys { id }}
                        }}")]
         (rf/dispatch [:get-contentful-data :election-in-view query :election])
         (fn [election-slug]
           (if-let [{:keys [startOnline endOnline]} @(rf/subscribe [:election-in-view])]
             (if (> (js/Date. endOnline) now)
               (if (> (js/Date. startOnline) now)
                 ;; if online voting hasn't started
                 [:div.voting-code-view
                  [:h1 (str "Voting opens on "
                            (.format (js/moment startOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]]
                 ;; if online voting is open
                 [:div.voting-code-view
                  [:h1 "Text " [:span#text-keyword election-slug] " to 1234567890 to get your voting code"]
                  [:p "Your phone number will NEVER be shared and will automatically get deleted after the election."
                   [:br]
                   "Your voting code is specific to this election and won’t be used for anything else."]
                  [:div.input-group.flexrow-wrap
                   [:div.input-group-prepend
                    [:input.form-control
                     {:type "text"
                      :placeholder "00000000"
                      :maxLength 8
                      :value @code
                      :on-change (fn [e]
                                   (let [input (-> e .-target .-value)]
                                     (reset! code input)))}]]
                   [:a {:on-click #(check-code @code)}
                    [:input#submit-code
                     {:type "submit"
                      :value "CONTINUE"
                      :disabled (or (< (count @code) 8)
                                    (nil? @(rf/subscribe [:captcha-passed])))}]]]
                  [captcha-component]
                  [:div]
                  (when-not (nil? @error-code)
                    (if (= @error-code 404)
                      [:div.error.not-found
                       "The voting code you entered is not valid. Please ensure the code is entered correctly, or follow the steps above to get your unique code."]
                      [:div.error.already-voted
                       "We already got your vote!"]))])
               ;; if online voting has ended
               [:div.voting-code-view
                [:h1 (str "Voting ended on "
                          (.format (js/moment endOnline) "dddd, MMMM Do YYYY [at] h:mm a."))]])
             [loading-component]))))}))
