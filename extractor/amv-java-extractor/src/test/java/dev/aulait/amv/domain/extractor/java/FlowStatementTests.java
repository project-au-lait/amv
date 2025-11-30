package dev.aulait.amv.domain.extractor.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.arch.test.ResourceUtils;
import dev.aulait.amv.domain.extractor.fdo.FlowStatementFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FlowStatementTests {

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void test() {
    Path sourceFile = ResourceUtils.res2path(this, "FlowStatements.java");

    SourceFdo extractedSource = extractor.extract(sourceFile).get();

    MethodFdo method = extractedSource.getTypes().get(0).getMethods().get(0);

    FlowStatementFdo flowStatement = method.getMethodCalls().get(0).getFlowStatement();

    assertEquals("1", flowStatement.getKind());
    assertEquals("true", flowStatement.getContent());
    assertEquals(6, flowStatement.getLineNo());
  }
}
