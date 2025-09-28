package entidades;

import logica.Objetivo;
import logica.Tablero;

/**
 * Modela el comportamiento de los Caramelos.
 */
public class Caramelo extends Entidad {
	
    public Caramelo(int f, int c, Color col, String path_img, Tablero t) {
      super(f, c, col, path_img, t, calcular_puntaje(col));
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
	public boolean puede_recibir(Glaseado g) {
		return false;
	}
	
    @Override
	public boolean puede_recibir(Potenciador p) {
		return true;
	}
	
    @Override
	public boolean puede_recibir(TdP1 tdp1) {
	    return true;
	}

    @Override
	public boolean puede_recibir(TdP2 tdp2) {
	    return true;
	}
    
	private static int calcular_puntaje(Color color) {
      int puntaje = 0;
      switch (color) {
          case VERDE: puntaje = 10; break;
          case AMARILLO: puntaje = 15; break;
          case AZUL: puntaje = 20; break;
          case VIOLETA: puntaje = 25; break;
          case ROSA: puntaje = 5; break;
          default: puntaje = 0;
      }
      return puntaje;
	}
	
	public void chequear_objetivo(Objetivo o) {
		o.chequear_objetivo(this);
	}
}