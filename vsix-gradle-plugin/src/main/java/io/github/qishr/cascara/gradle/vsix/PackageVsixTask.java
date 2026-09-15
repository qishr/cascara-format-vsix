package io.github.qishr.cascara.gradle.vsix;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.format.vsix.VsixPackage;
import io.github.qishr.cascara.format.vsix.LanguageContribution;
import io.github.qishr.cascara.format.vsix.RepositoryInfo;

import org.gradle.api.DefaultTask;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.Zip;

import java.io.File;


import org.gradle.api.tasks.*;

import java.io.IOException;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;

import java.nio.file.Path;

public abstract class PackageVsixTask extends DefaultTask {

    // Metadata inputs
    @Input public abstract Property<String> getManifestName();
    @Input public abstract Property<String> getManifestVersion();
    @Input @Optional public abstract Property<String> getManifestId();
    @Input @Optional public abstract Property<String> getManifestDisplayName();
    @Input @Optional public abstract Property<String> getManifestDescription();
    @Input @Optional public abstract Property<String> getManifestPublisher();
    @Input @Optional public abstract Property<String> getManifestIcon();
    @Input @Optional public abstract ListProperty<String> getCategories();
    @Input @Optional public abstract MapProperty<String, String> getEngines();
    @Input @Optional public abstract MapProperty<String, String> getRepository();

    // File inputs
    @InputDirectory @Optional public abstract DirectoryProperty getImagesDir();
    @InputFile @Optional public abstract RegularFileProperty getReadme();
    @InputFile @Optional public abstract RegularFileProperty getLicense();
    @InputFile @Optional public abstract RegularFileProperty getChangeLog();
    @InputFile @Optional public abstract RegularFileProperty getLanguageConfiguration();

    @InputFiles public abstract ConfigurableFileCollection getGrammarFiles();
    @Nested public abstract NamedDomainObjectContainer<LanguageExtension> getLanguages();

    @InputFiles public abstract ConfigurableFileCollection getThemeFiles();

    @OutputFile public abstract RegularFileProperty getOutputFile();

    @SuppressWarnings("resource")
    @TaskAction
    public void buildPackage() throws Exception {
        File destination = getOutputFile().get().getAsFile();

        try (VsixPackage vsix = VsixPackage.create(destination.toPath())) {

            // Build package.json and vsixmanifest directly from task properties

            vsix.getIdentity()
                .setId(getManifestId().getOrNull())
                .setVersion(getManifestVersion().get())
                .setPublisher(getManifestPublisher().getOrNull());

            vsix.setName(getManifestName().get())
                .setVersion(getManifestVersion().get())
                .setDisplayName(getManifestDisplayName().getOrNull())
                .setDescription(getManifestDescription().getOrNull())
                .setPublisher(getManifestPublisher().getOrNull())
                .setIcon(getManifestIcon().getOrNull())
                .setCategories(getCategories().getOrElse(java.util.Collections.emptyList()))
                .setEngines(getEngines().getOrElse(java.util.Collections.emptyMap()))
                .setRepository(new RepositoryInfo()
                    .setType(getRepository().get().get("type"))
                    .setUrl(getRepository().get().get("url"))
                );
                // .setLanguageConfiguration(getLanguageConfiguration().get().getAsFile().toPath());

            if (getReadme().isPresent()) vsix.setReadme(getReadme().get().getAsFile().toPath());
            if (getLicense().isPresent()) vsix.setLicense(getLicense().get().getAsFile().toPath());
            if (getChangeLog().isPresent()) vsix.setChangeLog(getChangeLog().get().getAsFile().toPath());
            if (getImagesDir().isPresent()) vsix.addImagesFromDirectory(getImagesDir().get().getAsFile().toPath());

            for (LanguageExtension language : getLanguages()) {
                LanguageContribution contribution = new LanguageContribution()
                    .setId(language.getId().get())
                    .setAliases(language.getAliases().get())
                    .setExtensions(language.getFileExtensions().get())
                    .setConfiguration(language.getConfiguration().get());
                vsix.addContribution(contribution);
            }

            for (File syntaxFile : getGrammarFiles().getAsFileTree().getFiles()) {
                if (syntaxFile.isFile() && syntaxFile.getName().endsWith(".json")) {
                    vsix.addSyntaxFile(syntaxFile.toPath());
                }
            }

            for (File themeFile : getThemeFiles().getAsFileTree().getFiles()) {
                if (themeFile.isFile() && themeFile.getName().endsWith(".json")) {
                    vsix.addThemeFile(themeFile.toPath());
                }
            }
        }
    }
}
