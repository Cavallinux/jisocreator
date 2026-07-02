# JisoCreator

A **MKISOFS Frontend** built with Eclipse technologies, providing a graphical user interface for creating and managing ISO 9660 images.

## Overview

JisoCreator is a Java-based desktop application that simplifies the process of creating and editing ISO images. It features dual file explorers for managing both the operating system file system and ISO image contents, a command-line interface for scripted usage, and multi-language (i18n) support.

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
- **XStream 1.4.21** - XML serialization library for ISO layout and configuration management
- **Apache Commons Lang3 3.20.0** - Utility functions for Java language operations
- **Apache Commons CLI 1.11.0** - Command-line argument parsing
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

## Testing

### Testing Framework

The project includes comprehensive unit tests using:
- **JUnit 5 (Jupiter)**: Modern Java testing framework (v5.10.2)
- **Mockito**: Mocking library for test doubles (v5.7.0)
- **Maven Surefire Plugin**: Test execution plugin (v3.2.5)

### Running Tests

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=OSExplorerTest
```

#### Run Tests During Build
Tests are automatically executed during the Maven build process. To skip tests:
```bash
mvn clean package -DskipTests
```

### Test Structure

```
src/test/java/cl/cavallinux/jisocreator/
├── model/osexplorer/
│   └── OSExplorerTest.java      # File system operations (13 tests)
└── util/
    └── IOUtilsPathTest.java     # File path utilities (5 tests)
```

**Current Test Statistics**: 18 tests total, all passing

### Test Features
- **Temporary Directory Support**: Uses JUnit 5's `@TempDir` for isolated file operations
- **Singleton Pattern Testing**: Validates OSExplorer singleton implementation
- **File System Operations**: Comprehensive testing of file and directory handling
- **Path Manipulation**: Tests for file path concatenation and validation

For detailed testing information, see `TESTING.md`.

## Continuous Integration

### GitHub Actions

The project uses GitHub Actions for automated testing and building. The CI pipeline is configured in `.github/workflows/ci.yml` and includes:

#### Workflow Triggers
- **Push** to `main` branch
- **Pull Requests** targeting `main` branch

#### CI Pipeline Steps
1. **Setup Java**: Configures JDK 21 for the build environment
2. **Dependency Caching**: Caches Maven dependencies for faster builds
3. **Code Compilation**: Runs `mvn clean compile` to verify code compilation
4. **Unit Tests**: Executes all unit tests with `mvn test`
5. **Package Build**: Creates JAR artifacts with `mvn package -DskipTests`

#### Workflow Status
[![CI](https://github.com/Cavallinux/jisocreator/workflows/CI/badge.svg)](https://github.com/Cavallinux/jisocreator/actions)

#### Local CI Simulation
To simulate the CI pipeline locally:
```bash
# Full CI simulation
mvn clean compile test package -DskipTests

# Quick verification (compilation + tests only)
mvn clean compile test
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
java --enable-native-access=ALL-UNNAMED -jar target/jisocreator.jar
```

### With Debug Mode
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
     --enable-native-access=ALL-UNNAMED \
     -jar target/jisocreator.jar
```

## Command Line Interface

In addition to the GUI, JisoCreator exposes a command-line interface (backed by Apache Commons CLI) for scripted/headless usage:

```bash
java -jar target/jisocreator.jar [OPTIONS]
```

| Short | Long        | Description                                  |
|-------|-------------|-----------------------------------------------|
| `-l`  | `--load`    | Load an existing XML layout file into the GUI |
| `-i`  | `--input`   | ISO XML layout file to load (headless mode)   |
| `-o`  | `--output`  | ISO file output path (headless mode)          |
| `-h`  | `--help`    | Show the help message                         |
| `-v`  | `--version` | Show application and JVM version              |
| `-L`  | `--license` | Show the application's GPLv3 license          |

Example:
```bash
jisocreator --license
jisocreator --load /path/to/layout.xml
```

## Project Structure

