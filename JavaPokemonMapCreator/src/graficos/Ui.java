package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import files.salvarCarregar;
import graficos.telas.Tela;
import graficos.telas.TelaCidadeCasa;
import graficos.telas.TelaConfiguracao;
import graficos.telas.TelaConstrucoes;
import graficos.telas.TelaSprites;
import main.Gerador;
import world.Build;
import world.Camera;
import world.Cidade;
import world.Tile;
import world.World;

public class Ui {
	public static BufferedImage[] setas, sprite_opcoes;
	public static boolean mostrar, colocar_parede, sprite_reajivel, colocar_escada, substituir;
	public static Rectangle caixinha_dos_sprites, futuro_local_altura,
	preencher_tudo, fazer_caixa, limpar_selecao, salvar_construcao, substitui,
	colocar_escadas, direcao_escadas, caixa_das_opcoes, caixa_sprite_reajivel;
	private Rectangle[] escadas;
	private static final String colocar_as_paredes = "setar paredes", colocar_as_escadas= "setar escadas", altura = "Altura: ", limpar = "limpar_seleção", caixa = "caixa", preencher = "preencher", substituira = "substituir?", interactive_sprite = "Adicionar sprite reajível", salva_construcao = "salvar construção";
	public static final String[] opcoes = {"colocar sprites", "configurar", "colocar construções", "criar casas/cidades"}, escada = {"colisao", "clique direito", "Buraco aberto", "Buraco fechado"};
	public static int opcao;
	public static ArrayList<Tela> telas;
	public static int modo_escadas, escadas_direction; // corresponde a qual sprite será guardado os sprites nos tiles ex: 0 = chao, 1 = paredes, 2 = decoracoes, etc.
	public static ArrayList<Tile> aTilesSelecionados;
	private static String a_selecionar;
	
	public Ui() {
		telas = new ArrayList<>();
		carregar_sprites();
		opcao = 0;
		modo_escadas = 0;
		mostrar = substituir = true;
		colocar_parede = sprite_reajivel = colocar_escada = false;
		futuro_local_altura = new Rectangle(Gerador.WIDTH-100, 20, 10, 10);
		caixa_sprite_reajivel = new Rectangle(futuro_local_altura.x, futuro_local_altura.y+futuro_local_altura.height*2, 10, 10);
		colocar_escadas = new Rectangle(caixa_sprite_reajivel.x, caixa_sprite_reajivel.y+caixa_sprite_reajivel.height*2, 10, 10);
		caixa_das_opcoes = new Rectangle(Gerador.WIDTH/2 - (opcoes.length)/2*Gerador.TS, -Gerador.TS, Gerador.TS*opcoes.length, Gerador.TS);
		escadas = new Rectangle[4];
		escadas[0] = new Rectangle(colocar_escadas.x, colocar_escadas.y+colocar_escadas.height*2, 10, 10);
		escadas[1] = new Rectangle(escadas[0].x, escadas[0].y+escadas[0].height*2, 10, 10);
		escadas[2] = new Rectangle(escadas[1].x, escadas[1].y+escadas[1].height*2, 10, 10);
		escadas[3] = new Rectangle(escadas[2].x, escadas[2].y+escadas[2].height*2, 10, 10);
		caixinha_dos_sprites = new Rectangle(0, 8, Gerador.quadrado.width*5, Gerador.quadrado.width*11);
		direcao_escadas = new Rectangle(colocar_escadas.x+colocar_escadas.width*2, colocar_escadas.y-colocar_escadas.height,32, 32);
		preencher_tudo = new Rectangle(Gerador.WIDTH-90, Gerador.HEIGHT/2, 90, 20);
		substitui = new Rectangle(preencher_tudo.x+preencher_tudo.width/3, preencher_tudo.y-60, 10, 10);
		fazer_caixa = new Rectangle(Gerador.WIDTH-90, Gerador.HEIGHT/2+preencher_tudo.height, preencher_tudo.width, preencher_tudo.height);
		limpar_selecao = new Rectangle(Gerador.WIDTH-90, Gerador.HEIGHT/2-preencher_tudo.height, preencher_tudo.width, preencher_tudo.height);
		salvar_construcao = new Rectangle(Gerador.WIDTH-90, fazer_caixa.y+preencher_tudo.height, preencher_tudo.width, preencher_tudo.height);
		aTilesSelecionados = new ArrayList<>();
		telas.add(new TelaSprites());
		telas.add(new TelaConfiguracao());
		telas.add(new TelaConstrucoes());
		telas.add(new TelaCidadeCasa());
	}
	
