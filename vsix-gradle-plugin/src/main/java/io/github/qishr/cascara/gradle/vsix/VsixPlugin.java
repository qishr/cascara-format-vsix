package io.github.qishr.cascara.gradle.vsix;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.nativeplatform.platform.OperatingSystem;
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;

public class VsixPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        VsixExtension extension = project.getExtensions().create("vsix", VsixExtension.class);

        TaskProvider<PackageVsixTask> packageVsix = project.getTasks().register("packageVsix", PackageVsixTask.class, task -> {
            String packageName = extension.getName().getOrElse(project.getName());

            task.setGroup("distribution");
            task.setDescription("Packages VS Code extension VSIX archive");

            // Wire Extension files -> Task inputs
            task.getReadme().set(extension.getReadme());
            task.getLicense().set(extension.getLicense());
            task.getChangeLog().set(extension.getChangeLog());
            task.getImagesDir().set(extension.getImagesDir());

            // Languages and Syntaxes/Grammars
            task.getGrammarFiles().from(extension.getGrammars());
            task.getLanguages().addAll(extension.getLanguages());

            // Collect explicitly added theme files or theme build directory
            task.getThemeFiles().from(extension.getThemes());

            // Bind structured manifest fields
            task.getManifestName().convention(packageName);
            task.getManifestVersion().convention(extension.getVersion().orElse(project.provider(() -> project.getVersion().toString())));
            task.getManifestDisplayName().set(extension.getDisplayName());
            task.getManifestDescription().set(extension.getDescription());
            task.getManifestId().set(extension.getId());
            task.getManifestPublisher().set(extension.getPublisher());
            task.getManifestIcon().set(extension.getIcon());
            task.getCategories().set(extension.getCategories());
            task.getEngines().set(extension.getEngines());
            task.getRepository().set(extension.getRepository());

            // Default output location
            task.getOutputFile().convention(
                project.getLayout().getBuildDirectory().file("distributions/" + packageName + ".vsix")
            );
        });


        // Task: uninstallVsix (Configuration Cache Safe)
        TaskProvider<UninstallVsixTask> uninstallVsix = project.getTasks().register("uninstallVsix", UninstallVsixTask.class, task -> {
            task.setGroup("vscode");
            task.setDescription("Uninstalls the extension from VS Code.");
            task.getPublisher().set(extension.getPublisher());
            task.getExtensionName().set(extension.getName().orElse(project.getName()));
        });

        // Task: installVsix (Configuration Cache Safe)
        TaskProvider<InstallVsixTask> installVsix = project.getTasks().register("installVsix", InstallVsixTask.class, task -> {
            task.setGroup("vscode");
            task.setDescription("Installs the generated VSIX package into VS Code.");
            task.getVsixFile().set(packageVsix.flatMap(PackageVsixTask::getOutputFile));
        });

        // Task: reinstallVsix
        project.getTasks().register("reinstallVsix", task -> {
            task.setGroup("vscode");
            task.setDescription("Uninstalls and then installs the generated VSIX package into VS Code.");
            task.dependsOn(uninstallVsix, installVsix);
        });

        installVsix.configure(task -> task.mustRunAfter(uninstallVsix));


        // // Use Gradle's public NativePlatform API
        // OperatingSystem os = DefaultNativePlatform.getCurrentOperatingSystem();
        // String codeExecutable = os.isWindows() ? "code.cmd" : "code";

        // // Task: uninstallVsix
        // TaskProvider<Exec> uninstallVsix = project.getTasks().register("uninstallVsix", Exec.class, task -> {
        //     task.setGroup("vscode");
        //     task.setDescription("Uninstalls the extension from VS Code.");

        //     task.doFirst(t -> {
        //         String publisher = extension.getPublisher().getOrElse("");
        //         // String packageName = extension.getName().getOrElse("");
        //         String packageName = extension.getName().getOrElse(project.getName());

        //         if (publisher.isBlank() || packageName.isBlank()) {
        //             throw new IllegalStateException("vsix.publisher and vsix.name must be configured to uninstall.");
        //         }

        //         String extensionId = publisher + "." + packageName;
        //         project.getLogger().lifecycle("Uninstalling extension from VS Code: " + extensionId);
        //         ((Exec) t).commandLine(codeExecutable, "--uninstall-extension", extensionId);
        //     });
        // });

        // // Task: installVsix
        // TaskProvider<Exec> installVsix = project.getTasks().register("installVsix", Exec.class, task -> {
        //     task.setGroup("vscode");
        //     task.setDescription("Installs the generated VSIX package into VS Code.");
        //     task.dependsOn(packageVsix);

        //     task.doFirst(t -> {
        //         var execTask = (Exec) t;
        //         execTask.environment("NODE_NO_WARNINGS", "1");

        //         var vsixFile = packageVsix.get().getOutputFile().get().getAsFile();
        //         project.getLogger().lifecycle("Installing extension into VS Code: " + vsixFile.getName());

        //         execTask.commandLine(codeExecutable, "--install-extension", vsixFile.getAbsolutePath(), "--force");
        //     });
        // });

        // // Task: reinstallVsix
        // project.getTasks().register("reinstallVsix", task -> {
        //     task.setGroup("vscode");
        //     task.setDescription("Uninstalls and then installs the generated VSIX package into VS Code.");
        //     task.dependsOn(uninstallVsix, installVsix);
        // });

        // // Ensure uninstall executes before install when running reinstallVsix
        // installVsix.configure(task -> task.mustRunAfter(uninstallVsix));
    }
}