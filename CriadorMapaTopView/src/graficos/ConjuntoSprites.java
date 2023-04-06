package graficos;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graficos.telas.sprites.TelaSprites;
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
		ArrayList<int[]> sprite;
		sprite = (sprites.size() > TelaSprites.tiles_nivel) ? sprites.get(TelaSprites.tiles_nivel) : null;

		if (sprite == null || sprite.size() == 0) {
			return;
		}
		TelaSprites.pegar_tile_ja_colocado(sprite);
	}

	public void adicionar_sprite_selecionado() {

		if (TelaSprites.sprite_selecionado.size() == 0) {
			sprites.get(TelaSprites.tiles_nivel).clear();
			return;
		}

		ArrayList<int[]> novo = new ArrayList<int[]>();
		if (TelaSprites.array.size() == 0 && sprites.size() < TelaSprites.tiles_nivel && sprites.size() > 0) {
			sprites.set(TelaSprites.tiles_nivel, null);
			return;
		}
		for (int i = 0; i < TelaSprites.sprite_selecionado.size(); i++) {
			int[] a = { TelaSprites.array.get(i), TelaSprites.lista.get(i) };
			novo.add(a);
		}
		if (sprites.size() > TelaSprites.tiles_nivel
				|| (sprites.size() > TelaSprites.tiles_nivel && sprites.get(TelaSprites.tiles_nivel) == null))
			sprites.set(TelaSprites.tiles_nivel, novo);
		else
			sprites.add(novo);
	}

}
