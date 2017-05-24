package icecite.utils.color;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A collection of utility methods that deal with colors.
 * 
 * @author Claudius Korzen
 */
public class ColorUtils {
  /**
   * Transforms the given packed RGB value into an array of three values in
   * range [0,255] representing the R, G and B values.
   * 
   * @param pixel
   *        The packed RGB value.
   * @return The RGB values in an array of three values.
   */
  public static float[] toRGBArray(int pixel) {
    float alpha = (pixel >> 24) & 0xff;
    float red = ((pixel >> 16) & 0xff) / 255f;
    float green = ((pixel >> 8) & 0xff) / 255f;
    float blue = ((pixel) & 0xff) / 255f;
    return new float[] { red, green, blue, alpha };
  }

  /**
   * Checks if the given image consists only of a single color and returns the
   * color if so. Returns null if there a at least two different colors.
   * 
   * @param image
   *        The image to process.
   * @return The color, if the image consists only of a single color; null
   *         otherwise.
   * @throws IOException
   *         if reading the image fails.
   */
  public static float[] getExclusiveColor(BufferedImage image)
      throws IOException {
    if (image == null) {
      return null;
    }

    int lastRgb = Integer.MAX_VALUE;
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int rgb = image.getRGB(i, j);
        if (lastRgb != Integer.MAX_VALUE && lastRgb != rgb) {
          return null;
        }
        lastRgb = rgb;
      }
    }

    if (lastRgb == Integer.MAX_VALUE) {
      return null;
    }

    return toRGBArray(lastRgb);
  }
}
