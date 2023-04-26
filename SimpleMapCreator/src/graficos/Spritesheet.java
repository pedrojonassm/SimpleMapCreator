package graficos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.configs.ExSpriteSheet;

public class Spritesheet extends ExSpriteSheet {

	private BufferedImage spritesheet;
	private int quadradosX, quadradosY;

	public Spritesheet(File prArquivo, int prSize, int prSprites) {
		totalSprites = prSprites;
		tamanho = prSize;
		nome = prArquivo.getParentFile().getName();
		try {
			spritesheet = ImageIO.read(prArquivo);
			quadradosX = spritesheet.getWidth() / tamanho;
			quadradosY = spritesheet.getHeight() / tamanho;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Spritesheet(String prCaminho, int t, int prSprites) {
		totalSprites = prSprites;
		tamanho = t;
		nome = (prCaminho.startsWith("/")) ? prCaminho.substring(1) : prCaminho;
		try {
			if (Spritesheet.class.getResourceAsStream(prCaminho) == null) // exportado
				prCaminho = "/res" + prCaminho;
			spritesheet = ImageIO.read(Spritesheet.class.getResourceAsStream(prCaminho));
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

	public String getNome() {
		return nome;
	}

}