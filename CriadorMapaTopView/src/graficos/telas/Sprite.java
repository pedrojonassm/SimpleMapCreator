package graficos.telas;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.annotation.JsonProperty;

import files.SalvarCarregar;
import main.Gerador;
import world.World;

public class Sprite {
	private String nome;
	private int posicao;

	public Sprite() {
	}

	public Sprite(@JsonProperty("nome") String prNome, @JsonProperty("posicao") int prPosicao) {
		nome = prNome;
		posicao = prPosicao;

	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	public BufferedImage pegarImagem() {
		if (World.spritesCarregados.containsKey(nome) && World.spritesCarregados.get(nome).length > posicao)
			return World.spritesCarregados.get(nome)[posicao];
		else if (!Gerador.aConfig.getSpritesIgnorados().contains(nome)) {
			if (JOptionPane.showConfirmDialog(null,
					"Não foi carregado nenhuma imagem com o nome '" + nome + "' Deseja Importala?\n"
							+ "Caso não, não será mostraddo nadda aqui",
					"Aviso", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				File lFile = new File(SalvarCarregar.arquivoLocalSpritesExternos, nome);
				if (lFile.exists()) {
					SalvarCarregar.carregarImagemExterna(new File(lFile, SalvarCarregar.nomeDataSpritesExternos));

				}
			} else {
				Gerador.aConfig.getSpritesIgnorados().add(nome);
			}
		}
		return new BufferedImage(Gerador.TS, Gerador.TS, BufferedImage.TYPE_INT_RGB);
	}

}
