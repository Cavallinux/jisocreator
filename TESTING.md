# JisoCreator Unit Testing Guide

## Overview
This project has been configured with a comprehensive unit testing framework using JUnit 5 and Mockito. The tests are located in the `src/test/java` directory and follow Maven's standard testing conventions.

## Testing Framework Setup

### Dependencies Added
- **JUnit 5 (Jupiter)**: Version 5.10.2 - Modern Java testing framework with powerful features
- **Mockito**: Version 5.7.0 - Mocking library for creating test doubles
- **Maven Surefire Plugin**: Version 3.2.5 - Maven test runner

### Project Structure
```
src/test/java/
├── cl/
│   └── cavallinux/
│       └── jisocreator/
│           ├── gui/
│           │   └── i18n/
│           │       └── MessagesBundleTest.java
│           ├── instances/
│           │   ├── CommandLineOptionsManagerTest.java
│           │   ├── CommandLineParserManagerTest.java
│           │   ├── JISOCreatorISOLevelOptionsTest.java
│           │   └── JISOCreatorLanguageOptionsTest.java
│           ├── model/
│           │   ├── cmdline/
│           │   │   └── JISOCreatorCommandLineParserTest.java
│           │   ├── isoexplorer/
│           │   │   └── impl/
│           │   │       ├── IsoFileSystemTest.java
│           │   │       └── TreeNodeTest.java
│           │   ├── parser/
│           │   │   ├── xml/
│           │   │   │   ├── XMLIsoFilesystemContractMapperTest.java
│           │   │   │   └── XMLIsoFilesystemParserTest.java
│           │   │   └── XMLIsoFilesystemParserCompatibilityTest.java
│           │   ├── providers/
│           │   │   └── impl/
│           │   │       ├── isoexplorer/
│           │   │       │   └── IsoTreeContentProviderTest.java
│           │   │       └── osexplorer/
│           │   │           └── OSTreeContentProviderTest.java
│           │   └── osexplorer/
│           │       └── OSExplorerTest.java
│           └── util/
│               └── IOUtilsPathTest.java
```

## Test Classes

### Current Unit Test Inventory

#### Core model and parser coverage
- `XMLIsoFilesystemParserCompatibilityTest` (2 tests)
- `XMLIsoFilesystemParserTest` (4 tests)
- `XMLIsoFilesystemContractMapperTest` (3 tests)
- `IsoFileSystemTest` (4 tests)
- `TreeNodeTest` (3 tests)

#### Explorer and provider coverage
- `OSExplorerTest` (13 tests)
- `IsoTreeContentProviderTest` (2 tests)
- `OSTreeContentProviderTest` (3 tests)

#### CLI and manager coverage
- `CommandLineOptionsManagerTest` (7 tests)
- `JISOCreatorCommandLineParserTest` (19 tests)
- `CommandLineParserManagerTest` (1 test)
- `JISOCreatorISOLevelOptionsTest` (2 tests)
- `JISOCreatorLanguageOptionsTest` (2 tests)

#### i18n coverage
- `MessagesBundleTest` (2 tests)

#### Utilities
- `IOUtilsPathTest` (5 tests)

Test fixture for XML compatibility:
- `src/test/resources/xml/c267b1a84ea9429088ce5530122e5c8a.xml`

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=OSExplorerTest
```

### Run with Coverage
```bash
mvn clean test
```

### Test Results
The Maven Surefire plugin automatically generates test reports in:
- `target/surefire-reports/` - Raw test results
- Console output during build

## Test Configuration

### Temporary Directory Support
Tests utilize JUnit 5's `@TempDir` annotation which:
- Automatically creates temporary directories for each test
- Cleans up resources after test execution
- Provides isolated test environments

### Test Isolation
- Each test is independent and can run in any order
- Tests use temporary directories to avoid file system pollution
- No external configuration files required for testing

## Maven Configuration

### Surefire Plugin Configuration
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
    </configuration>
</plugin>
```

Test files are discovered using the patterns:
- `**/*Test.java`
- `**/*Tests.java`

## Adding New Tests

