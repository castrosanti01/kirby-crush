package animadores;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import GUI.Celda;
import manejadorAnimaciones.CentralAnimaciones;

public class AnimadorCreacionConDelay extends Thread implements Animador {

	protected CentralAnimaciones manager;
	protected Celda celda_animada;
	protected int delay;
	protected int prioridad;
	protected String path_imagen_estado;
	
	public AnimadorCreacionConDelay(CentralAnimaciones manager, Celda celda, int delay) {
		this.manager = manager;
		this.celda_animada = celda;
		this.delay = delay;
		this.prioridad = PrioridadAnimaciones.PRIORIDAD_DETONACION;
		path_imagen_estado = celda.get_entidad_logica().get_imagen_representativa();
	}
	
	public Celda get_celda_asociada() {
		return celda_animada;
	}
	
	public int get_prioridad() {
		return prioridad;
	}

	public void comenzar_animacion() {
		this.start();
	}
	
	public void run() {
		int size_label = celda_animada.get_size_label();
		ImageIcon imgIcon = new ImageIcon(this.getClass().getResource("/imagenes/caramelos/0.png"));
		Image imgEscalada = imgIcon.getImage().getScaledInstance(size_label, size_label, Image.SCALE_SMOOTH);
		Icon iconoEscalado = new ImageIcon(imgEscalada);
		celda_animada.setIcon(iconoEscalado);
		
		try {
			sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		imgIcon = new ImageIcon(this.getClass().getResource(path_imagen_estado));
		imgEscalada = imgIcon.getImage().getScaledInstance(size_label, size_label, Image.SCALE_SMOOTH);
		iconoEscalado = new ImageIcon(imgEscalada);
		celda_animada.setIcon(iconoEscalado);
		
		manager.notificarse_finalizacion_animador(this);
	}

}
