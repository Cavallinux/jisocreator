# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Automated release workflow** (`.github/workflows/release.yml`): on every push to `master` that touches `pom.xml`, reads the project version and — provided it is not a `-SNAPSHOT` and no `v<version>` tag exists yet — builds and packages the distributable zip for all three platform profiles (default `linux`, `-Pwindows`, `-Papplesilicon`, all built on `ubuntu-latest` since packaging only requires the platform-specific SWT dependency jar, not a native toolchain), creates the `v<version>` tag against the released commit, and publishes a GitHub Release with the three zips attached and release notes sourced from the matching `CHANGELOG.md` section. Also exposes a `workflow_dispatch` trigger for manual re-runs.

### Changed
- **OS file-system explorer performance**: eliminated several redundant filesystem/native-lookup calls identified when loading directories with many files/subfolders:
  - **`OSExplorer#findProgram(String)`** (new): caches the result of `Program.findProgram(extension)` — an expensive OS-level file-association/MIME lookup — keyed by extension, since the same extension always resolves to the same program during the application's lifetime. `getFileType(Path)` and `ImageUtils#loadImage(Path)` (previously each called `Program.findProgram` independently for the same file, once for the type column and once for the icon) now both go through this cache, halving the number of native lookups per file and eliminating repeats across files sharing an extension.
  - **`OSTreeContentProvider#hasChildren(Object)`**: no longer calls `File#listFiles()` (which reads and materializes the entire directory listing) just to know whether a tree node should be expandable. Now uses `Files.newDirectoryStream(Path)` and stops at the first entry found (`iterator().hasNext()`), avoiding a full directory read for large folders.
  - **`OSDirectoriesComparator`**: overrides `sort(Viewer, Object[])` to precompute each element's directory/file category once per sort call (cached by element identity), instead of recomputing `Files.isDirectory(Path)` on every pairwise comparison performed by the underlying `Arrays.sort` (previously O(n log n) redundant filesystem checks per listing).
- **Test updates for the above**: `OSExplorerTest` grew to **16 tests** (new `testFindProgramCachesResultsPerExtension`, `testGetFileTypeForFileWithExtensionUsesCachedProgram`, both guarded by `SwtPlatformAssumptions.assumeNativePlatformMatches()`); `OSTreeContentProviderTest` grew to **5 tests** (new `shouldReportNoChildrenForEmptyDirectory`, `shouldReportChildrenPresentWithSingleEntry`); `OSDirectoriesComparatorTest` grew to **7 tests** (new `shouldSortMixedListingWithDirectoriesFirst`, `shouldComputeCategoryCorrectlyWithoutSortCall`). Suite grew to **272 tests / 62 classes**, verified passing under both `mvn -o clean test` (272/272, 0 skipped) and `mvn -o clean test -Pwindows` (272/272, 7 skipped, 0 failures).
- **OS file-system explorer background loading**: moved the expensive part of directory loading off the SWT UI thread, complementing the quick-win optimizations above:
  - **`OSExplorer#attributesCache`** / **`OSExplorer#warmAttributesCache(Path)`** (new): pre-fetches `BasicFileAttributes` (directory flag, size, last-modified time) for every entry of a directory in a single `Files.readAttributes(...)` call per entry, consolidating what were previously up to 3 separate stat calls. **`OSExplorer#isDirectory(Path)`** (new) and the existing `getFileType`/`length`/`lastModified`/`getExtension` now read from this cache when available, falling back to a direct filesystem check otherwise.
  - **`ShowOnlyDirectoriesFilter`**, **`OSDirectoriesComparator#computeCategory(Object)`**, **`ImageUtils#loadImage(Path)`**: now call `OSExplorer#isDirectory(Path)` instead of `Files.isDirectory(Path)` directly, so all three benefit from the pre-warmed cache.
  - **`LoadOSDirectoryContentsThread`** (new, `action/jobs`): a genuinely `.start()`'d background `Thread` (unlike `ToggleHiddenFilesOSExplorerThread`'s `asyncExec`-only usage) that warms `OSExplorer`'s attribute cache off the UI thread, then marshals `TableViewer#setInput(Object)` back via `Display#asyncExec(Runnable)`. A static "latest requested directory" guard discards stale results if the user navigates elsewhere before a previous background load finishes.
  - **`OSExplorerSashFormSelectionChangedListener`**: both directory-selection call sites now build and start a `LoadOSDirectoryContentsThread` instead of calling `setInput(...)` synchronously; cheap widget updates (text field, action enablement) remain synchronous.
