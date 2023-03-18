package graficos.telas.configuracao.subtelas;

import java.awt.Graphics;

import graficos.Ui;
import graficos.telas.Tela;

public class SubTelaVelocidade implements Tela {
	
	private int new_speed;
	
	public static SubTelaVelocidade instance;
	
	public SubTelaVelocidade() {
		new_speed = 0;
		instance = this;
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Graphics prGraphics) {
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
		return false;
	}

	@Override
	public boolean cliquedireito(int x, int y) {
		return false;
	}

	@Override
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (prRodinha > 0) 
			new_speed++;
		else 
			new_speed--;
		
		
		return true;
	}
	
	public void setNew_speed(int new_speed) {
		this.new_speed = new_speed;
	}
	public int getNew_speed() {
		return new_speed;
	}

}
