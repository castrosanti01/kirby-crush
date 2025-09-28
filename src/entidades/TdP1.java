package entidades;

import logica.Objetivo;
import logica.Tablero;

/**
 * Modela el comportamiento del caramelo TdP1.
 * 
 * Efecto: eliminar la fila y columna completa de caramelos en la que se encuentra. 
 * 
 * Detonación: además de detonar ante cualquier match de 3 o más según las reglas del 
 * juego, el caramelo detona cuando es intercambiado con cualquier otro caramelo de su 
 * mismo color, sin la necesidad de que el intercambio de lugar a un match 3.
 * 
 * Formación: este tipo de caramelo puede ingresar como cualquier otro caramelo al tablero, 
 * esto es, porque forma parte del nivel, o porque reemplaza a un caramelo anterior ante la 
 * detonación del mismo.
 *
 */
public class TdP1 extends Entidad {
  
  protected static final int PUNTAJE = 100;
  
  public TdP1(int f, int c, Color col, String img_path, Tablero t) {
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
  
  @Override
  public void detonar() {
	  mi_tablero.detonar(this);
  }
  
  public void detonar_especial() {
  	super.detonar();
  }
  
  public boolean detona_con_swap() {
		return true;
	}

}
