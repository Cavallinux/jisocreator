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
java --enable-native-access=ALL-UNNAMED -jar target/jisocreator-0.1.2.jar
```

### With Debug Mode
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
     --enable-native-access=ALL-UNNAMED \
     -jar target/jisocreator-0.1.2.jar
```

## Project Structure

```
jisocreator/
├── src/main/java/cl/cavallinux/jisocreator/
│   ├── action/           # Action handlers and commands
│   │   ├── decl/         # Action declarations
│   │   ├── main/         # Main application actions
│   │   ├── isoexplorer/  # ISO explorer specific actions
│   │   ├── osexplorer/   # OS explorer specific actions
│   │   └── jobs/         # Background job threads
│   ├── gui/              # GUI components and windows
│   │   ├── decl/         # GUI declarations
│   │   ├── dialog/       # Dialog components
│   │   ├── listeners/    # Event listeners
│   │   ├── preference/   # Preference dialogs
│   │   ├── sashfom/      # Sash form components
│   │   └── window/       # Main window components
│   ├── instances/        # Singleton managers
│   │   ├── ActionsManager.java       # Centralized action management
│   │   ├── GUIManager.java           # GUI component management
│   │   ├── ImageRegister.java        # Image resource registry
│   │   ├── IOManager.java            # I/O operations management
│   │   └── ...
│   ├── model/            # Data models and providers
│   │   ├── comparators/  # Custom comparators
│   │   ├── filters/      # File filters
│   │   ├── isoexplorer/  # ISO explorer models
│   │   ├── osexplorer/   # OS explorer models
│   │   └── providers/    # Data providers
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

## Architecture Highlights (v0.1.2+)

### Singleton Manager Pattern

The application uses enum-based singleton managers for centralized component management:

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
 (18 types)      (SashForms,       (OS/ISO)
                  Dialogs,
                  Windows)
```

### Benefits of Manager Pattern

1. **Centralized Control**: All singleton instances managed from one entry point
2. **Thread Safety**: Enum singletons guarantee thread-safe lazy initialization
3. **Testability**: Easy to mock managers for unit testing
4. **Maintainability**: Simplified dependency tracking and initialization order
5. **Scalability**: Easy to add new managed components

## Version

Current version: **0.1.2-SNAPSHOT**

### Version 0.1.2 Changes

This version introduces significant architectural refactoring focused on improving code maintainability and following better design patterns:

#### Key Architectural Improvements

**Centralized Singleton Managers**:
- **ActionsManager**: Enum-based centralized management of all 18 application actions
  - Provides clean, consistent access patterns across the application
  - Ensures proper initialization and lifecycle management
- **GUIManager**: Manages GUI component instances and lifecycle
- **OSAndIsoExplorerManager**: Unified management of file system and ISO explorers
- **ImageRegister**: Centralized image resource management

**Refactored Components** (30+ files):
- **18 Action Classes**: All action classes now centrally managed through `ActionsManager`
- **8 GUI Components**: Updated to use centralized managers and improved patterns
- **2 Utility Classes**: `ImageUtils` and `IOUtils` refactored to use enum singleton pattern
- **1 Test Suite**: Updated to verify singleton management patterns

**Enhanced Multi-Monitor Support**:
- Improved monitor detection logic in `MainWindow`
- Better handling of active shell detection
- Fallback to primary monitor when needed
- More reliable window positioning across multiple displays

**Improved Code Quality**:
- Removed scattered singleton implementations
- Unified approach to dependency management
- Better encapsulation and controlled access
- Enhanced testability with centralized manager pattern

#### Benefits
- **Cleaner Architecture**: Single point of access for singletons through enum-based managers
- **Thread Safety**: Enum singleton pattern provides inherent thread safety
- **Maintainability**: Centralized initialization and configuration of application components
- **Testability**: Easier to mock and test components through manager interfaces
- **Separation of Concerns**: Clear separation between action handlers, GUI components, and utilities
- **Code Consistency**: Unified approach to singleton pattern across the codebase
- **Better Encapsulation**: Manager enums provide controlled access to singleton instances

## License

See LICENSE file for details.
