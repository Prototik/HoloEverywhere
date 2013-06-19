#!/usr/bin/env python2
import os, os.path, sys, tarfile, platform, urllib, xml.dom.minidom, subprocess

def extract(tar_url, extract_path='.'):
    tar = tarfile.open(tar_url, 'r')
    for item in tar:
        tar.extract(item, extract_path)

def call(command, hide_output=False):
	if hide_output:
		subprocess.check_call(command, env=os.environ, stdout=os.open("/dev/null", os.W_OK))
	else:
		subprocess.check_call(command, env=os.environ)

def call_output(command):
	return subprocess.check_output(command, env=os.environ)

def which(program):
    def is_exe(fpath):
        return os.path.isfile(fpath) and os.access(fpath, os.X_OK)
    fpath, fname = os.path.split(program)
    if fpath:
        if is_exe(program):
            return program
    else:
        for path in os.environ["PATH"].split(os.pathsep):
            path = path.strip('"')
            exe_file = os.path.join(path, program)
            if is_exe(exe_file):
                return exe_file
    return False

def main():
	if which("sudo") and which("apt-get"):
		print " # Update system..."
		call(["sudo", "apt-get", "update", "-qq"], True);
		if platform.machine() == "x86_64":
			call(["sudo", "apt-get", "install", "-qq", "--force-yes", "libgd2-xpm", "ia32-libs", "ia32-libs-multiarch"], True);
	else:
		print "Cannot update system, this script runned on Ubuntu?"

	print " # Setup Android SDK"
	extract(urllib.urlretrieve("http://dl.google.com/android/android-sdk_r21-linux.tgz")[0])
	os.environ["ANDROID_HOME"] = os.getcwd() + "/android-sdk-linux"
	os.environ["PATH"] += ":" + os.environ["ANDROID_HOME"] + "/tools"
	os.environ["PATH"] += ":" + os.environ["ANDROID_HOME"] + "/platform-tools"
	call(["android", "update", "sdk", "--filter", "platform-tools,android-16", "--no-ui", "--force"], True)

	print " # Install support library r13"
	call(["mvn", "install:install-file",
	"-Dfile=android-support-v4-r13.jar",
	"-DgroupId=com.google.android", "-DartifactId=support-v4",
	"-Dversion=r13", "-Dpackaging=jar", "-DgeneratePom=true"], True)
	
	if os.environ["TRAVIS_SECURE_ENV_VARS"] == "false":
		unsecure();
	else:
		secure();
		
def unsecure():
	print " # [UNSECURE] Build"
	call(["mvn", "clean", "install", "--batch-mode", "-DskipTests=true"])

def secure():	
	if "[deploy snapshot]" in call_output(["git", "log", "-1", "--pretty=%B", os.environ["TRAVIS_COMMIT"]]):
		print " # [SECURE] Generate one-time keystore..."
		keystore = os.getcwd() + "/holoeverywhere.jks"
		alias = "holoeverywhere"
		keypass = "holoeverywhere"
		storepass = "holoeverywhere"
		call(["keytool", "-genkey", "-keystore", keystore, "-alias", alias, "-storepass", storepass, "-keypass", keypass,
			"-keyalg", "RSA", "-keysize", "2048", "-validity", "120", "-dname", "CN=HoloEverywhere One-Time Key,O=HoloEverywhere,C=US"]);
		
		print " # [SECURE] Create maven config"
		maven_config = os.getcwd() + "/.maven.xml"
		create_maven_config(maven_config, username = os.environ["SONATYPE_USERNAME"], password = os.environ["SONATYPE_PASSWORD"])
			
		print " # [SECURE] Build & Deploy..."
		call(["mvn", "clean", "install", "deploy", "--batch-mode", "-DskipTests=true", "-DrepositoryId=holoeverywhere-repo-snapshots", "--settings=" + maven_config,
			"-Dholo.sign.keystore=" + keystore, "-Dholo.sign.alias=" + alias, "-Dholo.sign.storepass=" + storepass, "-Dholo.sign.keypass=" + keypass])
	else:
		print " # [SECURE] Build"
		call(["mvn", "clean", "install", "--batch-mode", "-DskipTests=true"])

def create_maven_config(filename, username, password):  
	m2 = xml.dom.minidom.parse(os.path.expanduser("~") + '/.m2/settings.xml')

	settings = m2.getElementsByTagName("settings")[0]
	
	serversNodes = settings.getElementsByTagName("servers")
	if not serversNodes:
		serversNode = m2.createElement("servers")
		settings.appendChild(serversNode)
	else:
		serversNode = serversNodes[0]
		
	serverNode = m2.createElement("server")
	
	serverId = m2.createElement("id")
	serverId.appendChild(m2.createTextNode("holoeverywhere-repo-snapshots"))
	serverNode.appendChild(serverId)
	
	serverUser = m2.createElement("username")
	serverUser.appendChild(m2.createTextNode(username))
	serverNode.appendChild(serverUser)
	
	serverPass = m2.createElement("password")
	serverPass.appendChild(m2.createTextNode(password))
	serverNode.appendChild(serverPass)
 
	serversNode.appendChild(serverNode)
	
	f = open(filename, 'w')
	f.write(m2.toxml())
	f.close()
	
if __name__ == "__main__":
    main()
