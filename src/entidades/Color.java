package entidades;

import java.util.Random;

public enum Color {
	ROJO, VERDE, AMARILLO, AZUL, VIOLETA, ROSA, EMPTY;
	
	public static Color color_random() {
		int desde = 1; 
		int hasta = 5; 
		Random random = new Random();
		int numeroAleatorio = random.nextInt(hasta - desde + 1) + desde;
		return Color.values()[numeroAleatorio];
	}
}
