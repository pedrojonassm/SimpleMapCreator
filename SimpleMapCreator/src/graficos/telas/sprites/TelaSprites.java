package graficos.telas.sprites;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import files.SalvarCarregar;
import graficos.ConjuntoSprites;
import graficos.Ui;
import graficos.telas.Sprite;
import graficos.telas.Tela;
import graficos.telas.sprites.subtelas.SubTelaMultiplosSprites;
import graficos.telas.sprites.subtelas.SubTelaPreSets;
import main.Gerador;
import world.World;

public class TelaSprites implements Tela {

	public enum kdModoColocar {
		kdFullTile, kdLayerToLayer
	}

	private ArrayList<Integer> pagina, max_pagina, comecar_por, atual, sprites;

	private Rectangle caixinha_dos_livros, trocarSubTela, trocarModoColocar;
	private static ArrayList<String> nome_livros;
	private int max_sprites_por_pagina, livro, pagina_livros, max_pagina_livros, max_livros_por_pagina, livro_tile_pego,
			index_tile_pego, salvar_nesse_livro, aTela, aModoColocar, posicaoCliqueInicial, minPosicaoCliqueInicial,
			paginaCliqueInicial, posicaoCliqueAtual;
	public static int LayerLevel, max_tiles_nivel;
	private static ArrayList<ArrayList<ConjuntoSprites>> conjuntos_salvos;
	public ArrayList<ArrayList<Integer>> spriteSelecionado, spriteSelecionadoTemp, PosicaoSprite, PosicaoSpriteTemp;
	public ArrayList<ArrayList<String>> nomeSpritesheet, nomeSpritesheetTemp;

	private String[] aModosColocar;
	private ArrayList<Tela> subTelas;
	private String tile_nivel;

	public static TelaSprites instance;

	public TelaSprites() {
		aTela = 0;
		instance = this;
		livro = 0;
		pagina_livros = 0;
		LayerLevel = 0;
		max_tiles_nivel = Gerador.aConfig.getTotalLayers();
		aModosColocar = new String[2];
		aModoColocar = 0;
		for (int i = 0; i < 2; i++)
			aModosColocar[i] = Gerador.aConfig.getNomeModosSprites().get(i);
		nome_livros = new ArrayList<String>();
		nome_livros.add("todos os sprites");
		tile_nivel = Gerador.aConfig.getNomeTilesNivel();
		salvar_nesse_livro = 0;
		pagina = new ArrayList<Integer>();
		max_pagina = new ArrayList<Integer>();
		comecar_por = new ArrayList<Integer>();
		atual = new ArrayList<Integer>();
		sprites = new ArrayList<Integer>();
		pagina.add(0);
		max_pagina.add(0);
		comecar_por.add(0);
		atual.add(0);
		sprites.add(0);
		conjuntos_salvos = new ArrayList<ArrayList<ConjuntoSprites>>();
		spriteSelecionado = new ArrayList<ArrayList<Integer>>(max_tiles_nivel);
		nomeSpritesheet = new ArrayList<ArrayList<String>>(max_tiles_nivel);
		PosicaoSprite = new ArrayList<ArrayList<Integer>>(max_tiles_nivel);
		spriteSelecionadoTemp = new ArrayList<ArrayList<Integer>>(max_tiles_nivel);
		nomeSpritesheetTemp = new ArrayList<ArrayList<String>>(max_tiles_nivel);
		PosicaoSpriteTemp = new ArrayList<ArrayList<Integer>>(max_tiles_nivel);
		for (int i = 0; i < max_tiles_nivel; i++) {
			spriteSelecionado.add(i, new ArrayList<Integer>());
			nomeSpritesheet.add(i, new ArrayList<String>());
			PosicaoSprite.add(i, new ArrayList<Integer>());
			spriteSelecionadoTemp.add(i, new ArrayList<Integer>());
			nomeSpritesheetTemp.add(i, new ArrayList<String>());
			PosicaoSpriteTemp.add(i, new ArrayList<Integer>());
		}
		subTelas = new ArrayList<>();
		subTelas.add(new SubTelaPreSets());
		subTelas.add(new SubTelaMultiplosSprites());
		caixinha_dos_livros = new Rectangle(Gerador.VariavelX / 3, Ui.caixinha_dos_sprites.height);
		max_sprites_por_pagina = (Ui.caixinha_dos_sprites.width / Gerador.VariavelX)
				* (Ui.caixinha_dos_sprites.height / Gerador.VariavelX);
		trocarSubTela = new Rectangle(subTelas.size() * (Gerador.VariavelX / 3), Gerador.VariavelY / 3);
		max_livros_por_pagina = caixinha_dos_livros.height / caixinha_dos_livros.width;
		trocarModoColocar = new Rectangle(kdModoColocar.values().length * (Gerador.VariavelX / 3),
				Gerador.VariavelY / 3);
	}

