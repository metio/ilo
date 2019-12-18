package wtf.metio.ilo.tools.kubectl;

import wtf.metio.ilo.tools.api.CliTool;

/**
 * Represents the docker CLI and its commands.
 */
public interface KubectlCli extends CliTool {

    @Override
    default String name() {
        return "kubectl";
    }

}
