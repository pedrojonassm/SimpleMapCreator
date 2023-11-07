package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import files.SalvarCarregar;
import graficos.telas.Tela;
import graficos.telas.configuracao.TelaConfiguracao;
import graficos.telas.construcao.TelaConstrucoes;
import graficos.telas.sprites.TelaSprites;
import main.Gerador;
import world.Camera;
import world.Tile;
import world.World;

public class Ui implements Tela {
	public static boolean mostrar, substituir;
	public static Rectangle caixinha_dos_sprites, local_altura, preencher_tudo, fazer_caixa, limpar_selecao,
			salvar_construcao, substitui, caixa_das_opcoes;
	private static String altura, limpar, caixa, preencher, substituira, salva_construcao;

	private int opcao;
	public static ArrayList<Tela> telas;
	public static ArrayList<Tile> aTilesSelecionados;
	private static String a_selecionar;
	public static BufferedImage[] setas, sprite_opcoes;

	private static ArrayList<Runnable> renderizarDepois;

	public boolean cliqueUi;

	public Ui() {
		cliqueUi = false;
		altura = Gerador.aConfig.getNomeAlturaUi();
		limpar = Gerador.aConfig.getNomeLimparUi();
		caixa = Gerador.aConfig.getNomeCaixaUi();
		preencher = Gerador.aConfig.getNomePreencherUi();
		substituira = Gerador.aConfig.getNomeSubstituirUi();
		salva_construcao = Gerador.aConfig.getNomeSalva_construcaoUi();
		telas = new ArrayList<>();
		carregar_sprites();
		opcao = 0;
		mostrar = substituir = true;
		renderizarDepois = new ArrayList<>();
		local_altura = new Rectangle(Gerador.VariavelX / 6, Gerador.VariavelY / 6);

		caixinha_dos_sprites = new Rectangle(Gerador.VariavelX * 5,
				Gerador.VariavelY * (Gerador.windowHEIGHT / Gerador.VariavelY));
		preencher_tudo = new Rectangle((Gerador.VariavelX * 3) / 2, Gerador.VariavelY / 3);
		substitui = new Rectangle(Gerador.VariavelX / 6, Gerador.VariavelY / 6);
		fazer_caixa = new Rectangle(preencher_tudo.width, preencher_tudo.height);
		limpar_selecao = new Rectangle(preencher_tudo.width, preencher_tudo.height);
		salvar_construcao = new Rectangle(preencher_tudo.width, preencher_tudo.height);
		aTilesSelecionados = new ArrayList<>();
		telas.add(new TelaSprites());
		telas.add(new TelaConfiguracao());
		telas.add(new TelaConstrucoes());
		// telas.add(new TelaCidadeCasa());
		caixa_das_opcoes = new Rectangle(Gerador.VariavelX * telas.size(), Gerador.VariavelY);
		posicionarRetangulos();

	}

	@Override
	public void posicionarRetangulos() {
		local_altura.x = Gerador.windowWidth - (Gerador.VariavelX * 3) / 2;
		local_altura.y = Gerador.windowHEIGHT - Gerador.VariavelY / 3;
		caixinha_dos_sprites.x = 0;
		caixinha_dos_sprites.y = Gerador.VariavelY / 8;
		preencher_tudo.x = Gerador.windowWidth - preencher_tudo.width;
		preencher_tudo.y = Gerador.windowHEIGHT / 2;
		fazer_caixa.x = Gerador.windowWidth - fazer_caixa.width;
		fazer_caixa.y = Gerador.windowHEIGHT / 2 + preencher_tudo.height;
		substitui.x = preencher_tudo.x;
		substitui.y = preencher_tudo.y - preencher_tudo.height * 3;
		limpar_selecao.x = Gerador.windowWidth - limpar_selecao.width;
		limpar_selecao.y = Gerador.windowHEIGHT / 2 - preencher_tudo.height;
		salvar_construcao.x = Gerador.windowWidth - salvar_construcao.width;
		salvar_construcao.y = fazer_caixa.y + preencher_tudo.height;
		caixa_das_opcoes.x = Gerador.windowWidth / 2 - (telas.size()) / 2 * Gerador.VariavelX;
		caixa_das_opcoes.y = 0;
		for (Tela iTela : telas)
			iTela.posicionarRetangulos();
	}

	private void carregar_sprites() {
		Spritesheet spr = new Spritesheet("/setas.png", 32, 4);
		setas = new BufferedImage[spr.getQuadradosX() * spr.getQuadradosY()];
		for (int i = 0; i < setas.length; i++) {
			setas[i] = spr.getAsset(i);
		}

		spr = new Spritesheet("/opcoes.png", 64, 0);
		sprite_opcoes = new BufferedImage[spr.getQuadradosX() * spr.getQuadradosY()];
		for (int i = 0; i < sprite_opcoes.length; i++) {
			sprite_opcoes[i] = spr.getAsset(i);
		}
	}

