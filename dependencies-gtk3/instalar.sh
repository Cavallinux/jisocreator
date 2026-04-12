#!/bin/bash

mvn install:install-file -Dfile=org.eclipse.swt.gtk.linux.x86_64_3.133.0.v20260225-1014.jar -DgroupId=org.eclipse.swt -DartifactId=swt-linux-x86_64 -Dversion=3.133.0 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.swt.svg_3.132.0.v20251202-1523.jar -DgroupId=org.eclipse.swt -DartifactId=swt-svg -Dversion=3.133.0 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.jface_3.39.0.v20260122-1511.jar -DgroupId=org.eclipse.jface -DartifactId=jface -Dversion=3.133.0 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.commands_3.12.500.v20251103-0733.jar -DgroupId=org.eclipse.core -DartifactId=commands -Dversion=3.133.0 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.equinox.common_3.20.300.v20251111-0312.jar -DgroupId=org.eclipse.equinox -DartifactId=common -Dversion=3.133.0 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.osgi_3.24.100.v20251215-1416.jar -DgroupId=org.eclipse -DartifactId=osgi -Dversion=3.133.0 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.ui.workbench_3.138.0.v20260204-1601.jar -DgroupId=org.eclipse.ui -DartifactId=workbench -Dversion=3.133.0 -Dpackaging=jar
