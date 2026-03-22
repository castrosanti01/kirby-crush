package GUI;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import entidades.Potenciador;
import entidades.Color;
import factories.AbstractFactory;
import factories.KirbyFactory;
import factories.ZeldaFactory;
import logica.EntidadLogica;
import logica.Juego;
import logica.Jugador;
import logica.Objetivo;
import logica.Puntaje;
import logica.TopJugadores;
import manejadorAnimaciones.CentralAnimaciones;

public class Ventana extends JFrame implements VentanaAnimable, VentanaNotificable, Puntaje {
	
	// ===================== LÓGICA DEL JUEGO =====================
	protected Juego mi_juego;
	protected CentralAnimaciones mi_animador;
	protected AbstractFactory Generador;
	
	// ===================== DIMENSIONES Y CONFIGURACIÓN =====================
	private static final int SIZE_LABEL = 60;
	private static final int WINDOW_WIDTH_GAME = 700;
	private static final int WINDOW_HEIGHT_GAME = 700;
	
	// ===================== ESTRUCTURA DE PANELES =====================
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JLayeredPane panelSeleccionTema;
	private JLayeredPane panelSeleccionNiveles;
	private JLayeredPane panelFinal;
	private JLayeredPane panelJuego;
	private JDialog dialogTablaRanking;
	private DefaultTableModel tablaRanking;
	
	// ===================== COMPONENTES DE NAVEGACIÓN =====================
	private JButton botonKirby, botonZelda;
	private JButton botonNivel1, botonNivel2, botonNivel3, botonNivel4, botonNivel5;
	private JButton botonRankingNiveles, botonRankingJuego;
	
	// ===================== COMPONENTES DE VIDAS =====================
	private JLabel vida1, vida2, vida3;
	private boolean quedanVidas = true;
	
	// ===================== COMPONENTES DE INFORMACIÓN =====================
	private JLabel contadorMovimientos;	
	//private JLabel contadorTiempo;
	private JLabel contadorPuntaje;
	private JLabel labelPuntaje;
	private Map<Color, JLabel> contadores;
	private List<Objetivo> objetivos;
	
