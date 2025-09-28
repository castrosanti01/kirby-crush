package factories;

import entidades.Color;
import entidades.Entidad;
import entidades.Glaseado;
import logica.Tablero;

public interface AbstractFactory {
	
	public Entidad crearCaramelo(int fila, int columna, Color color, Tablero t); 
	
	public Glaseado crearGlaseado(int fila, int columna, Color color, Tablero t);
	
	public String toString();

	public Entidad crearTdP2(int filaActual, int j, Color color, Tablero tablero);

	public Entidad crearTdP1(int filaActual, int j, Color color, Tablero tablero);
}
