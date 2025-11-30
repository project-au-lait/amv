package dev.aulait.amv.domain.extractor.fdo;


import lombok.Data;

@Data
public class FlowStatementFdo {
  /** 1: if statement, 2: switch statement, 3: for statement, 4: while statement, etc. */
  private String kind;

  private String content;

  private int lineNo;

  private FlowStatementFdo parent;
}