- **Test updates for the above**: `OSExplorerTest` grew to **19 tests** (new `testWarmAttributesCachePopulatesEntriesForDirectoryListing`, `testIsDirectoryUsesCachedAttributesAfterFileDeleted`, `testWarmAttributesCacheClearsPreviousEntries`); new `LoadOSDirectoryContentsThreadTest` (**3 tests**) covering the package-private `isStillLatestRequest()` pure logic. Suite grew to **278 tests / 63 classes**, verified passing under both `mvn -o clean test` (278/278, 0 skipped) and `mvn -o clean test -Pwindows` (278/278, 7 skipped, 0 failures — unchanged skip count, no new native/`Display`-dependent tests were introduced in this round).

## [0.2.2] - 2026-07-25

### Added
- **Test Gap Analysis (Groups 1–7)**: full architecture-driven audit of all 92 production classes vs. existing tests, identifying 48 classes without dedicated test coverage, grouped by architectural layer/priority (see `TESTING.md` → "Test Gap Analysis & Coverage Plan"). Implemented Groups 1–5 in full, and investigated/documented Groups 6–7 (partially implemented where safe). Suite grew from **222 → 262 tests** (**52 → 62 classes**) across this work.
  - **Group 1 (model logic without SWT)**: `IOUtilsTest` (6 tests), `ITreeNodeTest` (13 tests, covering all 13 `default` methods of `ITreeNode`).
  - **Group 2 (DnD & providers)**: `IsoTreeLabelProviderTest` (2 tests), `JISOCreatorViewerDropAdapterTest` (2 tests), `JISOCreatorDragSourceAdapterTest` (3 tests). Established the subclassing/reflection techniques (with `Objenesis`, now a parameterized test dependency `${objenesis.version}`) needed to construct real SWT event/viewer objects headlessly, since concrete SWT/JFace classes cannot be mocked by Mockito in this toolchain.
  - **Group 3 (main menu actions)**: `AboutActionTest`, `ExitApplicationActionTest`, `LoadCommandLineISOLayoutActionTest`, `NewIsoLayoutActionTest`, `OpenIsoLayoutActionTest`, `PreferencesActionTest`, `SaveAsXMLActionTest` (22 tests total) — headless builder-state/type checks only, since every `run()` is coupled to `GUIManager.INSTANCE`/`Display`.
  - **Group 4 (ISO explorer actions)**: `DeleteIsoEntryActionTest`, `GoToIsoEntryParentActionTest`, `OpenIsoEntryActionTest`, `ShowIsoInformationActionTest` (17 tests total) — same headless pattern; `OpenIsoEntryAction`'s Lombok `node` accessor/mutator additionally covered.
  - **Group 5 (OS explorer actions & jobs)**: `GoToParentActionTest`, `OpenActionTest`, `RefreshExplorerActionTest`, `ShowHiddenFilesActionTest`, `ToggleHiddenFilesOSExplorerThreadTest` (23 tests total) — same headless pattern; `ToggleHiddenFilesOSExplorerThread`'s private `removeFilters(StructuredViewer)` additionally covered via reflection against a minimal headless `StructuredViewer` subclass.
  - **Group 6 (instance managers/singletons)**: `JFaceResourcesManagerTest` (4 tests) — the one class in this group with no `ImageRegister`/`Display` dependency in its constructors. The other 6 classes (`ActionsManager`, `IsoExplorerActionsManager`, `OSExplorerActionsManager`, `PreferencesNodeManager`, `ImageRegister`, `GUIManager`) were investigated and found to force real `Display` construction the instant their class is loaded (root cause: `ImageRegister.INSTANCE` → `new ImageUtils()` → `Display.getDefault()`); documented as deferred to a future headless-SWT (Xvfb) harness (Group 8).
  - **Group 7 (`util/ImageUtils`)**: investigated and documented (no new tests) — every method, including its own constructor, requires a live `Display`; deferred to Group 8 for the same reason as the Group 6 exclusions.
  - **New shared test helper**: `testsupport/SwtPlatformAssumptions.assumeNativePlatformMatches()` — compares the pure, non-native `SWT.getPlatform()` constant against `System.getProperty("os.name")` and uses JUnit 5 `Assumptions.assumeTrue(...)` to cleanly **skip** (not crash) tests that exercise native SWT `Transfer` registration when the active Maven platform profile (`-Pwindows`, `-Papplesilicon`, ...) doesn't match the host OS. Established as a mandatory pattern this session after discovering that a platform mismatch otherwise causes SWT's native loader to call a non-catchable `System.exit(1)`, crashing the entire Surefire forked JVM.

