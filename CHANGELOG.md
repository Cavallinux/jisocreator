# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Recursive add with cancellation coverage**: Added `AddFileActionRecursiveTest` (7 tests) to validate recursive add behavior, immediate and mid-recursion cancellation, empty directories, and progress monitor integration.
- **`IsoTreeNode` behavior coverage**: Added `IsoTreeNodeTest` (8 tests) to validate `addNode` vs `addLeafNode`, duplicate prevention, and directory recursion semantics.
- **`MainActionsManager` enum**: Extracted `MAINACTION` instantiation from `ActionsManager` into a dedicated `MainActionsManager` enum, allowing `MainAction` to be initialized without triggering GUI-bound singleton dependencies (e.g. `ImageRegister`). This makes `MainAction.main()` safe to call in non-graphical / headless environments.
- **`CommandLineMessages` i18n class**: New NLS bundle class (`gui/i18n/CommandLineMessages`) backed by `i18n/commandline/messages_en.properties` and `messages_es.properties`, externalizing all CLI-facing strings: version format, app description, example usage, option descriptions, help table title, column headers, and the syntax-line prefix. Added `COMMANDLINE_BUNDLE_MESSAGE` constant to `INLSBundleMessages`.
- **`JISOCreatorCommandLineHelpFormatter`**: New `HelpFormatter` subclass that overrides `getTableDefinition(Iterable<Option>)` to inject the i18n table caption and column headers from `CommandLineMessages`, and sets the syntax prefix via `setSyntaxPrefix`. Used by `JISOCreatorCommandLineParser` as its default formatter.
- **CLI i18n unit tests**:
  - `CommandLineMessagesTest` (5 tests) — validates all 13 static fields in `CommandLineMessages` are non-null, non-blank, and resolved (not in `!key!` error format), with dedicated checks for version, example usage, table column, and syntax-prefix messages.
  - `MainActionsManagerTest` (6 tests) — verifies `MAINACTION` constant, non-null `MainAction` instance, enum singleton contract, empty initial `layoutFilePath`, and parser referential equality with `CommandLineParserManager`.
  - `JISOCreatorCommandLineHelpFormatterTest` (6 tests) — verifies `getTableDefinition` overrides the caption and column headers with i18n messages, preserves parent column styles and rows, and that `printHelp` completes without throwing.
- **Updated coverage for existing test classes**:
  - `MessagesBundleTest` — extended to include `COMMANDLINE_BUNDLE_MESSAGE` in bundle-load and field-resolution checks.
  - `JISOCreatorCommandLineParserTest` — added assertion that the default `helpFormatter` field is an instance of `JISOCreatorCommandLineHelpFormatter` (total: **20 tests**).
  - `OSExplorerTest` — `testIsRootForSystemRoot` simplified: now asserts that every entry returned by `getRoots()` is identified as a root, since `OSExplorer` always uses `File.listRoots()`.

### Changed
- **Project version line**: Updated `pom.xml` from `0.2.0` to `0.2.1-SNAPSHOT` for the current development branch.
- **Surefire runtime compatibility flags**: Added `-XX:+EnableDynamicAgentLoading -Xshare:off` in `maven-surefire-plugin` `argLine` to improve local/CI compatibility with test instrumentation on recent JDKs.
- **`AddFileAction` workflow**: Refactored add flow to recursive processing with cooperative cancellation (`IProgressMonitor#isCanceled`), incremental progress updates, and GUI refresh in `finally` so partial additions are reflected even when the operation is interrupted.
- **ISO tree API refinement**: Added `ITreeNode#addLeafNode(ITreeNode)` and implemented it in `IsoTreeNode` to support direct child insertion without implicit recursion; kept `addNode` for recursive directory expansion.
- **Windows launcher behavior**: Updated `res/mkisofs/jisocreator.bat` to create `%LOCALAPPDATA%\jisocreator\logs`, set `-Dpath.logs`, and run via `javaw` in background mode.
- **Spanish i18n cleanup**: Normalized multiple labels/tooltips in `src/main/resources/i18n/*/messages_es.properties` (accented characters and wording consistency in main actions, OS/ISO explorer, preferences, and show-ISO-info dialogs).
- **`CommandLineOptionsManager` option descriptions**: All six `Option` descriptors (load, help, version, license, input, output) are now sourced from `CommandLineMessages` instead of hardcoded English strings, enabling full i18n of the help output.
- **`JISOCreatorCommandLineParser` refactoring**:
  - Extracted `APP_NAME`, `APP_VERSION`, `JVM_VERSION`, `JVM_VENDOR`, `OS_NAME`, and `LOWERCASE_APPNAME` as static constants to avoid repeated reflective lookups.
  - `printVersion()` now formats output using `CommandLineMessages.commandLineVersionMessage`.
  - `buildHelpHeader()` delegates to `CommandLineMessages.commandLineAppDescriptionMessage`.
  - `buildHelpFooter()` delegates to `CommandLineMessages.commandLineExampleUsageMessage` with `Strings.CI.replace` to substitute the app name.
  - Default `helpFormatter` field changed from an inline `HelpFormatter` to `JISOCreatorCommandLineHelpFormatter`.
