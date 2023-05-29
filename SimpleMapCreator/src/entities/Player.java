package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;

import files.SalvarCarregar;
import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import graficos.telas.configuracao.subtelas.SubTelaTransporte;
import main.Gerador;
import main.Uteis;
import main.interfaces.tickRender;
import world.Camera;
import world.Tile;
import world.World;

public class Player implements tickRender {
	private int x, y, z, tile_speed;
	private int horizontal, vertical, speed;
	public boolean left, right, up, down, aBloqueadoMovimentacao;
	Tile sqm_alvo = null;
	public int aPosAtual, aPosAlvo;

	public Player(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.horizontal = z;
		tile_speed = 0;
		left = right = up = down = aBloqueadoMovimentacao = false;

		aPosAtual = aPosAlvo = 0;

		speed = 4;
		horizontal = vertical = 0;
	}

	public int getSpeed() {
		return speed;
	}

	public void tick() {

		if (sqm_alvo != null
				&& Uteis.distancia(sqm_alvo.getX(), x, sqm_alvo.getY(), y) <= Uteis.modulo(speed + tile_speed)) {
			x = sqm_alvo.getX();
			y = sqm_alvo.getY();
			int k = sqm_alvo.ModificadorVelocidade();
			if (k > 0)
				tile_speed = k;
			else
				tile_speed = k;
			if (tile_speed == speed)
				tile_speed--;
			sqm_alvo = null;
			aPosAtual = aPosAlvo;
			Gerador.instance.calculcarPosMouse();
		} else if (sqm_alvo == null) {
			if (left) {
				if (x - Gerador.TS >= 0)
					horizontal = -1;

			} else if (right) {

				if (x + Gerador.TS < World.WIDTH * Gerador.TS)
					horizontal = 1;

			} else {
				horizontal = 0;
			}
			if (up) {

				if (y - Gerador.TS >= 0)
					vertical = -1;

			} else if (down) {

				if (y + Gerador.TS < World.HEIGHT * Gerador.TS)
					vertical = 1;

			} else {
				vertical = 0;
			}
			if ((horizontal != 0 || vertical != 0) && !aBloqueadoMovimentacao) {
				boolean lInverteuVelocidade = false;
				if (speed + tile_speed < 0) {
					lInverteuVelocidade = true;
					horizontal *= -1;
					vertical *= -1;
				}

				sqm_alvo = World
						.pegar_chao(World.calcular_pos(x + Gerador.TS * horizontal, y + Gerador.TS * vertical, z));

				if (sqm_alvo != null) {
					if (sqm_alvo.Solid())
						sqm_alvo = null;

					else if (Uteis.distancia(sqm_alvo.getX(), x, sqm_alvo.getY(), y) <= speed * 3
							+ Uteis.modulo(tile_speed) * 2)
						aBloqueadoMovimentacao = true;
				}
				if (sqm_alvo != null)
					aPosAlvo = sqm_alvo.getaPos();
				if (lInverteuVelocidade) {
					horizontal *= -1;
					vertical *= -1;
				}
			}
		} else {
			x += (speed + tile_speed) * horizontal;
			y += (speed + tile_speed) * vertical;
		}

		colidindoTransporte();
		updateCamera();
	}

	@SuppressWarnings("unchecked")
	public void utilizarEscada(Tile prTile) {
		HashMap<String, Object> lHashMap = (HashMap<String, Object>) prTile.getPropriedade("TRANSPORT");
		if (lHashMap == null || lHashMap.get("DESTINY") == null)
			return;

		Tile lTile = World.pegarAdicionarTileMundo(Tile.pegarPosicaoRelativa(prTile.getX(), prTile.getY(),
				prTile.getZ(), (List<Integer>) lHashMap.get("DESTINY")));

		x = lTile.getX();
		y = lTile.getY();
		z = lTile.getZ();
		sqm_alvo = lTile;
		aPosAlvo = sqm_alvo.getaPos();
	}

	private void colidindoTransporte() {
		if (Gerador.ui.getTela().getSubTela() instanceof SubTelaTransporte) {

			Tile lTile = World.pegar_chao(x + Gerador.TS / 2, y + Gerador.TS / 2, z);

			if (lTile != null && lTile.getPropriedade("TRANSPORT") != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> lHashmap = (HashMap<String, Object>) lTile.getPropriedade("TRANSPORT");
				try {
					if (lHashmap.get("TYPE") != null && lHashmap.get("TYPE").toString()
							.contentEquals(SubTelaTransporte.instance.opcaoSelecionada))
						utilizarEscada(lTile);
				} catch (Exception e) {
				}
			}
		} else if (Gerador.ui.getTela().getSubTela() instanceof SubTelaPropriedade) {
			Tile lTile = World.pegar_chao(x + Gerador.TS / 2, y + Gerador.TS / 2, z);

			if (lTile != null && lTile.getPropriedade("ToOtherWorld") != null) {
				Tile lPosicao = World.pegar_chao(lTile.getX() + Gerador.TS * horizontal * -1,
						lTile.getY() + Gerador.TS * vertical * -1, lTile.getZ());
				setX(lPosicao.getX());
				setY(lPosicao.getY());
				setZ(lPosicao.getZ());

				SalvarCarregar.toOtherWorld(lTile.getPropriedade("ToOtherWorld").toString());

			}
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
		Camera.x = Camera.clamp(x - Gerador.windowWidth / 2, 0,
				World.WIDTH * Gerador.quadrado.width - Gerador.windowWidth);
		Camera.y = Camera.clamp(y - Gerador.windowHEIGHT / 2, 0,
				World.HEIGHT * Gerador.quadrado.height - Gerador.windowHEIGHT);

	}

	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x - Camera.x, y - Camera.y, Gerador.quadrado.width, Gerador.quadrado.height);

		if (sqm_alvo != null) {
			g.setColor(new Color(175, 75, 50, 50));
			g.fillRect(sqm_alvo.getX() - Camera.x - (sqm_alvo.getZ() - Gerador.player.getZ()) * Gerador.quadrado.width,
					sqm_alvo.getY() - Camera.y - (sqm_alvo.getZ() - Gerador.player.getZ()) * Gerador.quadrado.height,
					Gerador.TS, Gerador.TS);
		}
	}

	public void camada(int acao) {
		int fz = z;
		if (acao > 0) {
			if (++fz >= World.HIGH) {
				fz = 0;
			}
		} else if (acao < 0) {
			if (--fz < 0) {
				fz = World.HIGH - 1;
			}
		}
		Tile lTile = World.pegar_chao(x, y, fz);
		if (lTile == null || !lTile.Solid()) {
			z = fz;
		}

	}
}
