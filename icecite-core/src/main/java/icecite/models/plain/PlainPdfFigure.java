package icecite.models.plain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import icecite.models.PdfFeature;
import icecite.models.PdfFigure;

/**
 * A plain implementation of {@link PdfFigure}.
 * 
 * @author Claudius Korzen
 */
public class PlainPdfFigure extends PlainPdfElement implements PdfFigure {
  // ==========================================================================

  @Override
  public PdfFeature getFeature() {
    return PdfFeature.FIGURE;
  }

  // ==========================================================================

  @Override
  public String toString() {
    return "PlainPdfFigure(pos: " + getPosition() + ")";
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof PdfFigure) {
      PdfFigure otherFigure = (PdfFigure) other;

      EqualsBuilder builder = new EqualsBuilder();
      builder.append(getPosition(), otherFigure.getPosition());
      builder.append(getFeature(), otherFigure.getFeature());
      
      return builder.isEquals();
    }
    return false;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append(getPosition());
    builder.append(getFeature());
    return builder.hashCode();
  }
}
