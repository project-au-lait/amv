package dev.aulait.amv.domain.diagram;

import java.util.Map;
import lombok.Data;

@Data
public class FlowStatementAggregate implements SequenceDiagramElement {
  String kind;
  String content;

  private static final Map<String, String> KIND_TO_PUML =
      Map.of(
          "1", "alt",
          "2", "alt switch",
          "3", "loop",
          "4", "loop");

  @Override
  public String toString() {
    if (kind == null) {
      return "end";
    }

    String keyword = KIND_TO_PUML.get(kind);
    if (keyword == null) {
      return "";
    }

    return keyword + " " + content;
  }
}
