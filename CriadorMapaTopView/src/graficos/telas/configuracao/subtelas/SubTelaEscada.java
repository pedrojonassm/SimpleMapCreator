package graficos.telas.configuracao.subtelas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graficos.Ui;
import graficos.telas.Tela;
import main.Gerador;
import world.Camera;
import world.Tile;
import world.World;

public class SubTelaEscada implements Tela {

	private ArrayList<String> opcoes;
	private Rectangle quadradoOpcoes, direcao_escadas;
	public int modo_escadas, escadas_direction, pagina, maxItensPagina;

	public static SubTelaEscada instance;

	public SubTelaEscada() {
		instance = this;
		opcoes = new ArrayList<>();
		opcoes.add("colisao");
		opcoes.add("clique direito");
		opcoes.add("Buraco aberto");
		opcoes.add("Buraco fechado");
		modo_escadas = escadas_direction = pagina = 0;

		quadradoOpcoes = new Rectangle(Ui.caixinha_dos_sprites.width, 20);
		quadradoOpcoes.x = Ui.caixinha_dos_sprites.x;
		definirQuadradoOpcoesY(null);
		maxItensPagina = (Ui.caixinha_dos_sprites.height - quadradoOpcoes.y) / quadradoOpcoes.height;

		direcao_escadas = new Rectangle(quadradoOpcoes.x + quadradoOpcoes.width / 2 - Gerador.TS / 2,
				quadradoOpcoes.y - Gerador.TS * 2, Gerador.TS, Gerador.TS);
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {
		for (int i = 0; (i + pagina * maxItensPagina) < opcoes.size() && i < maxItensPagina; i++) {

			definirQuadradoOpcoesY(i);

			prGraphics.setColor(Color.red);
			if (modo_escadas == i)
				prGraphics.setColor(Color.green);

			prGraphics.drawRect(quadradoOpcoes.x, quadradoOpcoes.y, quadradoOpcoes.width, quadradoOpcoes.height);
			prGraphics.setColor(Color.white);
			prGraphics.drawString(opcoes.get(i + pagina * maxItensPagina), quadradoOpcoes.x + quadradoOpcoes.height,
					quadradoOpcoes.y + (2 * quadradoOpcoes.height) / 3);
		}

		prGraphics.drawImage(Ui.setas[escadas_direction], direcao_escadas.x, direcao_escadas.y, direcao_escadas.width,
				direcao_escadas.height, null);
		prGraphics.drawString("Direção", direcao_escadas.x, direcao_escadas.y + direcao_escadas.height + 20);
	}

	private void definirQuadradoOpcoesY(Integer prMultiplicador) {
		if (prMultiplicador != null)
			quadradoOpcoes.y = Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4
					+ (prMultiplicador % maxItensPagina) * quadradoOpcoes.height;
		else
			quadradoOpcoes.y = Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4;
	}

	@Override
	public boolean clicou(int x, int y) {
		if (Ui.caixinha_dos_sprites.contains(x, y)) {
			if (direcao_escadas.contains(x, y))
				return true;

			for (int i = 0; (i + pagina * maxItensPagina) < opcoes.size() && i < maxItensPagina; i++) {
				definirQuadradoOpcoesY(i);
				if (quadradoOpcoes.contains(x, y)) {
					modo_escadas = i + pagina * maxItensPagina;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		Tile lTile = World.pegarAdicionarTileMundo(x + Camera.x, y + Camera.y, Gerador.player.getZ());
		if (Gerador.shift && lTile != null && lTile.getStairs_type() > 0) {
			Gerador.player.utilizarEscada(lTile);
			return true;
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (direcao_escadas.contains(x, y)) {
			escadas_direction += prRodinha;
			if (escadas_direction < 0)
				escadas_direction = Ui.setas.length - 1;
			else if (escadas_direction >= Ui.setas.length)
				escadas_direction = 0;
			return true;
		} else if (Ui.caixinha_dos_sprites.contains(x, y)) {
			pagina += prRodinha;
			if (pagina < 0) {
				pagina = opcoes.size() / maxItensPagina;
				if (pagina > 0 && pagina * maxItensPagina >= opcoes.size())
					pagina--;
			} else if (pagina >= opcoes.size() / maxItensPagina) {
				pagina = 0;
			}
		}
		return false;
	}

	@Override
	public String getNome() {
		return "Setar escadas";
	}

	@Override
	public Tela getSubTela() {
		return null;
	}

}
