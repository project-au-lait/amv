package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.util.GitUtils;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Setter;

@ApplicationScoped
public class CodebaseLogic {

  @Setter private Function<String, Boolean> statusJudge;
  @Setter private Function<CodebaseEntity, Boolean> pathJudge;

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
        .map(entry -> buildAggregate(entry.getKey(), entry.getValue()))
        .toList();
  }

  public CodebaseAggregate buildAggregate(CodebaseEntity codebase, List<ProjectEntity> projects) {
    return CodebaseAggregate.builder()
        .codebase(codebase)
        .projects(projects)
        .status(collectStatus(codebase))
        .build();
  }

  private CodebaseStatusVo collectStatus(CodebaseEntity codebase) {
    boolean metadataExtracted =
        Files.exists(
            DirectoryManager.getExtractionDir(codebase.getName(), codebase.getCommitHash())
                .resolve(".done"));

    return CodebaseStatusVo.builder()
        .analyzing(statusJudge.apply(codebase.getId()))
        .checkedOut(exists(codebase))
        .projectsLoaded(pathJudge.apply(codebase))
        .metadataExtracted(metadataExtracted)
        .build();
  }
}
