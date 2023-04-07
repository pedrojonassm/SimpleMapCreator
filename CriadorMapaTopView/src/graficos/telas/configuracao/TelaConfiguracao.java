package graficos.telas.configuracao;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graficos.Ui;
import graficos.telas.Tela;
import graficos.telas.configuracao.subtelas.SubTelaEscada;
import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import graficos.telas.configuracao.subtelas.SubTelaVelocidade;
import main.Gerador;

public class TelaConfiguracao implements Tela {

	private Rectangle[] opcoes;
	private int opcao;
	private Rectangle voltar;
	private ArrayList<Tela> subTelas;
	public static TelaConfiguracao instance;

	public TelaConfiguracao() {
		instance = this;
		opcao = -1;

		voltar = new Rectangle(Ui.caixinha_dos_sprites.width - Gerador.TS * 4 / 6,
				Ui.caixinha_dos_sprites.y + Gerador.TS / 4, Gerador.TS / 2, Gerador.TS / 2);
		subTelas = new ArrayList<>();
		subTelas.add(new SubTelaEscada());
		subTelas.add(new SubTelaVelocidade());
		subTelas.add(new SubTelaPropriedade());
		opcoes = new Rectangle[subTelas.size()];
		for (int i = 0; i < subTelas.size(); i++) {
			opcoes[i] = new Rectangle(Ui.caixinha_dos_sprites.x,
					Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4 + (i % Ui.maxItensPagina) * 20,
					Ui.caixinha_dos_sprites.width, 20);
		}
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {
		if (opcao == -1 || opcao >= subTelas.size()) {
			// renderizar opcoes para ir nas subtelas
			for (int i = 0; i < subTelas.size(); i++) {
				prGraphics.setColor(Color.green);
				prGraphics.drawRect(opcoes[i].x, opcoes[i].y, opcoes[i].width, opcoes[i].height);
				prGraphics.setColor(Color.white);
				prGraphics.drawString(subTelas.get(i).getNome(), Ui.caixinha_dos_sprites.x + 20,
						Ui.caixinha_dos_sprites.y + Gerador.TS / 10 + Ui.caixinha_dos_sprites.height / 4
								+ (i % Ui.maxItensPagina) * 20);
			}
		} else {
			subTelas.get(opcao).render(prGraphics);
			prGraphics.drawImage(Ui.setas[2], voltar.x, voltar.y, voltar.width, voltar.height, null);
		}

	}

	@Override
	public boolean clicou(int x, int y) {
		if (opcao < 0 || opcao >= opcoes.length) {
			for (int i = 0; i < opcoes.length; i++) {
				if (opcoes[i].contains(x, y)) {
					opcao = i;
					return true;
				}
			}
		} else if (voltar.contains(x, y)) {
			opcao = -1;
			return true;
		} else {
			return subTelas.get(opcao).clicou(x, y);
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		if (opcao >= 0 && opcao < opcoes.length) {
			return subTelas.get(opcao).cliquedireito(x, y);
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (opcao >= 0 && opcao < opcoes.length) {
			return subTelas.get(opcao).trocar_pagina(x, y, prRodinha);
		}
		return false;
	}

	public int getOpcao() {
		return opcao;
	}

	@Override
	public String getNome() {
		return "Configurações";
	}
}
