package animadores;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import GUI.Celda;
import manejadorAnimaciones.CentralAnimaciones;

public class AnimadorGanarNivel implements Animador {

	protected CentralAnimaciones manager;
	protected Celda celda_animada;
	protected int prioridad;
	protected String path_imagen_estado;

	protected JLayeredPane panel;
	protected JLabel label;	
	protected JButton boton;

	public AnimadorGanarNivel(CentralAnimaciones manager, JLayeredPane panel, JLabel label, JButton boton) {
        this.manager = manager;
		this.panel = panel;
		this.label = label;
		this.boton = boton;
		this.prioridad = PrioridadAnimaciones.PRIORIDAD_GANAR_NIVEL;
    }

    public Celda get_celda_asociada() {
		return celda_animada;
	}
	
	public int get_prioridad() {
		return prioridad;
	}

	public void comenzar_animacion() {
		panel.add(label, 0);
		panel.add(boton, 0);
		manager.notificarse_finalizacion_animador(this);
	}
}
