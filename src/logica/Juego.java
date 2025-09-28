package logica;


import java.awt.EventQueue;
import java.util.List;
import entidades.Entidad;
import entidades.Gelatina;
import entidades.Potenciador;
import entidades.TdP2;
import factories.AbstractFactory;
import GUI.EntidadGrafica;
import GUI.Ventana;

/**
 * Modela el comportamiento del Juego. Ofrece servicios para comunicar los diferentes elementos que
 * conforman la lógica de la aplicación con la gráfica de la misma.
 */
public class Juego {

  public static final int ARRIBA = 15000;
  public static final int ABAJO = 15001;
  public static final int IZQUIERDA = 15002;
  public static final int DERECHA = 15003;

  protected Tablero mi_tablero;
  protected Ventana mi_ventana;
  protected Nivel mi_nivel;
  protected ManagerObjetivos manager_objetivos;
  protected TopJugadores ranking;
  protected AbstractFactory skin;
  private Jugador jugador_actual;  
  protected int movimientos;
  protected int tiempo_restante;
  protected int contador_puntos;
  public int vidas = 3;

  public Juego() {
    mi_tablero = new Tablero(this);
    mi_ventana = new Ventana(this);
    ranking = new TopJugadores();
    jugador_actual = new Jugador();
  }
  
  public void cargarDatos(AbstractFactory generador) {
	  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/1-nivel.txt"), generador, mi_tablero);
	  skin = generador;
  }
  
  public void asociar() {
	  asociar_entidades_logicas_graficas();
	  mi_tablero.fijar_jugador(mi_nivel.get_fila_inicial_jugador(),mi_nivel.get_columna_inicial_jugador());
  }

  public void mover_jugador(int d) {
    mi_tablero.mover_jugador(d);
  }

  public void intercambiar_entidades(int d) {
    mi_tablero.intercambiar_entidades(d);
  }

  public void asociar_entidad_logica_grafica_nueva(Entidad e, int fila) {
	  //Caramelos que caen
	  EntidadGrafica eg = mi_ventana.agregar_entidad_nueva(e, fila);
	  e.set_entidad_grafica(eg);
  }
  
  public void asociar_entidad_logica_grafica_nueva(Potenciador p) {
	  	//Potenciadores que aparecen luego de la detonacion
	  	EntidadGrafica eg = mi_ventana.agregar_entidad_nueva(p);
	    p.set_entidad_grafica(eg);
  }
  
  public void asociar_gelatina_logica_grafica_nueva(Gelatina g) {
	  EntidadGrafica eg = mi_ventana.agregar_entidad(g);
	  g.set_entidad_grafica(eg);
	}

  private void asociar_entidades_logicas_graficas() {
    Entidad e;
    Gelatina g;
    EntidadGrafica eg;
    EntidadGrafica eg2;

    for (int f = 0; f < mi_tablero.get_filas(); f++) {
      for (int c = 0; c < mi_tablero.get_columnas(); c++) {
	        e = mi_tablero.get_entidad(f, c);
	        eg = mi_ventana.agregar_entidad(e);
	        e.set_entidad_grafica(eg);
	        if(mi_tablero.get_gelatina(f, c) != null) {
	        	g = mi_tablero.get_gelatina(f, c);
		        eg2 = mi_ventana.agregar_entidad(g);
		        g.set_entidad_grafica(eg2);
	        }
      }
    }
    
    mi_ventana.setLocationRelativeTo(null);
    mi_ventana.setVisible(false);
  }
  
  public Tablero getTablero() {
	  return mi_tablero;
  }

