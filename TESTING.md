# JisoCreator Unit Testing Guide

## Overview
This project uses JUnit 5 and Mockito for unit tests. Tests are located under `src/test/java` and run with Maven Surefire.

## Current branch status (`feature/v0.2.3`)
- Branch in progress: `pom.xml` version is **`0.2.3-SNAPSHOT`**.
- Current suite: **278 tests in 63 classes** (see "Test Gap Analysis & Coverage Plan" below).

## Tests Updated in this pass (source changes: OS file-system explorer background-loading optimization)
Building on the previous quick-win optimizations, directory loading in the local OS file-system explorer was further optimized by moving the expensive filesystem scan off the SWT UI thread. No architecture changes were made to the `IStructuredContentProvider`/`ITreeContentProvider`/`TableProviderAdapter` contracts (the higher-risk `ILazyContentProvider` migration alternative, which would have required reimplementing filtering/sorting manually and risked affecting the shared `IsoTableProvider`, was evaluated and rejected in favor of this lower-risk approach); only internal implementation details were optimized.
- **`OSExplorer#attributesCache`** (new field, `ConcurrentMap<Path, BasicFileAttributes>`) and **`OSExplorer#warmAttributesCache(Path directory)`** (new public method): pre-fetches `BasicFileAttributes` for every direct entry of a directory via a single `Files.readAttributes(entry, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)` call per entry, consolidating what were previously up to 3 separate stat calls (`Files.isDirectory`, `Files.size`, `Files.getLastModifiedTime`) into one. The cache is fully cleared and repopulated on every call, since only one directory listing is typically visible at a time.
- **`OSExplorer#isDirectory(Path)`** (new public method): consults `attributesCache` first, falling back to a direct `Files.isDirectory(Path)` check when the path wasn't pre-fetched. `getFileType(Path)`, `length(Path)`, `lastModified(Path)`/`lastModifiedInstant(Path)`, and `getExtension(Path)` were refactored to read from the cache (via `attributesCache.get(path)` or the new `isDirectory()`) instead of issuing raw `Files.*` calls directly, while preserving identical fallback behavior when attributes aren't cached.
- **`ShowOnlyDirectoriesFilter`**, **`OSDirectoriesComparator#computeCategory(Object)`**, and **`ImageUtils#loadImage(Path)`** were updated to call `OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isDirectory(path)` instead of `Files.isDirectory(path)` directly, so all three benefit from the pre-warmed attribute cache during a table/tree render pass.
- **`LoadOSDirectoryContentsThread`** (new class, `action/jobs` package): a genuine background `Thread` (built via `@lombok.Builder`, following the precedent of `SaveISO9660ImageThread` being truly `.start()`'d for I/O-heavy work, as opposed to `ToggleHiddenFilesOSExplorerThread`'s "deferred UI-thread runnable via `asyncExec`" idiom) that calls `OSExplorer#warmAttributesCache(Path)` off the UI thread, then marshals the actual `TableViewer#setInput(Object)` call back onto the UI thread via `Display#asyncExec(Runnable)` once metadata is already warmed. A static `AtomicReference<File> LATEST_REQUESTED_DIRECTORY`, set at construction time, backs the package-private `isStillLatestRequest()` check (consulted both after the background scan completes and again right before `setInput()` executes) so that stale results are discarded if the user navigates to a different directory before a previous background load finishes.
- **`OSExplorerSashFormSelectionChangedListener`**: both call sites that previously called `mainWindow.getOsExplorer().getOsDirectoriesTable().setInput(...)` synchronously now build and `.start()` a `LoadOSDirectoryContentsThread` instead (via a new private `loadDirectoryContents(MainWindow, File)` helper), while the cheap widget updates (`osTableText.setText(...)`, action enablement) remain synchronous as before — only the actual table population is deferred to the background thread.
  - `OSExplorerTest.java` grew from 16 to **19 tests**, adding `testWarmAttributesCachePopulatesEntriesForDirectoryListing` (verifies `isDirectory`/`length` are correctly resolved for both a subdirectory and a file after warming), `testIsDirectoryUsesCachedAttributesAfterFileDeleted` (warms the cache, deletes the underlying file, then asserts `isDirectory`/`length` still return the cached — no longer independently verifiable via a fresh stat — values, proving the cache and not a live filesystem call served the result), and `testWarmAttributesCacheClearsPreviousEntries` (warms a first directory, then a second, deletes a file from the first directory, and asserts `isDirectory()` now falls back to a fresh — correctly negative — filesystem check rather than returning a stale cached value, proving the cache is cleared on every `warmAttributesCache` call).
  - `LoadOSDirectoryContentsThreadTest.java` (new file, **3 tests**): following the `ToggleHiddenFilesOSExplorerThreadTest` convention of not invoking `run()` (which depends on `OSAndIsoExplorerManager`/`Display`/`TableViewer`, none available headlessly), covers only the package-private `isStillLatestRequest()` pure logic via reflection-free direct calls — `builderShouldProduceThreadInstance`, `isStillLatestRequestShouldReturnTrueForMostRecentlyBuiltThread`, and `isStillLatestRequestShouldReturnFalseWhenSupersededByNewerRequest` (builds two threads for different directories and asserts only the most recently built one still reports itself as current).
  - `ShowOnlyDirectoriesFilterTest`, `OSDirectoriesComparatorTest`, and `ImageUtils`-dependent tests were re-run unchanged and continue to pass, confirming no behavioral regression from routing through `OSExplorer.isDirectory()`.
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **278/278, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **278/278, 7 skipped, 0 failures, no JVM crash** (skip count unchanged from the previous pass — no new native/`Display`-dependent test cases were added in this round).

