package io.github.qishr.cascara.format.vsix;

import java.util.ArrayList;
import java.util.List;

public final class LanguageContribution extends Contribution {
    String id;
    List<String> aliases = new ArrayList<>();
    List<String> extensions = new ArrayList<>();
    String configuration;

    public String getId() {
        return id;
    }

    public LanguageContribution setId(String s) {
        id = s;
        return this;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public LanguageContribution setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public LanguageContribution setExtensions(List<String> extensions) {
        this.extensions = extensions;
        return this;
    }

    public String getConfiguration() {
        return configuration;
    }

    public LanguageContribution setConfiguration(String s) {
        configuration = s;
        return this;
    }
}
