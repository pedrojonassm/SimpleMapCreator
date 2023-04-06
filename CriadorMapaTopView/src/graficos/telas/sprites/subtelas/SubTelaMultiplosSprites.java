package graficos.telas.sprites.subtelas;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import graficos.ConjuntoSprites;
import graficos.telas.Tela;
import main.Gerador;

public class SubTelaMultiplosSprites implements Tela {

	private Rectangle caixinhaSprites, limpar;

	private ArrayList<ConjuntoSprites> aCoConjuntoSprites;

	private int max_sprites_por_pagina, pagina;

	public static SubTelaMultiplosSprites instance;

	public SubTelaMultiplosSprites() {
		instance = this;
		caixinhaSprites = new Rectangle(Gerador.quadrado.width * 3, Gerador.quadrado.width * 9);
		limpar = new Rectangle(caixinhaSprites.width / 3, Gerador.quadrado.height / 3);
		aCoConjuntoSprites = new ArrayList<>();
		pagina = 0;
		max_sprites_por_pagina = caixinhaSprites.width / Gerador.quadrado.width * caixinhaSprites.height
				/ Gerador.quadrado.width;
		posicionarRetangulos();
	}

	private void posicionarRetangulos() {
		caixinhaSprites.x = Gerador.WIDTH - Gerador.quadrado.width * 3 - Gerador.quadrado.width / 2;
		caixinhaSprites.y = Gerador.HEIGHT / 9;
		limpar.x = caixinhaSprites.x + caixinhaSprites.width - caixinhaSprites.width / 5 - limpar.width / 2;
		limpar.y = Gerador.HEIGHT / (Gerador.quadrado.width / 4);
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
		prGraphics.drawString("Limpar", limpar.x + limpar.width / 2 - w1 / 2, limpar.y + Gerador.HEIGHT / limpar.y);
		w1 = prGraphics.getFontMetrics()
				.stringWidth((pagina + 1) + "/" + ((aCoConjuntoSprites.size() / max_sprites_por_pagina) + 1));
		prGraphics.drawString((pagina + 1) + "/" + ((aCoConjuntoSprites.size() / max_sprites_por_pagina) + 1),
				caixinhaSprites.x + Gerador.quadrado.width / 2 - w1 / 2, limpar.y + Gerador.HEIGHT / limpar.y);
		int x, y, desenhando = 0;
		for (int i = 0; i < max_sprites_por_pagina
				&& i + (max_sprites_por_pagina * pagina) < aCoConjuntoSprites.size(); i++) {
			x = desenhando % (caixinhaSprites.width / Gerador.quadrado.width);
			y = desenhando / (caixinhaSprites.width / Gerador.quadrado.width);
			ArrayList<BufferedImage> lDesenhoAtual = aCoConjuntoSprites.get(i + (max_sprites_por_pagina * pagina))
					.obterSprite_atual();
			for (BufferedImage iBufferedImage : lDesenhoAtual)
				prGraphics.drawImage(iBufferedImage, x * Gerador.quadrado.width + caixinhaSprites.x,
						y * Gerador.quadrado.width + caixinhaSprites.y, Gerador.quadrado.width, Gerador.quadrado.height,
						null);

			desenhando++;
		}
	}

	@Override
	public boolean clicou(int x, int y) {
		if (caixinhaSprites.contains(x, y)) {
			ConjuntoSprites lConjuntoSprites = new ConjuntoSprites();
			lConjuntoSprites.adicionar_sprite_selecionado();
			aCoConjuntoSprites.add(lConjuntoSprites);
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
				int px = (x - caixinhaSprites.x) / Gerador.quadrado.width,
						py = (y - caixinhaSprites.y) / Gerador.quadrado.height;
				int aux = px + py * (caixinhaSprites.width / Gerador.quadrado.width);
				aux = aux + (max_sprites_por_pagina * pagina);
				aCoConjuntoSprites.remove(aux);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		int k = 0;
		if (prRodinha > 0)
			k = 1;
		else
			k = -1;
		if (caixinhaSprites.contains(x, y)) {

			pagina += k;
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

	public void addSpritesConjunto(List<ConjuntoSprites> aCoConjuntoSprites2) {
		aCoConjuntoSprites.addAll(aCoConjuntoSprites2);
	}

}
