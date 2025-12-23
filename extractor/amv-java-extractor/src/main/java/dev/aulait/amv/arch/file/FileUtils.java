package dev.aulait.amv.arch.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class FileUtils {

  public static Path createDir(Path parentDir, String dirName) {
    return createDir(parentDir.resolve(dirName));
  }

  public static Path createDir(Path dir) {
    if (!Files.exists(dir)) {
      log.info("Creating directory: {}", dir.toAbsolutePath().normalize());

      try {
        Files.createDirectories(dir);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    return dir;
  }

  public static Path createTempFile(Path dir, String prefix, String suffix) {
    try {
      Path tempFile = Files.createTempFile(dir, prefix, suffix);
      log.debug("Created temporary file: {}", tempFile.toAbsolutePath().normalize());
      return tempFile;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String read(Path file) {
    try {
      Path fileAbs = file.toAbsolutePath().normalize();
      log.debug("Reading from {}", fileAbs);
      return Files.readString(fileAbs);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path write(Path file, String text) {
    try {

      Path fileAbs = file.toAbsolutePath().normalize();

      Path dir = fileAbs.getParent();
      if (!Files.exists(dir)) {
        log.info("Creating directory {}", dir);
        Files.createDirectories(dir);
      }

      log.debug("Writing to {}", fileAbs);
      return Files.writeString(fileAbs, text);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path copy(Path source, Path target) {
    try {
      Path sourceAbs = source.toAbsolutePath().normalize();
      Path targetAbs = target.toAbsolutePath().normalize();

      Path targetDir = targetAbs.getParent();
      if (!Files.exists(targetDir)) {
        log.info("Creating directory {}", targetDir);
        Files.createDirectories(targetDir);
      }

      log.debug("Copying from {} to {}", sourceAbs, targetAbs);
      return Files.copy(sourceAbs, targetAbs, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Stream<Path> collectMatchedPaths(Path sourceDir, String globPattern) {
    if (!Files.exists(sourceDir)) {
      return Stream.empty();
    }

    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

    try (Stream<Path> paths = Files.walk(sourceDir)) {
      return paths
          .filter(path -> matcher.matches(sourceDir.relativize(path)))
          .collect(Collectors.toList())
          .stream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Stream<Path> collectMatchedDirs(Path sourceDir, String globPattern) {
    if (!Files.exists(sourceDir)) {
      return Stream.empty();
    }

    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

    try (Stream<Path> children = Files.list(sourceDir)) {
      return children
          .filter(path -> matcher.matches(path.getFileName()))
          .filter(Files::isDirectory)
          .collect(Collectors.toList())
          .stream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void delete(Path path) {
    try {
      if (Files.exists(path)) {
        log.info("Deleting: {}", path.toAbsolutePath().normalize());
        Files.delete(path);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void deleteRecursively(Path root) {
    try (Stream<Path> walk = Files.walk(root)) {
      walk.sorted(Comparator.comparingInt(Path::getNameCount).reversed())
          .forEach(FileUtils::delete);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