	private void carregar_sprites() {
		Spritesheet spr = new Spritesheet("/setas.png", 32);
		setas = new BufferedImage[spr.getQuadradosX()*spr.getQuadradosY()];
		for (int i = 0; i < setas.length; i++) {
			setas[i] = spr.getAsset(i);
		}
		escadas_direction = 0;
		
		spr = new Spritesheet("/opcoes.png", 64);
		sprite_opcoes = new BufferedImage[spr.getQuadradosX()*spr.getQuadradosY()];
		for (int i = 0; i < sprite_opcoes.length; i++) {
			sprite_opcoes[i] = spr.getAsset(i);
		}
	}

	public Rectangle getCaixinha_dos_sprites() {
		return caixinha_dos_sprites;
	}
	
	public void tick() {
		if (caixa_das_opcoes.intersects(new Rectangle(Gerador.quadrado.x, Gerador.quadrado.y-Gerador.TS, Gerador.TS, Gerador.TS))) {
			a_selecionar = opcoes[(Gerador.quadrado.x-caixa_das_opcoes.x)/Gerador.TS];
			if (caixa_das_opcoes.y < 0) {
				caixa_das_opcoes.y++;
			}
		} else {
			a_selecionar = null;
			if (caixa_das_opcoes.y > -Gerador.TS) {
				caixa_das_opcoes.y--;
			}
		}
	}
	
	public void render(Graphics g) {
		int w1;
		
		if (a_selecionar != null) {
			w1 = g.getFontMetrics().stringWidth(a_selecionar);
			g.drawString(a_selecionar, caixa_das_opcoes.x + caixa_das_opcoes.width/2 - w1/2, caixa_das_opcoes.y+Gerador.TS+20);
			g.drawRect(caixa_das_opcoes.x, caixa_das_opcoes.y, caixa_das_opcoes.width, caixa_das_opcoes.height);
			for (int i = 0; i < opcoes.length; i++) {
				g.drawImage(sprite_opcoes[i], caixa_das_opcoes.x+i*Gerador.TS, caixa_das_opcoes.y, null);
				if (opcao == i) {
					g.setColor(new Color(0, 255, 0, 50));
					g.fillRect(caixa_das_opcoes.x+i*Gerador.TS, caixa_das_opcoes.y, Gerador.TS, Gerador.TS);
					g.setColor(Color.white);
				}
			}
		}
		
		g.setColor(new Color(255, 255, 0, 50));
		int dx, dy;
		for (Tile iTile : aTilesSelecionados) {
			dx = iTile.getX() - Camera.x - (iTile.getZ()-Gerador.player.getZ())*Gerador.quadrado.width;
			dy = iTile.getY() - Camera.y - (iTile.getZ()-Gerador.player.getZ())*Gerador.quadrado.height;
			if (dx+Gerador.quadrado.width >= 0 && dx < Gerador.WIDTH && dy+Gerador.quadrado.height >= 0 && dy < Gerador.HEIGHT)
				g.fillRect(dx, dy, Gerador.quadrado.width, Gerador.quadrado.height);
		}
		
		if (aTilesSelecionados.size() > 0) {
			g.setColor(Color.green);
			if  (opcao == 0 || opcao == 1) {
				desenhar_opcoes(g);
			}
			
			w1 = g.getFontMetrics().stringWidth(substituira);
			g.setColor(Color.white);
			if (substituir) {
				g.fillRect(substitui.x, substitui.y, substitui.width, substitui.height);
			}else {
				g.drawRect(substitui.x, substitui.y, substitui.width, substitui.height);
			}
			g.drawString(substituira, substitui.x-substitui.width-w1, substitui.y+substitui.height);
		}
		
		if (mostrar) {
			g.setColor(Color.black);
			g.fillRect(caixinha_dos_sprites.x, caixinha_dos_sprites.y, caixinha_dos_sprites.width, caixinha_dos_sprites.height);
			g.setColor(Color.white);
			g.drawRect(caixinha_dos_sprites.x, caixinha_dos_sprites.y, caixinha_dos_sprites.width, caixinha_dos_sprites.height);
			
			telas.get(opcao).render(g);
			
			g.setColor(Color.white);
			w1 = g.getFontMetrics().stringWidth(altura+(Gerador.player.getZ()+1));
			g.drawString(altura+(Gerador.player.getZ()+1), futuro_local_altura.x-w1 + futuro_local_altura.width, Gerador.HEIGHT-futuro_local_altura.y-15);
		}
	}
	
