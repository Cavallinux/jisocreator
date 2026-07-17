# JisoCreator

A **MKISOFS Frontend** built with Eclipse technologies, providing a graphical user interface for creating and managing ISO 9660 images.

## Overview

JisoCreator is a Java-based desktop application that simplifies the process of creating and editing ISO images. It features dual file explorers for managing both the operating system file system and ISO image contents, drag-and-drop transfer from OS explorer to ISO explorer, a command-line interface for scripted usage, and multi-language (i18n) support.

## Installation

For full installation instructions (requirements, build, package install, and first-run setup), see [INSTALL](INSTALL).

## Requirements

- **Java**: JDK 21 or higher
- **Maven**: 3.6 or higher
- **Operating System**: Linux/Windows (x86_64 profiles available)

## Project Dependencies

### Core Frameworks
- **Eclipse SWT 3.133.0** - Standard Widget Toolkit for native GUI components
- **Eclipse JFace 3.39.0** - Higher-level UI framework built on SWT
- **Eclipse Core Commands 3.12.500** - Command pattern framework
- **Eclipse Equinox Common 3.20.300** - Equinox common utilities
- **Eclipse OSGi 3.24.100** - Module system and services
- **Eclipse UI Workbench 3.138.0** - Workbench framework

### Utilities & Libraries
- **Lombok 1.18.46** - Annotation processor for code generation (getters, setters, etc.)
- **Jackson XML 2.20.0** - XML serialization library for ISO layout and configuration management
- **Woodstox 7.1.1** - StAX XML processor used by Jackson XML
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
- `windows`: `win32.win32.x86_64` (excludes the `gtk.linux.x86_64` SWT artifact)
- `linux-cmdlinemode`: Linux runtime plus a CLI smoke execution (`-h`) wired into the `exec-maven-plugin`
- `windows-cmdlinemode`: Windows runtime plus a CLI smoke execution (`-h`) wired into the `exec-maven-plugin`

To activate a non-default profile, pass `-P<profile-id>` in the Maven command, e.g. `-Pwindows` or `-Plinux-cmdlinemode`. Only one platform profile (`linux`/`windows`) or its `-cmdlinemode` variant should be active at a time, since they select mutually exclusive SWT platform dependencies.

#### Using the Profiles

```bash
# Default (linux) platform build
mvn clean package

# Windows platform build
mvn clean package -Pwindows

# Linux build + CLI smoke execution (runs the packaged jar with -h)
mvn clean package exec:exec -Plinux-cmdlinemode

# Windows build + CLI smoke execution (runs the packaged jar with -h)
mvn clean package exec:exec -Pwindows-cmdlinemode
```

The `*-cmdlinemode` profiles reuse the same `exec-maven-plugin` configuration as the default build (native access flags, optional debug agent) but append `-h` to the executed command, making them convenient for a quick post-build sanity check of the CLI in CI or locally.

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
- **XMLUnit**: XML diff/assertion utilities for parser compatibility tests (v2.11.0)
- **Maven Surefire Plugin**: Test execution plugin (v3.2.5)

### Running Tests

#### Run All Tests (Linux — default profile)
```bash
mvn test
```

#### Run All Tests on Windows
```bash
mvn test -Pwindows
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
├── action/      # Action-layer tests (main/jobs/base/osexplorer actions)
├── gui/         # i18n message bundle tests
├── instances/   # Manager and enum singleton tests
├── model/       # Parser, providers, comparators, filters, explorers
└── util/        # IO utility tests
```

**Current Test Statistics**: 147 tests total across 36 test classes, all passing.

Current coverage includes:
- Critical workflow tests (`MainAction`, `SaveISO9660ImageThread`, `JISOCreatorBaseAction`, `AddFileActionRecursive`)
- Parser/contract/mapper tests (`IsoFilesystemParser`, `XMLIsoFilesystem*`)
- Explorer/provider/comparator/filter tests (OS and ISO, including `IsoTreeNode`)
- CLI/manager/i18n tests (`CommandLine*`, `MainActionsManager`, `IOManager`, `OSAndIsoExplorerManager`, message bundles including `CommandLineMessages`)

### Test Features
- **Temporary Directory Support**: Uses JUnit 5's `@TempDir` for isolated file operations
- **Singleton Pattern Testing**: Validates OSExplorer singleton implementation
- **File System Operations**: Comprehensive testing of file and directory handling
- **Path Manipulation**: Tests for file path concatenation and validation
- **XML Compatibility Validation**: Legacy XML layout deserialization and round-trip contract comparison, including cross-platform path separator normalization (Windows backslash → Unix forward slash)
- **Action and Workflow Validation**: Tests for command parsing branches and save-thread progress behavior

For detailed testing information, see `TESTING.md`.

## Continuous Integration

