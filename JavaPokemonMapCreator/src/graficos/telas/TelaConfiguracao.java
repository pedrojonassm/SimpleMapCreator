package graficos.telas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graficos.Ui;
import graficos.telas.subtelas.SubTelaEscada;
import main.Gerador;

public class TelaConfiguracao implements Tela {
	
	private int new_speed;
	
	private String[] opcoesName = {"Setar escadas", "Setar velocidade", "Setar/Adicionar propriedades"};
	private Rectangle[] opcoes;
	private int opcao;
	private Rectangle voltar, colocar_escadas;
	private ArrayList<Tela> subTelas;	
	public static TelaConfiguracao instance;
	
	public TelaConfiguracao() {
		instance = this;
		opcao = -1;
		opcoes = new Rectangle[opcoesName.length];
		for (int i = 0; i < opcoesName.length; i++) {
			opcoes[i] = new Rectangle(Ui.caixinha_dos_sprites.x, Ui.caixinha_dos_sprites.y+Ui.caixinha_dos_sprites.height/4+(i%Ui.maxItensPagina)*20, Ui.caixinha_dos_sprites.width, 20);
		}
		voltar = new Rectangle(Ui.caixinha_dos_sprites.width-Gerador.TS*4/6, Ui.caixinha_dos_sprites.y + Gerador.TS/4, Gerador.TS/2, Gerador.TS/2);
		colocar_escadas = new Rectangle(Ui.futuro_local_altura.x, Ui.futuro_local_altura.y+Ui.futuro_local_altura.height*2, 10, 10);
		subTelas = new ArrayList<>();
		subTelas.add(new SubTelaEscada());
		// Propriedades (paredes, lava, água, vip, etc.); ao exportar devera gerar um enum com base no tile.solido
		// Escadas (direcao também)
		// velocidade
		new_speed = 0;
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics prGraphics) {
		if (opcao == -1 || opcao >= subTelas.size()) {
			// renderizar opcoes para ir nas subtelas
			for (int i = 0; i < opcoesName.length; i++) {
				prGraphics.setColor(Color.green);
				prGraphics.drawRect(opcoes[i].x, opcoes[i].y, opcoes[i].width, opcoes[i].height);
				prGraphics.setColor(Color.white);
				prGraphics.drawString(opcoesName[i], Ui.caixinha_dos_sprites.x+20, Ui.caixinha_dos_sprites.y+15+Ui.caixinha_dos_sprites.height/4+(i%Ui.maxItensPagina)*20);
			}
		}else {
			prGraphics.drawImage(Ui.setas[2], voltar.x, voltar.y, voltar.width, voltar.height, null);
			subTelas.get(opcao).render(prGraphics);
		}
		
		int w1;
		String s = "";
		s = "speed: "+new_speed;
		w1 = prGraphics.getFontMetrics().stringWidth(s);
		prGraphics.drawString(s, Ui.caixinha_dos_sprites.x+Ui.caixinha_dos_sprites.width/2-w1/2, Ui.caixinha_dos_sprites.y+40);
		s = "pressione \"-\" para torná-la negativo";
		w1 = prGraphics.getFontMetrics().stringWidth(s);
		prGraphics.drawString(s, Ui.caixinha_dos_sprites.x+Ui.caixinha_dos_sprites.width/2-w1/2, Ui.caixinha_dos_sprites.y+60);
	}

	@Override
	public boolean clicou(int x, int y) {
		if (opcao < 0 || opcao >= opcoes.length) {
			for (int i = 0; i < opcoes.length; i++) {
				if (opcoes[i].contains(x, y)) {
					opcao = i;
					return true;
				}
			}
		}else if (voltar.contains(x, y)) {
			opcao = -1;
			return true;
		}else {
			return subTelas.get(opcao).clicou(x, y); 
		}
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		if (opcao >= 0 && opcao < opcoes.length) {
			return subTelas.get(opcao).cliquedireito(x, y);
		}
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (opcao >= 0 && opcao < opcoes.length) {
			return subTelas.get(opcao).trocar_pagina(x, y, prRodinha);
		}
		return false;
	}
	
	public void setNew_speed(int new_speed) {
		this.new_speed = new_speed;
	}
	public int getNew_speed() {
		return new_speed;
	}
	
	public int getOpcao() {
		return opcao;
	}
}
