package graficos.telas.configuracao.subtelas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import graficos.Ui;
import graficos.telas.Tela;
import main.Gerador;
import world.Camera;
import world.Tile;
import world.World;

public class SubTelaEscada implements Tela {

	private final String[] opcoes = { "colisao", "clique direito", "Buraco aberto", "Buraco fechado" };
	private Rectangle[] escadas;
	public int modo_escadas, escadas_direction;
	private Rectangle direcao_escadas;

	public static SubTelaEscada instance;

	public SubTelaEscada() {
		instance = this;
		modo_escadas = escadas_direction = 0;
		escadas = new Rectangle[opcoes.length];
		for (int i = 0; i < escadas.length; i++) {
			escadas[i] = new Rectangle(Ui.caixinha_dos_sprites.x,
					Ui.caixinha_dos_sprites.y + Ui.caixinha_dos_sprites.height / 4 + (i % Ui.maxItensPagina) * 20,
					Ui.caixinha_dos_sprites.width, 20);
		}
		direcao_escadas = new Rectangle(escadas[0].x + escadas[0].width / 2 - Gerador.TS / 2,
				escadas[0].y + Gerador.TS * 2, Gerador.TS, Gerador.TS);
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {
		for (int i = 0; i < escadas.length; i++) {
			prGraphics.setColor(Color.red);
			if (modo_escadas == i)
				prGraphics.setColor(Color.green);
			prGraphics.drawRect(escadas[i].x, escadas[i].y, escadas[i].width, escadas[i].height);
			prGraphics.setColor(Color.white);
			prGraphics.drawString(opcoes[i], Ui.caixinha_dos_sprites.x + 20,
					Ui.caixinha_dos_sprites.y + 15 + Ui.caixinha_dos_sprites.height / 4 + (i % Ui.maxItensPagina) * 20);
		}
		prGraphics.drawImage(Ui.setas[escadas_direction], direcao_escadas.x, direcao_escadas.y, direcao_escadas.width,
				direcao_escadas.height, null);
		prGraphics.drawString("Direção", direcao_escadas.x, direcao_escadas.y + direcao_escadas.height + 20);
	}

	@Override
	public boolean clicou(int x, int y) {
		for (int i = 0; i < escadas.length; i++) {
			if (escadas[i].contains(x, y)) {
				modo_escadas = i;
				return true;
			}
		}
		if (direcao_escadas.contains(x, y))
			return true;
		if (modo_escadas >= 0 && modo_escadas < opcoes.length) {
			Tile lTile = World.pegarAdicionarTileMundo(x + Camera.x, y + Camera.y, Gerador.player.getZ());
			if (lTile != null) {
				if (lTile.getStairs_type() == 0)
					lTile.virar_escada();
				else
					lTile.desvirar_escada();
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		Tile lTile = World.pegarAdicionarTileMundo(x + Camera.x, y + Camera.y, Gerador.player.getZ());
		if (lTile != null && lTile.getStairs_type() > 0) {
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
		}
		return false;
	}

	@Override
	public String getNome() {
		return "Setar escadas";
	}

}