	@Override
	public void posicionarRetangulos() {
		caixinha_dos_livros.x = Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width;
		caixinha_dos_livros.y = Ui.caixinha_dos_sprites.y;
		trocarSubTela.x = Gerador.windowWidth - Gerador.VariavelX * 2 - (subTelas.size() * Gerador.VariavelX / 3);
		trocarSubTela.y = Gerador.windowHEIGHT / (Gerador.VariavelY / 4);
		trocarModoColocar.x = caixinha_dos_livros.x + caixinha_dos_livros.width * 2;
		trocarModoColocar.y = caixinha_dos_livros.y + caixinha_dos_livros.width;
		for (Tela iTela : subTelas)
			iTela.posicionarRetangulos();
	}

	@Override
	public boolean clicou(int x, int y) {
		if (caixinha_dos_livros.contains(x, y)) {
			trocar_livro(x, y);
			return true;
		} else if (Ui.caixinha_dos_sprites.contains(x, y)) {
			pegar_ou_retirar_sprite_selecionado(x, y);
			return true;
		} else if (trocarSubTela.contains(x, y)) {
			aTela++;
			if (aTela >= subTelas.size())
				aTela = 0;
			return true;
		} else if (trocarModoColocar.contains(x, y)) {
			aModoColocar++;
			if (aModoColocar >= kdModoColocar.values().length)
				aModoColocar = 0;
			return true;
		} else {
			if (subTelas.get(aTela).clicou(x, y))
				return true;
		}
		return false;
	}

	@Override
	public void tick() {
		if (Gerador.ui.cliqueUi && livro == 0
				&& Ui.caixinha_dos_sprites.contains(Gerador.quadrado.x, Gerador.quadrado.y)) {
			int px = Gerador.quadrado.x / Gerador.VariavelX,
					py = (Gerador.quadrado.y - Ui.caixinha_dos_sprites.y) / Gerador.VariavelY;
			int aux = px + py * (Ui.caixinha_dos_sprites.width / Gerador.VariavelX);
			int posicaoAtual = aux + max_sprites_por_pagina * pagina.get(0);

			if (posicaoCliqueAtual != posicaoAtual) {
				posicaoCliqueAtual = posicaoAtual;
				spriteSelecionadoTemp.get(LayerLevel).clear();
				nomeSpritesheetTemp.get(LayerLevel).clear();
				PosicaoSpriteTemp.get(LayerLevel).clear();
				int k = 1;
				if (posicaoAtual < posicaoCliqueInicial)
					k = -1;
				aux = posicaoCliqueInicial; //
				while (aux != posicaoAtual + k) {
					pegar_ou_retirar_sprite_selecionado(aux);
					aux += k;
				}
			}

		}
	}