	private void desenhar_opcoes(Graphics g) {
		g.drawRect(preencher_tudo.x, preencher_tudo.y, preencher_tudo.width, preencher_tudo.height);
		g.drawRect(fazer_caixa.x, fazer_caixa.y, fazer_caixa.width, fazer_caixa.height);
		g.drawRect(limpar_selecao.x, limpar_selecao.y, limpar_selecao.width, limpar_selecao.height);
		g.drawRect(salvar_construcao.x, salvar_construcao.y, salvar_construcao.width, salvar_construcao.height);
		g.drawString(preencher, preencher_tudo.x, preencher_tudo.y+10);
		g.drawString(caixa, fazer_caixa.x, fazer_caixa.y+10);
		g.drawString(limpar, limpar_selecao.x, limpar_selecao.y+10);
		g.drawString(salva_construcao, salvar_construcao.x, salvar_construcao.y+10);
	}

	public boolean clicou(int x, int y) {
		//*
		if (futuro_local_altura.contains(x, y)) {
			colocar_escada = sprite_reajivel = false;
			colocar_parede = !colocar_parede;
			return true;
		}else if(caixa_sprite_reajivel.contains(x, y)){
			sprite_reajivel = !sprite_reajivel;
			colocar_escada = colocar_parede = false;
			return true;
		}else  if (colocar_escadas.contains(x, y)) {
			colocar_escada = !colocar_escada;
			colocar_parede = sprite_reajivel = false;
			return true;
		}
		//*/
		if (telas.get(opcao).clicou(x, y)) {
			return true;
			//*
			//*/
		}else if(caixa_das_opcoes.contains(x, y)) {
			opcao = (x-caixa_das_opcoes.x)/Gerador.TS;
			return true;
		}else if (aTilesSelecionados.size() > 0) {
			if (substitui.contains(x, y)) {
				substituir = !substituir;
				return true;
			}else if (limpar_selecao.contains(x, y)) {
				aTilesSelecionados.clear();
				return true;
			}else if (preencher_tudo.contains(x, y)) {
				World.fill(aTilesSelecionados);
				return true;
			}else if  (fazer_caixa.contains(x, y)) {
				World.empty(aTilesSelecionados);
				return true;
			}else if (salvar_construcao.contains(x, y)) {
				salvarCarregar.salvar_construcao(aTilesSelecionados);
			}
		}
		
		for (int i = 0; i < escadas.length; i++) {
			if (escadas[i].contains(x, y)) {
				modo_escadas = i;
				return true;
			}
		}
			
		return false;
	}

	public boolean cliquedireito(int x, int y) {
		if (mostrar && caixinha_dos_sprites.contains(x, y)) {
			return telas.get(opcao).cliquedireito(x, y);
		}
		return false;
	}
	
	public boolean trocar_pagina(int x, int y, int prRodinha) {
		if (mostrar) {
			telas.get(opcao).trocar_pagina(x, y, prRodinha);
		}
		return false;
	}

	public boolean selecionarTile(int prPos) {
		Tile t = World.pegarAdicionarTileMundo(prPos);
		if (aTilesSelecionados.size() == 0)
			aTilesSelecionados.add(t);
		else if (!removerTileLista(prPos)) {
			boolean lAdicionado = false;
			for (int i = 0; i < aTilesSelecionados.size(); i++) {
				Tile iTile = aTilesSelecionados.get(i);
				if (prPos < iTile.getaPos()) {
					lAdicionado = true;
					aTilesSelecionados.add(i, t);
					break;
				}
			}
			if (!lAdicionado)
				aTilesSelecionados.add(t);
		}
		return true;
	}
	
	private boolean removerTileLista(int prPos) {
		int lPosicaoLista = Tile.tileExisteLista(prPos, aTilesSelecionados);
		if (lPosicaoLista >= 0 && lPosicaoLista < aTilesSelecionados.size()) {
			aTilesSelecionados.remove(lPosicaoLista);
			return true;
		}
		return false;
	}

}