#!/bin/bash

export MVN_HOME=~cavallinux/dev/bin/maven/3.2.3

$MVN_HOME/bin/mvn install:install-file -Dfile=org.eclipse.swt/org.eclipse.swt-linux-x86_64.jar -DgroupId=org.eclipse.swt -DartifactId=swt-linux-x86_64 -Dversion=3.4.0 -Dpackaging=jar

$MVN_HOME/bin/mvn install:install-file -Dfile=org.eclipse.jface/org.eclipse.jface.jar -DgroupId=org.eclipse.jface -DartifactId=jface -Dversion=3.4.0 -Dpackaging=jar

$MVN_HOME/bin/mvn install:install-file -Dfile=org.eclipse.jface/org.eclipse.core.commands.jar -DgroupId=org.eclipse.core -DartifactId=commands -Dversion=3.4.0 -Dpackaging=jar

$MVN_HOME/bin/mvn install:install-file -Dfile=org.eclipse.jface/org.eclipse.equinox.common.jar -DgroupId=org.eclipse.equinox -DartifactId=common -Dversion=3.4.0 -Dpackaging=jar

$MVN_HOME/bin/mvn install:install-file -Dfile=org.eclipse.jface/org.eclipse.osgi.jar -DgroupId=org.eclipse -DartifactId=osgi -Dversion=3.4.0 -Dpackaging=jar

$MVN_HOME/bin/mvn install:install-file -Dfile=org.eclipse.jface/org.eclipse.ui.workbench.jar -DgroupId=org.eclipse.ui -DartifactId=workbench -Dversion=3.4.0 -Dpackaging=jar