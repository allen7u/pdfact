package icecite.drawer.pdfbox;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import icecite.drawer.PdfDrawer;
import icecite.models.HasBoundingBox;
import icecite.utils.geometric.Line;
import icecite.utils.geometric.Point;
import icecite.utils.geometric.Rectangle;
import icecite.utils.geometric.plain.PlainLine;
import icecite.utils.geometric.plain.PlainPoint;
import icecite.utils.geometric.plain.PlainRectangle;

/**
 * An implementation of PdfVisualizer using PdfBox.
 * 
 * @author Claudius Korzen.
 */
public class PdfBoxDrawer implements PdfDrawer {
  /**
   * The default color.
   */
  protected static final Color DEFAULT_COLOR = Color.BLACK;

  /**
   * The default font.
   */
  protected static final PDFont DEFAULT_FONT = PDType1Font.HELVETICA;

  /**
   * The default fontsize.
   */
  protected static final float DEFAULT_FONT_SIZE = 12;

  /**
   * The default line thickness.
   */
  protected static final float DEFAULT_LINE_THICKNESS = 0.1f;

  /**
   * The pdf file in fashion of PdfBox.
   */
  protected PDDocument pdDocument;

  /**
   * The cache of PDPageContentStream objects.
   */
  protected List<PDPageContentStream> pageStreams = new ArrayList<>();

  /**
   * The cache of page bounding boxes.
   */
  protected List<Rectangle> pageBoundingBoxes = new ArrayList<>();

  /**
   * Creates a new visualizer from the given file.
   * 
   * @param pdfFile
   *        The PDF file to process.
   * @throws IOException
   *         If reading the PDF file failed.
   */
  @AssistedInject
  public PdfBoxDrawer(@Assisted Path pdfFile) throws IOException {
    this(pdfFile != null ? pdfFile.toFile() : null);
  }

  /**
   * Creates a new visualizer from the given file.
   * 
   * @param pdfFile
   *        The PDF file to process.
   * @throws IOException
   *         If reading the PDF file failed.
   */
  @AssistedInject
  public PdfBoxDrawer(@Assisted File pdfFile) throws IOException {
    this(pdfFile != null ? PDDocument.load(pdfFile) : null);
  }

  /**
   * Creates a new visualizer from the given PDDocument.
   * 
   * @param pdDocument
   *        The PDDocument.
   * @throws IOException
   *         If parsing the PDDocument failed.
   */
  public PdfBoxDrawer(PDDocument pdDocument) throws IOException {
    this.pdDocument = pdDocument;

    if (pdDocument == null) {
      throw new IllegalArgumentException("No PD document given");
    }

    PDDocumentCatalog catalog = pdDocument.getDocumentCatalog();

    if (catalog == null) {
      throw new IllegalArgumentException("No document catalog given.");
    }

    PDPageTree pages = catalog.getPages();

    if (pages == null) {
      throw new IllegalArgumentException("No pages given.");
    }

    this.pageStreams.add(null); // Add dummy, because pageNumbers are 1-based.
    this.pageBoundingBoxes.add(null);
    // Preallocate the list of streams.
    for (PDPage page : pages) {
      this.pageStreams.add(new PDPageContentStream(pdDocument, page,
          PDPageContentStream.AppendMode.APPEND, true));
      // TODO: Use guice.
      Rectangle boundingBox = new PlainRectangle();

      PDRectangle box = page.getCropBox();
      if (box == null) {
        box = page.getMediaBox();
      }
      if (box != null) {
        boundingBox.setMinX(box.getLowerLeftX());
        boundingBox.setMinY(box.getLowerLeftY());
        boundingBox.setMaxX(box.getUpperRightX());
        boundingBox.setMaxY(box.getUpperRightY());
      }

      this.pageBoundingBoxes.add(boundingBox);
    }
  }

  // ==========================================================================

  @Override
  public void drawLine(Line line, int pageNum) throws IOException {
    drawLine(line, pageNum, false, false);
  }

  @Override
  public void drawLine(Line line, int pageNum, boolean relativeToUpperLeft,
      boolean originInUpperLeft) throws IOException {
    this.drawLine(line, pageNum, DEFAULT_COLOR, relativeToUpperLeft,
        originInUpperLeft);
  }

  @Override
  public void drawLine(Line line, int pageNum, Color color)
      throws IOException {
    this.drawLine(line, pageNum, color, false, false);
  }

  @Override
  public void drawLine(Line line, int pageNum, Color color,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    this.drawLine(line, pageNum, color, DEFAULT_LINE_THICKNESS,
        relativeToUpperLeft, originInUpperLeft);
  }

  @Override
  public void drawLine(Line line, int pageNum, Color color, float thickness)
      throws IOException {
    drawLine(line, pageNum, color, thickness, false, false);
  }

