package entidades;

/**
 * Define las operaciones esperables por sobre las entidades enfocables.
 */
public interface Enfocable {
	/**
	 * Fija el foco sobre el elemento que recibe el mensaje.
 	 * Notifica a la entidad gráfica del cambio de estado.
	 */
	public void enfocar();
	/**
	 * Quita el foco sobre el elemento que recibe el mensaje.
 	 * Notifica a la entidad gráfica del cambio de estado.
	 */
	public void desenfocar();
}
