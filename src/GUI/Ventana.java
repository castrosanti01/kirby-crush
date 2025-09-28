package GUI;

import java.awt.BorderLayout;
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
import entidades.TdP2;
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

/**
 * Modela el comportamiento de la Ventana de la aplicación.
 * Ofrece servicios para comunicar los diferentes elementos que conforman la gráfica de la aplicación con la lógica de la misma.
 *
 */
public class Ventana extends JFrame implements VentanaAnimable, VentanaNotificable, Puntaje {
	
	protected Juego mi_juego;
	protected CentralAnimaciones mi_animador;
	protected int filas;
	protected int columnas;
	protected List<Objetivo> objetivos;
	
	protected int animaciones_pendientes;
	protected boolean bloquear_intercambios;
	
	protected JLabel texto_superior;
	protected JLayeredPane panel_principal;
	
	protected JFrame frameDeNiveles;
	private int size_label = 60;
	
	private JLabel vida1, vida2, vida3;
	private Timer timerglobal;
	private boolean quedanVidas = true;
	
	private JComboBox<AbstractFactory> comboBox;
	protected AbstractFactory Generador;
	
	private List<JLabel> contadores;
	public JLabel contadorMovimientos;	
	public JLabel contadorTiempo;
	public JLabel contadorVerdes;
	public JLabel contadorAmarillos;
	public JLabel contadorAzules;
	public JLabel contadorVioletas;
	public JLabel contadorRosas;
	public JLabel contadorGelatina;
	public JLabel contadorGlaseados;
	public JLabel objetivo;
	public JLabel contadorPuntaje;
	
	protected JTextField nombreTextField;
	protected JFrame frameNombreJugador;
	protected JDialog dialogTablaRanking;
	protected JFrame frameTablaRanking;
	protected DefaultTableModel tablaRanking;
	
	private JButton botonNivel1, botonNivel2, botonNivel3, botonNivel4, botonNivel5;
	
	protected JButton botonRanking;
	
	private List<TdP2> entidadesTDP2;
	
	/**
	 * Inicializa la ventana asociada al juego en progreso, considerando
	 * @param j El juego que controlará la lógica de la aplicación, y con quien comunicará los movimientos del jugador.
	 */
	public Ventana(Juego j) {
		mi_juego = j;
		mi_animador = new CentralAnimaciones(this);
		
		animaciones_pendientes = 0;
		bloquear_intercambios = false;
		
		entidadesTDP2 = new ArrayList<>();
		
		inicializar();
	}
	
	public AbstractFactory getGenerador() {
		return Generador;
	}
	
	/**
	 * Crea una nueva celda, que quedará asociada a la entidad lógica parametrizada, a partir de la ubicación de esta.
	 * Agrega y deja visible la celda creada, por sobre la pantalla.
	 * @param e Entidad lógica con la que quedará asociada la celda.
	 * @return La entidad gráfica creada.
	 */
	public EntidadGrafica agregar_entidad(EntidadLogica e) {
		Celda celda = new Celda(this, e, size_label);
		panel_principal.add(celda, 0);
		return celda;
	}
	
	public EntidadGrafica agregar_entidad_nueva(EntidadLogica e, int fila) {
		//Caramelos que caen
		Celda celda = new Celda(this, e, size_label);
		celda.setBounds((e.get_columna()+1)*size_label, (fila)*size_label, size_label, size_label);
		panel_principal.add(celda, 0);
		return celda;
	}
	
	public EntidadGrafica agregar_entidad_nueva(Potenciador p) {
		//Potenciadores que aparecen luego de la detonacion
		Celda celda = new Celda(this, p, size_label);
		panel_principal.add(celda, 0);
		celda.imagen_vacia();
		animar_creacion_con_delay(celda);
		return celda;
	}
	
	@Override
	public void notificarse_animacion_en_progreso() {
		synchronized(this){
			animaciones_pendientes ++;
			bloquear_intercambios = true;
		}
	}
	
	@Override
	public void notificarse_animacion_finalizada() {
		synchronized(this){
			animaciones_pendientes --;
			bloquear_intercambios = animaciones_pendientes > 0;
		}
	}
	
