package utils.pdf;


import org.springframework.context.annotation.Lazy;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
@Component
@ScenarioScope
class ImageUtil {
	static Logger logger = Logger.getLogger(ImageUtil.class.getName());

	static boolean compareAndHighlight (BufferedImage img1, BufferedImage img2, String fileName, boolean highlight, int colorCode) throws IOException {
		int w = img1.getWidth();
		int h = img1.getHeight();
		int[] p1 = img1.getRGB(0, 0, w, h, (int[]) null, 0, w);
		int[] p2 = img2.getRGB(0, 0, w, h, (int[]) null, 0, w);
		if (Arrays.equals(p1, p2)) {
			return true;
		} else {
			logger.warning("Image compared - does not match");
			if (highlight) {
				for (int i = 0; i < p1.length; ++i) {
					if (p1[ i ] != p2[ i ]) {
						p1[ i ] = colorCode;
					}
				}

				BufferedImage out = new BufferedImage(w, h, 2);
				out.setRGB(0, 0, w, h, p1, 0, w);
				saveImage(out, fileName);
			}

			return false;
		}
	}

	static void saveImage (BufferedImage image, String file) {
		try {
			File outputfile = new File(file);
			ImageIO.write(image, "png", outputfile);
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

}