	@Override
	public void render(Graphics prGraphics) {
		int w1;

		prGraphics.setColor(Color.white);
		prGraphics.drawRect(caixinha_dos_livros.x, caixinha_dos_livros.y, caixinha_dos_livros.width,
				caixinha_dos_livros.height);
		desenhar_livros(prGraphics);
		desenhar_sprites_a_selecionar(prGraphics);
		if (caixinha_dos_livros.contains(Gerador.quadrado.x, Gerador.quadrado.y))
			mostrar_nome_livro(prGraphics);

		prGraphics.setColor(Color.white);
		prGraphics.drawRect(trocarModoColocar.x, trocarModoColocar.y, trocarModoColocar.width,
				trocarModoColocar.height);
		if (trocarModoColocar.contains(Gerador.quadrado.x, Gerador.quadrado.y)) {
			prGraphics.drawString(Gerador.aConfig.getNomeModo(), trocarModoColocar.x,
					trocarModoColocar.y + trocarModoColocar.height * 2);
		}

		w1 = prGraphics.getFontMetrics().stringWidth(tile_nivel + (LayerLevel + 1));
		prGraphics.drawString(tile_nivel + (LayerLevel + 1), Ui.local_altura.x - w1 + Ui.local_altura.width,
				Ui.local_altura.y + (Ui.local_altura.height * 2) / 3);
		w1 = prGraphics.getFontMetrics().stringWidth(Gerador.aConfig.getNomeTrocar());
		prGraphics.drawRect(trocarSubTela.x, trocarSubTela.y, trocarSubTela.width, trocarSubTela.height);
		prGraphics.setColor(Color.red);
		prGraphics.fillRect(trocarSubTela.x + (trocarSubTela.width / subTelas.size()) * aTela, trocarSubTela.y,
				trocarSubTela.width / subTelas.size(), trocarSubTela.height);
		prGraphics.setColor(Color.white);
		prGraphics.drawString(Gerador.aConfig.getNomeTrocar(), trocarSubTela.x + trocarSubTela.width / 2 - w1 / 2,
				trocarSubTela.y + (trocarSubTela.height * 3) / 4);

		w1 = prGraphics.getFontMetrics().stringWidth(aModosColocar[aModoColocar]);
		prGraphics.drawString(aModosColocar[aModoColocar], trocarModoColocar.x + trocarModoColocar.width / 2 - w1 / 2,
				trocarModoColocar.y + (trocarModoColocar.height * 3) / 4);

		subTelas.get(aTela).render(prGraphics);
	}

	public void max_pagina_por_total_de_sprites(int total_sprites) {
		int divisao = ((Ui.caixinha_dos_sprites.width / Gerador.VariavelX)
				* (Ui.caixinha_dos_sprites.height / Gerador.VariavelY));
		max_pagina.set(0, max_pagina.get(0) + ((int) (total_sprites / divisao)));
	}

	public static ArrayList<ConjuntoSprites> pegar_livro(int index) {
		return conjuntos_salvos.get(index);
	}

	private void mostrar_nome_livro(Graphics g) {
		g.setColor(Color.white);
		int py = (Gerador.quadrado.y - caixinha_dos_livros.y) / caixinha_dos_livros.width
				+ pagina_livros * max_livros_por_pagina;
		if (py >= max_livros_por_pagina * (pagina_livros + 1))
			return;
		String nome = null;
		if (py < nome_livros.size()) {
			nome = nome_livros.get(py);
		} else if (py == nome_livros.size()) {
			nome = "Adicionar novo livro";
		}
		if (nome != null) {
			g.drawString(nome, Gerador.quadrado.x + Gerador.VariavelX, Gerador.quadrado.y + Gerador.VariavelY / 6);
		}
	}

	private void desenhar_livros(Graphics prGraphics) {
		int y = caixinha_dos_livros.y;
		prGraphics.setColor(Color.blue);
		int i;
		for (i = max_livros_por_pagina * pagina_livros; i < max_livros_por_pagina * (pagina_livros + 1)
				&& i < nome_livros.size(); i++) {
			if (i == livro) {
				prGraphics.setColor(Color.red);
				prGraphics.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
				prGraphics.setColor(Color.blue);
			} else if (i == salvar_nesse_livro && i != 0) {
				prGraphics.setColor(Color.green);
				prGraphics.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
				prGraphics.setColor(Color.blue);
			} else {
				prGraphics.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
			}
			y += caixinha_dos_livros.width;
		}
		if (i < max_livros_por_pagina * (pagina_livros + 1)) {
			prGraphics.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
			prGraphics.setColor(Color.green);
			prGraphics.drawLine(caixinha_dos_livros.x + caixinha_dos_livros.width / 2, y,
					caixinha_dos_livros.x + caixinha_dos_livros.width / 2, y + caixinha_dos_livros.width);
			prGraphics.drawLine(caixinha_dos_livros.x, y + caixinha_dos_livros.width / 2,
					caixinha_dos_livros.x + caixinha_dos_livros.width, y + caixinha_dos_livros.width / 2);
		}
	}

