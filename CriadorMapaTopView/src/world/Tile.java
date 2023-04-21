package world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import graficos.ConjuntoSprites;
import graficos.Ui;
import graficos.telas.Sprite;
import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import graficos.telas.configuracao.subtelas.SubTelaTransporte;
import graficos.telas.sprites.TelaSprites;
import graficos.telas.sprites.subtelas.SubTelaMultiplosSprites;
import main.Gerador;
import main.Uteis;

public class Tile {
	private ArrayList<ConjuntoSprites> CoConjuntoSprites;
	private int x, y, z, aPos, posicao_Conjunto, aux;

	private HashMap<String, Object> aPropriedades;

	public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("z") int z) {
		posicao_Conjunto = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		CoConjuntoSprites = new ArrayList<>();
		CoConjuntoSprites.add(new ConjuntoSprites());
		aPos = World.calcular_pos(x, y, z);
	}

	public int ModificadorVelocidade() {
		if (aPropriedades != null && aPropriedades.get("Speed") != null)
			try {
				return Integer.parseInt(aPropriedades.get("Speed").toString());
			} catch (Exception e) {
			}
		return 0;
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
		return CoConjuntoSprites.get(posicao_Conjunto).obterSprite_atual();
	}

	@SuppressWarnings("unchecked")
	public void render(Graphics g) {
		if (posicao_Conjunto < CoConjuntoSprites.size() && CoConjuntoSprites.get(posicao_Conjunto) != null)
			for (ArrayList<Sprite> imagens : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
				if (imagens != null && imagens.size() > 0) {
					Sprite sprite = imagens.get(World.tiles_index % imagens.size());
					int dx, dy;
					BufferedImage image = sprite.pegarImagem();
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

		if (Gerador.ui.getTela().getSubTela() instanceof SubTelaPropriedade && aPropriedades != null
				&& aPropriedades.get(SubTelaPropriedade.instance.getPropriedadeSelecionada()) != null) {
			g.setColor(new Color(255, 255, 255, 50));
			g.fillRect(x - Camera.x, y - Camera.y, Gerador.TS, Gerador.TS);
			g.setColor(Color.white);

			aux = g.getFontMetrics()
					.stringWidth(aPropriedades.get(SubTelaPropriedade.instance.getPropriedadeSelecionada()).toString());
			if (aux > Gerador.TS) {
				String lPropriedadeMostrada = aPropriedades.get(SubTelaPropriedade.instance.getPropriedadeSelecionada())
						.toString()
						.substring(0, aPropriedades.get(SubTelaPropriedade.instance.getPropriedadeSelecionada())
								.toString().length() / ((aux / Gerador.TS) + 1));
				aux = g.getFontMetrics().stringWidth(lPropriedadeMostrada + "...");
				Ui.renderizarDepois.add(() -> g.drawString(lPropriedadeMostrada + "...",
						x + Gerador.TS / 2 - aux / 2 - Camera.x, y + Gerador.TS / 2 - Camera.y));
			} else
				Ui.renderizarDepois.add(() -> g.drawString(
						aPropriedades.get(SubTelaPropriedade.instance.getPropriedadeSelecionada()).toString(),
						x + Gerador.TS / 2 - aux / 2 - Camera.x, y + Gerador.TS / 2 - Camera.y));

		} else if (Gerador.ui.getTela().getSubTela() instanceof SubTelaTransporte && aPropriedades != null) {
			if (aPropriedades.get("TRANSPORT") != null) {
				HashMap<String, Object> lHashMap = (HashMap<String, Object>) aPropriedades.get("TRANSPORT");
				if (lHashMap.get("TYPE") != null && SubTelaTransporte.instance.opcaoSelecionada != null
						&& SubTelaTransporte.instance.opcaoSelecionada.contentEquals(lHashMap.get("TYPE").toString())) {
					g.setColor(new Color(255, 255, 255, 50));
					g.fillRect(x - Camera.x, y - Camera.y, Gerador.TS, Gerador.TS);
					if (Gerador.player.getZ() == z
							&& Gerador.quadrado.intersects(x - Camera.x + Gerador.quadrado.x % Gerador.TS,
									y - Camera.y + Gerador.quadrado.y % Gerador.TS, Gerador.TS, Gerador.TS)
							&& lHashMap.get("DESTINY") != null) {
						g.setColor(Color.white);
						Tile lTile = World.pegarAdicionarTileMundo(
								Tile.pegarPosicaoRelativa(x, y, z, (List<Integer>) lHashMap.get("DESTINY")));
						if (lTile != null) {
							int lDiferencaNivel = lTile.getZ() - z, angulo = (lDiferencaNivel > 0) ? 45 : 225;
							Ui.renderizarDepois.add(
									() -> g.drawRect(lTile.getX() - Camera.x - Gerador.quadrado.width * lDiferencaNivel,
											lTile.getY() - Camera.y - Gerador.quadrado.height * lDiferencaNivel,
											Gerador.TS, Gerador.TS));
							for (aux = 0; aux < Uteis.modulo(lDiferencaNivel); aux++) {
								Ui.renderizarDepois.add(() -> g.drawArc(
										lTile.getX() - Camera.x + Gerador.quadrado.width / 2
												- Gerador.quadrado.width * lDiferencaNivel,
										lTile.getY() - Camera.y + Gerador.quadrado.height / 2
												+ (aux + 1) * Gerador.TS / 10
												- Gerador.quadrado.height * lDiferencaNivel,
										Gerador.TS / 10, Gerador.TS / 10, angulo, 90));
							}

						}
					}
				}
			}
		}

	}

	public void adicionar_sprite_selecionado() {
		if (CoConjuntoSprites.size() == 0)
			CoConjuntoSprites.add(new ConjuntoSprites());
		CoConjuntoSprites.get(posicao_Conjunto).adicionar_sprite_selecionado();
	}

	@SuppressWarnings("unchecked")
	public void adicionarMultiplosSprites() {
		if (SubTelaMultiplosSprites.instance.getConjuntoSprites() != null) {
			CoConjuntoSprites.clear();
			for (ConjuntoSprites iConjuntoSprites : SubTelaMultiplosSprites.instance.getConjuntoSprites())
				CoConjuntoSprites.add(iConjuntoSprites.clone());

		}

	}

	public void copiarPraTela() {
		if (Gerador.ui.getTela().getSubTela() instanceof SubTelaMultiplosSprites)
			SubTelaMultiplosSprites.instance.addSpritesConjunto(CoConjuntoSprites);
		else if (Gerador.ui.getTela().getSubTela() instanceof SubTelaPropriedade) {
			if (aPropriedades != null
					&& aPropriedades.get(SubTelaPropriedade.instance.getPropriedadeSelecionada()) != null)
				SubTelaPropriedade.instance.setValorPropriedade(
						aPropriedades.get(SubTelaPropriedade.instance.getPropriedadeSelecionada()).toString());
		} else
			CoConjuntoSprites.get(posicao_Conjunto).pegarsprites();
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
		for (ArrayList<Sprite> spr : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
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
		if (prKey == null)
			return;
		if (aPropriedades == null)
			aPropriedades = new HashMap<>();
		if (aPropriedades.get(prKey) != null)
			aPropriedades.remove(prKey);
		if (prValor != null && !prValor.toString().isBlank())
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

	public boolean tem_sprites() {
		for (ArrayList<Sprite> spr : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
			if (spr.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean trocar_pagina(int x, int y, int prRodinha) {
		posicao_Conjunto += prRodinha;
		if (posicao_Conjunto >= CoConjuntoSprites.size())
			posicao_Conjunto = 0;
		else if (posicao_Conjunto < 0)
			posicao_Conjunto = CoConjuntoSprites.size() - 1;

		return true;
	}

	public void varios() {
		if (Gerador.ui.getTela() instanceof TelaSprites) {
			if (Ui.substituir || !tem_sprites())
				adicionar_sprite_selecionado();
		} else if (Gerador.ui.getTela().getSubTela() instanceof SubTelaPropriedade) {
			SubTelaPropriedade.instance.adicionarPropriedadeTile(this);
		}
	}

	public boolean Solid() {
		if (aPropriedades == null || getPropriedade("Solid") == null)
			return false;
		try {
			if (Boolean.valueOf(getPropriedade("Solid").toString())
					|| Integer.parseInt(getPropriedade("Solid").toString()) == 1)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	public List<ConjuntoSprites> getCoConjuntoSprites() {
		return CoConjuntoSprites;
	}

	public void setCoConjuntoSprites(ArrayList<ConjuntoSprites> aCoConjuntoSprites) {
		this.CoConjuntoSprites = aCoConjuntoSprites;
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

	public static List<Integer> pegarPosicaoRelativa(int prFromX, int prFromY, int prFromZ, int prX, int prY, int prZ) {
		List<Integer> lRetorno = new ArrayList<>(); // Horizontal, vertical, altura
		lRetorno.add((prX >> World.log_ts) - (prFromX >> World.log_ts));
		lRetorno.add((prY >> World.log_ts) - (prFromY >> World.log_ts));
		lRetorno.add(prZ - prFromZ);

		return lRetorno;
	}

	public static int pegarPosicaoRelativa(int prFromX, int prFromY, int prFromZ, List<Integer> prPosicaoRelativa) {
		return World.calcular_pos(prFromX + (prPosicaoRelativa.get(0) << World.log_ts),
				prFromY + (prPosicaoRelativa.get(1) << World.log_ts), prFromZ + prPosicaoRelativa.get(2));
	}

}
