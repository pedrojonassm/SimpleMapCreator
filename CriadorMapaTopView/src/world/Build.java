package world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import files.salvarCarregar;

public class Build {
	int horizontal, vertical, high;
	File pasta;
	BufferedImage image;
	public Build(int h, int v, int hi, File f) {
		horizontal = h;
		vertical = v;
		high = hi;
		pasta = f;
		try {
			image = ImageIO.read(new File(f, salvarCarregar.name_foto_builds));
		} catch (IOException e) {
			image = null;
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public File getFile() {
		return pasta;
	}
	public int getHorizontal() {
		return horizontal;
	}
	public int getVertical() {
		return vertical;
	}
	public int getHigh() {
		return high;
	}
}
