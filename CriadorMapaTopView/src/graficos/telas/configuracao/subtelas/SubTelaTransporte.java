package graficos.telas.configuracao.subtelas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import graficos.Ui;
import graficos.telas.Tela;
import main.Gerador;
import main.Uteis;
import world.Camera;
import world.Tile;
import world.World;

public class SubTelaTransporte implements Tela {

	private ArrayList<String> opcoes;
	private Rectangle quadradoOpcoes, direcaoTransporte, adicionarNovoTransporte, aIdaVolta;
	public String opcaoSelecionada;
	public int direction, pagina, maxItensPagina;

	public boolean aIsIdaVolta;

	public Tile aInicio, aDestino;

	public static SubTelaTransporte instance;

	public SubTelaTransporte() {
		instance = this;
		aIsIdaVolta = false;
		opcoes = Gerador.aConfig.getTransportes();

		opcaoSelecionada = null;
		direction = pagina = 0;

		quadradoOpcoes = new Rectangle(Ui.caixinha_dos_sprites.width, Gerador.VariavelY / 3);

		adicionarNovoTransporte = new Rectangle(
				Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width - Gerador.VariavelX / 2,
				Ui.caixinha_dos_sprites.y + Gerador.VariavelY, Gerador.VariavelX / 3, Gerador.VariavelY / 3);

		aIdaVolta = new Rectangle(Gerador.VariavelX / 3, Gerador.VariavelY / 3);

		direcaoTransporte = new Rectangle(Gerador.VariavelX, Gerador.VariavelY);
	}

	@Override
	public void posicionarRetangulos() {
		quadradoOpcoes.x = Ui.caixinha_dos_sprites.x;
		definirQuadradoOpcoesY(null);
		maxItensPagina = (Ui.caixinha_dos_sprites.height - quadradoOpcoes.y) / quadradoOpcoes.height;
		adicionarNovoTransporte.x = Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width - Gerador.VariavelX / 2;
		adicionarNovoTransporte.y = Ui.caixinha_dos_sprites.y + Gerador.VariavelY;
		direcaoTransporte.x = quadradoOpcoes.x + quadradoOpcoes.width / 2 - Gerador.VariavelX / 2;
		direcaoTransporte.y = quadradoOpcoes.y - Gerador.VariavelY * 2;
		aIdaVolta.x = Ui.caixinha_dos_sprites.x + Gerador.quadrado.width / 2;
		aIdaVolta.y = Ui.caixinha_dos_sprites.y + Gerador.quadrado.height;

	}

	public ArrayList<String> getOpcoes() {
		return opcoes;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics prGraphics) {
		int w1;
		prGraphics.setColor(Color.green);
		prGraphics.drawRect(adicionarNovoTransporte.x, adicionarNovoTransporte.y, adicionarNovoTransporte.width,
				adicionarNovoTransporte.height);
		prGraphics.drawLine(adicionarNovoTransporte.x, adicionarNovoTransporte.y + adicionarNovoTransporte.height / 2,
				adicionarNovoTransporte.x + adicionarNovoTransporte.width,
				adicionarNovoTransporte.y + adicionarNovoTransporte.height / 2);
		prGraphics.drawLine(adicionarNovoTransporte.x + adicionarNovoTransporte.width / 2, adicionarNovoTransporte.y,
				adicionarNovoTransporte.x + adicionarNovoTransporte.width / 2,
				adicionarNovoTransporte.y + adicionarNovoTransporte.height);
		prGraphics.setColor(Color.white);

		if (adicionarNovoTransporte.contains(Gerador.quadrado.x, Gerador.quadrado.y)) {
			w1 = prGraphics.getFontMetrics().stringWidth("Adicionar novo Transporte");
			prGraphics.drawString("Adicionar novo Transporte", adicionarNovoTransporte.x + w1 / 2,
					adicionarNovoTransporte.y + adicionarNovoTransporte.height / 2);
		}
		w1 = prGraphics.getFontMetrics().stringWidth("Ida e Volta");
		HashMap<String, Runnable> lHashMap = new HashMap<>();

		prGraphics.drawString("Ida e Volta", aIdaVolta.x + aIdaVolta.width / 2 - w1 / 2,
				aIdaVolta.y + (aIdaVolta.height * 2));

		for (int i = 0; (i + pagina * maxItensPagina) < opcoes.size() && i < maxItensPagina; i++) {

			definirQuadradoOpcoesY(i);

			prGraphics.setColor(Color.red);
			if (opcaoSelecionada != null && opcoes.get(i).contentEquals(opcaoSelecionada))
				prGraphics.setColor(Color.green);

			prGraphics.drawRect(quadradoOpcoes.x, quadradoOpcoes.y, quadradoOpcoes.width, quadradoOpcoes.height);
			prGraphics.setColor(Color.white);
			prGraphics.drawString(opcoes.get(i + pagina * maxItensPagina), quadradoOpcoes.x + quadradoOpcoes.height,
					quadradoOpcoes.y + (2 * quadradoOpcoes.height) / 3);
		}

		if (aIsIdaVolta) {
			prGraphics.fillRect(aIdaVolta.x, aIdaVolta.y, aIdaVolta.width, aIdaVolta.height);

			prGraphics.drawImage(Ui.setas[direction], direcaoTransporte.x, direcaoTransporte.y, direcaoTransporte.width,
					direcaoTransporte.height, null);
			prGraphics.drawString("Direção", direcaoTransporte.x,
					direcaoTransporte.y + direcaoTransporte.height + quadradoOpcoes.height);
		} else
			prGraphics.drawRect(aIdaVolta.x, aIdaVolta.y, aIdaVolta.width, aIdaVolta.height);

		if (aInicio != null) {
			int lDiferencaNivel = Gerador.player.getZ() - aInicio.getZ(), angulo = (lDiferencaNivel > 0) ? 45 : 225;
			int[] lVariacao = direcaoHorizontalVertical();
			prGraphics.drawLine(Gerador.quadrado.x + lVariacao[0] * Gerador.VariavelX,
					Gerador.quadrado.y + lVariacao[1] * Gerador.VariavelY,
					aInicio.getX() - Camera.x + Gerador.VariavelX / 2,
					aInicio.getY() + Gerador.VariavelY / 2 - Camera.y);
			for (int i = 0; i < Uteis.modulo(lDiferencaNivel); i++) {
				prGraphics.drawArc(Gerador.quadrado.x - quadradoOpcoes.height,
						Gerador.quadrado.y + (i + 1) * quadradoOpcoes.height / 4, quadradoOpcoes.height / 2,
						quadradoOpcoes.height / 2, angulo, 90);
			}
		}
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
		if (aIdaVolta.contains(x, y)) {
			aIsIdaVolta = !aIsIdaVolta;
			return true;
		} else if (adicionarNovoTransporte.contains(x, y)) {
			String lNome;
			do {
				lNome = JOptionPane.showInputDialog("Insira um nome para o Transporte");
				if (lNome == null)
					break;
				if (lNome != null && !lNome.isBlank()) {
					if (!opcoes.contains(lNome))
						opcoes.add(lNome);
				}
			} while (lNome == null);
			return true;
		} else if (Ui.caixinha_dos_sprites.contains(x, y)) {
			if (direcaoTransporte.contains(x, y))
				return true;

			for (int i = 0; (i + pagina * maxItensPagina) < opcoes.size() && i < maxItensPagina; i++) {
				definirQuadradoOpcoesY(i);
				if (quadradoOpcoes.contains(x, y)) {
					if (opcaoSelecionada != null
							&& opcoes.get(i + pagina * maxItensPagina).contentEquals(opcaoSelecionada))
						opcaoSelecionada = null;
					else
						opcaoSelecionada = opcoes.get(i + pagina * maxItensPagina);
					return true;
				}
			}
		} else {
			if (aInicio == null) {
				aInicio = World.pegarAdicionarTileMundo(x + Camera.x, y + Camera.y, Gerador.player.getZ());
				return true;
			}
			aDestino = World.pegarAdicionarTileMundo(x + Camera.x, y + Camera.y, Gerador.player.getZ());
			if (aInicio.getaPos() != aDestino.getaPos()) {
				adicionarTransporte(aInicio, aDestino);

				if (aIsIdaVolta) {
					// Direção Oposta
					int aux = direction;
					direction += Ui.setas.length / 2;
					if (direction >= Ui.setas.length)
						direction -= Ui.setas.length;

					adicionarTransporte(aDestino, aInicio);
					direction = aux;
				}
			}
			aDestino = null;
			aInicio = null;

			return true;
		}
		return false;
	}