### GitHub Actions

The project uses GitHub Actions for automated testing and building. The CI pipeline is configured in `.github/workflows/maven.yml` and includes:

#### Workflow Triggers
- **Push** (all branches)
- **Pull requests** (all branches)

#### CI Pipeline Steps
1. **Setup Java**: Configures JDK 21 for the build environment
2. **Dependency Caching**: Caches Maven dependencies for faster builds
3. **Build + test lifecycle**: Runs `mvn -B compile package --file pom.xml`

#### Workflow Status
[![Java CI with Maven](https://github.com/Cavallinux/jisocreator/actions/workflows/maven.yml/badge.svg)](https://github.com/Cavallinux/jisocreator/actions/workflows/maven.yml)

#### Local CI Simulation
To simulate the CI pipeline locally:
```bash
# Full CI simulation
mvn -B compile package --file pom.xml

# Quick verification (compilation + tests only)
mvn clean compile test
```

## Running the Application

### From Maven
```bash
mvn clean package
mvn exec:exec
```

This command uses the exec-maven-plugin configured in pom.xml and includes:
- Classpath configuration
- Native access permissions (--enable-native-access=ALL-UNNAMED)
- Debug port available on 5005 if needed

### Using the Maven Execution Profiles

The `-cmdlinemode` profiles bundle build + run into a single command, executing the packaged jar with `-h` as a CLI smoke test:

```bash
# Linux CLI smoke run
mvn clean package exec:exec -Plinux-cmdlinemode

# Windows CLI smoke run
mvn clean package exec:exec -Pwindows-cmdlinemode
```

To run the GUI with the Windows SWT dependency instead of the default Linux one:

```bash
mvn clean package exec:exec -Pwindows
```

See [Maven Profiles](#maven-profiles) for the full list of available profiles.

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

### Log Output Directory

Log4j2 writes logs to `./logs` by default. You can override this directory with:

```bash
java -Dpath.logs=/custom/log/path --enable-native-access=ALL-UNNAMED -jar target/jisocreator.jar
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
jisocreator --input /path/to/layout.xml --output /path/to/existing-output.iso
```

> Note: in the current CLI validation, `--input` and `--output` are both checked as existing, accessible filesystem paths before ISO generation starts.

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
│   │   ├── i18n/         # NLS message bundles (About, ISO/OS explorer, preferences, CLI, etc.)
│   │   │   └── CommandLineMessages.java   # i18n for all CLI-facing strings
│   │   ├── listeners/    # Event listeners
│   │   ├── preference/   # Preference pages (general, MKISOFS options)
│   │   ├── sashfom/      # Sash form components
│   │   └── window/       # Main window components
│   ├── instances/        # Singleton managers
│   │   ├── ActionsManager.java            # Centralized action management (GUI actions)
│   │   ├── MainActionsManager.java        # Headless-safe MainAction singleton
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
│   │   │   ├── JISOCreatorCommandLineParser.java      # CLI parser (i18n-aware, uses CommandLineMessages)
│   │   │   └── JISOCreatorCommandLineHelpFormatter.java # Custom HelpFormatter with i18n table headers
│   │   ├── comparators/  # Custom comparators
│   │   ├── filters/      # File filters
│   │   ├── isoexplorer/  # ISO explorer models
│   │   ├── osexplorer/   # OS explorer models
│   │   ├── parser/       # XML layout parsing (decl/ + xml/ implementations)
│   │   └── providers/    # Data providers
│   └── util/             # Utility classes
├── src/main/resources/   # Configuration and resources
│   ├── i18n/             # Message bundles per component (messages_en/es.properties)
│   ├── img/              # Icons and SVG assets
│   ├── conf/             # Default configuration files
│   ├── files/            # Bundled files (e.g. license.txt)
│   └── log4j2.xml        # Logging configuration
├── src/test/java/cl/cavallinux/jisocreator/  # Unit tests (SWT-free where possible)
│   ├── action/           # Action-layer tests (main/jobs/base/osexplorer actions)
│   │   ├── decl/         # JISOCreatorBaseAction tests
│   │   ├── jobs/         # SaveISO9660ImageThread tests
│   │   ├── main/         # MainAction tests
│   │   └── osexplorer/   # AddFileAction recursive behavior tests
│   ├── gui/i18n/         # Message bundle (i18n) tests (includes CommandLineMessagesTest)
│   ├── instances/        # Manager and enum singleton tests (CLI parser/options, IOManager, MainActionsManager, explorer manager)
│   ├── model/            # Parsers, providers, comparators, filters, explorer models
│   │   ├── cmdline/      # JISOCreatorCommandLineParser + JISOCreatorCommandLineHelpFormatter tests
│   │   ├── comparators/  # OS/ISO directories-first comparator tests
│   │   ├── filters/      # Hidden files / directories-only filter tests
│   │   ├── isoexplorer/  # IsoFileSystem / IsoTreeNode / TreeNode tests
│   │   ├── parser/       # decl (IsoFilesystemParser) + xml (Jackson-backed parser/contract) tests
│   │   └── providers/    # decl adapters + package-scoped OS/ISO tree/table/label providers tests
│   └── util/             # IO utility tests
├── src/test/resources/   # Test fixtures (e.g. legacy XML layout for compatibility tests)
├── res/                  # Native launch scripts and bundled mkisofs binaries
│   ├── linux/            # Linux launch script
│   └── mkisofs/          # Windows mkisofs binary and launch script
├── pom.xml               # Maven configuration
├── TESTING.md            # Detailed testing documentation and test inventory
└── README.md             # This file
```

## Features

- **Dual File Explorers**: Browse OS file system and ISO contents simultaneously
- **Drag and Drop to ISO Layout**: Drag files from OS explorer and drop them into the ISO explorer table to add entries quickly
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

The application uses enum-based singleton managers for centralized component management (`ActionsManager`, `MainActionsManager`, `GUIManager`, `IsoExplorerActionsManager`, `OSExplorerActionsManager`, `OSAndIsoExplorerManager`, `ImageRegister`, `IOManager`, `CommandLineParserManager`, `PreferencesNodeManager`, among others):

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

UI text is externalized into per-component NLS message bundles under `src/main/resources/i18n/` (e.g. `mainwindow`, `mainactions`, `osexplorer`, `isoexplorer`, `preferencedialog`, `aboutdialog`, `showisoinfodialog`, `commandline`), each with `messages_en.properties` and `messages_es.properties`. The active language is selectable from the General preferences page (`JISOCreatorLanguageOptions`) and applied at startup via `MainAction`.

The `commandline` bundle (`CommandLineMessages`) covers all CLI-facing strings: version output format, application description, example usage, individual option descriptions, help table caption, column headers, and the syntax-line prefix. `CommandLineOptionsManager` reads option descriptions from this bundle so the help output respects the active locale.

### Command Line Interface

`CommandLineParserManager` wraps a `JISOCreatorCommandLineParser` (built on Apache Commons CLI) exposing `--load`, `--input`, `--output`, `--help`, `--version` and `--license` options, allowing the application to be launched in headless/scripted scenarios in addition to its GUI mode. The help table rendered by `--help` uses `JISOCreatorCommandLineHelpFormatter`, a custom `HelpFormatter` subclass that injects i18n column headers and table caption from `CommandLineMessages`.

### XML Parser Layer

XML layout parsing is implemented through `IsoFilesystemParser` (`model/parser/decl`) and the `XMLIsoFilesystemParser` implementation (`model/parser/xml`) backed by Jackson XML. The mapper normalizes file path separators to forward slashes (`/`) before writing to XML, ensuring that layouts saved on Windows are byte-identical to those saved on Linux. Compatibility is validated with legacy XML fixtures under `src/test/resources/xml/`.

### Testing Architecture

The test suite (147 tests / 36 classes, see [TESTING.md](TESTING.md)) favors SWT-independent coverage so most tests run headlessly without a display:

- **Stub/record-based fakes over mocks**: Domain interfaces like `ITreeNode` are exercised with local `record`/anonymous implementations rather than Mockito mocks, keeping tests fast and free of native/SWT dependencies.
- **Real objects for CLI parsing**: `MainAction` and CLI-related tests build real `JISOCreatorCommandLineParser` instances instead of mocking Apache Commons CLI's `CommandLine`, working around a known incompatibility between Mockito's inline mock maker (ByteBuddy) and newer JDKs.
- **Cross-platform compatibility**: `HideHiddenFilesFilter` detects both DOS hidden-attribute files (Windows) and Unix-style dot-prefix hidden files. `XMLIsoFilesystemContractMapper` normalizes path separators to forward slashes so serialized XML is portable across platforms. Tests run cleanly on both Linux (`mvn test`) and Windows (`mvn test -Pwindows`).
- **Layered coverage**: parser/contract mappers → tree/table/label providers and adapters → comparators/filters → CLI managers/enums/i18n → critical workflows (`MainAction`, `SaveISO9660ImageThread`, `JISOCreatorBaseAction`, `AddFileActionRecursive`).
- **XMLUnit-based compatibility checks**: legacy XML layout fixtures are diffed against round-tripped output to guarantee backward compatibility of the XML parser.

## Version

- Latest stable release: **0.2.0** (released 2026-07-12)
- Current development: **0.2.1-SNAPSHOT** (`feature/v0.2.1`)

For a complete history of changes across all releases, see [CHANGELOG.md](CHANGELOG.md).

## License

See LICENSE file for details.
