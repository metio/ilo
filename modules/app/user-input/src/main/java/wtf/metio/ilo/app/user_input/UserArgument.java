package wtf.metio.ilo.app.user_input;

public class UserArgument<T> {

  public final String name;
  public final T defaultValue;
  public final String description;

  public UserArgument(final String name, final T defaultValue, final String description) {
    this.name = name;
    this.defaultValue = defaultValue;
    this.description = description;
  }

  public static <T> UserArgument<T> arg(final String name, final T defaultValue, final String description) {
    return new UserArgument<>(name, defaultValue, description);
  }

  @Override
  public String toString() {
    return "--" + name + "\t" + description + " Defaults to [" + defaultValue + "]";
  }
}
