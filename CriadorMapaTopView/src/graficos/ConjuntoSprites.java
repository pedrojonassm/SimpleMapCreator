package graficos;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graficos.telas.sprites.TelaSprites;
import graficos.telas.sprites.TelaSprites.kdModoColocar;
import world.World;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConjuntoSprites {
	private ArrayList<ArrayList<int[]>> sprites;

	public ConjuntoSprites() {
		sprites = new ArrayList<ArrayList<int[]>>();

		for (int i = 0; i < TelaSprites.max_tiles_nivel; i++) {
			sprites.add(new ArrayList<int[]>());
		}

	}

	public ArrayList<ArrayList<int[]>> getSprites() {
		return sprites;
	}

	public void setSprites(ArrayList<ArrayList<int[]>> sprites) {
		this.sprites = sprites;
	}

	public ArrayList<BufferedImage> obterSprite_atual() {
		ArrayList<BufferedImage> lDesenhoAtual = new ArrayList<BufferedImage>();
		for (ArrayList<int[]> imagens : sprites) {
			if (imagens != null && imagens.size() > 0) {
				int[] sprite = imagens.get(World.tiles_index % imagens.size());
				lDesenhoAtual.add(World.sprites_do_mundo.get(sprite[0])[sprite[1]]);
			}
		}

		return lDesenhoAtual;
	}

	public void pegarsprites() {
		TelaSprites.instance.pegar_tile_ja_colocado(sprites);
	}

	public void adicionar_sprite_selecionado() {

		if (!TelaSprites.instance.contemSpritesSelecionados()) {

			if (kdModoColocar.kdLayerToLayer.equals(TelaSprites.instance.getModoColocar())) {
				if (sprites.size() > TelaSprites.tilesLayer)
					sprites.get(TelaSprites.tilesLayer).clear();
			} else if (kdModoColocar.kdFullTile.equals(TelaSprites.instance.getModoColocar())) {
				for (ArrayList<int[]> iSprites : sprites)
					iSprites.clear();

			}
			return;
		}

		for (int iLayerTile = 0; iLayerTile < TelaSprites.instance.sprite_selecionado.size(); iLayerTile++) {
			if (kdModoColocar.kdLayerToLayer.equals(TelaSprites.instance.getModoColocar())
					&& TelaSprites.tilesLayer != iLayerTile)
				continue;

			if (TelaSprites.instance.nomeSpritesheet.get(iLayerTile).size() == 0 && sprites.size() < iLayerTile
					&& sprites.size() > 0) {
				sprites.set(iLayerTile, null);
				continue;
			}
			ArrayList<int[]> novo = new ArrayList<int[]>();
			for (int i = 0; i < TelaSprites.instance.sprite_selecionado.get(iLayerTile).size(); i++) {
				int[] a = { TelaSprites.instance.nomeSpritesheet.get(iLayerTile).get(i),
						TelaSprites.instance.PosicaoSprite.get(iLayerTile).get(i) };
				novo.add(a);
			}
			if (sprites.size() > iLayerTile || (sprites.size() > iLayerTile && sprites.get(iLayerTile) == null))
				sprites.set(iLayerTile, novo);
			else
				sprites.add(novo);
		}
	}

}
