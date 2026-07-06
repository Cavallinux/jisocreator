# JisoCreator Unit Testing Guide

## Overview
This project uses JUnit 5 and Mockito for unit tests. Tests are located under `src/test/java` and run with Maven Surefire.

## Testing Framework Setup

### Dependencies
- **JUnit 5 (Jupiter)**: 5.10.2
- **Mockito**: 5.7.0
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
    │   └── main/
    │       └── MainActionTest.java
    ├── gui/
    │   └── i18n/
    │       └── MessagesBundleTest.java
    ├── instances/
    │   ├── CommandLineOptionsManagerTest.java
    │   ├── CommandLineParserManagerTest.java
    │   ├── IOManagerTest.java
    │   ├── JISOCreatorISOLevelOptionsTest.java
    │   ├── JISOCreatorLanguageOptionsTest.java
    │   └── OSAndIsoExplorerManagerTest.java
    ├── model/
    │   ├── cmdline/
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
    │       │   ├── TableProviderAdapterTest.java
    │       │   ├── TreeContentAdapterTest.java
    │       │   └── TreeLabelAdapterTest.java
    │       └── impl/
    │           ├── isoexplorer/
    │           │   ├── IsoTableProviderTest.java
    │           │   └── IsoTreeContentProviderTest.java
    │           └── osexplorer/
    │               ├── OSTreeContentProviderTest.java
    │               ├── OSTreeLabelProviderTest.java
    │               └── OsTableProviderTest.java
    └── util/
        └── IOUtilsPathTest.java
```

## Running Tests

### Run All Tests
```bash
mvn test
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

## Current Test Statistics (v0.2.0-SNAPSHOT)
- **Total Tests**: 113
- **Test Classes**: 33
- **All Tests Passing**: ✓

### Test Statistics Summary
```
MainActionTest.java:                         2 tests
JISOCreatorBaseActionTest.java:              4 tests
SaveISO9660ImageThreadTest.java:             4 tests
MessagesBundleTest.java:                     2 tests
CommandLineOptionsManagerTest.java:          7 tests
CommandLineParserManagerTest.java:           1 test
IOManagerTest.java:                          2 tests
JISOCreatorISOLevelOptionsTest.java:         2 tests
JISOCreatorLanguageOptionsTest.java:         2 tests
OSAndIsoExplorerManagerTest.java:            1 test
JISOCreatorCommandLineParserTest.java:      19 tests
ITreeNodeDirectoriesFirstComparatorTest.java: 2 tests
OSDirectoriesComparatorTest.java:            1 test
HideHiddenFilesFilterTest.java:              2 tests
ShowOnlyDirectoriesFilterTest.java:          3 tests
ShowOnlyIsoDirectoriesFilterTest.java:       2 tests
IsoFileSystemTest.java:                      4 tests
TreeNodeTest.java:                           3 tests
OSExplorerTest.java:                        13 tests
XMLIsoFilesystemParserCompatibilityTest.java: 2 tests
IsoFilesystemParserTest.java:                3 tests
XMLIsoFilesystemContractMapperTest.java:     3 tests
XMLIsoFilesystemContractTest.java:           2 tests
XMLIsoFilesystemParserTest.java:             4 tests
TableProviderAdapterTest.java:               2 tests
TreeContentAdapterTest.java:                 2 tests
TreeLabelAdapterTest.java:                   2 tests
IsoTableProviderTest.java:                   3 tests
IsoTreeContentProviderTest.java:             2 tests
OSTreeContentProviderTest.java:              3 tests
OSTreeLabelProviderTest.java:                1 test
OsTableProviderTest.java:                    3 tests
IOUtilsPathTest.java:                        5 tests
--------------------------------------------------------
Total:                                     113 tests
```

## Notes on SWT-Dependent Testing
Some production classes depend on SWT/JFace runtime state (`Display`, images, widgets). Current suite prioritizes behavior that can be verified headless. UI-heavy integration tests are still pending.

## Future Testing Enhancements
1. Add integration tests for GUI actions and dialogs with SWT harness.
2. Add deeper negative/error-path tests for image loading fallbacks in SWT-bound components.
3. Add broader action coverage (`ActionsManager` and remaining `action/*` classes).
4. Add coverage reporting (JaCoCo) in CI.
