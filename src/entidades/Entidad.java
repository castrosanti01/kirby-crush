package entidades;

import GUI.EntidadGrafica;
import logica.EntidadLogica;
import logica.Objetivo;
import logica.Tablero;

/**
 * Generaliza el comportamiento estándar de todas las entidades que forman parte del tablero.
 */
public abstract class Entidad implements EntidadLogica, Enfocable, Intercambiable, Detonable {
	protected int fila;
	protected int columna;
	protected Color color;
	
	protected boolean enfocada;
	protected boolean detonada;
	protected int puntaje;
	protected String img_path;
	
	protected String [] imagenes_representativas;
	protected EntidadGrafica entidad_grafica;
	protected Tablero mi_tablero;
	
	/**
	 * Inicializa el estado interno de una entidad, considerando
	 * @param f La fila donde se ubica la entidad.
	 * @param c La columna donde se ubica la entidad.
	 * @param col El color asociado a la entidad. Se asume constante de la clase Color.
	 * @param path_img Ruta donde se encuentran todas las imágenes asociadas a la entidad creada.
     * @param t El tablero al que pertenece la entidad.
     * @param p El puntaje asociado a la entidad.
	 */
	protected Entidad(int f, int c, Color col, String path_img, Tablero t, int p) {
		fila = f;
		columna = c;
		color = col;
		mi_tablero = t;
		puntaje = p;
		img_path = path_img + col.ordinal()+".png";
		enfocada = false;
		detonada = false;
		cargar_imagenes_representativas(path_img);
	}
	
	/**
	 * Vincula el elemento con su entidad gráfica asociada.
	 * @param e Entidad gráfica que se encuentra asociada al elemento.
	 */
	public void set_entidad_grafica(EntidadGrafica e) {
		entidad_grafica = e;
	}
	
	@Override
	public int get_fila() {
		return fila;
	}
	
	@Override
	public int get_columna() {
		return columna;
	}
	
	public String get_image_path() {
		return img_path;
	}
	
	public boolean get_detonada() {
		return detonada;
	}
	
	/**
	 * Retorna el color asociado a la entidad.
	 * @return Constante numérica que representa el color de la entidad. Se asume un valor declarado en clase Color.
	 */
	public Color get_color() {
		return color;
	}
	
	@Override
	public int get_puntaje() {
	    return puntaje;
	}
	
	@Override
	public String get_imagen_representativa() {
		int indice = 0;
		indice += (enfocada ? 1 : 0);
		if(detonada)
			return imagenes_representativas[2];
		return imagenes_representativas[indice];
	}
	
	public void chequear_objetivo(Objetivo o) {
		o.chequear_objetivo(this);
	}
	
	@Override
	public void cambiar_posicion(int nf, int nc) {
		fila = nf;
		columna = nc;
		entidad_grafica.notificarse_cambio_posicion();
	}
	
	@Override
	public void caer(int nf, int nc) {
		fila = nf;
		columna = nc;
		entidad_grafica.notificarse_caida();
	}
	
	@Override
	public void enfocar() {
		enfocada = true;
		entidad_grafica.notificarse_cambio_estado();
	}
	
	@Override
	public void desenfocar() {
		enfocada = false;
		entidad_grafica.notificarse_cambio_estado();
	}
	
	@Override
	public void detonar() {
		detonada = true;
		entidad_grafica.notificarse_detonar();
		mi_tablero.notificar_sumar_puntaje(this.get_puntaje());
	}
	
	/**
	 * Inicializa el arreglo de paths que establecen las imágenes asociadas a los diferentes estados de la entidad.
	 * @param path_img Ruta donde se encuentran todas las imágenes asociadas a la entidad creada.
	 */
	protected void cargar_imagenes_representativas(String path_img) {
		imagenes_representativas = new String [4];
		imagenes_representativas[0] = path_img + color.ordinal() +".png";
		imagenes_representativas[1] = path_img + color.ordinal() +"-resaltado.png";
		imagenes_representativas[2] = path_img + color.ordinal() +"-detonado.gif";
		imagenes_representativas[3] = path_img + color.ordinal() +"-enfocado-detonado.png";
	}

}