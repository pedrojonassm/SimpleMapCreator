package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import entities.Player;
import files.salvarCarregar;
import graficos.Ui;
import graficos.telas.cidadescasas.TelaCidadeCasa;
import graficos.telas.configuracao.TelaConfiguracao;
import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import graficos.telas.construcao.TelaConstrucoes;
import graficos.telas.sprites.TelaSprites;
import graficos.telas.sprites.subtelas.SubTelaMultiplosSprites;
import graficos.telas.sprites.subtelas.SubTelaPreSets;
import main.configs.ExConfig;
import world.Camera;
import world.Tile;
import world.World;

public class Gerador extends Canvas
		implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	public static FileDialog fd;
	private Thread thread;
	private boolean isRunning = true;
	public static int windowWidth = 1240, windowHEIGHT = 720, TS, VariavelX, VariavelY, sprite_selecionado_index, FPS;
	// alguns itens da UI ficaram bem posicionados, mas foram utilizando o TS
	// constante como = 64 e uma tela fixa
	// VariavelX e VariavelY, é a variavel onde, quando era 1240/720 a tela, o valor
	// era 64 e 64
	private BufferedImage image;

	public static int nivel;
	public static World world;

	public static Player player;
	salvarCarregar memoria;

	public static Rectangle quadrado;
	private int aPos, aPosOld, aCliqueMouse;
	private boolean clique_no_mapa, aTrocouPosicao;
	public static boolean control, shift;
	public static Random random;
	public static Ui ui;
	private int sprite_selecionado_animation_time, aEstadoTile;

	public static ExConfig aConfig;

	public static Gerador instance;

	public Gerador() {
		instance = this;
		aConfig = new ExConfig();
		memoria = new salvarCarregar();
		world = new World(null);
		if (World.ok) {
			startGerador();
			initFrame();
		}

	}

	public void startGerador() {
		TS = aConfig.getTileSize();
		VariavelX = windowWidth / 19;
		VariavelY = windowHEIGHT / 11;
		player = new Player(aConfig.getPlayerX(), aConfig.getPlayerY(), 0);
		World.log_ts = Uteis.log(Gerador.TS, 2);
		quadrado = new Rectangle(Gerador.TS, Gerador.TS);
		ui = new Ui();
		World.carregarSprites();
		control = shift = clique_no_mapa = false;
		random = new Random();
		memoria.carregar_livros();
		memoria.carregar_construcoes();
		fd = new FileDialog(Gerador.frame, "Choose a file", FileDialog.LOAD);
		fd.setDirectory(Gerador.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		fd.setFile("*.world");

		image = new BufferedImage(windowWidth, windowHEIGHT, BufferedImage.TYPE_INT_RGB);
		sprite_selecionado_index = 0;
		sprite_selecionado_animation_time = 0;
		World.ready = true;
	}

	public void initFrame() {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO é bem feito, vai ter que reposicionar alguns quadrados unicamente, e
				// resto acreito que irá funcionar
				// mas faça isso depois. E lembre-se do frame.setResizable(false);

				windowWidth = e.getComponent().getWidth();
				windowHEIGHT = e.getComponent().getHeight();
				VariavelX = windowWidth / 19;
				VariavelY = windowHEIGHT / 11;
				ui.posicionarRetangulos();

				super.componentResized(e);
			}
		});
		setPreferredSize(new Dimension(windowWidth, windowHEIGHT));
		frame = new JFrame("Gerador de mundo");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Gerador gerador = new Gerador();
		if (world.ok)
			gerador.start();
	}

	public void tick() {
		if (clique_no_mapa && aTrocouPosicao) {
			aTrocouPosicao = false;
			if (!control) {
				clique_no_mapa = false;
			}
			if (aCliqueMouse == 1) {
				if (shift) {
					if (World.tiles[aPos] != null)
						World.tiles[aPos].copiarPraTela();
					clique_no_mapa = false;
				} else if (ui.getTela() instanceof TelaSprites) {
					Tile lEscolhido = World.pegarAdicionarTileMundo(aPos);
					if (lEscolhido != null) {

						if (!TelaSprites.instance.contemSpritesSelecionados()
								&& ui.getTela().getSubTela() instanceof SubTelaMultiplosSprites) {
							lEscolhido.adicionarMultiplosSprites();
						} else {
							lEscolhido.adicionar_sprite_selecionado();
						}
					}
				} else if (ui.getTela() instanceof TelaConfiguracao) {
					Tile lEscolhido = World.pegarAdicionarTileMundo(aPos);
					if (ui.getTela().getSubTela() instanceof SubTelaPropriedade) {
						SubTelaPropriedade.instance.adicionarPropriedadeTile(lEscolhido);
					}
				} else if (ui.getTela() instanceof TelaConstrucoes) {
					World.colocar_construcao(aPos, TelaConstrucoes.instance.pegar_construcao_selecionada());
				} else if (ui.getTela() instanceof TelaCidadeCasa) {
					// Cidades e casas
				}
			} else if (aCliqueMouse == 3) {
				boolean lAdicionar = (Tile.tileExisteLista(aPos, Ui.aTilesSelecionados) >= 0);
				if ((ui.getTela() instanceof TelaSprites || ui.getTela() instanceof TelaConfiguracao)
						&& (lAdicionar && aEstadoTile >= 0) || (!lAdicionar && aEstadoTile == -1))
					ui.selecionarTile(aPos);
			}
		}
		player.tick();
		world.tick();
		ui.tick();
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, windowWidth, windowHEIGHT);
		world.render(g);

		g.setColor(Color.red);
		int[] quadradinho_teste = World.calcularPosicaoSemAltura(aPos);
		g.drawRect(quadradinho_teste[0], quadradinho_teste[1], quadrado.width, quadrado.height);
		if (TelaSprites.instance.contemSpritesSelecionados()
				&& (!ui.getCaixinha_dos_sprites().contains(quadrado.x, quadrado.y) || !Ui.mostrar)) {
			if (++sprite_selecionado_animation_time >= World.max_tiles_animation_time) {
				sprite_selecionado_animation_time = 0;
				if (++sprite_selecionado_index >= TelaSprites.instance.getNumeroMaxSpritesSelecionados()) {
					sprite_selecionado_index = 0;
				}
			}
			for (int i = 0; i < TelaSprites.instance.sprite_selecionado.size(); i++) {
				if (TelaSprites.instance.sprite_selecionado.get(i).size() == 0
						|| (TelaSprites.kdModoColocar.kdLayerToLayer.equals(TelaSprites.instance.getModoColocar())
								&& i != TelaSprites.tilesLayer))
					continue;

				BufferedImage imagem = World.PegarSprite(
						TelaSprites.instance.nomeSpritesheet.get(i)
								.get(sprite_selecionado_index % TelaSprites.instance.nomeSpritesheet.get(i).size()),
						TelaSprites.instance.PosicaoSprite.get(i)
								.get(sprite_selecionado_index % TelaSprites.instance.PosicaoSprite.get(i).size()));
				if (imagem.getWidth() > quadrado.width || imagem.getHeight() > quadrado.height) {
					quadradinho_teste[0] -= quadrado.width * ((imagem.getWidth() / quadrado.width) - 1);
					quadradinho_teste[1] -= quadrado.height * ((imagem.getWidth() / quadrado.height) - 1);
				}
				g.drawImage(imagem, quadradinho_teste[0], quadradinho_teste[1], null);

			}
		}
		// */
		player.render(g);
		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, windowWidth, windowHEIGHT, null);
		bs.show();
	}

	public void run() {
		long lastTime = System.nanoTime();
		double ns = 1000000000 / aConfig.getAmountOfTicks();
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				if (World.ready && ui.getTela() != null) {
					try {
						tick();
						render();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				FPS = frames;
				frames = 0;
				timer += 1000;
			}
		}
		stop();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (!player.right)
				player.aBloqueadoMovimentacao = false;
			player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (!player.left)
				player.aBloqueadoMovimentacao = false;
			player.left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (!player.up)
				player.aBloqueadoMovimentacao = false;
			player.up = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (!player.down)
				player.aBloqueadoMovimentacao = false;
			player.down = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			control = true;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			shift = true;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			Ui.mostrar = !Ui.mostrar;
			return;
		}
		if (control) {
			if (e.getKeyCode() == KeyEvent.VK_S)
				World.salvar();
			else if (e.getKeyCode() == KeyEvent.VK_N)
				World.novo_mundo(null);
			else if (e.getKeyCode() == KeyEvent.VK_O)
				World.carregar_mundo();
			else if (e.getKeyCode() == KeyEvent.VK_C)
				if (World.tiles[aPos] != null)
					World.tiles[aPos].copiarPraTela();

		} else if (Gerador.ui.getTela().getSubTela() instanceof SubTelaPropriedade) {
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				SubTelaPropriedade.instance.retirarValor();
			else if (e.getKeyCode() == KeyEvent.VK_DELETE)
				SubTelaPropriedade.instance.setValorPropriedade("");
			else if ((e.getKeyChar() + "").getBytes().length == 1)
				SubTelaPropriedade.instance.mudarValor(e.getKeyChar());
			return;
		}
		if (e.getKeyCode() < 58 && e.getKeyCode() > 47 && ui.getTela().getSubTela() instanceof SubTelaPreSets)
			SubTelaPreSets.instance.ativar(Integer.parseInt(e.getKeyChar() + ""));

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			player.right = false;
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			player.left = false;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			player.up = false;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			player.down = false;
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			control = false;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			shift = false;
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
			World.deletarSelecionados();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Tile lEscolhido = World.tiles[aPos];
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!Ui.mostrar || !ui.clicou(e.getX(), e.getY())) {
				clique_no_mapa = true;
				aTrocouPosicao = true;
				aCliqueMouse = 1;
				return;
			}
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			int[] teste = World.calcularPosicaoSemAltura(aPos);
			System.out.println("mx: " + quadrado.x + " my: " + quadrado.y);
			System.out.println("cx: " + Camera.x + " cy: " + Camera.y);
			System.out.println("pos: " + aPos);
			System.out.println("tem tile: " + (lEscolhido != null));
			System.out.println("tx: " + teste[0] + " ty: " + teste[1] + " tz: " + teste[2] + "\n");
			return;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			if (ui.cliquedireito(e.getX(), e.getY()))
				return;
			else {
				aEstadoTile = Tile.tileExisteLista(aPos, Ui.aTilesSelecionados);
				clique_no_mapa = true;
				aTrocouPosicao = true;
				aCliqueMouse = 3;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		clique_no_mapa = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		aPos = World.calcular_pos(e.getX() + Camera.x, e.getY() + Camera.y, player.getZ());
		if (aPosOld != aPos) {
			aPosOld = aPos;
			aTrocouPosicao = true;
		}
		quadrado.x = e.getX();
		quadrado.y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		aPos = World.calcular_pos(e.getX() + Camera.x, e.getY() + Camera.y, player.getZ());
		if (aPosOld != aPos) {
			aPosOld = aPos;
			aTrocouPosicao = true;
		}
		quadrado.x = e.getX();
		quadrado.y = e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (ui.trocar_pagina(e.getX(), e.getY(), (e.getWheelRotation() > 0) ? 1 : -1))
			return;
		else {
			if (control) {
				player.camada(e.getWheelRotation());
				aPos = World.calcular_pos(e.getX() + Camera.x, e.getY() + Camera.y, player.getZ());
			} else if (shift && ui.getTela().getSubTela() instanceof SubTelaMultiplosSprites) {
				Tile lEscolhido = World.tiles[aPos];
				lEscolhido.trocar_pagina(e.getX(), e.getY(), e.getWheelRotation());
			}
		}
	}

}
