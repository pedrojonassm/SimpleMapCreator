package world;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import graficos.ConjuntoSprites;
import graficos.Ui;
import graficos.telas.configuracao.TelaConfiguracao;
import graficos.telas.configuracao.subtelas.SubTelaEscada;
import graficos.telas.sprites.TelaSprites;
import graficos.telas.sprites.subtelas.SubTelaMultiplosSprites;
import main.Gerador;

public class Tile {
	List<ConjuntoSprites> aCoConjuntoSprites;
	private int x, y, z, aPos, speed_modifier, evento, solid, stairs_type, stairs_direction, posicao_Conjunto;
	// stairs_type 0 = não tem, 1 = escada "normal", 2 = escada de clique direito, 3
	// = buraco sempre aberto, 4 = Buraco fechado (usar picareta ou
	// cavar para abrí-lo); direction 0 = direita, 1 = baixo, 2 = esquerda, 3 = cima
	// solid: 0 = chao normal; 1 = parede; 2 = água; 3 = lava; 4 = vip

	private HashMap<String, Object> aPropriedades;

	public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("z") int z) {
		// ao criar a casa essa variável recebe o nome da casa, isso serve para que ela
		// possa ser comprada
		posicao_Conjunto = evento = solid = 0;
		// quando o player interage com um tile, ocorre um evento, o evento é um int
		// enviado para o servidor junto com o tile para ocorrer algo
		stairs_type = 0;
		stairs_direction = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		aCoConjuntoSprites = new ArrayList<>();
		aCoConjuntoSprites.add(new ConjuntoSprites());
		aPos = World.calcular_pos(x, y, z);
	}

	public int getEvento() {
		return evento;
	}

	public void setEvento(int evento) {
		this.evento = evento;
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

	public int getSolid() {
		return solid;
	}

	public void setSolid(int solid) {
		if (stairs_type != 0 && solid == 1)
			return;
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

	public int getaPos() {
		return aPos;
	}

	public void setaPos(int aPos) {
		this.aPos = aPos;
	}

	public ArrayList<BufferedImage> obterSprite_atual() {
		return aCoConjuntoSprites.get(posicao_Conjunto).obterSprite_atual();
	}

	public void render(Graphics g) {
		if (posicao_Conjunto < aCoConjuntoSprites.size() && aCoConjuntoSprites.get(posicao_Conjunto) != null)
			for (ArrayList<int[]> imagens : aCoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
				if (imagens != null && imagens.size() > 0) {
					int[] sprite = imagens.get(World.tiles_index % imagens.size());
					int dx, dy;
					BufferedImage image = World.sprites_do_mundo.get(sprite[0])[sprite[1]];
					if (image.getWidth() > Gerador.quadrado.width || image.getHeight() > Gerador.quadrado.height) {
						dx = x - Camera.x - Gerador.quadrado.width;
						dy = y - Camera.y - Gerador.quadrado.height;
					} else {
						dx = x - Camera.x;
						dy = y - Camera.y;
					}
					dx -= (z - Gerador.player.getZ()) * Gerador.quadrado.width;
					dy -= (z - Gerador.player.getZ()) * Gerador.quadrado.height;
					g.drawImage(image, dx, dy, null);
				}
			}

		if (/* Ui.colocar_parede && */ solid == 1) {
			g.setColor(new Color(255, 0, 0, 50));
			g.fillRect(x - Camera.x - (z - Gerador.player.getZ()) * Gerador.quadrado.width,
					y - Camera.y - (z - Gerador.player.getZ()) * Gerador.quadrado.height, Gerador.TS, Gerador.TS);
		} else if (TelaConfiguracao.instance.getOpcao() == 0 && Ui.opcao == 1 && stairs_type != 0) {
			int[] cor = { 255, 255, 255 };
			if (stairs_type != 4)
				cor[stairs_type - 1] = 0;
			g.setColor(new Color(cor[0], cor[1], cor[2], 50));
			g.fillRect(x - Camera.x - (z - Gerador.player.getZ()) * Gerador.quadrado.width,
					y - Camera.y - (z - Gerador.player.getZ()) * Gerador.quadrado.height, Gerador.TS, Gerador.TS);
		}
		if (Ui.opcao == 1 && z == Gerador.player.getZ()) {
			if (solid == 2) {
				g.setColor(new Color(0, 255, 255, 50));
				g.fillRect(x - Camera.x - (z - Gerador.player.getZ()) * Gerador.quadrado.width,
						y - Camera.y - (z - Gerador.player.getZ()) * Gerador.quadrado.height, Gerador.TS, Gerador.TS);
			} else if (solid == 3) {
				g.setColor(new Color(255, 97, 0, 50));
				g.fillRect(x - Camera.x - (z - Gerador.player.getZ()) * Gerador.quadrado.width,
						y - Camera.y - (z - Gerador.player.getZ()) * Gerador.quadrado.height, Gerador.TS, Gerador.TS);
			} else if (solid == 4) {
				g.setColor(Color.white);
				g.drawRect(x - Camera.x - (z - Gerador.player.getZ()) * Gerador.quadrado.width,
						y - Camera.y - (z - Gerador.player.getZ()) * Gerador.quadrado.height, Gerador.TS, Gerador.TS);
			}

		}

		// TODO Remover isto quando tiver as propriedades
		if (!(TelaSprites.instance.getMultiplosSprites()) && Ui.opcao == 1) {
			Font f = g.getFont();
			g.setFont(new Font(f.getName(), f.getStyle(), 20));
			g.setColor(Color.white);
			g.drawString("" + speed_modifier, x + Gerador.TS / 2 - 2 - Camera.x, y + Gerador.TS / 2 + 7 - Camera.y);
			g.setFont(f);
		}
	}

	public void setSpeed_modifier(int speed_modifier) {
		this.speed_modifier = speed_modifier;
	}

	public void trocar_solid() {
		if (stairs_type == 0) {
			if (solid == 0)
				solid = 1;
			else if (solid == 1)
				solid = 0;
		}
	}

	public void adicionar_sprite_selecionado() {
		aCoConjuntoSprites.get(posicao_Conjunto).adicionar_sprite_selecionado();
	}

	@SuppressWarnings("unchecked")
	public void adicionarMultiplosSprites() {
		if (SubTelaMultiplosSprites.instance.getConjuntoSprites() != null) {
			aCoConjuntoSprites = (List<ConjuntoSprites>) SubTelaMultiplosSprites.instance.getConjuntoSprites().clone();
		}

	}

	public void pegarsprites() {
		if (TelaSprites.instance.getMultiplosSprites())
			SubTelaMultiplosSprites.instance.addSpritesConjunto(aCoConjuntoSprites);
		else
			aCoConjuntoSprites.get(posicao_Conjunto).pegarsprites();
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

	public boolean existe() {
		for (ArrayList<int[]> spr : aCoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
			if (spr.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public void addPropriedades(HashMap<String, Object> prPropriedades) {
		if (aPropriedades == null)
			aPropriedades = new HashMap<>();
		aPropriedades.putAll(prPropriedades);
	}

	public void addPropriedade(String prKey, Object prValor) {
		if (aPropriedades == null)
			aPropriedades = new HashMap<>();
		aPropriedades.put(prKey, prValor);
	}

	public Object getPropriedade(String prKey) {
		if (aPropriedades == null)
			return null;
		return aPropriedades.get(prKey);
	}

	public void removePropriedade(String prKey) {
		if (aPropriedades == null)
			return;
		aPropriedades.remove(prKey);
	}

	public void virar_escada() {
		stairs_type = SubTelaEscada.instance.modo_escadas + 1;
		stairs_direction = SubTelaEscada.instance.escadas_direction;
	}

	public void desvirar_escada() {
		stairs_type = 0;
	}

	public boolean tem_sprites() {
		for (ArrayList<int[]> spr : aCoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
			if (spr.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean pode_descer_com_colisao() {
		if (stairs_type == 1 || stairs_type == 2
				|| stairs_type == 3 /* || (stairs_type == 4 && !aberto_ou_fechado) */) {
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

	public boolean trocar_pagina(int x, int y, int prRodinha) {
		posicao_Conjunto += prRodinha;
		if (posicao_Conjunto >= aCoConjuntoSprites.size())
			posicao_Conjunto = 0;
		else if (posicao_Conjunto < 0)
			posicao_Conjunto = aCoConjuntoSprites.size() - 1;

		return true;
	}

	public void varios(int virar_solido) {
		// Ação realizada no World.fill ou World.empty
		if (Ui.opcao == 0) {
			// Colocar sprites
			if (Ui.substituir || !tem_sprites())
				adicionar_sprite_selecionado();
			else
				setSolid(virar_solido);
		}
	}

	public void setSprites(ArrayList<ArrayList<int[]>> sprites) {
		this.aCoConjuntoSprites.get(posicao_Conjunto).setSprites(sprites);
	}

	public ArrayList<ArrayList<int[]>> getSprites() {
		return aCoConjuntoSprites.get(posicao_Conjunto).getSprites();
	}

	public void mar(int solido) {
		if (solido == 1)
			solid = 2;
		else
			solid = 0;
	}

	public void lava(int solido) {
		if (solido == 1)
			solid = 3;
		else
			solid = 0;

	}

	public void vip(int solido) {
		if (solido == 1)
			solid = 4;
		else
			solid = 0;
	}

	public HashMap<String, Object> getaPropriedades() {
		return aPropriedades;
	}

	public void setaPropriedades(HashMap<String, Object> aPropriedades) {
		this.aPropriedades = aPropriedades;
	}

	public static int tileExisteLista(int prPos, ArrayList<Tile> prTilesList) {
		for (int i = 0; i < prTilesList.size(); i++) {
			Tile iTile = prTilesList.get(i);
			if (prPos == iTile.getaPos()) {
				return i;
			}
		}
		return -1;
	}

}
