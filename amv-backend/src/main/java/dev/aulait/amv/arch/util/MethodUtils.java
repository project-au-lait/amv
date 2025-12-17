package dev.aulait.amv.arch.util;

import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodParamEntity;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodUtils {

  private static final int MAX_LINE_LENGTH = 50;

  public static String buildFormattedSignature(MethodEntity method) {
    String simple = buildSimpleSignature(method);

    return simple.length() <= MAX_LINE_LENGTH ? simple : buildMultilineSignature(method);
  }

  public static String buildSimpleSignature(MethodEntity method) {
    StringBuilder sb = new StringBuilder();

    sb.append(method.getName());
    sb.append("(");
    sb.append(buildSimpleParamSignature(method.getMethodParams()));
    sb.append(")");

    return sb.toString();
  }

  public static String buildMultilineSignature(MethodEntity method) {
    StringBuilder sb = new StringBuilder();

    sb.append(method.getName());
    sb.append("(");
    sb.append(buildMultilineParamSignature(method.getMethodParams()));
    sb.append(")");

    return sb.toString();
  }

  private static String buildSimpleParamSignature(Set<MethodParamEntity> params) {
    return params.stream()
        .sorted(Comparator.comparingInt(param -> param.getId().getSeqNo()))
        .map(MethodUtils::buildSimpleParamSignature)
        .collect(Collectors.joining(", "));
  }

  private static String buildMultilineParamSignature(Set<MethodParamEntity> params) {
    return params.stream()
        .sorted(Comparator.comparingInt(param -> param.getId().getSeqNo()))
        .map(MethodUtils::buildSimpleParamSignature)
        .collect(Collectors.joining(",\\n    ", "\\n    ", "\\n"));
  }

  private static String buildSimpleParamSignature(MethodParamEntity param) {
    return SyntaxUtils.toSimpleType(param.getType()) + " " + param.getName();
  }
}
