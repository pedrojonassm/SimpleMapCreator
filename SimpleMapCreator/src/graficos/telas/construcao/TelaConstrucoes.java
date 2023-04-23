package graficos.telas.construcao;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graficos.Ui;
import graficos.telas.Tela;
import main.Gerador;
import main.Uteis;
import world.Build;

public class TelaConstrucoes implements Tela {

	private Rectangle quadradoOpcoes;
	private ArrayList<Build> construcoes;
	private int index_construcao_selecionada = -1, maxItensPagina, pagina;
	public static TelaConstrucoes instance;

	public TelaConstrucoes() {
		instance = this;
		pagina = 0;

		quadradoOpcoes = new Rectangle(Ui.caixinha_dos_sprites.width, Gerador.quadrado.height / 3);

	}

	@Override
	public void posicionarRetangulos() {
		quadradoOpcoes.x = Ui.caixinha_dos_sprites.x;
		definirQuadradoOpcoesY(null);
		maxItensPagina = (Ui.caixinha_dos_sprites.height - quadradoOpcoes.y) / quadradoOpcoes.height;
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
		if (Ui.mostrar)
			prGraphics.drawRect(Ui.caixinha_dos_sprites.x + 50, Ui.caixinha_dos_sprites.y + 10,
					Ui.caixinha_dos_sprites.width - 100, 150);
		for (int i = 0; i < maxItensPagina && (i + pagina * maxItensPagina) < construcoes.size(); i++) {
			if (Ui.mostrar) {
				if (index_construcao_selecionada == i) {
					prGraphics.setColor(Color.blue);
					prGraphics.drawImage(construcoes.get(i).getImage(), Ui.caixinha_dos_sprites.x + 50,
							Ui.caixinha_dos_sprites.y + 10, Ui.caixinha_dos_sprites.width - 100 + 1, 150 + 1, null);
				} else {
					prGraphics.setColor(Color.red);
				}
				definirQuadradoOpcoesY(i);
				prGraphics.drawRect(quadradoOpcoes.x, quadradoOpcoes.y, quadradoOpcoes.width, quadradoOpcoes.height);
				prGraphics.setColor(Color.white);
				prGraphics.drawString(construcoes.get(i + pagina * maxItensPagina).getFile().getName(),
						quadradoOpcoes.x + quadradoOpcoes.height, quadradoOpcoes.y + (2 * quadradoOpcoes.height) / 3);
			}
		}
		if (index_construcao_selecionada >= 0 && index_construcao_selecionada < construcoes.size()
				&& (!Ui.caixinha_dos_sprites.contains(Gerador.quadrado.x, Gerador.quadrado.y) || !Ui.mostrar)) {
			int[] localDesenho = Uteis.calcularPosicaoSemAlturaIgnorandoCamera(Gerador.instance.getPos());
			Build lBuild = construcoes.get(index_construcao_selecionada);
			prGraphics.drawImage(lBuild.getImage(), localDesenho[0], localDesenho[1], null);
		}
	}

	@Override
	public boolean clicou(int x, int y) {
		for (int i = 0; i < maxItensPagina && (i + pagina * maxItensPagina) < construcoes.size(); i++) {
			definirQuadradoOpcoesY(i);
			if (quadradoOpcoes.contains(x, y)) {
				if (i + pagina * maxItensPagina == index_construcao_selecionada)
					index_construcao_selecionada = -1;
				else
					index_construcao_selecionada = i + pagina * maxItensPagina;

				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		for (int i = 0; i < maxItensPagina && (i + pagina * maxItensPagina) < construcoes.size(); i++) {
			definirQuadradoOpcoesY(i);
			if (quadradoOpcoes.contains(x, y)) {
				Build lBuild = construcoes.get(i + pagina * maxItensPagina);
				if (lBuild != null) {
					removerConstrucao(lBuild);
					lBuild.delete();
					index_construcao_selecionada = -1;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (Ui.caixinha_dos_sprites.contains(x, y)) {
			pagina += prRodinha;
			if (pagina < 0) {
				pagina = construcoes.size() / maxItensPagina;
				if (pagina > 0 && pagina * maxItensPagina >= construcoes.size())
					pagina--;
			} else if (pagina >= construcoes.size() / maxItensPagina) {
				pagina = 0;
			}
		}
		return false;
	}

	public void adicionar_construcao(Build b) {
		construcoes.add(b);
	}

	public void removerConstrucao(Build prBuild) {
		construcoes.remove(prBuild);
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

	@Override
	public Tela getSubTela() {
		return null;
	}

}