	public void atualizar_caixinha() {
		comecar_por.set(livro, pagina.get(livro) * max_sprites_por_pagina);
		int atual = 0, posicao = 0, iSprites = 0;
		for (iSprites = 0; iSprites < World.nomeSprites.size(); iSprites++) {
			if (World.spritesCarregados.get(World.nomeSprites.get(iSprites)).length <= comecar_por.get(livro) - atual)
				atual += World.spritesCarregados.get(World.nomeSprites.get(iSprites)).length;
			else {
				posicao = comecar_por.get(livro) - atual;
				break;
			}
		}

		this.atual.set(livro, posicao);
		this.sprites.set(livro, iSprites);
	}

	private void desenhar_sprites_a_selecionar(Graphics g) {
		int lDesenhando = 0, lAtual = atual.get(livro), spr = sprites.get(livro);
		if (livro == 0)
			while (spr < World.nomeSprites.size() && World.spritesCarregados.get(World.nomeSprites.get(spr)) != null) {
				desenhar_no_quadrado(World.spritesCarregados.get(World.nomeSprites.get(spr))[lAtual], lDesenhando, g);
				lAtual++;
				lDesenhando++;
				if (lAtual >= World.spritesCarregados.get(World.nomeSprites.get(spr)).length) {
					spr++;
					lAtual = 0;
				}
				if (lDesenhando >= max_sprites_por_pagina) {
					break;
				}
			}
		else {
			ArrayList<ConjuntoSprites> tiles = conjuntos_salvos.get(livro - 1);
			int x, y;
			for (int i = 0; i < max_sprites_por_pagina
					&& i + (max_sprites_por_pagina * pagina.get(livro)) < tiles.size(); i++) {
				x = lDesenhando % (Ui.caixinha_dos_sprites.width / Gerador.VariavelX);
				y = lDesenhando / (Ui.caixinha_dos_sprites.width / Gerador.VariavelY);
				ArrayList<BufferedImage> lDesenhoAtual = tiles.get(i + (max_sprites_por_pagina * pagina.get(livro)))
						.obterSprite_atual();
				for (BufferedImage iBufferedImage : lDesenhoAtual)
					g.drawImage(iBufferedImage, x * Gerador.VariavelX + Ui.caixinha_dos_sprites.x,
							y * Gerador.VariavelY + Ui.caixinha_dos_sprites.y, Gerador.VariavelX, Gerador.VariavelY,
							null);

				if (i + (max_sprites_por_pagina * pagina.get(livro)) == index_tile_pego && livro == livro_tile_pego) {
					g.setColor(new Color(0, 255, 0, 50));
					g.fillRect(x * Gerador.VariavelX + Ui.caixinha_dos_sprites.x,
							y * Gerador.VariavelY + Ui.caixinha_dos_sprites.y, Gerador.VariavelX, Gerador.VariavelY);
				}

				lAtual++;
				lDesenhando++;
			}
			if (lDesenhando < max_sprites_por_pagina) {
				// desenhar o "+" para adicionar um novo sprite
				x = (lDesenhando % (Ui.caixinha_dos_sprites.width / Gerador.VariavelX)) * Gerador.VariavelX
						+ Ui.caixinha_dos_sprites.x;
				y = (lDesenhando / (Ui.caixinha_dos_sprites.width / Gerador.VariavelX)) * Gerador.VariavelX
						+ Ui.caixinha_dos_sprites.y;
				g.setColor(Color.green);
				g.drawRect(x, y, Gerador.VariavelX, Gerador.VariavelY);
				g.drawLine(x + Gerador.VariavelX / 2, y + Gerador.VariavelY / 5, x + Gerador.VariavelX / 2,
						y + Gerador.VariavelY - Gerador.VariavelY / 5);
				g.drawLine(x + Gerador.VariavelX / 5, y + Gerador.VariavelY / 2,
						x + Gerador.VariavelX - Gerador.VariavelY / 5, y + Gerador.VariavelY / 2);
			}
		}
	}

