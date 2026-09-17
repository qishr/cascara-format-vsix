package io.github.qishr.cascara.format.vsix;

import java.io.IOException;

import io.github.qishr.cascara.common.util.JreUtils;

public class ThemeTestBase extends ArchiveTestBase {
    protected String createSampleContentTypesXml() throws IOException {
        return JreUtils.getResourceAsString(ThemeTestBase.class, "/content-types.xml");
    }

    protected String createSampleManifestXml() throws IOException {
        return JreUtils.getResourceAsString(ThemeTestBase.class, "/manifest.xml");
    }

    protected String createSamplePackageJson() throws IOException {
        return JreUtils.getResourceAsString(ThemeTestBase.class, "/package-1.json");
    }

    protected String createSampleThemeJson() throws IOException {
        return JreUtils.getResourceAsString(ThemeTestBase.class, "/theme.json");
    }
}
