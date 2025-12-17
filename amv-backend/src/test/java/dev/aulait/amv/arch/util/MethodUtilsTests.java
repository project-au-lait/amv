package dev.aulait.amv.arch.util;

import static org.junit.jupiter.api.Assertions.*;

import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodParamEntity;
import dev.aulait.amv.domain.process.MethodParamEntityId;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MethodUtilsTests {

  @Test
  void testBuildSimpleSignature() {
    MethodEntity method = method("shortMethod", param("int", "x", 1), param("String", "str", 2));

    String signature = MethodUtils.buildSimpleSignature(method);
    assertEquals("shortMethod(int x, String str)", signature);
  }

  @Test
  void testBuildFormattedSignatureForShort() {
    MethodEntity method = method("shortMethod", param("int", "x", 1), param("String", "str", 2));

    String signature = MethodUtils.buildFormattedSignature(method);
    assertEquals("shortMethod(int x, String str)", signature);
  }

  @Test
  void testBuildFormattedSignatureForLong() {
    MethodEntity method =
        method(
            "longMethod",
            param("int", "x", 1),
            param("String", "str", 2),
            param("Map<String, List<Integer>>", "dataMap", 3));

    String expected =
        "longMethod(\\n    int x,\\n    String str,\\n    Map<String, List<Integer>> dataMap\\n)";

    String signature = MethodUtils.buildFormattedSignature(method);
    assertEquals(expected, signature);
  }

  private static MethodEntity method(String name, MethodParamEntity... params) {
    MethodEntity method = new MethodEntity();
    method.setName(name);
    method.setMethodParams(Set.of(params));
    return method;
  }

  private static MethodParamEntity param(String type, String name, int seqNo) {
    MethodParamEntity param = new MethodParamEntity();
    param.setId(new MethodParamEntityId("dummyType", 1, seqNo));
    param.setType(type);
    param.setName(name);
    return param;
  }
}
