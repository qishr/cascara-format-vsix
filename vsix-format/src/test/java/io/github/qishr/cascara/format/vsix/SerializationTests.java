package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.StandardReporter;
import io.github.qishr.cascara.common.util.JreUtils;
import io.github.qishr.cascara.lang.json.processor.JsonSerializer;
import io.github.qishr.cascara.lang.json.util.JsonOptions;

public class SerializationTests extends ArchiveTestBase {
    @Test
    void test() throws IOException {
        String json = JreUtils.getResourceAsString(SerializationTests.class, "/package-1.json");
        JsonSerializer serializer = new JsonSerializer();
        serializer.setReporter(new StandardReporter());
        serializer.setOptions(new JsonOptions().setTrackPosition(true));
        PackageJsonFile pkgJsonFile = serializer.fromString(json, PackageJsonFile.class);

        List<Contribution> themeContibs = pkgJsonFile.getContributions().get(CONTRIBUTES_THEMES);
        assertNotNull(themeContibs);
    }
}
