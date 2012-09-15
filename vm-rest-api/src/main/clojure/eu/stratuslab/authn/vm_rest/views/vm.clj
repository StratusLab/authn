(ns eu.stratuslab.authn.vm-rest.views.vm
  (:require [noir.core :refer [defpage]]
            [noir.response :as resp]
            [clojure.tools.logging :as log]
            [eu.stratuslab.authn.vm-rest.vmm.one :as one]))

;;
;; All resources are available to authenticated users only.  The
;; pre-route requiring this is defined in the authn views.  Do not add
;; additional pre-routes for authentication here.
;;

;; html page for starting machine (post to /vm)
(defpage [:get "/vm"] {}
  ;; FIXME: Do this with enlive.
  ;; Should take query parameters to pre-fill some fields.
  "dummy page")

;; post to create new machine
(defpage [:post ["/vm"]] {:as data}
  (one/create data))

;; summary all of the VMs
(defpage [:get "/vm/"] {}
  (one/summary))

;; get the VM status
(defpage [:get "/vm/:vmid"] {:keys [vmid]}
  (one/status vmid))

;; update VM status
(defpage [:put "/vm/:vmid"] {:as data}
  (one/update data))

;; kill a VM
(defpage [:delete "/vm/:vmid"] {:keys [vmid]}
  (one/delete vmid))
