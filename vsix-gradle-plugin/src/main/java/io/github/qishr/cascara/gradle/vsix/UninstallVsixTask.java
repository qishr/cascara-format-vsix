package io.github.qishr.cascara.gradle.vsix;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;

public abstract class UninstallVsixTask extends DefaultTask {

    @Input
    public abstract Property<String> getPublisher();

    @Input
    public abstract Property<String> getExtensionName();

    @Inject
    protected abstract ExecOperations getExecOperations();

    @TaskAction
    public void uninstall() {
        String publisherStr = getPublisher().getOrElse("");
        String nameStr = getExtensionName().getOrElse("");

        if (publisherStr.isBlank() || nameStr.isBlank()) {
            throw new IllegalStateException("vsix.publisher and vsix.name must be configured to uninstall.");
        }

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String codeExecutable = isWindows ? "code.cmd" : "code";
        String extensionId = publisherStr + "." + nameStr;

        getLogger().lifecycle("Uninstalling extension from VS Code: " + extensionId);

        getExecOperations().exec(execSpec -> {
            execSpec.commandLine(codeExecutable, "--uninstall-extension", extensionId);
        });
    }
}