### Changed
- **Start of `feature/v0.2.2` development (2026-07-19)**: advanced `pom.xml` project version from **`0.2.1`** to **`0.2.2-SNAPSHOT`**.
- **Documentation synchronization for Groups 1–7 test work**: updated `README.md` (Testing section: stats, test structure tree, platform-mismatch note) and `TESTING.md` (full per-group sections, updated file tree, statistics table, gap-analysis plan status, "Note on running tests under non-default platform profiles", "Last Validation Run") to reflect the current suite inventory (**262 tests / 62 classes**) and validation results under both the default and `-Pwindows` Maven profiles.
- **`pom.xml`**: parameterized the `objenesis` test dependency version as `${objenesis.version}` (previously hardcoded `3.3`), consistent with how every other dependency version is declared in this project.
- **`README.md` Quick Start**: split the single Windows-only PowerShell walkthrough into three OS-specific subsections (Linux, Windows PowerShell, macOS Apple Silicon), each with its matching Maven platform profile and packaged-distribution filename.
- **`JISOCreatorAttributes`**: added `toString()` (`"<appName> version <appVersion>"`) and `toString(String baseString)` (positional substitution of `appName`/`appVersion`/`jvmVersion`/`jvmVendor`/`osName` into an arbitrary format pattern).
- **`JISOCreatorCommandLineParser#printVersion()`**: now delegates to `attributes.toString(CommandLineMessages.commandLineVersionMessage)` instead of building the argument list inline; same output, simpler call site.
- **`MainAction#run()`**: GUI-mode startup log line now logs `ICommandLineParser.buildAttributes()` (via its new `toString()`) instead of a static string.
- **Test updates for the above**: `JISOCreatorAttributesTest` grew from 4 to **6 tests** (new `toString()`/`toString(baseString)` coverage); `JISOCreatorCommandLineParserTest` grew from 20 to **21 tests** (new `testPrintVersionContainsAttributeValues` asserting captured `System.out` content, not just "does not throw"); `MainActionTest` required no changes since its `TestableMainAction` overrides `run()` entirely. Suite grew to **265 tests / 62 classes**, verified passing under both `mvn -o clean test` (265/265, 0 skipped) and `mvn -o clean test -Pwindows` (265/265, 5 skipped, 0 failures).
- **OS explorer initial selection**: `OSExplorerSashForm#setInitialSelection()` (new method) selects the Unix filesystem root in the OS directories tree on non-Windows platforms (checked via `Strings.CI.containsAny(SWT.getPlatform(), "win")`), and `MainWindow#createContents` now invokes it right after building the OS explorer panel. Backed by the new `OSExplorer#getUnixOSRoot()` helper (`roots[0]`).
- **Test updates for the OS explorer initial selection**: `OSExplorerTest` grew from 13 to **14 tests**, adding `testGetUnixOSRoot` for the new pure `getUnixOSRoot()` method. `OSExplorerSashForm#setInitialSelection()` and the `MainWindow` call site were **not** unit-tested — both require real, `Display`-bound `Tree`/`Composite` controls, consistent with this project's existing convention of deferring full GUI-composite coverage to a future headless-SWT (Xvfb) harness (Group 8). Suite grew to **266 tests / 62 classes**, verified passing under both `mvn -o clean test` (266/266, 0 skipped) and `mvn -o clean test -Pwindows` (266/266, 5 skipped, 0 failures).
- **`OSExplorerSashForm#setInitialSelection()`**: follow-up change also calls `osDirectoriesTree.expandToLevel(osExplorer.getUnixOSRoot(), 1)` after selecting the Unix root, so the tree node is expanded one level. Same untested status as the rest of the method (real `Display`-bound `Tree`).
- **`GoToParentAction#run()`**: fixed the post-navigation `setEnabled(...)` logic — now disables the action once the newly selected parent directory is itself a filesystem root (`!osExplorer.isRoot(parent.toPath())`), instead of the previous (backwards) check against the pre-navigation file; also removed a redundant cast on `osExplorer.getTreeSelection()`. No test changes required: `run()` remains untestable headlessly (coupled to `GUIManager.INSTANCE.getMainWindow()`), and `GoToParentActionTest` already only covers builder state — verified suite still passes 266/266 (both profiles).
- **End of `feature/v0.2.2` development (2026-07-25)**: `pom.xml` project version finalized from `0.2.2-SNAPSHOT` to **`0.2.2`**, closing out this branch's test-coverage initiative (Groups 1–7 of the gap-analysis plan) and the OS-explorer initial-selection feature.

