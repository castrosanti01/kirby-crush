package GUI;

/**
 * Define las operaciones esperables por sobre una ventana que permite animar los cambios de sus entidades.
 * Un ventana animable podrá llevar adelante la animación de movimiento o de cambio de estado, de una entidad.
 */
public interface VentanaAnimable {
	
	/**
	 * Solicita que se anime el movimiento entre la posición actual de una celda, y la posición nueva que tendrá a partir de una 
	 * modificación de la posición de la entidad lógica asociada.
	 * @param c La celda que debe modificar su posición, de forma animada.
	 */
	public void animar_movimiento(Celda c);
	
	/**
	 * Solicita que se anime el cambio de estado entre la imagen actual de una celda, y la nueva imagen que tendrá a partir de una 
	 * modificación del estado de la entidad lógica asociada.
	 * @param c La celda que debe modificar su imagen asociada, de forma animada.
	 */
	public void animar_cambio_estado(Celda c);
	
	/**
	 * Solicita que se anime la detonacion de una celda
	 * @param c La celda que debe detonar, de forma animada.
	 */
	public void animar_detonacion(Celda celda);
	
	/**
	 * Solicita que se anime la caida de una celda
	 * @param c La celda que debe caer, de forma animada.
	 */
	public void animar_caida(Celda celda);
	
	/**
	 * Solicita que se elemine una celda
	 * @param c La celda que debe eliminarse.
	 */
	public void eliminar_celda(Celda celda);
	
	/**
	 * Solicita que se cree una celda con el tiempo adecuado
	 * @param c La celda que debe crearse.
	 */
	public void animar_creacion_con_delay(Celda celda);

	/**
	 * Solicita que se anime la gravedad de una celda
	 * @param c La celda que debe caer, de forma animada.
	 */
    public void animar_gravedad(Celda celda);

}