```
jisocreator/
├── src/main/java/cl/cavallinux/jisocreator/
│   ├── action/           # Action handlers and commands
│   │   ├── decl/         # Action declarations
│   │   ├── main/         # Main application actions (incl. MainAction / CLI entrypoint)
│   │   ├── isoexplorer/  # ISO explorer specific actions
│   │   ├── osexplorer/   # OS explorer specific actions
│   │   └── jobs/         # Background job threads
│   ├── gui/              # GUI components and windows
│   │   ├── decl/         # GUI declarations
│   │   ├── dialog/       # Dialog components
│   │   ├── i18n/         # NLS message bundles (About, ISO/OS explorer, preferences, etc.)
│   │   ├── listeners/    # Event listeners
│   │   ├── preference/   # Preference pages (general, MKISOFS options)
│   │   ├── sashfom/      # Sash form components
│   │   └── window/       # Main window components
│   ├── instances/        # Singleton managers
│   │   ├── ActionsManager.java            # Centralized action management
│   │   ├── GUIManager.java                # GUI component management
│   │   ├── ImageRegister.java             # Image resource registry
│   │   ├── IOManager.java                 # I/O operations management
│   │   ├── CommandLineParserManager.java  # Command-line parser singleton
│   │   ├── CommandLineOptionsManager.java # Command-line options definitions
│   │   ├── JISOCreatorLanguageOptions.java# Supported UI languages (EN/ES)
│   │   ├── JISOCreatorISOLevelOptions.java# Supported ISO 9660 levels (1-4)
│   │   ├── PreferencesNodeManager.java    # Preference page nodes
│   │   └── ...
│   ├── model/            # Data models and providers
│   │   ├── cmdline/      # Command-line parser implementation
│   │   ├── comparators/  # Custom comparators
│   │   ├── filters/      # File filters
│   │   ├── isoexplorer/  # ISO explorer models
│   │   ├── osexplorer/   # OS explorer models
│   │   ├── parser/       # XML layout parsing
│   │   └── providers/    # Data providers
│   └── util/             # Utility classes
├── src/main/resources/   # Configuration and resources
│   ├── i18n/             # Message bundles per component (messages_en/es.properties)
│   ├── img/              # Icons and SVG assets
│   ├── conf/             # Default configuration files
│   ├── files/            # Bundled files (e.g. license.txt)
│   └── log4j2.xml        # Logging configuration
├── res/                  # Native launch scripts and bundled mkisofs binaries
│   ├── linux/            # Linux launch script
│   └── mkisofs/          # Windows mkisofs binary and launch script
├── pom.xml               # Maven configuration
└── README.md             # This file
```

## Features

- **Dual File Explorers**: Browse OS file system and ISO contents simultaneously
- **ISO Management**: Create, edit, and explore ISO 9660 images
- **ISO Metadata**: Configure Volume ID, Publisher ID and Application ID for generated images
- **ISO Level Selection**: Choose the ISO 9660 conformance level (1-4) via preferences
- **Layout Editor**: Design ISO layouts before creation, with XML save/load support
- **Command Line Interface**: Load layouts, build ISOs, print version/help/license headlessly
- **Internationalization (i18n)**: English and Spanish UI languages, switchable via preferences
- **Preferences**: Customizable application settings (general options, MKISOFS options)
- **Logging**: Comprehensive logging to track operations

## Architecture Highlights

### Singleton Manager Pattern

The application uses enum-based singleton managers for centralized component management (`ActionsManager`, `GUIManager`, `IsoExplorerActionsManager`, `OSExplorerActionsManager`, `OSAndIsoExplorerManager`, `ImageRegister`, `IOManager`, `CommandLineParserManager`, `PreferencesNodeManager`, among others):

```
┌─────────────────────────────────────────────────────┐
│           Application Startup                       │
└────────────────────┬────────────────────────────────┘
                     │
          ┌──────────┼──────────┐
          ▼          ▼          ▼
    ┌──────────┐ ┌──────────┐ ┌──────────┐
    │ Actions  │ │   GUI    │ │ Explorers│
    │ Manager  │ │ Manager  │ │ Manager  │
    └────┬─────┘ └────┬─────┘ └────┬─────┘
         │            │            │
    ┌────┴────────────┼────────────┴────┐
    │                 │                  │
    ▼                 ▼                  ▼
 Actions           GUI Comps        Explorers
 (18+ types)      (SashForms,       (OS/ISO)
                   Dialogs,
                   Windows)
```

### Benefits of Manager Pattern

1. **Centralized Control**: All singleton instances managed from one entry point
2. **Thread Safety**: Enum singletons guarantee thread-safe lazy initialization
3. **Testability**: Easy to mock managers for unit testing
4. **Maintainability**: Simplified dependency tracking and initialization order
5. **Scalability**: Easy to add new managed components

### Internationalization (i18n)

UI text is externalized into per-component NLS message bundles under `src/main/resources/i18n/` (e.g. `mainwindow`, `mainactions`, `osexplorer`, `isoexplorer`, `preferencedialog`, `aboutdialog`, `showisoinfodialog`), each with `messages_en.properties` and `messages_es.properties`. The active language is selectable from the General preferences page (`JISOCreatorLanguageOptions`) and applied at startup via `MainAction`.

### Command Line Interface

`CommandLineParserManager` wraps a `JISOCreatorCommandLineParser` (built on Apache Commons CLI) exposing `--load`, `--input`, `--output`, `--help`, `--version` and `--license` options, allowing the application to be launched in headless/scripted scenarios in addition to its GUI mode.

## Version

Current version: **0.1.6-SNAPSHOT**

For a complete history of changes across all releases, see [CHANGELOG.md](CHANGELOG.md).

## License

See LICENSE file for details.
