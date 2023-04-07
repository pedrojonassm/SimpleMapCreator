package graficos.telas.sprites.subtelas;

import java.awt.Graphics;

import graficos.telas.Tela;
import graficos.telas.sprites.TelaSprites;
import main.Gerador;

public class SubTelaPreSets implements Tela {

	private final int aPreSetSize = Gerador.TS / 2;

	private Preset[] aPreSets;

	public static SubTelaPreSets instance;

	public SubTelaPreSets() {
		instance = this;
		aPreSets = new Preset[10];
		for (int i = 0; i < aPreSets.length; i++) {
			aPreSets[i] = new Preset(Gerador.WIDTH - aPreSetSize * 4,
					Gerador.HEIGHT / 2 - (aPreSetSize * aPreSets.length) / 2 + i * aPreSetSize, aPreSetSize,
					aPreSetSize);
		}
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {

		for (Preset iPreset : aPreSets) {
			iPreset.render(prGraphics);
		}
	}

	@Override
	public boolean clicou(int x, int y) {
		for (Preset iPreset : aPreSets) {
			if (iPreset.clicou(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		return false;
	}

	public void ativar(int prNumeroPressionado) {
		prNumeroPressionado--;
		if (prNumeroPressionado < 0)
			prNumeroPressionado = aPreSets.length - 1;

		if (TelaSprites.sprite_selecionado.size() > 0) {
			aPreSets[prNumeroPressionado].colar();
		} else {
			aPreSets[prNumeroPressionado].copiar();
		}

	}

	@Override
	public String getNome() {

		return "Pré-Sets";
	}

}
