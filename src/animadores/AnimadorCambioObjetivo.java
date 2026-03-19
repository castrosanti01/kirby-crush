package animadores;

import javax.swing.JLabel;

import GUI.Celda;
import manejadorAnimaciones.CentralAnimaciones;

public class AnimadorCambioObjetivo implements Animador {

	protected CentralAnimaciones manager;
	protected Celda celda_animada;
	protected int prioridad;
	protected String path_imagen_estado;

	protected int cantidad_objetivo;
	protected JLabel contador;
	
	public AnimadorCambioObjetivo(CentralAnimaciones manager, JLabel cont, int i) {
		this.manager = manager;
		this.contador = cont;
		this.cantidad_objetivo = i;
		this.prioridad = PrioridadAnimaciones.PRIORIDAD_CAMBIO_OBJETIVO;
	}

	public Celda get_celda_asociada() {
		return celda_animada;
	}
	
	public int get_prioridad() {
		return prioridad;
	}

	public void comenzar_animacion() {
		contador.setText(String.valueOf(cantidad_objetivo));
		manager.notificarse_finalizacion_animador(this);
	}
}
