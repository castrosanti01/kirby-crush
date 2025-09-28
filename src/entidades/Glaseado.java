package entidades;

import logica.Objetivo;
import logica.Tablero;

/**
 * Modela el comportamiento de los Glaseados.
 */
public class Glaseado extends Entidad {
	protected String ImagePath;
    protected static final int PUNTAJE = 25;
	
    public Glaseado(int f, int c, Color col, String img_path, Tablero t) {
		super(f, c, col, img_path, t, PUNTAJE);
		ImagePath = img_path + "6.png";
	}
    
    public String get_image_path() {
		return ImagePath;
	}
    
    public boolean tiene_gravedad() {
    	return false;
    }
	
	public void chequear_objetivo(Objetivo o) {
		o.chequear_objetivo(this);
	}
	
    @Override
	public boolean es_posible_intercambiar(Entidad e) {
		return false;
	}
	
    @Override
	public boolean puede_recibir(Caramelo c) {
		return false;
	}
	
    @Override
	public boolean puede_recibir(Glaseado g) {
		return false;
	}
	
    @Override
	public boolean puede_recibir(Potenciador p) {
		return false;
	}

    @Override
	public boolean puede_recibir(TdP1 tdp1) {
	    return true;
	}

    @Override
	public boolean puede_recibir(TdP2 tdp2) {
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