- **`ActionsManager` enum**: `MAINACTION` constant removed; `MainAction` instantiation moved to the new `MainActionsManager`.
- **`OSExplorer` root initialization**: Simplified constructor to always call `File.listRoots()`, removing the previous Windows/Linux conditional branch that used `user.home` file listing on Windows.
- **`OSTreeContentProvider`**: Replaced `instanceof File` cast pattern with a Java 16+ pattern-matching `instanceof`, and changed empty-return from `new File[0]` to `List.of().toArray(File[]::new)`. Removed the special-case null check in `getElements`, delegating entirely to `getChildren`.
- **`ShowOnlyDirectoriesFilter`**: Replaced `Files.isDirectory(path)` with `Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)` to correctly classify symbolic links. Removed `OSAndIsoExplorerManager.isRoot()` check—root directories are directories themselves and are now included through the regular directory predicate. Log level changed from `INFO` to `DEBUG`.
- **Documentation synchronization for branch test scope**: Updated `README.md` and `TESTING.md` to reflect current suite inventory and counts (**128 tests in 33 classes**), including new coverage in `AddFileActionRecursiveTest` and `IsoTreeNodeTest`.
- **Test suite growth**: After all branch changes, the test suite now comprises **147 tests across 36 test classes**.

### Fixed
- **Cross-platform parser compatibility assertion**: Updated `XMLIsoFilesystemParserCompatibilityTest` to assert the expected fixture-specific `volumeID` on both Linux and Windows, avoiding platform-dependent false negatives.
- **`ShowOnlyDirectoriesFilter` symlink handling**: Using `LinkOption.NOFOLLOW_LINKS` prevents symbolic links from being treated as directories on Linux, ensuring the tree viewer only shows actual directories.
- **Selection event log noise**: Changed `log.info` → `log.debug` in `OSExplorerSashFormSelectionChangedListener` for selection-changed events, reducing log verbosity at the INFO level during normal UI interaction.

## [0.2.0] - 2026-07-12

### Added
- **Drag and drop OS->ISO workflow**: Added `JISOCreatorDragSourceAdapter` and `JISOCreatorViewerDropAdapter` to support dragging files from the OS explorer table and dropping them directly into the ISO explorer table.
- **Programmatic add-files flow for drops**: `AddFileAction` now supports `run(List<File>)` so dropped files reuse the same add-to-layout pipeline and refresh/status updates used by the regular action flow.
- **Installation guide**: Added top-level `INSTALL` document covering source build, zip-based installation for Linux/Windows, first-run setup, and runtime verification commands.
- **XML compatibility fixtures**: Added legacy XML fixtures under `src/test/resources/xml/` (`c267b1a84ea9429088ce5530122e5c8a.xml` and `1560506077724c8e97f4d1664f851193.xml`) for parser compatibility and round-trip checks.

### Changed
- **Project version**: Updated `pom.xml` to release version `0.2.0` (from `0.1.6`).
- **XML parser architecture cleanup**: Finalized package split into `model/parser/decl` and `model/parser/xml` (`IsoFilesystemParser`, `XMLIsoFilesystemParser`, `XMLIsoFilesystemContract`, `XMLIsoFilesystemContractMapper`) with Jackson XML + Woodstox as the default stack.
- **Dependencies**: Consolidated XML stack to Jackson XML (`jackson-dataformat-xml`) + Woodstox (`woodstox-core`), added `xmlunit-core` for XML assertions, and updated Lombok to `1.18.46`.
- **Provider package organization**: Flattened provider implementation packages from `model/providers/impl/*` to `model/providers/*` and removed obsolete tree adapter classes/tests (`TreeContentAdapter`, `TreeLabelAdapter`) while keeping `TableProviderAdapter`.
- **OS explorer internals**: Refined comparators, filters, content/label/table providers, listeners, and `OSExplorer` traversal behavior to improve consistency between tree/table navigation and action enablement.
- **Documentation synchronization**: Updated `README.md`, `TESTING.md`, and `INSTALL` to match current build profiles, CLI smoke workflows, test inventory, and runtime guidance.