	// ===================== ANIMACIONES Y TIMERS =====================
	private int animaciones_pendientes;
	private boolean bloquear_intercambios;
	private boolean cartelWin;
	private Timer timerglobal;

	
	// ===================== CONFIGURACIÓN VENTANA =====================
	public Ventana(Juego j) {
		mi_juego = j;
		mi_animador = new CentralAnimaciones(this);
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		
		animaciones_pendientes = 0;
		bloquear_intercambios = false;
		cartelWin = false;
		timerglobal = new Timer(10000, null);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Kirby & Zelda Crush");
		setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/kirbyicon.png")).getImage());
		setResizable(false);
		setSize(WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		setLocationRelativeTo(null);
		setVisible(true);
		setContentPane(mainPanel);
		
		panelSeleccionTema = crearPanelSeleccionTema();
		panelSeleccionNiveles = new JLayeredPane();
		panelJuego = new JLayeredPane();
		panelFinal = new JLayeredPane();
		
		mainPanel.add(panelSeleccionTema, "seleccionTema");
		mainPanel.add(panelSeleccionNiveles, "seleccionNiveles");
		mainPanel.add(panelJuego, "juego");
		mainPanel.add(panelFinal, "final");
		
		cardLayout.show(mainPanel, "seleccionTema");

		//Ranking
		deserializacionRanking();
		dialogTablaRanking = new JDialog(Ventana.this, "RANKING - Top 5", true);
		dialogTablaRanking.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/kirbyicon.png")).getImage());

		//Movimientos con teclado
        panelJuego.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {	
				switch(e.getKeyCode()) {
					case KeyEvent.VK_LEFT: 	{ if (!bloquear_intercambios && !cartelWin) mi_juego.mover_jugador(Juego.IZQUIERDA); break; }
					case KeyEvent.VK_RIGHT: { if (!bloquear_intercambios && !cartelWin) mi_juego.mover_jugador(Juego.DERECHA); break; }
					case KeyEvent.VK_UP: 	{ if (!bloquear_intercambios && !cartelWin) mi_juego.mover_jugador(Juego.ARRIBA);break; }
					case KeyEvent.VK_DOWN: 	{ if (!bloquear_intercambios && !cartelWin) mi_juego.mover_jugador(Juego.ABAJO); break; }
					case KeyEvent.VK_W:		{ if (!bloquear_intercambios && !cartelWin) mi_juego.intercambiar_entidades(Juego.ARRIBA); break; }
					case KeyEvent.VK_S:		{ if (!bloquear_intercambios && !cartelWin) mi_juego.intercambiar_entidades(Juego.ABAJO); break; }
					case KeyEvent.VK_A:		{ if (!bloquear_intercambios && !cartelWin) mi_juego.intercambiar_entidades(Juego.IZQUIERDA); break; }
					case KeyEvent.VK_D:		{ if (!bloquear_intercambios && !cartelWin) mi_juego.intercambiar_entidades(Juego.DERECHA); break; } 
				}
			}
		});		
	}
	
	// ===================== PANEL DE SELECCIÓN DE TEMA =====================
	private JLayeredPane crearPanelSeleccionTema() {
		JLayeredPane panel = new JLayeredPane();
		Icon imgIcon = new ImageIcon(this.getClass().getResource("/imagenes/niveles/SeleccionTema.png"));
		JLabel fondo = new JLabel(imgIcon);
		fondo.setBounds(0, 0, WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		panel.add(fondo, 0);
		
		botonKirby = new JButton();
		Icon iconKirby = cargarIcono("/imagenes/niveles/Kirby/BotonKirby.png");
		botonKirby.setIcon(iconKirby);
		botonKirby.setBounds(100, 550, 130, 40);
		botonKirby.setOpaque(false);
		botonKirby.setContentAreaFilled(false);
		panel.add(botonKirby, 0);
		
		botonKirby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Generador = new KirbyFactory();
				mi_juego.setGenerador(Generador);
				setTitle(Generador.toString() + " Crush");
				cargarMusica();
				
				prepararPanelSeleccionNiveles();
				cardLayout.show(mainPanel, "seleccionNiveles");
			}
		});
		
		botonZelda = new JButton();
		Icon iconZelda = cargarIcono("/imagenes/niveles/Zelda/BotonZelda.png");
		botonZelda.setIcon(iconZelda);
		botonZelda.setBounds(465, 550, 130, 40);
		botonZelda.setOpaque(false);
		botonZelda.setContentAreaFilled(false);
		panel.add(botonZelda, 0);
		
		botonZelda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Generador = new ZeldaFactory();
				mi_juego.setGenerador(Generador);
				setTitle(Generador.toString() + " Crush");
				setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/zeldaicon.png")).getImage());
				cargarMusica();
				
				prepararPanelSeleccionNiveles();
				cardLayout.show(mainPanel, "seleccionNiveles");
			}
		});
		return panel;
	}

	// ===================== PANEL DE SELECCIÓN DE NIVELES =====================
	private void prepararPanelSeleccionNiveles() {
		Icon imgIcon = cargarIcono("/imagenes/niveles/"+Generador.toString()+"/PanelSeleccion.png");
		JLabel fondo = new JLabel(imgIcon);
		fondo.setBounds(0, 0, WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		panelSeleccionNiveles.add(fondo, 0);

		Icon iconVidas = cargarIcono("/imagenes/niveles/vidas.png");
		vida1 = new JLabel(iconVidas);
		vida1.setBounds(265, 360, 75, 75);
		panelSeleccionNiveles.add(vida1, 0);
		vida2 = new JLabel(iconVidas);
		vida2.setBounds(315, 360, 75, 75);
		panelSeleccionNiveles.add(vida2, 0);
		vida3 = new JLabel(iconVidas);
		vida3.setBounds(365, 360, 75, 75);
		panelSeleccionNiveles.add(vida3, 0);

		botonRankingNiveles = new JButton();
		Icon iconRanking = cargarIcono("/imagenes/niveles/"+Generador.toString()+"/BotonRanking.png");
		botonRankingNiveles.setIcon(iconRanking);
        botonRankingNiveles.setBounds(260, 575, 180, 70);
        botonRankingNiveles.setOpaque(false);
		botonRankingNiveles.setBorderPainted(false);
		botonRankingNiveles.setContentAreaFilled(false);
		panelSeleccionNiveles.add(botonRankingNiveles, 0);
		botonRankingNiveles.addActionListener(e -> mostrarRanking());

		agregarBotonesNiveles();
	}
	
	private void agregarBotonesNiveles() {
		botonNivel1 = crearBotonNivel(Generador.toString()+"/BotonNivel1.png", 180, 180, 100, 100);
		panelSeleccionNiveles.add(botonNivel1, 0);
		botonNivel1.addActionListener(e -> iniciarNivel(1));
		
		botonNivel2 = crearBotonNivel(Generador.toString()+"/BotonNivelConCandado.png", 305, 180, 100, 100);
		panelSeleccionNiveles.add(botonNivel2, 0);
		
		botonNivel3 = crearBotonNivel(Generador.toString()+"/BotonNivelConCandado.png", 425, 180, 100, 100);
		panelSeleccionNiveles.add(botonNivel3, 0);
		
		botonNivel4 = crearBotonNivel(Generador.toString()+"/BotonNivelConCandado.png", 245, 260, 100, 100);
		panelSeleccionNiveles.add(botonNivel4, 0);

		botonNivel5 = crearBotonNivel(Generador.toString()+"/BotonNivelConCandado.png", 365, 260, 100, 100);
		panelSeleccionNiveles.add(botonNivel5, 0);
	}

	private JButton crearBotonNivel(String imagenNombre, int x, int y, int ancho, int alto) {
		JButton boton = new JButton();
		Icon icon = cargarIcono("/imagenes/niveles/"+imagenNombre);
		boton.setIcon(icon);
		boton.setBounds(x, y, ancho, alto);
		boton.setOpaque(false);
		boton.setBorderPainted(false);
		boton.setContentAreaFilled(false);
		return boton;
	}
	
	// ===================== PANEL JUEGO =====================
	private void iniciarNivel(int nivel) {
		cardLayout.show(mainPanel, "juego");

		mi_juego.cargarDatos(nivel, Generador);
		mi_juego.asociar();
		mi_juego.set_movimientos();
		
		Icon fondoNiveles = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/PanelNivel.png"));
		JLabel labelFondoNiveles = new JLabel(fondoNiveles);
		labelFondoNiveles.setBounds(0, 0, WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		panelJuego.add(labelFondoNiveles, JLayeredPane.DEFAULT_LAYER);
		
        cargar_objetivos();
        botonRankingJuego = new JButton();
        Icon iconRankingJuego = cargarIcono("/imagenes/niveles/"+Generador.toString()+"/BotonRanking.png");
        botonRankingJuego.setIcon(iconRankingJuego);
        botonRankingJuego.setBounds(500, 7, 165, 50);
        botonRankingJuego.setOpaque(false);
        botonRankingJuego.setBorderPainted(false);
        botonRankingJuego.setContentAreaFilled(false);
        botonRankingJuego.addActionListener(e -> mostrarRanking());
		panelJuego.add(botonRankingJuego, 0);
		
        labelPuntaje = new JLabel("Puntaje: ");
        labelPuntaje.setFont(new Font("Monospaced", Font.BOLD, 50));
        labelPuntaje.setBounds(70, 540, 500, 70);
        panelJuego.add(labelPuntaje, 0);

		int puntaje = mi_juego.get_jugador_actual().get_puntaje_acumulado();
		contadorPuntaje = new JLabel(String.valueOf(puntaje));
		contadorPuntaje.setFont(new Font("Monospaced", Font.BOLD, 50));
		contadorPuntaje.setBounds(340, 540, 500, 70);
		panelJuego.add(contadorPuntaje, 0);

		int movimientos = mi_juego.get_movimientos();
        contadorMovimientos = new JLabel("Movimientos: "+movimientos);
        contadorMovimientos.setFont(new Font("Monospaced", Font.BOLD, 50));
        contadorMovimientos.setBounds(70, 590, 500, 70);
        panelJuego.add(contadorMovimientos, 0);
        
		panelJuego.setLayout(null);
		panelJuego.setVisible(true);
        panelJuego.setFocusable(true);
		panelJuego.revalidate();
		panelJuego.repaint();
		panelJuego.requestFocusInWindow();
	}		

	private void cargar_objetivos() {
		contadores = new HashMap<>();
		objetivos = mi_juego.actualizar_manager_objetivos();

		int filaAux = 96;

		for (Objetivo objetivo : objetivos) {
			if (objetivo.get_color() != null) {

				JLabel contador = new JLabel(String.valueOf(objetivo.get_cantidad()));
				contador.setFont(new Font("MONOSPACED", Font.BOLD, 58));
				contador.setBounds(615, filaAux - 5, 110, 70);
				panelJuego.add(contador, 0);
				contadores.put(objetivo.get_color(), contador);

				JLabel caramelo = new JLabel();
				caramelo.setIcon(new ImageIcon(	this.getClass().getResource(objetivo.get_image_path())));
				Icon icon = caramelo.getIcon();
				if (icon != null) {
					int width = icon.getIconWidth();
					int height = icon.getIconHeight();
					double scaleFactor = Math.min((double) SIZE_LABEL / width, (double) SIZE_LABEL / height);
					int newWidth = (int) (width * scaleFactor);
					int newHeight = (int) (height * scaleFactor);
					caramelo.setIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
				}
				caramelo.setBounds(550, filaAux, 60, 60);
				panelJuego.add(caramelo, 0);

				filaAux += 60;
			}
    	}
	}
	
	public void actualizar_objetivos(List<Objetivo> lista) {
		for (int i = 0; i < lista.size(); i++) {
			Objetivo objetivo = lista.get(i);
			animar_cambio_objetivo(contadores.get(objetivo.get_color()), objetivo.get_cantidad());
		}		
	}

	public void actualizar_puntaje(int puntaje_a_sumar) {
		animar_cambio_objetivo(contadorPuntaje, (mi_juego.get_jugador_actual().get_puntaje_acumulado() + mi_juego.get_jugador_actual().get_puntaje_nivel_actual()));	
	}

	// ===================== GANAR NIVEL =====================
	public void ganarNivel() {
		cartelWin = true;
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	            Thread.sleep(2000); 
	            return null;
	        }

	        @Override
	        protected void done() {
				mi_juego.sumarPuntos();

	            JLabel victoria = new JLabel();
	            victoria.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/PanelVictoria.gif")));
	            victoria.setBounds(panelJuego.getWidth() / 2 - 150, panelJuego.getHeight() / 2 - 150, 300, 300);
	            victoria.setVisible(true);

	            JButton levelUp = new JButton();
	            levelUp.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/BotonLevelUp.png")));
	            levelUp.setBounds(panelJuego.getWidth() / 2 - 150, panelJuego.getHeight() / 2 - 150, 300, 300);
	            levelUp.setOpaque(false);
	            levelUp.setBorderPainted(false);
	            levelUp.setContentAreaFilled(false);
	            
	            levelUp.addActionListener(new ActionListener() {
	                public void actionPerformed (ActionEvent e) {
	                	limpiarEntidades();
						cartelWin = false;
						cardLayout.show(mainPanel, "seleccionNiveles");
	                	bloquearNivelesAnteriores();

	                	switch(mi_juego.nivelActual()) {
	                		case 1:
	                			botonNivel2.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/BotonNivel2.png")));
	                			configuraBotonNivel(botonNivel2, 2);
	                			break;
	                		case 2:
	                			botonNivel3.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/BotonNivel3.png")));
	                			configuraBotonNivel(botonNivel3, 3);
	                			break;
	                		case 3:
	                			botonNivel4.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/BotonNivel4.png")));
	                			configuraBotonNivel(botonNivel4, 4);
	                			break;
	                		case 4:
	                			botonNivel5.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/BotonNivel5.png")));
	                			configuraBotonNivel(botonNivel5, 5);
	                			break;
	                		case 5:
	                			terminarJuego();
	                			break;
	                	}
	                }
	            });

				animar_ganar_nivel(panelJuego, victoria, levelUp);

	        }
	    };
	    worker.execute();
	}

	private void configuraBotonNivel(JButton boton, int nivel) {
		ActionListener[] listeners = boton.getActionListeners();
		if (listeners.length > 0) 
			boton.removeActionListener(listeners[0]);
		
		boton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				iniciarNivel(nivel);
			}
		});
	}

	// ===================== GAME OVER =====================
	private void mostrarGameOver() {
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        @Override
	        protected Void doInBackground() {
	            try {
	                Thread.sleep(2000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            return null;
	        }

	        @Override
	        protected void done() {
				perderVida();

	            JLabel gameOver = new JLabel(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/PanelGameOver.png")));
	            gameOver.setBounds(0, 0, 500, 500);
	            
	            JButton retry = new JButton(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/BotonRetry.png")));
	            retry.setBounds(160, 250, 175, 40);
	            retry.setOpaque(false);
	            retry.setBorderPainted(false);
	            retry.setContentAreaFilled(false);
	            
	            JButton exit = new JButton(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/BotonExit.png")));
	            exit.setBounds(160, 290, 175, 40);
	            exit.setOpaque(false);
	            exit.setBorderPainted(false);
	            exit.setContentAreaFilled(false);
	            
	            retry.addActionListener(new ActionListener() {
	                public void actionPerformed (ActionEvent e) {
						limpiarEntidades();

	                    if (quedanVidas) 
	                        iniciarNivel(mi_juego.nivelActual());
	                    else {                     
	                        cardLayout.show(mainPanel, "seleccionNiveles");
	                        bloquearTodosLosNiveles();
	                        setNombreJugador();
	                    }
	                }
	            });
	    		
	    		exit.addActionListener(new ActionListener() {
	    		    public void actionPerformed (ActionEvent e) {
						limpiarEntidades();
	    		    	
						if (quedanVidas) 
	                        cardLayout.show(mainPanel, "seleccionNiveles");
	                    else {                     
	                        cardLayout.show(mainPanel, "seleccionNiveles");
	                        bloquearTodosLosNiveles();
	                        setNombreJugador();
	                    }
	    		    }
	    		});

				animar_perder_nivel(panelJuego, gameOver, retry, exit);

	        }
	    };

	    worker.execute();
	}	
	
	private void perderVida() {
	    if (vida3.isVisible()) {
	        vida3.setVisible(false);
	    } else if (vida2.isVisible()) {
	        vida2.setVisible(false);
	    } else if (vida1.isVisible()) {
	        vida1.setVisible(false);
	        quedanVidas = false;
	    }
	}
	
	private void limpiarEntidades() {
	    panelJuego.removeAll();
	    panelJuego.revalidate();
	    panelJuego.repaint();
	}

	private void bloquearNivelesAnteriores() {
		switch(mi_juego.nivelActual()) {
			case 4:
				botonNivel4.setEnabled(false);
			case 3:
				botonNivel3.setEnabled(false);
			case 2:
				botonNivel2.setEnabled(false);
			case 1:
				botonNivel1.setEnabled(false);
				break;
		}
	}
	
	private void bloquearTodosLosNiveles() {
		botonNivel1.setEnabled(false);
		botonNivel2.setEnabled(false);
		botonNivel3.setEnabled(false);
		botonNivel4.setEnabled(false);
		botonNivel5.setEnabled(false);
	}

	// ===================== PANEL FINAL =====================
	public void terminarJuego() {
		limpiarEntidades();
		cardLayout.show(mainPanel, "final");

		JLabel kirbylink = new JLabel();
		kirbylink.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"/PanelFinal.png")));
		kirbylink.setBounds(0, 0, WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		panelFinal.add(kirbylink, JLayeredPane.DEFAULT_LAYER);
		
		panelFinal.revalidate();
		panelFinal.repaint();
		
		setNombreJugador();
	}

	// ===================== RANKING =====================
	private void mostrarRanking() {
		if (timerglobal != null) 
			timerglobal.stop();
        tablaPuntajes();
        dialogTablaRanking.setVisible(true);
        panelJuego.requestFocusInWindow();
        if (timerglobal != null) 
			timerglobal.start();
	}

	private void tablaPuntajes() {
	    dialogTablaRanking.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialogTablaRanking.setSize(300, 150);
	    dialogTablaRanking.setLocationRelativeTo(null);

	    if (tablaRanking == null) {
	        tablaRanking = new DefaultTableModel();
	        tablaRanking.addColumn("JUGADOR");
	        tablaRanking.addColumn("PUNTAJE");
	    }
	    
		//Actualizar la tabla
        List<Jugador> jugadores = mi_juego.getTopJugadores().get_ranking_ordenado();
        tablaRanking.setRowCount(0);
		
        for (Jugador jugador : jugadores) {
            tablaRanking.addRow(new Object[]{jugador.get_jugador(), jugador.get_puntaje_acumulado()});
        }
        
	    JTable tabla = new JTable(tablaRanking);
	    tabla.setDefaultEditor(Object.class, null);
	    tabla.getTableHeader().setReorderingAllowed(false);
	    JScrollPane scrollPane = new JScrollPane(tabla);
	    dialogTablaRanking.add(scrollPane);
	}
	
	private void setNombreJugador() {
		JDialog frameNombreJugador = new JDialog(Ventana.this, "Jugador", true);
	    frameNombreJugador.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    frameNombreJugador.setSize(350, 180);
	    frameNombreJugador.setLocationRelativeTo(null);
	    frameNombreJugador.setResizable(false);
	    frameNombreJugador.setLayout(new BoxLayout(frameNombreJugador.getContentPane(), BoxLayout.Y_AXIS));
		frameNombreJugador.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/kirbyicon.png")).getImage());

	    JPanel panelNombre = new JPanel();
	    panelNombre.setLayout(new BoxLayout(panelNombre, BoxLayout.Y_AXIS));
	    panelNombre.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));

	    JLabel etiquetaNombre = new JLabel("Fin de la partida, ingrese su nombre:");
	    etiquetaNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
	    etiquetaNombre.setFont(new Font("Arial", Font.BOLD, 12));

	    JTextField nombreTextField = new JTextField(20);
	    nombreTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
	    nombreTextField.setMaximumSize(new java.awt.Dimension(250, 30));
	    nombreTextField.setFont(new Font("Arial", Font.PLAIN, 14));

	    JButton botonAceptar = new JButton("ACEPTAR");
	    botonAceptar.setAlignmentX(Component.CENTER_ALIGNMENT);
	    botonAceptar.setMaximumSize(new java.awt.Dimension(100, 40));
	    botonAceptar.setFont(new Font("Arial", Font.BOLD, 13));
	    botonAceptar.setFocusPainted(false);
	    
	    botonAceptar.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String nombre = nombreTextField.getText().trim();
	            if (nombre != null && !nombre.isEmpty()) {
	            	mi_juego.get_jugador_actual().set_nombre(nombre);
	            	mi_juego.getTopJugadores().agregar_jugador(mi_juego.get_jugador_actual());
	                frameNombreJugador.dispose(); 
					serializacionRanking();
					mostrarRanking();			        
	            } else {
	                JOptionPane.showMessageDialog(frameNombreJugador, "Por favor, ingrese nombre de jugador.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
	            }
	        }
	    });

	    panelNombre.add(etiquetaNombre);
	    panelNombre.add(javax.swing.Box.createVerticalStrut(15));
	    panelNombre.add(nombreTextField);
	    panelNombre.add(javax.swing.Box.createVerticalStrut(15));
	    panelNombre.add(botonAceptar);
	    frameNombreJugador.add(panelNombre);
	    frameNombreJugador.setVisible(true);
	    nombreTextField.requestFocusInWindow();
	}
	
	private void serializacionRanking() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("./src/puntaje/ranking.tdp");
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		    objectOutputStream.writeObject(mi_juego.getTopJugadores());
		    objectOutputStream.flush();
		    objectOutputStream.close();
		}
		catch(FileNotFoundException e) {
			//No va nada acá porque si no encuentra el archivo lo crea
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void deserializacionRanking() {
		try {
			FileInputStream fileInputStream = new FileInputStream("./src/puntaje/ranking.tdp");
		    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		    mi_juego.set_top_jugadores((TopJugadores) objectInputStream.readObject());
		    objectInputStream.close();
		}
		catch(FileNotFoundException e) {
			//No va nada acá porque si no encuentra el archivo lo crea
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// ===================== MUSICA =====================
	private void cargarMusica() {
		try {
			java.net.URL url = getClass().getResource("/musica/" + Generador.toString() + "-music.wav");
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			Clip musica = AudioSystem.getClip();
			musica.open(audioInputStream);
			musica.start();
			musica.loop(Clip.LOOP_CONTINUOUSLY); // Reproducir en bucle
			FloatControl controlVolumen = (FloatControl) musica.getControl(FloatControl.Type.MASTER_GAIN);
			controlVolumen.setValue(controlVolumen.getValue() - 28.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ===================== LOGICA GUI =====================
	public AbstractFactory getGenerador() {
		return Generador;
	}
	
	public EntidadGrafica agregar_entidad(EntidadLogica e) {
		Celda celda = new Celda(this, e, SIZE_LABEL);
		panelJuego.add(celda, 0);
		return celda;
	}
	
	public EntidadGrafica agregar_entidad_nueva(EntidadLogica e, int fila) {
		//Caramelos que caen
		Celda celda = new Celda(this, e, SIZE_LABEL);
		celda.setBounds((e.get_columna()+1)*SIZE_LABEL, (fila)*SIZE_LABEL, SIZE_LABEL, SIZE_LABEL);
		panelJuego.add(celda, 0);
		return celda;
	}
	
	public EntidadGrafica agregar_entidad_nueva(Potenciador p) {
		//Potenciadores que aparecen luego de la detonacion
		Celda celda = new Celda(this, p, SIZE_LABEL);
		panelJuego.add(celda, 0);
		celda.imagen_vacia();
		animar_creacion_con_delay(celda);
		return celda;
	}

	public void eliminar_celda(Celda celda) {
		panelJuego.remove(celda);
		panelJuego.repaint();
	}

	
	private Icon cargarIcono(String ruta) {
		try {
			return new ImageIcon(this.getClass().getResource(ruta));
		} catch (Exception e) {
			return null;
		}
	}

	// ===================== NOTIFICACIONES =====================
	public void notificar_perder() {
		mostrarGameOver();
	}

	public void notificar_movimiento(int movimientos) {
		contadorMovimientos.setText("Movimientos: " + movimientos);
	}
	
	public void notificarse_animacion_en_progreso() {
		synchronized(this){
			animaciones_pendientes ++;
			bloquear_intercambios = true;
		}
	}
	
	public void notificarse_animacion_finalizada() {
		synchronized(this){
			animaciones_pendientes --;
			bloquear_intercambios = animaciones_pendientes > 0;
		}
	}
	
	// ===================== ANIMACIONES =====================
	public void animar_movimiento(Celda c) {
	    mi_animador.animar_intercambio(c);
	}

	public void animar_cambio_estado(Celda c) {
	    mi_animador.animar_cambio_foco(c);
	}

	public void animar_detonacion(Celda c) {
	    mi_animador.animar_detonacion(c);
	}

	public void animar_gravedad(Celda c) {
	    mi_animador.animar_gravedad(c);
	}
	
	public void animar_caida(Celda celda) {
		mi_animador.animar_caida(celda);
	}
	
	public void animar_creacion_con_delay(Celda celda) {
		mi_animador.animar_creacion_con_delay(celda);
	}

	public void animar_cambio_objetivo(JLabel contador, int i){
		mi_animador.animar_cambio_objetivo(contador, i);
	}

	public void animar_ganar_nivel(JLayeredPane panel, JLabel label, JButton boton) {
		mi_animador.animar_ganar_nivel(panel, label, boton);
	}

	public void animar_perder_nivel(JLayeredPane panel, JLabel label, JButton boton, JButton exit) {
		mi_animador.animar_perder_nivel(panel, label, boton, exit);
	}
	
}