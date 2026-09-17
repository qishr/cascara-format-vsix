package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.diagnostic.StandardReporter;
import io.github.qishr.cascara.common.util.ArchiveFile;

public class ThemeTests extends ThemeTestBase {

    @Disabled("Method isn't implemented yet")
    @Test
    void testAddThemeFromString() throws IOException {
        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);

        String themeJson = """
              {
                "name": "Test Theme",
                "type": "dark",
                "semanticHighlighting": true
              }
            """;

        pkg.addFile(themeJson, DIR_THEMES + "test.json");

        assertFalse(pkg.getThemes().isEmpty());

        ThemeContribution theme = pkg.getThemes().getFirst();

        assertEquals("Test Theme", theme.getName());
        assertEquals("dark", theme.getType());
        assertEquals(true, theme.getSemanticHighlighting());
    }

    @Test
    void testAddThemeFileFile() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir.resolve("themes"));

        Path pkgPath = tempDir.resolve("package.vsix");

        String fileName = "test.json";
        Path filePath = sourceDir.resolve(fileName);
        String themeJson = """
              {
                "name": "Test Theme",
                "type": "dark",
                "semanticHighlighting": true
              }
            """;
        Files.writeString(
            filePath, themeJson, StandardCharsets.UTF_8
        );

        ThemeContribution theme = null;
        try (VsixPackage pkg = VsixPackage.create(pkgPath)) {
            pkg.setReporter(new StandardReporter().setLevel(Level.DEBUG));
            pkg.addFile(filePath, DIR_THEMES + fileName);
            // assertFalse(pkg.getEngines().isEmpty());
            assertFalse(pkg.getThemes().isEmpty());

            theme = pkg.getThemes().getFirst();
            assertNotNull(theme);
            assertEquals("Test Theme", theme.getName());
            assertEquals("dark", theme.getType());
            assertEquals(true, theme.getSemanticHighlighting());
        }

        // Re-open it and check again...
        try (VsixPackage pkg = VsixPackage.open(pkgPath)) {
            pkg.setReporter(reporter);
            // assertFalse(pkg.getEngines().isEmpty());

            theme = pkg.getThemes().getFirst();
            assertNotNull(theme);
            assertEquals("Test Theme", theme.getName());
            assertEquals("dark", theme.getType());
            assertEquals(true, theme.getSemanticHighlighting());
        }
    }

    @Test
    void test_openExistingVsix() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir.resolve("themes"));

        Path pkgPath = tempDir.resolve("package.vsix");
        try (ArchiveFile archive = ArchiveFile.create(pkgPath)) {
            archive.addFile(createSampleContentTypesXml(), ENTRY_CONTENT_TYPES);
            archive.addFile(createSampleManifestXml(), ENTRY_MANIFEST_XML);
            archive.addFile(createSamplePackageJson(), ENTRY_PACKAGE_JSON);
            archive.addFile(createSampleThemeJson(), DIR_THEMES + "test-theme.json");
        }

        long initialTimestamp = pkgPath.toFile().lastModified();

        try (VsixPackage pkg = VsixPackage.open(pkgPath)) {
            ThemeContribution themeInfo = pkg.getThemes().getFirst();
            String themePath = themeInfo.getPath();
            assertEquals("extension/themes/test-theme.json", VsixPackage.normalizeEntry(DIR_EXTENSION, themePath));
        }

        // Check the VSIX file hasn't been modified
        long finalTimestamp = pkgPath.toFile().lastModified();
        assertEquals(initialTimestamp, finalTimestamp);
    }
}