## Tests Updated in this pass (source changes: OS file-system explorer performance optimization)
Directory loading in the local OS file-system explorer was optimized to reduce redundant filesystem/native-lookup calls for folders containing many files/subfolders. No architecture changes were made to the `IStructuredContentProvider`/`ITreeContentProvider` contracts; only internal implementation details were optimized.
- **`OSExplorer#findProgram(String)`** (new public method): wraps `Program.findProgram(extension)` — an OS-level file-association/MIME lookup — behind a `ConcurrentHashMap<String, Optional<Program>>` cache keyed by extension, since a given extension always resolves to the same program for the lifetime of the running application. `getFileType2(Path)` now calls this cached method instead of `Program.findProgram(extension)` directly, and `ImageUtils#loadImage(Path)` was updated to go through the same cache (previously it called `Program.findProgram(extension)` independently, duplicating the exact lookup already performed by `getFileType`). This halves the number of native lookups per non-directory file per render pass and eliminates repeats across files sharing the same extension.
  - `OSExplorerTest.java` grew from 14 to **16 tests**, adding `testFindProgramCachesResultsPerExtension` (asserts two calls with the same extension return the *same* `Program` instance, proving the second call is served from the cache rather than triggering another native lookup) and `testGetFileTypeForFileWithExtensionUsesCachedProgram` (end-to-end check that `getFileType(Path)` still resolves a non-blank type for an extension file). Both are guarded by `SwtPlatformAssumptions.assumeNativePlatformMatches()`, since `Program.findProgram` is backed by the platform-specific native SWT fragment.
- **`OSTreeContentProvider#hasChildren(Object)`**: previously called `File#listFiles()` — the same full-directory-read already performed by `getChildren(Object)` — purely to check `files.length > 0`. Now uses `Files.newDirectoryStream(Path)` and returns as soon as `iterator().hasNext()` finds a single entry, avoiding a full directory scan (`readdir`) just to decide whether a tree node should show an expand arrow. This is the single biggest win for folders containing thousands of entries.
  - `OSTreeContentProviderTest.java` grew from 3 to **5 tests**, adding `shouldReportNoChildrenForEmptyDirectory` and `shouldReportChildrenPresentWithSingleEntry` to explicitly cover the new short-circuit logic's boundary cases. The existing `shouldHandleRegularFileInputWithNoChildren` test continues to pass, since `Files.newDirectoryStream` on a regular file throws `NotDirectoryException`, which is caught and treated as "no children" (same externally observable behavior as before).
- **`OSDirectoriesComparator`**: overrides `sort(Viewer, Object[])` to populate a per-sort `IdentityHashMap<Object, Integer>` cache of each element's computed category (directory vs. file) before delegating to `super.sort(...)`. `category(Object)` now consults this cache when present, falling back to a direct `Files.isDirectory(Path)` check otherwise (e.g. when `category()`/`compare()` are called directly, outside of a `sort()` invocation, as some existing tests do). This reduces the number of `Files.isDirectory` filesystem checks from O(n log n) — one for nearly every comparison performed by the underlying `Arrays.sort` — down to O(n), one per element, for large directory listings. The class's `@Builder` annotation was moved from the class level to an explicit no-arg constructor, since Lombok would otherwise have generated an all-args constructor requiring the new cache field.
  - `OSDirectoriesComparatorTest.java` grew from 5 to **7 tests**, adding `shouldSortMixedListingWithDirectoriesFirst` (sorts 10 mixed files/directories and asserts all directories precede all files in the result) and `shouldComputeCategoryCorrectlyWithoutSortCall` (asserts `category()` still returns correct results when called directly, without an active per-sort cache).
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **272/272, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **272/272, 7 skipped, 0 failures, no JVM crash** (the 2 new native-`Program`-dependent `OSExplorerTest` cases join the existing 5 skips).

## Tests Updated in this pass (source change: `GoToParentAction.run()` enabled-state fix)
`GoToParentAction.run()` was updated to fix its post-navigation `setEnabled(...)` logic: it previously called `setEnabled(osExplorer.isRoot(file.toPath()))` (enabling the action once the *pre-navigation* file was itself a root — effectively backwards), and now calls `setEnabled(!osExplorerInstance.isRoot(parent.toPath()))` (disabling the action once the *newly selected parent* is a filesystem root, since there is no further parent to navigate to). A redundant `(IStructuredSelection)` cast on `osExplorer.getTreeSelection()` (already declared to return `IStructuredSelection`) was also removed.
- **Not unit-tested**, consistent with the established convention documented in `GoToParentActionTest`'s class javadoc: the entire `run()` body is coupled to `GUIManager.INSTANCE.getMainWindow()` (a real `MainWindow`/`OSExplorerSashForm` with live `Tree`/`Display`-bound controls) and `OSAndIsoExplorerManager.INSTANCE.getOsExplorer()`, so it cannot be invoked headlessly. `GoToParentActionTest` continues to cover only builder state (message/tooltip/imageDescriptor, initial `enabled=false`), `instanceof` checks, and builder-instance independence — unaffected by this internal logic fix.
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **266/266, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **266/266, 5 skipped, 0 failures, no JVM crash**.

