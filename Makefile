.PHONY : clean default dockerimage

SHELL = /bin/bash

default: dockerimage

# this docker command builds a local docker image
# includes maven and nodejs
# which will be used to build the .war file
warbuilderimage:
	docker build -f Dockerfile.warbuilder -t myimages/metalnx-warbuilder .

# this docker command uses the ./local_maven_repo directory
# to cache the maven artifacts for repeated builds
#
# the local_maven_repo directory keeps your personal maven
# repository (i.e. $HOME/.m2) clean and safe from the "root" user.
#
# it creates the .war file in packaging/docker/
packaging/docker/metalnx.war: warbuilderimage
	docker run -it --rm \
		-v "$$PWD":/usr/src \
		-v "$$PWD"/src:/usr/src/mymaven \
		-v "$$PWD/local_maven_repo":/root/.m2 \
		-w /usr/src/mymaven \
		myimages/metalnx-warbuilder \
		mvn clean package -Dmaven.test.skip=true

# this docker command builds a local docker image. this command
# may result in compilation of the Java source code.
dockerimage: packaging/docker/metalnx.war
	docker build -t myimages/metalnx:latest .

# this docker command only builds a local docker image.
# it DOES NOT compile the Java source code.
#
# this command exists for developers compiling the source outside
# of a docker container, but running the WAR file in a container.
dockerimage_only:
	docker build -t myimages/metalnx:latest .

# this removes:
#  - the .war file
#  - maven artifacts
#  - node and node_modules
#  - other build artifacts
# it does not remove the docker image
clean:
	rm -f packaging/docker/metalnx.war
	docker run -it --rm \
		-v "$$PWD":/usr/src \
		-v "$$PWD"/src:/usr/src/mymaven \
		-v "$$PWD/local_maven_repo":/root/.m2 \
		-w /usr/src/mymaven \
		myimages/metalnx-warbuilder \
		mvn clean
	docker run -it --rm \
		-v "$$PWD"/src:/usr/src/mymaven \
		-w /usr/src/mymaven \
		myimages/metalnx-warbuilder \
		rm -f metalnx-web/package-lock.json
	docker run -it --rm \
		-v "$$PWD"/src:/usr/src/mymaven \
		-w /usr/src/mymaven \
		myimages/metalnx-warbuilder \
		rm -rf metalnx-web/etc
	docker run -it --rm \
		-v "$$PWD"/src:/usr/src/mymaven \
		-w /usr/src/mymaven \
		myimages/metalnx-warbuilder \
		rm -rf metalnx-web/target
	docker run -it --rm \
		-v "$$PWD"/src:/usr/src/mymaven \
		-w /usr/src/mymaven \
		myimages/metalnx-warbuilder \
		rm -rf metalnx-web/node
	docker run -it --rm \
		-v "$$PWD"/src:/usr/src/mymaven \
		-w /usr/src/mymaven \
		myimages/metalnx-warbuilder \
		rm -rf metalnx-web/node_modules
