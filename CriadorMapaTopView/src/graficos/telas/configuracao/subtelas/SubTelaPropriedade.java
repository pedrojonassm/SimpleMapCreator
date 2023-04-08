package graficos.telas.configuracao.subtelas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import graficos.Ui;
import graficos.telas.Tela;
import main.Gerador;
import world.Tile;

public class SubTelaPropriedade implements Tela {

	private Rectangle adicionarNovaPropriedade, quadradoOpcoes;
	private int maxPropriedadesPagina, pagina;
	private ArrayList<String> aCoPropriedades;

	private String aPropriedadeSelecionada, aValorPropriedade;

	public static SubTelaPropriedade instance;

	public SubTelaPropriedade() {
		instance = this;
		pagina = 0;
		quadradoOpcoes = new Rectangle(Ui.caixinha_dos_sprites.width, Gerador.quadrado.height / 3);
		quadradoOpcoes.x = Ui.caixinha_dos_sprites.x;
		definirQuadradoOpcoesY(null);
		maxPropriedadesPagina = (Ui.caixinha_dos_sprites.height - quadradoOpcoes.y) / quadradoOpcoes.height;

		adicionarNovaPropriedade = new Rectangle(
				Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width - Gerador.quadrado.width / 2,
				Ui.caixinha_dos_sprites.y + Gerador.quadrado.height, Gerador.quadrado.width / 3,
				Gerador.quadrado.height / 3);

		aCoPropriedades = new ArrayList<>();
		aCoPropriedades.add("Solid");
		aCoPropriedades.add("Speed");

		aValorPropriedade = "";
	}

	public void mudarValor(char novo) {
		aValorPropriedade += novo;
	}

	public void retirarValor() {
		if (!aValorPropriedade.isEmpty())
			aValorPropriedade = aValorPropriedade.substring(0, aValorPropriedade.length() - 1);
	}

	private void definirQuadradoOpcoesY(Integer prMultiplicador) {
		if (prMultiplicador != null)
			quadradoOpcoes.y = Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 6
					+ (prMultiplicador % maxPropriedadesPagina) * quadradoOpcoes.height;
		else
			quadradoOpcoes.y = Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 6;
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

		if (aPropriedadeSelecionada != null) {
			w1 = prGraphics.getFontMetrics().stringWidth(aPropriedadeSelecionada + " = " + aValorPropriedade);
			prGraphics.drawString(aPropriedadeSelecionada + " = " + aValorPropriedade, Gerador.quadrado.x - w1 / 2,
					Gerador.quadrado.y + Gerador.quadrado.height / 2);
		}

		if (adicionarNovaPropriedade.contains(Gerador.quadrado.x, Gerador.quadrado.y)) {
			w1 = prGraphics.getFontMetrics().stringWidth("Adicionar nova propriedade");
			prGraphics.drawString("Adicionar nova propriedade", adicionarNovaPropriedade.x + w1 / 2,
					adicionarNovaPropriedade.y);
		}

		for (int i = 0; (i + pagina * maxPropriedadesPagina) < aCoPropriedades.size()
				&& i < maxPropriedadesPagina; i++) {

			definirQuadradoOpcoesY(i);

			if (aPropriedadeSelecionada != null
					&& aCoPropriedades.get(i + pagina * maxPropriedadesPagina).contentEquals(aPropriedadeSelecionada))
				prGraphics.setColor(Color.green);
			else
				prGraphics.setColor(Color.red);
			prGraphics.drawRect(quadradoOpcoes.x, quadradoOpcoes.y, quadradoOpcoes.width, quadradoOpcoes.height);
			prGraphics.setColor(Color.white);
			prGraphics.drawString(aCoPropriedades.get(i + pagina * maxPropriedadesPagina),
					quadradoOpcoes.x + quadradoOpcoes.height, quadradoOpcoes.y + (2 * quadradoOpcoes.height) / 3);
		}

	}

	@Override
	public boolean clicou(int x, int y) {
		if (adicionarNovaPropriedade.contains(x, y)) {
			String lNome;
			do {
				lNome = JOptionPane.showInputDialog("Insira um nome para a nova Propriedade");
				if (lNome != null && !lNome.isBlank()) {
					if (!aCoPropriedades.contains(lNome))
						aCoPropriedades.add(lNome);
				}
			} while (lNome == null);
			return true;
		} else {
			for (int i = 0; (i + pagina * maxPropriedadesPagina) < aCoPropriedades.size()
					&& i < maxPropriedadesPagina; i++) {
				definirQuadradoOpcoesY(i);
				if (quadradoOpcoes.contains(x, y)) {
					if (aPropriedadeSelecionada != null && aCoPropriedades.get(i + pagina * maxPropriedadesPagina)
							.contentEquals(aPropriedadeSelecionada))
						aPropriedadeSelecionada = null;
					else
						aPropriedadeSelecionada = aCoPropriedades.get(i + pagina * maxPropriedadesPagina);
					aValorPropriedade = "";
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		for (int i = 0; (i + pagina * maxPropriedadesPagina) < aCoPropriedades.size()
				&& i < maxPropriedadesPagina; i++) {
			definirQuadradoOpcoesY(i);
			if (quadradoOpcoes.contains(x, y)) {
				if (JOptionPane.showConfirmDialog(null, "tem certeza que deseja apagar essa propriedade?") == 0) {
					aCoPropriedades.remove(i + pagina * maxPropriedadesPagina);
					aPropriedadeSelecionada = null;
					aValorPropriedade = "";
				}
				return true;
			}
		}
		return false;
	}

	public String getPropriedadeSelecionada() {
		return aPropriedadeSelecionada;
	}

	public void adicionarPropriedadeTile(Tile prTile) {
		prTile.addPropriedade(aPropriedadeSelecionada, aValorPropriedade);
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (Ui.caixinha_dos_sprites.contains(x, y)) {
			pagina += prRodinha;
			if (pagina < 0) {
				pagina = aCoPropriedades.size() / maxPropriedadesPagina;
				if (pagina > 0 && pagina * maxPropriedadesPagina >= aCoPropriedades.size())
					pagina--;
			} else if (pagina >= aCoPropriedades.size() / maxPropriedadesPagina) {
				pagina = 0;
			}
		}
		return true;
	}

	@Override
	public String getNome() {
		return "Setar/Adicionar propriedades";
	}

	@Override
	public Tela getSubTela() {
		return null;
	}

	public String getValorPropriedade() {
		return aValorPropriedade;
	}

	public void setValorPropriedade(String aValorPropriedade) {
		this.aValorPropriedade = aValorPropriedade;
	}

}
