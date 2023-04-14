package graficos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {

	private BufferedImage spritesheet;
	private int tamanho, quadradosX, quadradosY;
	String arquivo;

	public Spritesheet(File prArquivo, int prSize) {
		tamanho = prSize;
		arquivo = prArquivo.getName();
		try {
			spritesheet = ImageIO.read(prArquivo);
			quadradosX = spritesheet.getWidth() / tamanho;
			quadradosY = spritesheet.getHeight() / tamanho;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Spritesheet(String prCaminhho, int t) {
		tamanho = t;
		arquivo = prCaminhho;
		try {
			spritesheet = ImageIO.read(getClass().getResource(prCaminhho));
			quadradosX = spritesheet.getWidth() / tamanho;
			quadradosY = spritesheet.getHeight() / tamanho;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BufferedImage getAsset(int x, int y) {
		return getAsset(x + y * tamanho);
	}

	public BufferedImage getAsset(int position) {
		return spritesheet.getSubimage((position % quadradosX) * tamanho, (position / quadradosX) * tamanho, tamanho,
				tamanho);
	}

	public BufferedImage[] get_x_sprites(int total) {
		BufferedImage[] retorno = new BufferedImage[total];
		for (int i = 0; i < total; i++) {
			retorno[i] = getAsset(i);
		}
		return retorno;
	}

	public int getQuadradosX() {
		return quadradosX;
	}

	public int getQuadradosY() {
		return quadradosY;
	}

	public int getTamanho() {
		return tamanho;
	}

	public String getArquivo() {
		return arquivo;
	}

}