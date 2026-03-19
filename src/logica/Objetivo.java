package logica;

import entidades.Caramelo;
import entidades.Color;
import entidades.Entidad;
import entidades.Envuelto;
import entidades.RayadoHorizontal;
import entidades.RayadoVertical;

public class Objetivo {
	protected int cantidad;
	protected String img_path;
	protected Color color;
	
	public Objetivo(int c) {
		cantidad = c;
	}
	
	public Objetivo(String path , int cant, Color c) {
		img_path = path;
		cantidad = cant;
		color = c;
	}
	
	public int get_cantidad() {
		return cantidad;
	}

	public Color get_color() {
		return color;
	}
	
	public String get_image_path() {
		return img_path;
	}
	
	public void chequear_objetivo(Entidad e) {
		e.chequear_objetivo(this);
	}
	
	public void chequear_objetivo(Caramelo c) {
		//si el caramelo que se chequea es del tipo que corresponde al objetivo, se reduce la cantidad del objetivo
		if (c.get_color() == color) 
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