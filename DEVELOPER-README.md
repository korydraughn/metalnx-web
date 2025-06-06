# Developer notes

## API references

* Spring 4.1.9 Ref https://docs.spring.io/spring/docs/4.1.9.RELEASE/spring-framework-reference/html/
* Spring 4.1.9 API https://docs.spring.io/spring/docs/4.1.9.RELEASE/javadoc-api/

## Testing setup
Metalnx has been updated to use the same testing.properties setup and settings.xml from jargon
tests.  See https://github.com/DICE-UNC/jargon/wiki/Setting-up-unit-tests for some orientation.

Those jargon notes specify how to configure a target iRODS instance as a test platform. It is recommended that testing
not be done on a shared server and certainly on a production server!

### settings

Unit tests rely on two properties files:

* testing.properties - generated by pom.xml, identical to the settings and properties utilized by the Jargon unit tests. NB that
there is still work to do to fully integrate the jargon unit testing facilities into the metanlx tests, so right now it's mostly
surfacing those properties in the existing spring config.

* test.metalnx.properties - generated by pom.xml. This is the test analogue to the normal /etc/irods-ext/metalnx.properties
used in production deployments

For Junit tests, the /test/resources spring config files should pick both of these up by classpath: references

### maven setup

Add Jargon profile information to the settings.xml in maven per the above Jargon notes, something like this. Note that this
refers to a private VM running on VirtualBox on the developer's machine, and this form of test setup is highly recommended!

```xml
<profile>
  <id>irods421.irodslocal</id>
  <properties>
    <jargon.test.data.directory>/Users/conwaymc/temp/irodsscratch/</jargon.test.data.directory>
    <jargon.test.irods.admin>rods</jargon.test.irods.admin>
    <jargon.test.irods.admin.password>rods</jargon.test.irods.admin.password>
    <jargon.test.irods.user>test1</jargon.test.irods.user>
    <jargon.test.irods.password>test</jargon.test.irods.password>
    <jargon.test.irods.resource>test1-resc</jargon.test.irods.resource>
    <jargon.test.irods.user2>test2</jargon.test.irods.user2>
    <jargon.test.irods.password2>test</jargon.test.irods.password2>
    <jargon.test.irods.resource2>test1-resc2</jargon.test.irods.resource2>
    <jargon.test.irods.resource3>test1-resc3</jargon.test.irods.resource3>
    <jargon.test.irods.user3>test3</jargon.test.irods.user3>
    <jargon.test.irods.password3>test</jargon.test.irods.password3>
    <jargon.test.kerberos.user />
    <jargon.test.irods.host>irods421.irodslocal</jargon.test.irods.host>
    <jargon.test.irods.port>1247</jargon.test.irods.port>
    <jargon.test.irods.zone>zone1</jargon.test.irods.zone>
    <jargon.test.resource.group>testResourceGroup</jargon.test.resource.group>
    <jargon.test.user.group>jargonTestUg</jargon.test.user.group>
    <jargon.test.irods.userDN>test1DN</jargon.test.irods.userDN>
    <jargon.test.irods.scratch.subdir>jargon-scratch</jargon.test.irods.scratch.subdir>
    <jargon.test.option.exercise.remoteexecstream>true</jargon.test.option.exercise.remoteexecstream>
    <jargon.test.option.exercise.ticket>true</jargon.test.option.exercise.ticket>
    <jargon.test.option.exercise.audit>false</jargon.test.option.exercise.audit>
    <jargon.test.option.exercise.workflow>false</jargon.test.option.exercise.workflow>
    <jargon.test.option.exercise.filesystem.mount>false</jargon.test.option.exercise.filesystem.mount>
    <test.option.mount.basedir>/home/mconway/temp/basedir</test.option.mount.basedir>
    <jargon.test.pam.user>pam</jargon.test.pam.user>
    <jargon.test.pam.password>pam</jargon.test.pam.password>
    <test.option.pam>false</test.option.pam>
    <test.option.ssl.configured>false</test.option.ssl.configured>
    <test.option.distributed.resources>false</test.option.distributed.resources>
    <test.option.registration>false</test.option.registration>
    <test.option.federated.zone>false</test.option.federated.zone>
    <test.option.kerberos>false</test.option.kerberos>
    <test.option.strictACL>false</test.option.strictACL>
    <test.rest.port>8888</test.rest.port>
    <jargon.test.federated.irods.admin>rods</jargon.test.federated.irods.admin>
    <jargon.test.federated.irods.admin.password>test</jargon.test.federated.irods.admin.password>
    <jargon.test.federated.irods.user>test1</jargon.test.federated.irods.user>
    <jargon.test.federated.irods.password>test</jargon.test.federated.irods.password>
    <jargon.test.federated.irods.resource>test1-resc</jargon.test.federated.irods.resource>
    <jargon.test.federated.irods.host>fedzone2</jargon.test.federated.irods.host>
    <jargon.test.federated.irods.port>1247</jargon.test.federated.irods.port>
    <jargon.test.federated.irods.zone>fedzone2</jargon.test.federated.irods.zone>
    <test.option.python>true</test.option.python>
  </properties>
</profile>


```

