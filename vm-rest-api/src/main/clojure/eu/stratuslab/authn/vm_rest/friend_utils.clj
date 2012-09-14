(ns eu.stratuslab.authn.vm-rest.friend-utils
  "Utilities for setting up the friend authentication framework."
  (:require [noir.server :as server]
            [cemerick.friend :as friend]
            [cemerick.friend [workflows :as workflows]
             [credentials :as creds]]
            [clojure.tools.logging :as log]))

;; FIXME!
(defn login-failure-handler [& opts]
  nil)

(defn configure-friend [f & [context-path]]
  (let [context-path (or context-path "")
        login-uri (str context-path "/login")
        landing-uri (str context-path "/")]
    (server/add-middleware
     friend/authenticate
     {:credential-fn (partial creds/bcrypt-credential-fn f)
      :workflows [(workflows/interactive-form)]
      :login-uri login-uri
      :login-failure-handler login-failure-handler
      :unauthorized-redirect-uri login-uri
      :default-landing-uri landing-uri})))

;; FIXME!
(defn credential-fn [identity]
  {})
