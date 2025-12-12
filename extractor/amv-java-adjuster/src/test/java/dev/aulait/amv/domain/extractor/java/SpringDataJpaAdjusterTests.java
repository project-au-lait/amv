package dev.aulait.amv.domain.extractor.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.arch.test.ResourceUtils;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SpringDataJpaAdjusterTests {
  final String SAVE_ALL_QUALIFIED_SIG =
      "dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources.BookRepository.saveAll(java.lang.Iterable)";

  final String SAVE_QUALIFIED_SIG =
      "dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources.BookRepository.save(java.lang.Object)";

  final String SAVE_AND_FLUSH_QUALIFIED_SIG =
      "dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources.BookRepository.saveAndFlush(java.lang.Object)";

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void saveAllDeclarationTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookRepository.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodFdo saveAll =
        extractedSource.getTypes().get(0).getMethods().stream()
            .filter(method -> "saveAll".equals(method.getName()))
            .findFirst()
            .get();

    assertEquals(SAVE_ALL_QUALIFIED_SIG, saveAll.getQualifiedSignature());
  }

  @Test
  void saveAllReferenceTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookService.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodCallFdo saveAllCall =
        extractedSource.getTypes().get(0).getMethods().get(0).getMethodCalls().get(0);

    assertEquals(SAVE_ALL_QUALIFIED_SIG, saveAllCall.getQualifiedSignature());
  }

  @Test
  void saveDeclarationTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookRepository.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodFdo save =
        extractedSource.getTypes().get(0).getMethods().stream()
            .filter(method -> "save".equals(method.getName()))
            .findFirst()
            .get();

    assertEquals(SAVE_QUALIFIED_SIG, save.getQualifiedSignature());
  }

  @Test
  void saveReferenceTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookService.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodCallFdo saveCall =
        extractedSource.getTypes().get(0).getMethods().get(1).getMethodCalls().get(0);

    assertEquals(SAVE_QUALIFIED_SIG, saveCall.getQualifiedSignature());
  }

  @Test
  void saveAndFlushDeclarationTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookRepository.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodFdo saveAndFlush =
        extractedSource.getTypes().get(0).getMethods().stream()
            .filter(method -> "saveAndFlush".equals(method.getName()))
            .findFirst()
            .get();

    assertEquals(SAVE_AND_FLUSH_QUALIFIED_SIG, saveAndFlush.getQualifiedSignature());
  }

  @Test
  void saveAndFlushReferenceTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookService.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodCallFdo saveAndFlushCall =
        extractedSource.getTypes().get(0).getMethods().get(2).getMethodCalls().get(0);

    assertEquals(SAVE_AND_FLUSH_QUALIFIED_SIG, saveAndFlushCall.getQualifiedSignature());
  }
}
