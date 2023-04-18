package graficos.telas.sprites.subtelas;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graficos.ConjuntoSprites;
import graficos.telas.Tela;
import graficos.telas.sprites.TelaSprites;
import main.Gerador;

public class SubTelaMultiplosSprites implements Tela {

	private Rectangle caixinhaSprites, limpar;

	private ArrayList<ConjuntoSprites> aCoConjuntoSprites;

	private int max_sprites_por_pagina, pagina;

	public static SubTelaMultiplosSprites instance;

	public SubTelaMultiplosSprites() {
		instance = this;
		caixinhaSprites = new Rectangle(Gerador.VariavelX * 3, Gerador.VariavelX * 9);
		limpar = new Rectangle(caixinhaSprites.width / 3, Gerador.VariavelY / 3);
		aCoConjuntoSprites = new ArrayList<>();
		pagina = 0;

	}

	@Override
	public void posicionarRetangulos() {
		caixinhaSprites.x = Gerador.windowWidth - Gerador.VariavelX * 3 - Gerador.VariavelX / 2;
		caixinhaSprites.y = Gerador.windowHEIGHT / 9;
		limpar.x = caixinhaSprites.x + caixinhaSprites.width - caixinhaSprites.width / 5 - limpar.width / 2;
		limpar.y = Gerador.windowHEIGHT / (Gerador.VariavelX / 4);
		max_sprites_por_pagina = (caixinhaSprites.width / Gerador.VariavelX)
				* (caixinhaSprites.height / Gerador.VariavelX);
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {
		int w1;
		prGraphics.drawRect(caixinhaSprites.x, caixinhaSprites.y, caixinhaSprites.width, caixinhaSprites.height);
		prGraphics.drawRect(limpar.x, limpar.y, limpar.width, limpar.height);
		w1 = prGraphics.getFontMetrics().stringWidth("Limpar");
		prGraphics.drawString("Limpar", limpar.x + limpar.width / 2 - w1 / 2,
				limpar.y + Gerador.windowHEIGHT / limpar.y);
		w1 = prGraphics.getFontMetrics()
				.stringWidth((pagina + 1) + "/" + ((aCoConjuntoSprites.size() / max_sprites_por_pagina) + 1));
		prGraphics.drawString((pagina + 1) + "/" + ((aCoConjuntoSprites.size() / max_sprites_por_pagina) + 1),
				caixinhaSprites.x + Gerador.VariavelX / 2 - w1 / 2, limpar.y + Gerador.windowHEIGHT / limpar.y);
		int x, y, desenhando = 0;
		for (int i = 0; i < max_sprites_por_pagina
				&& i + (max_sprites_por_pagina * pagina) < aCoConjuntoSprites.size(); i++) {
			x = desenhando % (caixinhaSprites.width / Gerador.VariavelX);
			y = desenhando / (caixinhaSprites.width / Gerador.VariavelX);
			ArrayList<BufferedImage> lDesenhoAtual = aCoConjuntoSprites.get(i + (max_sprites_por_pagina * pagina))
					.obterSprite_atual();
			for (BufferedImage iBufferedImage : lDesenhoAtual)
				prGraphics.drawImage(iBufferedImage, x * Gerador.VariavelX + caixinhaSprites.x,
						y * Gerador.VariavelX + caixinhaSprites.y, Gerador.VariavelX, Gerador.VariavelY, null);

			desenhando++;
		}
	}

	@Override
	public boolean clicou(int x, int y) {
		if (caixinhaSprites.contains(x, y)) {
			if (TelaSprites.instance.contemSpritesSelecionados()) {
				ConjuntoSprites lConjuntoSprites = new ConjuntoSprites();
				lConjuntoSprites.adicionar_sprite_selecionado();
				aCoConjuntoSprites.add(lConjuntoSprites);
			}
			return true;
		} else if (limpar.contains(x, y)) {
			aCoConjuntoSprites.clear();
			return true;
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		if (caixinhaSprites.contains(x, y)) {
			if (!aCoConjuntoSprites.isEmpty()) {
				int px = (x - caixinhaSprites.x) / Gerador.VariavelX, py = (y - caixinhaSprites.y) / Gerador.VariavelY;
				int aux = px + py * (caixinhaSprites.width / Gerador.VariavelX);
				aux = aux + (max_sprites_por_pagina * pagina);
				aCoConjuntoSprites.remove(aux);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (caixinhaSprites.contains(x, y)) {

			pagina += prRodinha;
			if (pagina < 0) {
				pagina = aCoConjuntoSprites.size() / max_sprites_por_pagina;
			} else if (pagina > aCoConjuntoSprites.size() / max_sprites_por_pagina) {
				pagina = 0;
			}
			return true;
		}
		return false;
	}

	public ArrayList<ConjuntoSprites> getConjuntoSprites() {
		return aCoConjuntoSprites;
	}

	public void addSpritesConjunto(ArrayList<ConjuntoSprites> prCoConjuntoSprites) {
		for (ConjuntoSprites iConjuntoSprites : prCoConjuntoSprites) {
			aCoConjuntoSprites.add(iConjuntoSprites.clone());
		}
	}

	@Override
	public String getNome() {
		return "Múltiplos Sprites";
	}

	@Override
	public Tela getSubTela() {
		return null;
	}

}
