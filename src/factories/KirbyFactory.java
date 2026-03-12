package factories;

import entidades.Caramelo;
import entidades.Color;
import entidades.Envuelto;
import logica.Tablero;

public class KirbyFactory implements AbstractFactory {

	@Override
	public Caramelo crearCaramelo(int fila, int columna, Color color, Tablero t) {
		return new Caramelo(fila, columna, color,"/imagenes/caramelos/Kirby-", t);
	}
	
	public Envuelto crearEnvuelto(int fila, int columna, Color color, Tablero t) {
		return new Envuelto(fila, columna, color, "/imagenes/envuelto/Kirby-", t);
	}
	
	@Override
	public String toString() {
		return "Kirby";
	}

}