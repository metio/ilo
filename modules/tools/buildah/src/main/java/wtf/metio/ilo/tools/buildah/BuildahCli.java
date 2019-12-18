package wtf.metio.ilo.tools.buildah;

import wtf.metio.ilo.tools.api.CliTool;

/**
 * Represents the buildah CLI and its commands.
 */
public interface BuildahCli extends CliTool {

    @Override
    default String name() {
        return "buildah";
    }

}
