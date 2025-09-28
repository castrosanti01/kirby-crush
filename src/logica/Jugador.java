package logica;

import java.io.Serializable;

public class Jugador implements Comparable<Jugador>, Serializable{
	private String jugador;
	
    /**
     * Declaro los atributos como Integer para poder implementar compareTo
     */
	private Integer puntaje_nivel_actual;
	private Integer puntaje_acumulado;
	
	/**
	 * Declaro el serialVersionUID para asegurar la compatibilidad de versiones al serializar
	 * y deserializar objetos de esta clase. El valor 1L es utilizado como identificador único
	 * y controlado manualmente. Esto evita errores al cerrar y abrir el programa, garantizando
	 * que el número de serie de versión sea constante incluso si se realizan cambios en la clase.
	 * Este enfoque ayuda a prevenir problemas de serialización al mantener un valor consistente
	 * a través de diferentes versiones del código.
	 */
	private static final long serialVersionUID = 1L; 

	public Jugador(String jugador, Integer puntaje_acumulado) {
		this.jugador = jugador;
		this.puntaje_acumulado = puntaje_acumulado;
	}
	
	public Jugador() {
		this.jugador = null;
		this.puntaje_acumulado = 0;
		this.puntaje_nivel_actual = 0;
	}

	public String get_jugador() {
		return this.jugador;
	}
	
	public Integer get_puntaje_acumulado() {
		return this.puntaje_acumulado;
	}
	
	public Integer get_puntaje_nivel_actual() {
		return this.puntaje_nivel_actual;
	}
	
	public void set_nombre(String nombre) {
		this.jugador = nombre;
	}
	
    public void actualizar_puntaje_nivel_actual(int puntos) {
        if (puntos > 0) puntaje_nivel_actual += puntos;
    }

    public void sumar_puntos_del_nivel() {
    	puntaje_acumulado += puntaje_nivel_actual;
    	puntaje_nivel_actual = 0;
    }
    
    public void resetear_puntaje() {
    	puntaje_nivel_actual = 0;
    }
    
	@Override
	public int compareTo(Jugador otroJugador) {
		return this.puntaje_acumulado.compareTo(otroJugador.get_puntaje_acumulado());
	}
}