	private void desenhar_no_quadrado(BufferedImage bufferedImage, int desenhando, Graphics g) {
		int x = desenhando % (Ui.caixinha_dos_sprites.width / Gerador.VariavelX),
				y = desenhando / (Ui.caixinha_dos_sprites.width / Gerador.VariavelY);
		g.drawImage(bufferedImage, x * Gerador.VariavelX + Ui.caixinha_dos_sprites.x,
				y * Gerador.VariavelY + Ui.caixinha_dos_sprites.y, Gerador.VariavelX, Gerador.VariavelY, null);
		for (int iTilesLayer = 0; iTilesLayer < spriteSelecionadoTemp.size(); iTilesLayer++) {
			if (kdModoColocar.kdLayerToLayer.equals(getModoColocar()) && iTilesLayer != LayerLevel)
				continue;
			if (spriteSelecionadoTemp.get(iTilesLayer).contains(desenhando + max_sprites_por_pagina * pagina.get(livro))
					|| spriteSelecionado.get(iTilesLayer)
							.contains(desenhando + max_sprites_por_pagina * pagina.get(livro))) {
				g.setColor(new Color(0, 255, 0, 50));
				g.fillRect(x * Gerador.VariavelX + Ui.caixinha_dos_sprites.x,
						y * Gerador.VariavelY + Ui.caixinha_dos_sprites.y, Gerador.VariavelX, Gerador.VariavelY);
			}
		}
	}

	private void trocar_livro(int x, int y) {
		int py = (y - caixinha_dos_livros.y) / caixinha_dos_livros.width + pagina_livros * max_livros_por_pagina;
		if (py == pagina.size()) {
			String nome = null;
			do {
				nome = JOptionPane.showInputDialog("Insira um nome que já não seja um nome do livro");
				if (nome == null || nome.isBlank())
					return;
			} while (nome_livros.contains(nome));
			adicionar_livro(nome);
		} else if (py < pagina.size()) {
			livro = py;
			atualizar_caixinha();
		}
	}

	private void adicionar_livro(String nome) {
		pagina.add(0);
		max_pagina.add(0);
		comecar_por.add(0);
		atual.add(0);
		sprites.add(0);
		conjuntos_salvos.add(new ArrayList<ConjuntoSprites>());
		nome_livros.add(nome);
		max_pagina_livros = nome_livros.size() / max_livros_por_pagina;
	}

	private void pegar_ou_retirar_sprite_selecionado(int x, int y) {
		int px = x / Gerador.VariavelX, py = (y - Ui.caixinha_dos_sprites.y) / Gerador.VariavelY;
		int posicaoAuxiliar = px + py * (Ui.caixinha_dos_sprites.width / Gerador.VariavelX);
		posicaoCliqueInicial = posicaoAuxiliar + max_sprites_por_pagina * pagina.get(livro);
		paginaCliqueInicial = pagina.get(livro);
		posicaoCliqueAtual = posicaoCliqueInicial;
		minPosicaoCliqueInicial = atual.get(livro);
		pegar_ou_retirar_sprite_selecionado(posicaoAuxiliar + max_sprites_por_pagina * pagina.get(livro));

	}