	@Override
	public void animar_movimiento(Celda c) {
	    mi_animador.animar_intercambio(c);
	}

	@Override
	public void animar_cambio_estado(Celda c) {
	    mi_animador.animar_cambio_foco(c);
	}

	@Override
	public void animar_detonacion(Celda c) {
	    mi_animador.animar_detonacion(c);
	}
	
	@Override
	public void animar_caida(Celda celda) {
		mi_animador.animar_caida(celda);
	}
	
	@Override
	public void animar_creacion_con_delay(Celda celda) {
		mi_animador.animar_creacion_con_delay(celda);
	}
	
	@Override
	public void eliminar_celda(Celda celda) {
		panel_principal.remove(celda);
		panel_principal.repaint();
	}

	
	protected void inicializar() {
		
		panel_principal = new JLayeredPane();
	    //Pantalla para elegir skin
	
		 timerglobal = new Timer(10000, comboBox);
		
		//Crea el frame donde se contendra el panel inicial
		JFrame framePanelElegir = new JFrame("Kirby & Zelda Crush");
		framePanelElegir.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		framePanelElegir.setSize(510, 439);
		framePanelElegir.setLocationRelativeTo(null);
		framePanelElegir.setResizable(false);
		framePanelElegir.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/kirbyicon.png")).getImage());
		framePanelElegir.setVisible(true);
	
		//Crea el panel inicial
		JLayeredPane panelElegir = new JLayeredPane();
		panelElegir.setPreferredSize(new Dimension(500, 400));
		framePanelElegir.add(panelElegir);
	
		//Settea el fondo del panel inicial
		Icon imgIcon = new ImageIcon(this.getClass().getResource("/imagenes/niveles/FondoProyecto2.png"));
		JLabel fondo = new JLabel(imgIcon);
		fondo.setBounds(0, 0, 500, 400);
		panelElegir.add(fondo, 0);
	
		//Creo un box para iniciar el dominio de aplicacion seleccionado
		AbstractFactory[] factories = new AbstractFactory[2];
		factories[0] = new KirbyFactory();
		factories[1] = new ZeldaFactory();
		comboBox = new JComboBox<AbstractFactory>(factories);
		comboBox.setBounds(185, 337, 132, 40);
		panelElegir.add(comboBox);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Abstact factory para la eleccion de skin
				Generador = (AbstractFactory)comboBox.getSelectedItem();
				mi_juego.cargarDatos(Generador);
				filas = mi_juego.getTablero().get_filas();
				columnas = mi_juego.getTablero().get_columnas();
				mi_juego.asociar();
				
				mi_juego.setGenerador(Generador);
				
				frameDeNiveles = new JFrame(Generador.toString()+" Crush");
				frameDeNiveles.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/"+Generador.toString()+"icon.png")).getImage());				
				
				frameNombreJugador = new JFrame(Generador.toString()+" Crush");
				frameNombreJugador.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/"+Generador.toString()+"icon.png")).getImage());				
				
				framePanelElegir.dispose();
				panelEleccionNiveles();

				//cargarMusica();
				
				dialogTablaRanking = new JDialog(frameTablaRanking, "RANKING - Top 5", true);
				dialogTablaRanking.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/"+Generador.toString()+"icon.png")).getImage());				
				
				deserializacionRanking();
			}
		});
	}
	
	public void panelEleccionNiveles() {
		
		//Frame que contendra el panel de los niveles 
		frameDeNiveles.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameDeNiveles.setSize(510, 439);
		frameDeNiveles.setLocationRelativeTo(null);
		frameDeNiveles.setVisible(true);
		frameDeNiveles.setResizable(false);
		
		//Panel en el que mostrara los niveles disponibles
		JLayeredPane panelDeNiveles = new JLayeredPane();
		panelDeNiveles.setPreferredSize(new Dimension(510, 439));
		frameDeNiveles.add(panelDeNiveles);
		
		//Settear el fondo del panel de niveles
		Icon imgIcon2 = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"gif.gif"));
		JLabel fondo2 = new JLabel(imgIcon2);
		fondo2.setBounds(0, 0, 500, 400);
		panelDeNiveles.add(fondo2, 0);
		
		JLabel niveles = new JLabel(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"MenuNiveles.png")));
		niveles.setBounds(-11, -101, 550, 500);
		panelDeNiveles.add(niveles, 0);
		
		//Agrego las labels que contienen la representacion visual de las vidas disponibles 
		//que se mostraran en el panel de los niveles
		vida1 = new JLabel(new ImageIcon(this.getClass().getResource("/imagenes/niveles/vidas.png")));
		vida1.setBounds(180, 10, 50, 50);
		panelDeNiveles.add(vida1, 0);
		
		vida2 = new JLabel(new ImageIcon(this.getClass().getResource("/imagenes/niveles/vidas.png")));
		vida2.setBounds(220, 10, 50, 50);
		panelDeNiveles.add(vida2, 0);
		
		vida3 = new JLabel(new ImageIcon(this.getClass().getResource("/imagenes/niveles/vidas.png")));
		vida3.setBounds(260, 10, 50, 50);
		panelDeNiveles.add(vida3, 0);
		
		botonRanking = new JButton();
        botonRanking.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"Ranking.png")));
        botonRanking.setBounds(155, 319, 180, 70);
        botonRanking.setOpaque(false);
        botonRanking.setBorderPainted(false);
        botonRanking.setContentAreaFilled(false);
		panelDeNiveles.add(botonRanking, 0);
		
		botonRanking.addActionListener(new ActionListener() {
		    public void actionPerformed (ActionEvent e) {
		    	
		    	if(!entidadesTDP2.isEmpty())
			    	for(TdP2 entidad : entidadesTDP2)
			    		if(entidad.get_timer() != null) 
			    			entidad.get_timer().stop();
		    	
		    	if (timerglobal != null) timerglobal.stop();
		        tablaPuntajes();
		        dialogTablaRanking.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		        dialogTablaRanking.setVisible(true);
		        panel_principal.requestFocusInWindow();
		        if (timerglobal != null) timerglobal.start();
		        
		        if(!entidadesTDP2.isEmpty())
			        for(TdP2 entidad : entidadesTDP2)
			        	if(entidad.get_timer() != null) 
			        		entidad.get_timer().start();

		    }
		});

	    
		//Creo 5 botones para los niveles y los agrego al panel de niveles
		botonNivel1 = new JButton();
		botonNivel1.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel1.png")));
		botonNivel1.setBounds(120, 125, 100, 100);
		botonNivel1.setOpaque(false);
		botonNivel1.setBorderPainted(false);
		botonNivel1.setContentAreaFilled(false);
		panelDeNiveles.add(botonNivel1, 0);
		
		botonNivel2 = new JButton();
		botonNivel2.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivelConCandado.png")));
		botonNivel2.setBounds(200, 125, 100, 100);
		botonNivel2.setOpaque(false);
		botonNivel2.setBorderPainted(false);
		botonNivel2.setContentAreaFilled(false);
		panelDeNiveles.add(botonNivel2, 0);
		
		botonNivel3 = new JButton();
		botonNivel3.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivelConCandado.png")));
		botonNivel3.setBounds(280, 125, 100, 100);
		botonNivel3.setOpaque(false);
		botonNivel3.setBorderPainted(false);
		botonNivel3.setContentAreaFilled(false);
		panelDeNiveles.add(botonNivel3, 0);
		
		botonNivel4 = new JButton();
		botonNivel4.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivelConCandado.png")));
		botonNivel4.setBounds(160, 200, 100, 100);
		botonNivel4.setOpaque(false);
		botonNivel4.setBorderPainted(false);
		botonNivel4.setContentAreaFilled(false);
		panelDeNiveles.add(botonNivel4, 0);
		
		botonNivel5 = new JButton();
		botonNivel5.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivelConCandado.png")));
		botonNivel5.setBounds(243, 200, 100, 100);
		botonNivel5.setOpaque(false);
		botonNivel5.setBorderPainted(false);
		botonNivel5.setContentAreaFilled(false);
		panelDeNiveles.add(botonNivel5, 0);
	
		botonNivel1.addActionListener(new ActionListener() {
		    public void actionPerformed (ActionEvent e) {
		    	frameDeNiveles.setVisible(false);
		    	panelNivel();
		    	for(TdP2 entidad : entidadesTDP2)
		    		entidad.iniciarTemporizador();
		    }
		});
		panel_principal.addKeyListener(new KeyAdapter() {
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
	}
		
	public void panelNivel() {
		//Panel niveles
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 700);
		//setSize(new Dimension((columnas+2)*63, (filas/2+3)*59));
		setLocationRelativeTo(null);
		setTitle(frameDeNiveles.getTitle());
		setVisible(true);
		setResizable(false);
		setIconImage(frameDeNiveles.getIconImage());

		Icon fondoNiveles = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"FondoNiveles.png"));
		JLabel labelFondoNiveles = new JLabel(fondoNiveles);
		labelFondoNiveles.setBounds(0, 0, 700, 700);
		
		panel_principal.setSize(size_label * filas, size_label * columnas);
		panel_principal.add(labelFondoNiveles, JLayeredPane.DEFAULT_LAYER);
		panel_principal.setLayout(null);
		panel_principal.setVisible(true);
		getContentPane().add(panel_principal, BorderLayout.CENTER);
		panel_principal.setFocusable(true);
		
		mi_juego.set_movimientos();
		int movimientos = mi_juego.get_movimientos();
	        contadorMovimientos = new JLabel("Movimientos: "+movimientos);
	        contadorMovimientos.setFont(new Font("Ink Free", Font.BOLD, 50));
	        contadorMovimientos.setBounds(70, 586, 380, 70);
	        panel_principal.add(contadorMovimientos, 0);
		
	        cargar_objetivos();
	        
	        botonRanking.setBounds(480, 28, 180, 70);
			panel_principal.add(botonRanking, 0);
			
		int puntaje = mi_juego.get_jugador_actual().get_puntaje_acumulado();
	        contadorPuntaje = new JLabel("Puntaje: "+puntaje);
	        contadorPuntaje.setFont(new Font("Ink Free", Font.BOLD, 50));
	        contadorPuntaje.setBounds(70, 520, 380, 70);
	        panel_principal.add(contadorPuntaje, 0);

	}		

	public void mostrarGameOver() {
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        @Override
	        protected Void doInBackground() {
	            try {
	                // Espera durante 2,5 segundos (2500 milisegundos)
	                Thread.sleep(100);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            return null;
	        }
	
	        @Override
	        protected void done() {
	        	//Muestro mensaje de game over
	            JLabel gameOver = new JLabel(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"GameOverPane.png")));
	            gameOver.setBounds(0, 0, 500, 500);
	            
	            //Creo el boton para volver a intentar el nivel
	            JButton retry = new JButton(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"RetryButton.png")));
	            retry.setBounds(160, 250, 175, 40);
	            retry.setOpaque(false);
	            retry.setBorderPainted(false);
	            retry.setContentAreaFilled(false);
	            
	            //Creo el boton para volver al menu de i=niveles
	            JButton exit = new JButton(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"ExitButton.png")));
	            exit.setBounds(160, 290, 175, 40);
	            exit.setOpaque(false);
	            exit.setBorderPainted(false);
	            exit.setContentAreaFilled(false);
	            
	            //Agrego todo al panel, 2.5 segundos despues de que se acabaron los movimientos
	            panel_principal.add(gameOver, 0);
	            panel_principal.add(retry, 0);
	            panel_principal.add(exit, 0);
	            panel_principal.setFocusable(false);
	            panel_principal.revalidate();
	            panel_principal.repaint();
	            
	            retry.addActionListener(new ActionListener() {
	                public void actionPerformed (ActionEvent e) {
	                    perderVida(); // Actualiza la visibilidad de las vidas
	                    
	                    //reiniciar contadores tdp2
	                    entidadesTDP2.clear();

	                    if (quedanVidas) {
	                        limpiarEntidades();
	                        mi_juego.reiniciarNivel();
	                        gameOver.setVisible(false);
	                        exit.setVisible(false);
	                        retry.setVisible(false);
	                		frameDeNiveles.setVisible(true);
	                        botonRanking.setBounds(155, 319, 180, 70);
	                		frameDeNiveles.add(botonRanking, 0);
	                		pack();
	                		mi_juego.set_movimientos();		    
	                    } else {                     
	                        mostrarPanelNiveles();
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
	}
	
	protected void perderVida() {
	    if (vida3.isVisible()) {
	        vida3.setVisible(false);
	    } else if (vida2.isVisible()) {
	        vida2.setVisible(false);
	    } else if (vida1.isVisible()) {
	        vida1.setVisible(false);
	        quedanVidas = false;
	    }
	}
	
	private void mostrarPanelNiveles() {
	    panel_principal.setVisible(false);
	    setVisible(false);
	    frameDeNiveles.setVisible(true);
	}

	public void limpiarEntidades() {
	    panel_principal.removeAll(); // Elimina todos los componentes del panel_principal
	    panel_principal.revalidate(); // Revalida el panel
	    panel_principal.repaint(); // Repinta el panel
	}
	
	public void ganarNivel() {
		
		if(!entidadesTDP2.isEmpty())
	    	for(TdP2 entidad : entidadesTDP2)
    			if (entidad.get_timer() != null) entidad.get_timer().stop();
		
	    // Deshabilita cualquier interacción con el juego mientras se muestra la etiqueta de "victoria"
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	            // Espera unos segundos antes de mostrar la etiqueta de "victoria"
	            Thread.sleep(500); 

	            return null;
	        }

	        @Override
	        protected void done() {
	            // Muestra la etiqueta de "victoria" después de la espera
	            JLabel victoria = new JLabel();
	            victoria.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"-dance.gif")));
	            victoria.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
	            victoria.setVisible(true);
	            panel_principal.add(victoria, 0);

	            JButton levelUp = new JButton();
	            levelUp.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"LevelUp.png")));
	            levelUp.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
	            levelUp.setOpaque(false);
	            levelUp.setBorderPainted(false);
	            levelUp.setContentAreaFilled(false);
	            panel_principal.add(levelUp, 0);
	            
	            panel_principal.revalidate(); // Asegura que la interfaz se repinte correctamente
	            panel_principal.repaint();
	            
	            levelUp.addActionListener(new ActionListener() {
	                public void actionPerformed (ActionEvent e) {
	                	mostrarPanelNiveles();
                        botonRanking.setBounds(155, 319, 180, 70);
                		frameDeNiveles.add(botonRanking, 0);
	                	if(mi_juego.nivelActual() == 1) {
	                		botonNivel2.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel2.png")));
	                		limpiarEntidades();
	                		mi_juego.cargarProximoNivel();
	                		victoria.setVisible(false);
	                		levelUp.setVisible(false);
	                		
		            		botonNivel2.addActionListener(new ActionListener() {
		            		    public void actionPerformed (ActionEvent e) {
		            		    	
		            		    	//reiniciar contadores tdp2
		    	                    entidadesTDP2.clear();
		            		    	
		            		    	frameDeNiveles.setVisible(false);
		            		    	setSize(700, 700);
		            		    	Icon fondoNiveles = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"FondoNiveles.png"));
		            				JLabel labelFondoNiveles = new JLabel(fondoNiveles);
		            				labelFondoNiveles.setBounds(0, 0, 700, 700);
		            		    	resetPantallaNivel();
		            		    	victoria.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	levelUp.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	
		            				panel_principal.add(labelFondoNiveles, JLayeredPane.DEFAULT_LAYER);

		            				contadorMovimientos.setBounds(70, 586, 380, 70);
		            				contadorPuntaje.setBounds(70, 520, 380, 70);
		            				botonRanking.setBounds(480, 28, 180, 70);
		            				panel_principal.add(botonRanking, 0);
		            		    }
		            		});
		            		
	                	} else if(mi_juego.nivelActual() == 2) {
	                		botonNivel3.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel3.png")));
	                		limpiarEntidades();
	                		mi_juego.cargarProximoNivel();
	                		victoria.setVisible(false);
	                		levelUp.setVisible(false);
		            		botonNivel3.addActionListener(new ActionListener() {
		            		    public void actionPerformed (ActionEvent e) {
		            		    	
		            		    	//reiniciar contadores tdp2
		    	                    entidadesTDP2.clear();
		            		    	
		            		    	frameDeNiveles.setVisible(false);
		            		    	resetPantallaNivel();
		            		    	victoria.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	levelUp.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	
		            		    	Icon fondoNiveles = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"FondoNiveles.png"));
		            				JLabel labelFondoNiveles = new JLabel(fondoNiveles);
		            				labelFondoNiveles.setBounds(0, 0, 700, 700);
		            				panel_principal.add(labelFondoNiveles, JLayeredPane.DEFAULT_LAYER);
		            				
		            				contadorMovimientos.setBounds(70, 586, 380, 70);
		            				contadorPuntaje.setBounds(70, 520, 380, 70);
		            				botonRanking.setBounds(480, 28, 180, 70);
		            				panel_principal.add(botonRanking, 0);
		            		    }
		            		});
	                	} else if(mi_juego.nivelActual() == 3) {
	                		botonNivel4.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel4.png")));
	                		limpiarEntidades();
	                		mi_juego.cargarProximoNivel();
	                		victoria.setVisible(false);
	                		levelUp.setVisible(false);
		            		botonNivel4.addActionListener(new ActionListener() {
		            		    public void actionPerformed (ActionEvent e) {
		            		    	
		            		    	//reiniciar contadores tdp2
		    	                    entidadesTDP2.clear();
		            		    	
		            		    	frameDeNiveles.setVisible(false);
		            		    	setSize(700, 700);
		            		    	resetPantallaNivel();
		            		    	victoria.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	levelUp.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	
		            		    	Icon fondoNiveles = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"FondoNiveles.png"));
		            				JLabel labelFondoNiveles = new JLabel(fondoNiveles);
		            				labelFondoNiveles.setBounds(0, 0, 700, 700);
		            				panel_principal.add(labelFondoNiveles, JLayeredPane.DEFAULT_LAYER);
		            				
		            				mi_juego.set_tiempo_restante();
		            		        int tiempoInicial = mi_juego.get_tiempo_restante();
		            		        if (tiempoInicial > 0) {
		            		            contadorTiempo = new JLabel("Tiempo: "+tiempoInicial);
		            		            contadorTiempo.setFont(new Font("Ink Free", Font.BOLD, 50));
		            		            contadorTiempo.setBounds(70, 586, 380, 70);
		            		            panel_principal.add(contadorTiempo, 0);

		            		            // Crear un temporizador que actualice el contador de tiempo cada segundo
		            		            Timer timer = new Timer(1000, new ActionListener() {		            		        
		            		            	int tiempo = tiempoInicial;
		            		                
		            		                @Override
		            		                public void actionPerformed(ActionEvent e) {
		            		                    contadorTiempo.setText("Tiempo: " + tiempo);
		            		                    if (tiempo == 0) {
		            		                        mostrarGameOver();
		            		                    }
		            		                    tiempo--;

		            		                    if (tiempo < 0) {
		            		                        // Detener el temporizador cuando el tiempo sea negativo
		            		                        ((Timer) e.getSource()).stop();
		            		                    }
		            		                }
		            		            });

		            		            // Iniciar el temporizador
		            		            timer.start();
		            		            timerglobal = timer;
	
		            		        }
		            				contadorTiempo.setBounds(70, 586, 380, 70);
		            				contadorMovimientos.setVisible(false);
		            				contadorPuntaje.setBounds(70, 520, 380, 70);
		            				botonRanking.setBounds(480, 28, 180, 70);
		            				panel_principal.add(botonRanking, 0);
		            		    }
		            		});
	                	} else if(mi_juego.nivelActual() == 4) {
	                		botonNivel5.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"BotonNivel5.png")));
	                		limpiarEntidades();
	                		mi_juego.cargarProximoNivel();
	                		victoria.setVisible(false);
	                		levelUp.setVisible(false);
	                		if(timerglobal != null && timerglobal.isRunning()) timerglobal.stop();
		            		botonNivel5.addActionListener(new ActionListener() {
		            		    public void actionPerformed (ActionEvent e) {
		            		    	
		            		    	//reiniciar contadores tdp2
		    	                    entidadesTDP2.clear();
		            		    	
		            		    	frameDeNiveles.setVisible(false);
		            		    	setSize(700, 700);
		            		    	resetPantallaNivel();
		            		    	victoria.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	levelUp.setBounds(panel_principal.getWidth() / 2 - 150, panel_principal.getHeight() / 2 - 150, 300, 300);
		            		    	
		            		    	Icon fondoNiveles = new ImageIcon(this.getClass().getResource("/imagenes/niveles/"+Generador.toString()+"FondoNiveles.png"));
		            				JLabel labelFondoNiveles = new JLabel(fondoNiveles);
		            				labelFondoNiveles.setBounds(0, 0, 700, 700);
		            				panel_principal.add(labelFondoNiveles, JLayeredPane.DEFAULT_LAYER);
		            				contadorMovimientos.setBounds(70, 586, 380, 70);
		            				contadorMovimientos.setVisible(true);	
		            				contadorPuntaje.setBounds(70, 520, 380, 70);
		            				botonRanking.setBounds(480, 28, 180, 70);
		            				panel_principal.add(botonRanking, 0);
		            		    }
		            		});
	                	}
	                }
	            });
	        }
	    };

	    worker.execute(); // Inicia el SwingWorker para esperar y luego mostrar la etiqueta
	}

	public void resetPantallaNivel() {
		panel_principal.setVisible(true);
    	setVisible(true);
    	panel_principal.setFocusable(true);
        panel_principal.add(contadorMovimientos);
        panel_principal.add(contadorPuntaje);
        cargar_objetivos();
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
		dispose();
		frameDeNiveles.dispose();
		
		JLabel kirbylink = new JLabel();
		kirbylink.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/kirbygiffinal.gif")));
		kirbylink.setSize(539, 539);
		
		JFrame frameVentanaFinal = new JFrame("Kirby Crush");
		frameVentanaFinal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameVentanaFinal.setSize(539, 539);
		frameVentanaFinal.setLocationRelativeTo(null);
		frameVentanaFinal.setResizable(false);
		frameVentanaFinal.setVisible(true);
		frameVentanaFinal.setIconImage(new ImageIcon(this.getClass().getResource("/imagenes/icono/"+Generador.toString()+"icon.png")).getImage());				
		
		JLayeredPane ventanaFinal = new JLayeredPane();
		ventanaFinal.setSize(539, 539);
		frameVentanaFinal.add(ventanaFinal);
		
		ventanaFinal.add(kirbylink, JLayeredPane.DEFAULT_LAYER);
		
		JLabel graciasPorJugar = new JLabel();
		graciasPorJugar.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/niveles/graciasPorJugar.png")));
		graciasPorJugar.setBounds(170, 200, 300, 90);
		ventanaFinal.add(graciasPorJugar, 0);
		
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
		mi_juego.notificar_regla_match();
		contadores = new ArrayList<JLabel>();
		objetivos = mi_juego.actualizar_manager_objetivos();
		JLabel contador_aux;
		int filaAux = 96;
		
		for(Objetivo obejetivo : objetivos) {
			if(obejetivo.get_image_path()!= null) {
				contador_aux = new JLabel(" " + obejetivo.get_cantidad());;
	    		contador_aux.setFont(new Font("Ink Free", Font.BOLD, 58));
	    		contador_aux.setBounds(564, filaAux-5, 110, 70);
	    		panel_principal.add(contador_aux, 0);
	    		contadores.add(contador_aux);
	    		
	    		JLabel carameloGeneral = new JLabel();
	    		carameloGeneral.setIcon(new ImageIcon(this.getClass().getResource(obejetivo.get_image_path())));
	    		carameloGeneral.setBounds(494, filaAux, 60, 60);
	    		panel_principal.add(carameloGeneral, 0);
	    		
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

	public void notificar_existencia(TdP2 tdp2) {
		entidadesTDP2.add(tdp2);
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
}