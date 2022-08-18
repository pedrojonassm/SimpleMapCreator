package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import files.salvarCarregar;
import main.Gerador;
import world.Build;
import world.Camera;
import world.Cidade;
import world.Tile;
import world.World;

public class Ui {
	private static BufferedImage[] setas, sprite_opcoes;
	public static boolean mostrar, colocar_parede, sprite_reajivel, colocar_escada, substituir;
	private Rectangle colocar_paredes, caixinha_dos_sprites, caixinha_dos_livros,
	preencher_tudo, fazer_caixa, limpar_selecao, salvar_construcao, substitui, // preencher coloca em todos os tiles, sem excessão, já a caixa deixa a parte de "dentro" vazia
	colocar_escadas, direcao_escadas, caixa_das_opcoes, caixa_sprite_reajivel, adicionar_nova_cidade, voltar;
	private Rectangle[] escadas;
	private static final String colocar_as_paredes = "setar paredes", colocar_as_escadas= "setar escadas", tile_nivel = "Nível nos tiles: ", altura = "Altura: ", limpar = "limpar_seleção", caixa = "caixa", preencher = "preencher", substituira = "substituir?", interactive_sprite = "Adicionar sprite reajível", salva_construcao = "salvar construção";
	public static final String[] opcoes = {"colocar sprites", "configurar", "colocar construções", "criar casas/cidades"}, escada = {"colisao", "clique direito", "Buraco aberto", "Buraco fechado"};
	private int max_sprites_por_pagina, livro, pagina_livros, max_pagina_livros, max_livros_por_pagina, livro_tile_pego, index_tile_pego, new_speed;
	private ArrayList<Integer> pagina, max_pagina, comecar_por, atual, sprites;
	private static ArrayList<ArrayList<Tile>> tiles_salvos;
	private static ArrayList<String> nome_livros;
	public static ArrayList<Integer> sprite_selecionado, array, lista; // esses dois pegam a imagem na lista de imagens estáticas World.sprites.get(array)[lista]
	public static int tiles_nivel, max_tiles_nivel, modo_escadas, escadas_direction; // corresponde a qual sprite será guardado os sprites nos tiles ex: 0 = chao, 1 = paredes, 2 = decoracoes, etc.
	public Tile pontoA, pontoB; // selecione 2 pontos para preenche-lo com as opcoes abaixo
	public static String opcao;
	private static String a_selecionar;
	private ArrayList<Build> construcoes;
	private int index_construcao_selecionada = -1;
	private static int max_construcoes_por_pagina, pagina_construcoes, salvar_nesse_livro;
	private ArrayList<Cidade> cidades;
	private Cidade cidade_selecionada;
	