	private void pegar_ou_retirar_sprite_selecionado(int posicaoCalculada) {
		if (livro == 0) {
			int posicao = posicaoCalculada - max_sprites_por_pagina * paginaCliqueInicial;
			int posicaoVerificar = minPosicaoCliqueInicial, spristesWorld = sprites.get(livro);
			if (posicao < 0) {
				int pagesToSubtract = ((posicao * -1) / max_sprites_por_pagina) + 1;
				posicaoVerificar -= pagesToSubtract * max_sprites_por_pagina;
				if (posicaoVerificar < 0) {
					spristesWorld--;
					if (spristesWorld < 0)
						spristesWorld = World.nomeSprites.size() - 1;
					posicaoVerificar = World.spritesCarregados.get(World.nomeSprites.get(spristesWorld)).length
							+ posicaoVerificar;
				}
				posicao = posicaoCalculada - max_sprites_por_pagina * (paginaCliqueInicial - pagesToSubtract);
			}
			boolean lContem = false;
			if (!Gerador.control) {
				for (int iTileNivel = 0; iTileNivel < spriteSelecionado.size(); iTileNivel++) {
					if (kdModoColocar.kdLayerToLayer.equals(getModoColocar()) && iTileNivel != LayerLevel)
						continue;
					ArrayList<Integer> iSprites = spriteSelecionado.get(iTileNivel);
					if (iSprites.contains(posicaoCalculada)) {
						lContem = true;
						iSprites.remove((Integer) (posicaoCalculada));
						int k = posicaoVerificar, spr = spristesWorld, desenhando = 0;
						while (spr < World.nomeSprites.size()) {
							if (desenhando == posicao) {
								nomeSpritesheetTemp.get(iTileNivel).remove(World.nomeSprites.get(spr));
								PosicaoSpriteTemp.get(iTileNivel).remove((Integer) k);

								break;
							}
							k++;
							if (k >= World.spritesCarregados.get(World.nomeSprites.get(spr)).length) {
								spr++;
								k = 0;
							}
							desenhando++;
						}
					}
				}
			}
			if (Gerador.control || !lContem) {
				spriteSelecionadoTemp.get(LayerLevel).add(posicaoCalculada);
				int k = posicaoVerificar, spr = spristesWorld, desenhando = 0;
				while (spr < World.nomeSprites.size()) {
					if (desenhando == posicao) {
						nomeSpritesheetTemp.get(LayerLevel).add(World.nomeSprites.get(spr));
						PosicaoSpriteTemp.get(LayerLevel).add(k);
						break;
					}
					k++;
					if (k >= World.spritesCarregados.get(World.nomeSprites.get(spr)).length) {
						spr++;
						k = 0;
					}
					desenhando++;
				}
			}

		} else {
			if (posicaoCalculada == conjuntos_salvos.get(livro - 1).size()) {
				// clicou no "+"
				if (contemSpritesSelecionados())
					adicionar_novo_tile_ao_livro(livro);

			} else if (posicaoCalculada < conjuntos_salvos.get(livro - 1).size()) {
				livro_tile_pego = livro;
				index_tile_pego = posicaoCalculada;
				conjuntos_salvos.get(livro - 1).get(posicaoCalculada).pegarsprites();
			}
		}
		Gerador.sprite_selecionado_index = 0;
	}

	public Boolean contemSpritesSelecionados() {
		for (ArrayList<Integer> iSprites : spriteSelecionado) {
			if (iSprites.size() > 0)
				return true;

		}
		return false;
	}

	public Boolean contemSpritesSelecionadosTemp() {
		for (ArrayList<Integer> iSprites : spriteSelecionadoTemp) {
			if (iSprites.size() > 0)
				return true;

		}
		return false;
	}

	public void selecionar_livro() {
		if (salvar_nesse_livro != livro) {
			salvar_nesse_livro = livro;
			if (salvar_nesse_livro != 0)
				JOptionPane.showMessageDialog(null, "Você irá salvar as coisas em: " + nome_livros.get(livro));
			return;
		} else {
			adicionar_novo_tile_ao_livro(salvar_nesse_livro);
		}
	}