### Fixed
- **Cross-platform hidden-file detection in `HideHiddenFilesFilter`**: Added fallback check for Unix-style hidden names (`.` prefix) so filtering behaves consistently on Linux and Windows.
- **Cross-platform XML path normalization**: `XMLIsoFilesystemContractMapper` now normalizes serialized file paths to forward slashes (`/`) using `file.getPath().replace(File.separatorChar, '/')`, preserving layout round-trip compatibility across platforms.
- **XML compatibility assertions**: Updated mapper/parser compatibility tests and normalized fixture line endings to avoid false negatives caused by platform-specific separators or CRLF/LF differences.

## [0.1.6] - 2026-07-06

### Added
- **Expanded unit test suite (multi-phase implementation)**:
  - Added coverage for action layer (`MainAction`, `JISOCreatorBaseAction`, `SaveISO9660ImageThread`)
  - Added coverage for managers/enums (`IOManager`, `OSAndIsoExplorerManager`, CLI/language/ISO options managers)
  - Added coverage for parser contracts and XML DTO/mapper layers
  - Added coverage for providers/comparators/filters in OS/ISO explorers
  - Added coverage for i18n message bundle resolution
  - Test suite now totals **113 tests in 31 test classes**, all passing
- **Unit tests for command line interface**: New test classes covering `CommandLineOptionsManager` (7 tests) and `JISOCreatorCommandLineParser` (19 tests) - option declarations, short/long option parsing, mutually exclusive options, missing/invalid arguments, help/version output, and `handleCommandLine` input/output path validation. Total test count now 44 (up from 18)
- **License printing from CLI**: New `--license` / `-L` command line option prints the bundled GPLv3 license text (`ICommandLineParser#printLicense`, backed by `IOManager`/`IOUtils` loading `files/license.txt`)
- **ISO filesystem status info**: Main window status bar now shows ISO filesystem information (volume/size) after loading or saving a layout, via `IsoFileSystem` and `MainWindow`
- **XML parser compatibility regression test**: Added `XMLIsoFilesystemParserCompatibilityTest` (2 tests) plus legacy fixture `src/test/resources/xml/c267b1a84ea9429088ce5530122e5c8a.xml` to ensure deserialize/serialize compatibility against historical layouts

### Changed
- **Documentation sync after testing phases**:
  - Updated `README.md` testing section to reflect current scope and statistics
  - Rebuilt `TESTING.md` to match real test inventory and counts
  - Resolved stale/merged content in testing documentation and aligned statistics with CI/local runs
- **Resource reorganization**: Images (`img/`), i18n bundles (`i18n/`) and default configuration (`conf/defaultconfig.properties`) moved out of `util/res` into `src/main/resources` top-level folders for clearer separation between code and resources
- **ISO length/info calculation**: Refactored `IsoFileSystem` and `ShowIsoInformationAction`/`OpenIsoLayoutAction` to compute and print ISO size/info more accurately
- `AddFileAction` and `IsoExplorerSashForm` updated to keep the status bar in sync with ISO filesystem changes
- **Project documentation synchronization**: Updated README/TESTING to reflect current versioning (`0.2.0-SNAPSHOT`), current GitHub Actions workflow path (`.github/workflows/maven.yml`), actual CI trigger scope, Maven profile matrix, runtime log-path configuration (`-Dpath.logs`), and current parser/test architecture
- **XML parser implementation migrated**: Refactored XML layout parser to Jackson XML + Woodstox (`model/parser/xml/XMLIsoFilesystemParser`) and moved parser contracts/interfaces into `model/parser/decl` + `model/parser/xml`
- **Dependency updates**: Replaced XStream with Jackson XML stack, added XMLUnit for XML assertions, and updated Lombok to `1.18.46`
- **Project version advanced**: `pom.xml` now targets `0.2.0-SNAPSHOT`

## [0.1.5] - 2026-06-26

