package main.configs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import main.Gerador;
import world.World;

public class ExConfig {
	private int worldWidth, worldHeight, worldHigh, amountOfTicks, TileSize, playerX, playerY, tamanhoPreSets;
	private ArrayList<String> propriedades, transportes;
	private String nomeAlturaUi, nomeLimparUi, nomeCaixaUi, nomePreencherUi, nomeSubstituirUi, nomeSpriteInteragivelUi,
			nomeSalva_construcaoUi;

	public ExConfig() {
		worldWidth = 20;
		worldHeight = 20;
		worldHigh = 7;
		amountOfTicks = 60;
		TileSize = 64;
		playerX = playerY = 0;
		propriedades = new ArrayList<>();
		transportes = new ArrayList<>();
		propriedadespadrao();
		transportespadrao();
		tamanhoPreSets = 32;
		nomeAlturaUi = "Altura: ";
		nomeLimparUi = "limpar_seleção";
		nomeCaixaUi = "caixa";
		nomePreencherUi = "preencher";
		nomeSubstituirUi = "substituir?";
		nomeSpriteInteragivelUi = "Adicionar sprite reajível";
		nomeSalva_construcaoUi = "salvar construção";
	}

	private void propriedadespadrao() {
		propriedades.add("Solid");
		propriedades.add("Speed");
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
		propriedades = SubTelaPropriedade.instance.getaCoPropriedades();
	}

	public static Integer[] determinarTmanhoMundo() {
		JTextField width = new JTextField(), height = new JTextField(), high = new JTextField();
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
		width.addKeyListener(l);
		height.addKeyListener(l);
		high.addKeyListener(l);
		Object[] message = { "Width (>= 20):", width, "Height (>= 20):", height, "High:", high };

		int option = JOptionPane.showConfirmDialog(null, message, "Tamanho do mundo", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			Integer[] lValores = new Integer[3];
			try {
				lValores[0] = Integer.parseInt(width.getText());
				lValores[1] = Integer.parseInt(height.getText());
				lValores[2] = Integer.parseInt(high.getText());

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

	public String getNomeSpriteInteragivelUi() {
		return nomeSpriteInteragivelUi;
	}

	public void setNomeSpriteInteragivelUi(String nomeSpriteInteragivelUi) {
		this.nomeSpriteInteragivelUi = nomeSpriteInteragivelUi;
	}

	public String getNomeSalva_construcaoUi() {
		return nomeSalva_construcaoUi;
	}

	public void setNomeSalva_construcaoUi(String nomeSalva_construcaoUi) {
		this.nomeSalva_construcaoUi = nomeSalva_construcaoUi;
	}
}
