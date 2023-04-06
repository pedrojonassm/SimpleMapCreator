package graficos.telas.sprites;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import files.salvarCarregar;
import graficos.ConjuntoSprites;
import graficos.Ui;
import graficos.telas.Tela;
import graficos.telas.sprites.subtelas.SubTelaMultiplosSprites;
import graficos.telas.sprites.subtelas.SubTelaPreSets;
import main.Gerador;
import world.World;

public class TelaSprites implements Tela {

	private ArrayList<Integer> pagina, max_pagina, comecar_por, atual, sprites;
	private Rectangle caixinha_dos_livros;
	private static ArrayList<String> nome_livros;
	private int max_sprites_por_pagina, livro, pagina_livros, max_pagina_livros, max_livros_por_pagina, livro_tile_pego,
			index_tile_pego, salvar_nesse_livro, aTela;
	public static int tiles_nivel, max_tiles_nivel;
	private static ArrayList<ArrayList<ConjuntoSprites>> conjuntos_salvos;
	public static ArrayList<Integer> sprite_selecionado, array, lista; // esses dois pegam a imagem na lista de imagens
																		// estáticas World.sprites.get(array)[lista]
	private ArrayList<Tela> subTelas;

	private Rectangle trocarSubTela;

	public static TelaSprites instance;

	public TelaSprites() {
		aTela = 0;
		instance = this;
		livro = 0;
		pagina_livros = 0;
		tiles_nivel = 0;
		max_tiles_nivel = 4;
		nome_livros = new ArrayList<String>();
		nome_livros.add("todos os sprites");
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
		sprite_selecionado = new ArrayList<Integer>();
		array = new ArrayList<Integer>();
		lista = new ArrayList<Integer>();
		subTelas = new ArrayList<>();
		subTelas.add(new SubTelaPreSets());
		subTelas.add(new SubTelaMultiplosSprites());
		caixinha_dos_livros = new Rectangle(Gerador.quadrado.width / 3, Ui.caixinha_dos_sprites.height);
		max_sprites_por_pagina = (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width)
				* (Ui.caixinha_dos_sprites.height / Gerador.quadrado.width);
		trocarSubTela = new Rectangle(subTelas.size() * (Gerador.quadrado.height / 3), Gerador.quadrado.height / 3);
		posicionarRetangulos();
		max_livros_por_pagina = caixinha_dos_livros.height / caixinha_dos_livros.width;

	}

