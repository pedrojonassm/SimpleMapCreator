package graficos.telas.sprites.subtelas;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graficos.telas.sprites.TelaSprites;
import world.World;

public class Preset {
	private ArrayList<ArrayList<int[]>> sprites;
	private ArrayList<ArrayList<int[]>> spritesOld;
	private Rectangle rectangle;
	private Rectangle rectangleOld;

	public Preset(int x, int y, int width, int height) {
		rectangle = new Rectangle(x, y, width, height);
		rectangleOld = new Rectangle(rectangle.x + rectangle.width * 2, rectangle.y, rectangle.width, rectangle.height);
		sprites = new ArrayList<>();
		spritesOld = new ArrayList<>();
	}

	public ArrayList<ArrayList<int[]>> getSprites() {
		return sprites;
	}

	public void setSprites(ArrayList<ArrayList<int[]>> sprites) {
		this.sprites = sprites;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public void render(Graphics prGraphics) {
		prGraphics.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		ArrayList<int[]> imagens;
		if (sprites != null && TelaSprites.tiles_nivel < sprites.size()) {
			imagens = sprites.get(TelaSprites.tiles_nivel);
			if (imagens != null && imagens.size() > 0) {
				int[] sprite = imagens.get(World.tiles_index % imagens.size());
				BufferedImage image = World.sprites_do_mundo.get(sprite[0])[sprite[1]];
				prGraphics.drawImage(image, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
			}

		}

		if (spritesOld != null && TelaSprites.tiles_nivel < spritesOld.size()) {
			imagens = spritesOld.get(TelaSprites.tiles_nivel);
			if (imagens != null && imagens.size() > 0) {
				int[] sprite = imagens.get(World.tiles_index % imagens.size());
				BufferedImage image = World.sprites_do_mundo.get(sprite[0])[sprite[1]];
				prGraphics.drawImage(image, rectangleOld.x, rectangleOld.y, rectangleOld.width, rectangleOld.height,
						null);
			}
		}
	}

	public void colar() {
		colar(sprites);
	}

	private void colar(ArrayList<ArrayList<int[]>> prSprites) {
		// Do selecionado traz pra cá
		if (TelaSprites.sprite_selecionado.size() == 0) {
			prSprites.get(TelaSprites.tiles_nivel).clear();
			return;
		}

		if (TelaSprites.array.size() == 0 && prSprites.size() < TelaSprites.tiles_nivel && prSprites.size() > 0) {
			prSprites.set(TelaSprites.tiles_nivel, null);
			return;
		}

		ArrayList<int[]> novo = new ArrayList<int[]>();
		for (int i = 0; i < TelaSprites.sprite_selecionado.size(); i++) {
			int[] a = { TelaSprites.array.get(i), TelaSprites.lista.get(i) };
			novo.add(a);
		}
		if (prSprites.size() > TelaSprites.tiles_nivel
				|| (prSprites.size() > TelaSprites.tiles_nivel && prSprites.get(TelaSprites.tiles_nivel) == null)) {
			if (spritesOld.size() > TelaSprites.tiles_nivel || (spritesOld.size() > TelaSprites.tiles_nivel
					&& spritesOld.get(TelaSprites.tiles_nivel) == null)) {
				spritesOld.set(TelaSprites.tiles_nivel, prSprites.get(TelaSprites.tiles_nivel));
			} else {
				spritesOld.add(prSprites.get(TelaSprites.tiles_nivel));
			}
			prSprites.set(TelaSprites.tiles_nivel, novo);
		} else
			prSprites.add(novo);
	}

	public void copiar() {
		copiar(sprites);
	}

	private void copiar(ArrayList<ArrayList<int[]>> prSprites) {
		// Daqui joga pros selecionados

		if (prSprites.size() <= TelaSprites.tiles_nivel || prSprites.get(TelaSprites.tiles_nivel).size() == 0) {
			return;
		}
		TelaSprites.pegar_tile_ja_colocado(prSprites.get(TelaSprites.tiles_nivel));
	}

	public boolean clicou(int x, int y) {
		if (rectangle.contains(x, y)) {
			if (TelaSprites.sprite_selecionado.size() > 0) {
				colar();
			} else {
				copiar();
			}
			return true;
		} else if (rectangleOld.contains(x, y)) {
			copiar(spritesOld);
			return true;
		}
		return false;
	}

}
