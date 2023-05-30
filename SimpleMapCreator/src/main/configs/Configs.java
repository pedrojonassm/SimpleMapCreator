package main.configs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import files.SalvarCarregar;
import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import main.Gerador;
import world.World;

public class Configs {
	private int worldWidth, worldHeight, worldHigh, amountOfTicks, TileSize, playerX, playerY, playerZ, tamanhoPreSets,
			totalLayers;
	private ArrayList<String> propriedades, transportes, nomeModosSprites, spriteSheetExternos, spritesIgnorados;
	private String nomeAlturaUi, nomeLimparUi, nomeCaixaUi, nomePreencherUi, nomeSubstituirUi, nomeSalva_construcaoUi,
			nomeTilesNivel, nomeModo, nomeTrocar;

	public Configs() {
		worldWidth = 20;
		worldHeight = 20;
		worldHigh = 7;
		amountOfTicks = 60;
		TileSize = 64;
		playerX = playerY = 12 * TileSize;
		propriedades = new ArrayList<>();
		transportes = new ArrayList<>();
		nomeModosSprites = new ArrayList<>();
		spriteSheetExternos = new ArrayList<>();
		spritesIgnorados = new ArrayList<>();
		listasPadrao();

		tamanhoPreSets = 32;
		totalLayers = 4;
		nomeAlturaUi = "Altura: ";
		nomeLimparUi = "limpar_seleção";
		nomeCaixaUi = "caixa";
		nomePreencherUi = "preencher";
		nomeSubstituirUi = "substituir?";
		nomeSalva_construcaoUi = "salvar construção";
		nomeTilesNivel = "layer dos tiles: ";
		nomeModo = "Modo e colocação dos Sprites";
		nomeTrocar = "Trocar";
	}

	private void listasPadrao() {
		nomeModosSprites.add("Full Tile");
		nomeModosSprites.add("layer");
		propriedadespadrao();
		transportespadrao();
	}

	private void propriedadespadrao() {
		propriedades.add("ToOtherWorld");
		propriedades.add("Solid");
		propriedades.add("Speed");
		propriedades.add("renderLayerPosWorldRender");
		propriedades.add("renderLayerPosWorldRenderHorizontal");
		propriedades.add("renderLayerPosWorldRenderVertical");
	}

	private void transportespadrao() {
		transportes.add("colisao");
		transportes.add("clique direito");
		transportes.add("Buraco aberto");
		transportes.add("Buraco fechado");
	}

	public void atualizarAntesSalvar() {
		Gerador.aConfig.setPlayerX(Gerador.player.getX() - Gerador.player.getX() % Gerador.TS);
		Gerador.aConfig.setPlayerY(Gerador.player.getY() - Gerador.player.getY() % Gerador.TS);
		Gerador.aConfig.setPlayerZ(Gerador.player.getZ());
		propriedades = SubTelaPropriedade.instance.getaCoPropriedades();
	}

	public static Integer[] determinarConfiguraçõesMundo() {
		JTextField lWorldWidth = new JTextField(), lWorldHeight = new JTextField(), lWorldHigh = new JTextField(),
				lTileSize = new JTextField(Gerador.aConfig.TileSize + ""),
				lTotalLayerTiles = new JTextField(Gerador.aConfig.getTotalLayers() + "");
		KeyListener l = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9')) {
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		};
		lTileSize.addKeyListener(l);
		lTotalLayerTiles.addKeyListener(l);
		lWorldWidth.addKeyListener(l);
		lWorldHeight.addKeyListener(l);
		lWorldHigh.addKeyListener(l);
		Object[] message = { "Width:", lWorldWidth, "Height:", lWorldHeight, "High:", lWorldHigh,
				"Total de Layers por Tile: ", lTotalLayerTiles, "Tamanho de cada Tile (pixels)", lTileSize };

		int option = JOptionPane.showConfirmDialog(null, message, "Tamanho do mundo", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			Integer[] lValores = new Integer[3];
			try {
				if (Integer.parseInt(lTotalLayerTiles.getText()) > 0)
					Gerador.aConfig.setTotalLayers(Integer.parseInt(lTotalLayerTiles.getText()));
				if (Integer.parseInt(lTileSize.getText()) >= 64)
					Gerador.aConfig.setTileSize(Integer.parseInt(lTileSize.getText()));

				lValores[0] = Integer.parseInt(lWorldWidth.getText());
				lValores[1] = Integer.parseInt(lWorldHeight.getText());
				lValores[2] = Integer.parseInt(lWorldHigh.getText());

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"alguns dados não foram inseridos ou foram inseridos incorretamente;\n Inserindo valores padrão");
				lValores = loadValoresPadrao();
			}

