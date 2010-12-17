#!/usr/bin/env python
import xmlrpclib

class SafeTransportWithCert(xmlrpclib.SafeTransport):
    __cert_file = "grid.cert"
    __key_file  = "grid.key"
    
    def make_connection(self, host):
        host_with_cert = (host, {
                'key_file'  :  self.__key_file,
                'cert_file' :  self.__cert_file
                } )
        return  xmlrpclib.SafeTransport.make_connection(self, host_with_cert)
    

#server_url = "http://localhost:2633/RPC2"
#server_url = "https://onehost-172:8444/xmlrpc"
server_url = "https://localhost:8443/authn_proxy/xmlrpc"

transport = SafeTransportWithCert()

server = xmlrpclib.ServerProxy(server_url, transport = transport)
#server = xmlrpclib.ServerProxy(server_url)

rslt = server.one.vmpool.info("dummy:pass", -1)

print rslt

