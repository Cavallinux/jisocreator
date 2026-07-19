# JisoCreator Unit Testing Guide

## Overview
This project uses JUnit 5 and Mockito for unit tests. Tests are located under `src/test/java` and run with Maven Surefire.

## Tests Added in `feature/v0.2.1`
- `action/osexplorer/AddFileActionRecursiveTest.java` (7 tests) — recursive add, cancellation, empty directories, progress monitor
- `model/isoexplorer/impl/IsoTreeNodeTest.java` (8 tests) — `addNode` vs `addLeafNode`, duplicate prevention, recursion semantics
- `model/parser/XMLIsoFilesystemParserCompatibilityTest.java` (updated coverage)
- `gui/i18n/CommandLineMessagesTest.java` (5 tests) — validates all `CommandLineMessages` fields resolve from the i18n bundle
- `instances/MainActionsManagerTest.java` (6 tests) — `MAINACTION` singleton, `MainAction` type, `layoutFilePath` and parser initialization
- `model/cmdline/JISOCreatorCommandLineHelpFormatterTest.java` (6 tests) — `getTableDefinition` caption/headers i18n override, column styles, `printHelp` smoke

### Updated test classes
- `gui/i18n/MessagesBundleTest.java` — added `COMMANDLINE_BUNDLE_MESSAGE` to bundle-load and field-resolution checks
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
    │       └── MainActionTest.java
    ├── gui/
    │   └── i18n/
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
- **Total Tests**: 147
- **Test Classes**: 36
- **All Tests Passing**: ✓

### Test Statistics Summary
```
AddFileActionRecursiveTest.java:                7 tests
MainActionTest.java:                           2 tests
JISOCreatorBaseActionTest.java:                4 tests
SaveISO9660ImageThreadTest.java:               4 tests
CommandLineMessagesTest.java:                  5 tests  ← new
MessagesBundleTest.java:                       2 tests
CommandLineOptionsManagerTest.java:            7 tests
CommandLineParserManagerTest.java:             1 test
IOManagerTest.java:                            2 tests
JISOCreatorISOLevelOptionsTest.java:           2 tests
JISOCreatorLanguageOptionsTest.java:           2 tests
MainActionsManagerTest.java:                   6 tests  ← new
OSAndIsoExplorerManagerTest.java:              1 test
JISOCreatorCommandLineHelpFormatterTest.java:  6 tests  ← new
JISOCreatorCommandLineParserTest.java:        20 tests  ← +1
ITreeNodeDirectoriesFirstComparatorTest.java:  2 tests
OSDirectoriesComparatorTest.java:              5 tests
HideHiddenFilesFilterTest.java:                2 tests
ShowOnlyDirectoriesFilterTest.java:            3 tests
ShowOnlyIsoDirectoriesFilterTest.java:         2 tests
IsoFileSystemTest.java:                        4 tests
IsoTreeNodeTest.java:                          8 tests
TreeNodeTest.java:                             3 tests
OSExplorerTest.java:                          13 tests
XMLIsoFilesystemParserCompatibilityTest.java:  2 tests
IsoFilesystemParserTest.java:                  3 tests
XMLIsoFilesystemContractMapperTest.java:       3 tests
XMLIsoFilesystemContractTest.java:             2 tests
XMLIsoFilesystemParserTest.java:               4 tests
TableProviderAdapterTest.java:                 2 tests
IsoTableProviderTest.java:                     3 tests
IsoTreeContentProviderTest.java:               2 tests
OSTreeContentProviderTest.java:                3 tests
OSTreeLabelProviderTest.java:                  1 test
OsTableProviderTest.java:                      3 tests
IOUtilsPathTest.java:                          5 tests
----------------------------------------------------------
Total:                                       147 tests
```

## Notes on SWT-Dependent Testing
Some production classes depend on SWT/JFace runtime state (`Display`, images, widgets). Current suite prioritizes behavior that can be verified headless. UI-heavy integration tests are still pending.

## Notes on Cross-Platform Testing

The test suite runs on both Linux and Windows. Some platform-specific considerations:

- **Hidden files (`HideHiddenFilesFilter`)**: On Windows, `Files.isHidden()` only detects files with the DOS hidden attribute. The filter also checks for Unix-style hidden files (names starting with `.`) so that `HideHiddenFilesFilterTest` passes on both platforms.
- **Path separators (`XMLIsoFilesystemContractMapper`)**: `XMLIsoFilesystemContractMapper` normalizes file paths to forward slashes (`/`) before writing to XML. Tests compare paths using the same normalization to guarantee equality on both Linux and Windows.
- **XML fixtures**: Test fixture files under `src/test/resources/xml/` use Unix (LF) line endings to ensure consistent XMLUnit diff results across platforms.
- **File system roots (`OSExplorer`)**: `OSExplorer` now always initializes with `File.listRoots()` regardless of platform. `OSExplorerTest.testIsRootForSystemRoot` verifies all returned roots are recognized as roots via `isRoot()`.
- **Symbolic links (`ShowOnlyDirectoriesFilter`)**: Uses `Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)` to classify entries, ensuring symlinks are not treated as real directories on Linux.
- **OS tree provider children contract (`OSTreeContentProvider`)**: For directory inputs, `getChildren(File)` returns directory entries. For regular files, it returns `null` (native `File#listFiles()` behavior). `OSTreeContentProviderTest.shouldHandleRegularFileInputWithNoChildren` asserts this contract so behavior remains explicit and stable.

## Future Testing Enhancements
1. Add integration tests for GUI actions and dialogs with SWT harness.
2. Add deeper negative/error-path tests for image loading fallbacks in SWT-bound components.
3. Add broader action coverage (`ActionsManager` and remaining `action/*` classes not yet covered).
4. Add coverage reporting (JaCoCo) in CI.

## Last Validation Run

Latest local validation executed with:

```bash
mvn -q test -Pwindows
```

Result: **147 tests passing in 36 classes** (from `target/surefire-reports`).