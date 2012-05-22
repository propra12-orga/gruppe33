package propra2012.gruppe33.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author Matthias Hesse
 *
 */

public class SpriteMerge implements FilenameFilter {

	private File f;
	private static File[] fileArray;

	private File finish = new File("").getAbsoluteFile();

	private final String[] okFileExtensions = new String[] { "jpg", "png",
			"gif","bmp" };

	public SpriteMerge() {

		f = new File("").getAbsoluteFile();
		fileArray = f.listFiles(this);
		
		System.out.println("Dir: " + f.getAbsolutePath());
		System.out.println("File in dir: " + f.list().length);
	}

	public BufferedImage[] readImages(File[] fileArray) throws IOException {
		BufferedImage[] subImages = new BufferedImage[fileArray.length];
		for (int i = 0; i < subImages.length; i++) {
			subImages[i] = ImageIO.read(fileArray[i]);
		}
		return subImages;
	}

	public Color readBackgroundColor(BufferedImage image) {
		Color backgroundColor = new Color(image.getRGB(0, 0));

		return backgroundColor;

	}

	public BufferedImage mergeImages(BufferedImage[] imageArray) {
		
		int xSize = imageArray[0].getWidth();
		int ySize = imageArray[0].getHeight();
		int num = imageArray.length;
		int plates = (int)Math.sqrt(num);
		BufferedImage newImage = new BufferedImage(plates*xSize, plates*ySize,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		int x = 0;
		for(int i = 0; i < plates;i++){
			for(int j = 0; j < plates;j++){
				g.drawImage(imageArray[x], xSize*i, ySize*j, null);
				x++;
					
					
			}
		}
		g.dispose();
		return newImage;  

	}

	public BufferedImage setTransparency(BufferedImage dirtyImage,
			Color backgroundColor) {
		BufferedImage cleanImage = dirtyImage;
		for (int i = 0; i < cleanImage.getHeight(); i++) {
			for (int j = 0; j < cleanImage.getWidth(); j++) {
				if (new Color(cleanImage.getRGB(i, j)).equals(backgroundColor)) {
					cleanImage.setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
				}
			}
		}

		return cleanImage;
	}

	public void saveImage(BufferedImage image) throws IOException {
		finish = new File("test.png");
		ImageIO.write(image, "png", finish);

	}

	public static void main(String[] args) throws IOException {
		SpriteMerge sm = new SpriteMerge();
		BufferedImage[] subImages = sm.readImages(fileArray);
		BufferedImage newImage = sm.mergeImages(subImages);
		newImage = sm.setTransparency(newImage, sm.readBackgroundColor(newImage));
		sm.saveImage(newImage);

	}

	@Override
	public boolean accept(File dir, String name) {
		for (String extension : okFileExtensions) {
			if (name.toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

}
