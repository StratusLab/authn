(ns eu.stratuslab.authn.vm-rest.views.authn
  (:require [noir.core :refer [defpage pre-route]]
            [noir.response :as resp]
            [cemerick.friend :as friend]
            [clojure.tools.logging :as log]))

(def context-path (atom ""))

(defn set-context-path
  [path]
  (reset! context-path path))

;;
;; Leave out the action attribute so that the URL defaults to the same
;; URL as the initial form.  This avoids needing to add the servlet
;; context path explicitly.
;;
;; FIXME: Should be done with enlive.
;;
(def ^:const login-html
     "<!DOCTYPE html>
<html>
  <head>
    <title>StratusLab VM Management</title>
  </head>
  <body>
    <form method='POST'>
      <label for='username'>Username: </label>
      <input id='username' name='username' type='text' />
      <label for='password'>Password: </label>
      <input id='password' name='password' type='password' />
      <input type='submit' value='Login'>
    </form>
  </body>
</html>
")

(defn clear-identity [response]
  (update-in response [:session] dissoc ::identity))

;; protect all but "/", "/login", and "/logout"
(pre-route [:any ["/:path" :path #"(?!login|logout|$).*"]] {}
           (friend/authenticated
            ;; no-op
            nil))

(defpage "/login" {}
  login-html)

(defpage "/logout" {}
  (clear-identity (resp/redirect (str @context-path "/"))))
