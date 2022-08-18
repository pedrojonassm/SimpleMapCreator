package world;

import java.util.ArrayList;

public class Cidade {
	private ArrayList<Casa> casas; // casas da cidade
	private String nome; // nome da cidade, pq né
	private int tile; // posicao do tp da cidade, caso necessário
	
	public Cidade(int tile, String nome) {
		this.tile = tile;
		this.nome = nome;
		casas = new ArrayList<Casa>();
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public int getTile() {
		return tile;
	}
	public void setTile(int tile) {
		this.tile = tile;
	}
	public void addCasa(Casa c) {
		casas.add(c);
	}
	public void remove_casa(Casa c) {
		casas.remove(c);
	}
	
	public String salvar() {
		String retorno = nome+"--"+tile+"--";
		for (Casa c : casas) retorno += c.salvar();
		return retorno;
	}
	
	public Cidade carregar(String content) {
		String[] s = content.split("--");
		String nome = s[0];
		int tile =  Integer.parseInt(s[1]);
		ArrayList<Casa> casas = new ArrayList<Casa>();
		for (String str : s[2].split("\n")) casas.add(Casa.carregar(str));
		return null;
	}
}
