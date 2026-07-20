# JisoCreator Unit Testing Guide

## Overview
This project uses JUnit 5 and Mockito for unit tests. Tests are located under `src/test/java` and run with Maven Surefire.

## Current branch status (`feature/v0.2.2`)
- Latest branch commit initializes version `0.2.2-SNAPSHOT` in `pom.xml`.
- No test additions/removals were introduced yet; current suite remains **174 tests in 40 classes**.

## Tests Added/Updated in `feature/v0.2.1` (released as v0.2.1)
- `action/osexplorer/AddFileActionRecursiveTest.java` (**10 tests**, up from 7) — recursive add, cancellation, empty directories, progress monitor, full `run(IProgressMonitor)` happy/cancel paths, and builder instantiation
- `action/main/SaveAsIsoActionTest.java` (**new, 8 tests**) — headless builder defaults (`inputXMLLayoutFile`/`outputISOFile` empty, `commandLineMode` false), setter round-trips, instance independence
- `model/isoexplorer/impl/IsoTreeNodeTest.java` (8 tests) — `addNode` vs `addLeafNode`, duplicate prevention, recursion semantics
- `model/parser/XMLIsoFilesystemParserCompatibilityTest.java` (updated coverage)
- `gui/i18n/CommandLineMessagesTest.java` (5 tests) — validates all `CommandLineMessages` fields resolve from the i18n bundle
- `gui/i18n/AddToISODialogMessagesTest.java` (**new, 3 tests**) — validates `AddToISODialogMessages` fields resolve from the `addtoisodialog` i18n bundle (EN/ES)
- `instances/MainActionsManagerTest.java` (6 tests) — both `MainActionsManager` constants (`MAINACTION`, `SAVEASISOACTION`), action types, `layoutFilePath` and parser initialization
- `model/cmdline/JISOCreatorCommandLineHelpFormatterTest.java` (6 tests) — `getTableDefinition` caption/headers i18n override, column styles, `printHelp` smoke
- `model/cmdline/JISOCreatorAttributesTest.java` (**new, 4 tests**) — builder field population, `toBuilder()` copies, value-based equality, `ICommandLineParser.buildAttributes()` reflects live JVM/OS properties
- `model/cmdline/ICommandLineParserTest.java` (**new, 9 tests**) — dedicated coverage for the interface's static `buildAttributes()` and default `buildHelpHeader`/`buildHelpFooter`/`buildOptions` helpers

### Updated test classes
- `gui/i18n/MessagesBundleTest.java` — added `COMMANDLINE_BUNDLE_MESSAGE` **and `ADDTOISODIALOG_BUNDLE_MESSAGE`** to bundle-load and field-resolution checks
- `model/cmdline/JISOCreatorCommandLineParserTest.java` — added `testHelpFormatterIsJISOCreatorCommandLineHelpFormatter` and adapted parser helper assertions to instance-based methods (now **20 tests**)
- `model/cmdline/JISOCreatorCommandLineHelpFormatterTest.java` — adapted options setup to use a parser instance (`buildOptions()` is now a default interface method)
- `model/osexplorer/OSExplorerTest.java` — simplified `testIsRootForSystemRoot` to assert all roots from `File.listRoots()` are identified as roots
- `model/providers/osxplorer/OSTreeContentProviderTest.java` — aligned regular-file expectation with `File#listFiles()` semantics (`getChildren(file)` returns `null` for non-directory files)

## Testing Framework Setup

### Dependencies
- **JUnit 5 (Jupiter)**: 5.10.2
- **Mockito**: 5.7.0
- **XMLUnit**: 2.11.0
- **Maven Surefire Plugin**: 3.2.5

