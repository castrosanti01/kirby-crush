package entidades;

import logica.Objetivo;
import logica.Tablero;

/**
 * Modela el comportamiento de los Envueltos.
 */
public class Envuelto extends Potenciador {
    
    protected static final int PUNTAJE = 50;
	
    public Envuelto(int f, int c, Color col, String img_path, Tablero t) {
		super(f, c, col, img_path, t, PUNTAJE);
	}
    
    public void chequear_objetivo(Objetivo o) {
		o.chequear_objetivo(this);
	}
	
	@Override
	public boolean es_posible_intercambiar(Entidad e) {
		return e.puede_recibir(this);
	}

	@Override
	public boolean puede_recibir(Caramelo c) {
		return true; 
	}

	@Override
	public boolean puede_recibir(Potenciador p) {
		return true;
	}
	
	@Override
	public void detonar() {
		mi_tablero.detonar(this);
	}
	
	public void detonar_especial() {
    	super.detonar();
    }
	
}