	private void posicionarRetangulos() {
		caixinha_dos_livros.x = Ui.caixinha_dos_sprites.x + Ui.caixinha_dos_sprites.width;
		caixinha_dos_livros.y = Ui.caixinha_dos_sprites.y;
		trocarSubTela.x = Gerador.WIDTH - Gerador.TS * 2 - (subTelas.size() * 20) / 2;
		trocarSubTela.y = Gerador.HEIGHT / (Gerador.quadrado.width / 4);
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
		} else {
			if (subTelas.get(aTela).clicou(x, y))
				return true;
		}
		return false;
	}

	@Override
	public void tick() {

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
		String tile_nivel = "Nível nos tiles: ";
		w1 = prGraphics.getFontMetrics().stringWidth(tile_nivel + (tiles_nivel + 1));
		prGraphics.drawString(tile_nivel + (tiles_nivel + 1),
				Ui.futuro_local_altura.x - w1 + Ui.futuro_local_altura.width,
				Gerador.HEIGHT - Ui.futuro_local_altura.y);
		w1 = prGraphics.getFontMetrics().stringWidth("Trocar");
		prGraphics.drawRect(trocarSubTela.x, trocarSubTela.y, trocarSubTela.width, trocarSubTela.height);
		prGraphics.setColor(Color.red);
		prGraphics.fillRect(trocarSubTela.x + (trocarSubTela.width / subTelas.size()) * aTela, trocarSubTela.y,
				trocarSubTela.width / subTelas.size(), trocarSubTela.height);
		prGraphics.setColor(Color.white);
		prGraphics.drawString("Trocar", trocarSubTela.x + trocarSubTela.width / 2 - w1 / 2, trocarSubTela.y + 15);

		subTelas.get(aTela).render(prGraphics);
	}

	public void max_pagina_por_total_de_sprites(int total_sprites) {
		int divisao = ((Ui.caixinha_dos_sprites.width / Gerador.quadrado.width)
				* (Ui.caixinha_dos_sprites.height / Gerador.quadrado.width));
		max_pagina.set(0, (int) (total_sprites / divisao));
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
			g.drawString(nome, Gerador.quadrado.x + Gerador.quadrado.width, Gerador.quadrado.y + 10);
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
		int atual = 0, sprites = 0;
		for (sprites = 0; sprites < World.sprites_do_mundo.size() && atual < comecar_por.get(livro); sprites++) {
			if (World.sprites_do_mundo.get(sprites).length <= comecar_por.get(livro) - atual) {
				atual += World.sprites_do_mundo.get(sprites).length;
			} else {
				atual += comecar_por.get(livro) - atual;
				break;
			}
		}
		for (int i = 0; i < sprites; i++) {
			atual -= World.sprites_do_mundo.get(i).length;
		}
		this.atual.set(livro, atual);
		this.sprites.set(livro, sprites);
	}

	private void desenhar_sprites_a_selecionar(Graphics g) {
		int desenhando = 0, k = atual.get(livro), spr = sprites.get(livro);
		if (livro == 0)
			while (spr < World.sprites_do_mundo.size()) {
				desenhar_no_quadrado(World.sprites_do_mundo.get(spr)[k], desenhando, g);
				k++;
				desenhando++;
				if (k >= World.sprites_do_mundo.get(spr).length) {
					spr++;
					k = 0;
				}
				if (desenhando >= max_sprites_por_pagina) {
					break;
				}
			}
		else {
			ArrayList<ConjuntoSprites> tiles = conjuntos_salvos.get(livro - 1);
			int x, y;
			for (int i = 0; i < max_sprites_por_pagina
					&& i + (max_sprites_por_pagina * pagina.get(livro)) < tiles.size(); i++) {
				x = desenhando % (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width);
				y = desenhando / (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width);
				ArrayList<BufferedImage> lDesenhoAtual = tiles.get(i + (max_sprites_por_pagina * pagina.get(livro)))
						.obterSprite_atual();
				for (BufferedImage iBufferedImage : lDesenhoAtual)
					g.drawImage(iBufferedImage, x * Gerador.quadrado.width + Ui.caixinha_dos_sprites.x,
							y * Gerador.quadrado.width + Ui.caixinha_dos_sprites.y, Gerador.quadrado.width,
							Gerador.quadrado.height, null);

				if (i + (max_sprites_por_pagina * pagina.get(livro)) == index_tile_pego && livro == livro_tile_pego) {
					g.setColor(new Color(0, 255, 0, 50));
					g.fillRect(x * Gerador.quadrado.width + Ui.caixinha_dos_sprites.x,
							y * Gerador.quadrado.width + Ui.caixinha_dos_sprites.y, Gerador.quadrado.width,
							Gerador.quadrado.height);
				}

				k++;
				desenhando++;
			}
			if (desenhando < max_sprites_por_pagina) {
				// desenhar o "+" para adicionar um novo sprite
				x = (desenhando % (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width)) * Gerador.quadrado.width
						+ Ui.caixinha_dos_sprites.x;
				y = (desenhando / (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width)) * Gerador.quadrado.width
						+ Ui.caixinha_dos_sprites.y;
				g.setColor(Color.green);
				g.drawRect(x, y, Gerador.quadrado.width, Gerador.quadrado.height);
				g.drawLine(x + Gerador.quadrado.width / 2, y + Gerador.quadrado.width / 5,
						x + Gerador.quadrado.width / 2, y + Gerador.quadrado.height - Gerador.quadrado.width / 5);
				g.drawLine(x + Gerador.quadrado.width / 5, y + Gerador.quadrado.height / 2,
						x + Gerador.quadrado.width - Gerador.quadrado.width / 5, y + Gerador.quadrado.height / 2);
			}
		}
	}

	private void desenhar_no_quadrado(BufferedImage bufferedImage, int desenhando, Graphics g) {
		int x = desenhando % (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width),
				y = desenhando / (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width);
		g.drawImage(bufferedImage, x * Gerador.quadrado.width + Ui.caixinha_dos_sprites.x,
				y * Gerador.quadrado.width + Ui.caixinha_dos_sprites.y, Gerador.quadrado.width, Gerador.quadrado.height,
				null);
		if (sprite_selecionado.contains(desenhando + max_sprites_por_pagina * pagina.get(livro))) {
			g.setColor(new Color(0, 255, 0, 50));
			g.fillRect(x * Gerador.quadrado.width + Ui.caixinha_dos_sprites.x,
					y * Gerador.quadrado.width + Ui.caixinha_dos_sprites.y, Gerador.quadrado.width,
					Gerador.quadrado.height);
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
		int px = x / Gerador.quadrado.width, py = (y - Ui.caixinha_dos_sprites.y) / Gerador.quadrado.height;
		int aux = px + py * (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width);
		if (livro == 0) {
			if (!Gerador.control && sprite_selecionado.contains(aux + max_sprites_por_pagina * pagina.get(livro))) {
				sprite_selecionado.remove((Integer) (aux + max_sprites_por_pagina * pagina.get(livro)));
				int k = atual.get(livro), spr = sprites.get(livro), desenhando = 0;
				while (spr < World.sprites_do_mundo.size()) {
					if (desenhando == aux) {
						array.remove((Integer) spr);
						;
						lista.remove((Integer) k);
						;
						break;
					}
					k++;
					if (k >= World.sprites_do_mundo.get(spr).length) {
						spr++;
						k = 0;
					}
					desenhando++;
				}
			} else {
				sprite_selecionado.add(aux + max_sprites_por_pagina * pagina.get(livro));
				int k = atual.get(livro), spr = sprites.get(livro), desenhando = 0;
				while (spr < World.sprites_do_mundo.size()) {
					if (desenhando == aux) {
						array.add(spr);
						lista.add(k);
						break;
					}
					k++;
					if (k >= World.sprites_do_mundo.get(spr).length) {
						spr++;
						k = 0;
					}
					desenhando++;
				}
			}
		} else {
			aux = aux + (max_sprites_por_pagina * pagina.get(livro));
			if (aux == conjuntos_salvos.get(livro - 1).size() && sprite_selecionado.size() > 0) {
				// clicou no "+"
				adicionar_novo_tile_ao_livro(livro);

			} else if (aux < conjuntos_salvos.get(livro - 1).size()) {
				livro_tile_pego = livro;
				index_tile_pego = aux;
				conjuntos_salvos.get(livro - 1).get(aux).pegarsprites();
			}
		}
		Gerador.sprite_selecionado_index = 0;
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
		if (array.size() == 0)
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
		salvarCarregar.salvar_livro(livro - 1);
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		int k = 0;
		if (prRodinha > 0)
			k = 1;
		else
			k = -1;
		if (Ui.caixinha_dos_sprites.contains(x, y)) {
			if (Gerador.control) {
				if (sprite_selecionado.size() != 0) {
					if (encontrarProximoSpriteSelecionado())
						atualizar_caixinha();
					return true;
				}
			}
			pagina.set(livro, pagina.get(livro) + k);
			if (pagina.get(livro) < 0) {
				pagina.set(livro, max_pagina.get(livro));
			} else if (pagina.get(livro) > max_pagina.get(livro)) {
				pagina.set(livro, 0);
			}
			atualizar_caixinha();
			return true;
		} else if (caixinha_dos_livros.contains(x, y)) {
			pagina_livros += k;
			if (pagina_livros < 0) {
				pagina_livros = max_pagina_livros;
			} else if (pagina_livros > max_pagina_livros) {
				pagina_livros = 0;
			}
			return true;
		} else if (trocarSubTela.contains(x, y)) {
			aTela += k;
			if (aTela >= subTelas.size())
				aTela = 0;
			else if (aTela < 0)
				aTela = subTelas.size() - 1;

			return true;
		} else {

			if (subTelas.get(aTela).trocar_pagina(x, y, prRodinha))
				return true;

			if (!Gerador.control && !Gerador.shift) {
				trocar_Nivel(prRodinha);
				return true;
			}
		}
		return false;
	}

	private boolean encontrarProximoSpriteSelecionado() {
		int lProximo = 0;
		for (int i = 0; i < max_sprites_por_pagina; i++) {
			int lPos = pagina.get(0) * max_sprites_por_pagina + i;
			if (sprite_selecionado.contains(lPos)) {
				lProximo = i + 1;
			}
		}

		if (lProximo >= sprite_selecionado.size())
			lProximo = 0;

		int lNovaPagina = sprite_selecionado.get(lProximo) / max_sprites_por_pagina;
		if (pagina.get(0) == lNovaPagina)
			return false;

		pagina.set(0, lNovaPagina);
		return true;
	}

	public void trocar_Nivel(int prWheelRotation) {
		if (prWheelRotation > 0) {
			tiles_nivel++;
			if (tiles_nivel > max_tiles_nivel) {
				tiles_nivel = 0;
			}
		} else if (prWheelRotation < 0) {
			tiles_nivel--;
			if (tiles_nivel < 0) {
				tiles_nivel = max_tiles_nivel;
			}
		}
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		if (Ui.mostrar) {
			if (Ui.caixinha_dos_sprites.contains(x, y)) {
				if (sprite_selecionado.size() > 0) {
					sprite_selecionado.clear();
					array.clear();
					lista.clear();
					livro_tile_pego = -1;
					index_tile_pego = -1;
				} else if (livro > 0) {
					int px = x / Gerador.quadrado.width, py = (y - Ui.caixinha_dos_sprites.y) / Gerador.quadrado.height;
					int aux = px + py * (Ui.caixinha_dos_sprites.width / Gerador.quadrado.width)
							+ (max_sprites_por_pagina * pagina.get(livro));
					if (conjuntos_salvos.get(livro - 1).size() > aux) {
						if (JOptionPane.showConfirmDialog(null, "tem certeza que deseja apagar esse sprite?") == 0) {
							conjuntos_salvos.get(livro - 1).remove(aux);
							salvarCarregar.salvar_livro(livro - 1);
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

	public static void pegar_tile_ja_colocado(ArrayList<int[]> sprites) {
		sprite_selecionado.clear();
		array.clear();
		lista.clear();
		for (int[] a : sprites) {
			array.add(a[0]);
			lista.add(a[1]);
			adicionar_sprite_colocado_aos_selecionados(a[0], a[1]);
		}
		Gerador.sprite_selecionado_index = 0;
	}

	private static void adicionar_sprite_colocado_aos_selecionados(int array, int lista) {
		int k = 0;
		for (int i = 0; i < array; i++) {
			k += World.sprites_do_mundo.get(i).length;
		}
		k += lista;
		sprite_selecionado.add(k);
	}

	public boolean getMultiplosSprites() {
		return subTelas.get(aTela) instanceof SubTelaMultiplosSprites;
	}

}