	public Ui() {
		carregar_sprites();
		cidades = new ArrayList<Cidade>();
		opcao = opcoes[0];
		modo_escadas = salvar_nesse_livro = 0;
		livro = 0; // os livros podem ser adicionados depois, a fim de criar novas páginas para maior facilidade de achar sprites
		pagina = new ArrayList<Integer>();
		max_pagina = new ArrayList<Integer>();
		comecar_por = new ArrayList<Integer>();
		atual = new ArrayList<Integer>();
		sprites = new ArrayList<Integer>();
		pagina.add(0);
		max_pagina.add(0);
		comecar_por.add(0);
		atual.add(0);
		sprites.add(0);
		tiles_salvos = new ArrayList<ArrayList<Tile>>();
		nome_livros = new ArrayList<String>();
		nome_livros.add("todos os sprites");
		mostrar = substituir = true;
		colocar_parede = sprite_reajivel = colocar_escada = false;
		colocar_paredes = new Rectangle(Gerador.WIDTH-100, 20, 10, 10);
		caixa_sprite_reajivel = new Rectangle(colocar_paredes.x, colocar_paredes.y+colocar_paredes.height*2, 10, 10);
		colocar_escadas = new Rectangle(caixa_sprite_reajivel.x, caixa_sprite_reajivel.y+caixa_sprite_reajivel.height*2, 10, 10);
		caixa_das_opcoes = new Rectangle(Gerador.WIDTH/2 - (opcoes.length)/2*Gerador.TS, -Gerador.TS, Gerador.TS*opcoes.length, Gerador.TS);
		escadas = new Rectangle[4];
		escadas[0] = new Rectangle(colocar_escadas.x, colocar_escadas.y+colocar_escadas.height*2, 10, 10);
		escadas[1] = new Rectangle(escadas[0].x, escadas[0].y+escadas[0].height*2, 10, 10);
		escadas[2] = new Rectangle(escadas[1].x, escadas[1].y+escadas[1].height*2, 10, 10);
		escadas[3] = new Rectangle(escadas[2].x, escadas[2].y+escadas[2].height*2, 10, 10);
		caixinha_dos_sprites = new Rectangle(0, 8, Gerador.quadrado.width*5, Gerador.quadrado.width*11);
		direcao_escadas = new Rectangle(colocar_escadas.x+colocar_escadas.width*2, colocar_escadas.y-colocar_escadas.height,32, 32);
		caixinha_dos_livros = new Rectangle(caixinha_dos_sprites.x + caixinha_dos_sprites.width, caixinha_dos_sprites.y, Gerador.quadrado.width/3, caixinha_dos_sprites.height);
		preencher_tudo = new Rectangle(Gerador.WIDTH-90, Gerador.HEIGHT/2, 90, 20);
		substitui = new Rectangle(preencher_tudo.x+preencher_tudo.width/3, preencher_tudo.y-60, 10, 10);
		fazer_caixa = new Rectangle(Gerador.WIDTH-90, Gerador.HEIGHT/2+preencher_tudo.height, preencher_tudo.width, preencher_tudo.height);
		limpar_selecao = new Rectangle(Gerador.WIDTH-90, Gerador.HEIGHT/2-preencher_tudo.height, preencher_tudo.width, preencher_tudo.height);
		salvar_construcao = new Rectangle(Gerador.WIDTH-90, fazer_caixa.y+preencher_tudo.height, preencher_tudo.width, preencher_tudo.height);
		adicionar_nova_cidade = new Rectangle(caixinha_dos_sprites.x + caixinha_dos_sprites.width - 40, caixinha_dos_sprites.y+80, 20, 20);
		voltar = new Rectangle(caixinha_dos_sprites.x + 20, adicionar_nova_cidade.y, adicionar_nova_cidade.width, adicionar_nova_cidade.height);
		sprite_selecionado = new ArrayList<Integer>();
		array = new ArrayList<Integer>();
		lista = new ArrayList<Integer>();
		max_sprites_por_pagina= (caixinha_dos_sprites.width/Gerador.quadrado.width)*(caixinha_dos_sprites.height/Gerador.quadrado.width);
		pagina_livros = 0;
		max_livros_por_pagina = caixinha_dos_livros.height/caixinha_dos_livros.width;
		tiles_nivel = 0;
		max_tiles_nivel = 4;
		max_construcoes_por_pagina = 26;
		pagina_construcoes = 0;
		cidades.add(new Cidade(5, "teste1"));
		cidades.add(new Cidade(6, "teste2"));
		cidades.add(new Cidade(7, "teste3"));
	}
	
	public void setNew_speed(int new_speed) {
		this.new_speed = new_speed;
	}
	public int getNew_speed() {
		return new_speed;
	}
	
	public void adicionar_construcao(Build b) {
		construcoes.add(b);
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

	public static String pegar_nome_livro(int index) {
		return nome_livros.get(index);
	}
	
	public static ArrayList<Tile> pegar_livro(int index) {
		return tiles_salvos.get(index);
	}
	
	public Rectangle getCaixinha_dos_sprites() {
		return caixinha_dos_sprites;
	}
	
	public void max_pagina_por_total_de_sprites(int total_sprites) {
		int divisao = ((caixinha_dos_sprites.width/Gerador.quadrado.width)*(caixinha_dos_sprites.height/Gerador.quadrado.width));
		max_pagina.set(0, (int) (total_sprites/divisao));
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
				if (opcao.equalsIgnoreCase(opcoes[i])) {
					g.setColor(new Color(0, 255, 0, 50));
					g.fillRect(caixa_das_opcoes.x+i*Gerador.TS, caixa_das_opcoes.y, Gerador.TS, Gerador.TS);
					g.setColor(Color.white);
				}
			}
		}else if (opcao.equalsIgnoreCase(opcoes[3]) && adicionar_nova_cidade.contains(Gerador.quadrado.x, Gerador.quadrado.y)) {
			w1 = g.getFontMetrics().stringWidth("Criar nova cidade");
			g.drawString("Criar nova cidade", adicionar_nova_cidade.x+w1/2, adicionar_nova_cidade.y);
		}
		
		g.setColor(new Color(255, 255, 0, 50));
		int dx, dy;
		if (pontoA != null) {
			dx = pontoA.getX() - Camera.x - (pontoA.getZ()-Gerador.player.getZ())*Gerador.quadrado.width;
			dy = pontoA.getY() - Camera.y - (pontoA.getZ()-Gerador.player.getZ())*Gerador.quadrado.height;
			g.fillRect(dx, dy, Gerador.quadrado.width, Gerador.quadrado.height);
		}
		if (pontoB != null) {
			dx = pontoB.getX() - Camera.x - (pontoB.getZ()-Gerador.player.getZ())*Gerador.quadrado.width;
			dy = pontoB.getY() - Camera.y - (pontoB.getZ()-Gerador.player.getZ())*Gerador.quadrado.height;
			g.fillRect(dx, dy, Gerador.quadrado.width, Gerador.quadrado.height);
		}
		
