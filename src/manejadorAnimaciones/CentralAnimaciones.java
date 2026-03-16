package manejadorAnimaciones;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import GUI.Celda;
import GUI.Ventana;
import GUI.VentanaNotificable;
import animadores.Animador;
import animadores.AnimadorCaida;
import animadores.AnimadorCambioFoco;
import animadores.AnimadorCreacionConDelay;
import animadores.AnimadorDetonacion;
import animadores.AnimadorMovimiento;
import animadores.PrioridadAnimaciones;

public class CentralAnimaciones implements ManejadorAnimaciones{
	
	protected VentanaNotificable ventana; 
	protected List<Animador> animadores_pendientes;
	protected HashMap<Celda, List<Animador>> mapeo_celda_animadores;
	protected int prioridad_ultimo_animador_lanzado;
	protected int cantidad_animadores_ultima_prioridad_lanzados;
	// Fase actual de animaciones: para asegurar que no se mezclen detonaciones con caídas
	protected int fase_actual;
	protected static final int FASE_LIBRE = 0;
	protected static final int FASE_DETONACION = 1016;
	protected static final int FASE_CAIDA = 1017;
	
	
	public CentralAnimaciones(Ventana ventana) {
		this.ventana = ventana;
		animadores_pendientes = new LinkedList<Animador>();
		mapeo_celda_animadores = new HashMap<Celda, List<Animador>>();
		prioridad_ultimo_animador_lanzado = PrioridadAnimaciones.PRIORIDAD_SIN_PRIORIDAD;
		cantidad_animadores_ultima_prioridad_lanzados = 0;
		fase_actual = FASE_LIBRE;
	}
	
	public void animar_intercambio(Celda celda) {
		Animador animador = new AnimadorMovimiento(this, 10, 50, celda);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	public void animar_cambio_foco(Celda celda) {
		Animador animador = new AnimadorCambioFoco(this, celda);
		agregar_animador_y_lanzar_pendientes(animador);
	}

	public void animar_detonacion(Celda celda) {
		Animador animador = new AnimadorDetonacion(this, celda, 400);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	public void animar_caida(Celda celda) {
		Animador animador = new AnimadorCaida(this, 10, 25, celda);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	public void animar_creacion_con_delay(Celda celda) {
		Animador animador = new AnimadorCreacionConDelay(this, celda, 400);
		agregar_animador_y_lanzar_pendientes(animador);
	}
	
	protected void agregar_animador_y_lanzar_pendientes(Animador animador) {
		ventana.notificarse_animacion_en_progreso();
		animadores_pendientes.add(animador);
		lanzar_pendientes();
	}
	
	protected void lanzar_pendientes() {
		Animador animador;
		int prioridad_animador;
		
		while(existen_pendientes() && existe_proxima_animacion_preparada()) {
			animador = animadores_pendientes.remove(0);
			prioridad_animador = animador.get_prioridad();
			agregar_animador_en_progreso(animador);
			incrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado(prioridad_animador);
			animador.comenzar_animacion();
		}
	}
	
	protected void agregar_animador_en_progreso(Animador animador) {
		Celda celda_animador = animador.get_celda_asociada();
		List<Animador> animadores_celda = mapeo_celda_animadores.get(celda_animador);
		if (animadores_celda != null) {
			animadores_celda.add(animador);
		}else {
			animadores_celda = new LinkedList<Animador>();
			animadores_celda.add(animador);
			mapeo_celda_animadores.put(celda_animador, animadores_celda);
		}
	}
	
	protected void incrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado(int prioridad_ultimo) {
		prioridad_ultimo_animador_lanzado = prioridad_ultimo;
		cantidad_animadores_ultima_prioridad_lanzados ++;
		// Establecer la fase actual
		if (prioridad_ultimo == PrioridadAnimaciones.PRIORIDAD_DETONACION) {
			fase_actual = FASE_DETONACION;
		} else if (prioridad_ultimo == PrioridadAnimaciones.PRIORIDAD_CAIDA) {
			fase_actual = FASE_CAIDA;
		}
	}
	
	protected boolean existen_pendientes() {
		return ! animadores_pendientes.isEmpty();
	}
	
	protected boolean existe_proxima_animacion_preparada() {
		boolean retorno = false;
		
		if (existen_animadores_activos()) {
			retorno = existe_preparada_con_animadores_activos();
		}else {
			retorno = existe_preparada_sin_animadores_activos();
		}
		return retorno;
	}
	
	protected boolean existen_animadores_activos() {
		return cantidad_animadores_ultima_prioridad_lanzados != 0;
	}
	
	protected boolean existe_preparada_con_animadores_activos() {
		Animador animador_en_tope_espera;
		Celda celda_animador;
		int prioridad_animador;
		boolean retorno = false;
		
		if (existen_pendientes()) {
			animador_en_tope_espera = animadores_pendientes.get(0);
			celda_animador = animador_en_tope_espera.get_celda_asociada();
			prioridad_animador = animador_en_tope_espera.get_prioridad();
			
			// Verificar que la celda está libre
			boolean celda_libre = ! tiene_animadores_en_progreso(celda_animador);
			
			// Verificar que tiene IGUAL prioridad que el último lanzado
			boolean misma_prioridad = tiene_igual_prioridad_que_ultimo_animador_lanzado(prioridad_animador);
			
			// CRÍTICO: No permitir cambio de fase si hay animadores activos
			// Si intenta cambiar de prioridad mientras hay activos, NO se ejecuta
			if (misma_prioridad && celda_libre) {
				retorno = true;
			}
			// Si es diferente prioridad, solo permitir si NO hay animadores activos
			else if (!misma_prioridad && cantidad_animadores_ultima_prioridad_lanzados == 0 && celda_libre) {
				retorno = true;
			}
		}
		return retorno;
	}
	
	protected boolean existe_preparada_sin_animadores_activos() {
		return existen_pendientes();
	}

	protected boolean tiene_animadores_en_progreso(Celda celda) {
		boolean retorno = false;
		List<Animador> animadores_celda_pendientes = mapeo_celda_animadores.get(celda);
		if (animadores_celda_pendientes != null) {
			retorno = ! animadores_celda_pendientes.isEmpty();
		}
		return retorno;
	}
	
	protected boolean tiene_igual_prioridad_que_ultimo_animador_lanzado(int prioridad) {
		return prioridad == prioridad_ultimo_animador_lanzado;
	}
	
	public void notificarse_finalizacion_animador(Animador animador) {
		ventana.notificarse_animacion_finalizada();
		quitar_animador_en_progreso(animador);
		decrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado();
		lanzar_pendientes();
	}
	
	protected void quitar_animador_en_progreso(Animador animador) {
		Celda celda_animador = animador.get_celda_asociada();
		List<Animador> animadores_celda = mapeo_celda_animadores.get(celda_animador);
		animadores_celda.remove(animador);
	}
	
	protected void decrementar_animadores_activos_y_actualizar_ultima_prioridad_lanzado() {
		cantidad_animadores_ultima_prioridad_lanzados --;
		if (cantidad_animadores_ultima_prioridad_lanzados == 0) {
			// Cuando termina una fase, resetear para permitir la siguiente
			prioridad_ultimo_animador_lanzado = PrioridadAnimaciones.PRIORIDAD_SIN_PRIORIDAD;
			fase_actual = FASE_LIBRE;
		}
	}
}
