package icecite.models;

import java.util.List;

/**
 * A page in a PDF document.
 * 
 * @author Claudius Korzen
 */
public interface PdfPage extends HasCharacters {
  /**
   * Returns the figures in this page.
   * 
   * @return The list of figures in this page.
   */
  List<PdfFigure> getFigures();

  /**
   * Sets the figures of this page.
   * 
   * @param figures
   *        The list of figures to set.
   */
  void setFigures(List<PdfFigure> figures);

  /**
   * Adds the given figure to this page.
   * 
   * @param figure
   *        The figure to add.
   */
  void addFigure(PdfFigure figure);

  // ==========================================================================

  /**
   * Returns the shapes in this page.
   * 
   * @return The list of shapes in this page.
   */
  List<PdfShape> getShapes();

  /**
   * Sets the shapes of this page.
   * 
   * @param shapes
   *        The list of shapes to set.
   */
  void setShapes(List<PdfShape> shapes);

  /**
   * Adds the given shape to this page.
   * 
   * @param shape
   *        The shape to add.
   */
  void addShape(PdfShape shape);

  // ==========================================================================

  /**
   * Returns the number of this page in the PDF document.
   * 
   * @return The page number.
   */
  int getPageNumber();

  /**
   * Sets the number of this page in the PDF document.
   * 
   * @param pageNumber
   *        The page number.
   */
  void setPageNumber(int pageNumber);

  // ==========================================================================

  /**
   * Returns the identified text blocks in this page.
   * 
   * @return The list of text blocks in this page.
   */
  List<PdfTextBlock> getTextBlocks();

  /**
   * Sets the text blocks of this page.
   * 
   * @param blocks
   *        The list of text blocks to set.
   */
  void setTextBlocks(List<PdfTextBlock> blocks);

  /**
   * Adds the given text block to this page.
   * 
   * @param block
   *        The text block to add.
   */
  void addTextBlock(PdfTextBlock block);

  // ==========================================================================

  /**
   * Returns the identified paragraphs in this page.
   * 
   * @return The list of paragraphs in this page.
   */
  List<PdfParagraph> getParagraphs();

  /**
   * Sets the paragraphs of this page.
   * 
   * @param paragraphs
   *        The list of paragraphs to set.
   */
  void setParagraphs(List<PdfParagraph> paragraphs);

  /**
   * Adds the given paragraph to this page.
   * 
   * @param paragraph
   *        The paragraph to add.
   */
  void addParagraph(PdfParagraph paragraph);

  // ==========================================================================

  /**
   * The factory to creates instances of {@link PdfPage}.
   * 
   * @author Claudius Korzen
   */
  public interface PdfPageFactory {
    /**
     * Creates a new PDF page.
     * 
     * @return An instance of {@link PdfPage}.
     */
    PdfPage create();

    /**
     * Creates a new PDF page.
     * 
     * @param pageNum
     *        The number of the page in the PDF document.
     * 
     * @return An instance of {@link PdfPage}.
     */
    PdfPage create(int pageNum);
  }
}
