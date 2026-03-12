package logica;

import entidades.Caramelo;
import entidades.Entidad;
import entidades.Envuelto;
import entidades.RayadoHorizontal;
import entidades.RayadoVertical;

public class Objetivo {
	protected int cantidad;
	protected String img_path;
	
	public Objetivo(int c) {
		cantidad = c;
	}
	
	public Objetivo(String path , int cant) {
		img_path = path;
		cantidad = cant;
	}
	
	public int get_cantidad() {
		return cantidad;
	}
	
	public String get_image_path() {
		return img_path;
	}
	
	public void chequear_objetivo(Entidad e) {
		e.chequear_objetivo(this);
	}
	
	public void chequear_objetivo(Caramelo c) {
		if (c.get_image_path().equals(img_path)) 
			if (cantidad > 0) {
				cantidad--;
			}
	}
		
	public void chequear_objetivo(RayadoVertical t) {
	}
	
	public void chequear_objetivo(RayadoHorizontal t) {
	}
	
	public void chequear_objetivo(Envuelto t) {
	}
	
}