## [0.2.1] - 2026-07-19

### Added
- **Recursive add with cancellation coverage**: Added `AddFileActionRecursiveTest` (originally 7, now **10 tests**) to validate recursive add behavior, immediate and mid-recursion cancellation, empty directories, progress monitor integration, the full `run(IProgressMonitor)` happy/cancel paths, and `AddFileAction.builder()` instantiation.
- **`IsoTreeNode` behavior coverage**: Added `IsoTreeNodeTest` (8 tests) to validate `addNode` vs `addLeafNode`, duplicate prevention, and directory recursion semantics.
- **Cancellable add-to-ISO workflow**: `AddFileAction` now processes dropped/selected files recursively (`addFileRecursively`) with cooperative cancellation via `IProgressMonitor#isCanceled`, per-node `ITreeNode#addLeafNode` insertion (no implicit recursion), incremental `monitor.worked(1)` reporting, and a `refreshGUI()` step run in `finally` so partial additions are reflected even when the user cancels mid-operation. The status line "cancel" button is toggled through `MainWindow#setStatusLineActiveCancelButton`.
- **ISO tree API refinement**: Added `ITreeNode#addLeafNode(ITreeNode)` (default no-op) and implemented it in `IsoTreeNode` to support direct child insertion without implicit recursion; kept `addNode` for recursive directory expansion.
- **`MainActionsManager` enum**: Extracted `MAINACTION` instantiation from `ActionsManager` into a dedicated `MainActionsManager` enum, allowing `MainAction` to be initialized without triggering GUI-bound singleton dependencies (e.g. `ImageRegister`). Later extended with a second constant, **`SAVEASISOACTION`**, wrapping a headless-safe `SaveAsIsoAction.builder().build()` instance so the CLI save-to-ISO workflow (`-i/-o`) no longer depends on `ActionsManager`/GUI singletons. This makes both `MainAction.main()` and CLI ISO saving safe to call in non-graphical / headless environments.
- **`SaveAsIsoAction` headless builder**: Added a no-argument `@Builder protected SaveAsIsoAction()` constructor (in addition to the existing GUI-oriented `(message, tooltip, imageDescriptor)` one) so it can be instantiated by `MainActionsManager` without GUI-only parameters.
- **`SaveAsIsoActionTest`** (new, 8 tests): validates default builder state (`inputXMLLayoutFile`/`outputISOFile` empty, `commandLineMode` false), setter round-trips, and instance independence between builder calls.
- **`CommandLineMessages` i18n class**: New NLS bundle class (`gui/i18n/CommandLineMessages`) backed by `i18n/commandline/messages_en.properties` and `messages_es.properties`, externalizing all CLI-facing strings: version format, app description, example usage, option descriptions, help table title, column headers, and the syntax-line prefix. Added `COMMANDLINE_BUNDLE_MESSAGE` constant to `INLSBundleMessages`.
- **`JISOCreatorCommandLineHelpFormatter`**: New `HelpFormatter` subclass that overrides `getTableDefinition(Iterable<Option>)` to inject the i18n table caption and column headers from `CommandLineMessages`, and sets the syntax prefix via `setSyntaxPrefix`. Used by `JISOCreatorCommandLineParser` as its default formatter. Simplified to a no-arg protected constructor (`new JISOCreatorCommandLineHelpFormatter()`) internally delegating to `HelpFormatter.builder().setShowSince(false)`.
- **`JISOCreatorAttributes` record**: New immutable record (`appName`, `appVersion`, `jvmVersion`, `jvmVendor`, `osName`) encapsulating CLI version/help metadata, with a Lombok `@Builder(toBuilder = true)`. Built via the new static helper `ICommandLineParser.buildAttributes()`.
- **`JISOCreatorAttributesTest`** (new, 4 tests): validates builder field population, `toBuilder()` derived-copy semantics, value-based `equals`/`hashCode`, and that `ICommandLineParser.buildAttributes()` reflects live JVM/OS system properties.
- **`ICommandLineParserTest`** (new, 9 tests): dedicated coverage for the interface's `static buildAttributes()` and the `default` helpers `buildHelpHeader`, `buildHelpFooter`, and `buildOptions` (moved here from `JISOCreatorCommandLineParser` in this same release).
- **Add-to-ISO dialog i18n**: New `AddToISODialogMessages` NLS class (`gui/i18n/AddToISODialogMessages`) backed by `i18n/addtoisodialog/messages_en.properties` / `messages_es.properties`, externalizing `ADDFileToIsoLayoutDialog`'s window title, static info label and OK/Cancel button text. Added `ADDTOISODIALOG_BUNDLE_MESSAGE` constant to `INLSBundleMessages`. The dialog's OK/Cancel buttons are now relabeled in `createButtonsForButtonBar` instead of relying on JFace defaults.
- **`AddToISODialogMessagesTest`** (new, 3 tests): validates static field resolution, non-blank window title/labels, and that the `addtoisodialog` bundle loads correctly for English and Spanish locales.
- **Apple Silicon (macOS) packaging**: Added the `applesilicon` / `applesilicon-cmdlinemode` Maven profiles (`swt.platform=cocoa.macosx.aarch64`), a dedicated assembly descriptor (`src/assembly/assembly.cocoa.macosx.aarch64.xml`), and launch script `res/applesilicon/jisocreator.sh`, extending distribution support from Linux/Windows to macOS on Apple Silicon.
- **CLI i18n unit tests** (carried over from earlier CLI i18n work):
  - `CommandLineMessagesTest` (5 tests) — validates all static fields in `CommandLineMessages` are non-null, non-blank, and resolved (not in `!key!` error format), with dedicated checks for version, example usage, table column, and syntax-prefix messages.
  - `MainActionsManagerTest` (6 tests) — verifies both `MainActionsManager` constants, non-null action instances, enum singleton contract, empty initial `layoutFilePath`, and parser referential equality with `CommandLineParserManager`.
  - `JISOCreatorCommandLineHelpFormatterTest` (6 tests) — verifies `getTableDefinition` overrides the caption and column headers with i18n messages, preserves parent column styles and rows, and that `printHelp` completes without throwing.
