# JisoCreator

A **MKISOFS Frontend** built with Eclipse technologies, providing a graphical user interface for creating and managing ISO 9660 images.

## Overview

JisoCreator is a Java-based desktop application that simplifies the process of creating and editing ISO images. It features dual file explorers for managing both the operating system file system and ISO image contents.

## Requirements

- **Java**: JDK 21 or higher
- **Maven**: 3.6 or higher
- **Operating System**: Linux (currently configured for GTK on x86_64)

## Project Dependencies

### Core Frameworks
- **Eclipse SWT 3.133.0** - Standard Widget Toolkit for native GUI components
- **Eclipse JFace 3.39.0** - Higher-level UI framework built on SWT
- **Eclipse Core Commands 3.12.500** - Command pattern framework
- **Eclipse Equinox Common 3.20.300** - Equinox common utilities
- **Eclipse OSGi 3.24.100** - Module system and services
- **Eclipse UI Workbench 3.138.0** - Workbench framework

### Utilities & Libraries
- **Lombok 1.18.44** - Annotation processor for code generation (getters, setters, etc.)
- **XStream 1.4.21** - XML serialization library for configuration management
- **Apache Commons Lang3 3.20.0** - Utility functions for Java language operations
- **JSVG 2.1.0** - SVG rendering support
- **SWT SVG 3.132.0** - SWT integration for SVG graphics

### Logging
- **SLF4J with Log4j2 2.26.0** - Comprehensive logging framework
  - log4j-slf4j2-impl
  - log4j-core

## Building the Project

### Maven Profiles

The project uses Maven profiles to select the SWT platform dependency:

- `linux` (active by default): `gtk.linux.x86_64`
- `windows`: `win32.win32.x86_64`

To activate `windows` profile add -Pwindows in maven command to be used.

### Clean Build
```bash
mvn clean compile
```

### Package as JAR
```bash
mvn clean package
```

This creates an executable JAR file in the `target/` directory with all dependencies configured.

### Build with Debugging
```bash
mvn clean compile -DskipTests
```

## Running the Application

### From Maven
```bash
mvn exec:exec
```

This command uses the exec-maven-plugin configured in pom.xml and includes:
- Classpath configuration
- Native access permissions (--enable-native-access=ALL-UNNAMED)
- Debug port available on 5005 if needed

### From Command Line (after packaging)
```bash
java --enable-native-access=ALL-UNNAMED -jar target/jisocreator-0.0.3.jar
```

### With Debug Mode
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
     --enable-native-access=ALL-UNNAMED \
     -jar target/jisocreator-0.0.3.jar
```

## Project Structure

```
jisocreator/
├── src/main/java/cl/cavallinux/jisocreator/
│   ├── action/           # Action handlers and commands
│   ├── gui/              # GUI components and windows
│   ├── model/            # Data models and providers
│   └── util/             # Utility classes
├── src/main/resources/   # Configuration and resources
│   └── log4j2.xml       # Logging configuration
├── pom.xml              # Maven configuration
└── README.md            # This file
```

## Features

- **Dual File Explorers**: Browse OS file system and ISO contents simultaneously
- **ISO Management**: Create, edit, and explore ISO 9660 images
- **Layout Editor**: Design ISO layouts before creation
- **Preferences**: Customizable application settings
- **Logging**: Comprehensive logging to track operations

## Version

Current version: **0.0.3**

## License

See LICENSE file for details.