	private void adicionar_novo_tile_ao_livro(int livro2) {
		if (nomeSpritesheetTemp.size() == 0)
			return;
		if (livro2 == 0 && salvar_nesse_livro == 0) {
			JOptionPane.showMessageDialog(null,
					"Primeiro você precisa selecionar um livro! Vá até o livro e aperte '+'");
			return;
		}
		ConjuntoSprites lConjuntoSprites = new ConjuntoSprites();
		lConjuntoSprites.adicionar_sprite_selecionado();
		conjuntos_salvos.get(livro2 - 1).add(lConjuntoSprites);
		if (conjuntos_salvos.get(livro2 - 1).size() >= max_sprites_por_pagina) {
			max_pagina.set(livro2, max_pagina.get(livro2) + 1);
		}
		SalvarCarregar.salvar_livro(livro - 1);
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (Ui.caixinha_dos_sprites.contains(x, y)) {
			if (Gerador.control) {
				if (contemSpritesSelecionados()) {
					if (encontrarProximoSpriteSelecionado())
						atualizar_caixinha();
					return true;
				}
			}
			if (Gerador.shift) {
				trocarLayerTiles(prRodinha);
				return true;
			}

			pagina.set(livro, pagina.get(livro) + prRodinha);
			if (pagina.get(livro) < 0) {
				pagina.set(livro, max_pagina.get(livro));
			} else if (pagina.get(livro) > max_pagina.get(livro)) {
				pagina.set(livro, 0);
			}
			atualizar_caixinha();

			return true;
		} else if (caixinha_dos_livros.contains(x, y)) {
			pagina_livros += prRodinha;
			if (pagina_livros < 0) {
				pagina_livros = max_pagina_livros;
			} else if (pagina_livros > max_pagina_livros) {
				pagina_livros = 0;
			}
			return true;
		} else if (trocarSubTela.contains(x, y)) {
			aTela += prRodinha;
			if (aTela >= subTelas.size())
				aTela = 0;
			else if (aTela < 0)
				aTela = subTelas.size() - 1;

			return true;
		} else {

			if (subTelas.get(aTela).trocar_pagina(x, y, prRodinha))
				return true;

			if (!Gerador.control && !Gerador.shift) {
				trocarLayerTiles(prRodinha);
				return true;
			}
		}
		return false;
	}

	private boolean encontrarProximoSpriteSelecionado() {
		int[] lProximo = { 0, 0 };
		for (int i = 0; i < max_sprites_por_pagina; i++) {
			int lPos = pagina.get(0) * max_sprites_por_pagina + i;
			for (int iTilesLayer = 0; iTilesLayer < spriteSelecionado.size(); iTilesLayer++) {
				if (kdModoColocar.kdLayerToLayer.equals(getModoColocar()) && iTilesLayer != LayerLevel)
					continue;
				ArrayList<Integer> iSprites = spriteSelecionado.get(iTilesLayer);
				if (iSprites.contains(lPos)) {
					lProximo[0] = iTilesLayer;
					lProximo[1] = spriteSelecionado.get(iTilesLayer).indexOf(lPos) + 1;
				}
			}

		}

		while (lProximo[1] >= spriteSelecionado.get(lProximo[0]).size()) {
			lProximo[0] = lProximo[0] + 1;
			lProximo[1] = 0;
			if (lProximo[0] >= spriteSelecionado.size()) {
				lProximo[0] = 0;
			}
		}

		int lNovaPagina = spriteSelecionado.get(lProximo[0]).get(lProximo[1]) / max_sprites_por_pagina;
		if (pagina.get(0) == lNovaPagina)
			return false;

		pagina.set(0, lNovaPagina);
		return true;
	}

