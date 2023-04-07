package graficos.telas.construcao;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import graficos.Ui;
import graficos.telas.Tela;
import world.Build;

public class TelaConstrucoes implements Tela {

	private ArrayList<Build> construcoes;
	private int index_construcao_selecionada = -1;
	private static int pagina_construcoes;
	public static TelaConstrucoes instance;

	public TelaConstrucoes() {
		instance = this;
		pagina_construcoes = 0;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {
		prGraphics.drawRect(Ui.caixinha_dos_sprites.x + 50, Ui.caixinha_dos_sprites.y + 10,
				Ui.caixinha_dos_sprites.width - 100, 150);
		for (int i = pagina_construcoes * Ui.maxItensPagina; i < Ui.maxItensPagina * (pagina_construcoes + 1)
				&& i < construcoes.size(); i++) {
			// System.out.println(Ui.maxItensPagina*(pagina_construcoes+1));
			if (index_construcao_selecionada == i) {
				prGraphics.setColor(Color.blue);
				prGraphics.drawImage(construcoes.get(i).getImage(), Ui.caixinha_dos_sprites.x + 50,
						Ui.caixinha_dos_sprites.y + 10, Ui.caixinha_dos_sprites.width - 100 + 1, 150 + 1, null);
			} else {
				prGraphics.setColor(Color.red);
			}
			prGraphics.drawRect(Ui.caixinha_dos_sprites.x,
					Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4 + (i % Ui.maxItensPagina) * 20,
					Ui.caixinha_dos_sprites.width, 20);
			prGraphics.setColor(Color.white);
			prGraphics.drawString(construcoes.get(i).getFile().getName(), Ui.caixinha_dos_sprites.x + 20,
					Ui.caixinha_dos_sprites.y + 15 + Ui.caixinha_dos_sprites.height / 4 + (i % Ui.maxItensPagina) * 20);
		}
	}

	@Override
	public boolean clicou(int x, int y) {
		if (Ui.caixinha_dos_sprites.contains(x, y)) {
			int novo = (y - (Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4)) / 20;
			if (novo == index_construcao_selecionada) {
				index_construcao_selecionada = -1;
			} else if (novo >= 0 && novo < construcoes.size()) {
				index_construcao_selecionada = novo;
			}
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

	public void adicionar_construcao(Build b) {
		construcoes.add(b);
	}

	public Build pegar_construcao_selecionada() {
		if (index_construcao_selecionada == -1) {
			return null;
		}
		return construcoes.get(index_construcao_selecionada);
	}

	public void adicionar_construcoes_salvas(ArrayList<Build> construcoes2) {
		construcoes = construcoes2;
	}

	@Override
	public String getNome() {
		return "Construções";
	}

}