## Tests Updated in this pass (source changes: `OSExplorer.getUnixOSRoot()`, `OSExplorerSashForm.setInitialSelection()`, `MainWindow`)
Further production changes (unrelated to the Group 1–8 gap-analysis workflow) added an "OS explorer initial selection" feature: on non-Windows platforms, `MainWindow#createContents` now calls the new `OSExplorerSashForm#setInitialSelection()` right after building the OS explorer panel, which selects the Unix filesystem root (`OSExplorer#getUnixOSRoot()`) in the OS directories tree.
- **`OSExplorer#getUnixOSRoot()`** (new method, `return roots[0]`): pure array-access logic, no `Display`/GUI dependency — testable the same way as the rest of `OSExplorer`. `OSExplorerTest.java` grew from 13 to **14 tests**, adding `testGetUnixOSRoot` (sets a two-element roots array via the existing `@Setter roots` and asserts `getUnixOSRoot()` returns the first element).
- **`OSExplorerSashForm#setInitialSelection()`** and the `MainWindow#createContents` call site: **not unit-tested**, consistent with the established convention — `OSExplorerSashForm` is a real `SashForm`/`Composite` built with live `Tree`/`TableViewer` controls (constructed in its own constructor), and `setInitialSelection()` calls `osDirectoriesTree.setSelection(...)`/`osDirectoriesTree.expandToLevel(...)` (the latter added in a follow-up change to also expand the Unix root node one level) on one of those real, `Display`-bound controls. Like the rest of the GUI-composite layer, this has no test file today and remains deferred to the future headless-SWT (Xvfb) harness tracked as Group 8.
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **266/266, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **266/266, 5 skipped, 0 failures, no JVM crash**.

## Tests Updated in this pass (source changes: `MainAction`, `JISOCreatorAttributes`, `JISOCreatorCommandLineParser`)
Three production classes were modified outside the Group 1–8 gap-analysis workflow (via direct edits to `MainAction.run()`, `JISOCreatorAttributes`, and `JISOCreatorCommandLineParser`), and their associated tests were updated to keep coverage current:
- **`JISOCreatorAttributes`**: added `toString()` (formats `"<appName> version <appVersion>"`) and `toString(String baseString)` (substitutes `appName`/`appVersion`/`jvmVersion`/`jvmVendor`/`osName`, in that order, into an arbitrary `String.format` pattern — used by `printVersion()` below). `JISOCreatorAttributesTest.java` grew from 4 to **6 tests**, adding `shouldFormatDefaultToStringAsNameAndVersion` and `shouldFormatCustomToStringWithBaseString`.
- **`JISOCreatorCommandLineParser.printVersion()`**: now delegates the argument list construction to `attributes.toString(CommandLineMessages.commandLineVersionMessage)` instead of building a `List.of(...)` inline — same runtime output, simpler call site. `JISOCreatorCommandLineParserTest.java` grew from 20 to **21 tests**, adding `testPrintVersionContainsAttributeValues`, which captures `System.out` while calling `printVersion()` and asserts the rendered text contains the configured `appName`/`appVersion`/`jvmVersion`/`jvmVendor`/`osName` values (validating the new `toString(baseString)` delegation end-to-end, not just "does not throw").
- **`MainAction.run()`**: the GUI-mode log statement now logs `ICommandLineParser.buildAttributes()` (relying on the new `JISOCreatorAttributes.toString()`) instead of a static string. No test change was required: `MainActionTest`'s `TestableMainAction` overrides `run()` entirely (to avoid touching `GUIManager`/`Display`), so the log-line content is outside what that test exercises; this remains consistent with the project's convention of never asserting on log output for GUI-coupled `run()` methods.
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **266/266, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **266/266, 5 skipped, 0 failures, no JVM crash**.

## Tests Added in this pass (Gap Analysis — Group 7: `util/ImageUtils`)
No new test files were added for this group either. A dedicated investigation of `util/ImageUtils.java` determined that **every single method requires a live, real `Display`**, so there is no pure-logic subset left to isolate — even the ones that look like plain path/name resolution:

- The class's own no-arg `@Builder` constructor calls `Display.getDefault()` immediately (`imageRegistry = new ImageRegistry(Display.getDefault())`, `ImageUtils.java:29-31`) — it is **impossible to even instantiate `ImageUtils` without a real `Display`**.
- `loadImage(String name)` and `loadImage(Program program)` both call `new Image(Display.getDefault()/getCurrent(), ...)` directly.
- `loadImage(ITreeNode node)` and `loadImage(Path path)` contain some branching that *looks* pure (root/directory checks via `OSAndIsoExplorerManager`, `Files.isDirectory`, extension resolution), but every branch always terminates in a call to one of the two `Display`-bound `loadImage` overloads above — there is no code path that returns without touching `Display`.
- `loadImageDescriptor(String imagePath)` simply wraps `loadImage(imagePath)`.

Unlike the native-`Transfer`-registration tests in Group 2 (which only *register* a `Transfer` type — a lightweight, already-safe-when-matching operation), constructing `ImageUtils` and calling any of its methods would create a **real** `ImageRegistry`/`Image` backed by a live `Display`/X11 connection. A grep across every existing test file in the suite (`grep -rn "Display\.get" src/test/java`) confirms this project's established convention: `Display.getDefault()`/`Display.getCurrent()` are **only ever mentioned in javadoc comments explaining an exclusion**, never actually invoked by any test — this session's tests followed that same convention throughout Groups 1–6 (using subclassing/reflection instead of a live `Display`, as documented above). Actually calling `Display.getDefault()` from a test — even behind a `SwtPlatformAssumptions.assumeNativePlatformMatches()` guard — would only prevent the *mismatched-platform crash*; it would still open a real windowing-system connection as a side effect, which is integration-test territory (and would silently hang or fail in a true headless CI runner with no `DISPLAY` at all, regardless of profile).

**Conclusion for Group 7**: `util/ImageUtils` is deferred to the Group 8 headless-SWT-harness (Xvfb) work item for the same reasons as the 6 classes deferred from Group 6 — none of its behavior can be verified without constructing a real `Display`.

