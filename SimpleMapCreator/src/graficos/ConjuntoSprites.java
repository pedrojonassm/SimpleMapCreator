package graficos;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graficos.telas.Sprite;
import graficos.telas.sprites.TelaSprites;
import graficos.telas.sprites.TelaSprites.kdModoColocar;
import world.World;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConjuntoSprites {
	private ArrayList<ArrayList<Sprite>> sprites;

	public ConjuntoSprites() {
		sprites = new ArrayList<ArrayList<Sprite>>();

		for (int i = 0; i < TelaSprites.max_tiles_nivel; i++) {
			sprites.add(new ArrayList<Sprite>());
		}

	}

	public ArrayList<ArrayList<Sprite>> getSprites() {
		return sprites;
	}

	public void setSprites(ArrayList<ArrayList<Sprite>> sprites) {
		this.sprites = sprites;
	}

	public ArrayList<BufferedImage> obterSprite_atual() {
		ArrayList<BufferedImage> lDesenhoAtual = new ArrayList<BufferedImage>();
		for (ArrayList<Sprite> imagens : sprites) {
			if (imagens != null && imagens.size() > 0) {
				Sprite sprite = imagens.get(World.tiles_index % imagens.size());
				lDesenhoAtual.add(sprite.pegarImagem());
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
				if (sprites.size() > TelaSprites.LayerLevel)
					sprites.get(TelaSprites.LayerLevel).clear();
			} else if (kdModoColocar.kdFullTile.equals(TelaSprites.instance.getModoColocar())) {
				for (ArrayList<Sprite> iSprites : sprites)
					iSprites.clear();

			}
			return;
		}

		for (int iLayerTile = 0; iLayerTile < TelaSprites.instance.sprite_selecionado.size(); iLayerTile++) {
			if (kdModoColocar.kdLayerToLayer.equals(TelaSprites.instance.getModoColocar())
					&& TelaSprites.LayerLevel != iLayerTile)
				continue;

			if (TelaSprites.instance.nomeSpritesheet.get(iLayerTile).size() == 0 && sprites.size() < iLayerTile
					&& sprites.size() > 0) {
				sprites.set(iLayerTile, null);
				continue;
			}
			ArrayList<Sprite> novo = new ArrayList<Sprite>();
			for (int i = 0; i < TelaSprites.instance.sprite_selecionado.get(iLayerTile).size(); i++) {
				novo.add(new Sprite(TelaSprites.instance.nomeSpritesheet.get(iLayerTile).get(i),
						TelaSprites.instance.PosicaoSprite.get(iLayerTile).get(i)));
			}
			if (sprites.size() > iLayerTile || (sprites.size() > iLayerTile && sprites.get(iLayerTile) == null))
				sprites.set(iLayerTile, novo);
			else
				sprites.add(novo);
		}
	}

	public ConjuntoSprites clone() {
		ConjuntoSprites lConjuntoSprites = new ConjuntoSprites();
		ArrayList<ArrayList<Sprite>> lSpritesConjunto = new ArrayList<>();
		ArrayList<Sprite> lCoSprites;
		Sprite lSprite;
		for (int i = 0; i < getSprites().size(); i++) {
			lCoSprites = new ArrayList<>();
			for (int j = 0; j < getSprites().get(i).size(); j++) {
				lSprite = new Sprite();
				lSprite.setNome(getSprites().get(i).get(j).getNome());
				lSprite.setPosicao(getSprites().get(i).get(j).getPosicao());
				lCoSprites.add(lSprite);
			}
			lSpritesConjunto.add(lCoSprites);
		}
		lConjuntoSprites.setSprites(lSpritesConjunto);
		return lConjuntoSprites;
	}

}
