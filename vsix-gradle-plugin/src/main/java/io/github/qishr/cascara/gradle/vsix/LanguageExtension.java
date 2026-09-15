package io.github.qishr.cascara.gradle.vsix;

import org.gradle.api.Named;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import javax.inject.Inject;

public abstract class LanguageExtension implements Named {

    private final String name;

    @Input public abstract Property<String> getId();
    @Input public abstract Property<String> getConfiguration();
    @Input public abstract ListProperty<String> getAliases();

    // Renamed from getExtensions() to avoid method collisions with Gradle's ExtensionAware/extensions
    @Input public abstract ListProperty<String> getFileExtensions();

    @Inject
    public LanguageExtension(String name, ObjectFactory objects) {
        this.name = name;
        getId().convention(name);
        getAliases().convention(objects.listProperty(String.class));
        getFileExtensions().convention(objects.listProperty(String.class));

        // TODO: Default values...
        // getConfiguration().convention("./language-configuration.json");
    }

    @Override
    @Input
    public String getName() {
        return name;
    }
}
