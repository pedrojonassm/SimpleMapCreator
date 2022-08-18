package world;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import files.salvarCarregar;
import graficos.Spritesheet;
import graficos.Ui;
import main.Gerador;

public class World {

	public static Tile[] tiles;
	public static int WIDTH,HEIGHT,HIGH;
	public static final int TILE_SIZE = Gerador.TS;
	public static int maxDistance = (Gerador.WIDTH/Gerador.TS + 10)/2, posX, posY;
	public static ArrayList<BufferedImage[]> sprites_do_mundo; // chaos64, chaos128, paredes64, paredes128, itens64, itens128, escadas64, escadas128
	public static int log_ts;
	private static int minX, minY, minZ, maxX, maxY, maxZ;
	
	public static int tiles_index, tiles_animation_time, max_tiles_animation_time;
	static File arquivo;
	public static boolean ready;
	
	public World(File file){
		ready = false;
		log_ts = log2(Gerador.TS);
		//*
		tiles_index = tiles_animation_time = 0;
		max_tiles_animation_time = 15;
		 try {
			 if (file == null) {
				 arquivo = null;
				 determinar_tamanho();
				 tiles = new Tile[WIDTH * HEIGHT * HIGH];
					for(int xx = 0; xx < WIDTH; xx++)
						for(int yy = 0; yy < HEIGHT; yy++)
							for (int zz = 0; zz < HIGH; zz++)
								tiles[(xx + (yy * WIDTH))*HIGH+zz] = new Tile(xx*Gerador.TS,yy*Gerador.TS, zz);
			 }else {
				arquivo = file.getParentFile();
				@SuppressWarnings("resource")
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String singleLine = null;
				singleLine = reader.readLine();
				String[] sla = singleLine.split(";");
				WIDTH = Integer.parseInt(sla[0]); HEIGHT = Integer.parseInt(sla[1]); HIGH = Integer.parseInt(sla[2]);
				tiles = new Tile[WIDTH * HEIGHT * HIGH];
				for(int xx = 0; xx < WIDTH; xx++)
					for(int yy = 0; yy < HEIGHT; yy++)
						for (int zz = 0; zz < HIGH; zz++) {
							Tile t = new Tile(xx*Gerador.TS,yy*Gerador.TS, zz);
							tiles[(xx + (yy * WIDTH))*HIGH+zz] = t;
							String str = reader.readLine();
							t.carregar_sprites(str);
						}
			 }
			ready = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//*/
	}
	
	private void determinar_tamanho() {
		JTextField width = new JTextField(), height = new JTextField(), high = new JTextField();
		KeyListener l = new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9')) {
	               e.consume();
	            }
			}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		};
		width.addKeyListener(l); height.addKeyListener(l); high.addKeyListener(l);
		Object[] message = {
		    "Width (>= 20):", width,
		    "Height (>= 20):", height,
		    "High:", high
		};

		int option = JOptionPane.showConfirmDialog(null, message, "Tamanho do mundo", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION && !width.getText().isBlank() && height.getText().isBlank() && high.getText().isBlank()) {
		    WIDTH = Integer.parseInt(width.getText());
		    HEIGHT = Integer.parseInt(height.getText());
		    HIGH = Integer.parseInt(high.getText());
		    
		    if (WIDTH <= 20 || HEIGHT <= 20 || HIGH <= 0) {
		    	JOptionPane.showMessageDialog(null, "alguns dados não foram inseridos ou foram inseridos incorretamente;\n Inserindo valores padrão");
		    	valores_padrao();
		    }
		    
		} else {
			valores_padrao();
		}
	}
	
	private void valores_padrao() { WIDTH = 20; HEIGHT = 20; HIGH = 7;}

	public static void carregar_sprites() {
		sprites_do_mundo = new ArrayList<BufferedImage[]>();
		Spritesheet[] sprites = new Spritesheet[8];
		int[] total_de_sprites = {36*40+16, 9, 27*20-3, 40*32-11, 40*23-16, 20*16+2, 35, 40};
		sprites[0] = new Spritesheet("/chaos64.png", 64); // total de sprites: 36*40 + 16
		sprites[1] = new Spritesheet("/chaos128.png", 128); // total de sprites: 9
		sprites[2] = new Spritesheet("/paredes64.png", 64); // total de sprites: 27*20 - 3
		sprites[3] = new Spritesheet("/paredes128.png", 128); // total de sprites: 40*32 - 11
		sprites[4] = new Spritesheet("/itens64.png", 64); // total de sprites: 40*23 - 16
		sprites[5] = new Spritesheet("/itens128.png", 128); // total de sprites: 20*16 + 2
		sprites[6] = new Spritesheet("/escadas64.png", 64); // total de sprites: 35
		sprites[7] = new Spritesheet("/escadas128.png", 128); // total de sprites: 40
		
		int max_pagina = 0;
		for (int i = 0; i < 8; i++) {
			sprites_do_mundo.add(sprites[i].get_x_sprites(total_de_sprites[i]));
			max_pagina += total_de_sprites[i];
		}
		Gerador.ui.max_pagina_por_total_de_sprites(max_pagina);
	}
	
	public static Tile pegar_chao(int pos) {
		if (pos >= tiles.length) {
			return null;
		}
		return tiles[pos];
	}
	
	public static Tile pegar_chao(int mx, int my, int mz) {
		return pegar_chao(calcular_pos(mx, my, mz));
	}
	
	public static int calcular_pos(int mx, int my, int mz) {
		return ((mx >> log_ts) + (my>>log_ts)*World.WIDTH)*World.HIGH+mz;
	}

	public void tick() {
		if (++tiles_animation_time >= max_tiles_animation_time) {
			tiles_animation_time = 0;
			if (++tiles_index >= 100) {
				tiles_index = 0;
			}
		}
	}
	
	public static boolean isFree(int xnext, int ynext, int z){
		
		int x1 = xnext;
		int y1 = ynext;
		
		int x2 = (xnext+TILE_SIZE);
		int y2 = ynext;
		
		int x3 = xnext;
		int y3 = (ynext+TILE_SIZE);
		
		int x4 = (xnext+TILE_SIZE);
		int y4 = (ynext+TILE_SIZE);
		
		return !((pegar_chao(x1, y1, z).getSolid() == 1) ||
				(pegar_chao(x2, y2, z).getSolid() == 1) ||
				(pegar_chao(x3, y3, z).getSolid() == 1) ||
				(pegar_chao(x4, y4, z).getSolid() == 1));
	}
	
	public static Tile[] tiles_ao_redor(int x, int y, int z) {
		Tile[] retorno = new Tile[8];
		
		int[]
				xs = {x-TILE_SIZE, x, x+TILE_SIZE, x-TILE_SIZE, x+TILE_SIZE, x-TILE_SIZE, x, x+TILE_SIZE},
				ys = {y-TILE_SIZE, y-TILE_SIZE, y-TILE_SIZE, y, y, y+TILE_SIZE, y+TILE_SIZE, y+TILE_SIZE};
		for (int i = 0; i < 8; i++) {
			retorno[i] = pegar_chao(xs[i], ys[i], z);
		}
		
		return retorno;
	}
	
	private int log2(int n) {
		int k = 0;
		while (n%2 == 0) {
			k++;
			n = n/2;
		}
		return k;
	}
	public void render(Graphics g){
		int xstart = Camera.x >> log_ts;
		int ystart = Camera.y >> log_ts;
		
		int xfinal = xstart + (Gerador.WIDTH >> log_ts) + 1;
		int yfinal = ystart + (Gerador.HEIGHT >> log_ts) + 1;
		
		if ((xstart-=(Gerador.player.getZ()+1)) < 0) xstart = 0;
		if ((ystart-=(Gerador.player.getZ()+1)) < 0) ystart = 0;
		
		Tile t;
		int maxZ = HIGH;
		for (int i = 0; i < HIGH-Gerador.player.getZ()-1; i++) {
			t = pegar_chao(((Gerador.quadrado.x >> log_ts) + (i+1) + (i+1)*WIDTH + (Gerador.quadrado.y>>log_ts)*WIDTH)*HIGH+Gerador.player.getZ()+1); // trocar por player.x e player.y
			if  ( t.existe() ) {
				maxZ = t.getZ(); // caso exista uma imagem que não dê para ser vista, ela some
				break;
			}
		}
		
		/*
		 player = x, y, z
		 x, y, z
		 */
		
		for(int xx = xstart; xx <= xfinal; xx++)
			for(int yy = ystart; yy <= yfinal; yy++)
				for (int zz = 0; zz < maxZ; zz++){
					if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
						continue;
					}
					tiles[(xx + (yy * WIDTH))*HIGH+zz].render(g);
			}
	}
	
	public static void salvar() {
		if (arquivo == null) {
			
			try {
				String nome = null;
				do {
					nome = JOptionPane.showInputDialog("Insira um nome válido para esse mundo");
					if (nome == null) {
						if (JOptionPane.showConfirmDialog(null, "Tem certeza que deseja cancelar?") == 0) return; 
					}
				}while(nome == null || nome.isBlank() || (arquivo = new File(salvarCarregar.arquivo_worlds, nome)).exists());
				arquivo.mkdir();
				new File(arquivo, salvarCarregar.name_file_world).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		String salvar = ""+WIDTH+";"+HEIGHT+";"+HIGH+"\n";
		for(int xx = 0; xx < WIDTH; xx++)
			for(int yy = 0; yy < HEIGHT; yy++)
				for (int zz = 0; zz < HIGH; zz++)
					salvar+=tiles[(xx + (yy * WIDTH))*HIGH+zz].salvar();
		
		salvarCarregar.salvar_mundo(arquivo, salvar);	
	}
	
	private static void ordenar_valores(Tile pontoA, Tile pontoB) {
		// coloca os valores minimos e máximos
		if (pontoA.getX() < pontoB.getX()) {
			minX = pontoA.getX() >> log_ts;
			maxX = pontoB.getX() >> log_ts; 
		}else {
			minX = pontoB.getX() >> log_ts;
			maxX = pontoA.getX() >> log_ts;
		}
		if (pontoA.getY() < pontoB.getY()) {
			minY = pontoA.getY() >> log_ts;
			maxY = pontoB.getY() >> log_ts;
		}else {
			minY = pontoB.getY() >> log_ts;
			maxY = pontoA.getY() >> log_ts;
		}
		if (pontoA.getZ() < pontoB.getZ()) {
			minZ = pontoA.getZ();
			maxZ = pontoB.getZ();
		}else {
			minZ = pontoB.getZ();
			maxZ = pontoA.getZ();
		}
	}
	
	public static void fill(Tile pontoA, Tile pontoB) {
		ordenar_valores(pontoA, pontoB);
		
		int virar_solido = 0;
		if (pontoA.getSolid() == pontoB.getSolid()) {
			if (Ui.colocar_parede || (Ui.opcao.equalsIgnoreCase(Ui.opcoes[1]) && (Ui.colocar_escada || Ui.sprite_reajivel))) {
				virar_solido = pontoA.getSolid();
				if (virar_solido > 1) {
					virar_solido = 0;
				}else {
					virar_solido = 1;
				}
			}
		}
		
		// colocar sprites
		for(int xx = minX; xx <= maxX; xx++)
			for(int yy = minY; yy <= maxY; yy++)
				for (int zz = minZ; zz <= maxZ; zz++){
					if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
						continue;
					}
					tiles[(xx + (yy * WIDTH))*HIGH+zz].varios(virar_solido);
					
			}
	}
	
	public static ArrayList<Tile> pegar_construção(Tile pontoA, Tile pontoB) {
		ordenar_valores(pontoA, pontoB);
		ArrayList<Tile> construcao = new ArrayList<Tile>();
		
		for(int xx = minX; xx <= maxX; xx++)
			for(int yy = minY; yy <= maxY; yy++)
				for (int zz = minZ; zz <= maxZ; zz++){
					if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
						continue;
					}		
					construcao.add(tiles[(xx + (yy * WIDTH))*HIGH+zz]);
			}
		return construcao;
	}
	
	public static void colocar_construção(Tile inicial, Build construcao) {
		if (construcao == null) return;
		ArrayList<Tile> tiles_construcao = salvarCarregar.carregar_construcao(construcao);
		int i = 0, x_ini = inicial.getX()>>log_ts, y_ini = inicial.getY() >> log_ts, z_ini = inicial.getZ();
		if (x_ini+construcao.getHorizontal() >= WIDTH || y_ini+construcao.getVertical() >= HEIGHT) {
			JOptionPane.showMessageDialog(null, "A construção não poderá ser feita aqui pois sairá do mapa");
			return;
		}
		for (int xx = 0; xx < construcao.getHorizontal(); xx++) 
			for (int yy = 0; yy < construcao.getVertical(); yy++)
				for (int zz = 0; zz < construcao.getHigh(); zz++) {
					int pos = (x_ini+xx + (y_ini + yy)*World.WIDTH)*World.HIGH+zz+z_ini;
					if (pos < tiles.length)	tiles[pos].setSprites(tiles_construcao.get(i++).getSprites());
				}
		
	}
	
	public static void empty(Tile pontoA, Tile pontoB) {
		ordenar_valores(pontoA, pontoB);
		int aX = pontoA.getX() >> log_ts, aY = pontoA.getY() >> log_ts, aZ = pontoA.getZ(), bX = pontoB.getX() >> log_ts, bY = pontoB.getY() >> log_ts, bZ = pontoB.getZ();
		
		int virar_solido = 0;
		if (Ui.colocar_parede == true && pontoA.getSolid() == pontoB.getSolid()) {
			virar_solido = pontoA.getSolid();
		}
		if (aZ == bZ) {
			for(int xx = minX; xx <= maxX; xx++)
				for(int yy = minY; yy <= maxY; yy++){
						if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT || ((aX != xx && bX != xx) && (aY != yy && bY != yy))) {
							continue;
						}
						tiles[(xx + (yy * WIDTH))*HIGH+aZ].varios(virar_solido);
				}
		}else if (aY == bY) {
			for(int xx = minX; xx <= maxX; xx++)
				for(int zz = minZ; zz <= maxZ; zz++){
						if(xx < 0 || xx >= WIDTH || ((aX != xx && bX != xx) && (aZ != zz && bZ != zz))) {
							continue;
						}
						tiles[(xx + (aY * WIDTH))*HIGH+zz].varios(virar_solido);
				}
		}else if (aX == bX) {
			for(int yy = minY; yy <= maxY; yy++)
				for(int zz = minZ; zz <= maxZ; zz++){
					if(yy < 0 || yy >= HEIGHT || ((aZ != zz && bZ != zz) && (aY != yy && bY != yy))) {
						continue;
					}
					tiles[(aX + (yy * WIDTH))*HIGH+zz].varios(virar_solido);
				}
		}else {
			for(int xx = minX; xx <= maxX; xx++)
				for(int yy = minY; yy <= maxY; yy++)
					for(int zz = minZ; zz <= maxZ; zz++){
						if((xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)) {
							continue;
						}else if (xx == aX || xx == bX || yy == aY || yy == bY || zz == aZ || zz == bZ) tiles[(xx + (yy * WIDTH))*HIGH+zz].varios(virar_solido);
					}
		}
	}

	public static void novo_mundo(File file) {
		if (JOptionPane.showConfirmDialog(null, "Deseja salvar o mundo atual?") == 0) salvar();
		
		Gerador.world = new World(file);
	}

	public static void carregar_mundo() {
		
		Gerador.fd.setVisible(true);
		novo_mundo(Gerador.fd.getFiles()[0]);
	}
}
