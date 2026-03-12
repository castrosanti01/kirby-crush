package factories;

import entidades.Color;
import entidades.Entidad;
import logica.Tablero;

public interface AbstractFactory {
	
	public Entidad crearCaramelo(int fila, int columna, Color color, Tablero t); 
	
	public String toString();

}
