package world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import files.salvarCarregar;
import graficos.Spritesheet;
import graficos.Ui;
import graficos.telas.sprites.TelaSprites;
import main.Gerador;
import main.configs.ExConfig;

public class World {

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT, HIGH;
	public static HashMap<String, BufferedImage[]> spritesCarregados;

	// chaos64, chaos128, paredes64, paredes128, itens64, itens128, escadas64,
	// escadas128
	public static int log_ts;

	public static int tiles_index, tiles_animation_time, max_tiles_animation_time;
	private static File arquivo;
	public static boolean ready, ok;

	public static int[] calcularPosicaoSemAltura(int prPos) {
		int[] retorno = { 0, 0, 0 };
		retorno[0] = (int) ((prPos % (WIDTH * HIGH)) / HIGH) * Gerador.TS - Camera.x;
		retorno[1] = (int) (prPos / HEIGHT / HIGH) * Gerador.TS - Camera.y;
		retorno[2] = (prPos % HIGH);
		return retorno;
	}

	public static int[] calcularPosicaoComAltura(int prPos) {
		int[] retorno = calcularPosicaoSemAltura(prPos);
		int lSubtract = (prPos % HIGH) * Gerador.TS;
		retorno[0] -= lSubtract;
		retorno[1] -= lSubtract;
		return retorno;
	}

