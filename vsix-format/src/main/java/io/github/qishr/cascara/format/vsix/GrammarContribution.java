package io.github.qishr.cascara.format.vsix;

import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.common.annotation.DataIgnore;

public class GrammarContribution extends Contribution {
    String language;
    String scopeName;
    // List<String> fileTypes = new ArrayList<>();
    String path;

    public String getLanguage() {
        return language;
    }

    public GrammarContribution setLanguage(String s) {
        language = s;
        return this;
    }

    public String getScopeName() {
        return scopeName;
    }

    public GrammarContribution setScopeName(String s) {
        scopeName = s;
        return this;
    }

    // public List<String> getFileTypes() {
    //     return fileTypes;
    // }

    // public GrammarContribution setFileTypes(List<String> fileTypes) {
    //     this.fileTypes = fileTypes;
    //     return this;
    // }

    public String getPath() {
        return path;
    }

    public GrammarContribution setPath(String s) {
        path = s;
        return this;
    }
}
