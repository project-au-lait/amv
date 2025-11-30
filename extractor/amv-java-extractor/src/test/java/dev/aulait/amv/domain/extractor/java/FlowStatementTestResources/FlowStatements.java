package dev.aulait.amv.domain.extractor.java.FlowStatementTestResources;

public class FlowStatements {

  public void flowStatement() {
    if (true) {
      innerMethod();
    }
  }

  private void innerMethod() {
    System.out.println("flow statement");
  }
}