		if (pontoA != null || pontoB != null) {
			g.setColor(Color.green);
			if  (opcao.equalsIgnoreCase(opcoes[0]) || opcao.equalsIgnoreCase(opcoes[1])) {
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
			
			if  (opcao.equalsIgnoreCase(opcoes[0])) {
				renderizar_colocar_sprites(g);
			}else if (opcao.equalsIgnoreCase(opcoes[1])) {
				renderizar_configurar(g);
			}else if (opcao.equalsIgnoreCase(opcoes[2])) {
				renderizar_construcoes(g);
			}else if (opcao.equalsIgnoreCase(opcoes[3])) {
				renderizar_criar_casas_e_cidades(g);
			}
		}
	}
	
	private void renderizar_criar_casas_e_cidades(Graphics g) {
		g.setColor(Color.green);
		g.drawRect(adicionar_nova_cidade.x, adicionar_nova_cidade.y, adicionar_nova_cidade.width, adicionar_nova_cidade.height);
		g.drawLine(adicionar_nova_cidade.x, adicionar_nova_cidade.y + adicionar_nova_cidade.height/2, adicionar_nova_cidade.x + adicionar_nova_cidade.width, adicionar_nova_cidade.y + adicionar_nova_cidade.height/2);
		g.drawLine(adicionar_nova_cidade.x + adicionar_nova_cidade.width/2, adicionar_nova_cidade.y, adicionar_nova_cidade.x + adicionar_nova_cidade.width/2, adicionar_nova_cidade.y + adicionar_nova_cidade.height);
		g.setColor(Color.white);
		if (cidade_selecionada == null) {
			desenhar_cidades(g);
		}
		else{
			desenhar_casas(g);
		}
		
		
	}

	private void desenhar_cidades(Graphics g) {
		// Agora é só fazer desenhas a lista das cidades
		g.drawString("CIDADES:", caixinha_dos_sprites.x+caixinha_dos_sprites.width/2 - g.getFontMetrics().stringWidth("CIDADES:")/2, 60);
	}