  public TopJugadores getTopJugadores() {
	  return ranking;
  }
  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          new Juego();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * El juego es notificado de que se realizó un movimiento exitosamente y debe descontarse la cantidad de movimientos restantes.
   * 
   */
  public void notificarMovimiento() {
    movimientos--;
    mi_ventana.contadorMovimientos.setText("Movimientos: " + movimientos);
    if (movimientos == 0)
      mi_ventana.mostrarGameOver();
  }
/**
 *  Reinicia el nivel y reestablece su lógica
 *
 */
  public void reiniciarNivel() {
    // Restablecer la lógica del juego
	  if (nivelActual() == 1)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/1-nivel.txt"), skin, mi_tablero);
	  else if (nivelActual() == 2)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/2-nivel.txt"), skin, mi_tablero);
	  else if (nivelActual() == 3)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/3-nivel.txt"), skin, mi_tablero);
	  else if (nivelActual() == 4)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/4-nivel.txt"), skin, mi_tablero);
	  else if (nivelActual() == 5)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/5-nivel.txt"), skin, mi_tablero);

    // Actualizar la representación gráfica
    mi_ventana.limpiarEntidades(); // Este método debería eliminar todas las entidades gráficas
                                   // actuales
    asociar_entidades_logicas_graficas(); // Vuelve a asociar las entidades lógicas con las
                                          // entidades gráficas

    mi_tablero.fijar_jugador(mi_nivel.get_fila_inicial_jugador(), mi_nivel.get_columna_inicial_jugador());
    set_movimientos();
    mi_ventana.contadorMovimientos.setText("Movimientos: " + movimientos);
    vidas--;
    jugador_actual.resetear_puntaje();
    mi_ventana.contadorPuntaje.setText("Puntaje: " + jugador_actual.get_puntaje_acumulado());
    // Repintar la ventana
    mi_ventana.repaint();
  }

/**
 * Se inicializa la cantidad de movimientos disponibles
 *
 */
  public void set_movimientos() {
    movimientos = mi_nivel.get_movimientos();
  }
/**
 *Se devuelve la cantidad de movimientos actuales
 *@return movimientos cantidad de movimientos actuales.
 * */
  public int get_movimientos() {
    return movimientos;
  }
  /**
   * Establece el tiempo restante
   *
   */
    public void set_tiempo_restante() {
      tiempo_restante = mi_nivel.get_tiempo_restante();
    }
/**
 *Se devuelve la cantidad de movimientos actuales
 *@return movimientos cantidad de movimientos actuales.
 * */
  public int get_tiempo_restante() {
    return tiempo_restante;
  }
/**
 * Se devuelve las vidas restantes
 * @return vidas cantidad de vidas restantes
 */
  public int getVidas() {
    return vidas;
  }
  
  public void chequeo_nivel() {
	  boolean termino_nivel = manager_objetivos.get_lista().size() == 0;
	  if (termino_nivel) {
		  mi_ventana.ganarNivel();
	      if (nivelActual() == 5)
	    	  mi_ventana.terminarJuego();
	    }
  }
  
  public List<Objetivo> actualizar_manager_objetivos() {
	  manager_objetivos = mi_nivel.get_manager_objetivos();
	  mi_tablero.actualizar_manager_objetivos(manager_objetivos);
	return manager_objetivos.get_lista();
  }
  
  public ManagerObjetivos get_manager_objetivos() {
	  return manager_objetivos;
  }
  
  public void actualizar_objetivos() {
	  mi_ventana.actualizar_objetivos(manager_objetivos.get_lista());
  }
  
  public void cargarProximoNivel() {
	  if (nivelActual() == 1)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/2-nivel.txt"), skin, mi_tablero);
	  else if (nivelActual() == 2)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/3-nivel.txt"), skin, mi_tablero);
	  else if (nivelActual() == 3)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/4-nivel.txt"), skin, mi_tablero);
	  else if (nivelActual() == 4)
		  mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream("/niveles/5-nivel.txt"), skin, mi_tablero);
	  tiempo_restante = mi_nivel.get_tiempo_restante();
	  movimientos = mi_nivel.get_movimientos();
	  vidas = 3;
	  
	  asociar_entidades_logicas_graficas(); // Vuelve a asociar las entidades lógicas con las entidades gráficas

	  mi_tablero.fijar_jugador(mi_nivel.get_fila_inicial_jugador(),
	  mi_nivel.get_columna_inicial_jugador());
	  mi_ventana.contadorMovimientos.setText("Movimientos: " + movimientos);
	  jugador_actual.sumar_puntos_del_nivel();
	  mi_ventana.contadorPuntaje.setText("Puntaje: " + jugador_actual.get_puntaje_acumulado());
	  
	  mi_ventana.repaint();
	  
	  mi_ventana.bloquearNivelesAnteriores();
  }
  
  
  public int nivelActual() {
	  return mi_nivel.get_nro_nivel();
  }

	public void setGenerador(AbstractFactory generador) {
		skin = generador;
	}
	
	public AbstractFactory getGenerador() {
		return skin;
	}

	public void notificar_perder() {
		mi_ventana.notificar_perder();
	}

	public void notificar_existencia(TdP2 tdp2) {
		mi_ventana.notificar_existencia(tdp2);
	}

	public void notificar_desuscripcion(int posicion) {
		mi_ventana.notificar_desuscripcion(posicion);
	}


	public void notificar_sumar_puntaje(int puntaje_a_sumar) {
		mi_ventana.actualizar_puntaje(puntaje_a_sumar);
	}

	public Jugador get_jugador_actual() {
		return jugador_actual;
	}

	public void set_jugador_actual(Jugador jugador_actual) {
		this.jugador_actual = jugador_actual;
	}
	

	public void set_top_jugadores(TopJugadores ranking) {
		this.ranking = ranking;
	}

	public void notificar_regla_match() {
		mi_tablero.notificar_regla_match(mi_nivel.get_regla_de_match());
		
	}

}