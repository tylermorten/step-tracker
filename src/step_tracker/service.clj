(ns step-tracker.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [step-tracker.db :refer [db]]
            [step-tracker.db.steps :as steps]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn get-user-id
  [headers]
  (-> (get headers "authorize")
      Integer/parseInt))

(defn all-steps
  [request]
  (let [user-id (get-user-id (:headers request))]
    {:status 200
     :body (steps/steps-all db {:user-id user-id})}))

(defn insert-step-count
  [request]
  (let [res (get-in request [:json-params])
        user-id (get-user-id (:headers request))
        steps (get res :steps 0)
        result (steps/insert-step-count db {:user-id user-id
                                            :steps steps})]
    (if (> result 0)
      {:status 200
       :body {:message "ok"}}
      {:staus 403
       :body {:message "failed"}})))

(defn total-step-count
  [request]
  (let [user-id (get-user-id (:headers request))]
    {:status 200
     :body (steps/total-step-count db {:user-id user-id})}))

(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/about" :get (conj common-interceptors `about-page)]
              ["/steps" :get [http/json-body `all-steps]]
              ["/steps" :post [(body-params/body-params) http/json-body `insert-step-count]]
              ["/totals" :get [http/json-body `total-step-count]]})



;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) bootstrap/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) bootstrap/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by step-tracker.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})