### Added
- **Internationalization (i18n)**: Full NLS support with English and Spanish message bundles for About dialog, main window, main actions, OS/ISO explorer actions, preferences dialog and Show ISO Info dialog (`INLSBundleMessages` + per-component `*Messages` classes)
- **Language preference**: New `JISOCreatorLanguageOptions` enum and General preferences page option to switch the application UI language at runtime/startup
- **ISO metadata support**: Added Volume ID, Publisher ID and Application ID fields when generating ISO images, exposed through the Show ISO Layout Information dialog
- **XML layout serialization**: Added/refined serialization and deserialization hooks for saving and loading ISO layouts as XML

### Changed
- Refactored `ShowIsoLayoutInformationDialog` with I18N messages and updated volume ID validation logic
- Refactored Preferences dialog instantiation and preference node management (`PreferencesNodeManager`)
- Refactored the XML ISO filesystem parser (`model/parser`) for improved robustness
- Refactored command line parser (`JISOCreatorCommandLineParser`) and fixed related parsing bugs

### Fixed
- Fixed UTF-8 encoding issues when reading/writing files
- Fixed command line parser manager bugs

## [0.1.4] - 2026-06-20

### Added
- Bundled `mkisofs.exe` 3.02a10 for the Windows distribution
- Error handling and user feedback when loading an XML layout via the GUI fails

### Fixed
- Fixed bug where the `mkisofs` child process could remain alive after exiting the application
- Fixed bug in OS explorer initial root file system loading
- Fixed bug in Save As XML action

### Changed
- Refactored XML layout management, ISO/OS explorer model class hierarchy, and load/save XML and ISO file handling
- Refactored ISO image saving progress reporting
- Refactored Save As ISO and Save As XML Layout actions

## [0.1.3] - 2026-06-15

### Added
- **Command line interface**: First implementation of the JisoCreator command line arguments parser (`CommandLineParserManager`, `CommandLineOptionsManager`) supporting `--load`, `--input`, `--output`, `--help` and `--version`
- Support for a `JISOCREATOR_LOGS_PATH` environment variable to customize the logs directory
- `--add-opens` JVM option added to startup scripts for Java module compatibility

### Changed
- Refactored usage/help text and command line parsing logic
- Refactored exception handling during load-XML-arguments validation

### Fixed
- Fixed assembly ID configuration issue in the Maven Assembly plugin (Windows/GTK packaging)
- Fixed dependency management issue affecting the GTK (Linux) distribution

## [0.1.2] - 2026-06-14

### Added
- **Centralized Singleton Managers**: Introduced enum-based managers for consistent singleton pattern across the application
  - `ActionsManager` enum - Centralized management of all application actions
  - `GUIManager` enum - Centralized GUI component management
  - `OSAndIsoExplorerManager` enum - Unified management of OS and ISO explorers
- **Version Variables in POM**: Extracted version numbers into POM properties for easier maintenance
- **Enhanced Multi-Monitor Support**: Improved monitor detection logic in MainWindow for reliable multi-monitor setups

### Changed

#### Architecture & Design Patterns
- **Actions Refactoring**: Complete refactoring of all action classes to use the centralized `ActionsManager` enum singleton pattern:
  - `AboutAction` - Centralized through ActionsManager
  - `ExitApplicationAction` - Centralized through ActionsManager
  - `MainAction` - Centralized through ActionsManager
  - `NewIsoLayoutAction` - Centralized through ActionsManager
  - `OpenIsoLayoutAction` - Centralized through ActionsManager
  - `PreferencesAction` - Centralized through ActionsManager
  - `SaveAsDropDownMenuAction` - Centralized through ActionsManager
  - `SaveAsIsoAction` - Centralized through ActionsManager
  - `SaveAsXMLAction` - Centralized through ActionsManager
  - `AddFileAction` - Centralized through ActionsManager
  - `GoToParentAction` - Centralized through ActionsManager
  - `OpenAction` - Centralized through ActionsManager
  - `RefreshExplorerAction` - Centralized through ActionsManager
  - `ShowHiddenFilesAction` - Centralized through ActionsManager
  - `DeleteIsoEntryAction` - Centralized through ActionsManager
  - `GoToIsoEntryParentAction` - Centralized through ActionsManager
  - `OpenIsoEntryAction` - Centralized through ActionsManager
  - `ShowIsoInformationAction` - Centralized through ActionsManager

