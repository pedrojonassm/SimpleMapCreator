package graficos.telas.configuracao.subtelas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import graficos.Ui;
import graficos.telas.Tela;
import main.Gerador;

public class SubTelaPropriedade implements Tela {
	// Propriedades Com base na proprieddade Escolhhidda, sssera mostrado noss
	// tiless casso tiverem

	private Rectangle adicionarNovaPropriedade;

	public static SubTelaPropriedade instance;

	public SubTelaPropriedade() {
		instance = this;

		adicionarNovaPropriedade = new Rectangle(Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width - 40,
				Ui.caixinha_dos_sprites.y + 80, 20, 20);
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Graphics prGraphics) {
		int w1;

		prGraphics.setColor(Color.green);
		prGraphics.drawRect(adicionarNovaPropriedade.x, adicionarNovaPropriedade.y, adicionarNovaPropriedade.width,
				adicionarNovaPropriedade.height);
		prGraphics.drawLine(adicionarNovaPropriedade.x,
				adicionarNovaPropriedade.y + adicionarNovaPropriedade.height / 2,
				adicionarNovaPropriedade.x + adicionarNovaPropriedade.width,
				adicionarNovaPropriedade.y + adicionarNovaPropriedade.height / 2);
		prGraphics.drawLine(adicionarNovaPropriedade.x + adicionarNovaPropriedade.width / 2, adicionarNovaPropriedade.y,
				adicionarNovaPropriedade.x + adicionarNovaPropriedade.width / 2,
				adicionarNovaPropriedade.y + adicionarNovaPropriedade.height);
		prGraphics.setColor(Color.white);

		if (adicionarNovaPropriedade.contains(Gerador.quadrado.x, Gerador.quadrado.y)) {
			w1 = prGraphics.getFontMetrics().stringWidth("Adicionar nova propriedade");
			prGraphics.drawString("Adicionar nova propriedade", adicionarNovaPropriedade.x + w1 / 2,
					adicionarNovaPropriedade.y);
		}

	}

	@Override
	public boolean clicou(int x, int y) {
		if (adicionarNovaPropriedade.contains(x, y)) {
			System.out.println("Adicionar nova propriedade!");
			return true;
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {

		return true;
	}

	@Override
	public String getNome() {
		return "Setar/Adicionar propriedades";
	}

}