- **Updated coverage for existing test classes**:
  - `MessagesBundleTest` — extended to include `COMMANDLINE_BUNDLE_MESSAGE` **and `ADDTOISODIALOG_BUNDLE_MESSAGE`** in bundle-load and field-resolution checks.
  - `JISOCreatorCommandLineParserTest` — added assertion that the default `helpFormatter` field is an instance of `JISOCreatorCommandLineHelpFormatter`, and adapted `buildOptions`/`buildHelpHeader`/`buildHelpFooter` calls to the instance-based `ICommandLineParser` default methods (total: **20 tests**).
  - `OSExplorerTest` — `testIsRootForSystemRoot` simplified: now asserts that every entry returned by `getRoots()` is identified as a root, since `OSExplorer` always uses `File.listRoots()`.

### Changed
- **Project version**: `pom.xml` progressed from `0.2.0` → `0.2.1-SNAPSHOT` (start of branch) → **`0.2.1`** (release, end of branch).
- **Documentation refresh (2026-07-17)**: Updated `README.md` with a documentation map and a Windows PowerShell quick-start block, and updated `INSTALL` with clearer Windows PowerShell equivalents plus optional `*-cmdlinemode` profile guidance.
- **Surefire runtime compatibility flags**: Added `-XX:+EnableDynamicAgentLoading -Xshare:off` in `maven-surefire-plugin` `argLine` to improve local/CI compatibility with test instrumentation on recent JDKs.
- **`AddFileAction` workflow**: Refactored add flow to recursive processing with cooperative cancellation (`IProgressMonitor#isCanceled`), incremental progress updates, and GUI refresh in `finally` so partial additions are reflected even when the operation is interrupted.
- **Windows launcher behavior**: Updated `res/mkisofs/jisocreator.bat` (three iterations) to create `%LOCALAPPDATA%\jisocreator\logs`, set `-Dpath.logs`, and run via `javaw` in background mode.
- **Spanish i18n cleanup**: Normalized multiple labels/tooltips in `src/main/resources/i18n/*/messages_es.properties` (accented characters via `\uXXXX` escapes and wording consistency in main actions, OS/ISO explorer, preferences, and show-ISO-info dialogs), and fixed the `IsoTreeNode#addNode` null-safety when a node's underlying element is not a `File`.
- **`CommandLineOptionsManager` option descriptions**: All six `Option` descriptors (load, help, version, license, input, output) are now sourced from `CommandLineMessages` instead of hardcoded English strings, enabling full i18n of the help output.
- **`JISOCreatorCommandLineParser` refactoring (2026-07-17)**:
  - Introduced `JISOCreatorAttributes` record to encapsulate app/JVM/OS metadata used by CLI version/help output.
  - Moved shared CLI helpers (`buildOptions`, `buildHelpHeader`, `buildHelpFooter`) into `ICommandLineParser` default methods (`buildHelpHeader`/`buildHelpFooter` now take a `JISOCreatorAttributes` parameter instead of reading static fields).
  - `JISOCreatorCommandLineParser` now composes those helpers at runtime (`parse`/`printHelp`) and keeps `JISOCreatorCommandLineHelpFormatter` as default formatter.
  - Parser tests were updated to call these helpers through parser instances and to provide deterministic test attributes.
