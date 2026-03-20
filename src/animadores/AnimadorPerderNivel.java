package animadores;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import GUI.Celda;
import manejadorAnimaciones.CentralAnimaciones;

public class AnimadorPerderNivel implements Animador {

	protected CentralAnimaciones manager;
	protected Celda celda_animada;
	protected int prioridad;
	protected String path_imagen_estado;

	protected JLayeredPane panel;
	protected JLabel label;	
	protected JButton boton1, boton2;

	public AnimadorPerderNivel(CentralAnimaciones manager, JLayeredPane panel, JLabel label, JButton boton1, JButton boton2) {
        this.manager = manager;
		this.panel = panel;
		this.label = label;
		this.boton1 = boton1;
		this.boton2 = boton2;
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
		panel.add(boton1, 0);
		panel.add(boton2, 0);
		manager.notificarse_finalizacion_animador(this);
	}
}
