(ns app.frontend.views)

(defn main-ui []
  [:html
   [:head
    [:title "To-Do List"]
    [:link {:rel "stylesheet" :href "/css/styles.css"}]]
   [:body
    [:div#app
     [:h1 "To-Do List"]
     [:p "Welcome to the Clojure To-Do List web application."]]
    [:script {:src "/js/main.js"}]]])