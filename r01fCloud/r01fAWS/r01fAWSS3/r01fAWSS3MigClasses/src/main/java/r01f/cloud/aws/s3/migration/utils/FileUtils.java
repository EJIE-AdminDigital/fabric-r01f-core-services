package r01f.cloud.aws.s3.migration.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Utility class for file system operations.
 */
public class FileUtils {

    /**
     * Deletes a folder/file and all its contents (files and subfolders) recursively.
     * If the path does not exist or is not a directory, no action is performed.
     *
     * @param pathObj The {@link Path} of the folder/file to delete.
     * @throws IOException If an I/O error occurs during deletion (e.g., insufficient permissions,
     *                     file in use, etc.) that prevents the complete deletion.
     *                     This exception is thrown if any individual file/directory deletion fails.
     */
    public static void delete(Path pathObj) throws IOException {
        if (Files.exists(pathObj)) {
        	if(Files.isDirectory(pathObj)) {
	            try (Stream<Path> walk = Files.walk(pathObj)) {
	                walk.sorted(Comparator.reverseOrder()) // Reverse order to delete children first
	                    .forEach(path -> {
	                        try {
	                            Files.delete(path);
	                        } catch (IOException e) {
	                            throw new RuntimeException("Failed to delete " + path.toAbsolutePath() + ": " + e.getMessage(), e);
	                        }
	                    });
	            }
        	} else {
        		Files.delete(pathObj);
        	}
        }
    }
}
