package graficos.telas;

import java.awt.image.BufferedImage;

import com.fasterxml.jackson.annotation.JsonProperty;

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
		return new BufferedImage(Gerador.TS, Gerador.TS, BufferedImage.TYPE_INT_RGB);
	}

}