- **Utility Classes Singleton Pattern**:
  - `ImageUtils` - Refactored to use enum-based singleton pattern (ImageRegister)
  - `IOUtils` - Refactored to use enum-based singleton pattern

- **GUI Components Refactoring**:
   - `IsoExplorerSashForm` - Complete refactoring:
     - Now uses `IsoExplorerActionsManager` for centralized action access
     - Toolbar actions added through manager pattern: OPENISOENTRY, GOTOISOPARENT, SHOWISOINFO, DELETEISOENTRY
     - Listener management delegated through manager's actions
     - Cleaner initialization without direct action instantiation
   - `OSExplorerSashForm` - Complete refactoring:
     - Now uses `OSExplorerActionsManager` for centralized action access
     - Toolbar actions added through manager pattern: OPENFILEACTION, GOTOPARENTACTION, REFRESHACTION, ADDFILEACTION, SHOWHIDDENFILES
     - OSExplorer access through `OSAndIsoExplorerManager.INSTANCE.getOsExplorer()`
     - Listener management delegated through manager's actions
   - `MainWindow` - Complete refactoring:
     - Removed internal static singleton pattern, now managed by architecture
     - Enhanced multi-monitor support with `determinateActiveMonitor()` method
     - Improved toolbar layout with reusable separator component
     - Simplified menu and toolbar management through `ActionsManager`
     - Better initialization and lifecycle management
   - `PreferencesDialog` - Updated to use centralized managers
   - `AboutDialog` - Updated to use centralized managers
   - `BaseProgressMonitorDialog` - Updated to work with new architecture

- **OS and ISO Explorer Instantiation**:
  - `OSExplorer` - Now accessed through `OSAndIsoExplorerManager`
  - Improved singleton lifecycle management
  - Better encapsulation of explorer instances

- **Event Listeners Refactoring** - Major separation of concerns with dedicated listener classes:
   - `OSExplorerSashFormDoubleClickListener` - Implements `IDoubleClickListener`:
     - Handles double-click events from TreeViewer (expand/collapse navigation) and TableViewer (file opening)
     - Uses `OSExplorerActionsManager.OPENFILEACTION` to open selected files
     - Uses `GUIManager` to access UI components for tree expansion/collapse operations
     - Comprehensive JSON logging of double-click events
   - `OSExplorerSashFormSelectionChangedListener` - Implements `ISelectionChangedListener` with full manager integration:
     - Uses `OSExplorerActionsManager` to enable/disable actions based on selection state
     - Uses `GUIManager` to access MainWindow and OSExplorer components
     - Uses `OSAndIsoExplorerManager` to check if current file is root directory
     - Handles both TreeViewer and TableViewer selection events
     - Provides intelligent action state management based on file system navigation
     - Logs selection events with JSON format for debugging
   - `ISOExplorerSashFormDoubleClickListener` - Implements `IDoubleClickListener`:
     - Handles double-click events for ISO explorer navigation and file opening
     - Uses `IsoExplorerActionsManager.OPENISOENTRY` action integration
     - Manages tree expand/collapse and node navigation
     - Accesses UI components through `GUIManager`
   - `ISOExplorerSashFormSelectionChangedListener` - Implements `ISelectionChangedListener`:
     - Manages ISO explorer selection state and action enablement
     - Distinguishes between TreeViewer (directory) and TableViewer (file) selections
     - Uses `IsoExplorerActionsManager` for dynamic action state management
     - Handles edge cases and SWT library bugs gracefully

#### Test Updates
- **OSExplorerTest**: Updated to access `OSExplorer` through `OSAndIsoExplorerManager` instead of direct getInstance()
  - `testGetInstance()` now verifies singleton management through the centralized manager
  - Ensures tests follow the same architectural patterns as production code

#### Dependencies
- Updated POM with version properties for better maintainability
- Maven profiles for platform-specific builds remain active (linux, windows)

### Benefits of 0.1.2 Refactoring

1. **Cleaner Architecture**: Single point of access for singletons through enum-based managers
2. **Thread Safety**: Enum singleton pattern provides inherent thread safety
3. **Maintainability**: Centralized initialization and configuration of application components
4. **Testability**: Easier to mock and test components through manager interfaces
5. **Separation of Concerns**: Clear separation between action handlers, GUI components, and utility classes
6. **Code Consistency**: Unified approach to singleton pattern implementation across the codebase
7. **Better Encapsulation**: Manager enums provide controlled access to singleton instances

