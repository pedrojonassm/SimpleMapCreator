package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Rectangle;
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
import graficos.telas.configuracao.TelaConfiguracao;
import graficos.telas.configuracao.subtelas.SubTelaVelocidade;
import graficos.telas.construcao.TelaConstrucoes;
import graficos.telas.sprites.TelaSprites;
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
	public static final int WIDTH = 1240, HEIGHT = 720, TS = 64;
	public static int FPS = 0;
	private BufferedImage image;

	public static int nivel;
	public static World world;
	public static double amountOfTicks = 60.0;

	public static Player player;
	salvarCarregar memoria;

	public static Rectangle quadrado;
	private int aPos, aPosOld, aCliqueMouse;
	private boolean clique_no_mapa, aTrocouPosicao;
	public static boolean control, shift;
	public static Random random;
	public static Ui ui;
	public static int sprite_selecionado_index;
	private int sprite_selecionado_animation_time, aEstadoTile;

	public Gerador() {

		player = new Player(Gerador.TS * 5, Gerador.TS * 5, 0);
		memoria = new salvarCarregar();
		quadrado = new Rectangle(Gerador.TS, Gerador.TS);
		ui = new Ui();
		world = new World(null);
		if (world.ok) {
			World.carregar_sprites();
			control = shift = clique_no_mapa = false;
			random = new Random();
			memoria.carregar_livros();
			memoria.carregar_construcoes();
			initFrame();
			fd = new FileDialog(Gerador.frame, "Choose a file", FileDialog.LOAD);
			fd.setDirectory("C:\\");
			fd.setFile("*.world");

			image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			sprite_selecionado_index = sprite_selecionado_animation_time = 0;
		}

	}

	public void initFrame() {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
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
				if (Ui.opcao == 0) {
					if (shift) {
						if (World.tiles[aPos] != null)
							World.tiles[aPos].pegarsprites();
						clique_no_mapa = false;
					} else {
						Tile lEscolhido = World.pegarAdicionarTileMundo(aPos);
						if (lEscolhido != null) {
							/*
							 * if (Ui.colocar_parede) { lEscolhido.setSolid(aEstadoTile); if
							 * (TelaSprites.sprite_selecionado.size() > 0) {
							 * lEscolhido.adicionar_sprite_selecionado(); } } else
							 */

							if (TelaConfiguracao.instance.getOpcao() == 0 && Ui.opcao == 1) {
								if (lEscolhido.getZ() < World.HIGH - 1)
									World.pegarAdicionarTileMundo(World.calcular_pos(lEscolhido.getX(),
											lEscolhido.getY(), lEscolhido.getZ() + 1)).virar_escada();

							} else if (TelaSprites.instance.getMultiplosSprites()) {
								lEscolhido.adicionarMultiplosSprites();
							} else {
								lEscolhido.adicionar_sprite_selecionado();
							}
						}
					}
				} else if (Ui.opcao == 1) {
					if (TelaConfiguracao.instance.getOpcao() == 1) {
						Tile lEscolhido = World.pegarAdicionarTileMundo(aPos);

						if (lEscolhido != null) {
							lEscolhido.setSpeed_modifier(SubTelaVelocidade.instance.getNew_speed());
						}
					}
				} else if (Ui.opcao == 2) {
					World.colocar_construcao(aPos, TelaConstrucoes.instance.pegar_construcao_selecionada());
				} else if (Ui.opcao == 3) {
					// Cidades e casas
				}
			} else if (aCliqueMouse == 3) {
				boolean lAdicionar = (Tile.tileExisteLista(aPos, Ui.aTilesSelecionados) >= 0);
				if ((Ui.opcao <= 1) && (lAdicionar && aEstadoTile >= 0) || (!lAdicionar && aEstadoTile == -1))
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
		g.fillRect(0, 0, WIDTH, HEIGHT);
		world.render(g);

		g.setColor(Color.red);
		int[] quadradinho_teste = World.calcularPosicaoSemAltura(aPos);
		g.drawRect(quadradinho_teste[0], quadradinho_teste[1], quadrado.width, quadrado.height);
		if (TelaSprites.sprite_selecionado.size() > 0
				&& (!ui.getCaixinha_dos_sprites().contains(quadrado.x, quadrado.y) || !Ui.mostrar)) {
			if (++sprite_selecionado_animation_time >= World.max_tiles_animation_time) {
				sprite_selecionado_animation_time = 0;
				if (++sprite_selecionado_index >= TelaSprites.sprite_selecionado.size()) {
					sprite_selecionado_index = 0;
				}
			}
			BufferedImage imagem = World.sprites_do_mundo.get(
					TelaSprites.array.get(sprite_selecionado_index))[TelaSprites.lista.get(sprite_selecionado_index)];
			if (imagem.getWidth() > quadrado.width || imagem.getHeight() > quadrado.height) {
				quadradinho_teste[0] -= quadrado.width * ((imagem.getWidth() / quadrado.width) - 1); // TODO Ajustar
																										// para imagenbs
																										// em qualquer
																										// tamanho
				quadradinho_teste[1] -= quadrado.height * ((imagem.getWidth() / quadrado.height) - 1); // TODO testar
			}
			g.drawImage(imagem, quadradinho_teste[0], quadradinho_teste[1], null);
		}
		// */
		player.render(g);
		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		bs.show();
	}

	public void run() {
		long lastTime = System.nanoTime();
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				if (World.ready) {
					tick();
					render();
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
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			Ui.mostrar = !Ui.mostrar;
		if (control) {
			if (e.getKeyCode() == KeyEvent.VK_S)
				World.salvar();
			else if (e.getKeyCode() == KeyEvent.VK_N)
				World.novo_mundo(null);
			else if (e.getKeyCode() == KeyEvent.VK_O)
				World.carregar_mundo();
		}
		if (e.getKeyChar() == '-') {
			int k = SubTelaVelocidade.instance.getNew_speed() * -1;
			if (player.getSpeed() + k <= 0) {
				k = (player.getSpeed() - 1) * -1;
			}
			SubTelaVelocidade.instance.setNew_speed(k);
		}
		if (e.getKeyCode() < 58 && e.getKeyCode() > 47) {
			ui.hotBar(Integer.parseInt(e.getKeyChar() + ""));
		}

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
			System.out.println("tx: " + teste[0] + " ty: " + teste[1] + "\n");
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
		if (ui.trocar_pagina(e.getX(), e.getY(), e.getWheelRotation()))
			return;
		else {
			if (control) {
				player.camada(e.getWheelRotation());
				aPos = World.calcular_pos(e.getX() + Camera.x, e.getY() + Camera.y, player.getZ());
			} else if (shift && TelaSprites.instance.getMultiplosSprites()) {
				Tile lEscolhido = World.tiles[aPos];
				lEscolhido.trocar_pagina(e.getX(), e.getY(), e.getWheelRotation());
			}
		}
	}

}
