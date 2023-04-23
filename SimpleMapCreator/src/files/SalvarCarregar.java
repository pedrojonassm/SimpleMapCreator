package files;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import graficos.ConjuntoSprites;
import graficos.telas.Sprite;
import graficos.telas.construcao.TelaConstrucoes;
import graficos.telas.sprites.TelaSprites;
import main.Gerador;
import main.configs.Configs;
import main.configs.ExConfig;
import main.configs.ExSpriteSheet;
import world.Build;
import world.Tile;
import world.World;

public class SalvarCarregar {
	public static File arquivoBooks, arquivoWorlds, arquivoConstrucoes, arquivoLocalSpritesExternos,
			arquivoLocalExportacoes;
	public static final String localBooks = "books", localWorlds = "worlds", localBuilds = "construcoes",
			localSpritesExternos = "externalSprites", localExportacoes = "Exports", name_file_builds = "build.bld",
			nameImagem = "image.png", end_file_book = ".book", name_file_world = "world.world",
			name_file_config = "world.config", nomeDataSpritesExternos = "data.config";

	public SalvarCarregar() {
		arquivoBooks = new File(localBooks);
		if (!arquivoBooks.exists()) {
			arquivoBooks.mkdir();
		}
		arquivoWorlds = new File(localWorlds);
		if (!arquivoWorlds.exists()) {
			arquivoWorlds.mkdir();
		}
		arquivoConstrucoes = new File(localBuilds);
		if (!arquivoConstrucoes.exists()) {
			arquivoConstrucoes.mkdir();
		}

		arquivoLocalSpritesExternos = new File(localSpritesExternos);
		if (!arquivoLocalSpritesExternos.exists()) {
			arquivoLocalSpritesExternos.mkdir();
		}
		arquivoLocalExportacoes = new File(localExportacoes);
		if (!arquivoLocalExportacoes.exists()) {
			arquivoLocalExportacoes.mkdir();
		}
	}

