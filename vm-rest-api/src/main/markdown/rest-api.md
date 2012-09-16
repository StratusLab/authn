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

Root Resource
=============

The root resource of the server (`/`) does *not* require
authentication.  It is used to provide information about the current
status of the service.

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
cookie based authentication mechanism. 

The workflow for authentication is to POST user credentials to the
`login` resource that then returns an authentication token (cookie) to
the client to be used in future requests.  The cookie can be removed
by the client manually or by visiting the `logout` resource.

`/login` (GET)
--------------

Provides a form that can be posted to the same URL to log into
the server.

### Supported MIME Types
  * HTML
  * JSON

### HTTP Status Codes
  * 200: server provides message about posting credentials


`/login` (POST)
---------------

Users authenticate with the server by sending credentials to the
service via a POST.  The HTTP entity with the request should be in
"application/x-www-form-urlencoded" format with "username" and
"password" fields.  Alternately, this can be provided in JSON or EDN
format. 

If the credentials are valid, then the server will send a successful
`200` status code along with a cookie (authentication token) that
must be included in future requests to the server.  If the login
request was from a previous redirect, the server may send a `302`
response with the referring URL.

The user will be redirected to the 

### Accepted MIME Types
  * application/x-www-form-urlencoded
  * JSON
  * EDN

### HTTP Status Codes
  * 200: authentication succeeded message with authentication cookie
  * 302: redirect to previous resource
  * 401: invalid credentials were presented


`/logout` (GET)
---------------

Accessing this resource will cause the server to pass an empty,
expired cookie (authentication token) to the client.  This has the
effect of deleting the cookie and requiring that the client
re-authenticate with the server.

Rather than visiting this resource, the client can also just delete
the authentication cookie manually.

It is not an error to access this resource without an existing
authentication cookie.

### HTTP Status Codes
  * 200: response with empty, expired authn. cookie returned
  * 302: may redirect client to the root resource


VM Resources
============

These resources allow virtual machines to be created, manipulated, and
deleted.  Accessing any of these resources requires a valid
authentication cookie from the client.  All of the resources will
return an authorization required `401` status for un-authenticated
requests.

`/vm` (GET)
--------------

Provides a form for launching a virtual machine.  This form is a
subset of the full set of options that can be specified with a
template. 

### HTTP Status Codes
  * 200: successful response with list of offers
  * 401: unauthorized request


`/vm` (POST)
------------

This will create a new virtual machine on the server.  The data to
associate with the offer can be passed as key value pairs in the
"application/x-www-form-urlencoded" formatted entity.  More complete
virtual machine templates can be provided in JSON or EDN format.

The response will provide the newly-created offer's identifier:

The HTTP response in this case is `302` (Found/Redirect) with the URI
of the new offer in the "Location" header.

### Accepted MIME Types
  * urlencoded form
  * JSON
  * EDN

### Response MIME Types
  * HTML
  * JSON
  * EDN

### HTTP Status Codes
  * 302: redirect to URL of new offer
  * 401: unauthorized request


`/vm/:vmid` (GET)
-----------------

Show the status of a particular virtual machine.  This will return a
status code of `200` for existing resources along with the status of
the virtual machine.

### Response MIME Types
  * HTML
  * JSON
  * EDN

### HTTP Status Codes
  * 200: successful response with contents of named offer
  * 401: unauthorized request
  * 404: non-existant resource or user cannot access this offer
  * 410: server *may* provide this response for a deleted offer


`/vm/:vmid` (PUT)
-----------------

This will update the status of the virtual machine.  The data to
update with the offer may be passed as key value pairs in the
"application/x-www-form-urlencoded" formatted entity or via JSON or
EDN structures.  Not all fields can be updated; trying to update a
read-only field will result in an error.

### Response MIME Types
  * HTML
  * JSON
  * EDN

### HTTP Status Codes
  * 200: successful response to update offer
  * 400: bad request (e.g. trying to update an invalid field)
  * 401: unauthorized request
  * 403: user may not perform this request (e.g. approval without 
    sufficient priviledges)
  * 404: non-existant resource or user cannot access this offer


`/vm/:vmid` (DELETE)
--------------------

This will kill a virtual machine.  This immediately kills the machine
and releases any reserved resources.

### HTTP Status Codes
  * 200: successful response to kill virtual machine
  * 401: unauthorized request
  * 403: user may not perform this request (e.g. deleting VM s/he does
    not own)