	public World(File file) {
		ready = false;
		// *
		tiles_index = tiles_animation_time = 0;
		max_tiles_animation_time = 15;
		try {
			ok = true;
			if (file == null) {
				arquivo = null;
				Integer[] lTamanho = ExConfig.loadValoresPadrao();
				if (Gerador.player != null)
					lTamanho = ExConfig.determinarTmanhoMundo();
				if (lTamanho == null)
					ok = false;
				else {
					WIDTH = lTamanho[0];
					HEIGHT = lTamanho[1];
					HIGH = lTamanho[2];
					tiles = new Tile[WIDTH * HEIGHT * HIGH];
					Gerador.aConfig.mundoCarregado();
				}

			} else {
				arquivo = file.getParentFile();
				tiles = salvarCarregar.carregarMundo(file);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// */
	}

	public static void carregarSprites() {
		spritesCarregados = new HashMap<String, BufferedImage[]>();
		int maxPagina = carregarSpritesPadroes();
		TelaSprites.instance.max_pagina_por_total_de_sprites(maxPagina);
	}

	public static int carregarSpritesPadroes() {

		Spritesheet[] sprites = new Spritesheet[8];
		int[] total_de_sprites = { 36 * 40 + 16, 9, 27 * 20 - 3, 40 * 32 - 11, 40 * 23 - 16, 20 * 16 + 2, 35, 40 };
		sprites[0] = new Spritesheet("/chaos64.png", 64); // total de sprites: 36*40 + 16
		sprites[1] = new Spritesheet("/chaos128.png", 128); // total de sprites: 9
		sprites[2] = new Spritesheet("/paredes64.png", 64); // total de sprites: 27*20 - 3
		sprites[3] = new Spritesheet("/paredes128.png", 128); // total de sprites: 40*32 - 11
		sprites[4] = new Spritesheet("/itens64.png", 64); // total de sprites: 40*23 - 16
		sprites[5] = new Spritesheet("/itens128.png", 128); // total de sprites: 20*16 + 2
		sprites[6] = new Spritesheet("/escadas64.png", 64); // total de sprites: 35
		sprites[7] = new Spritesheet("/escadas128.png", 128); // total de sprites: 40

		int max_pagina = 0;
		for (int i = 0; i < 8; i++) {
			spritesCarregados.put(sprites[i].getArquivo(), sprites[i].get_x_sprites(total_de_sprites[i]));
			max_pagina += total_de_sprites[i];
		}
		return max_pagina;

	}

	public static Tile pegar_chao(int pos) {
		if (pos >= tiles.length || pos < 0) {
			return null;
		}
		return tiles[pos];
	}

	public static Tile pegar_chao(int mx, int my, int mz) {
		return pegar_chao(calcular_pos(mx, my, mz));
	}

	public static int calcular_pos(int mx, int my, int mz) {
		return ((mx >> log_ts) + (my >> log_ts) * World.WIDTH) * World.HIGH + mz;
	}

	public void tick() {
		if (++tiles_animation_time >= max_tiles_animation_time) {
			tiles_animation_time = 0;
			if (++tiles_index >= 100) {
				tiles_index = 0;
			}
		}
	}

	public static boolean isFree(int xnext, int ynext, int z) {

		int x1 = xnext;
		int y1 = ynext;

		int x2 = (xnext + Gerador.TS);
		int y2 = ynext;

		int x3 = xnext;
		int y3 = (ynext + Gerador.TS);

		int x4 = (xnext + Gerador.TS);
		int y4 = (ynext + Gerador.TS);

		return !((pegar_chao(x1, y1, z) == null || pegar_chao(x1, y1, z).Solid())
				|| (pegar_chao(x2, y2, z) == null || pegar_chao(x2, y2, z).Solid())
				|| (pegar_chao(x3, y3, z) == null || pegar_chao(x3, y3, z).Solid())
				|| (pegar_chao(x4, y4, z) == null || pegar_chao(x4, y4, z).Solid()));
	}

	public static Tile[] tiles_ao_redor(int x, int y, int z) {
		Tile[] retorno = new Tile[8];

		int[] xs = { x - Gerador.TS, x, x + Gerador.TS, x - Gerador.TS, x + Gerador.TS, x - Gerador.TS, x,
				x + Gerador.TS },
				ys = { y - Gerador.TS, y - Gerador.TS, y - Gerador.TS, y, y, y + Gerador.TS, y + Gerador.TS,
						y + Gerador.TS };
		for (int i = 0; i < 8; i++) {
			retorno[i] = pegar_chao(xs[i], ys[i], z);
		}

		return retorno;
	}

	public void render(Graphics g) {
		int xstart = Camera.x >> log_ts;
		int ystart = Camera.y >> log_ts;

		int xfinal = xstart + (Gerador.windowWidth >> log_ts) + 1;
		int yfinal = ystart + (Gerador.windowHEIGHT >> log_ts) + 1;

		if ((xstart -= (Gerador.player.getZ() + 1)) < 0)
			xstart = 0;
		if ((ystart -= (Gerador.player.getZ() + 1)) < 0)
			ystart = 0;

		Tile t;
		int maxZ = HIGH;
		for (int i = 0; i < HIGH - Gerador.player.getZ() - 1; i++) {
			t = pegar_chao(((Gerador.player.getX() >> log_ts) + (i + 1) + (i + 1) * WIDTH
					+ (Gerador.player.getY() >> log_ts) * WIDTH) * HIGH + Gerador.player.getZ() + 1);

			if (t != null && t.existe()) {
				maxZ = t.getZ();
				break;
			}
		}

		for (int xx = xstart; xx <= xfinal; xx++)
			for (int yy = ystart; yy <= yfinal; yy++)
				for (int zz = 0; zz < maxZ; zz++) {
					if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
						continue;
					}

					Tile lTile = tiles[(xx + (yy * WIDTH)) * HIGH + zz];
					if (lTile != null)
						lTile.render(g);
				}
	}

	public static void fill(ArrayList<Tile> prTilesSelecionados) {
		for (Tile iTile : prTilesSelecionados)
			iTile.varios();
	}

	public static Tile pegarAdicionarTileMundo(int prPos) {
		Tile lRetorno = World.pegar_chao(prPos);
		if (lRetorno == null && prPos >= 0 && prPos < tiles.length) {
			int[] lPosXY = World.calcularPosicaoSemAltura(prPos);
			lRetorno = new Tile(lPosXY[0] + Camera.x, lPosXY[1] + Camera.y, lPosXY[2]);
			tiles[prPos] = lRetorno;
		}
		return lRetorno;
	}

	public static Tile pegarAdicionarTileMundo(int x, int y, int z) {
		int lPos = World.calcular_pos(x, y, z);
		Tile lRetorno = World.pegar_chao(lPos);
		if (lRetorno == null && lPos >= 0 && lPos < tiles.length) {
			int[] lPosXY = World.calcularPosicaoSemAltura(lPos);
			lRetorno = new Tile(lPosXY[0] + Camera.x, lPosXY[1] + Camera.y, z);
			tiles[lPos] = lRetorno;
		}
		return lRetorno;
	}

	public static void colocar_construcao(int prPOS, Build prConstrucao) {
		if (prConstrucao == null)
			return;
		int[] lPosXY = calcularPosicaoSemAltura(prPOS);
		Tile[] tiles_construcao = salvarCarregar.carregar_construcao(prConstrucao);
		if ((lPosXY[0] >> log_ts) + prConstrucao.getHorizontal() >= WIDTH
				|| (lPosXY[1] >> log_ts) + prConstrucao.getVertical() >= HEIGHT) {
			JOptionPane.showMessageDialog(null, "A construção não poderá ser feita aqui pois sairá do mapa");
			return;
		}
		Tile lTileInicial = pegarAdicionarTileMundo(prPOS);
		for (Tile iTile : tiles_construcao) {
			if (iTile.getPropriedade("CRIADORMAPATOPVIEW_POSICAO_RELATIVA") == null)
				continue;

			@SuppressWarnings("unchecked")
			ArrayList<Integer> posicaoRelativa = (ArrayList<Integer>) iTile
					.getPropriedade("CRIADORMAPATOPVIEW_POSICAO_RELATIVA");

			int iPos = Tile.pegarPosicaoRelativa(lTileInicial.getX(), lTileInicial.getY(), lTileInicial.getZ(),
					posicaoRelativa);
			iTile.setaPos(iPos);

			iTile.removePropriedade("CRIADORMAPATOPVIEW_POSICAO_RELATIVA");

			iTile.setX(pegarAdicionarTileMundo(iPos).getX());
			iTile.setY(World.tiles[iPos].getY());
			iTile.setZ(World.tiles[iPos].getZ());
			World.tiles[iPos] = iTile;
		}
	}

	public static void deletarSelecionados() {
		for (Tile iTile : Ui.aTilesSelecionados) {
			World.tiles[iTile.getaPos()] = null;
		}
		Ui.aTilesSelecionados.clear();
	}

	public static void empty(ArrayList<Tile> prTilesSelecionados) {
		ArrayList<Tile> lPonta = new ArrayList<>();
		ArrayList<Integer> lPosicoes = new ArrayList<>();
		for (Tile iTile : prTilesSelecionados) {
			lPosicoes.add(iTile.getaPos());
		}
		for (int i = 0; i < prTilesSelecionados.size(); i++) {
			int iPos = lPosicoes.get(i);
			if (!lPosicoes.contains(iPos - HIGH) || !lPosicoes.contains(iPos + HIGH)
					|| !lPosicoes.contains(iPos - WIDTH * HIGH) || !lPosicoes.contains(iPos + WIDTH * HIGH))
				lPonta.add(prTilesSelecionados.get(i));
		}
		if (lPonta.size() == 0)
			return;
		for (Tile iTile : lPonta)
			iTile.varios();
	}

	public static void novo_mundo(File file) {
		if (JOptionPane.showConfirmDialog(null, "Deseja salvar o mundo atual?") == 0)
			salvar();

		ready = false;
		Gerador.world = new World(file);
		Gerador.instance.startGerador();
	}

	public static void salvar() {
		salvarCarregar.salvar_mundo(arquivo);

	}

	public static void carregar_mundo() {
		Gerador.fd.setVisible(true);
		if (Gerador.fd.getFiles() != null && Gerador.fd.getFiles().length > 0)
			novo_mundo(Gerador.fd.getFiles()[0]);
	}

}
