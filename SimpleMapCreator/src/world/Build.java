package world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import files.SalvarCarregar;

public class Build {
	int horizontal, vertical, high;
	File pasta;
	BufferedImage image;
	private boolean temporaria;

	public Build(int h, int v, int hi, File f) {
		temporaria = false;
		horizontal = h;
		vertical = v;
		high = hi;
		pasta = f;
		try {
			File lFileImagem = new File(f, SalvarCarregar.nameImagem);
			if (lFileImagem.exists())
				image = ImageIO.read(lFileImagem);
			else
				delete();
		} catch (IOException e) {
			image = null;
			e.printStackTrace();
		}
	}

	public void changeTemporaria(boolean prTemporaria) {
		temporaria = prTemporaria;
	}

	public boolean isTemporaria() {
		return temporaria;
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

	public void delete() {
		/*
		 * try { FileUtils.deleteDirectory(pasta); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		File lFile;
		for (String iFile : SalvarCarregar.listFilesForFolder(pasta)) {
			lFile = new File(pasta.getParentFile(), iFile);
			if (lFile.exists())
				lFile.delete();
		}
		pasta.delete();

	}
}
