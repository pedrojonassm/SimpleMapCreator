package world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import files.SalvarCarregar;
import graficos.Spritesheet;
import graficos.Ui;
import graficos.telas.sprites.TelaSprites;
import main.Gerador;
import main.Uteis;
import main.configs.Configs;

public class World {

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT, HIGH;
	public static HashMap<String, BufferedImage[]> spritesCarregados;
	public static ArrayList<String> nomeSprites;

	// chaos64, chaos128, paredes64, paredes128, itens64, itens128, escadas64,
	// escadas128
	public static int log_ts;

	public static int tiles_index, tiles_animation_time, max_tiles_animation_time;
	private static File arquivo;
	public static boolean ready, ok;

	public World(File file) {
		ready = false;
		// *
		tiles_index = tiles_animation_time = 0;
		max_tiles_animation_time = 15;
		try {
			ok = true;
			if (file == null) {
				arquivo = null;
				Integer[] lTamanho = Configs.loadValoresPadrao();
				if (Gerador.player != null)
					lTamanho = Configs.determinarConfiguraçõesMundo();
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
				tiles = SalvarCarregar.carregarMundo(file);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// */
	}

	public static void carregarSprites() {
		spritesCarregados = new HashMap<String, BufferedImage[]>();
		nomeSprites = new ArrayList<>();
		int maxPagina = carregarSpritesPadroes();
		TelaSprites.instance.max_pagina_por_total_de_sprites(maxPagina);
		carregarSpritesExternos();

	}

	private static void carregarSpritesExternos() {
		for (String iExSpriteSheet : Gerador.aConfig.getSpriteSheetExternos()) {
			File lFile = new File(SalvarCarregar.arquivoLocalSpritesExternos, iExSpriteSheet);
			if (lFile.exists()) {
				SalvarCarregar.carregarImagemExterna(new File(lFile, SalvarCarregar.nomeDataSpritesExternos));

			}
		}
	}

	public static void adicionarSpritesExterno(File lFile, int tamanho, int totalSprites) {
		Spritesheet lSpritesheet = new Spritesheet(lFile, tamanho, totalSprites);
		adicionarSpriteSheet(lSpritesheet.getNome(), lSpritesheet.get_x_sprites(lSpritesheet.getTotalSprites()));
	}

	public static int carregarSpritesPadroes() {

		Spritesheet[] sprites = new Spritesheet[8];
		sprites[0] = new Spritesheet("/chaos64.png", 64, 36 * 40 + 16);
		sprites[1] = new Spritesheet("/chaos128.png", 128, 9);
		sprites[2] = new Spritesheet("/paredes64.png", 64, 27 * 20 - 3);
		sprites[3] = new Spritesheet("/paredes128.png", 128, 40 * 32 - 11);
		sprites[4] = new Spritesheet("/itens64.png", 64, 40 * 23 - 16);
		sprites[5] = new Spritesheet("/itens128.png", 128, 20 * 16 + 2);
		sprites[6] = new Spritesheet("/escadas64.png", 64, 35);
		sprites[7] = new Spritesheet("/escadas128.png", 128, 40);

		int max_pagina = 0;
		for (int i = 0; i < 8; i++) {
			adicionarSpriteSheet(sprites[i].getNome(), sprites[i].get_x_sprites(sprites[i].getTotalSprites()));
			max_pagina += sprites[i].getTotalSprites();
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

			if (t != null && t.tem_sprites()) {
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
			int[] lPosXY = Uteis.calcularPosicaoSemAlturaIgnorandoCamera(prPos);
			lRetorno = new Tile(lPosXY[0] + Camera.x, lPosXY[1] + Camera.y, lPosXY[2]);
			tiles[prPos] = lRetorno;
		}
		return lRetorno;
	}

	public static Tile pegarAdicionarTileMundo(int x, int y, int z) {
		int lPos = World.calcular_pos(x, y, z);
		Tile lRetorno = World.pegar_chao(lPos);
		if (lRetorno == null && lPos >= 0 && lPos < tiles.length) {
			int[] lPosXY = Uteis.calcularPosicaoSemAlturaIgnorandoCamera(lPos);
			lRetorno = new Tile(lPosXY[0] + Camera.x, lPosXY[1] + Camera.y, z);
			tiles[lPos] = lRetorno;
		}
		return lRetorno;
	}

	public static void colocar_construcao(int prPOS, Build prConstrucao) {
		if (prConstrucao == null)
			return;
		int[] lPosXY = Uteis.calcularPosicaoSemAlturaIgnorandoCamera(prPOS);
		Tile[] tiles_construcao = SalvarCarregar.carregar_construcao(prConstrucao);
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
		Gerador.aConfig = new Configs();
		Gerador.world = new World(file);
		Gerador.instance.startGerador();
	}

	public static void salvar() {
		SalvarCarregar.salvar_mundo(arquivo);

	}

	public static void carregar_mundo() {
		Gerador.aFileDialog.setFile(SalvarCarregar.name_file_world);
		Gerador.aFileDialog.setDirectory(SalvarCarregar.localWorlds);
		Gerador.aFileDialog.setVisible(true);
		if (Gerador.aFileDialog.getFiles() != null && Gerador.aFileDialog.getFiles().length > 0)
			if (!Gerador.aFileDialog.getFiles()[0].getName().contentEquals(SalvarCarregar.name_file_world)) {
				JOptionPane.showMessageDialog(null, "arquivo selecionado não bate com o esperado");
				return;
			}
		novo_mundo(Gerador.aFileDialog.getFiles()[0]);
	}

	public static void adicionarSpriteSheet(String nome, BufferedImage[] imagens) {
		spritesCarregados.put(nome, imagens);
		nomeSprites.add(nome);
	}

	public static BufferedImage PegarSprite(String Key, int posicao) {
		return spritesCarregados.get(Key)[posicao];
	}

	public static void saveWorldAsBuild() {
		ArrayList<Tile> lMundoAntigo = new ArrayList<>();
		for (Tile iTile : tiles)
			if (iTile != null && iTile.tem_sprites())
				lMundoAntigo.add(iTile);

		SalvarCarregar.salvar_construcao(lMundoAntigo);

	}

}
