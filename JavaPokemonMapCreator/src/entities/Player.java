package entities;

import java.awt.Color;
import java.awt.Graphics;

import main.Gerador;
import main.interfaces.tickRender;
import world.Camera;
import world.Tile;
import world.World;

public class Player implements tickRender{
	private int x, y, z, tile_speed;
	private int horizontal, vertical, speed;
	public boolean left, right, up, down, can_surf, can_walk_on_lava, vip;
	Tile sqm_alvo = null;
	
	public Player(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.horizontal = z;
		tile_speed = 0;
		left = right = up = down = can_surf = can_walk_on_lava = vip = false;
		
		speed = 4;
		horizontal = vertical = 0;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void tick() {
		
		if (sqm_alvo != null && distancia(sqm_alvo.getX(), x, sqm_alvo.getY(), y) <= speed+tile_speed) {
			x = sqm_alvo.getX();
			y = sqm_alvo.getY();
			int k = sqm_alvo.getSpeed_modifier();
			if (k > 0) tile_speed = k*3;
			else tile_speed = k;
			sqm_alvo = null;
		}else if (sqm_alvo == null) {
			boolean mover = false;
			if (left) {
				horizontal = -1;
				mover = true;
			}else if (right) {
				horizontal = 1;
				mover = true;
			}else {
				horizontal = 0;
			}
			if (up) {
				vertical = -1;
				mover = true;
			}else if (down) {
				vertical = 1;
				mover = true;
			}else {
				vertical = 0;
			}
			if (mover) {
				sqm_alvo = World.pegar_chao(World.calcular_pos(x+Gerador.TS*horizontal, y+Gerador.TS*vertical, z));
				
				if (sqm_alvo != null && (sqm_alvo.getSolid() == 1 || (sqm_alvo.getSolid() == 2 && !can_surf) || (sqm_alvo.getSolid() == 3 && !can_walk_on_lava) || (sqm_alvo.getSolid() == 4 && !vip))) {
					sqm_alvo = null;
				}
			}
		}else {
			x += (speed+tile_speed)*horizontal;
			y += (speed+tile_speed)*vertical;
		}
		
		colidindo_com_escada();
		updateCamera();
	}
	
	public static double distancia(int x1, int x2, int y1, int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	public void utilizarEscada(Tile prEscada) {
		if (prEscada == null)
			return;
		if (prEscada.getZ() < z)
			z--;
		else if (prEscada.getZ() == z)
			z++;
		
		switch (prEscada.getStairs_direction()) {
		case 0:
			x=prEscada.getX()+Gerador.quadrado.width;
			y=prEscada.getY();
			break;
		case 1:
			y=prEscada.getY()+Gerador.quadrado.height;
			x=prEscada.getX();
			break;
		case 2:
			x=prEscada.getX()-Gerador.quadrado.width;
			y=prEscada.getY();
			break;
		case 3:
			y=prEscada.getY()-Gerador.quadrado.height;
			x=prEscada.getX();
			break;
		}
		sqm_alvo = World.pegarAdicionarTileMundo(World.calcular_pos(x, y, z));
		x = sqm_alvo.getX(); y = sqm_alvo.getY();
	}
	
	private void colidindo_com_escada() {
		Tile t = World.pegar_chao(x+Gerador.TS/2, y+Gerador.TS/2, z);
		if (t!=null && t.getStairs_type() != 0 && t.pode_subir_com_colisao()) {
			utilizarEscada(t);
			return;
		}
		t = World.pegar_chao(x+Gerador.TS/2, y+Gerador.TS/2, z-1);
		if (t!=null && t.getStairs_type() != 0 && t.pode_descer_com_colisao()) {
			utilizarEscada(t);
			return;
		}
		
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(x - Gerador.WIDTH/2, 0, World.WIDTH*Gerador.TS - Gerador.WIDTH);
		Camera.y = Camera.clamp(y - Gerador.HEIGHT/2, 0, World.HEIGHT*Gerador.TS - Gerador.HEIGHT);
	}
	
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x - Camera.x, y - Camera.y, Gerador.quadrado.width, Gerador.quadrado.height);
		
	}

	public void camada(int acao) {
		int fz = z;
		if (acao > 0) {
			if (++fz >= World.HIGH) {
				fz = 0;
			}
		}else if (acao < 0) {
			if (--fz < 0) {
				fz = World.HIGH-1;
			}
		}
		Tile lTile = World.pegar_chao(x, y, fz);
		if (lTile == null || lTile.getSolid() != 1) {
			z = fz;
		}
		
	}
}