			return lValores;
		} else {
			return null;
		}
	}

	public static Integer[] loadValoresPadrao() {

		Integer[] lValoresPadrao = new Integer[3];
		lValoresPadrao[0] = Gerador.aConfig.worldWidth;
		lValoresPadrao[1] = Gerador.aConfig.worldHeight;
		lValoresPadrao[2] = Gerador.aConfig.worldHigh;
		return lValoresPadrao;
	}

	public void mundoCarregado() {
		worldWidth = World.WIDTH;
		worldHeight = World.HEIGHT;
		worldHigh = World.HIGH;
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(int worldWidth) {
		this.worldWidth = worldWidth;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(int worldHeight) {
		this.worldHeight = worldHeight;
	}

	public int getWorldHigh() {
		return worldHigh;
	}

	public void setWorldHigh(int worldHigh) {
		this.worldHigh = worldHigh;
	}

	public int getAmountOfTicks() {
		return amountOfTicks;
	}

	public void setAmountOfTicks(int amountOfTicks) {
		this.amountOfTicks = amountOfTicks;
	}

	public int getTileSize() {
		return TileSize;
	}

	public void setTileSize(int tileSize) {
		TileSize = tileSize;
	}

	public int getPlayerX() {
		return playerX;
	}

	public void setPlayerX(int playerX) {
		this.playerX = playerX;
	}

	public int getPlayerY() {
		return playerY;
	}

	public void setPlayerY(int playerY) {
		this.playerY = playerY;
	}

	public int getPlayerZ() {
		return playerZ;
	}

	public void setPlayerZ(int playerZ) {
		this.playerZ = playerZ;
	}

	public ArrayList<String> getPropriedades() {
		return propriedades;
	}

	public ArrayList<String> getTransportes() {
		return transportes;
	}

	public void setTransportes(ArrayList<String> transportes) {
		this.transportes = transportes;
	}

	public void setPropriedades(ArrayList<String> propriedades) {
		this.propriedades = propriedades;
	}

	public int getTamanhoPreSets() {
		return tamanhoPreSets;
	}

	public void setTamanhoPreSets(int tamanhoPreSets) {
		this.tamanhoPreSets = tamanhoPreSets;
	}

	public int getTotalLayers() {
		return totalLayers;
	}

	public void setTotalLayers(int totalLayers) {
		this.totalLayers = totalLayers;
	}

	public String getNomeAlturaUi() {
		return nomeAlturaUi;
	}

	public void setNomeAlturaUi(String nomeAlturaUi) {
		this.nomeAlturaUi = nomeAlturaUi;
	}

	public String getNomeLimparUi() {
		return nomeLimparUi;
	}

	public void setNomeLimparUi(String nomeLimparUi) {
		this.nomeLimparUi = nomeLimparUi;
	}

	public String getNomeCaixaUi() {
		return nomeCaixaUi;
	}

	public void setNomeCaixaUi(String nomeCaixaUi) {
		this.nomeCaixaUi = nomeCaixaUi;
	}

	public String getNomePreencherUi() {
		return nomePreencherUi;
	}

	public void setNomePreencherUi(String nomePreencherUi) {
		this.nomePreencherUi = nomePreencherUi;
	}

	public String getNomeSubstituirUi() {
		return nomeSubstituirUi;
	}

	public void setNomeSubstituirUi(String nomeSubstituirUi) {
		this.nomeSubstituirUi = nomeSubstituirUi;
	}

	public String getNomeSalva_construcaoUi() {
		return nomeSalva_construcaoUi;
	}

	public void setNomeSalva_construcaoUi(String nomeSalva_construcaoUi) {
		this.nomeSalva_construcaoUi = nomeSalva_construcaoUi;
	}

	public String getNomeTilesNivel() {
		return nomeTilesNivel;
	}

	public void setNomeTilesNivel(String nomeTilesNivel) {
		this.nomeTilesNivel = nomeTilesNivel;
	}

	public ArrayList<String> getNomeModosSprites() {
		return nomeModosSprites;
	}

	public void setNomeModosSprites(ArrayList<String> nomeModosSprites) {
		this.nomeModosSprites = nomeModosSprites;
	}

	public String getNomeModo() {
		return nomeModo;
	}

	public void setNomeModo(String nomeModo) {
		this.nomeModo = nomeModo;
	}

	public String getNomeTrocar() {
		return nomeTrocar;
	}

	public void setNomeTrocar(String nomeTrocar) {
		this.nomeTrocar = nomeTrocar;
	}

	public ArrayList<String> getSpriteSheetExternos() {
		return spriteSheetExternos;
	}

	public void setSpriteSheetExternos(ArrayList<String> spriteSheetExternos) {
		this.spriteSheetExternos = spriteSheetExternos;
	}

	public void importarPropriedades(ArrayList<String> prPropriedades) {
		if (propriedades == null)
			propriedades = prPropriedades;
		else
			for (String iPropriedade : prPropriedades)
				if (!propriedades.contains(iPropriedade))
					propriedades.add(iPropriedade);

	}

	public ArrayList<String> getSpritesIgnorados() {
		return spritesIgnorados;
	}

	public void setSpritesIgnorados(ArrayList<String> spritesIgnorados) {
		this.spritesIgnorados = spritesIgnorados;
	}

	public void importarTransportes(ArrayList<String> prTransportes) {
		if (transportes == null)
			propriedades = prTransportes;
		else
			for (String iTransporte : prTransportes)
				if (!transportes.contains(iTransporte))
					transportes.add(iTransporte);
	}

	public void importarSpriteSheetExternos(ArrayList<String> prSpriteSheet) {
		if (spriteSheetExternos == null)
			spriteSheetExternos = prSpriteSheet;
		else
			for (String iSpriteSheet : prSpriteSheet)
				if (!spriteSheetExternos.contains(iSpriteSheet)) {
					File lFile = new File(SalvarCarregar.arquivoLocalSpritesExternos, iSpriteSheet);
					if (lFile.exists()) {
						spriteSheetExternos.add(iSpriteSheet);
						SalvarCarregar.carregarImagemExterna(new File(lFile, SalvarCarregar.nomeDataSpritesExternos));
					}
				}
	}
}
