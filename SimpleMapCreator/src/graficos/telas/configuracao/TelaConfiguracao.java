package graficos.telas.configuracao;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graficos.Ui;
import graficos.telas.Tela;
import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import graficos.telas.configuracao.subtelas.SubTelaTransporte;
import main.Gerador;

public class TelaConfiguracao implements Tela {

	private Rectangle quadradoOpcoes;
	private int opcao, maxItensPagina;
	private Rectangle voltar;
	private ArrayList<Tela> subTelas;
	public static TelaConfiguracao instance;

	public TelaConfiguracao() {
		instance = this;
		opcao = -1;

		voltar = new Rectangle(Gerador.VariavelX / 2, Gerador.VariavelY / 2);
		subTelas = new ArrayList<>();
		subTelas.add(new SubTelaTransporte());
		subTelas.add(new SubTelaPropriedade());
		quadradoOpcoes = new Rectangle(Ui.caixinha_dos_sprites.width, Gerador.VariavelY / 3);

	}

	@Override
	public void posicionarRetangulos() {
		voltar.x = Ui.caixinha_dos_sprites.width - Gerador.VariavelX * 4 / 6;
		voltar.y = Ui.caixinha_dos_sprites.y + Gerador.VariavelY / 4;
		quadradoOpcoes.x = Ui.caixinha_dos_sprites.x;
		definirQuadradoOpcoesY(null);
		maxItensPagina = (Ui.caixinha_dos_sprites.height - quadradoOpcoes.y) / quadradoOpcoes.height;
		for (Tela iTela : subTelas)
			iTela.posicionarRetangulos();

	}

	private void definirQuadradoOpcoesY(Integer prMultiplicador) {
		if (prMultiplicador != null)
			quadradoOpcoes.y = Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4
					+ (prMultiplicador % maxItensPagina) * quadradoOpcoes.height;
		else
			quadradoOpcoes.y = Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4;
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
				definirQuadradoOpcoesY(i);
				prGraphics.drawRect(quadradoOpcoes.x, quadradoOpcoes.y, quadradoOpcoes.width, quadradoOpcoes.height);
				prGraphics.setColor(Color.white);
				prGraphics.drawString(subTelas.get(i).getNome(), quadradoOpcoes.x + quadradoOpcoes.height,
						quadradoOpcoes.y + (2 * quadradoOpcoes.height) / 3);
			}
		} else {
			subTelas.get(opcao).render(prGraphics);
			prGraphics.drawImage(Ui.setas[2], voltar.x, voltar.y, voltar.width, voltar.height, null);
		}

	}

	@Override
	public boolean clicou(int x, int y) {
		if (opcao < 0 || opcao >= subTelas.size()) {
			for (int i = 0; i < subTelas.size(); i++) {
				definirQuadradoOpcoesY(i);
				if (quadradoOpcoes.contains(x, y)) {
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
		if (opcao >= 0 && opcao < subTelas.size()) {
			return subTelas.get(opcao).cliquedireito(x, y);
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (opcao >= 0 && opcao < subTelas.size()) {
			return subTelas.get(opcao).trocar_pagina(x, y, prRodinha);
		}
		return false;
	}

	@Override
	public String getNome() {
		return "Configurações";
	}

	@Override
	public Tela getSubTela() {
		if (opcao == -1)
			return null;
		return subTelas.get(opcao);
	}

}
