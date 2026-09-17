# Cascara VSIX Package Support

## Cascara VSIX Module

The **Cascara VSIX Module** is the core library responsible for constructing, reading, and writing Visual Studio Code extension packages (`.vsix`). It provides a fully programmatic interface for assembling VSIX archives using Cascara’s internal JSON, XML, and archive tooling, without requiring external VS Code packaging utilities.

At its center is the `VsixPackage` class, which models a VSIX file as an editable Open Packaging Conventions (OPC) archive. It manages all extension metadata, theme contributions, manifest generation, and file layout under the `extension/` directory.

### Key Responsibilities

- **Package Assembly**

  Builds the complete VSIX structure, including:

  - `extension/package.json`

  - `extension/extension.vsixmanifest`

  - `extension/[Content_Types].xml`

  - `extension/themes/*.json`

  - `extension/images/*`

  - Optional files (`README.md`, `CHANGELOG.md`, `LICENSE.md`)

- **Manifest Parsing & Serialization**

  Reads existing VSIX files (`open(Path)`) and reconstructs:

  - Identity metadata (publisher, version, language, ID)

  - Icon and repository information

  - Theme contributions and UI theme classification

- **Theme Contribution Extraction**

  Automatically inspects theme JSON files and injects them into `package.json` under the `contributes.themes` section.
  This includes:

  - Theme name

  - Theme type (`light` / `dark`)

  - Semantic highlighting flag

  - Correct UI theme mapping (`vs-light` / `vs-dark`)

  - Relative theme path inside the VSIX

- **Content-Type and Manifest Generation**

  Produces valid OPC metadata and VS Code manifest XML without requiring templates or external tools.
  The module ensures:

  - Correct MIME types

  - Valid VSIX schema compliance

  - Proper asset declarations for Marketplace publishing

- **Optional File Enumeration**

  Detects presence of optional files and automatically adds Marketplace assets for:

  - README

  - CHANGELOG

  - LICENSE

### Usage

The module is designed for internal use by the Cascara VSIX Gradle Plugin, but can also be used directly:

```java
Path vsixPath = Path.of("build/distributions/mytheme.vsix");
VsixPackage pkg = VsixPackage.create(vsixPath);
pkg.setName("mytheme");
pkg.setDisplayName("My Theme");
pkg.setPublisher("Cascara");
pkg.setVersion("1.0.0");

// Add theme JSON files
pkg.addThemesFromDirectory(Path.of("build/themes/vscode"));

// Add images
pkg.addImagesFromDirectory(Path.of("src/main/resources/vscode/images"));

// Finalize package
pkg.close();
```

### Design Notes

- **In-memory manifests**

  `package.json` and `extension.vsixmanifest` are not written until `flush()` or `close()`, allowing incremental updates.

- **Interceptors**

  The module uses specialized handlers (`SpecialFileHandler`, `SpecialDirectoryFileHandler`, etc.) to intercept writes to specific VSIX entries and apply Cascara-specific logic.

- **Strict JSON/XML Parsing**

  Uses Cascara’s own JSON and XML AST parsers to ensure schema correctness and avoid reliance on external libraries.

## Cascara VSIX Gradle Plugin

The **Cascara VSIX Gradle Plugin** provides a declarative, imperative-free domain DSL to package Visual Studio Code extensions (`.vsix` files) without needing custom script copying, regex manifest stamping, or manual Open Packaging Conventions (`[Content_Types].xml`) overhead.

It seamlessly integrates with `cascara-gradle-plugins.ct` to assemble color theme packages and manage local VS Code installation lifecycles.

### Features

- **Declarative DSL**

  Configure package metadata (`publisher`, `engines`, `repository`, etc.) directly inside Gradle.

- **In-Memory Manifest Generation**

  Automatically manages `package.json`, `extension.vsixmanifest`, and `[Content_Types].xml`.

- **Dynamic Theme Indexing**

  Scans compiled themes and injects contributions into `package.json` with correct UI metadata (`vs-light` / `vs-dark`).

- **VS Code Integration**

  Built-in tasks to install, uninstall, and reinstall the generated VSIX directly into Visual Studio Code.

### Quick Start

#### 1. Apply Plugins

Add the plugin alongside `cascara-gradle-plugins.ct` in your `build.gradle`:

```groovy
plugins {
    id 'base'
    id 'io.github.qishr.cascara-gradle-plugins.vsix' version '0.2.3'
}
```

#### 2. Configure `vsix` Extension

Define metadata and source resources inside the `vsix` extension block:

```groovy
vsix {
    name        = "cascara-retro-theme"
    displayName = "Cascara Retro Theme"
    description = "Retro theme pack for Visual Studio Code"
    version     = project.version.toString()
    publisher   = "Cascara"
    icon        = "images/icon.png"

    categories "Themes"

    engines = [
        "vscode": "^1.103.0"
    ]

    repository = [
        "type": "git",
        "url" : "git+https://github.com/sandydunlop/cascara-retro-theme.git"
    ]

    readme      = layout.projectDirectory.file("README.md")
    license     = layout.projectDirectory.file("LICENSE")
    changeLog   = layout.projectDirectory.file("CHANGELOG.md")
    imagesDir   = layout.projectDirectory.dir("src/main/resources/vscode/images")

    themes.from(layout.buildDirectory.dir("themes"))
}
```

### Configuration Reference

| **Property**  | **Type**                     | **Description**                                                   |
| ------------- | ---------------------------- | ----------------------------------------------------------------- |
| `name`        | `String`                     | Technical extension ID in `package.json` / VSIX identity.         |
| `displayName` | `String`                     | Display name shown in the VS Code Marketplace and Extensions tab. |
| `description` | `String`                     | Short summary of the extension.                                   |
| `version`     | `String`                     | Extension semantic version (e.g., `project.version.toString()`).  |
| `publisher`   | `String`                     | Publisher identifier.                                             |
| `icon`        | `String`                     | Relative path to extension icon image inside the package.         |
| `categories`  | `List<String>`               | Marketplace categories (e.g., `categories "Themes"`).             |
| `engines`     | `Map<String, String>`        | Compatibility engine mapping (e.g., `["vscode": "^1.103.0"]`).    |
| `repository`  | `Map<String, String>`        | Repository info (`type`, `url`).                                  |
| `readme`      | `RegularFile`                | Path to `README.md`.                                              |
| `license`     | `RegularFile`                | Path to `LICENSE`.                                                |
| `changeLog`   | `RegularFile`                | Path to `CHANGELOG.md`.                                           |
| `imagesDir`   | `Directory`                  | Source directory for icon and screenshot assets.                  |
| `themes`      | `ConfigurableFileCollection` | Directories or JSON theme files to include in the package.        |

## Tasks

The plugin registers the following tasks under the `vscode` task group:

### `packageVsix`

Builds the `.vsix` archive and places it in `build/distributions/<name>-<version>.vsix`.

```bash
./gradlew packageVsix
```

#### `installVsix`

Installs the generated `.vsix` file into the local VS Code installation using `code --install-extension <path> --force`.

```bash
./gradlew installVsix
```

#### `uninstallVsix`

Uninstalls the extension from local VS Code using `code --uninstall-extension <publisher>.<name>`.

```bash
./gradlew uninstallVsix
```

#### `reinstallVsix`

Performs an `uninstallVsix` followed by an `installVsix` operation.

```bash
./gradlew reinstallVsix
```

### License

[GPL 3](https://github.com/qishr/cascara-format-vsix/blob/main/LICENSE)

