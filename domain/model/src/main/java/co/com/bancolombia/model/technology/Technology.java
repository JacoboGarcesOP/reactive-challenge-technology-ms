package co.com.bancolombia.model.technology;

import co.com.bancolombia.model.technology.values.Description;
import co.com.bancolombia.model.technology.values.Id;
import co.com.bancolombia.model.technology.values.Name;

public class Technology {
  private Id id;
  private Name name;
  private Description description;

  public Technology(Long id, String name, String description) {
    this.id = new Id(id);
    this.name = new Name(name);
    this.description = new Description(description);
  }

  public Technology(String name, String description) {
    this.name = new Name(name);
    this.description = new Description(description);
  }

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public Description getDescription() {
    return description;
  }

  public void setDescription(Description description) {
    this.description = description;
  }
}
