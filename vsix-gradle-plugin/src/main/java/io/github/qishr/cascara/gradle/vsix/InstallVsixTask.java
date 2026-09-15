package io.github.qishr.cascara.gradle.vsix;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;

public abstract class InstallVsixTask extends DefaultTask {

    @InputFile
    public abstract RegularFileProperty getVsixFile();

    @Inject
    protected abstract ExecOperations getExecOperations();

    @TaskAction
    public void install() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String codeExecutable = isWindows ? "code.cmd" : "code";
        String vsixPath = getVsixFile().get().getAsFile().getAbsolutePath();

        getLogger().lifecycle("Installing extension into VS Code: " + getVsixFile().get().getAsFile().getName());

        getExecOperations().exec(execSpec -> {
            execSpec.environment("NODE_NO_WARNINGS", "1");
            execSpec.commandLine(codeExecutable, "--install-extension", vsixPath, "--force");
        });
    }
}