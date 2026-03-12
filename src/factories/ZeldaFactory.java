package factories;

import entidades.Caramelo;
import entidades.Color;
import entidades.Envuelto;
import logica.Tablero;

public class ZeldaFactory implements AbstractFactory {

	@Override
	public Caramelo crearCaramelo(int fila, int columna, Color color, Tablero t) {
		return new Caramelo(fila, columna, color,"/imagenes/caramelos/Zelda-", t);
	}
	
	@Override
	public String toString() {
		return "Zelda";
	}

	public Envuelto crearEnvuelto(int fila, int columna, Color color, Tablero t) {
		return new Envuelto(fila, columna, color, "/imagenes/envuelto/Zelda-", t);
	}

}