	private int[] direcaoHorizontalVertical() {
		int[] lRetorno = { 0, 0 };
		if (aIsIdaVolta) {
			switch (direction) {
			case 0:
				lRetorno[0] = 1;
				break;
			case 1:
				lRetorno[1] = 1;
				break;
			case 2:
				lRetorno[0] = -1;
				break;

			case 3:
				lRetorno[1] = -1;
				break;
			}
		}
		return lRetorno;
	}

	public void adicionarTransporte(Tile prFrom, Tile prTo) {
		HashMap<String, Object> lHashMap = new HashMap<>();
		lHashMap.put("TYPE", opcaoSelecionada);

		int[] lVariacao = direcaoHorizontalVertical();

		List<Integer> lDestino = Tile.pegarPosicaoRelativa(prFrom.getX(), prFrom.getY(), prFrom.getZ(),
				prTo.getX() + lVariacao[0] * Gerador.VariavelX, prTo.getY() + lVariacao[1] * Gerador.VariavelY,
				prTo.getZ());

		if (World.pegarAdicionarTileMundo(
				Tile.pegarPosicaoRelativa(prFrom.getX(), prFrom.getY(), prFrom.getZ(), lDestino)) != null) {

			lHashMap.put("DESTINY", lDestino);

			prFrom.addPropriedade("TRANSPORT", lHashMap);
		}
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		Tile lTile = World.pegarAdicionarTileMundo(x + Camera.x, y + Camera.y, Gerador.player.getZ());
		if (Gerador.shift && lTile != null && lTile.getPropriedade("TRANSPORT") != null) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> lHashmap = (HashMap<String, Object>) lTile.getPropriedade("TRANSPORT");
			try {
				if (lHashmap.get("TYPE") != null && lHashmap.get("TYPE").toString().contentEquals(opcaoSelecionada))
					Gerador.player.utilizarEscada(lTile);
			} catch (Exception e) {
			}

			return true;
		}
		if (Ui.caixinha_dos_sprites.contains(x, y)) {
			for (int i = 0; (i + pagina * maxItensPagina) < opcoes.size() && i < maxItensPagina; i++) {
				definirQuadradoOpcoesY(i);
				if (quadradoOpcoes.contains(x, y)) {
					opcoes.remove(i + pagina * maxItensPagina);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (direcaoTransporte.contains(x, y)) {
			direction += prRodinha;
			if (direction < 0)
				direction = Ui.setas.length - 1;
			else if (direction >= Ui.setas.length)
				direction = 0;
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
			return true;
		}
		return false;
	}

	@Override
	public String getNome() {
		return "Setar Transporte";
	}

	@Override
	public Tela getSubTela() {
		return null;
	}

}