	public Rectangle getCaixinha_dos_sprites() {
		return caixinha_dos_sprites;
	}

	public void tick() {
		if (caixa_das_opcoes.intersects(new Rectangle(Gerador.quadrado.x, Gerador.quadrado.y - Gerador.VariavelY,
				Gerador.VariavelX, Gerador.VariavelY))) {

			a_selecionar = telas.get((Gerador.quadrado.x - caixa_das_opcoes.x) / Gerador.VariavelX).getNome();
			if (caixa_das_opcoes.y < 0) {
				caixa_das_opcoes.y++;
			}
		} else {
			a_selecionar = null;
			if (caixa_das_opcoes.y > -Gerador.VariavelX) {
				caixa_das_opcoes.y--;
			}
		}
		telas.get(opcao).tick();
	}

	public static void renderizarImagemDepois(Graphics prGraphics, BufferedImage image, int prPosX, int prPosY) {
		renderizarDepois.add(() -> prGraphics.drawImage(image, prPosX, prPosY, null));
	}

	public static void renderizarEscritaDepois(Graphics prGraphics, String prString, int prPosX, int prPosY) {
		renderizarDepois.add(() -> prGraphics.drawString(prString, prPosX, prPosY));
	}

	public static void renderizarDesenharQuadradoDepois(Graphics prGraphics, int prPosX, int prPosY, int prWidth,
			int prHeight) {
		renderizarDepois.add(() -> prGraphics.drawRect(prPosX, prPosY, prWidth, prHeight));
	}

	public static void renderizarDesenharArcoDepois(Graphics prGraphics, int prPosX, int prPosY, int prWidth,
			int prHeight, int prStartAngle, int prArcAngle) {
		renderizarDepois.add(() -> prGraphics.drawArc(prPosX, prPosY, prWidth, prHeight, prStartAngle, prArcAngle));
	}

	public void render(Graphics g) {
		int w1;

		g.setColor(new Color(255, 255, 0, 50));
		int dx, dy;

		if (opcao <= 1)
			for (Tile iTile : aTilesSelecionados) {
				if (iTile == null || iTile.getZ() != Gerador.player.getZ())
					continue;
				dx = iTile.getX() - Camera.x - (iTile.getZ() - Gerador.player.getZ()) * Gerador.quadrado.width;
				dy = iTile.getY() - Camera.y - (iTile.getZ() - Gerador.player.getZ()) * Gerador.quadrado.height;
				if (dx + Gerador.quadrado.width >= 0 && dx < Gerador.windowWidth && dy + Gerador.quadrado.height >= 0
						&& dy < Gerador.windowHEIGHT)
					g.fillRect(dx, dy, Gerador.quadrado.width, Gerador.quadrado.height);
			}

		g.setColor(Color.white);

		if (renderizarDepois.size() > 0) {

			for (Runnable iRunnable : renderizarDepois) {
				try {
					iRunnable.run();
				} catch (Exception e) {
				}

			}

			renderizarDepois.clear();
		}

		g.setColor(Color.white);
		if (mostrar) {

			if (caixa_das_opcoes.y > -caixa_das_opcoes.height) {
				if (a_selecionar != null) {
					w1 = g.getFontMetrics().stringWidth(a_selecionar);
					g.drawString(a_selecionar, caixa_das_opcoes.x + caixa_das_opcoes.width / 2 - w1 / 2,
							caixa_das_opcoes.y + sprite_opcoes[0].getWidth() + (local_altura.height * 3) / 2);
				}
				g.drawRect(caixa_das_opcoes.x, caixa_das_opcoes.y, caixa_das_opcoes.width, caixa_das_opcoes.height);
				for (int i = 0; i < telas.size(); i++) {
					g.drawImage(sprite_opcoes[i], caixa_das_opcoes.x + i * sprite_opcoes[0].getWidth(),
							caixa_das_opcoes.y, null);
					if (opcao == i) {
						g.setColor(new Color(0, 255, 0, 50));
						g.fillRect(caixa_das_opcoes.x + i * sprite_opcoes[0].getWidth(), caixa_das_opcoes.y,
								sprite_opcoes[0].getWidth(), sprite_opcoes[0].getWidth());
						g.setColor(Color.white);
					}
				}
			}

			if (aTilesSelecionados.size() > 0) {
				g.setColor(Color.green);
				if (opcao == 0 || opcao == 1) {
					desenhar_opcoes(g);
				}

				g.setColor(Color.white);
				if (substituir) {
					g.fillRect(substitui.x, substitui.y, substitui.width, substitui.height);
				} else {
					g.drawRect(substitui.x, substitui.y, substitui.width, substitui.height);
				}
				g.drawString(substituira, substitui.x + (substitui.width * 3) / 2, substitui.y + substitui.height);
			}

			g.setColor(Color.black);
			g.fillRect(caixinha_dos_sprites.x, caixinha_dos_sprites.y, caixinha_dos_sprites.width,
					caixinha_dos_sprites.height);
			g.setColor(Color.white);
			g.drawRect(caixinha_dos_sprites.x, caixinha_dos_sprites.y, caixinha_dos_sprites.width,
					caixinha_dos_sprites.height);

			telas.get(opcao).render(g);

			g.setColor(Color.white);
			w1 = g.getFontMetrics().stringWidth(altura + (Gerador.player.getZ() + 1));
			g.drawString(altura + (Gerador.player.getZ() + 1), local_altura.x - w1 + local_altura.width,
					local_altura.y - (local_altura.height * 3) / 2);
		}

		if (telas.get(opcao) instanceof TelaConstrucoes)
			telas.get(opcao).render(g);
	}

