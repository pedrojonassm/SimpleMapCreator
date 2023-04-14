package graficos.telas.sprites.subtelas;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graficos.telas.sprites.TelaSprites;
import graficos.telas.sprites.TelaSprites.kdModoColocar;
import world.World;

public class Preset {
	private ArrayList<ArrayList<int[]>> sprites;
	private ArrayList<ArrayList<int[]>> spritesOld;
	private Rectangle rectangle;
	private Rectangle rectangleOld;

	public Preset(int width, int height) {
		rectangle = new Rectangle(width, height);
		rectangleOld = new Rectangle(rectangle.width, rectangle.height);
		sprites = new ArrayList<>();
		spritesOld = new ArrayList<>();
	}

	public void posicionarRetangulos(int x, int y) {
		rectangle.x = x;
		rectangle.y = y;
		rectangleOld.x = rectangle.x + rectangle.width * 2;
		rectangleOld.y = rectangle.y;
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
		for (int i = 0; i < sprites.size() && i < spritesOld.size(); i++) {
			if ((TelaSprites.kdModoColocar.kdLayerToLayer.equals(TelaSprites.instance.getModoColocar())
					&& i != TelaSprites.tilesLayer))
				continue;
			imagens = sprites.get(i);

			if (imagens != null && imagens.size() > 0) {
				int[] sprite = imagens.get(World.tiles_index % imagens.size());
				BufferedImage image = World.sprites_do_mundo.get(sprite[0])[sprite[1]];
				prGraphics.drawImage(image, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
			}

			imagens = spritesOld.get(i);
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

		if (!TelaSprites.instance.contemSpritesSelecionados()) {

			if (kdModoColocar.kdLayerToLayer.equals(TelaSprites.instance.getModoColocar())) {
				if (prSprites.size() > TelaSprites.tilesLayer)
					prSprites.get(TelaSprites.tilesLayer).clear();
			} else if (kdModoColocar.kdFullTile.equals(TelaSprites.instance.getModoColocar())) {
				for (ArrayList<int[]> iSprites : sprites)
					iSprites.clear();

			}
			return;
		}
		ArrayList<int[]> novo;
		for (int iLayerTile = 0; iLayerTile < TelaSprites.instance.sprite_selecionado.size(); iLayerTile++) {
			if (kdModoColocar.kdLayerToLayer.equals(TelaSprites.instance.getModoColocar())
					&& TelaSprites.tilesLayer != iLayerTile)
				continue;
			novo = new ArrayList<int[]>();
			for (int i = 0; i < TelaSprites.instance.sprite_selecionado.get(iLayerTile).size(); i++) {
				int[] a = { TelaSprites.instance.array.get(iLayerTile).get(i),
						TelaSprites.instance.lista.get(iLayerTile).get(i) };
				novo.add(a);
			}
			if (prSprites.size() > iLayerTile || (prSprites.size() > iLayerTile && prSprites.get(iLayerTile) == null)) {
				if (spritesOld.size() > iLayerTile
						|| (spritesOld.size() > iLayerTile && spritesOld.get(iLayerTile) == null)) {
					spritesOld.set(iLayerTile, prSprites.get(iLayerTile));
				} else {
					spritesOld.add(prSprites.get(iLayerTile));
				}
				prSprites.set(iLayerTile, novo);
			} else
				prSprites.add(novo);
		}
	}

	public void copiar() {
		copiar(sprites);
	}

	private void copiar(ArrayList<ArrayList<int[]>> prSprites) {
		TelaSprites.instance.pegar_tile_ja_colocado(prSprites);
	}

	public boolean clicou(int x, int y) {
		if (rectangle.contains(x, y)) {
			if (TelaSprites.instance.contemSpritesSelecionados()) {
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