  @Override
  public void drawLine(Line line, int pageNum, Color color, float thickness,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    PDPageContentStream stream = getPdPageContentStream(pageNum);
    Line adapted = adaptLine(line, pageNum, relativeToUpperLeft,
        originInUpperLeft);

    stream.setStrokingColor(color);
    stream.setLineWidth(thickness);
    stream.moveTo(adapted.getStartX(), adapted.getStartY());
    stream.lineTo(adapted.getEndX(), adapted.getEndY());
    stream.stroke();
  }

  // ==========================================================================

  @Override
  public void drawRectangle(Rectangle rect, int pageNum) throws IOException {
    this.drawRectangle(rect, pageNum, false, false);
  }

  @Override
  public void drawRectangle(Rectangle rect, int pageNum,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    this.drawRectangle(rect, pageNum, DEFAULT_COLOR, relativeToUpperLeft,
        originInUpperLeft);
  }

  @Override
  public void drawRectangle(Rectangle rect, int pageNum, Color color)
      throws IOException {
    this.drawRectangle(rect, pageNum, color, false, false);
  }

  @Override
  public void drawRectangle(Rectangle rect, int pageNum, Color color,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    this.drawRectangle(rect, pageNum, color, DEFAULT_LINE_THICKNESS,
        relativeToUpperLeft, originInUpperLeft);
  }

  @Override
  public void drawRectangle(Rectangle rect, int pageNum, Color color,
      float thickness) throws IOException {
    drawRectangle(rect, pageNum, color, thickness, false, false);
  }

  @Override
  public void drawRectangle(Rectangle rect, int pageNum, Color color,
      float thickness, boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    if (rect == null) {
      return;
    }
    PDPageContentStream stream = getPdPageContentStream(pageNum);
    
    Rectangle adapted = adaptRectangle(rect, pageNum, relativeToUpperLeft,
        originInUpperLeft);

    stream.setStrokingColor(color);
    stream.setNonStrokingColor(color);
    stream.setLineWidth(thickness);
    stream.moveTo(adapted.getMinX(), adapted.getMinY());
    stream.lineTo(adapted.getMaxX(), adapted.getMinY());
    stream.lineTo(adapted.getMaxX(), adapted.getMaxY());
    stream.lineTo(adapted.getMinX(), adapted.getMaxY());
    stream.lineTo(adapted.getMinX(), adapted.getMinY());
    stream.stroke();
  }

  // ==========================================================================

  @Override
  public void drawBoundingBox(HasBoundingBox box, int pageNum)
      throws IOException {
    this.drawRectangle(box.getBoundingBox(), pageNum);
  }

  @Override
  public void drawBoundingBox(HasBoundingBox box, int pageNum,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    this.drawRectangle(box.getBoundingBox(), pageNum, relativeToUpperLeft,
        originInUpperLeft);
  }

  @Override
  public void drawBoundingBox(HasBoundingBox box, int pageNum, Color color)
      throws IOException {
    this.drawRectangle(box.getBoundingBox(), pageNum, color);
  }

  @Override
  public void drawBoundingBox(HasBoundingBox box, int pageNum, Color color,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    this.drawRectangle(box.getBoundingBox(), pageNum, color,
        relativeToUpperLeft, originInUpperLeft);
  }

  @Override
  public void drawBoundingBox(HasBoundingBox box, int pageNum, Color color,
      float thickness) throws IOException {
    drawRectangle(box.getBoundingBox(), pageNum, color, thickness);
  }

  @Override
  public void drawBoundingBox(HasBoundingBox box, int pageNum, Color color,
      float thickness, boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    drawRectangle(box.getBoundingBox(), pageNum, color, thickness,
        relativeToUpperLeft, originInUpperLeft);
  }

  // ==========================================================================

  @Override
  public void drawText(String text, int pageNum) throws IOException {
    this.drawText(text, pageNum, false, false);
  }

  @Override
  public void drawText(String text, int pageNum, boolean relativeToUpperLeft,
      boolean originInUpperLeft) throws IOException {
    this.drawText(text, pageNum, new PlainPoint(0, 0), relativeToUpperLeft,
        originInUpperLeft);
  }

  @Override
  public void drawText(String text, int pageNum, Point point)
      throws IOException {
    this.drawText(text, pageNum, point, false, false);
  }

  @Override
  public void drawText(String text, int pageNum, Point point,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    this.drawText(text, pageNum, point, DEFAULT_COLOR, relativeToUpperLeft,
        originInUpperLeft);
  }

  @Override
  public void drawText(String text, int pageNum, Point point, Color color)
      throws IOException {
    this.drawText(text, pageNum, point, color, false, false);
  }

  @Override
  public void drawText(String text, int pageNum, Point point, Color color,
      boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    this.drawText(text, pageNum, point, color, DEFAULT_FONT_SIZE,
        relativeToUpperLeft, originInUpperLeft);
  }

  @Override
  public void drawText(String text, int pageNum, Point point, Color color,
      float fontsize) throws IOException {
    drawText(text, pageNum, point, color, fontsize, false, false);
  }

