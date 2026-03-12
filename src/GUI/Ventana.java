package GUI;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
	
	protected int filas;
	protected int columnas;
	
	// ===================== ESTRUCTURA DE PANELES =====================
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JLayeredPane panelSeleccionTema;
	private JLayeredPane panelSeleccionNiveles;
	private JLayeredPane panelFinal;
	protected JLayeredPane panelPrincipal;
	
	// ===================== COMPONENTES DE NAVEGACIÓN =====================
	private JComboBox<AbstractFactory> comboBox;
	private JButton botonNivel1, botonNivel2, botonNivel3, botonNivel4, botonNivel5;
	protected JButton botonRanking;
	
	// ===================== COMPONENTES DE VIDAS =====================
	private JLabel vida1, vida2, vida3;
	private boolean quedanVidas = true;
	
	// ===================== COMPONENTES DE INFORMACIÓN =====================
	public JLabel contadorMovimientos;	
	public JLabel contadorTiempo;
	public JLabel contadorPuntaje;
	private List<JLabel> contadores;
	protected List<Objetivo> objetivos;
	
	// ===================== COMPONENTES NO USADOS (LEGACY) =====================
	public JLabel contadorVerdes;
	public JLabel contadorAmarillos;
	public JLabel contadorAzules;
	public JLabel contadorVioletas;
	public JLabel contadorRosas;
	public JLabel contadorGelatina;
	public JLabel contadorGlaseados;
	public JLabel objetivo;
	protected JLabel texto_superior;
	
	// ===================== ANIMACIONES Y TIMERS =====================
	protected int animaciones_pendientes;
	protected boolean bloquear_intercambios;
	private Timer timerglobal;
	
	// ===================== DIÁLOGOS Y FRAMES SECUNDARIOS =====================
	protected JTextField nombreTextField;
	protected JFrame frameNombreJugador;
	protected JDialog dialogTablaRanking;
	protected JFrame frameTablaRanking;
	protected DefaultTableModel tablaRanking;
	protected JFrame frameDeNiveles;
	
	private JPanel panelNombreJugador;
	
	// ===================== CONFIGURACIÓN VENTANA =====================
	public Ventana(Juego j) {
		mi_juego = j;
		mi_animador = new CentralAnimaciones(this);
		
		animaciones_pendientes = 0;
		bloquear_intercambios = false;
		timerglobal = new Timer(10000, null);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Kirby & Zelda Crush");
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/kirbyicon.png")).getImage());
		
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		setContentPane(mainPanel);
		
		panelSeleccionTema = crearPanelSeleccionTema();
		panelSeleccionNiveles = new JLayeredPane();
		panelPrincipal = new JLayeredPane();
		panelNombreJugador = new JPanel();
		panelFinal = new JLayeredPane();
		
		mainPanel.add(panelSeleccionTema, "seleccionTema");
		mainPanel.add(panelSeleccionNiveles, "seleccionNiveles");
		mainPanel.add(panelPrincipal, "juego");
		mainPanel.add(panelNombreJugador, "nombreJugador");
		mainPanel.add(panelFinal, "final");
		
		setSize(WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		setLocationRelativeTo(null);
		setVisible(true);
		
		cardLayout.show(mainPanel, "seleccionTema");
	}
	
	// ===================== PANEL DE SELECCIÓN DE NIVELES =====================
	private JLayeredPane crearPanelSeleccionTema() {
		JLayeredPane panel = new JLayeredPane();
		panel.setPreferredSize(new Dimension(WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME));
		
		Icon imgIcon = new ImageIcon(this.getClass().getResource("/imagenes/niveles/FondoProyecto2.png"));
		JLabel fondo = new JLabel(imgIcon);
		fondo.setBounds(0, 0, WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		panel.add(fondo, 0);
		
		AbstractFactory[] factories = new AbstractFactory[2];
		factories[0] = new KirbyFactory();
		factories[1] = new ZeldaFactory();
		comboBox = new JComboBox<AbstractFactory>(factories);
		comboBox.setBounds(285, 537, 132, 40);
		panel.add(comboBox, 0);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Generador = (AbstractFactory)comboBox.getSelectedItem();
				setTitle(Generador.toString() + " Crush");
				
				prepararPanelSeleccionNiveles();
				
				dialogTablaRanking = new JDialog(Ventana.this, "RANKING - Top 5", true);
				dialogTablaRanking.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/"+Generador.toString()+"icon.png")).getImage());
				
				deserializacionRanking();
				
				cardLayout.show(mainPanel, "seleccionNiveles");
			}
		});
		return panel;
	}

	private void prepararPanelSeleccionNiveles() {
		panelSeleccionNiveles.removeAll();
		panelSeleccionNiveles.setPreferredSize(new Dimension(WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME));
		
		agregarFondoAlPanelNiveles();
		agregarVidas();
		agregarBotonRanking();
		agregarBotonesNiveles();
		
		panelSeleccionNiveles.revalidate();
		panelSeleccionNiveles.repaint();
	}

	private void agregarFondoAlPanelNiveles() {
		Icon imgIcon2 = cargarIcono("/imagenes/niveles/"+Generador.toString()+"PN.jpg");
		if (imgIcon2 != null) {
			JLabel fondo2 = new JLabel(imgIcon2);
			fondo2.setBounds(0, 0, WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
			panelSeleccionNiveles.add(fondo2, 0);
		}
	}
	
	private void agregarVidas() {
		Icon iconVidas = cargarIcono("/imagenes/niveles/vidas.png");
		if (iconVidas != null) {
			vida1 = new JLabel(iconVidas);
			vida1.setBounds(280, 10, 50, 50);
			panelSeleccionNiveles.add(vida1, 0);
			
			vida2 = new JLabel(iconVidas);
			vida2.setBounds(320, 10, 50, 50);
			panelSeleccionNiveles.add(vida2, 0);
			
			vida3 = new JLabel(iconVidas);
			vida3.setBounds(360, 10, 50, 50);
			panelSeleccionNiveles.add(vida3, 0);
		}
	}
	
	private void agregarBotonRanking() {
		botonRanking = new JButton();
		Icon iconRanking = cargarIcono("/imagenes/niveles/"+Generador.toString()+"Ranking.png");
		if (iconRanking != null) {
			botonRanking.setIcon(iconRanking);
		}
        botonRanking.setBounds(260, 519, 180, 70);
        configurarBoton(botonRanking);
		panelSeleccionNiveles.add(botonRanking, 0);
		
		botonRanking.addActionListener(e -> mostrarRanking());
	}

	private void mostrarRanking() {
		if (timerglobal != null) timerglobal.stop();
        tablaPuntajes();
        dialogTablaRanking.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogTablaRanking.setVisible(true);
        panelPrincipal.requestFocusInWindow();
        if (timerglobal != null) timerglobal.start();
	}

	private void agregarBotonesNiveles() {
		botonNivel1 = crearBotonNivel(Generador.toString()+"BotonNivel1.png", 175, 300, 100, 100);
		agregarBotonAlPanel(botonNivel1);
		botonNivel1.addActionListener(e -> iniciarNivel());
		
		botonNivel2 = crearBotonNivel(Generador.toString()+"BotonNivelConCandado.png", 300, 300, 100, 100);
		agregarBotonAlPanel(botonNivel2);
		
		botonNivel3 = crearBotonNivel(Generador.toString()+"BotonNivelConCandado.png", 425, 300, 100, 100);
		agregarBotonAlPanel(botonNivel3);
		
		botonNivel4 = crearBotonNivel(Generador.toString()+"BotonNivelConCandado.png", 240, 400, 100, 100);
		agregarBotonAlPanel(botonNivel4);
		
		botonNivel5 = crearBotonNivel(Generador.toString()+"BotonNivelConCandado.png", 365, 400, 100, 100);
		agregarBotonAlPanel(botonNivel5);
	}

	private void agregarBotonAlPanel(JButton boton) {
		panelSeleccionNiveles.add(boton, 0);
	}
	
	private JButton crearBotonNivel(String imagenNombre, int x, int y, int ancho, int alto) {
		JButton boton = new JButton();
		Icon icon = cargarIcono("/imagenes/niveles/"+imagenNombre);
		if (icon != null) {
			boton.setIcon(icon);
		}
		boton.setBounds(x, y, ancho, alto);
		configurarBoton(boton);
		return boton;
	}
	
	private void configurarBoton(JButton boton) {
		boton.setOpaque(false);
		boton.setBorderPainted(false);
		boton.setContentAreaFilled(false);
	}
	
	// ===================== PANEL JUEGO =====================
	private void iniciarNivel() {
		panelSeleccionNiveles.removeAll();
		setSize(WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		setLocationRelativeTo(null);

		mi_juego.cargarDatos(Generador);
		filas = mi_juego.getTablero().get_filas();
		columnas = mi_juego.getTablero().get_columnas();
		mi_juego.asociar();
		mi_juego.setGenerador(Generador);
		
		Icon fondoNiveles = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"FondoNiveles.png"));
		JLabel labelFondoNiveles = new JLabel(fondoNiveles);
		labelFondoNiveles.setBounds(0, 0, WINDOW_WIDTH_GAME, WINDOW_HEIGHT_GAME);
		
		panelPrincipal.setSize(SIZE_LABEL * filas, SIZE_LABEL * columnas);
		panelPrincipal.add(labelFondoNiveles, JLayeredPane.DEFAULT_LAYER);
		panelPrincipal.setLayout(null);
		panelPrincipal.setVisible(true);
		
		mi_juego.set_movimientos();
		int movimientos = mi_juego.get_movimientos();
        contadorMovimientos = new JLabel("Movimientos: "+movimientos);
        contadorMovimientos.setFont(new Font("Ink Free", Font.BOLD, 50));
        contadorMovimientos.setBounds(70, 586, 380, 70);
        panelPrincipal.add(contadorMovimientos, 0);
	
        cargar_objetivos();
        
        botonRanking.setBounds(480, 28, 180, 70);
		panelPrincipal.add(botonRanking, 0);
		
		int puntaje = mi_juego.get_jugador_actual().get_puntaje_acumulado();
        contadorPuntaje = new JLabel("Puntaje: "+puntaje);
        contadorPuntaje.setFont(new Font("Ink Free", Font.BOLD, 50));
        contadorPuntaje.setBounds(70, 520, 380, 70);
        panelPrincipal.add(contadorPuntaje, 0);
        
        panelPrincipal.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {	
				switch(e.getKeyCode()) {
					case KeyEvent.VK_LEFT: 	{ if (!bloquear_intercambios) mi_juego.mover_jugador(Juego.IZQUIERDA); break; }
					case KeyEvent.VK_RIGHT: { if (!bloquear_intercambios) mi_juego.mover_jugador(Juego.DERECHA); break; }
					case KeyEvent.VK_UP: 	{ if (!bloquear_intercambios) mi_juego.mover_jugador(Juego.ARRIBA);break; }
					case KeyEvent.VK_DOWN: 	{ if (!bloquear_intercambios) mi_juego.mover_jugador(Juego.ABAJO); break; }
					case KeyEvent.VK_W:		{ if (!bloquear_intercambios) mi_juego.intercambiar_entidades(Juego.ARRIBA); break; }
					case KeyEvent.VK_S:		{ if (!bloquear_intercambios) mi_juego.intercambiar_entidades(Juego.ABAJO); break; }
					case KeyEvent.VK_A:		{ if (!bloquear_intercambios) mi_juego.intercambiar_entidades(Juego.IZQUIERDA); break; }
					case KeyEvent.VK_D:		{ if (!bloquear_intercambios) mi_juego.intercambiar_entidades(Juego.DERECHA); break; } 
				}
			}
		});
        
        panelPrincipal.setFocusable(true);
		panelPrincipal.revalidate();
		panelPrincipal.repaint();
		
		cardLayout.show(mainPanel, "juego");
		panelPrincipal.requestFocusInWindow();
	}		

	public void mostrarGameOver() {
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        @Override
	        protected Void doInBackground() {
	            try {
	                Thread.sleep(100);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            return null;
	        }

	        @Override
	        protected void done() {
	            JLabel gameOver = new JLabel(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"GameOverPane.png")));
	            gameOver.setBounds(0, 0, 500, 500);
	            
	            JButton retry = new JButton(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"RetryButton.png")));
	            retry.setBounds(160, 250, 175, 40);
	            retry.setOpaque(false);
	            retry.setBorderPainted(false);
	            retry.setContentAreaFilled(false);
	            
	            JButton exit = new JButton(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"ExitButton.png")));
	            exit.setBounds(160, 290, 175, 40);
	            exit.setOpaque(false);
	            exit.setBorderPainted(false);
	            exit.setContentAreaFilled(false);
	            
	            panelPrincipal.add(gameOver, 0);
	            panelPrincipal.add(retry, 0);
	            panelPrincipal.add(exit, 0);
	            panelPrincipal.setFocusable(false);
	            panelPrincipal.revalidate();
	            panelPrincipal.repaint();
	            
	            retry.addActionListener(new ActionListener() {
	                public void actionPerformed (ActionEvent e) {
	                    perderVida();
	                    
	                    if (quedanVidas) {
	                        limpiarEntidades();
	                        mi_juego.reiniciarNivel();
	                        gameOver.setVisible(false);
	                        exit.setVisible(false);
	                        retry.setVisible(false);
	                        
	                        botonRanking.setBounds(480, 28, 180, 70);
	                        panelPrincipal.add(botonRanking, 0);
	                        
	                        mi_juego.set_movimientos();		    
	                    } else {                     
	                        volverAlPanelNiveles();
	                        limpiarEntidades();
	                        bloquearTodosLosNiveles();
	                        gameOver.setVisible(false);
	                        exit.setVisible(false);
	                        retry.setVisible(false);
	                        panelSetNombreJugador();
	                    }
	                }
	            });
	    		
	    		exit.addActionListener(new ActionListener() {
	    		    public void actionPerformed (ActionEvent e) {
	    		    	System.exit(0);
	    		    }
	    		});
	        }
	    };

	    worker.execute();
	}	protected void perderVida() {
	    if (vida3.isVisible()) {
	        vida3.setVisible(false);
	    } else if (vida2.isVisible()) {
	        vida2.setVisible(false);
	    } else if (vida1.isVisible()) {
	        vida1.setVisible(false);
	        quedanVidas = false;
	    }
	}
	
	public void ganarNivel() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	            Thread.sleep(500); 
	            return null;
	        }

	        @Override
	        protected void done() {
	            JLabel victoria = new JLabel();
	            victoria.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"-dance.gif")));
	            victoria.setBounds(panelPrincipal.getWidth() / 2 - 150, panelPrincipal.getHeight() / 2 - 150, 300, 300);
	            victoria.setVisible(true);
	            panelPrincipal.add(victoria, 0);

	            JButton levelUp = new JButton();
	            levelUp.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"LevelUp.png")));
	            levelUp.setBounds(panelPrincipal.getWidth() / 2 - 150, panelPrincipal.getHeight() / 2 - 150, 300, 300);
	            levelUp.setOpaque(false);
	            levelUp.setBorderPainted(false);
	            levelUp.setContentAreaFilled(false);
	            panelPrincipal.add(levelUp, 0);
	            
	            panelPrincipal.revalidate();
	            panelPrincipal.repaint();
	            
	            levelUp.addActionListener(new ActionListener() {
	                public void actionPerformed (ActionEvent e) {
	                	volverAlPanelNiveles();
	                	limpiarEntidades();
	                	
	                	if(mi_juego.nivelActual() == 1) {
	                		botonNivel2.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel2.png")));
	                		mi_juego.cargarProximoNivel();
	                		configuraBotonNivel(botonNivel2);
	                	} else if(mi_juego.nivelActual() == 2) {
	                		botonNivel3.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel3.png")));
	                		mi_juego.cargarProximoNivel();
	                		configuraBotonNivel(botonNivel3);
	                	} else if(mi_juego.nivelActual() == 3) {
	                		botonNivel4.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel4.png")));
	                		mi_juego.cargarProximoNivel();
	                		configuraBotonNivel(botonNivel4);
	                	} else if(mi_juego.nivelActual() == 4) {
	                		botonNivel5.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel5.png")));
	                		mi_juego.cargarProximoNivel();
	                		configuraBotonNivel(botonNivel5);
	                	} else if(mi_juego.nivelActual() == 5) {
	                		terminarJuego();
	                	}
	                }
	            });
	        }
	    };
	    worker.execute();
	}
	
	private void configuraBotonNivel(JButton boton) {
		boton.removeActionListener(boton.getActionListeners()[0]);
		boton.addActionListener(new ActionListener() {
		    public void actionPerformed (ActionEvent e) {
		    	iniciarNivel();
		    }
		});
	}

	private void volverAlPanelNiveles() {
		setSize(700, 700);
		setLocationRelativeTo(null);
		cardLayout.show(mainPanel, "seleccionNiveles");
	}
	
	public void limpiarEntidades() {
	    panelPrincipal.removeAll();
	    panelPrincipal.revalidate();
	    panelPrincipal.repaint();
	}

	public void panelSetNombreJugador() {
	    // Crear el JFrame
	    frameNombreJugador.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frameNombreJugador.setSize(300, 105);
	    frameNombreJugador.setLocationRelativeTo(null);
	    frameNombreJugador.setLayout(new BoxLayout(frameNombreJugador.getContentPane(), BoxLayout.Y_AXIS));

	    // Configuración del panel para ingresar el nombre
	    JPanel panelNombre = new JPanel();
	    panelNombre.setLayout(new BoxLayout(panelNombre, BoxLayout.Y_AXIS));

	    JLabel etiquetaNombre = new JLabel("Fin de la Partida. Ingrese nombre de jugador:");
	    etiquetaNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

	    nombreTextField = new JTextField();
	    nombreTextField.setAlignmentX(Component.CENTER_ALIGNMENT);

	    JButton botonAceptar = new JButton("Aceptar");
	    botonAceptar.setAlignmentX(Component.CENTER_ALIGNMENT);

	    panelNombre.add(etiquetaNombre);
	    panelNombre.add(nombreTextField);
	    panelNombre.add(botonAceptar);
	    frameNombreJugador.add(panelNombre);
	    frameNombreJugador.setVisible(true);

	    botonAceptar.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String nombre = nombreTextField.getText();
	            if (nombre != null && !nombre.isEmpty()) {
	            	mi_juego.get_jugador_actual().set_nombre(nombre);
	            	mi_juego.getTopJugadores().agregar_jugador(mi_juego.get_jugador_actual());
	                frameNombreJugador.setVisible(false);     
	                frameNombreJugador.dispose(); 
	                tablaPuntajes();			        
	                serializacionRanking();
	                dialogTablaRanking.setVisible(true);            
	            } else {
	                JOptionPane.showMessageDialog(null, "Por favor, ingrese nombre de jugador.");
	            }
	        }
	    });
	}
	
	public void tablaPuntajes() {
	    // Configuración del JDialog
	    dialogTablaRanking.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialogTablaRanking.setSize(300, 150);
	    dialogTablaRanking.setLocationRelativeTo(null);

	    // Crear el modelo de la tabla
	    if (tablaRanking == null) {
	        tablaRanking = new DefaultTableModel();
	        tablaRanking.addColumn("JUGADOR");
	        tablaRanking.addColumn("PUNTAJE");
	    }
	    
        // Ordenar la tabla por puntaje de mayor a menor
        actualizarTabla();
        
	    // Crear la tabla con el modelo
	    JTable tabla = new JTable(tablaRanking);
	    
	    // Deshabilitar la edición de celdas en la tabla
	    tabla.setDefaultEditor(Object.class, null);

	    // Deshabilitar el reordenamiento de columnas
	    tabla.getTableHeader().setReorderingAllowed(false);
        
	    // Agregar la tabla a un JScrollPane
	    JScrollPane scrollPane = new JScrollPane(tabla);

	    // Agregar el JScrollPane al JDialog
	    dialogTablaRanking.add(scrollPane);
	    
	}

    public void actualizarTabla() {
        // Obtener la lista de jugadores desde TopJugadores
        List<Jugador> jugadores = mi_juego.getTopJugadores().get_ranking_ordenado();

        // Limpiar el modelo de la tabla
        tablaRanking.setRowCount(0);

        // Llenar el modelo de la tabla con los datos de los jugadores
        for (Jugador jugador : jugadores) {
            tablaRanking.addRow(new Object[]{jugador.get_jugador(), jugador.get_puntaje_acumulado()});
        }
    }
    
    
	//Serialización del objeto TopJugadores y escritura en un archivo
	private void serializacionRanking() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("./puntaje/ranking.tdp");
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
	
	//Deserialización del objeto desde el archivo
	private void deserializacionRanking() {
		try {
			FileInputStream fileInputStream = new FileInputStream("./puntaje/ranking.tdp");
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
	
	public void terminarJuego() {
		setSize(700, 700);
		setLocationRelativeTo(null);
		
		panelFinal.removeAll();
		panelFinal.setPreferredSize(new Dimension(700, 700));
		
		JLabel kirbylink = new JLabel();
		kirbylink.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/kirbygiffinal.gif")));
		kirbylink.setBounds(0, 0, 539, 539);
		panelFinal.add(kirbylink, JLayeredPane.DEFAULT_LAYER);
		
		JLabel graciasPorJugar = new JLabel();
		graciasPorJugar.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/graciasPorJugar.png")));
		graciasPorJugar.setBounds(170, 200, 300, 90);
		panelFinal.add(graciasPorJugar, 0);
		
		panelFinal.revalidate();
		panelFinal.repaint();
		
		cardLayout.show(mainPanel, "final");
		
		panelSetNombreJugador();
	}

	public void bloquearNivelesAnteriores() {
		if (mi_juego.nivelActual() == 2)
			botonNivel1.setEnabled(false);
		if (mi_juego.nivelActual() == 3) {
			botonNivel1.setEnabled(false);	
			botonNivel2.setEnabled(false);
		}
		if (mi_juego.nivelActual() == 4) {
			botonNivel1.setEnabled(false);
			botonNivel2.setEnabled(false);
			botonNivel3.setEnabled(false);
		}
		if (mi_juego.nivelActual() == 5) {
			botonNivel1.setEnabled(false);
			botonNivel2.setEnabled(false);
			botonNivel3.setEnabled(false);
			botonNivel4.setEnabled(false);
		}
		
	}
	
	public void bloquearTodosLosNiveles() {
		botonNivel1.setEnabled(false);
		botonNivel2.setEnabled(false);
		botonNivel3.setEnabled(false);
		botonNivel4.setEnabled(false);
		botonNivel5.setEnabled(false);
	}
	
	protected void cargar_objetivos() {
		contadores = new ArrayList<JLabel>();
		objetivos = mi_juego.actualizar_manager_objetivos();
		JLabel contador_aux;
		int filaAux = 96;
		
		for(Objetivo obejetivo : objetivos) {
			if(obejetivo.get_image_path()!= null) {
				contador_aux = new JLabel(" " + obejetivo.get_cantidad());;
	    		contador_aux.setFont(new Font("Ink Free", Font.BOLD, 58));
	    		contador_aux.setBounds(564, filaAux-5, 110, 70);
	    		panelPrincipal.add(contador_aux, 0);
	    		contadores.add(contador_aux);
	    		
	    		JLabel carameloGeneral = new JLabel();
	    		carameloGeneral.setIcon(new ImageIcon(this.getClass().getResource(obejetivo.get_image_path())));
	    		carameloGeneral.setBounds(494, filaAux, 60, 60);
	    		panelPrincipal.add(carameloGeneral, 0);
	    		
	    		filaAux = filaAux + 96;
			}
		}
		
	}
	
	public void actualizar_objetivos(List<Objetivo> lista) {
		int aux = 0;
		if (contadores.size() > 0)
		for(Objetivo objetivo : lista){
			if(objetivo.get_cantidad() == 0)
				contadores.remove(aux);
			else
				contadores.get(aux).setText(" " + (objetivo.get_cantidad()) );
			aux++;
		}
	}

	@SuppressWarnings("unused")
	private void cargarMusica() {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/musica/" + Generador.toString() + "-music.wav"));
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

	public void notificar_perder() {
		mostrarGameOver();
	}

	public void notificar_desuscripcion(int posicion) {
		if(contadores.size() > posicion) {
			contadores.get(posicion).setText("0");
			contadores.remove(posicion);
		}
	}
	
	public void actualizar_puntaje(int puntaje_a_sumar) {
		mi_juego.get_jugador_actual().actualizar_puntaje_nivel_actual(puntaje_a_sumar);
		contadorPuntaje.setText("Puntaje: " + (mi_juego.get_jugador_actual().get_puntaje_acumulado() + mi_juego.get_jugador_actual().get_puntaje_nivel_actual()));
		
	}
	
	private Icon cargarIcono(String ruta) {
		try {
			return new ImageIcon(this.getClass().getResource(ruta));
		} catch (Exception e) {
			return null;
		}
	}

	// ===================== LOGICA GUI =====================
	public AbstractFactory getGenerador() {
		return Generador;
	}
	
	public EntidadGrafica agregar_entidad(EntidadLogica e) {
		Celda celda = new Celda(this, e, SIZE_LABEL);
		panelPrincipal.add(celda, 0);
		return celda;
	}
	
	public EntidadGrafica agregar_entidad_nueva(EntidadLogica e, int fila) {
		//Caramelos que caen
		Celda celda = new Celda(this, e, SIZE_LABEL);
		celda.setBounds((e.get_columna()+1)*SIZE_LABEL, (fila)*SIZE_LABEL, SIZE_LABEL, SIZE_LABEL);
		panelPrincipal.add(celda, 0);
		return celda;
	}
	
	public EntidadGrafica agregar_entidad_nueva(Potenciador p) {
		//Potenciadores que aparecen luego de la detonacion
		Celda celda = new Celda(this, p, SIZE_LABEL);
		panelPrincipal.add(celda, 0);
		celda.imagen_vacia();
		animar_creacion_con_delay(celda);
		return celda;
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
	
	public void animar_movimiento(Celda c) {
	    mi_animador.animar_intercambio(c);
	}

	public void animar_cambio_estado(Celda c) {
	    mi_animador.animar_cambio_foco(c);
	}

	public void animar_detonacion(Celda c) {
	    mi_animador.animar_detonacion(c);
	}
	
	public void animar_caida(Celda celda) {
		mi_animador.animar_caida(celda);
	}
	
	public void animar_creacion_con_delay(Celda celda) {
		mi_animador.animar_creacion_con_delay(celda);
	}
	
	public void eliminar_celda(Celda celda) {
		panelPrincipal.remove(celda);
		panelPrincipal.repaint();
	}
}