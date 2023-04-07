package graficos.telas.cidadescasas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graficos.Ui;
import graficos.telas.Tela;
import main.Gerador;
import world.Cidade;

public class TelaCidadeCasa implements Tela {
	private ArrayList<Cidade> cidades;
	private Cidade cidade_selecionada;

	private Rectangle adicionar_nova_cidade, voltar;

	public static TelaCidadeCasa instance;

	public TelaCidadeCasa() {
		instance = this;

		cidades = new ArrayList<Cidade>();

		adicionar_nova_cidade = new Rectangle(Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width - 40,
				Ui.caixinha_dos_sprites.y + 80, 20, 20);
		voltar = new Rectangle(Ui.caixinha_dos_sprites.x + 20, adicionar_nova_cidade.y, adicionar_nova_cidade.width,
				adicionar_nova_cidade.height);
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {
		int w1;
		prGraphics.setColor(Color.green);
		prGraphics.drawRect(adicionar_nova_cidade.x, adicionar_nova_cidade.y, adicionar_nova_cidade.width,
				adicionar_nova_cidade.height);
		prGraphics.drawLine(adicionar_nova_cidade.x, adicionar_nova_cidade.y + adicionar_nova_cidade.height / 2,
				adicionar_nova_cidade.x + adicionar_nova_cidade.width,
				adicionar_nova_cidade.y + adicionar_nova_cidade.height / 2);
		prGraphics.drawLine(adicionar_nova_cidade.x + adicionar_nova_cidade.width / 2, adicionar_nova_cidade.y,
				adicionar_nova_cidade.x + adicionar_nova_cidade.width / 2,
				adicionar_nova_cidade.y + adicionar_nova_cidade.height);
		prGraphics.setColor(Color.white);
		if (cidade_selecionada == null) {
			prGraphics.drawString("CIDADES:", Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width / 2
					- prGraphics.getFontMetrics().stringWidth("CIDADES:") / 2, 60);
		} else {
			desenhar_casas(prGraphics);
		}

		if (adicionar_nova_cidade.contains(Gerador.quadrado.x, Gerador.quadrado.y)) {
			w1 = prGraphics.getFontMetrics().stringWidth("Criar nova cidade");
			prGraphics.drawString("Criar nova cidade", adicionar_nova_cidade.x + w1 / 2, adicionar_nova_cidade.y);
		}
	}

	@Override
	public boolean clicou(int x, int y) {
		if (adicionar_nova_cidade.contains(x, y)) {
			System.out.println("Adicionar nova cidade!");
			return true;
		} else if (voltar.contains(x, y) && cidade_selecionada != null) {
			cidade_selecionada = null;
			return true;
		} else if (Ui.caixinha_dos_sprites.contains(x, y)) {
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
		return false;
	}

	private void desenhar_casas(Graphics g) {
		g.drawString(cidade_selecionada.getNome(), Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width / 2
				- g.getFontMetrics().stringWidth(cidade_selecionada.getNome()) / 2, 60);
		g.drawImage(Ui.setas[2], voltar.x, voltar.y, voltar.width, voltar.height, null);
	}

	@Override
	public String getNome() {
		return "Cidades e Casas";
	}

}