## Tests Added in this pass (Gap Analysis — Group 6: instance managers/singletons)
- `instances/JFaceResourcesManagerTest.java` (**new, 4 tests**) — verifies both enum constants (`OSEXPLORER_INSTANCE`/`ISOEXPLORER_INSTANCE`) expose the correct concrete filter/comparator/label-provider/content-provider/table-provider-adapter/menu-listener/double-click-listener/selection-changed-listener types, the shared tree/table `SWT` style options (`COMPOSITE_SWT_OPTIONS`), and `values()`/`valueOf()` enum contract. Unlike the other 6 classes in this group, none of `JFaceResourcesManager`'s constructor arguments reference `ImageRegister`/`Display` — empirically verified via a standalone `java` reproduction (no native-library-load warning) — so it is safe to reference headlessly on any platform. **Note:** the two usages of `ImageRegister` in `OSTreeLabelProvider`/`OsTableProvider` (constructed as part of `OSEXPLORER_INSTANCE`) live inside their `getImage(...)`/`getColumnImage(...)` method bodies, not their no-arg builders/constructors, so building these providers does **not** eagerly trigger `Display` — consistent with `OSTreeLabelProviderTest`/`OsTableProviderTest` already passing headlessly since earlier releases.

For the other 6 classes originally targeted by this group, no new test files were added. A dedicated investigation (including empirical, standalone-`java` reproduction outside Surefire) determined that **all 6 are fundamentally impossible to unit-test headlessly**, on **any** platform, not just on a mismatched one:

- `instances/ImageRegister` — its sole enum constant `INSTANCE` calls `ImageUtils.builder().build()` at class-load time, and `ImageUtils`' constructor immediately calls `Display.getDefault()` (`ImageUtils.java`: `imageRegistry = new ImageRegistry(Display.getDefault())`). **Merely referencing the `ImageRegister` type at all — even just to load the class — forces real `Display` creation.** This is the root cause propagating to every other class below.
- `instances/ActionsManager`, `instances/IsoExplorerActionsManager`, `instances/OSExplorerActionsManager` — every enum constant's initializer calls `ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor(...)` to build its icon, so referencing any of these types eagerly constructs **every** action in the manager plus loads **every** one of their real icon images — the same "too broad a side effect for a unit test" situation already excluded in Groups 2–3 (`AddFileAction.run()`, `PreferencesAction.createPreferenceManager()`), except here it is baked into simply loading the class, not just calling one method.
- `instances/PreferencesNodeManager` — same pattern: both enum constants' initializers call `ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("iso.svg")` directly in the field initializer.
- `instances/GUIManager` — its `INSTANCE` constant builds a real `MainWindow` (`MainWindow.builder().build()`). Empirically verified (standalone `java` probe, classpath extracted via `mvn -o dependency:build-classpath`) that this constructor chain — via `addMenuBar()` invoking the overridden `MainWindow.createMenuManager()` — populates the real main menu with `ActionsManager`'s actions, which transitively triggers the same `ImageRegister` → `Display.getDefault()` chain. The probe's log output confirmed real menu/toolbar/status-line manager construction and an SWT native-library-load warning, even though the platform matched the host in that run.

Because the "poisoning" happens the instant any of these enum types is loaded by the JVM (not just when a particular method is called), there is **no safe subset of pure/mockable logic** to isolate — unlike Groups 1–5, where the GUI-coupled `run()` methods could simply be skipped while the builder/constructor/pure-logic parts remained testable. Here the class itself, at `<clinit>` time, is the GUI-coupled part. Attempting to reference any of these 6 types — even behind a `SwtPlatformAssumptions.assumeNativePlatformMatches()` guard — would still construct a full, real `Display`/menu/icon tree as an unavoidable side effect, which is integration-test territory, not unit-test territory.

**Conclusion for Group 6**: these 6 classes are deferred to the future headless-SWT-harness (Xvfb) work item already tracked as Group 8 in the "Test Gap Analysis & Coverage Plan" below, where they can be exercised as part of true GUI integration tests with a real (headless) `Display`, rather than forced into unit tests that would violate the project's existing convention of not unit-testing `Display`-bound code paths. `JFaceResourcesManager` was the one exception in this group and is now fully covered (see above).
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **266/266, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **266/266, 5 skipped, 0 failures, no JVM crash** — `JFaceResourcesManagerTest` never touches a native SWT API, so it runs identically (and is never skipped) under both profiles.

## Tests Added in this pass (Gap Analysis — Group 5: OS explorer actions & jobs)
- `action/osexplorer/GoToParentActionTest.java` (**new, 4 tests**), `OpenActionTest.java` (**new, 5 tests**), `RefreshExplorerActionTest.java` (**new, 4 tests**), `ShowHiddenFilesActionTest.java` (**new, 4 tests**) — builder state (message/tooltip/imageDescriptor), the initial `enabled` flag (`false` for `GoToParentAction`/`OpenAction`; left at the JFace `Action` default of `true` for `RefreshExplorerAction`/`ShowHiddenFilesAction`, since neither constructor calls `setEnabled(false)`), the `AS_CHECK_BOX` style used by `ShowHiddenFilesAction` (via `getStyle()`), the Lombok `@Getter`/`@Setter` round-trip on `OpenAction.file`, and `instanceof` checks against `JISOCreatorBaseAction`. No `run()` is invoked: all are coupled to `GUIManager.INSTANCE`/`OSAndIsoExplorerManager.INSTANCE`/`Display.getCurrent()`, and `OpenAction.run()`'s file branch additionally calls the native `Program.launch` (via `OSExplorer.launch`), same exclusion rationale as `OpenIsoEntryAction` in Group 4.
- `action/jobs/ToggleHiddenFilesOSExplorerThreadTest.java` (**new, 2 tests**) — builder/`Thread` instantiation, plus the private `removeFilters(StructuredViewer)` helper (pure JFace filter-list manipulation), invoked via reflection against a minimal headless `StructuredViewer` subclass (no `Control`/`Display`), reusing the subclassing technique from `JISOCreatorDragSourceAdapterTest` (Group 2). `run()` is **not** exercised: it unconditionally triggers the eager `OSExplorerActionsManager`/`JFaceResourcesManager` enum initializers (real image loading via `ImageRegister`) and depends on `GUIManager.INSTANCE.getMainWindow()`, the same too-broad-a-side-effect exclusion applied to `AddFileAction.run()`/`PreferencesAction.createPreferenceManager()` in Groups 2–3.
- `IFileManagementAction.obtainFileFilterNames`/`obtainFileDialogExtensions` (default methods) were **already covered** in Group 3 via `SaveAsXMLActionTest`/`OpenIsoLayoutActionTest` — no Group 5 class implements `IFileManagementAction`, so no additional test was needed for this item of the plan.
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **258/258, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **258/258, 5 skipped, 0 failures, no JVM crash** — the new Group 5 tests never touch a native SWT API, so they run identically (and are never skipped) under both profiles.

