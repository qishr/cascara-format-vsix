package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.io.TempDir;

import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.diagnostic.StandardReporter;
import io.github.qishr.cascara.common.util.ArchiveFile.EntryInfo;

public abstract class ArchiveTestBase {
    protected StandardReporter reporter = new StandardReporter().setLevel(Level.DEBUG);

    protected static final String LICENSE_FILE = "LICENSE";
    protected static final String README_FILE = "README.md";

    protected static final String DIR_EXTENSION = "extension/";
    protected static final String DIR_IMAGES = DIR_EXTENSION + "images/";
    protected static final String DIR_THEMES = DIR_EXTENSION + "themes/";

    protected static final String FILE_CONTENT_TYPES = "[Content_Types].xml";
    protected static final String FILE_MANIFEST_XML = "extension.vsixmanifest";
    protected static final String FILE_PACKAGE_JSON = "package.json";

    protected static final String ENTRY_CONTENT_TYPES = FILE_CONTENT_TYPES;
    protected static final String ENTRY_MANIFEST_XML = FILE_MANIFEST_XML;
    protected static final String ENTRY_PACKAGE_JSON = DIR_EXTENSION + FILE_PACKAGE_JSON;
    protected static final String ENTRY_CHANGELOG = DIR_EXTENSION + "CHANGELOG.md";
    protected static final String ENTRY_LICENSE = DIR_EXTENSION + "LICENSE.md";
    protected static final String ENTRY_README = DIR_EXTENSION + "README.md";

    protected static final String CATEGORIES_LANGUAGES = "Programming Languages";
    protected static final String CATEGORIES_THEMES = "Themes";

    protected static final String CONTRIBUTES_GRAMMARS = "grammars";
    protected static final String CONTRIBUTES_LANGUAGES = "languages";
    protected static final String CONTRIBUTES_THEMES = "themes";

    @TempDir
    protected Path tempDir;

    protected void assertContainsFile(String fileName, List<EntryInfo> files, Path pkgPath) throws IOException {
        for (EntryInfo info : files) {
            if (info.getPath().equals(fileName)) {
                return;
            }
        }
        showContents(pkgPath);
        assertTrue(false, "File missing: " + fileName);
    }

    protected void showContents(Path zipFile) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(
                Files.newInputStream(zipFile))) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }

    protected void writeFile(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }

}