In addition, an additional profile for Metalnx is required, like this...


```xml

<profile>
			<id>metalnx-local</id>
			<properties>
				<metalnx.auth.scheme>STANDARD</metalnx.auth.scheme>
				<metalnx.ssl.policy>CS_NEG_REFUSE</metalnx.ssl.policy>
				<metalnx.packing.streams>true</metalnx.packing.streams>
				<metalnx.compute.checksum>true</metalnx.compute.checksum>
				<selenium.test.hostname>localhost</selenium.test.hostname>
				<selenium.test.chrome.driver>webdriver.chrome.driver</selenium.test.chrome.driver>
				<selenium.test.chrome.driver.loaction>C:/Users/pateldes/driver/chromedriver.exe</selenium.test.chrome.driver.loaction>
	        		<metalnx.enable.tickets>true</metalnx.enable.tickets>
	       		 <metalnx.enable.upload.rules>false</metalnx.enable.upload.rules>
	        		<metalnx.download.limit>100</metalnx.download.limit>
				<metalnx.starttls.enable>true</metalnx.starttls.enable>
				<metalnx.smtp.auth>true</metalnx.smtp.auth>
				<metalnx.transport.protocol>smtp</metalnx.transport.protocol>
        			<metalnx.jwt.issuer>gov.nih.niehs</metalnx.jwt.issuer>
				<metalnx.jwt.algo>HS256</metalnx.jwt.algo>
				<metalnx.jwt.algo>thisisasecret</metalnx.jwt.algo>
			</properties>
		</profile>

```

Note that these config files are in the src/metalnx/test-scripts/sample-maven-settings.xml

Here it's pointing to a Postgres database, pretty soon we'll show an example with a local
database like Derby, that's probably a better plan.

For selenium automation testing we need to download browser specific drivers refer to http://www.seleniumhq.org/download/

JWT configuration is optional if one is utilizing the search and notification, along with other supporting microservices


### custom metalnx template for master page

Using Thymeleaf templating the various views are fragments that are encapsulated in a template.html file. This is generated on build in the emc-metalnx-ui-admin subproject
in the pom.xml. By default, if the property 'metalnx.custom.template' is undefined in settings.xml or elsewhere, maven will simply copy the defaultTemplate.html to the template.html
during the build. If this property is defined, the maven install will copy from the provided source location to the template.html file. This allows site-specific customization
of the overall template in the case where simple css or image changes are insufficient to achieve a theme.

By default this can be ignored during the build process.

If this behavior is wanted, then the following should be added to settings.xml, probably in the above metalnx.properties

```xml
<metalnx.custom.template>/Users/conwaymc/Documents/workspace-niehs-dev/metalnx-niehs-plugins/web-assets/opt/irods-ext/metalnx/template.html</metalnx.custom.template>

```

### Generating test properties and running tests

A mvn install will cause these properties files to be generated. Once the irods server
and database are initially installed and configured, and the properties are all set
correctly in settings.xml (be sure to activate the appropriate profiles in maven!) You should
get a clean build running the tests, and once maven has run to get the properties, running
unit tests in eclipse, etc should also work fine.

### Docker support for a build container

A Docker build image (Dockerfile.testbuild) has been added in docker-test-framework. This mounts the source directory and provides a command prompt to build the Metalnx war, including providing node support

See comments in Docker file, but essentially cd into the top level directory of the git repo and then, after building,  issue

```

docker run -it -v `pwd`/src/:/usr/src/metalnx metalnx-build /bin/bash

```

This will provide a command prompt and allow maven commands, mainly:

```

mvn package -DskipTests

```

