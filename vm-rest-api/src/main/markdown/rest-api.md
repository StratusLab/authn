% VM REST API Specification
%
% ${project.version}

Introduction
============

The virtual management management API uses RESTful Web Service
technologies.  RESTful web services allow faster service development,
lighter-weight services, easier use by clients, and faster evolution
with changing user requirements.

These web services follow the Resource Oriented Architecture (ROA)
pattern. Virtual machines are mapped into an URI hierarchy; standard
HTTP verbs are used to manipulate those resources.

This document describes three types of resources: the root resource,
authentication resources, and compute resources. Eventually this service will
be expanded to proxy other StratusLab services, providing separate REST APIs
under `/storage`, `marketplace`, etc.

Root Resource
=============

The root resource of the server (`/`) does *not* require
authentication.  It is used to provide information about the current
status of the service(s).

`/` (GET)
---------

This resource provides a simple message about the status of the
current service and some general metrics of resource utilization.  It
also provides the current authentication information of the client.

### Supported MIME Types
  * HTML
  * JSON

### HTTP Status Codes
  * 200: server is correctly configured and running


Authentication Resources
========================

The authentication resources do *not* require authentication.  These
resources allow users to authenticate with the server via a form and
session (cookie) based authentication mechanism. 

The workflow for authentication is to POST user credentials to the `login`
resource that then returns an authentication token session (cookie) to the
client to be used in future requests. The token can be removed by the client
manually or by visiting the `logout` resource.

`/login` (GET)
--------------

Provides a form that can be posted to the same URL to obtain an authentication
token from the server. (This token in the form of a session cookie must be
returned for each subsequent request.)

### Supported MIME Types
  * HTML

### HTTP Status Codes
  * 200: server provides form for posting credentials


`/login` (POST)
---------------

Users authenticate with the server by sending credentials to the service via a
POST. The HTTP entity with the request can be in "form-urlencoded", JSON, or
EDN format with "username" and "password" fields.

If the credentials are valid, then the server will send an authentication
token (session cookie) to the user. The server may send either a `200` status
code or a `302` response if there was an initial referring URL.

### Accepted MIME Types
  * form-urlencoded
  * JSON
  * EDN

### HTTP Status Codes
  * 200: authentication succeeded message with authentication cookie
  * 302: redirect to previous resource
  * 401: invalid credentials were presented


`/logout` (GET)
---------------

Accessing this resource will cause the server to send headers to the client
that remove the authentication token (session cookie). Rather than visiting
this resource, the client can also just delete the authentication token
(session cookie) manually.

It is not an error to access this resource without an existing authentication
cookie.

### HTTP Status Codes
  * 200: response with header to remove authentication token
  * 302: may redirect client to the root resource


Compute Resources
=================

These resources allow virtual machines to be created, manipulated, and
deleted. Accessing any of these resources requires a valid authentication
cookie from the client. All of the resources will return an authorization
required `401` status for un-authenticated requests.

`/compute` (GET)
----------------

The root compute resource of the server (`/compute`) does *not* require
authentication.

It will provide some general metrics of resource utilization on the platform.
If the user is authenticated, then additional information is provided giving
the user's resource utilization metrics. 

### Supported MIME Types
  * HTML
  * JSON
  * EDN

### HTTP Status Codes
  * 200: service is working and provides utilization metrics


`/compute/vm` (GET)
-------------------

Provides a form for launching a virtual machine.  This form is a
subset of the full set of options that can be specified with a
template in JSON or EDN.

### HTTP Status Codes
  * 200: successful response with list of virtual machines
  * 401: unauthorized request


`/compute/vm` (POST)
--------------------

This will create a new virtual machine on the server. The data to associate
with the virtual machine can be passed in form-urlencoded, JSON, or EDN
format. The form-urlencoded format provides only a subset of the options
available by providing a complete template in JSON or EDN format.

The response will provide the newly-created virtual machine's identifier.

The HTTP response in this case is `302` with the URI of the new virtual
machine in the "Location" header. The body will also include the raw machine
identifier.

### Accepted MIME Types
  * urlencoded form
  * JSON
  * EDN
  * OVF? (should work be done to support this?)

### Response MIME Types
  * HTML
  * JSON
  * EDN

### HTTP Status Codes
  * 302: redirect to URL of new virtual machine
  * 401: unauthorized request


`/compute/vm/:vmid` (GET)
-------------------------

Show the status of a particular virtual machine.  This will return a
status code of `200` for existing resources along with the status of
the virtual machine.

### Response MIME Types
  * HTML
  * JSON
  * EDN

### HTTP Status Codes
  * 200: successful response with contents of named virtual machine
  * 401: unauthorized request
  * 404: non-existant resource or user cannot access this virtual machine
  * 410: server *may* provide this response for a deleted virtual machine


`/compute/vm/:vmid` (PUT)
-------------------------

This will update the status of the virtual machine. The data to update with
the virtual machine may be passed as form-urlencoded, JSON, or EDN formats.
Not all fields can be updated; trying to update a read-only field will result
in an error.

### Accepted MIME Types
  * url-formencoded (subset of full template)
  * JSON
  * EDN
  
### Response MIME Types
  * HTML
  * JSON
  * EDN

### HTTP Status Codes
  * 200: successful response to update virtual machine
  * 400: bad request (e.g. trying to update an invalid field)
  * 401: unauthorized request
  * 403: user may not perform this request (e.g. state change without 
    sufficient priviledges)
  * 404: non-existant resource or user cannot access this virtual machine


`/compute/vm/:vmid` (DELETE)
----------------------------

This will kill a virtual machine.  This immediately kills the machine
and releases any reserved resources.

### HTTP Status Codes
  * 200: successful response to kill virtual machine
  * 401: unauthorized request
  * 403: user may not perform this request (e.g. deleting VM s/he does
    not own)
  * 409: conflict with deleting the resource 
