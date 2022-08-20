package world;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import files.salvarCarregar;
import graficos.Ui;
import main.Gerador;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tile {
	private ArrayList<ArrayList<int[]>> sprites;
	private int x, y, z, speed_modifier, evento, solid, // solid: 0 = chao normal; 1 = parede; 2 = água; 3 = lava; 4 = vip
	stairs_type, stairs_direction; // stairs_type 0 = não tem, 1 = escada "normal", 2 = escada de clique direito, 3 = buraco sempre aberto, 4 = Buraco fechado (usar picareta ou cavar para abrí-lo); direction 0 = direita, 1 = baixo, 2 = esquerda, 3 = cima
	private boolean aberto_ou_fechado; // aberto_ou_fechado: usado para paredes; usado em conjunto para ver se esta aberto ou fechado
	private int[] sprite_fechado, sprite_aberto; // sprites de reações
	private String house_door;
	
	public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("z") int z){
		house_door = ""; // ao criar a casa essa variável recebe o nome da casa, isso serve para que ela possa ser comprada
		evento = solid = 0; // quando o player interage com um tile, ocorre um evento, o evento é um int enviado para o servidor junto com o tile para ocorrer algo
		aberto_ou_fechado = true;
		stairs_type = 0;
		stairs_direction = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		sprite_fechado = sprite_aberto = null;
		sprites = new ArrayList<ArrayList<int[]>>();
		for (int i = 0; i < Ui.max_tiles_nivel; i++) {
			sprites.add(new ArrayList<int[]>());
		}
	}
	
	public int getEvento() {
		return evento;
	}

	public void setEvento(int evento) {
		this.evento = evento;
	}

	public boolean isAberto_ou_fechado() {
		return aberto_ou_fechado;
	}

	public void setAberto_ou_fechado(boolean aberto_ou_fechado) {
		this.aberto_ou_fechado = aberto_ou_fechado;
	}

	public int[] getSprite_fechado() {
		return sprite_fechado;
	}

	public void setSprite_fechado(int[] sprite_fechado) {
		this.sprite_fechado = sprite_fechado;
	}

	public int[] getSprite_aberto() {
		return sprite_aberto;
	}

	public void setSprite_aberto(int[] sprite_aberto) {
		this.sprite_aberto = sprite_aberto;
	}

	public String getHouse_door() {
		return house_door;
	}

	public void setHouse_door(String house_door) {
		this.house_door = house_door;
	}

	public void setStairs_type(int stairs_type) {
		this.stairs_type = stairs_type;
	}

	public void setStairs_direction(int stairs_direction) {
		this.stairs_direction = stairs_direction;
	}

	public int getSpeed_modifier() {
		return speed_modifier;
	}
	
	public int getSolid(){
		return solid;
	}
	
	public void setSolid(int solid) {
		if (stairs_type != 0 && solid == 1) return;
		this.solid = solid;
	}
	
	public int getStairs_type() {
		return stairs_type;
	}
	public int getStairs_direction() {
		return stairs_direction;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	
	public void carregar_sprites(String linha) {
		sprites.clear();
		// total de srpites; lista de sprites; tipo de escada - direcao da escada - solido? - speed_modifier; sprite ajustavel
		String[] sla = linha.split(";"), sla2 = sla[1].split("-"), sla4 = sla[2].split("-");
		for (int i = 0; i < Integer.parseInt(sla[0]); i++) {
			ArrayList<int[]> sprite = new ArrayList<int[]>();
			String[] sla3 = sla2[i].split(":");
			for (int k = 0; k < Integer.parseInt(sla3[0]); k++) {
				String[] s = sla3[k+1].split("a");
				int[] a = {Integer.parseInt(s[0]), Integer.parseInt(s[1])};
				sprite.add(a);
			}
			sprites.add(sprite);
		}
		stairs_type = Integer.parseInt(sla4[0]);
		stairs_direction = Integer.parseInt(sla4[1]);
		solid = Integer.parseInt(sla4[2]);
		speed_modifier = Integer.parseInt(sla4[3]);
		house_door = sla4[4]; 
		evento = Integer.parseInt(sla4[5]);
		if (stairs_type == 4) {
			String[] sprites_buraco = sla[3].split("-"), fechado = sprites_buraco[0].split("a"), aberto = sprites_buraco[1].split("a");
			sprite_fechado = new int[2];
			sprite_aberto = new int[2];
			for (int i = 0; i < 2; i++) {
				sprite_fechado[i] = Integer.parseInt(fechado[i]);
				sprite_aberto[i] = Integer.parseInt(aberto[i]);
			}
		}
	}
	
	public ArrayList<BufferedImage> obterSprite_atual() {
		ArrayList<BufferedImage> lDesenhoAtual = new ArrayList<BufferedImage>();
		for (ArrayList<int[]> imagens : sprites) {
			if (imagens != null && imagens.size() > 0) {
				int[] sprite = imagens.get(World.tiles_index%imagens.size());
				lDesenhoAtual.add(World.sprites_do_mundo.get(sprite[0])[sprite[1]]);
			}
		}
		if (sprite_aberto != null) {
			lDesenhoAtual.add((aberto_ou_fechado) ? World.sprites_do_mundo.get(sprite_fechado[0])[sprite_fechado[1]] : World.sprites_do_mundo.get(sprite_aberto[0])[sprite_aberto[1]]);
		}
		return lDesenhoAtual;
	}
	
	public void render(Graphics g){
		for (ArrayList<int[]> imagens : sprites) {
			if (imagens != null && imagens.size() > 0) {
				int[] sprite = imagens.get(World.tiles_index%imagens.size());
				int dx, dy;
				BufferedImage image = World.sprites_do_mundo.get(sprite[0])[sprite[1]];
				if (image.getWidth() > Gerador.quadrado.width || image.getHeight() > Gerador.quadrado.height) {
					dx = x - Camera.x - Gerador.quadrado.width;
					dy = y - Camera.y - Gerador.quadrado.height;
				}
				else {
					dx = x - Camera.x;
					dy = y - Camera.y;
				}
				dx -= (z-Gerador.player.getZ())*Gerador.quadrado.width;
				dy -= (z-Gerador.player.getZ())*Gerador.quadrado.height;
				g.drawImage(image, dx, dy, null);
			}
		}
		if (sprite_aberto != null) {
			BufferedImage image;
			int dx = x - Camera.x - (z-Gerador.player.getZ())*Gerador.quadrado.width, dy = y - Camera.y - (z-Gerador.player.getZ())*Gerador.quadrado.height;
			if (aberto_ou_fechado) {
				image = World.sprites_do_mundo.get(sprite_fechado[0])[sprite_fechado[1]];
			}else {
				image = World.sprites_do_mundo.get(sprite_aberto[0])[sprite_aberto[1]];
			}
			g.drawImage(image, dx, dy, null);
		}
		
		if (Ui.colocar_parede && solid == 1) {
			g.setColor(new Color(255, 0, 0, 50));
			g.fillRect(x - Camera.x-(z-Gerador.player.getZ())*Gerador.quadrado.width, y - Camera.y-(z-Gerador.player.getZ())*Gerador.quadrado.height, Gerador.TS, Gerador.TS);
		}else if (Ui.colocar_escada && stairs_type != 0) {
			int[] cor = {255, 255, 255};
			if (stairs_type != 4) cor[stairs_type-1] = 0;
			g.setColor(new Color(cor[0], cor[1], cor[2], 50));
			g.fillRect(x - Camera.x-(z-Gerador.player.getZ())*Gerador.quadrado.width, y - Camera.y-(z-Gerador.player.getZ())*Gerador.quadrado.height, Gerador.TS, Gerador.TS);
		}
		if (Ui.opcao.equalsIgnoreCase(Ui.opcoes[1]) && z == Gerador.player.getZ()) {
			if (solid == 2) {
				g.setColor(new Color(0, 255, 255, 50));
				g.fillRect(x - Camera.x-(z-Gerador.player.getZ())*Gerador.quadrado.width, y - Camera.y-(z-Gerador.player.getZ())*Gerador.quadrado.height, Gerador.TS, Gerador.TS);
			}else if (solid == 3) {
				g.setColor(new Color(255, 97, 0, 50));
				g.fillRect(x - Camera.x-(z-Gerador.player.getZ())*Gerador.quadrado.width, y - Camera.y-(z-Gerador.player.getZ())*Gerador.quadrado.height, Gerador.TS, Gerador.TS);
			}else if (solid == 4) {
				g.setColor(Color.white);
				g.drawRect(x - Camera.x-(z-Gerador.player.getZ())*Gerador.quadrado.width, y - Camera.y-(z-Gerador.player.getZ())*Gerador.quadrado.height, Gerador.TS, Gerador.TS);
			}
				
			if (!(Ui.colocar_escada || Ui.sprite_reajivel || Ui.colocar_parede)) {
				Font f = g.getFont();
				g.setFont(new Font(f.getName(), f.getStyle(), 20));
				g.setColor(Color.white);
				g.drawString(""+speed_modifier, x+Gerador.TS/2-2-Camera.x, y+Gerador.TS/2+7-Camera.y);
				g.setFont(f);
			}
		}
	}
	
	public void setSpeed_modifier(int speed_modifier) {
		this.speed_modifier = speed_modifier;
	}

	public void trocar_solid() {
		if (stairs_type == 0) {
			if (solid == 0) solid = 1;
			else if (solid == 1) solid = 0;
		}
	}

	public void adicionar_sprite_selecionado() {
		ArrayList<int[]> novo = new ArrayList<int[]>();
		if (Ui.array.size() == 0 && sprites.size() < Ui.tiles_nivel && sprites.size() > 0) {
			sprites.set(Ui.tiles_nivel, null);
			return;
		}
		for (int i = 0; i < Ui.sprite_selecionado.size(); i++) {
			int[] a = {Ui.array.get(i), Ui.lista.get(i)};
			novo.add(a);
		}
		if (sprites.size() > Ui.tiles_nivel || (sprites.size() > Ui.tiles_nivel && sprites.get(Ui.tiles_nivel) == null))	sprites.set(Ui.tiles_nivel, novo);
		else sprites.add(novo);
	}
	
	public boolean adicionar_sprite_reajivel() {
		if (sprite_fechado != null && Ui.sprite_selecionado.size() == 0) {
			sprite_aberto = sprite_fechado = null;
			return true;
		}
		if (Ui.sprite_selecionado.size() != 2)	{
			JOptionPane.showMessageDialog(null, "Necessário ter 2 esprites selecionados, o primeiro representa o fechado, enquanto o segundo aberto");
			return false;
		}
		sprite_fechado = new int[2];
		sprite_fechado[0] = Ui.array.get(0);
		sprite_fechado[1] = Ui.lista.get(0);
		sprite_aberto = new int[2];
		sprite_aberto[0] = Ui.array.get(1);
		sprite_aberto[1] = Ui.lista.get(1);
		aberto_ou_fechado = true;
		return true;
	}

	public void pegarsprites() {
		ArrayList<int[]> sprite;
		if (!Ui.sprite_reajivel) {
			sprite = (sprites.size() > Ui.tiles_nivel) ? sprites.get(Ui.tiles_nivel) : null;
		}else {
			sprite = new ArrayList<int[]>();
			sprite.add(sprite_fechado);
			sprite.add(sprite_aberto);
		}
		if (sprite == null || sprite.size() == 0) {
			return;
		}
		Ui.pegar_tile_ja_colocado(sprite);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setZ(int z) {
		this.z = z;
	}
	
	public String salvar() {
		return salvarCarregar.toJSON(this);
	}
	
	public boolean existe() {
		for (ArrayList<int[]> spr : sprites) {
			if (spr.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public void virar_escada() {
		if (solid != 1) {
			if (Ui.modo_escadas == 3) {
				if (!adicionar_sprite_reajivel()) return;
			}else if (Ui.modo_escadas == 2) {
				adicionar_sprite_selecionado(); // escada de clique direito
			}
			stairs_type = Ui.modo_escadas+1;
			stairs_direction = Ui.escadas_direction;
		}
	}

	public void desvirar_escada() {
		stairs_type = 0;
	}

	public boolean tem_sprites() {
		for (ArrayList<int[]> spr : sprites) {
			if (spr.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean pode_descer_com_colisao() {
		if (stairs_type == 1 || stairs_type == 2 || stairs_type == 3 || (stairs_type == 4 && !aberto_ou_fechado)) {
			return true;
		}
		return false;
	}

	public boolean pode_subir_com_colisao() {
		if (stairs_type == 1) {
			return true;
		}
		return false;
	}

	public void reajir() {
		aberto_ou_fechado = !aberto_ou_fechado;
	}

	public void varios(int virar_solido) {
		// Ação realizada no World.fill ou World.empty
		if (Ui.opcao.equalsIgnoreCase(Ui.opcoes[0])) {
			// Colocar sprites
			if (Ui.substituir || !tem_sprites()) adicionar_sprite_selecionado();
			if (Ui.colocar_escada) virar_escada();
			else setSolid(virar_solido);
		}else if (Ui.opcao.equalsIgnoreCase(Ui.opcoes[1])) {
			if (Ui.colocar_parede) mar(virar_solido);
			else if (Ui.sprite_reajivel) lava(virar_solido);
			else if (Ui.colocar_escada) vip(virar_solido);
			else setSpeed_modifier(Gerador.ui.getNew_speed());
		}else if (Ui.opcao.equalsIgnoreCase(Ui.opcoes[2])) {
			
		}else if (Ui.opcao.equalsIgnoreCase(Ui.opcoes[3])) {
			// criar casa
		}
	}
	
	public void setSprites(ArrayList<ArrayList<int[]>> sprites) {
		this.sprites = sprites;
	}
	
	public ArrayList<ArrayList<int[]>> getSprites() {
		return sprites;
	}

	public void mar(int solido) {
		System.out.println("a: "+solido);
		if (solido == 1) solid = 2;
		else solid = 0;
	}

	public void lava(int solido) {
		if (solido == 1) solid = 3;
		else solid = 0;
		
	}

	public void vip(int solido) {
		if (solido == 1) solid = 4;
		else solid = 0;
	}

}
