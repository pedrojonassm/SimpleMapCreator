package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.io.File;
import java.text.NumberFormat;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import entities.Player;
import files.SalvarCarregar;
import graficos.Ui;
import graficos.telas.configuracao.TelaConfiguracao;
import graficos.telas.configuracao.subtelas.SubTelaPropriedade;
import graficos.telas.construcao.TelaConstrucoes;
import graficos.telas.sprites.TelaSprites;
import graficos.telas.sprites.subtelas.SubTelaMultiplosSprites;
import graficos.telas.sprites.subtelas.SubTelaPreSets;
import main.configs.Configs;
import main.configs.ExSpriteSheet;
import world.Camera;
import world.Tile;
import world.World;

public class Gerador extends Canvas
		implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	public static FileDialog aFileDialog;
	private Thread thread;
	private boolean isRunning = true;
	public static int windowWidth = 1240, windowHEIGHT = 720, TS, VariavelX, VariavelY, sprite_selecionado_index, FPS;
	// alguns itens da UI ficaram bem posicionados, mas foram utilizando o TS
	// constante como = 64 e uma tela fixa VariavelX e VariavelY, é a variavel onde,
	// quando era 1240/720 a tela, o valor era 64 e 64
	private BufferedImage image;

	public static World world;

	public static Player player;
	SalvarCarregar memoria;

	public static Rectangle quadrado;
	private int aPos, aPosOld, aCliqueMouse, aSpaceClickX, aSpaceClickY;
	private Tile aTileCliqueDireitoInicial;
	private boolean clique_no_mapa, aTrocouPosicao;
	public static boolean control, shift, space;
	public static Random random;
	public static Ui ui;
	private int sprite_selecionado_animation_time, aEstadoTile;

	public static Configs aConfig;

	public static Gerador instance;

	public Gerador() {
		instance = this;
		aConfig = new Configs();
		memoria = new SalvarCarregar();
		world = new World(null);
		if (World.ok) {
			startGerador();
			initFrame();
		}

	}

	public void startGerador() {
		TS = aConfig.getTileSize();
		VariavelX = 64;
		VariavelY = 64;
		player = new Player(aConfig.getPlayerX(), aConfig.getPlayerY(), 0);
		player.aSeguindo = aConfig.getSeguindoJogador();
		World.log_ts = Uteis.log(Gerador.TS, 2);
		quadrado = new Rectangle(Gerador.TS, Gerador.TS);
		ui = new Ui();
		World.carregarSprites();
		control = shift = clique_no_mapa = false;
		random = new Random();
		memoria.carregar_livros();
		memoria.carregar_construcoes();
		aFileDialog = new FileDialog(Gerador.frame, "Choose a file", FileDialog.LOAD);
		Gerador.aFileDialog.setDirectory(Gerador.class.getProtectionDomain().getCodeSource().getLocation().getPath());
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
				// É bem feito, vai ter que reposicionar alguns quadrados unicamente, e
				// resto acreito que irá funcionar
				// mas faça isso depois. E lembre-se do frame.setResizable(false);

				windowWidth = e.getComponent().getWidth();
				windowHEIGHT = e.getComponent().getHeight();
				ui.posicionarRetangulos();

				super.componentResized(e);
			}
		});
		frame = new JFrame("Criador de Mundo");
		frame.setMenuBar(createMenuBar());
		setPreferredSize(new Dimension(windowWidth, windowHEIGHT));
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
		if (World.ok)
			gerador.start();
	}

	public void tick() {
		if (clique_no_mapa && aTrocouPosicao) {
			aTrocouPosicao = false;
			if (aCliqueMouse == 1) {
				if (space) {
					Camera.x += aSpaceClickX - quadrado.x;
					Camera.y += aSpaceClickY - quadrado.y;

					if (Camera.x < -1 * windowWidth / 2)
						Camera.x = -1 * windowWidth / 2;
					if (Camera.y < -1 * windowHEIGHT / 2)
						Camera.y = -1 * windowHEIGHT / 2;
					aSpaceClickX = quadrado.x;
					aSpaceClickY = quadrado.y;
				} else {
					if (!control) {
						clique_no_mapa = false;
					}
					if (shift) {
						if (World.pegar_chao(aPos) != null)
							World.pegar_chao(aPos).copiarPraTela();
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
					}
				}

			} else if (aCliqueMouse == 3) {
				boolean lAdicionar = (Tile.tileExisteLista(aPos, Ui.aTilesSelecionados) >= 0);
				if ((ui.getTela() instanceof TelaSprites || ui.getTela() instanceof TelaConfiguracao)
						&& ((lAdicionar && aEstadoTile >= 0) || (!lAdicionar && aEstadoTile == -1))
						&& aTileCliqueDireitoInicial != null)
					if (control || aTileCliqueDireitoInicial.getaPos() == aPos) {
						ui.selecionarTile(aPos);
					} else {

						int[] menorXYZ = Uteis.calcularPosicaoSemAltura(aTileCliqueDireitoInicial.getaPos()),
								maiorXYZ = Uteis.calcularPosicaoSemAltura(aPos);
						int aux;
						for (int i = 0; i < 3; i++) {
							if (menorXYZ[i] > maiorXYZ[i]) {
								aux = maiorXYZ[i];
								maiorXYZ[i] = menorXYZ[i];
								menorXYZ[i] = aux;
							}
						}

						for (int xx = menorXYZ[0]; xx <= maiorXYZ[0]; xx += TS)
							for (int yy = menorXYZ[1]; yy <= maiorXYZ[1]; yy += TS)
								for (int zz = menorXYZ[2]; zz <= maiorXYZ[2]; zz += TS) {
									aux = World.calcular_pos(xx, yy, zz);
									lAdicionar = (Tile.tileExisteLista(aux, Ui.aTilesSelecionados) >= 0);
									if ((lAdicionar && aEstadoTile >= 0) || (!lAdicionar && aEstadoTile == -1))
										ui.selecionarTile(aux);
								}
					}
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
		g.fillRect(0, 0, windowWidth + TS, windowHEIGHT + TS);
		if (World.ready && ui.getTela() != null) {
			world.render(g);

			g.setColor(Color.red);
			int[] localDesenho = Uteis.calcularPosicaoSemAlturaIgnorandoCamera(aPos);
			int desenharX, desenharY;
			g.drawRect(localDesenho[0], localDesenho[1], quadrado.width, quadrado.height);
			desenharX = g.getFontMetrics().stringWidth(aPos + "");
			g.drawString(aPos + "", localDesenho[0] + quadrado.width / 2 - desenharX / 2,
					localDesenho[1] + quadrado.height / 2);
			if (ui.getTela() instanceof TelaSprites && TelaSprites.instance.contemSpritesSelecionados()) {
				if (++sprite_selecionado_animation_time >= World.max_tiles_animation_time) {
					sprite_selecionado_animation_time = 0;
					if (++sprite_selecionado_index >= TelaSprites.instance.getNumeroMaxSpritesSelecionados()) {
						sprite_selecionado_index = 0;
					}
				}
				if ((!ui.getCaixinha_dos_sprites().contains(quadrado.x, quadrado.y) || !Ui.mostrar))
					for (int i = 0; i < TelaSprites.instance.spriteSelecionado.size(); i++) {

						if (TelaSprites.instance.spriteSelecionado.get(i).size() == 0
								|| (TelaSprites.kdModoColocar.kdLayerToLayer
										.equals(TelaSprites.instance.getModoColocar()) && i != TelaSprites.LayerLevel))
							continue;

						desenharX = localDesenho[0];
						desenharY = localDesenho[1];

						BufferedImage imagem = World.PegarSprite(
								TelaSprites.instance.nomeSpritesheet.get(i).get(
										sprite_selecionado_index % TelaSprites.instance.nomeSpritesheet.get(i).size()),
								TelaSprites.instance.PosicaoSprite.get(i).get(
										sprite_selecionado_index % TelaSprites.instance.PosicaoSprite.get(i).size()));
						if (imagem.getWidth() > quadrado.width || imagem.getHeight() > quadrado.height) {
							desenharX -= quadrado.width * ((imagem.getWidth() / quadrado.width) - 1);
							desenharY -= quadrado.height * ((imagem.getWidth() / quadrado.height) - 1);
						}
						g.drawImage(imagem, desenharX, desenharY, null);

					}
			}
			// */
			if (player.aPosAtual < 0 || World.tiles[player.aPosAtual] == null)
				player.render(g);
			ui.render(g);
		} else {
			g.setColor(Color.white);
			sprite_selecionado_animation_time += 5;
			if (sprite_selecionado_animation_time >= 360) {
				sprite_selecionado_animation_time = 30;
			}

			sprite_selecionado_index += 3;
			if (sprite_selecionado_index >= 360)
				sprite_selecionado_index = 0;

			g.drawArc(windowWidth / 2 - TS / 2, windowHEIGHT / 2 - TS / 2, TS, TS, sprite_selecionado_index,
					sprite_selecionado_animation_time);
		}
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

				try {
					if (World.ready && ui.getTela() != null)
						tick();
					render();
				} catch (Exception e) {
					e.printStackTrace();
				}
				frames++;
				delta = 0;
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
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			space = true;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			Ui.mostrar = !Ui.mostrar;
			return;
		}
		if (control) {
			if (e.getKeyCode() == KeyEvent.VK_C)
				if (World.pegar_chao(aPos) != null)
					World.pegar_chao(aPos).copiarPraTela();

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
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			space = false;
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
		Tile lEscolhido = World.pegar_chao(aPos);
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (space || !Ui.mostrar || !ui.clicou(e.getX(), e.getY())) {
				clique_no_mapa = true;
				aTrocouPosicao = true;
				aCliqueMouse = 1;
				if (space) {
					aSpaceClickX = e.getX();
					aSpaceClickY = e.getY();
				}
				return;
			}
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			int[] teste = Uteis.calcularPosicaoSemAlturaIgnorandoCamera(aPos);
			System.out.println("mx: " + quadrado.x + " my: " + quadrado.y);
			System.out.println("ex: " + e.getX() + " ey: " + e.getY());
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
				aTileCliqueDireitoInicial = World.pegarAdicionarTileMundo(aPos);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		clique_no_mapa = false;
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (ui.getTela() instanceof TelaSprites)
				TelaSprites.instance.endClick(1);
			ui.cliqueUi = false;
		}

	}

	public void calculcarPosMouse() {
		aPos = World.calcular_pos(quadrado.x + Camera.x, quadrado.y + Camera.y, player.getZ());
		if (aPosOld != aPos) {
			aPosOld = aPos;
			aTrocouPosicao = true;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getX() < 0 || e.getY() < 0 || e.getX() > windowWidth || e.getY() > windowHEIGHT)
			return;
		quadrado.x = e.getX();
		quadrado.y = e.getY();
		if (space)
			aTrocouPosicao = true;
		calculcarPosMouse();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (e.getX() < 0 || e.getY() < 0 || e.getX() > windowWidth || e.getY() > windowHEIGHT)
			return;
		quadrado.x = e.getX();
		quadrado.y = e.getY();
		calculcarPosMouse();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (ui.trocar_pagina(e.getX(), e.getY(), (e.getWheelRotation() > 0) ? 1 : -1))
			return;
		else {
			if (control) {
				player.camada(e.getWheelRotation());
				calculcarPosMouse();
			} else if (shift && ui.getTela().getSubTela() instanceof SubTelaMultiplosSprites) {

				Tile lEscolhido = World.pegar_chao(aPos);
				lEscolhido.trocar_pagina(e.getX(), e.getY(), e.getWheelRotation());
			}
		}
	}

	public int getPos() {
		return aPos;
	}

	private MenuBar createMenuBar() {
		MenuBar lMenuBar = new MenuBar();

		lMenuBar.add(createMenuFile());

		lMenuBar.add(createMenuConfig());

		lMenuBar.add(createMenuImportar());

		lMenuBar.add(createMenuExportar());

		return lMenuBar;
	}

	private Menu createMenuConfig() {
		Menu lMenuConfig = new Menu("Config");
		Menu lMenuPlayer = new Menu("Player");
		MenuItem lMenuItem;

		lMenuItem = new MenuItem("Seguir Jogador");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Gerador.player.aSeguindo = true;
			}
		});

		lMenuPlayer.add(lMenuItem);

		lMenuItem = new MenuItem("Deixar de Seguir Jogador");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Gerador.player.aSeguindo = false;
			}
		});

		lMenuPlayer.add(lMenuItem);

		lMenuItem = new MenuItem("Mover Jogador pra cá");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tile lTile = World.pegar_chao(Camera.x + windowWidth / 2, Camera.y + windowHEIGHT / 2, player.getZ());
				if (lTile != null) {
					player.setX(lTile.getX());
					player.setY(lTile.getY());
				}

			}
		});

		lMenuPlayer.add(lMenuItem);

		lMenuItem = new MenuItem("Ir até o jogador");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				player.updateCamera();

			}
		});

		lMenuPlayer.add(lMenuItem);

		lMenuConfig.add(lMenuPlayer);

		return lMenuConfig;
	}

	private Menu createMenuFile() {
		Menu lMenuFile = new Menu("File");
		MenuItem lMenuItem;

		lMenuItem = new MenuItem("New World", new MenuShortcut(KeyEvent.VK_N));

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				World.novo_mundo(null);
			}
		});

		lMenuFile.add(lMenuItem);

		lMenuItem = new MenuItem("Open World", new MenuShortcut(KeyEvent.VK_O));

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				World.carregar_mundo();
			}
		});

		lMenuFile.add(lMenuItem);

		lMenuItem = new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S));

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				World.salvar();
			}
		});

		lMenuFile.add(lMenuItem);

		lMenuItem = new MenuItem("Save World as a Build");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				World.saveWorldAsBuild();

			}
		});

		lMenuFile.add(lMenuItem);
		return lMenuFile;
	}

	private Menu createMenuImportar() {
		Menu lMenuImport = new Menu("Import");
		MenuItem lMenuItem;

		lMenuItem = new MenuItem("Import Config");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				aFileDialog.setFile(SalvarCarregar.name_file_config);
				aFileDialog.setVisible(true);
				if (Gerador.aFileDialog.getFiles() != null && Gerador.aFileDialog.getFiles().length > 0) {
					if (!Gerador.aFileDialog.getFiles()[0].getName().contentEquals(SalvarCarregar.name_file_config)) {
						JOptionPane.showMessageDialog(null, "arquivo selecionado não bate com o esperado");
						return;
					}
					try {
						Configs lConfig = SalvarCarregar.carregarConfiguracoesMundo(Gerador.aFileDialog.getFiles()[0]);
						if (lConfig != null) {
							JCheckBox lPropriedades = new JCheckBox("Importar Propriedades", true),
									lTransportes = new JCheckBox("Importar Transportes", true),
									lSpritesImportados = new JCheckBox("Sprites Externos", false);
							Object[] message = { lPropriedades, lTransportes, lSpritesImportados };

							int option = JOptionPane.showConfirmDialog(null, message, "Tamanho do mundo",
									JOptionPane.OK_CANCEL_OPTION);

							if (option == JOptionPane.OK_OPTION) {
								if (lPropriedades.isSelected())
									aConfig.importarPropriedades(lConfig.getPropriedades());
								if (lTransportes.isSelected())
									aConfig.importarTransportes(lConfig.getTransportes());
								if (lSpritesImportados.isSelected())
									aConfig.importarSpriteSheetExternos(lConfig.getSpriteSheetExternos());

							}
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Não foi possível carregar essas Configurações");
					}
				}

			}
		});

		lMenuImport.add(lMenuItem);

		lMenuItem = new MenuItem("Import new SpriteSheet");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Gerador.aFileDialog.setFile("*");
				aFileDialog.setVisible(true);
				if (Gerador.aFileDialog.getFiles() != null && Gerador.aFileDialog.getFiles().length > 0) {
					try {
						BufferedImage lBufferedImage = ImageIO.read(Gerador.aFileDialog.getFiles()[0]);
						JTextField lNomeSpriteSheet = new JTextField(Gerador.aFileDialog.getFiles()[0].getName());
						NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
						formatter.setValueClass(Integer.class);
						formatter.setMinimum(0);
						formatter.setMaximum(Integer.MAX_VALUE);
						formatter.setAllowsInvalid(false);
						formatter.setCommitsOnValidEdit(true);
						JFormattedTextField lTamanhoSprite = new JFormattedTextField(formatter),
								lTotalSprites = new JFormattedTextField(formatter);
						lTamanhoSprite.setText("" + TS);
						lTotalSprites.setText(
								"" + (int) ((lBufferedImage.getWidth() / TS) * (lBufferedImage.getHeight() / TS)));

						Object[] message = { "Nome Sprite Sheet: ", lNomeSpriteSheet, "total de Sprites na Imagem:",
								lTotalSprites, "tamanho de cada Sprite (pixels):", lTamanhoSprite };
						boolean lDadosOk = false;
						while (!lDadosOk) {
							int option = JOptionPane.showConfirmDialog(null, message, "Dados SpriteSheet",
									JOptionPane.OK_CANCEL_OPTION);
							if (option == JOptionPane.OK_OPTION) {
								if (World.spritesCarregados.containsKey(lNomeSpriteSheet.getText())) {
									JOptionPane.showMessageDialog(null, "Existe um SpriteSheetImportado com esse nome");
									continue;
								} else {
									File lFile = new File(SalvarCarregar.arquivoLocalSpritesExternos,
											lNomeSpriteSheet.getText());
									if (lFile.exists()) {
										JOptionPane.showMessageDialog(null,
												"Já existe um SpriteSheet externo com esse nome");
										continue;
									} else {
										SalvarCarregar.adicionarImagemExterna(
												new ExSpriteSheet(lNomeSpriteSheet.getText(),
														Integer.parseInt(lTamanhoSprite.getText()),
														Integer.parseInt(lTotalSprites.getText())),
												lFile, lBufferedImage);

										lDadosOk = true;

									}
								}

							} else {
								lDadosOk = true;
							}
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Não foi possível Importar esse SpriteSheet");
					}
				}
			}
		});

		lMenuImport.add(lMenuItem);

		lMenuItem = new MenuItem("Add Imported SpriteSheet");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SalvarCarregar.carregarImagemExterna();
			}
		});

		lMenuImport.add(lMenuItem);

		return lMenuImport;
	}

	private Menu createMenuExportar() {
		Menu lMenuExportar = new Menu("Export");
		MenuItem lMenuItem;

		lMenuItem = new MenuItem("JSON");

		lMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SalvarCarregar.exportarMundoJson();
			}
		});

		lMenuExportar.add(lMenuItem);

		return lMenuExportar;
	}
}