	private void desenhar_opcoes(Graphics prGraphics) {
		prGraphics.drawRect(preencher_tudo.x, preencher_tudo.y, preencher_tudo.width, preencher_tudo.height);
		prGraphics.drawRect(fazer_caixa.x, fazer_caixa.y, fazer_caixa.width, fazer_caixa.height);
		prGraphics.drawRect(limpar_selecao.x, limpar_selecao.y, limpar_selecao.width, limpar_selecao.height);
		prGraphics.drawRect(salvar_construcao.x, salvar_construcao.y, salvar_construcao.width,
				salvar_construcao.height);
		prGraphics.drawString(preencher, preencher_tudo.x, preencher_tudo.y + (local_altura.height * 3) / 2);
		prGraphics.drawString(caixa, fazer_caixa.x, fazer_caixa.y + (local_altura.height * 3) / 2);
		prGraphics.drawString(limpar, limpar_selecao.x, limpar_selecao.y + (local_altura.height * 3) / 2);
		prGraphics.drawString(salva_construcao, salvar_construcao.x,
				salvar_construcao.y + (local_altura.height * 3) / 2);
	}

	public boolean clicou(int x, int y) {
		if (!mostrar)
			return false;

		if (aTilesSelecionados.size() > 0) {
			if (substitui.contains(x, y)) {
				substituir = !substituir;
				return true;
			} else if (limpar_selecao.contains(x, y)) {
				aTilesSelecionados.clear();
				return true;
			} else if (preencher_tudo.contains(x, y)) {
				World.fill(aTilesSelecionados);
				return true;
			} else if (fazer_caixa.contains(x, y)) {
				World.empty(aTilesSelecionados);
				return true;
			} else if (salvar_construcao.contains(x, y)) {
				SalvarCarregar.salvar_construcao(aTilesSelecionados);
				return true;
			}
		}

		if (caixa_das_opcoes.contains(x, y)) {
			opcao = (x - caixa_das_opcoes.x) / Gerador.VariavelX;
			return true;
		} else if (telas.get(opcao).clicou(x, y)) {
			return true;
		}

		return false;
	}

	public boolean cliquedireito(int x, int y) {
		if (mostrar) {
			if (telas.get(opcao).cliquedireito(x, y))
				return true;
			return caixinha_dos_sprites.contains(x, y);
		}

		return false;
	}

	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (mostrar) {
			return telas.get(opcao).trocar_pagina(x, y, prRodinha);
		}
		return false;
	}

	public boolean selecionarTile(int prPos) {
		Tile t = World.pegarAdicionarTileMundo(prPos);
		if (aTilesSelecionados.size() == 0)
			aTilesSelecionados.add(t);
		else if (!removerTileLista(prPos)) {
			boolean lAdicionado = false;
			for (int i = 0; i < aTilesSelecionados.size(); i++) {
				Tile iTile = aTilesSelecionados.get(i);
				if (iTile != null && prPos < iTile.getaPos()) {
					lAdicionado = true;
					aTilesSelecionados.add(i, t);
					break;
				}
			}
			if (!lAdicionado)
				aTilesSelecionados.add(t);
		}
		return true;
	}

	private boolean removerTileLista(int prPos) {
		int lPosicaoLista = Tile.tileExisteLista(prPos, aTilesSelecionados);
		if (lPosicaoLista >= 0 && lPosicaoLista < aTilesSelecionados.size()) {
			aTilesSelecionados.remove(lPosicaoLista);
			return true;
		}
		return false;
	}

	@Override
	public String getNome() {
		return "Ui";
	}

	public Tela getTela() {
		return telas.get(opcao);
	}

	@Override
	public Tela getSubTela() {
		return null;
	}

}