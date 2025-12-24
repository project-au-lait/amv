package dev.aulait.amv.interfaces.diagram;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class DiagramCriteriaDto {
  @Schema(required = true)
  private String qualifiedSignature;

  @Schema(required = true)
  private int depth;
}
