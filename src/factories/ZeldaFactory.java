package factories;

import entidades.Caramelo;
import entidades.Color;
import entidades.Envuelto;
import entidades.Glaseado;
import entidades.TdP1;
import entidades.TdP2;
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

	@Override
	public Glaseado crearGlaseado(int fila, int columna, Color color, Tablero t) {
		return new Glaseado(fila, columna, color,"/imagenes/glaseados/Zelda-", t);
	}
	
	public TdP1 crearTdP1(int fila, int columna, Color color, Tablero t) {
		return new TdP1(fila, columna, color, "/imagenes/tdp1/Zelda-", t);
	}
	
	public TdP2 crearTdP2(int fila, int columna, Color color, Tablero t) {
		TdP2 nuevo = null;

		if (color == Color.VERDE)
			nuevo = new TdP2(fila, columna, color, "/imagenes/tdp2/verde/", t);
		if (color == Color.AMARILLO)
			nuevo = new TdP2(fila, columna, color, "/imagenes/tdp2/amarillo/", t);
		if (color == Color.AZUL)
			nuevo = new TdP2(fila, columna, color, "/imagenes/tdp2/azul/", t);
		if (color == Color.VIOLETA)
			nuevo = new TdP2(fila, columna, color, "/imagenes/tdp2/violeta/", t);
		if (color == Color.ROSA)
			nuevo = new TdP2(fila, columna, color, "/imagenes/tdp2/rosa/", t);

		return nuevo;
	}
	
	public Envuelto crearEnvuelto(int fila, int columna, Color color, Tablero t) {
		return new Envuelto(fila, columna, color, "/imagenes/envuelto/Zelda-", t);
	}

}