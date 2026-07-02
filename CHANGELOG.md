# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - 0.1.6-SNAPSHOT

### Added
- **License printing from CLI**: New `--license` / `-L` command line option prints the bundled GPLv3 license text (`ICommandLineParser#printLicense`, backed by `IOManager`/`IOUtils` loading `files/license.txt`)
- **ISO filesystem status info**: Main window status bar now shows ISO filesystem information (volume/size) after loading or saving a layout, via `IsoFileSystem` and `MainWindow`

### Changed
- **Resource reorganization**: Images (`img/`), i18n bundles (`i18n/`) and default configuration (`conf/defaultconfig.properties`) moved out of `util/res` into `src/main/resources` top-level folders for clearer separation between code and resources
- **ISO length/info calculation**: Refactored `IsoFileSystem` and `ShowIsoInformationAction`/`OpenIsoLayoutAction` to compute and print ISO size/info more accurately
- `AddFileAction` and `IsoExplorerSashForm` updated to keep the status bar in sync with ISO filesystem changes

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

- [0.1.6-SNAPSHOT](https://github.com/Cavallinux/jisocreator/compare/v0.1.5...feature/v0.1.6) - Unreleased, in development
- [0.1.5](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.5) - i18n support, ISO metadata (Volume/Publisher/Application ID)
- [0.1.4](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.4) - Windows mkisofs update, XML layout fixes
- [0.1.3](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.3) - Command line interface
- [0.1.2](https://github.com/Cavallinux/jisocreator/releases/tag/v0.1.2) - Centralized singleton manager architecture
- [0.1.1](https://github.com/Cavallinux/jisocreator/releases/tag/0.1.1) - First major release with testing framework

---

For more information about the project, see:
- [README.md](README.md) - General project information
- [TESTING.md](TESTING.md) - Testing guide and framework documentation
- [pom.xml](pom.xml) - Maven build configuration
