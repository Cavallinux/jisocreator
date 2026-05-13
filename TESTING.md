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
│           ├── model/
│           │   └── osexplorer/
│           │       └── OSExplorerTest.java
│           └── util/
│               └── IOUtilsPathTest.java
```

## Test Classes

### 1. OSExplorerTest (13 tests)
**Location**: `src/test/java/cl/cavallinux/jisocreator/model/osexplorer/OSExplorerTest.java`

Tests the OSExplorer singleton class which manages file system operations:

- **testGetName**: Verifies file name extraction
- **testGetAbsolutePath**: Validates absolute path retrieval
- **testLength**: Tests file size conversion to string
- **testLastModified**: Checks date formatting of last modification time
- **testGetFileTypeForDirectory**: Confirms directory type detection
- **testGetFileTypeForFileWithoutExtension**: Tests file type for extensionless files
- **testIsRootForSystemRoot**: Validates system root detection
- **testIsNotRoot**: Ensures non-root files are properly identified
- **testGetExtensionForDirectory**: Tests extension retrieval for directories
- **testGetExtensionForFile**: Validates file extension with dot notation
- **testGetExtensionForFileWithoutExtension**: Tests empty extension for files without extension
- **testSetAndGetRoots**: Verifies root directory setter and getter
- **testGetInstance**: Confirms singleton pattern implementation

### 2. IOUtilsPathTest (5 tests)
**Location**: `src/test/java/cl/cavallinux/jisocreator/util/IOUtilsPathTest.java`

Tests file path operations and configurations:

- **testValidFilePath**: Validates handling of file paths
- **testDirectoryCreation**: Tests directory creation operations
- **testFileOperationsInConfigDir**: Verifies file operations in configuration directories
- **testXMLFileExistence**: Validates XML file handling
- **testFilePathConcatenation**: Tests string path concatenation

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

## Current Test Statistics
- **Total Tests**: 18
- **Test Classes**: 2
- **All Tests Passing**: ✓

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

## Future Testing Enhancements

1. Add integration tests for ISO file operations
2. Add API tests for file system operations
3. Add tests for action classes
4. Add tests for provider implementations
5. Consider adding code coverage reporting with JaCoCo
6. Add performance benchmarks for large file operations