- **`MainAction` / `SaveAsIsoAction` decoupling from `ActionsManager`**: `MainAction.handleCommandLine` now resolves `SaveAsIsoAction` through `MainActionsManager.SAVEASISOACTION` instead of `ActionsManager.SAVEASISOACTION`, removing a GUI-manager dependency from the headless CLI save-to-ISO path. `JISOCreatorCommandLineParser#handleCommandLine` now validates the **parent directory** of the requested output file (`outputFileObj.getParentFile()`) instead of the (not-yet-existing) output file itself.
- **`ActionsManager` enum**: `MAINACTION` constant removed; `MainAction` instantiation moved to the new `MainActionsManager`.
- **`OSExplorer` root initialization**: Simplified constructor to always call `File.listRoots()`, removing the previous Windows/Linux conditional branch that used `user.home` file listing on Windows.
- **`OSTreeContentProvider` alignment with Java `File` API**: `getChildren(File)` now delegates directly to `File#listFiles()`, returning its result (including `null` for regular files) as-is instead of substituting an empty array; `getElements` no longer special-cases a `null` input. Root resolution for non-`File` input continues through `OSAndIsoExplorerManager`.
- **`ShowOnlyDirectoriesFilter`**: Went through two iterations in this release — first replacing the plain `isRoot`-aware directory check with `Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)` (so symlinks are not treated as directories), then simplifying further to `Files.isDirectory(path)` once regular directory detection was confirmed sufficient. Log level changed from `INFO` to `DEBUG`.
- **Application icon rebrand**: Refreshed `about.png`, `add.png`, `delete.png`, `exit.png`, `new.png`, `open.png`, `preferences.png`, `properties.png`, `refresh.png`, `run.png`, `saveas.png`, `up.png`, `x-cd-image.png`, `xml.png`, `drive.png` and `folder.png`. Removed the raster `iso.png`/`iso128.png`/`iso72.png`/`info.png`/`newfolder.png` assets in favor of new vector icons `iso.svg` (ISO root node icon, used by `ImageUtils#loadImage(ITreeNode)`/`(Path)`) and a new application icon pair `jisocreator.png` / `jisocreator.svg`, now referenced from `AboutDialog`, `IsoExplorerSashForm`, `MainWindow` and `PreferencesNodeManager`.
- **Documentation synchronization for branch test scope**: Updated `README.md` and `TESTING.md` to reflect current suite inventory and counts, iterating from **128 tests / 33 classes** → **147 tests / 36 classes** → final release count **174 tests / 40 classes** (see [0.2.1] test additions above).

