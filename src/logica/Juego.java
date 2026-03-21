package logica;

import java.awt.EventQueue;
import java.util.List;
import entidades.Entidad;
import entidades.Potenciador;
import entidades.Color;
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
	protected Jugador jugador_actual;  
	protected int movimientos;
	protected int tiempo_restante;
	protected int contador_puntos;
	protected boolean nivel_ganado;
	public int vidas = 3;

	public Juego() {
		mi_tablero = new Tablero(this);
		mi_ventana = new Ventana(this);
		ranking = new TopJugadores();
		jugador_actual = new Jugador();
	}

	public void cargarDatos(int nivel, AbstractFactory generador) {
		String nivelPath = "/niveles/" + nivel + "-nivel.txt";
		mi_nivel = GeneradorNivel.cargar_nivel_y_tablero(getClass().getResourceAsStream(nivelPath), generador, mi_tablero);
		skin = generador;
		nivel_ganado = false;
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

	private void asociar_entidades_logicas_graficas() {
		Entidad e;
		EntidadGrafica eg;

		for (int f = 0; f < mi_tablero.get_filas(); f++) {
			for (int c = 0; c < mi_tablero.get_columnas(); c++) {
				e = mi_tablero.get_entidad(f, c);
				eg = mi_ventana.agregar_entidad(e);
				e.set_entidad_grafica(eg);
			}
		}
		mi_ventana.setLocationRelativeTo(null);
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

	public void notificarMovimiento() {
		movimientos--;
		mi_ventana.contadorMovimientos.setText("Movimientos: " + movimientos);
		if (movimientos == 0)
			mi_ventana.notificar_perder();
	}

	public void set_movimientos() {
		movimientos = mi_nivel.get_movimientos();
	}

	public int get_movimientos() {
		return movimientos;
	}

	public void set_tiempo_restante() {
		tiempo_restante = mi_nivel.get_tiempo_restante();
	}

	public int get_tiempo_restante() {
		return tiempo_restante;
	}

	public int getVidas() {
		return vidas;
	}

	public void chequeo_nivel() { 
		boolean objetivosTerminados = true;

		for (Objetivo objetivo : manager_objetivos.get_lista()) {
			if (objetivo.get_cantidad() > 0) {
				objetivosTerminados = false;
				break;
			}
		}

		if (objetivosTerminados && !nivel_ganado) {
			mi_ventana.ganarNivel();
			nivel_ganado = true;
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

	public void sumarPuntos(){
		jugador_actual.sumar_puntos_del_nivel();
		mi_ventana.contadorPuntaje.setText("Puntaje: " + jugador_actual.get_puntaje_acumulado());
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

	public void notificar_desuscripcion(Color color) {
		mi_ventana.notificar_desuscripcion(color);
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

}