package logica;

/**
 * Define los mensaje posibles de solicitar por sobre las entidades lógicas de la aplicación.
 */
public interface EntidadLogica {
	/**
	 * Obtiene la fila en la que se ubica la entidad lógica.
	 * @return el valor de la fila.
	 */
	public int get_fila();
	/**
	 * Obtiene la columna en la que se ubica la entidad lógica.
	 * @return el valor de la columna.
	 */
	public int get_columna();
	/**
	 * Retorna el puntaje asociado a la entidad.
	 * @return El puntaje asociado a la entidad.
	 */
	public int get_puntaje();
	/**
	 * Obtiene la ruta donde se encuentra la imagen representativa de la entidad, en relación a su estado.
	 * @return la ruta hacia la imagen.
	 */
	public String get_imagen_representativa();
	
}