### Create a New Test Class
1. Create the test file in `src/test/java` following the package structure
2. Name the class with `Test` suffix (e.g., `MyClassTest`)
3. Use JUnit 5 annotations:
   - `@DisplayName` - Human-readable test descriptions
   - `@Test` - Mark test methods
   - `@BeforeEach` - Setup before each test
   - `@io.TempDir` - For temporary directory support

### Example Test Structure
```java
@DisplayName("MyClass Tests")
class MyClassTest {
    
    private MyClass instance;
    
    @BeforeEach
    void setUp() {
        instance = new MyClass();
    }
    
    @Test
    @DisplayName("Should perform operation correctly")
    void testOperation() {
        assertEquals("expected", instance.operation());
    }
}
```

## Current Test Statistics (as of v0.2.0-SNAPSHOT)
- **Total Tests**: 72
- **Test Classes**: 15
- **All Tests Passing**: ✓

The project now includes dedicated coverage for parser/mapper logic, ISO filesystem metadata behavior, tree content providers, command line managers/options, and i18n bundle resolution.

### Test Statistics Summary
```
MessagesBundleTest.java:                    2 tests
OSExplorerTest.java:                       13 tests
OSTreeContentProviderTest.java:             3 tests
IsoTreeContentProviderTest.java:            2 tests
JISOCreatorCommandLineParserTest.java:     19 tests
TreeNodeTest.java:                          3 tests
IsoFileSystemTest.java:                     4 tests
XMLIsoFilesystemContractMapperTest.java:    3 tests
XMLIsoFilesystemParserTest.java:            4 tests
XMLIsoFilesystemParserCompatibilityTest:    2 tests
JISOCreatorLanguageOptionsTest.java:        2 tests
CommandLineParserManagerTest.java:          1 test
CommandLineOptionsManagerTest.java:         7 tests
JISOCreatorISOLevelOptionsTest.java:        2 tests
IOUtilsPathTest.java:                       5 tests
─────────────────────────────────────────────────
Total:                                     72 tests
```

## Best Practices

1. **Test Naming**: Use descriptive names that explain the test scenario
2. **Test Isolation**: Each test should be independent and not rely on others
3. **Use Assertions**: Prefer specific assertions (`assertEquals`, `assertTrue`) over boolean checks
4. **Mock External Dependencies**: Use Mockito to mock SWT and GUI components
5. **Temporary Resources**: Use `@TempDir` for file operations instead of hardcoded paths

## Notes on Testing SWT Components

Some classes depend on SWT (Standard Widget Toolkit) which requires an active Display. For now:
- Tests focus on non-GUI logic
- Model and utility classes are prioritized
- Future integration with SWT testing frameworks can be added if needed

## Version 0.1.2 Testing Updates

### Architectural Changes in Tests
As part of the v0.1.2 refactoring, the following testing improvements were made:

1. **Centralized Singleton Manager Access**
   - Tests now use the `OSAndIsoExplorerManager` enum to access singleton instances
   - This ensures tests follow the same architectural pattern as the application code
   - Better encapsulation and centralized management of singleton lifecycle

2. **Manager Pattern Integration**
   - All test setup now uses the centralized managers (`OSAndIsoExplorerManager`)
   - Provides a single point of access for test fixtures
   - Improves maintainability and consistency with application architecture

### Example of Updated Test Pattern
```java
@BeforeEach
void setUp() {
    // Previous approach (deprecated in v0.1.2):
    // osExplorer = OSExplorer.getInstance();
    
    // New approach (v0.1.2+):
    osExplorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
}
```

## Future Testing Enhancements

1. Add integration tests for ISO file operations
2. Add API tests for file system operations
3. Add tests for action classes (utilizing centralized `ActionsManager`)
4. Add remaining tests for SWT-dependent providers (`IsoTableProvider`, label providers, filters/comparators)
5. Add tests for new manager components (`GUIManager`, `ImageRegister`)
6. Add deeper edge-case tests for XML parsing/serialization (`model/parser`) and ISO metadata handling (Volume/Publisher/Application ID)
7. Add negative/edge tests for `IsoFileSystem` with large directory trees and invalid node payloads
8. Consider adding code coverage reporting with JaCoCo
9. Add performance benchmarks for large file operations
