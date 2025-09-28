package entidades;

import GUI.EntidadGrafica;
import logica.EntidadLogica;
import logica.Objetivo;

/**
 * Modela el comportamiento de las Gelatinas.
 */
public class Gelatina implements EntidadLogica, Detonable {
	protected int fila;
	protected int columna;
	protected boolean detonada;
	protected int puntaje;
	protected String [] imagenes_representativas;
	protected String ImagePath = "/imagenes/gelatina/7.png" ;
	protected EntidadGrafica entidad_grafica;
	
	public Gelatina(int f, int c) {
		fila = f;
		columna = c;
		detonada = false;
		puntaje = 10;
		cargar_imagenes_representativas();
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
	
	@Override
	public int get_puntaje() {
	    return puntaje;
	}
	
	public String get_image_path() {
		return ImagePath;
	}
	
	@Override
	public String get_imagen_representativa() {
		int indice = 0;
		indice += (detonada ? 1 : 0);
		return imagenes_representativas[indice];
	}
	
	public void chequear_objetivo(Objetivo o) {
		o.chequear_objetivo(this);
	}
	
	@Override
	public void detonar() {
		detonada = true;
		entidad_grafica.notificarse_detonar();
	}
	
	/**
	 * Inicializa el arreglo de paths que establecen las imágenes asociadas a los diferentes estados de la entidad.
	 * @param path_img Ruta donde se encuentran todas las imágenes asociadas a la entidad creada.
	 */
	protected void cargar_imagenes_representativas() {
		imagenes_representativas = new String [2];
		imagenes_representativas[0] = "/imagenes/gelatina/7.png";
		imagenes_representativas[1] = "/imagenes/gelatina/7-detonado.gif";
	}

}