	private void desenhar_casas(Graphics g) {
		g.drawString(cidade_selecionada.getNome(), caixinha_dos_sprites.x+caixinha_dos_sprites.width/2 - g.getFontMetrics().stringWidth(cidade_selecionada.getNome())/2, 60);
		g.drawImage(setas[2], voltar.x, voltar.y, voltar.width, voltar.height, null);
		
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

	private void renderizar_configurar(Graphics g) {
		int w1;
		String s = "";
		if (colocar_parede)	g.fillRect(colocar_paredes.x, colocar_paredes.y, colocar_paredes.width, colocar_paredes.height);
		else g.drawRect(colocar_paredes.x, colocar_paredes.y, colocar_paredes.width, colocar_paredes.height);
		if (sprite_reajivel) g.fillRect(caixa_sprite_reajivel.x, caixa_sprite_reajivel.y, caixa_sprite_reajivel.width, caixa_sprite_reajivel.height);
		else g.drawRect(caixa_sprite_reajivel.x, caixa_sprite_reajivel.y, caixa_sprite_reajivel.width, caixa_sprite_reajivel.height);
		if (colocar_escada) {
			g.fillRect(colocar_escadas.x, colocar_escadas.y, colocar_escadas.width, colocar_escadas.height);
		}
		else g.drawRect(colocar_escadas.x, colocar_escadas.y, colocar_escadas.width, colocar_escadas.height);
		if (!(colocar_escada || sprite_reajivel || colocar_parede)) {
			s = "speed: "+new_speed;
			w1 = g.getFontMetrics().stringWidth(s);
			g.drawString(s, caixinha_dos_sprites.x+caixinha_dos_sprites.width/2-w1/2, caixinha_dos_sprites.y+40);
			s = "pressione \"-\" para torná-la negativo";
			w1 = g.getFontMetrics().stringWidth(s);
			g.drawString(s, caixinha_dos_sprites.x+caixinha_dos_sprites.width/2-w1/2, caixinha_dos_sprites.y+60);
		}
		s = "Água";
		w1 = g.getFontMetrics().stringWidth(s);
		g.drawString(s, colocar_paredes.x-colocar_paredes.width-w1, colocar_paredes.y+colocar_paredes.height);
		s = "Lava";
		w1 = g.getFontMetrics().stringWidth(s);
		g.drawString(s, caixa_sprite_reajivel.x-caixa_sprite_reajivel.width-w1, caixa_sprite_reajivel.y+caixa_sprite_reajivel.height);
		s = "Vip";
		w1 = g.getFontMetrics().stringWidth(s);
		g.drawString(s, colocar_escadas.x-colocar_escadas.width-w1, colocar_escadas.y+colocar_escadas.height);
		
	}

	private void renderizar_construcoes(Graphics g) {
		g.drawRect(caixinha_dos_sprites.x+50, caixinha_dos_sprites.y+10, caixinha_dos_sprites.width-100, 150);
		for (int i = pagina_construcoes*max_construcoes_por_pagina; i < max_construcoes_por_pagina*(pagina_construcoes+1) && i < construcoes.size(); i++) {
			//System.out.println(max_construcoes_por_pagina*(pagina_construcoes+1));
			if (index_construcao_selecionada == i) {
				g.setColor(Color.blue);
				g.drawImage(construcoes.get(i).getImage(), caixinha_dos_sprites.x+50, caixinha_dos_sprites.y+10, caixinha_dos_sprites.width-100+1, 150+1, null);
			}else {
				g.setColor(Color.red);
			}
			g.drawRect(caixinha_dos_sprites.x, caixinha_dos_sprites.y+caixinha_dos_sprites.height/4+(i%max_construcoes_por_pagina)*20, caixinha_dos_sprites.width, 20);
			g.setColor(Color.white);
			g.drawString(construcoes.get(i).getFile().getName(), caixinha_dos_sprites.x+20, caixinha_dos_sprites.y+15+caixinha_dos_sprites.height/4+(i%max_construcoes_por_pagina)*20);
		}
		
	}

	private void pegar_construcao_salva(int x, int y) {
		int novo = (y-(caixinha_dos_sprites.y+caixinha_dos_sprites.height/4))/20;
		if (novo == index_construcao_selecionada) {
			index_construcao_selecionada = -1;
		}else if (novo >= 0 && novo < construcoes.size()) {
			index_construcao_selecionada = novo;
		}
	}

	private void renderizar_colocar_sprites(Graphics g) {
		int w1;
		
		g.setColor(Color.white);
		if (colocar_parede) g.fillRect(colocar_paredes.x, colocar_paredes.y, colocar_paredes.width, colocar_paredes.height);
		else g.drawRect(colocar_paredes.x, colocar_paredes.y, colocar_paredes.width, colocar_paredes.height);
		if (sprite_reajivel) g.fillRect(caixa_sprite_reajivel.x, caixa_sprite_reajivel.y, caixa_sprite_reajivel.width, caixa_sprite_reajivel.height);
		else g.drawRect(caixa_sprite_reajivel.x, caixa_sprite_reajivel.y, caixa_sprite_reajivel.width, caixa_sprite_reajivel.height);
		if (colocar_escada) {
			g.fillRect(colocar_escadas.x, colocar_escadas.y, colocar_escadas.width, colocar_escadas.height);
			g.drawImage(setas[escadas_direction], direcao_escadas.x, direcao_escadas.y, null);
			for (int i = 0; i < escadas.length; i++) {
				if (i == modo_escadas) {
					g.fillRect(escadas[i].x, escadas[i].y, escadas[i].width, escadas[i].height);
				}else {
					g.drawRect(escadas[i].x, escadas[i].y, escadas[i].width, escadas[i].height);
				}
				
				w1 = g.getFontMetrics().stringWidth(escada[i]);
				g.drawString(escada[i], escadas[i].x-escadas[i].width-w1, escadas[i].y+escadas[i].height);
			}
		}
		else
			g.drawRect(colocar_escadas.x, colocar_escadas.y, colocar_escadas.width, colocar_escadas.height);
		
		w1 = g.getFontMetrics().stringWidth(colocar_as_paredes);
		g.drawString(colocar_as_paredes, colocar_paredes.x-colocar_paredes.width-w1, colocar_paredes.y+colocar_paredes.height);
		w1 = g.getFontMetrics().stringWidth(interactive_sprite);
		g.drawString(interactive_sprite, caixa_sprite_reajivel.x-caixa_sprite_reajivel.width-w1, caixa_sprite_reajivel.y+caixa_sprite_reajivel.height);
		w1 = g.getFontMetrics().stringWidth(colocar_as_escadas);
		g.drawString(colocar_as_escadas, colocar_escadas.x-colocar_escadas.width-w1, colocar_escadas.y+colocar_escadas.height);
		g.drawRect(caixinha_dos_livros.x, caixinha_dos_livros.y, caixinha_dos_livros.width, caixinha_dos_livros.height);
		desenhar_livros(g);
		desenhar_sprites_a_selecionar(g);
		if (caixinha_dos_livros.contains(Gerador.quadrado.x, Gerador.quadrado.y)) mostrar_nome_livro(g);
		
		g.setColor(Color.white);
		w1 = g.getFontMetrics().stringWidth(tile_nivel+(tiles_nivel+1));
		g.drawString(tile_nivel+(tiles_nivel+1), colocar_paredes.x-w1 + colocar_paredes.width, Gerador.HEIGHT-colocar_paredes.y);
		w1 = g.getFontMetrics().stringWidth(altura+(Gerador.player.getZ()+1));
		g.drawString(altura+(Gerador.player.getZ()+1), colocar_paredes.x-w1 + colocar_paredes.width, Gerador.HEIGHT-colocar_paredes.y-15);
		
	}

	private void mostrar_nome_livro(Graphics g) {
		g.setColor(Color.white);
		int py = (Gerador.quadrado.y-caixinha_dos_livros.y)/caixinha_dos_livros.width + pagina_livros*max_livros_por_pagina;
		if (py >= max_livros_por_pagina*(pagina_livros+1)) return;
		String nome = null;
		if (py < nome_livros.size()) {
			nome = nome_livros.get(py);
		}else if (py == nome_livros.size()) {
			nome = "Adicionar novo livro";
		}
		if (nome != null) {
			g.drawString(nome, Gerador.quadrado.x + Gerador.quadrado.width, Gerador.quadrado.y+10);
		}
	}

	private void desenhar_livros(Graphics g) {
		int y = caixinha_dos_livros.y;
		g.setColor(Color.blue);
		int i;
		for (i = max_livros_por_pagina*pagina_livros; i < max_livros_por_pagina*(pagina_livros+1) && i < nome_livros.size(); i++) {
			if (i == livro) {
				g.setColor(Color.red);
				g.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
				g.setColor(Color.blue);
			}else if (i == salvar_nesse_livro && i != 0) {
				g.setColor(Color.green);
				g.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
				g.setColor(Color.blue);
			}else {
				g.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
			}
			y+=caixinha_dos_livros.width;
		}
		if (i < max_livros_por_pagina*(pagina_livros+1)) {
			g.drawRect(caixinha_dos_livros.x, y, caixinha_dos_livros.width, caixinha_dos_livros.width);
			g.setColor(Color.green);
			g.drawLine(caixinha_dos_livros.x+caixinha_dos_livros.width/2, y, caixinha_dos_livros.x+caixinha_dos_livros.width/2, y+caixinha_dos_livros.width);
			g.drawLine(caixinha_dos_livros.x, y+caixinha_dos_livros.width/2, caixinha_dos_livros.x+caixinha_dos_livros.width, y+caixinha_dos_livros.width/2);
		}
	}

	public void atualizar_caixinha() {
		comecar_por.set(livro, pagina.get(livro)*max_sprites_por_pagina);
		int atual = 0, sprites = 0;
		for (sprites = 0; sprites < World.sprites_do_mundo.size() && atual < comecar_por.get(livro); sprites++) {
			if (World.sprites_do_mundo.get(sprites).length <= comecar_por.get(livro)-atual) {
				atual += World.sprites_do_mundo.get(sprites).length;
			}else {
				atual += comecar_por.get(livro)-atual;
				break;
			}
		}
		for (int i = 0; i < sprites; i++) {
			atual -= World.sprites_do_mundo.get(i).length;
		}
		this.atual.set(livro, atual);
		this.sprites.set(livro, sprites);
	}

	private void desenhar_sprites_a_selecionar(Graphics g) {
		int desenhando = 0, k = atual.get(livro), spr = sprites.get(livro);
		if (livro == 0) 
			while(spr < World.sprites_do_mundo.size()) {
				desenhar_no_quadrado(World.sprites_do_mundo.get(spr)[k], desenhando, g);
				k++;
				desenhando++;
				if (k >= World.sprites_do_mundo.get(spr).length) {
					spr++;
					k = 0;
				}
				if (desenhando >= max_sprites_por_pagina) {
					break;
				}
			}
		else {
			ArrayList<Tile> tiles = tiles_salvos.get(livro-1);
			int x, y;
			for (int i = 0; i < max_sprites_por_pagina && i+(max_sprites_por_pagina*pagina.get(livro)) < tiles.size(); i++) {
				x = desenhando%(caixinha_dos_sprites.width/Gerador.quadrado.width);
				y = desenhando/(caixinha_dos_sprites.width/Gerador.quadrado.width);
				ArrayList<BufferedImage> lDesenhoAtual = tiles.get(i+(max_sprites_por_pagina*pagina.get(livro))).getSprite_atual();
				for (BufferedImage iBufferedImage : lDesenhoAtual)
					g.drawImage(iBufferedImage, x*Gerador.quadrado.width+caixinha_dos_sprites.x, y*Gerador.quadrado.width+caixinha_dos_sprites.y, Gerador.quadrado.width, Gerador.quadrado.height, null);
				
				if (i+(max_sprites_por_pagina*pagina.get(livro)) == index_tile_pego && livro == livro_tile_pego) {
					g.setColor(new Color(0, 255, 0, 50));
					g.fillRect(x*Gerador.quadrado.width+caixinha_dos_sprites.x, y*Gerador.quadrado.width+caixinha_dos_sprites.y, Gerador.quadrado.width, Gerador.quadrado.height);
				}
				
				k++;
				desenhando++;
			}
			if (desenhando < max_sprites_por_pagina) {
				// desenhar o "+" para adicionar um novo sprite
				x = (desenhando%(caixinha_dos_sprites.width/Gerador.quadrado.width))*Gerador.quadrado.width+caixinha_dos_sprites.x;
				y = (desenhando/(caixinha_dos_sprites.width/Gerador.quadrado.width))*Gerador.quadrado.width+caixinha_dos_sprites.y;
				g.setColor(Color.green);
				g.drawRect(x, y, Gerador.quadrado.width, Gerador.quadrado.height);
				g.drawLine(x+Gerador.quadrado.width/2, y+Gerador.quadrado.width/5, x+Gerador.quadrado.width/2, y+Gerador.quadrado.height-Gerador.quadrado.width/5);
				g.drawLine(x+Gerador.quadrado.width/5, y+Gerador.quadrado.height/2, x+Gerador.quadrado.width-Gerador.quadrado.width/5, y+Gerador.quadrado.height/2);
			}
		}
	}

	private void desenhar_no_quadrado(BufferedImage bufferedImage, int desenhando, Graphics g) {
		int x = desenhando%(caixinha_dos_sprites.width/Gerador.quadrado.width), y = desenhando/(caixinha_dos_sprites.width/Gerador.quadrado.width);
		g.drawImage(bufferedImage, x*Gerador.quadrado.width+caixinha_dos_sprites.x, y*Gerador.quadrado.width+caixinha_dos_sprites.y, Gerador.quadrado.width, Gerador.quadrado.height, null);
		if (sprite_selecionado.contains(desenhando+max_sprites_por_pagina*pagina.get(livro))) {
			g.setColor(new Color(0, 255, 0, 50));
			g.fillRect(x*Gerador.quadrado.width+caixinha_dos_sprites.x, y*Gerador.quadrado.width+caixinha_dos_sprites.y, Gerador.quadrado.width, Gerador.quadrado.height);
		}
	}

	public boolean clicou(int x, int y) {
		if (colocar_paredes.contains(x, y)) {
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
		}else if (caixinha_dos_sprites.contains(x, y)) {
			if (opcao.equalsIgnoreCase(opcoes[0])) pegar_ou_retirar_sprite_selecionado(x,y);
			else if (opcao.equalsIgnoreCase(opcoes[2])) pegar_construcao_salva(x,y);
			else if (opcao.equalsIgnoreCase(opcoes[3])) {
				if (adicionar_nova_cidade.contains(x, y)) System.out.println("Adicionar nova cidade!");
				else if (voltar.contains(x, y) && cidade_selecionada != null) cidade_selecionada = null;
			}
			return true;
		}else if (caixinha_dos_livros.contains(x, y)) {
			trocar_livro(x, y);
			return true;
		}else if(caixa_das_opcoes.contains(x, y)) {
			opcao = opcoes[(x-caixa_das_opcoes.x)/Gerador.TS];
			return true;
		}else if ((pontoA != null || pontoB != null)) {
			if (substitui.contains(x, y)) {
				substituir = !substituir;
				return true;
			}else if (limpar_selecao.contains(x, y)) {
				pontoA = pontoB = null;
				return true;
			}else if  (pontoA != null && pontoB != null) {
				if (preencher_tudo.contains(x, y)) {
					World.fill(pontoA, pontoB);
					return true;
				}else if  (fazer_caixa.contains(x, y)) {
					World.empty(pontoA, pontoB);
					return true;
				}else if (salvar_construcao.contains(x, y)) {
					salvarCarregar.salvar_construcao(pontoA, pontoB);
				}
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

	private void trocar_livro(int x, int y) {
		int py = (y-caixinha_dos_livros.y)/caixinha_dos_livros.width + pagina_livros*max_livros_por_pagina;
		if (py == pagina.size()) {
			String nome = null;
			do {
				nome = JOptionPane.showInputDialog("Insira um nome que já não seja um nome do livro");
				if (nome == null || nome.isBlank()) return;
			} while (nome_livros.contains(nome));
			adicionar_livro(nome);
		}else if (py < pagina.size()) {
			livro = py;
		}
	}
	
	public void construcao() {
		if (pontoA != null && pontoB != null && pontoA != pontoB) {
			salvarCarregar.salvar_construcao(pontoA, pontoB);
		}
	}

	private void adicionar_livro(String nome) {
		pagina.add(0);
		max_pagina.add(0);
		comecar_por.add(0);
		atual.add(0);
		sprites.add(0);
		tiles_salvos.add(new ArrayList<Tile>());
		nome_livros.add(nome);
		max_pagina_livros = nome_livros.size()/max_livros_por_pagina;
	}

	private void pegar_ou_retirar_sprite_selecionado(int x, int y) {
		int px = x/Gerador.quadrado.width, py = (y-caixinha_dos_sprites.y)/Gerador.quadrado.height;
		int aux = px+py*(caixinha_dos_sprites.width/Gerador.quadrado.width);
		if (livro == 0) {
			if (!Gerador.control && sprite_selecionado.contains(aux+max_sprites_por_pagina*pagina.get(livro))) {
				sprite_selecionado.remove((Integer) (aux+max_sprites_por_pagina*pagina.get(livro)));
				int k = atual.get(livro), spr = sprites.get(livro), desenhando = 0;
				while(spr < World.sprites_do_mundo.size()) {
					if (desenhando == aux) {
						array.remove((Integer) spr);;
						lista.remove((Integer) k);;
						break;
					}
					k++;
					if (k >= World.sprites_do_mundo.get(spr).length) {
						spr++;
						k = 0;
					}
					desenhando++;
				}
			}else {
				sprite_selecionado.add(aux+max_sprites_por_pagina*pagina.get(livro));
				int k = atual.get(livro), spr = sprites.get(livro), desenhando = 0;
				while(spr < World.sprites_do_mundo.size()) {
					if (desenhando == aux) {
						array.add(spr);
						lista.add(k);
						break;
					}
					k++;
					if (k >= World.sprites_do_mundo.get(spr).length) {
						spr++;
						k = 0;
					}
					desenhando++;
				}
			}
		}else {
			aux = aux+(max_sprites_por_pagina*pagina.get(livro));
			if (aux == tiles_salvos.get(livro-1).size() && sprite_selecionado.size() > 0) {
				// clicou no "+"
				adicionar_novo_tile_ao_livro(livro);
				
			}else if (aux < tiles_salvos.get(livro-1).size()) {
				livro_tile_pego = livro;
				index_tile_pego = aux;
				tiles_salvos.get(livro-1).get(aux).pegarsprites();
			}
		}
		Gerador.sprite_selecionado_index = 0;
	}
	
	public void selecionar_livro() {
		if (salvar_nesse_livro != livro) {
			salvar_nesse_livro = livro;
			if (salvar_nesse_livro != 0) JOptionPane.showMessageDialog(null, "Você irá salvar as coisas em: "+nome_livros.get(livro));
			return;
		}else{
			adicionar_novo_tile_ao_livro(salvar_nesse_livro);
		}
	}

	private void adicionar_novo_tile_ao_livro(int livro2) {
		if (Ui.array.size() == 0) return;
		if (livro2 == 0 && salvar_nesse_livro == 0) {
			JOptionPane.showMessageDialog(null, "Primeiro você precisa selecionar um livro! Vá até o livro e aperte '+'");
			return;
		}
		Tile tile = new Tile(0, 0, 0);
		tile.adicionar_sprite_selecionado();
		tiles_salvos.get(livro2-1).add(tile);
		if (tiles_salvos.get(livro2-1).size() >= max_sprites_por_pagina) {
			max_pagina.set(livro2, max_pagina.get(livro2)+1);
		}
		salvarCarregar.salvar_livro(livro-1);
	}

	public boolean trocar_pagina(int x, int y, int rodinha) {
		if (mostrar) {
			int k = 0;
			if (rodinha > 0) k=1;
			else k=-1;
			if (caixinha_dos_sprites.contains(x, y)) {
				if (opcao.equalsIgnoreCase(opcoes[0])) {
					pagina.set(livro, pagina.get(livro)+k);
					if (pagina.get(livro) < 0) {
						pagina.set(livro, max_pagina.get(livro));
					}else if (pagina.get(livro) > max_pagina.get(livro)) {
						pagina.set(livro, 0);
					}
					atualizar_caixinha();
				}else if (opcao.equalsIgnoreCase(opcoes[1])) {
					
				}else if (opcao.equalsIgnoreCase(opcoes[2])) {
					pagina_construcoes+=k;
					//*
					 if (pagina_construcoes < 0) {
						pagina_construcoes = construcoes.size()/max_construcoes_por_pagina;
					}else if (pagina_construcoes > construcoes.size()/max_construcoes_por_pagina) {
						pagina_construcoes = 0;
					}//*/
				}
				return true;
			}else if (caixinha_dos_livros.contains(x, y)) {
				pagina_livros += k;
				if (pagina_livros < 0) {
					pagina_livros = max_pagina_livros;
				}else if  (pagina_livros > max_pagina_livros) {
					pagina_livros = 0;
				}
				return true;
			}else if (direcao_escadas.contains(x, y)) {
				escadas_direction += k;
				if (escadas_direction < 0) {
					escadas_direction = setas.length-1;
				}else if (escadas_direction >= setas.length) {
					escadas_direction = 0;
				}
				return true;
			}
		}
		return false;
	}
	
	public static void trocar_Nivel(int wheelRotation) {
		if (wheelRotation > 0) {
			tiles_nivel++;
			if (tiles_nivel > max_tiles_nivel) {
				tiles_nivel = 0;
			}
		}else if (wheelRotation < 0) {
			tiles_nivel--;
			if (tiles_nivel < 0) {
				tiles_nivel = max_tiles_nivel;
			}
		}
	}

	public static void pegar_tile_ja_colocado(ArrayList<int[]> sprites) {
		sprite_selecionado.clear();
		array.clear();
		lista.clear();
		for (int[] a : sprites) {
			array.add(a[0]);
			lista.add(a[1]);
			adicionar_sprite_colocado_aos_selecionados(a[0], a[1]);
		}
		Gerador.sprite_selecionado_index = 0;
	}
	
	private static void adicionar_sprite_colocado_aos_selecionados(int array, int lista) {
		int k = 0;
		for (int i = 0; i < array; i++) {
			k += World.sprites_do_mundo.get(i).length;
		}
		k+=lista;
		sprite_selecionado.add(k);
	}

	public boolean cliquedireito(int x, int y) {
		if (mostrar && caixinha_dos_sprites.contains(x, y)) {
			if (opcao.equalsIgnoreCase(opcoes[0])) {
				if (sprite_selecionado.size() > 0) {
					Ui.sprite_selecionado.clear();
					Ui.array.clear();
					Ui.lista.clear();
					livro_tile_pego = -1;
					index_tile_pego = -1;
				}else if (livro > 0) {
					int px = x/Gerador.quadrado.width, py = (y-caixinha_dos_sprites.y)/Gerador.quadrado.height;
					int aux = px+py*(caixinha_dos_sprites.width/Gerador.quadrado.width)+(max_sprites_por_pagina*pagina.get(livro));
					if (tiles_salvos.get(livro-1).size() > aux) {
						if (JOptionPane.showConfirmDialog(null, "tem certeza que deseja apagar esse sprite?") == 0) {
							tiles_salvos.get(livro-1).remove(aux);
							salvarCarregar.salvar_livro(livro-1);
						}
					}
				}
				return true;
			}else if (opcao.equalsIgnoreCase(opcoes[1])) {
				
			}else if (opcao.equalsIgnoreCase(opcoes[2])) {
				
			}
		}
		return false;
	}

	public void adicionar_livro_salvo(String nome, ArrayList<Tile> tiles) {
		adicionar_livro(nome);
		tiles_salvos.set(tiles_salvos.size()-1, tiles);
		max_pagina.set(tiles_salvos.size(), (int) (tiles_salvos.get(tiles_salvos.size()-1).size()/max_sprites_por_pagina));
	}

	public boolean addponto(int x, int y) {
		Tile t = World.pegar_chao(x, y, Gerador.player.getZ());
		if (t == pontoA) {
			pontoA = null;
			return true;
		}else if (t == pontoB) {
			pontoB = null;
			return true;
		}
		if (pontoA == null) {
			pontoA = t;
			return true;
		}else if (pontoB == null) {
			pontoB = t;
			return true;
		}
		return false;
	}
	
	public Build pegar_construcao_selecionada() {
		if (index_construcao_selecionada == -1) {
			return null;
		}
		return construcoes.get(index_construcao_selecionada);
	}

	public void adicionar_construcoes_salvas(ArrayList<Build> construcoes2) {
		construcoes = construcoes2;
	}
}
