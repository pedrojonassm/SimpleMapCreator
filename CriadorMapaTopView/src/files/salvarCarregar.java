package files;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import graficos.ConjuntoSprites;
import graficos.telas.construcao.TelaConstrucoes;
import graficos.telas.sprites.TelaSprites;
import main.Gerador;
import main.configs.ExConfig;
import world.Build;
import world.Tile;
import world.World;

public class salvarCarregar {
	public static File arquivoBooks, arquivoWorlds, arquivoConstrucoes, arquivoLocalSpritesExternos;
	public static final String localBooks = "books", localWorlds = "worlds", localBuilds = "construcoes",
			localSpritesExternos = "externalSprites", name_file_builds = "build.bld", name_foto_builds = "image.png",
			end_file_book = ".book", name_file_world = "world.world", name_file_config = "world.config";

	public salvarCarregar() {
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
	}

	public ArrayList<String> listFilesForFolder(final File folder) {
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

	public static void salvar_construcao(ArrayList<Tile> prTilesSelecionados) {
		if (prTilesSelecionados == null || prTilesSelecionados.size() == 0)
			return;
		try {
			String nome = null;
			File pasta = null;
			do {
				nome = JOptionPane.showInputDialog("Insira um nome NOVO para a nova construção");
				if (nome == null)
					return;
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
			TelaConstrucoes.instance.adicionar_construcao(new Build(horizontal, vertical, high, pasta));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void criar_imagem(File pasta) {
		Tile[] tiles;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(pasta, name_file_builds)));
			String singleLine = null;
			singleLine = reader.readLine();
			String[] sla = singleLine.split(";");
			int WIDTH = Integer.parseInt(sla[0]), HEIGHT = Integer.parseInt(sla[1]), HIGH = Integer.parseInt(sla[2]);
			ArrayList<String> lLinhas = new ArrayList<>();
			while ((singleLine = reader.readLine()) != null && !singleLine.isBlank()) {
				lLinhas.add(singleLine);
			}
			tiles = (Tile[]) salvarCarregar.fromJson(lLinhas.get(0), World.tiles.getClass());
			int pX = tiles[0].getX(), pY = tiles[0].getY(), pZ = tiles[0].getZ();
			BufferedImage image = new BufferedImage((WIDTH + HIGH) * Gerador.TS, (HEIGHT + HIGH) * Gerador.TS,
					BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			for (Tile t : tiles) {
				t.setX(t.getX() - pX);
				t.setY(t.getY() - pY);
				t.setZ(t.getZ() - pZ);
				t.render(g);
			}
			ImageIO.write(image, "png", new File(pasta, name_foto_builds));
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
				construcoes.add(new Build(Integer.parseInt(size[0]), Integer.parseInt(size[1]),
						Integer.parseInt(size[2]), pasta));
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

	public static void carregarConfiguracoesMundo(File lFileConfig) throws Exception {
		if (lFileConfig.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(lFileConfig));
			String singleLine = null;
			String lFile = "";
			while ((singleLine = reader.readLine()) != null) {
				lFile += singleLine;
			}
			Gerador.aConfig = (ExConfig) fromJson(lFile, ExConfig.class);
		} else {
			Gerador.aConfig = new ExConfig();
		}
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
						|| (pastaDoMundo = new File(salvarCarregar.arquivoWorlds, nome)).exists());
				pastaDoMundo.mkdir();
				new File(pastaDoMundo, salvarCarregar.name_file_world).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		String salvar = "";
		salvar += salvarCarregar.toJSON(World.tiles);
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
		carregarConfiguracoesMundo(new File(prfile.getParentFile().getAbsolutePath() + "/" + name_file_config));
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(prfile));
		String singleLine = null;
		while ((singleLine = reader.readLine()) == null || singleLine.isBlank()) {
		}
		return (Tile[]) salvarCarregar.fromJson(singleLine, Tile[].class);

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
			while ((singleLine = reader.readLine()) != null && !singleLine.isBlank()) {
				return (Tile[]) salvarCarregar.fromJson(singleLine, World.tiles.getClass());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