### Detailed Changes Summary

#### Manager Integration in GUI Components

**IsoExplorerSashForm Manager Integration**:
- Uses `IsoExplorerActionsManager` singleton enum for toolbar actions:
  - `IsoExplorerActionsManager.OPENISOENTRY.getAction()` - Open ISO entries
  - `IsoExplorerActionsManager.GOTOISOPARENT.getAction()` - Navigate to parent
  - `IsoExplorerActionsManager.SHOWISOINFO.getAction()` - Show ISO info
  - `IsoExplorerActionsManager.DELETEISOENTRY.getAction()` - Delete ISO entries
- Actions also serve as `IDoubleClickListener` and `ISelectionChangedListener`
- Clean separation of concerns: UI layout and action management

**OSExplorerSashForm Manager Integration**:
- Uses `OSExplorerActionsManager` singleton enum for toolbar actions:
  - Dynamically adds all actions using `Arrays.stream(OSExplorerActionsManager.values()).forEach()`
  - `OSExplorerActionsManager.OPENFILEACTION.getAction()` - Open files
  - `OSExplorerActionsManager.GOTOPARENTACTION.getAction()` - Navigate to parent
  - `OSExplorerActionsManager.REFRESHACTION.getAction()` - Refresh explorer
  - `OSExplorerActionsManager.ADDFILEACTION.getAction()` - Add files to ISO
  - `OSExplorerActionsManager.SHOWHIDDENFILES.getAction()` - Toggle hidden files
- Uses `OSAndIsoExplorerManager.INSTANCE.getOsExplorer()` for model initialization
- Actions serve dual purpose as listeners and toolbar contributions
- Private `fillToolbarAndCoolbars()` method for cleaner toolbar/coolbar management
- Selection helper methods: `getTableSelection()` and `getTreeSelection()` returning `IStructuredSelection`

**MainWindow Manager Integration**:
- Uses `ActionsManager` singleton enum for all application actions
- Menu management through: NEWISOLAYOUTACTION, OPENISOLAYOUTACTION, SAVEASXMLACTION, SAVEASISOACTION, SAVEAISDROPDOWNMENUACTION, EXITACTION, PREFERENCESACTION, ABOUTACTION
- Toolbar management with actions and separator component
- Multi-monitor support through `determinateActiveMonitor()` method

**Event Listener Manager Integration**:

**OS Explorer Listeners**:
- **OSExplorerSashFormSelectionChangedListener**: Selection change listener with full manager integration
   - Listens to selection changes from both TreeViewer and TableViewer
   - Uses `OSExplorerActionsManager` to dynamically enable/disable toolbar actions:
     - OPENFILEACTION enabled only when TableViewer has selection
     - ADDFILEACTION enabled when TreeViewer has valid directory selection
     - GOTOPARENTACTION enabled only when not at file system root
   - Uses `GUIManager.INSTANCE.getMainWindow()` to access and update UI components:
     - Updates osTableText with current path
     - Updates osDirectoriesTable with current directory's contents
   - Uses `OSAndIsoExplorerManager.INSTANCE.getOsExplorer()` to check root directory status
   - Provides comprehensive logging of selection events using ToStringBuilder with JSON style
   - Handles SWT library edge cases gracefully

- **OSExplorerSashFormDoubleClickListener**: Double-click listener for file system navigation
   - Listens to double-click events from both TreeViewer and TableViewer
   - TreeViewer events: Expand/collapse directory navigation
   - TableViewer events: Open files using `OPENFILEACTION` through `OSExplorerActionsManager`
   - Uses `GUIManager.INSTANCE.getMainWindow()` to access tree viewer for expansion state
   - Comprehensive logging using JSON format for debugging

**ISO Explorer Listeners**:
- **ISOExplorerSashFormSelectionChangedListener**: Selection change listener for ISO navigation
   - Listens to selection changes from TreeViewer and TableViewer
   - Uses `IsoExplorerActionsManager` to manage action states:
     - OPENISOENTRY and DELETEISOENTRY enabled only for TableViewer selections
     - GOTOISOPARENT enabled when selected node is not root
   - Updates UI components through `GUIManager`
   - Handles SWT library edge cases

