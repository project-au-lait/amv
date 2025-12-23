package dev.aulait.amv.arch.file;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Manages application directories. The directory structure is as follows:
 *
 * <pre>
 * - amv-home
 *   - codebase
 *     - {codebaseName}
 *   - extraction
 *     - {codebaseName}_{commitHash}
 *   - h2db  (actually this is not managed here, but created by H2 database engine)
 *     - amv.h2.db
 *     - amv.trace.db
 * </pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectoryManager {

  private static final boolean APP_IN_CONTAINER = System.getenv().containsKey("APP_IN_CONTAINER");

  public static final Path MOUNT_DIR_HOST =
      Path.of("./target/amv-mnt").toAbsolutePath().normalize();

  public static final Path MOUNT_DIR_CONTAINER = Path.of("/mnt/amv").toAbsolutePath().normalize();

  public static final Path MOUNT_DIR = APP_IN_CONTAINER ? MOUNT_DIR_CONTAINER : MOUNT_DIR_HOST;

  public static final Path AMV_HOME_HOST =
      Path.of("./target/amv-home").toAbsolutePath().normalize();

  public static final Path AMV_HOME_CONTAINER = Path.of("./amv").toAbsolutePath().normalize();

  public static final Path AMV_HOME = APP_IN_CONTAINER ? AMV_HOME_CONTAINER : AMV_HOME_HOST;

  public static final Path CODEBASE_ROOT = AMV_HOME.resolve("codebase");

  public static final Path EXTRACTION_ROOT = AMV_HOME.resolve("extraction");

  public static final Path SECURITY_ROOT = AMV_HOME.resolve("security");

  static {
    FileUtils.createDir(MOUNT_DIR);
    FileUtils.createDir(AMV_HOME);
    FileUtils.createDir(CODEBASE_ROOT);
    FileUtils.createDir(EXTRACTION_ROOT);
    FileUtils.createDir(SECURITY_ROOT);
  }

  public static Path createExtractionDir(String codebaseName, String hash) {
    String dirName = codebaseName + "_" + hash;
    return FileUtils.createDir(EXTRACTION_ROOT, dirName);
  }

  public static Path getExtractionDir(String codebaseName, String hash) {
    String dirName = codebaseName + "_" + hash;
    return FileUtils.collectMatchedDirs(EXTRACTION_ROOT, dirName).findFirst().orElseThrow();
  }

  public static void deleteExtractionDirs(String codebaseName) {
    String dirGlob = codebaseName + "_*";
    FileUtils.collectMatchedDirs(EXTRACTION_ROOT, dirGlob).forEach(FileUtils::deleteRecursively);
  }

  public static void deleteCodebaseDir(String codebaseName) {
    FileUtils.collectMatchedDirs(CODEBASE_ROOT, codebaseName).forEach(FileUtils::deleteRecursively);
  }
}
