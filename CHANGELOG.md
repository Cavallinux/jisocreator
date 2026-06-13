# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.2-SNAPSHOT] - 2026-06-13

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
  - `IsoExplorerSashForm` - Updated to work with centralized managers
  - `OSExplorerSashForm` - Updated to work with centralized managers
  - `MainWindow` - Optimized instantiation and monitor detection logic
  - `PreferencesDialog` - Updated to use centralized managers
  - `AboutDialog` - Updated to use centralized managers
  - `BaseProgressMonitorDialog` - Updated to work with new architecture

- **OS and ISO Explorer Instantiation**:
  - `OSExplorer` - Now accessed through `OSAndIsoExplorerManager`
  - Improved singleton lifecycle management
  - Better encapsulation of explorer instances

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

### Project Statistics (v0.1.2)
- **Total Commits in this Release**: 15
- **Files Modified**: 30+
- **Test Classes Updated**: 1 (OSExplorerTest)
- **Total Tests**: 18 (all passing)
- **Code Quality**: Improved through centralized management patterns

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

- [0.1.2-SNAPSHOT](https://github.com/Cavallinux/jisocreator/compare/0.1.1...feature/v0.1.2) - Unreleased, in development
- [0.1.1](https://github.com/Cavallinux/jisocreator/releases/tag/0.1.1) - First major release with testing framework

---

For more information about the project, see:
- [README.md](README.md) - General project information
- [TESTING.md](TESTING.md) - Testing guide and framework documentation
- [pom.xml](pom.xml) - Maven build configuration