### Project Structure
```
src/test/java/
└── cl/cavallinux/jisocreator/
    ├── action/
    │   ├── decl/
    │   │   └── JISOCreatorBaseActionTest.java
    │   ├── jobs/
    │   │   └── SaveISO9660ImageThreadTest.java
    │   ├── osexplorer/
    │   │   └── AddFileActionRecursiveTest.java
    │   └── main/
    │       ├── MainActionTest.java
    │       └── SaveAsIsoActionTest.java
    ├── gui/
    │   └── i18n/
    │       ├── AddToISODialogMessagesTest.java
    │       ├── CommandLineMessagesTest.java
    │       └── MessagesBundleTest.java
    ├── instances/
    │   ├── CommandLineOptionsManagerTest.java
    │   ├── CommandLineParserManagerTest.java
    │   ├── IOManagerTest.java
    │   ├── JISOCreatorISOLevelOptionsTest.java
    │   ├── JISOCreatorLanguageOptionsTest.java
    │   ├── MainActionsManagerTest.java
    │   └── OSAndIsoExplorerManagerTest.java
    ├── model/
    │   ├── cmdline/
    │   │   ├── ICommandLineParserTest.java
    │   │   ├── JISOCreatorAttributesTest.java
    │   │   ├── JISOCreatorCommandLineHelpFormatterTest.java
    │   │   └── JISOCreatorCommandLineParserTest.java
    │   ├── comparators/
    │   │   ├── ITreeNodeDirectoriesFirstComparatorTest.java
    │   │   └── OSDirectoriesComparatorTest.java
    │   ├── filters/
    │   │   ├── HideHiddenFilesFilterTest.java
    │   │   ├── ShowOnlyDirectoriesFilterTest.java
    │   │   └── isoexplorer/
    │   │       └── ShowOnlyIsoDirectoriesFilterTest.java
    │   ├── isoexplorer/impl/
    │   │   ├── IsoFileSystemTest.java
    │   │   ├── IsoTreeNodeTest.java
    │   │   └── TreeNodeTest.java
    │   ├── osexplorer/
    │   │   └── OSExplorerTest.java
    │   ├── parser/
    │   │   ├── XMLIsoFilesystemParserCompatibilityTest.java
    │   │   ├── decl/
    │   │   │   └── IsoFilesystemParserTest.java
    │   │   └── xml/
    │   │       ├── XMLIsoFilesystemContractMapperTest.java
    │   │       ├── XMLIsoFilesystemContractTest.java
    │   │       └── XMLIsoFilesystemParserTest.java
    │   └── providers/
    │       ├── decl/
    │       │   └── TableProviderAdapterTest.java
    │       ├── isoexplorer/
    │       │   ├── IsoTableProviderTest.java
    │       │   └── IsoTreeContentProviderTest.java
    │       └── osxplorer/
    │           ├── OSTreeContentProviderTest.java
    │           ├── OSTreeLabelProviderTest.java
    │           └── OsTableProviderTest.java
    └── util/
        └── IOUtilsPathTest.java
```

## Running Tests

### Run All Tests (Linux — default profile)
```bash
mvn test
```

### Run All Tests on Windows
```bash
mvn test -Pwindows
```

### Run a Specific Test Class
```bash
mvn test -Dtest=OSExplorerTest
```

### Run Clean + Tests
```bash
mvn clean test
```

### Test Reports
Surefire writes reports to:
- `target/surefire-reports/`

## Current Test Statistics (v0.2.1)
- **Total Tests**: 174
- **Test Classes**: 40
- **All Tests Passing**: ✓

### Test Statistics Summary
```
AddFileActionRecursiveTest.java:               10 tests  ← +3
MainActionTest.java:                            2 tests
SaveAsIsoActionTest.java:                       8 tests  ← new
JISOCreatorBaseActionTest.java:                 4 tests
SaveISO9660ImageThreadTest.java:                4 tests
AddToISODialogMessagesTest.java:                3 tests  ← new
CommandLineMessagesTest.java:                   5 tests
MessagesBundleTest.java:                        2 tests
CommandLineOptionsManagerTest.java:             7 tests
CommandLineParserManagerTest.java:              1 test
IOManagerTest.java:                             2 tests
JISOCreatorISOLevelOptionsTest.java:            2 tests
JISOCreatorLanguageOptionsTest.java:            2 tests
MainActionsManagerTest.java:                    6 tests
OSAndIsoExplorerManagerTest.java:               1 test
ICommandLineParserTest.java:                    9 tests  ← new
JISOCreatorAttributesTest.java:                 4 tests  ← new
JISOCreatorCommandLineHelpFormatterTest.java:   6 tests
JISOCreatorCommandLineParserTest.java:         20 tests
ITreeNodeDirectoriesFirstComparatorTest.java:   2 tests
OSDirectoriesComparatorTest.java:               5 tests
HideHiddenFilesFilterTest.java:                 2 tests
ShowOnlyDirectoriesFilterTest.java:             4 tests
ShowOnlyIsoDirectoriesFilterTest.java:          2 tests
IsoFileSystemTest.java:                         4 tests
IsoTreeNodeTest.java:                           8 tests
TreeNodeTest.java:                              3 tests
OSExplorerTest.java:                           13 tests
XMLIsoFilesystemParserCompatibilityTest.java:   2 tests
IsoFilesystemParserTest.java:                   3 tests
XMLIsoFilesystemContractMapperTest.java:        3 tests
XMLIsoFilesystemContractTest.java:              2 tests
XMLIsoFilesystemParserTest.java:                4 tests
TableProviderAdapterTest.java:                  2 tests
IsoTableProviderTest.java:                      3 tests
IsoTreeContentProviderTest.java:                2 tests
OSTreeContentProviderTest.java:                 3 tests
OSTreeLabelProviderTest.java:                   1 test
OsTableProviderTest.java:                       3 tests
IOUtilsPathTest.java:                           5 tests
----------------------------------------------------------
Total:                                        174 tests
```

