package entidades;

import logica.Tablero;

/**
 * Modela el comportamiento de los Potenciadores.
 */
public abstract class Potenciador extends Entidad {

	public Potenciador(int f, int c, Color col, String path_img, Tablero t, int p) {
		super(f, c, col, path_img, t, p);
	}
	
}