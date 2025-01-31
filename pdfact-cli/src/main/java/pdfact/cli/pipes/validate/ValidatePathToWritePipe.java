package pdfact.cli.pipes.validate;

import java.nio.file.Path;

import pdfact.core.util.pipeline.Pipe;

/**
 * A pipe to validate target paths for visualizations.
 * 
 * @author Claudius Korzen
 */
public interface ValidatePathToWritePipe extends Pipe {
  /**
   * Sets the path to validate.
   * 
   * @param path
   *        The path to validate.
   */
  void setPath(Path path);

  /**
   * Returns the path to validate.
   * 
   * @return The path to validate.
   */
  Path getPath();
}