## Tests Added in this pass (Gap Analysis — Group 4: ISO explorer actions)
- `action/isoexplorer/DeleteIsoEntryActionTest.java` (**new, 4 tests**), `GoToIsoEntryParentActionTest.java` (**new, 4 tests**), `OpenIsoEntryActionTest.java` (**new, 5 tests**), `ShowIsoInformationActionTest.java` (**new, 4 tests**) — builder state (message/tooltip/imageDescriptor), the initial `enabled` flag set by each constructor (`false` for all except `ShowIsoInformationAction`, which is `true`), `instanceof` checks (`JISOCreatorBaseAction`/`IRunnableWithProgress`), and — for `OpenIsoEntryAction` — the Lombok `@Getter`/`@Setter` round-trip on its `node` field (using a mocked `ITreeNode` interface). No `run()`/`run(IProgressMonitor)` method in this package is invoked: all of them are coupled to `GUIManager.INSTANCE.getMainWindow()`/`Display.getDefault()`/a real modal `Shell`, and `OpenIsoEntryAction.run()`'s file branch additionally calls `OSExplorer.launch(Path)`, which delegates to the native SWT `Program.launch(String)` API and would literally open an external application if invoked — consistent with the exclusion convention established in Groups 2–3.
- Validated per this session's standing rule: `mvn -o clean test` (default `linux` profile) → **239/239, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **239/239, 5 skipped, 0 failures, no JVM crash** — the new Group 4 tests never touch a native SWT API, so they run identically (and are never skipped) under both profiles.

## Tests Added in this pass (Gap Analysis — Group 1: model logic without SWT)
- `util/IOUtilsTest.java` (**new, 6 tests**) — covers `getStore()`/`saveStore()`/`loadFileContentFromClasspath()`/`loadFormattedLicenseFile()`, the methods of `IOUtils` not exercised by `IOUtilsPathTest` (which only covers path manipulation). Uses `@TempDir`-backed `PreferenceStore` instances to avoid touching the real `~/.config/jisocreator` store.
- `model/isoexplorer/decl/ITreeNodeTest.java` (**new, 13 tests**) — dedicated coverage for all 13 `default` methods declared in `ITreeNode`, using a minimal no-override test double (`BareTreeNode`), analogous to the existing `ICommandLineParserTest` pattern for interface default methods. Previously these defaults were only exercised indirectly through `IsoTreeNodeTest`/`TreeNodeTest` overrides, never in their un-overridden form.

## Tests Added in this pass (Gap Analysis — Group 3: main menu actions)
- `action/main/AboutActionTest.java` (**new, 3 tests**), `ExitApplicationActionTest.java` (**new, 3 tests**), `LoadCommandLineISOLayoutActionTest.java` (**new, 3 tests**), `NewIsoLayoutActionTest.java` (**new, 3 tests**), `OpenIsoLayoutActionTest.java` (**new, 4 tests**), `PreferencesActionTest.java` (**new, 2 tests**), `SaveAsXMLActionTest.java` (**new, 4 tests**) — builder state (message/tooltip/imageDescriptor), `instanceof` type checks against `JISOCreatorBaseAction`/`IRunnableWithProgress`/`IFileManagementAction`, and — where applicable — the pure default methods `obtainFileFilterNames`/`obtainFileDialogExtensions` from `IFileManagementAction`. Every `run()`/`run(IProgressMonitor)` in this package is coupled to `GUIManager.INSTANCE`, `Display.getDefault()` or a real `Shell`, so none are invoked directly, following the same convention as `SaveAsIsoActionTest`.

## Tests Added in this pass (Gap Analysis — Group 2: DnD & providers)
- `model/providers/isoexplorer/IsoTreeLabelProviderTest.java` (**new, 2 tests**) — root vs. non-root label dispatch (`getIsoName()` vs `getShortName()`), analogous to the existing `OSTreeLabelProviderTest`, using real `IsoTreeNode` instances.
- `model/dnd/JISOCreatorViewerDropAdapterTest.java` (**new, 2 tests**) — `validateDrop` accept/reject of `TransferData` types using the real, native-platform-gated `FileTransfer` type registration (see "Note on running tests under non-default platform profiles" below). `performDrop` is intentionally **not** exercised (see that same note).
- `model/dnd/JISOCreatorDragSourceAdapterTest.java` (**new, 3 tests**) — `dragStart` publishing the viewer selection to `LocalSelectionTransfer`; `dragSetData` populating `FileTransfer` data with absolute file paths, and clearing `event.data` for unsupported transfer types. Uses a minimal `Viewer` subclass (no mocking of concrete/abstract SWT/JFace classes — see note below) and a small reflection-based helper to construct a real `DragSourceEvent` headlessly.