### Fixed
- **Root executions bug**: Fixed `SaveAsIsoAction` resolution in `MainAction` (was looking up `ActionsManager.SAVEASISOACTION`, which no longer registers that action) and fixed `JISOCreatorCommandLineParser#handleCommandLine` incorrectly validating the not-yet-created output ISO file instead of its parent directory — both caused command-line `-i/-o` ISO generation to fail when run outside the GUI (e.g. as root/headless).
- **Cross-platform parser compatibility assertion**: Updated `XMLIsoFilesystemParserCompatibilityTest` to assert the expected fixture-specific `volumeID` on both Linux and Windows, avoiding platform-dependent false negatives.
- **`ShowOnlyDirectoriesFilter` symlink/root handling**: Removed the previous `OSAndIsoExplorerManager.isRoot()` fallback (root directories are already directories) and, in an intermediate iteration, used `LinkOption.NOFOLLOW_LINKS` to prevent symbolic links from being treated as directories on Linux, ensuring the tree viewer only shows actual directories.
- **Selection event log noise**: Changed `log.info` → `log.debug` in `OSExplorerSashFormSelectionChangedListener` for selection-changed events, reducing log verbosity at the INFO level during normal UI interaction.
- **`OSTreeContentProviderTest` expectation mismatch**: Updated `shouldHandleRegularFileInputWithNoChildren` to assert `null` for regular-file children, matching `File#listFiles()` behavior and preventing false negatives in local/CI test runs.
- **Spanish text typos**: Fixed misspelled `Acerda de JISOCreator` → `Acerca de JISOCreator` and applied proper accent encoding across ISO explorer, main actions, OS explorer, preferences and show-ISO-info Spanish message bundles.

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

- [Unreleased](https://github.com/Cavallinux/jisocreator/compare/v0.2.1...HEAD) - In development
- [0.2.1](https://github.com/Cavallinux/jisocreator/compare/v0.2.0...v0.2.1) - Headless-safe CLI ISO saving, CLI/dialog i18n externalization, macOS Apple Silicon packaging, icon rebrand, expanded test suite (174 tests / 40 classes)
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