  @Override
  public void drawText(String text, int pageNum, Point point, Color color,
      float fontsize, boolean relativeToUpperLeft, boolean originInUpperLeft)
      throws IOException {
    PDPageContentStream stream = getPdPageContentStream(pageNum);

    Point adapted = adaptPoint(point, pageNum, relativeToUpperLeft,
        originInUpperLeft);

    stream.setNonStrokingColor(color);
    stream.beginText();
    stream.setFont(DEFAULT_FONT, fontsize);
    stream.newLineAtOffset(adapted.getX(), adapted.getY());
    stream.showText(text);
    stream.endText();
  }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    try {
      // Close all the open PDPageContentStream objects. Start at 1 because of
      // the dummy at the start.
      for (int i = 1; i < this.pageStreams.size(); i++) {
        try {
          this.pageStreams.get(i).close();
        } catch (IOException e) {
          continue;
        }
      }
      // Try to save the pdf document to the given file.
      this.pdDocument.save(os);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("Error on visualization: " + e.getMessage());
    } finally {
      try {
        // Try to close the pdf document.
        this.pdDocument.close();
      } catch (IOException e) {
        throw new IOException("Error on closing the pdf: " + e.getMessage());
      }
    }
  }

  /**
   * Returns the content stream for the given page.
   * 
   * @param pageNum
   *        The number of page to process.
   * @return The content stream of the given page.
   */
  protected PDPageContentStream getPdPageContentStream(int pageNum) {
    if (pageNum < 1 || pageNum >= this.pageStreams.size()) {
      throw new IllegalArgumentException("The given page number is invalid.");
    }
    return this.pageStreams.get(pageNum);
  }

  /**
   * Returns the PDDocument.
   * 
   * @return An instance of PDDocument.
   */
  public PDDocument getPdDocument() {
    return this.pdDocument;
  }

  /**
   * Adapts the given point dependent on whether the coordinates are relative
   * to upper left and whether the origin is in upper left.
   * 
   * @param point
   *        The point to adapt.
   * @param pageNum
   *        The number of the page in which the point is located.
   * @param isRelativeToUpperLeft
   *        A flag to indicate whether the coordinates of the point are
   *        relative to the upper left.
   * @param isOriginInUpperLeft
   *        A flag to indicate whether the origin of the page is in the upper
   *        left.
   * @return a new instance of Point with adapted coordinates.
   */
  public Point adaptPoint(Point point, int pageNum,
      boolean isRelativeToUpperLeft, boolean isOriginInUpperLeft) {
    Point copy = new PlainPoint(point.getX(), point.getY());
    if (isRelativeToUpperLeft) {
      Rectangle pageBoundingBox = this.pageBoundingBoxes.get(pageNum);
      copy.setY(pageBoundingBox.getMaxY() - point.getY());
    }
    return copy;
  }

  /**
   * Adapts the given line dependent on whether the coordinates are relative to
   * upper left and whether the origin is in upper left.
   * 
   * @param line
   *        The line to adapt.
   * @param pageNum
   *        The number of the page in which the line is located.
   * @param isRelativeToUpperLeft
   *        A flag to indicate whether the coordinates of the line are relative
   *        to the upper left.
   * @param isOriginInUpperLeft
   *        A flag to indicate whether the origin of the page is in the upper
   *        left.
   * @return A new instance of Line with adapted coordinates.
   */
  public Line adaptLine(Line line, int pageNum, boolean isRelativeToUpperLeft,
      boolean isOriginInUpperLeft) {
    Line copy = new PlainLine(line.getStartPoint(), line.getEndPoint());
    if (isRelativeToUpperLeft) {
      Rectangle pageBoundingBox = this.pageBoundingBoxes.get(pageNum);
      copy.setStartY(pageBoundingBox.getMaxY() - line.getStartY());
      copy.setEndY(pageBoundingBox.getMaxY() - line.getEndY());
    }
    return copy;
  }

  /**
   * Adapts the given line dependent on whether the coordinates are relative to
   * upper left and whether the origin is in upper left.
   * 
   * @param rect
   *        The rectangle to adapt.
   * @param pageNum
   *        The number of the page in which the rectangle is located.
   * @param isRelativeToUpperLeft
   *        A flag to indicate whether the coordinates of the rectangle are
   *        relative to the upper left.
   * @param isOriginInUpperLeft
   *        A flag to indicate whether the origin of the page is in the upper
   *        left.
   * @return A new instance of Rectangle with adapted coordinates.
   */
  public Rectangle adaptRectangle(Rectangle rect, int pageNum,
      boolean isRelativeToUpperLeft, boolean isOriginInUpperLeft) {
    Rectangle copy = new PlainRectangle(rect);
    if (isRelativeToUpperLeft) {
      Rectangle pageBoundingBox = this.pageBoundingBoxes.get(pageNum);
      copy.setMinY(pageBoundingBox.getMaxY() - rect.getMinY());
      copy.setMaxY(pageBoundingBox.getMaxY() - rect.getMaxY());
    }
    if (!isOriginInUpperLeft) {
      copy.setMaxY(copy.getMinY() + rect.getHeight());
    }
    return copy;
  }
}