- **ISOExplorerSashFormDoubleClickListener**: Double-click listener for ISO content navigation
   - TreeViewer events: Expand/collapse node navigation with tree state tracking
   - TableViewer events: Open ISO entries using `OPENISOENTRY` action
   - Uses `GUIManager` to access UI components
   - Smart tree state management for improved UX

#### Summary of Manager Enums

**Action Classes Refactored**: 18 total
- Main Actions: AboutAction, ExitApplicationAction, MainAction, NewIsoLayoutAction, OpenIsoLayoutAction, PreferencesAction, SaveAsDropDownMenuAction, SaveAsIsoAction, SaveAsXMLAction (9 classes)
- OS Explorer Actions: AddFileAction, GoToParentAction, OpenAction, RefreshExplorerAction, ShowHiddenFilesAction (5 classes)
- ISO Explorer Actions: DeleteIsoEntryAction, GoToIsoEntryParentAction, OpenIsoEntryAction, ShowIsoInformationAction (4 classes)

**GUI Components Updated**: 8 major components
- MainWindow, IsoExplorerSashForm, OSExplorerSashForm
- Dialogs: PreferencesDialog, AboutDialog, BaseProgressMonitorDialog
- Event Listeners: ISODirectoriesMenuListener, OSDirectoriesMenuListener, OSExplorerSashFormSelectionChangedEvent

**Utility Improvements**:
- ImageUtils refactored to enum singleton (ImageRegister)
- IOUtils refactored to enum singleton
- SaveISO9660ImageThread updated for new architecture

**Test Updates**:
- OSExplorerTest: Updated to use OSAndIsoExplorerManager
- Maintained 18 tests with 100% pass rate

### Project Statistics (v0.1.2)
- **Total Commits in this Release**: 20+
- **Files Modified**: 37+
- **Component Categories Changed**: 5 (Actions, GUI, Utilities, Event Listeners, Tests)
- **Manager Enums**: 4 (ActionsManager, IsoExplorerActionsManager, OSExplorerActionsManager, OSAndIsoExplorerManager)
- **Event Listeners Refactored**: 6 total classes with full separation of concerns
  - Menu Listeners: ISODirectoriesMenuListener, OSDirectoriesMenuListener
  - Double-Click Listeners: ISOExplorerSashFormDoubleClickListener, OSExplorerSashFormDoubleClickListener
  - Selection Changed Listeners: ISOExplorerSashFormSelectionChangedListener, OSExplorerSashFormSelectionChangedListener
- **Test Classes Updated**: 1 (OSExplorerTest)
- **Total Tests**: 18 (all passing)
- **Code Quality**: Improved through centralized management and listener separation patterns
- **Lines of Code Refactored**: 1500+

## [0.1.1] - 2026-06-11

### Added
- Initial unit testing framework setup with JUnit 5 and Mockito
- Comprehensive test suite for OSExplorer and IOUtils
- Testing documentation in TESTING.md
- GitHub Actions CI/CD pipeline for automated testing and building

### Features
- Dual file explorers for OS and ISO browsing
- ISO 9660 image creation and editing
- Layout editor for ISO design
- Preferences configuration
- Comprehensive logging with Log4j2

---

## Format Notes

This changelog follows the [Keep a Changelog](https://keepachangelog.com/) format:

- **Added** - for new features
- **Changed** - for changes in existing functionality
- **Deprecated** - for soon-to-be removed features
- **Removed** - for now removed features
- **Fixed** - for any bug fixes
- **Security** - in case of security vulnerabilities

## Version Links

- [Unreleased](https://github.com/Cavallinux/jisocreator/compare/v0.2.0...HEAD) - In development
- [0.2.0](https://github.com/Cavallinux/jisocreator/compare/v0.1.6...v0.2.0) - Drag & drop workflow, parser/package cleanup, docs + test synchronization
- [0.1.6](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.6) - Expanded unit testing and documentation synchronization
- [0.1.5](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.5) - i18n support, ISO metadata (Volume/Publisher/Application ID)
- [0.1.4](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.4) - Windows mkisofs update, XML layout fixes
- [0.1.3](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.3) - Command line interface
- [0.1.2](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.2) - Centralized singleton manager architecture
- [0.1.1](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.1) - First major release with testing framework

---

For more information about the project, see:
- [README.md](README.md) - General project information
- [TESTING.md](TESTING.md) - Testing guide and framework documentation
- [pom.xml](pom.xml) - Maven build configuration
