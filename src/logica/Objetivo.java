package logica;

import entidades.Caramelo;
import entidades.Entidad;
import entidades.Envuelto;
import entidades.Gelatina;
import entidades.Glaseado;
import entidades.RayadoHorizontal;
import entidades.RayadoVertical;
import entidades.TdP1;
import entidades.TdP2;

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
		
	public void chequear_objetivo(Glaseado g){
		if (g.get_image_path().equals(img_path))
			if (cantidad > 0)
					cantidad--;
	}
	
	public void chequear_objetivo(Gelatina gel){
		if (gel.get_image_path().equals(img_path)) 
			if (cantidad > 0) {
					cantidad--;
			}
	}
	
	public void chequear_objetivo(TdP1 t) {
	}
	
	public void chequear_objetivo(TdP2 t) {
	}
	
	public void chequear_objetivo(RayadoVertical t) {
	}
	
	public void chequear_objetivo(RayadoHorizontal t) {
	}
	
	public void chequear_objetivo(Envuelto t) {
	}
	
}