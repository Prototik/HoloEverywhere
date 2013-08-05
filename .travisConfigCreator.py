#!/usr/bin/env python2
import sys
import os
import os.path
import xml.dom.minidom
 
if os.environ["TRAVIS_SECURE_ENV_VARS"] == "false":
  print "no secure env vars available, sure that you update secure vars before building"
  sys.exit()
  
m2 = xml.dom.minidom.parse(os.path.expanduser("~") + '/.m2/settings.xml')
settings = m2.getElementsByTagName("settings")[0]
 
serversNodes = settings.getElementsByTagName("servers")
if not serversNodes:
  serversNode = m2.createElement("servers")
  settings.appendChild(serversNode)
else:
  serversNode = serversNodes[0]
  
sonatypeServerNode = m2.createElement("server")
sonatypeServerId = m2.createElement("id")
sonatypeServerUser = m2.createElement("username")
sonatypeServerPass = m2.createElement("password")
 
idNode = m2.createTextNode("holoeverywhere-repo-snapshots")
userNode = m2.createTextNode(os.environ["SONATYPE_USERNAME"])
passNode = m2.createTextNode(os.environ["SONATYPE_PASSWORD"])
 
sonatypeServerId.appendChild(idNode)
sonatypeServerUser.appendChild(userNode)
sonatypeServerPass.appendChild(passNode)
 
sonatypeServerNode.appendChild(sonatypeServerId)
sonatypeServerNode.appendChild(sonatypeServerUser)
sonatypeServerNode.appendChild(sonatypeServerPass)
 
serversNode.appendChild(sonatypeServerNode)
  
m2Str = m2.toxml()
f = open(os.environ["NEW_MAVEN_CONFIG"], 'w')
f.write(m2Str)
f.close()
