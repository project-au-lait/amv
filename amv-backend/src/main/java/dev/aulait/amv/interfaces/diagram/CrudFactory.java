package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.domain.diagram.CrudElementVo;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.interfaces.process.MethodDto;
import dev.aulait.amv.interfaces.process.MethodFactory;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
@RequiredArgsConstructor
public class CrudFactory {

  private final MethodFactory methodFactory;

  private final Comparator<MethodEntity> methodComparator =
      Comparator.nullsLast(Comparator.comparing(this::buildSortKey));

  public CrudDto build(List<CrudElementVo> elements, Map<String, String> typeId2url) {
    CrudDto crudDto = new CrudDto();

    elements.stream()
        .sorted(Comparator.comparing(CrudElementVo::getMethod, methodComparator))
        .forEach(
            e -> {
              MethodDto methodDto = null;
              if (e.getMethod() != null) {
                methodDto = methodFactory.build(e.getMethod());
                methodDto.setSrcUrl(
                    // typeId2url.get(e.getMethod().getTypeId()) + "#L" +
                    // e.getMethod().getDecLineNo());
                    typeId2url.get(e.getMethod().getId().getTypeId())
                        + "#L"
                        + e.getMethod().getLineNo());
              }
              crudDto.add(e, methodDto);
            });

    return crudDto;
  }

  private String buildSortKey(MethodEntity method) {
    if (method == null || method.getQualifiedSignature() == null) {
      return null;
    }
    String qualifiedMethodName = StringUtils.substringBefore(method.getQualifiedSignature(), "(");

    int lastDot = qualifiedMethodName.lastIndexOf('.');
    int prevDot = qualifiedMethodName.lastIndexOf('.', lastDot - 1);
    return qualifiedMethodName.substring(prevDot + 1);
  }
}