### Note on testing SWT event classes without a `Display`
Concrete SWT classes (e.g. `DragSourceEvent`, `Viewer`) **cannot be mocked with Mockito in this project's toolchain** (JDK 26 + Mockito inline mock maker fails to retransform them — verified empirically; only interfaces like `IStructuredSelection`/`IProgressMonitor` are mockable, consistent with all prior tests in this suite). Two safe, headless-only alternatives were used instead, and should be reused for future SWT-touching tests before falling back to a full Xvfb harness:
1. **Subclass abstract SWT/JFace types directly** (e.g. a minimal `Viewer` subclass) instead of mocking them.
2. **Construct real event objects via reflection** when their only non-public constructor path requires an internal SWT type (e.g. `DragSourceEvent(DNDEvent)`, where `DNDEvent` is package-private). This requires a non-null placeholder `Widget` as the event's `EventObject` source, obtained via `Objenesis.newInstance(...)` (a Mockito transitive dependency, now declared explicitly as a test dependency in `pom.xml`) to bypass `Widget`'s `Display`-dependent constructor — no native GUI/X server is created or touched.

### Note on running tests under non-default platform profiles (`-Pwindows`, `-Papplesilicon`, ...)
The project's Maven profiles (`linux`, `windows`, `applesilicon`, ...) each select a **platform-specific SWT native fragment** (gtk/win32/cocoa) on the classpath. Several tests exercise real SWT native APIs that are headless-safe *only when the active fragment matches the actual host OS*:
- `Transfer` registration (`FileTransfer.getInstance()`, `LocalSelectionTransfer.getTransfer()`, used by `JISOCreatorViewerDropAdapterTest`/`JISOCreatorDragSourceAdapterTest`) requires the native SWT library to load successfully.
- `Display.getDefault()` (reached transitively via `ImageRegister`/`ImageUtils`, e.g. from `PreferencesNodeManager`/`OSExplorerActionsManager` enum initializers) does the same.

When the active platform profile does **not** match the host OS (e.g. running `-Pwindows` on a Linux/macOS host to validate the profile compiles/tests cleanly), invoking any of these native APIs makes SWT call an internal, **non-catchable** `System.exit(1)` — no Java `try/catch` (including the ones inside `performDrop`/`validateDrop`) can intercept it, and it crashes the entire Surefire forked JVM instead of failing a single test.

To keep the suite runnable fluidly under **any** platform profile without ever attempting to load a mismatched native library:
- `cl.cavallinux.jisocreator.testsupport.SwtPlatformAssumptions.assumeNativePlatformMatches()` is called as the **first line** of any test (or its `@BeforeEach`) that touches a native-dependent SWT API. It compares the pure, non-native constant `SWT.getPlatform()` against `System.getProperty("os.name")` and uses `Assumptions.assumeTrue(...)` to **skip** (not fail) the test when they don't match.
- `JISOCreatorViewerDropAdapterTest`/`JISOCreatorDragSourceAdapterTest` use this guard. `performDrop` (in `JISOCreatorViewerDropAdapterTest`) and `createPreferenceManager()` (in `PreferencesActionTest`) are **not exercised at all**, even with the guard, because they unconditionally trigger eager enum initializers (`OSExplorerActionsManager`, `PreferencesNodeManager`) that construct every action/node in the manager — including real image loading — as an unavoidable side effect of merely referencing the enum, which is too broad a side effect for a unit test regardless of platform match.
- Verified: `mvn -o clean test` (default `linux` profile) → **222/222, 0 skipped**; `mvn -o clean test -Pwindows` (mismatched profile on this Linux host) → **222/222, 5 skipped, 0 failures, no JVM crash**.

## Test Gap Analysis & Coverage Plan

A full architecture-driven audit compared all 92 production classes under `src/main/java` against the 40 (now 62) test classes under `src/test/java`. After excluding 8 i18n message classes already covered indirectly by `MessagesBundleTest` (`AboutDialogMessages`, `IsoExplorerMessages`, `MainActionsMessages`, `MainWindowMessages`, `OSExplorerMessages`, `PreferenceDialogMessages`, `ShowIsoInformationDialogMessages`, `INLSBundleMessages`), **48 production classes remain without a dedicated `*Test.java`**. They are grouped below by architectural layer and priority; groups 1–5 are complete, group 6 is complete for its one testable class (the other 6 deferred to Group 8), and group 7 is fully documented as deferred to Group 8 as well.