	public void trocarLayerTiles(int prWheelRotation) {
		if (prWheelRotation > 0) {
			LayerLevel++;
			if (LayerLevel >= max_tiles_nivel) {
				LayerLevel = 0;
			}
		} else if (prWheelRotation < 0) {
			LayerLevel--;
			if (LayerLevel < 0) {
				LayerLevel = max_tiles_nivel - 1;
			}
		}
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		if (Ui.mostrar) {
			if (Ui.caixinha_dos_sprites.contains(x, y)) {
				if (contemSpritesSelecionadosTemp() || contemSpritesSelecionados()) {
					for (int i = 0; i < spriteSelecionadoTemp.size(); i++) {
						if (getModoColocar().equals(kdModoColocar.kdLayerToLayer) && i != LayerLevel)
							continue;
						spriteSelecionadoTemp.get(i).clear();
						nomeSpritesheetTemp.get(i).clear();
						PosicaoSpriteTemp.get(i).clear();
						spriteSelecionado.get(i).clear();
						nomeSpritesheet.get(i).clear();
						PosicaoSprite.get(i).clear();
					}
					livro_tile_pego = -1;
					index_tile_pego = -1;
				} else if (livro > 0) {
					int px = x / Gerador.VariavelX, py = (y - Ui.caixinha_dos_sprites.y) / Gerador.VariavelY;
					int aux = px + py * (Ui.caixinha_dos_sprites.width / Gerador.VariavelX)
							+ (max_sprites_por_pagina * pagina.get(livro));
					if (conjuntos_salvos.get(livro - 1).size() > aux) {
						if (JOptionPane.showConfirmDialog(null, "tem certeza que deseja apagar esse sprite?") == 0) {
							conjuntos_salvos.get(livro - 1).remove(aux);
							SalvarCarregar.salvar_livro(livro - 1);
						}
					}
				}
				return true;
			} else {
				for (Tela i : subTelas) {
					if (i.cliquedireito(x, y))
						return true;
				}
			}
		}
		return false;
	}

	public void adicionar_livro_salvo(String nome, ArrayList<ConjuntoSprites> lCoConjuntoSprites) {
		adicionar_livro(nome);
		conjuntos_salvos.set(conjuntos_salvos.size() - 1, lCoConjuntoSprites);
		max_pagina.set(conjuntos_salvos.size(),
				(int) (conjuntos_salvos.get(conjuntos_salvos.size() - 1).size() / max_sprites_por_pagina));
	}

	public static String pegar_nome_livro(int index) {
		return nome_livros.get(index);
	}

	public void pegar_tile_ja_colocado(ArrayList<ArrayList<Sprite>> prSprites) {
		for (int i = 0; i < prSprites.size(); i++) {
			if (kdModoColocar.kdLayerToLayer.equals(kdModoColocar.values()[aModoColocar]) && i != LayerLevel)
				continue;
			spriteSelecionadoTemp.get(i).clear();
			nomeSpritesheetTemp.get(i).clear();
			PosicaoSpriteTemp.get(i).clear();
			ArrayList<Sprite> lSprites = prSprites.get(i);
			for (Sprite iSprite : lSprites) {
				nomeSpritesheetTemp.get(i).add(iSprite.getNome());
				PosicaoSpriteTemp.get(i).add(iSprite.getPosicao());
				int k = 0;
				for (int j = 0; j < World.nomeSprites.indexOf(iSprite.getNome()); j++) {
					k += World.spritesCarregados.get(World.nomeSprites.get(j)).length;
				}
				k += iSprite.getPosicao();
				spriteSelecionadoTemp.get(i).add(k);
			}
		}
		Gerador.sprite_selecionado_index = 0;
	}

	@Override
	public String getNome() {

		return "colocar sprites";
	}

	@Override
	public Tela getSubTela() {
		return subTelas.get(aTela);
	}

	public kdModoColocar getModoColocar() {
		if (aModoColocar >= 1)
			return kdModoColocar.kdLayerToLayer;
		else
			return kdModoColocar.kdFullTile;
	}

	public int getNumeroMaxSpritesSelecionados() {
		int lRetorno = 0;
		for (ArrayList<Integer> lista : spriteSelecionado)
			if (lista.size() > lRetorno)
				lRetorno = lista.size();
		return lRetorno;
	}

	public void endClick(int prMouseClick) {
		if (prMouseClick == 1) {
			if (contemSpritesSelecionadosTemp()) {
				for (int i = 0; i < spriteSelecionadoTemp.size(); i++) {
					spriteSelecionado.get(i).addAll(spriteSelecionadoTemp.get(i));
					nomeSpritesheet.get(i).addAll(nomeSpritesheetTemp.get(i));
					PosicaoSprite.get(i).addAll(PosicaoSpriteTemp.get(i));
					spriteSelecionadoTemp.get(i).clear();
					nomeSpritesheetTemp.get(i).clear();
					PosicaoSpriteTemp.get(i).clear();
				}
			}

		}

	}

}
