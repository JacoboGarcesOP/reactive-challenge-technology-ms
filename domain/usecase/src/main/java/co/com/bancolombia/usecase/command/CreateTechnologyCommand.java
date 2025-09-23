package co.com.bancolombia.usecase.command;

public class CreateTechnologyCommand {
  private final String name;
  private final String description;

  public CreateTechnologyCommand(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}