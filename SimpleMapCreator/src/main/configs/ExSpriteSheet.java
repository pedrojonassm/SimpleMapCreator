package main.configs;

public class ExSpriteSheet {
	protected String nome;
	protected int tamanho, totalSprites;

	public ExSpriteSheet() {
	}

	public ExSpriteSheet(String prNome, int prTamanho, int prTotalSprites) {
		nome = prNome;
		tamanho = prTamanho;
		totalSprites = prTotalSprites;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getTamanho() {
		return tamanho;
	}

	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public int getTotalSprites() {
		return totalSprites;
	}

	public void setTotalSprites(int totalSprites) {
		this.totalSprites = totalSprites;
	}

}
