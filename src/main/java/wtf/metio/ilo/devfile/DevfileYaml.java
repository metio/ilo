/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at https://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

package wtf.metio.ilo.devfile;

import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://devfile.io/docs/2.2.0/devfile-schema">devfile schema v2.2.0</a>
 */
final class DevfileYaml {

  static final class Metadata {

    /**
     * The name of your devfile. This name links you to the devfile registry if listed.
     */
    public String name;

    /**
     * The version of your devfile.
     */
    public String version;

  }

  static final class Component {

    /**
     * Mandatory name that allows referencing the component from other elements (such as commands) or from an external
     * devfile that may reference this component through a parent or a plugin.
     */
    public String name;

    /**
     * Allows adding and configuring devworkspace-related containers
     */
    public Container container;

    /**
     * Allows specifying the definition of an image for outer loop builds
     */
    public Image image;

    /**
     * Allows specifying the definition of a volume shared by several other components
     */
    public Volume volume;

  }

  static final class Container {

    public String image;
    public String memoryRequest;
    public String memoryLimit;
    public String cpuRequest;
    public String cpuLimit;

    /**
     * Specify if a container should run in its own separated pod, instead of running as part of the main development
     * environment pod.
     */
    public boolean dedicatedPod;

    /**
     * Toggles whether the project source code should be mounted in the component.
     */
    public boolean mountSources;

    /**
     * Optional specification of the path in the container where project sources should be transferred/mounted when
     * mountSources is true. When omitted, the default value of /projects is used.
     */
    public String sourceMapping;

    /**
     * The command to run in the dockerimage component instead of the default one provided in the image.
     */
    public List<String> command;

    /**
     * The arguments to supply to the command running the docker image component. The arguments are supplied either to
     * the default command provided in the image or to the overridden command.
     */
    public List<String> args;

    /**
     * Environment variables used in this container.
     */
    public List<Env> env;

    public List<Endpoint> endpoints;

    public List<VolumeMount> volumeMounts;

  }

  static final class Endpoint {

    /**
     * Describes how the endpoint should be exposed on the network.
     */
    public String exposure;

    public String name;

    /**
     * Path of the endpoint URL
     */
    public String path;

    /**
     * Describes the application and transport protocols of the traffic that will go through this endpoint.
     */
    public String protocol;

    /**
     * Describes whether the endpoint should be secured and protected by some authentication process. This requires a
     * protocol of 'https' or 'wss'.
     */
    public boolean secure;

    /**
     * Port number to be used within the container component. The same port cannot be used by two different container
     * components.
     */
    public int targetPort;

  }

  static final class Env {

    public String name;
    public String value;

  }

  static final class VolumeMount {

    /**
     * Name of the volume.
     */
    public String name;

    /**
     * Container path for the volume.
     */
    public String path;

  }

  static final class Image {

    /**
     * Defines if the image should be built during startup.
     */
    public boolean autoBuild;

    /**
     * Name of the image for the resulting outerloop build
     */
    public String imageName;

    /**
     * Allows specifying dockerfile type build
     */
    public Dockerfile dockerfile;

  }

  static final class Dockerfile {

    /**
     * The arguments to supply to the dockerfile build.
     */
    public List<String> args;

    /**
     * Path of source directory to establish build context. Defaults to ${PROJECT_SOURCE} in the container
     */
    public String buildContext;

    /**
     * Specify if a privileged builder pod is required.
     */
    public boolean rootRequired;

    /**
     * URI Reference of a Dockerfile. It can be a full URL or a relative URI from the current devfile as the base URI.
     */
    public String uri;

    /**
     * Dockerfile's Devfile Registry source.
     */
    public DevfileRegistry devfileRegistry;

    /**
     * Dockerfile's Git source.
     */
    public Git git;

  }

  static final class DevfileRegistry {

    /**
     * ID in a devfile registry that contains a Dockerfile. The src in the OCI registry required for the Dockerfile
     * build will be downloaded for building the image.
     */
    public String id;

    /**
     * Devfile Registry URL to pull the Dockerfile from when using the Devfile Registry as Dockerfile src.
     */
    public String registryUrl;

  }

  static final class Git {

    /**
     * Location of the Dockerfile in the Git repository when using git as Dockerfile src. Defaults to Dockerfile.
     */
    public String fileLocation;

    /**
     * The remotes map which should be initialized in the git project. Projects must have at least one remote configured
     * while StarterProjects & Image Component's Git source can only have at most one remote configured.
     */
    public Map<String, String> remotes;

    /**
     * Defines from what the project should be checked out. Required if there are more than one remote configured.
     */
    public CheckoutFrom checkoutFrom;

  }

  static final class CheckoutFrom {

    /**
     * The remote name should be used as init. Required if there are more than one remote configured.
     */
    public String remote;

    /**
     * The revision to check out from. Should be branch name, tag or commit id. Default branch is used if missing or
     * specified revision is not found.
     */
    public String revision;

  }

  static final class Volume {

    /**
     * Ephemeral volumes are not stored persistently across restarts.
     */
    public boolean ephemeral;

    /**
     * Size of the volume.
     */
    public String size;

  }

  /**
   * Devfile schema version.
   */
  public String schemaVersion;

  /**
   * Map of key-value variables used for string replacement in the devfile.
   */
  public Map<String, String> variables;

  /**
   * Optional metadata.
   */
  public Metadata metadata;

  /**
   * List of the devworkspace components, such as editor and plugins, user-provided containers, or other types of components.
   */
  public List<Component> components;

}