## Notes on SWT-Dependent Testing
Some production classes depend on SWT/JFace runtime state (`Display`, images, widgets). Current suite prioritizes behavior that can be verified headless. UI-heavy integration tests are still pending.

## Notes on Cross-Platform Testing

The test suite runs on both Linux and Windows. Some platform-specific considerations:

- **Hidden files (`HideHiddenFilesFilter`)**: On Windows, `Files.isHidden()` only detects files with the DOS hidden attribute. The filter also checks for Unix-style hidden files (names starting with `.`) so that `HideHiddenFilesFilterTest` passes on both platforms.
- **Path separators (`XMLIsoFilesystemContractMapper`)**: `XMLIsoFilesystemContractMapper` normalizes file paths to forward slashes (`/`) before writing to XML. Tests compare paths using the same normalization to guarantee equality on both Linux and Windows.
- **XML fixtures**: Test fixture files under `src/test/resources/xml/` use Unix (LF) line endings to ensure consistent XMLUnit diff results across platforms.
- **File system roots (`OSExplorer`)**: `OSExplorer` now always initializes with `File.listRoots()` regardless of platform. `OSExplorerTest.testIsRootForSystemRoot` verifies all returned roots are recognized as roots via `isRoot()`.
- **Symbolic links / directory detection (`ShowOnlyDirectoriesFilter`)**: Uses `Files.isDirectory(path)` to classify entries as directories for the OS tree viewer filter; `ShowOnlyDirectoriesFilterTest` covers both root and non-root, directory and non-directory paths.
- **OS tree provider children contract (`OSTreeContentProvider`)**: For directory inputs, `getChildren(File)` returns directory entries. For regular files, it returns `null` (native `File#listFiles()` behavior, passed through as-is). `OSTreeContentProviderTest.shouldHandleRegularFileInputWithNoChildren` asserts this contract so behavior remains explicit and stable.
- **Headless CLI ISO saving (`SaveAsIsoAction` / `MainActionsManager`)**: `SaveAsIsoAction` can now be built with a no-argument builder (no GUI message/tooltip/image descriptor required) and is registered as `MainActionsManager.SAVEASISOACTION`, so `SaveAsIsoActionTest` and `MainActionsManagerTest` can exercise the CLI save path without needing a `Display` or `ActionsManager`/GUI singletons.

## Future Testing Enhancements
1. Add integration tests for GUI actions and dialogs with SWT harness (e.g. `ADDFileToIsoLayoutDialog`, `AboutDialog`, `MainWindow`, `IsoExplorerSashForm`).
2. Add deeper negative/error-path tests for image loading fallbacks in SWT-bound components (`ImageUtils`, `ImageRegister`).
3. Add broader action coverage (`ActionsManager` and remaining `action/*` classes not yet covered, e.g. full `AddFileAction.run()` GUI dialog flow).
4. Add coverage reporting (JaCoCo) in CI.

## Last Validation Run

Latest local validation executed with:

```bash
mvn -o test
```

Result: **174 tests passing in 40 classes** (from `target/surefire-reports`).