	public static ArrayList<String> listFilesForFolder(final File folder) {
		ArrayList<String> retorno = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				for (String nome : listFilesForFolder(fileEntry)) {
					retorno.add(folder.getName() + "/" + nome);
				}
			} else {
				retorno.add(folder.getName() + "/" + fileEntry.getName());
			}
		}
		return retorno;
	}

	public static Build salvar_construcao(ArrayList<Tile> prTilesSelecionados) {
		if (prTilesSelecionados == null || prTilesSelecionados.size() == 0)
			return null;
		try {
			String nome = null;
			File pasta = null;
			do {
				nome = JOptionPane.showInputDialog("Insira um nome NOVO para a nova construção");
				if (nome == null)
					return null;
				if (!nome.isBlank()) {
					pasta = new File(localBuilds, nome);
					if (pasta.exists()) {
						pasta = null;
					} else {
						pasta.mkdir();
					}
				}
			} while (nome == null || pasta == null);
			int minX = prTilesSelecionados.get(0).getX(), maxX = prTilesSelecionados.get(0).getX(),
					minY = prTilesSelecionados.get(0).getY(), maxY = prTilesSelecionados.get(0).getY(),
					minZ = prTilesSelecionados.get(0).getZ(), maxZ = prTilesSelecionados.get(0).getZ();
			ArrayList<Tile> lTilesSelecionados = new ArrayList<>();
			for (Tile iTile : prTilesSelecionados) {
				if (iTile.getX() < minX)
					minX = iTile.getX();
				if (iTile.getY() < minY)
					minY = iTile.getY();
				if (iTile.getZ() < minZ)
					minZ = iTile.getZ();
				if (iTile.getX() > maxX)
					maxX = iTile.getX();
				if (iTile.getY() > maxY)
					maxY = iTile.getY();
				if (iTile.getZ() > maxZ)
					maxZ = iTile.getZ();
				lTilesSelecionados.add((Tile) fromJson(toJSON(iTile), iTile.getClass()));
			}
			for (Tile iTile : lTilesSelecionados) {
				iTile.addPropriedade("CRIADORMAPATOPVIEW_POSICAO_RELATIVA",
						Tile.pegarPosicaoRelativa(minX, minY, minZ, iTile.getX(), iTile.getY(), iTile.getZ()));
			}
			String lConteudo = toJSON(lTilesSelecionados);
			int horizontal = (maxX >> World.log_ts) - (minX >> World.log_ts),
					vertical = (maxY >> World.log_ts) - (minY >> World.log_ts), high = maxZ - minZ;

			// 9 - 7 = 2, entretanto são as posições 7, 8 e 9, logo o correto seria 3. Logo,
			// se o resultado for maior que 0, o resultado sempre deve ser somado +1
			horizontal++;
			vertical++;
			high++;
			File file = new File(pasta, name_file_builds);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			lConteudo = horizontal + ";" + vertical + ";" + high + "\n" + lConteudo;
			writer.write(lConteudo);
			writer.flush();
			writer.close();
			criar_imagem(pasta);
			Build lBuild = new Build(horizontal, vertical, high, pasta);
			TelaConstrucoes.instance.adicionar_construcao(lBuild);
			return lBuild;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void criar_imagem(File pasta) {
		Tile[] tiles;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(pasta, name_file_builds)));
			String singleLine = null;
			singleLine = reader.readLine();
			String[] sla = singleLine.split(";");
			int WIDTH = Integer.parseInt(sla[0]), HEIGHT = Integer.parseInt(sla[1]),
					HIGH = Integer.parseInt(sla[2]) - 1;
			String lConteudo = "";
			while ((singleLine = reader.readLine()) != null)
				lConteudo += singleLine;

			tiles = (Tile[]) SalvarCarregar.fromJson(lConteudo, Tile[].class);
			int pX = tiles[0].getX(), pY = tiles[0].getY(), pZ = tiles[0].getZ();
			BufferedImage image = new BufferedImage((WIDTH + HIGH) * Gerador.TS, (HEIGHT + HIGH) * Gerador.TS,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) image.getGraphics();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			for (Tile t : tiles) {
				t.setX(t.getX() - pX);
				t.setY(t.getY() - pY);
				t.setZ(t.getZ() - pZ);
				t.desenharSprite(g, 0, 0, 0, 0);
			}

			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			g.dispose();
			ImageIO.write(image, "PNG", new File(pasta, nameImagem));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void carregar_construcoes() {
		File[] arquivos = arquivoConstrucoes.listFiles();
		try {
			BufferedReader reader;
			ArrayList<Build> construcoes = new ArrayList<Build>();
			for (File pasta : arquivos) {
				File f = new File(pasta, name_file_builds);
				reader = new BufferedReader(new FileReader(f));
				String[] size = reader.readLine().split(";");
				reader.close();
				Build lBuild = new Build(Integer.parseInt(size[0]), Integer.parseInt(size[1]),
						Integer.parseInt(size[2]), pasta);
				if (lBuild.getImage() != null)
					construcoes.add(lBuild);
			}
			TelaConstrucoes.instance.adicionar_construcoes_salvas(construcoes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void carregar_livros() {
		ArrayList<String> arquivos = listFilesForFolder(arquivoBooks);
		try {
			BufferedReader reader;
			for (String caminho : arquivos) {
				reader = new BufferedReader(new FileReader(new File(caminho)));
				String singleLine = null;
				ArrayList<ConjuntoSprites> lCoConjuntoSprites = new ArrayList<ConjuntoSprites>();
				while ((singleLine = reader.readLine()) != null && !singleLine.isBlank()) {
					lCoConjuntoSprites.add((ConjuntoSprites) fromJson(singleLine, ConjuntoSprites.class));
				}
				caminho = caminho.split("/")[caminho.split("/").length - 1];
				caminho = caminho.substring(0, caminho.length() - end_file_book.length());
				TelaSprites.instance.adicionar_livro_salvo(caminho, lCoConjuntoSprites);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void salvar_livro(int index) {
		ArrayList<ConjuntoSprites> lConjunto = TelaSprites.pegar_livro(index);
		String nome = TelaSprites.pegar_nome_livro(index + 1);
		File file = new File(arquivoBooks, nome + end_file_book);
		try {
			if (!file.exists())
				file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			String lConteudo = "";

			for (ConjuntoSprites t : lConjunto) {
				lConteudo += toJSON(t);
			}
			writer.write(lConteudo);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void salvarConfiguracoesMundo(File pasta_do_mundo) throws IOException {
		File lFileworld = new File(pasta_do_mundo, name_file_config);
		if (!lFileworld.exists())
			lFileworld.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(lFileworld));

		Gerador.aConfig.atualizarAntesSalvar();

		writer.write(toJSON(Gerador.aConfig));
		writer.flush();
		writer.close();

	}

	public static Configs carregarConfiguracoesMundo(File lFileConfig) throws Exception {
		Configs lExConfig = new Configs();
		if (lFileConfig.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(lFileConfig));
			String singleLine = null;
			String lFile = "";
			while ((singleLine = reader.readLine()) != null) {
				lFile += singleLine;
			}
			reader.close();
			lExConfig = (Configs) fromJson(lFile, Configs.class);
		}
		return lExConfig;
	}

	public static void salvar_mundo(File pastaDoMundo) {
		if (pastaDoMundo == null) {

			try {
				String nome = null;
				do {
					nome = JOptionPane.showInputDialog("Insira um nome válido para esse mundo");
					if (nome == null) {
						if (JOptionPane.showConfirmDialog(null, "Tem certeza que deseja cancelar?") == 0)
							return;
					}
				} while (nome == null || nome.isBlank()
						|| (pastaDoMundo = new File(SalvarCarregar.arquivoWorlds, nome)).exists());
				pastaDoMundo.mkdir();
				new File(pastaDoMundo, SalvarCarregar.name_file_world).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		String salvar = "";
		salvar += SalvarCarregar.toJSON(World.tiles);
		try {
			salvarConfiguracoesMundo(pastaDoMundo);
			File lFileworld = new File(pastaDoMundo, name_file_world);
			if (!lFileworld.exists())
				lFileworld.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(lFileworld));
			writer.write(salvar);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Tile[] carregarMundo(File prfile) throws Exception {
		Gerador.aConfig = carregarConfiguracoesMundo(
				new File(prfile.getParentFile().getAbsolutePath() + "/" + name_file_config));
		BufferedReader reader = new BufferedReader(new FileReader(prfile));
		String singleLine = null, lFile = "";
		while ((singleLine = reader.readLine()) != null) {
			lFile += singleLine;
		}
		reader.close();
		return (Tile[]) SalvarCarregar.fromJson(lFile, Tile[].class);

	}

	public static String toJSON(final Object prObj) {
		String lJSON = "";

		ObjectMapper lObjectMapper = new ObjectMapper();

		try {
			lObjectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
			lJSON = lObjectMapper.writeValueAsString(prObj) + "\n";
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return lJSON;
	}

	public static <T> Object fromJson(String prJson, Class<T> prClass) {
		ObjectMapper lObjectMapper = new ObjectMapper();
		try {
			lObjectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
			return lObjectMapper.readValue(prJson, prClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Tile[] carregar_construcao(Build construcao) {
		if (construcao == null || construcao.getFile() == null)
			return null;
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File(construcao.getFile(), name_file_builds)));
			reader.readLine(); // pula a linha das dimensões
			String singleLine;
			String lConteuddo = "";
			while ((singleLine = reader.readLine()) != null) {
				lConteuddo += singleLine;

			}
			reader.close();
			return (Tile[]) SalvarCarregar.fromJson(lConteuddo, World.tiles.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void adicionarImagemExterna(ExSpriteSheet prExSpriteSheet, File prFile, BufferedImage prBufferedImage)
			throws Exception {
		prFile.mkdir();
		File lFileImagem = new File(prFile, nameImagem),
				lFileData = new File(prFile, SalvarCarregar.nomeDataSpritesExternos);
		lFileData.createNewFile();
		BufferedWriter lBufferedWriter = new BufferedWriter(new FileWriter(lFileData));
		lBufferedWriter.write(SalvarCarregar.toJSON(prExSpriteSheet));
		lBufferedWriter.flush();
		lBufferedWriter.close();
		ImageIO.write(prBufferedImage, "png", lFileImagem);
		World.adicionarSpritesExterno(lFileImagem, prExSpriteSheet.getTamanho(), prExSpriteSheet.getTotalSprites());
		Gerador.aConfig.getSpriteSheetExternos().add(prExSpriteSheet.getNome());
		TelaSprites.instance.max_pagina_por_total_de_sprites(prExSpriteSheet.getTotalSprites());

	}

	public static void carregarImagemExterna() {
		Gerador.aFileDialog.setDirectory(localSpritesExternos);
		Gerador.aFileDialog.setFile(nomeDataSpritesExternos);
		Gerador.aFileDialog.setVisible(true);
		if (Gerador.aFileDialog.getFiles() != null && Gerador.aFileDialog.getFiles().length > 0) {
			if (!Gerador.aFileDialog.getFiles()[0].getName().contentEquals(nomeDataSpritesExternos)) {
				JOptionPane.showMessageDialog(null, "arquivo selecionado não bate com o esperado");
				return;
			}

			carregarImagemExterna(Gerador.aFileDialog.getFiles()[0]);
			Gerador.aConfig.getSpriteSheetExternos().add(Gerador.aFileDialog.getFiles()[0].getParentFile().getName());
		}
	}

	public static void carregarImagemExterna(File prFileData) {

		try {
			if (World.spritesCarregados.containsKey(prFileData.getParentFile().getName())) {
				JOptionPane.showMessageDialog(null, "Já existe um SpriteSheet importado com esse nome");
				return;
			}

			BufferedReader reader = new BufferedReader(new FileReader(prFileData));
			String singleLine, lFile = "";
			while ((singleLine = reader.readLine()) != null && !singleLine.isBlank()) {
				lFile += singleLine;
			}
			reader.close();
			ExSpriteSheet lExSpriteSheet = (ExSpriteSheet) fromJson(lFile, ExSpriteSheet.class);
			File lFileImagem = new File(prFileData.getParentFile(), nameImagem);
			World.adicionarSpritesExterno(lFileImagem, lExSpriteSheet.getTamanho(), lExSpriteSheet.getTotalSprites());
			TelaSprites.instance.max_pagina_por_total_de_sprites(lExSpriteSheet.getTotalSprites());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportarMundoJson() {
		String lNome = JOptionPane.showInputDialog("Insira um nome para a pasta a ser salvo o Mundo Exportado");
		File lFileExportacao = new File(arquivoLocalExportacoes, lNome), lFileImagens, lFileMundoExportado, lFileImagem,
				lFileConfig, lFileImageConfigs;
		if (lFileExportacao.exists())
			lFileExportacao = new File(arquivoLocalExportacoes,
					lNome + "-" + new SimpleDateFormat("yyyy-MM-dd HH.mm").format(new Date()));

		lFileImagens = new File(lFileExportacao, "imagens");
		lFileMundoExportado = new File(lFileExportacao, name_file_world);
		lFileConfig = new File(lFileExportacao, name_file_config);
		lFileExportacao.mkdir();
		lFileImagens.mkdir();
		try {
			lFileMundoExportado.createNewFile();
			lFileConfig.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao criar o arquivo de exportação, cancelando");
			return;
		}

		ArrayList<Tile> lExport = new ArrayList<>();
		HashMap<String, ArrayList<BufferedImage>> lImagensToExport = new HashMap<>();
		HashMap<String, ArrayList<Integer>> lFrom = new HashMap<>(), lTo = new HashMap<>();
		Tile lTile;
		ConjuntoSprites lConjuntoSprites;
		for (Tile iTile : World.tiles) {
			if (iTile == null) {
				lExport.add(null);
				continue;
			}
			lTile = new Tile(iTile.getX(), iTile.getY(), iTile.getZ());
			lTile.getCoConjuntoSprites().remove(0);
			for (ConjuntoSprites iConjuntoSprites : iTile.getCoConjuntoSprites()) {
				lConjuntoSprites = iConjuntoSprites.clone();
				for (ArrayList<Sprite> iList : lConjuntoSprites.getSprites()) {
					for (Sprite iSprite : iList) {
						if (!lImagensToExport.containsKey(iSprite.getNome())) {
							lImagensToExport.put(iSprite.getNome(), new ArrayList<BufferedImage>());
							lFrom.put(iSprite.getNome(), new ArrayList<Integer>());
							lTo.put(iSprite.getNome(), new ArrayList<Integer>());
						}

						if (!lFrom.get(iSprite.getNome()).contains(iSprite.getPosicao())) {
							// Se o Sprite ainda não foi adicionado na exportação
							lFrom.get(iSprite.getNome()).add(iSprite.getPosicao());
							lTo.get(iSprite.getNome()).add(lTo.get(iSprite.getNome()).size());
							lImagensToExport.get(iSprite.getNome())
									.add(World.spritesCarregados.get(iSprite.getNome())[iSprite.getPosicao()]);
						}
						iSprite.setPosicao(lTo.get(iSprite.getNome())
								.get(lFrom.get(iSprite.getNome()).indexOf(iSprite.getPosicao())));
					}
				}
				lTile.getCoConjuntoSprites().add(lConjuntoSprites);
			}

			lExport.add(lTile);
		}

		// Exporttar as imagens
		int lTamanho, lLinhas, lColunas;
		BufferedImage iBufferedImage;
		Graphics iGraphics;
		BufferedWriter writer;
		ExSpriteSheet lExSpriteSheet = new ExSpriteSheet();
		for (Entry<String, ArrayList<BufferedImage>> iImagens : lImagensToExport.entrySet()) {
			lTamanho = iImagens.getValue().get(0).getHeight();
			lLinhas = (int) Math.sqrt(iImagens.getValue().size());
			lColunas = lLinhas;
			while (lLinhas * lColunas < iImagens.getValue().size())
				lLinhas++;

			iBufferedImage = new BufferedImage(lTamanho * lLinhas, lTamanho * lColunas, BufferedImage.TYPE_INT_RGB);

			iGraphics = iBufferedImage.getGraphics();
			iGraphics.setColor(Color.black);
			iGraphics.fillRect(0, 0, iBufferedImage.getWidth(), iBufferedImage.getHeight());
			for (int i = 0; i < iImagens.getValue().size(); i++) {
				iGraphics.drawImage(iImagens.getValue().get(i), (i % lLinhas) * lTamanho, (i / lLinhas) * lTamanho,
						null);
			}

			iGraphics.drawImage(iBufferedImage, 0, 0, iBufferedImage.getWidth(), iBufferedImage.getHeight(), null);
			iGraphics.dispose();
			lFileImagem = new File(lFileImagens, iImagens.getKey());

			lExSpriteSheet.setTotalSprites(iImagens.getValue().size());
			lExSpriteSheet.setTamanho(iImagens.getValue().get(0).getWidth());
			lExSpriteSheet.setNome(iImagens.getKey());
			try {
				lFileImagem.mkdir();
				lFileImagem = new File(lFileImagem, nameImagem);
				lFileImagem.createNewFile();
				ImageIO.write(iBufferedImage, "PNG", lFileImagem);
				lFileImageConfigs = new File(lFileImagem.getParentFile(), nomeDataSpritesExternos);
				lFileImageConfigs.createNewFile();
				writer = new BufferedWriter(new FileWriter(lFileImageConfigs));
				writer.write(toJSON(lExSpriteSheet));
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Ocorreu um erro ao criar o arquivo de exportação, cancelando");
				return;
			}

		}

		try {
			writer = new BufferedWriter(new FileWriter(lFileMundoExportado));
			String lConteudo = toJSON(lExport);
			writer.write(lConteudo);
			writer.flush();
			writer.close();
			writer = new BufferedWriter(new FileWriter(lFileConfig));
			ExConfig lConfig = new ExConfig();
			lConfig.fromConfig(Gerador.aConfig);
			lConfig.setPlayerX(Gerador.player.getX());
			lConfig.setPlayerY(Gerador.player.getY());
			lConfig.setPlayerZ(Gerador.player.getZ());
			writer.write(toJSON(lConfig));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao criar o arquivo de exportação, cancelando");
			return;
		}

	}
}
