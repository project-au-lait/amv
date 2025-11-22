package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.util.GitUtils;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CodebaseLogic {

  public Path dir(CodebaseEntity codebase) {
    return GitUtils.extractRootDir(DirectoryManager.CODEBASE_ROOT, codebase.getUrl());
  }

  public Path resolve(CodebaseEntity codebase, String relativePath) {
    return dir(codebase).resolve(relativePath);
  }

  public boolean exists(CodebaseEntity codebase) {
    return dir(codebase).toFile().exists();
  }

  public List<CodebaseAggregate> aggregate(List<ProjectEntity> projects) {
    Map<CodebaseEntity, List<ProjectEntity>> projectsByCodebase =
        projects.stream()
            .filter(project -> project.getCodebase() != null)
            .collect(Collectors.groupingBy(ProjectEntity::getCodebase));

    return projectsByCodebase.entrySet().stream()
        .map(
            entry ->
                CodebaseAggregate.builder()
                    .codebase(entry.getKey())
                    .projects(entry.getValue())
                    .build())
        .toList();
  }
}
