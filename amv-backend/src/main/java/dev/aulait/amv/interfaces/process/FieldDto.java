package dev.aulait.amv.interfaces.process;

import lombok.Data;

@Data
public class FieldDto implements Comparable<FieldDto> {
  private FieldDtoId id;
  private String name;
  private String type;

  @Override
  public int compareTo(FieldDto other) {
    return this.name.compareTo(other.name);
  }
}