1. **DONE — Model logic without SWT (highest priority):** `util/IOUtils`, `model/isoexplorer/decl/ITreeNode`.
2. **DONE — DnD & providers (high priority):** `model/dnd/JISOCreatorDragSourceAdapter`, `model/dnd/JISOCreatorViewerDropAdapter`, `model/providers/isoexplorer/IsoTreeLabelProvider`.
3. **DONE — Main menu actions (medium-high priority):** `action/main/AboutAction`, `ExitApplicationAction`, `LoadCommandLineISOLayoutAction`, `NewIsoLayoutAction`, `OpenIsoLayoutAction`, `PreferencesAction`, `SaveAsXMLAction` — followed the headless-builder pattern of `MainActionTest`/`SaveAsIsoActionTest`; only builder state, type checks and pure default methods (`IFileManagementAction.obtainFileFilterNames`/`obtainFileDialogExtensions`) are exercised, since every `run()` is coupled to `GUIManager.INSTANCE`/`Display`.
4. **DONE — ISO explorer actions (medium-high priority):** `action/isoexplorer/DeleteIsoEntryAction`, `GoToIsoEntryParentAction`, `OpenIsoEntryAction`, `ShowIsoInformationAction` — same headless-builder pattern as Group 3; `OpenIsoEntryAction`'s Lombok `node` accessor/mutator is additionally covered, since it is pure state with no SWT coupling.
5. **DONE — OS explorer actions & jobs (medium priority):** `action/osexplorer/GoToParentAction`, `OpenAction`, `RefreshExplorerAction`, `ShowHiddenFilesAction`, `action/jobs/ToggleHiddenFilesOSExplorerThread` — same headless-builder pattern as Groups 3–4; `ToggleHiddenFilesOSExplorerThread`'s private `removeFilters(StructuredViewer)` (pure JFace filter-list logic) is additionally covered via reflection against a minimal `StructuredViewer` subclass. `IFileManagementAction`'s pure default methods were already covered in Group 3.
6. **DOCUMENTED / PARTIALLY DONE — Instance managers/singletons (medium priority):** `instances/ActionsManager`, `IsoExplorerActionsManager`, `OSExplorerActionsManager`, `PreferencesNodeManager`, `ImageRegister`, `GUIManager` — investigated in depth (see "Note on instance managers/singletons that cannot be unit-tested headlessly" above); every one of these enum types eagerly forces real `Display`/image/menu construction the instant the class is loaded (root cause: `ImageRegister.INSTANCE` → `new ImageUtils()` → `Display.getDefault()`), with no safe pure-logic subset to isolate. No unit tests were added for these 6; they require the Group 8 headless-SWT (Xvfb) harness instead. `instances/JFaceResourcesManager` (the 7th class originally in scope) does **not** share this problem — none of its constructor arguments touch `ImageRegister`/`Display` — and is now fully covered by `JFaceResourcesManagerTest` (4 tests).
7. **DOCUMENTED / DEFERRED to Group 8 — `util/ImageUtils` (low-medium priority):** every method (including its own no-arg constructor) requires a live, real `Display` — see "Note on `util/ImageUtils`" above for the full investigation. No pure-logic subset exists to isolate; deferred to the Group 8 headless-SWT (Xvfb) harness.
8. **Pending — Full GUI/SWT classes (low priority, needs new infrastructure):** `gui/dialog/*` (`AboutDialog`, `ADDFileToIsoLayoutDialog`, `JISOCreatorPreferencesDialog`, `ShowIsoLayoutInformationDialog`), `gui/window/MainWindow`, `gui/sashfom/*`, `gui/preference/*`, `gui/listeners/*` (6 classes), `gui/decl/ICompositeCreator`. Already flagged in "Future Testing Enhancements" below; requires a headless SWT test harness (e.g. Xvfb) before writing tests — do not use partial mocks that break the real-widget architecture.

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
    │   ├── isoexplorer/
    │   │   ├── DeleteIsoEntryActionTest.java
    │   │   ├── GoToIsoEntryParentActionTest.java
    │   │   ├── OpenIsoEntryActionTest.java
    │   │   └── ShowIsoInformationActionTest.java
    │   ├── jobs/
    │   │   ├── SaveISO9660ImageThreadTest.java
    │   │   └── ToggleHiddenFilesOSExplorerThreadTest.java
    │   ├── osexplorer/
    │   │   ├── AddFileActionRecursiveTest.java
    │   │   ├── GoToParentActionTest.java
    │   │   ├── OpenActionTest.java
    │   │   ├── RefreshExplorerActionTest.java
    │   │   └── ShowHiddenFilesActionTest.java
    │   └── main/
    │       ├── AboutActionTest.java
    │       ├── ExitApplicationActionTest.java
    │       ├── LoadCommandLineISOLayoutActionTest.java
    │       ├── MainActionTest.java
    │       ├── NewIsoLayoutActionTest.java
    │       ├── OpenIsoLayoutActionTest.java
    │       ├── PreferencesActionTest.java
    │       ├── SaveAsIsoActionTest.java
    │       └── SaveAsXMLActionTest.java
    ├── gui/
    │   └── i18n/
    │       ├── AddToISODialogMessagesTest.java
    │       ├── CommandLineMessagesTest.java
    │       └── MessagesBundleTest.java
    ├── instances/
    │   ├── CommandLineOptionsManagerTest.java
    │   ├── CommandLineParserManagerTest.java
    │   ├── IOManagerTest.java
    │   ├── JFaceResourcesManagerTest.java
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
    │   ├── dnd/
    │   │   ├── JISOCreatorDragSourceAdapterTest.java
    │   │   └── JISOCreatorViewerDropAdapterTest.java
    │   ├── filters/
    │   │   ├── HideHiddenFilesFilterTest.java
    │   │   ├── ShowOnlyDirectoriesFilterTest.java
    │   │   └── isoexplorer/
    │   │       └── ShowOnlyIsoDirectoriesFilterTest.java
    │   ├── isoexplorer/
    │   │   ├── decl/
    │   │   │   └── ITreeNodeTest.java
    │   │   └── impl/
    │   │       ├── IsoFileSystemTest.java
    │   │       ├── IsoTreeNodeTest.java
    │   │       └── TreeNodeTest.java
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
    │       │   ├── IsoTreeContentProviderTest.java
    │       │   └── IsoTreeLabelProviderTest.java
    │       └── osxplorer/
    │           ├── OSTreeContentProviderTest.java
    │           ├── OSTreeLabelProviderTest.java
    │           └── OsTableProviderTest.java
    ├── testsupport/
    │   └── SwtPlatformAssumptions.java  ← new (non-test helper, no @Test methods)
    └── util/
        ├── IOUtilsPathTest.java
        └── IOUtilsTest.java
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
> **Note:** running `-Pwindows` (or any platform profile that doesn't match the host OS) does not fail the build. A handful of native-Transfer-dependent tests self-skip via `SwtPlatformAssumptions.assumeNativePlatformMatches()` instead of attempting to load a mismatched native SWT library (see "Note on running tests under non-default platform profiles" above). Expect `Skipped: 5` when running `-Pwindows` on a non-Windows host.

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

## Current Test Statistics (`feature/v0.2.3`)
- **Total Tests**: 278
- **Test Classes**: 63
- **All Tests Passing**: ✓ (`mvn -o clean test`: 278/278, 0 skipped; `mvn -o clean test -Pwindows`: 278/278, 7 skipped, 0 failures)

### Test Statistics Summary
```
AddFileActionRecursiveTest.java:               10 tests
GoToParentActionTest.java:                      4 tests  ← new
OpenActionTest.java:                            5 tests  ← new
RefreshExplorerActionTest.java:                 4 tests  ← new
ShowHiddenFilesActionTest.java:                 4 tests  ← new
MainActionTest.java:                            2 tests
SaveAsIsoActionTest.java:                       8 tests
AboutActionTest.java:                           3 tests
ExitApplicationActionTest.java:                 3 tests
LoadCommandLineISOLayoutActionTest.java:        3 tests
NewIsoLayoutActionTest.java:                    3 tests
OpenIsoLayoutActionTest.java:                   4 tests
PreferencesActionTest.java:                     2 tests
SaveAsXMLActionTest.java:                       4 tests
DeleteIsoEntryActionTest.java:                  4 tests
GoToIsoEntryParentActionTest.java:               4 tests
OpenIsoEntryActionTest.java:                     5 tests
ShowIsoInformationActionTest.java:               4 tests
JISOCreatorBaseActionTest.java:                 4 tests
SaveISO9660ImageThreadTest.java:                4 tests
ToggleHiddenFilesOSExplorerThreadTest.java:      2 tests
LoadOSDirectoryContentsThreadTest.java:          3 tests  ← new
AddToISODialogMessagesTest.java:                3 tests
CommandLineMessagesTest.java:                   5 tests
MessagesBundleTest.java:                        2 tests
CommandLineOptionsManagerTest.java:             7 tests
CommandLineParserManagerTest.java:              1 test
IOManagerTest.java:                             2 tests
JFaceResourcesManagerTest.java:                  4 tests  ← new
JISOCreatorISOLevelOptionsTest.java:            2 tests
JISOCreatorLanguageOptionsTest.java:            2 tests
MainActionsManagerTest.java:                    6 tests
OSAndIsoExplorerManagerTest.java:               1 test
ICommandLineParserTest.java:                    9 tests
JISOCreatorAttributesTest.java:                 6 tests
JISOCreatorCommandLineHelpFormatterTest.java:   6 tests
JISOCreatorCommandLineParserTest.java:         21 tests
ITreeNodeDirectoriesFirstComparatorTest.java:   2 tests
OSDirectoriesComparatorTest.java:                7 tests  ← updated
JISOCreatorDragSourceAdapterTest.java:           3 tests
JISOCreatorViewerDropAdapterTest.java:           2 tests
HideHiddenFilesFilterTest.java:                 2 tests
ShowOnlyDirectoriesFilterTest.java:              4 tests
ShowOnlyIsoDirectoriesFilterTest.java:           2 tests
ITreeNodeTest.java:                            13 tests
IsoFileSystemTest.java:                         4 tests
IsoTreeNodeTest.java:                           8 tests
TreeNodeTest.java:                              3 tests
OSExplorerTest.java:                           19 tests  ← updated
XMLIsoFilesystemParserCompatibilityTest.java:   2 tests
IsoFilesystemParserTest.java:                   3 tests
XMLIsoFilesystemContractMapperTest.java:        3 tests
XMLIsoFilesystemContractTest.java:              2 tests
XMLIsoFilesystemParserTest.java:                4 tests
TableProviderAdapterTest.java:                  2 tests
IsoTableProviderTest.java:                      3 tests
IsoTreeContentProviderTest.java:                2 tests
IsoTreeLabelProviderTest.java:                  2 tests
OSTreeContentProviderTest.java:                 5 tests  ← updated
OSTreeLabelProviderTest.java:                   1 test
OsTableProviderTest.java:                       3 tests
IOUtilsPathTest.java:                           5 tests
IOUtilsTest.java:                               6 tests
----------------------------------------------------------
Total:                                        278 tests
```

## Notes on SWT-Dependent Testing
Some production classes depend on SWT/JFace runtime state (`Display`, images, widgets). Current suite prioritizes behavior that can be verified headless. UI-heavy integration tests are still pending. See also "Note on running tests under non-default platform profiles" above for the `SwtPlatformAssumptions` skip mechanism used by native-Transfer-dependent tests.

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
1. Complete the Test Gap Analysis plan above (Group 8): the full-GUI/SWT classes, which will also cover the 6 instance managers/singletons deferred from Group 6 and `util/ImageUtils` deferred from Group 7 — all require a headless SWT harness (Xvfb) with a real `Display`.
2. Add integration tests for GUI actions and dialogs with SWT harness (e.g. `ADDFileToIsoLayoutDialog`, `AboutDialog`, `MainWindow`, `IsoExplorerSashForm`).
3. Add deeper negative/error-path tests for image loading fallbacks in SWT-bound components (`ImageUtils`, `ImageRegister`).
4. Add broader action coverage (`ActionsManager` and remaining `action/*` classes not yet covered, e.g. full `AddFileAction.run()` GUI dialog flow).
5. Add coverage reporting (JaCoCo) in CI.

## Last Validation Run

Latest local validation executed with:

```bash
mvn -o clean test
mvn -o clean test -Pwindows
```

Results:
- Default (`linux`) profile: **278 tests passing in 63 classes, 0 skipped** (from `target/surefire-reports`).
- `windows` profile (mismatched native platform on this Linux host): **278 tests, 7 skipped, 0 failures, no JVM crash** — 5 skips are the native-`Transfer`-dependent tests in `JISOCreatorViewerDropAdapterTest`/`JISOCreatorDragSourceAdapterTest`, plus 2 native-`Program`-dependent skips in `OSExplorerTest` (unchanged from the previous pass — the new background-loading tests added in this round require no native SWT calls). `JFaceResourcesManagerTest` never touches a native SWT API and therefore runs identically under both profiles.