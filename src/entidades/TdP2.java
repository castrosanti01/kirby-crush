package entidades;

import logica.Objetivo;
import logica.Tablero;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modela el comportamiento del caramelo TdP2.
 *
 * Efecto: finaliza la partida del jugador, independientemente de la cantidad de vidas. Este
 * efecto se origina después de una X cantidad de segundos que transcurren de forma
 * descendente y se indican mediante la imagen asociada al caramelo especial.
 *
 * Detonación: la detonación se da en el mismo sentido que los caramelos Glaseados, esto
 * es, en consecuencia de la detonación caramelo adyacente que no es un Glaseado.
 *
 * Formación: este tipo de caramelos forman parte únicamente de algunos niveles, que
 * pueden considerarse desafíos, donde el tablero se conforma de manera tal de, desafiar al
 * jugador a desactivar los caramelos TdP-2 presentes, antes de perder. Este tipo de
 * caramelo, por ejemplo, puede pensarse para niveles especiales y en consonancia con la
 * cantidad de movimientos disponibles.
 */
public class TdP2 extends Entidad {

    public static final int PUNTAJE = 150;
    private int contador;
    private Timer timer;
    private String img_path;

    public TdP2(int f, int c, Color col, String img_path, Tablero t) {
        super(f, c, col, img_path, t, PUNTAJE);
        contador = 15;
        notificar_existencia();
    }
    
    public Timer get_timer() {
		return timer;
    }
    
    @Override
    public String get_imagen_representativa() {
        int indice = 0;
        indice += (enfocada ? 1 : 0);
        if (detonada)
            return imagenes_representativas[2];
        return imagenes_representativas[indice];
    }
    
    public boolean tiene_gravedad() {
        return true;
    }
   
    private void notificar_existencia() {
    	mi_tablero.notificar_existencia(this);
	}
    
	public void chequear_objetivo(Objetivo o) {
		o.chequear_objetivo(this);
	}

    public void iniciarTemporizador() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    actualizarImagen();
                });
                if (contador <= 1) {
                    timer.stop();
                    mi_tablero.notificar_perder();
                }
                contador--;
            }
        });
        timer.start();
    }

    private void actualizarImagen() {
        actualizar_imagen(contador);
    }
    
    private String color_en_string() {
    	String toReturn = null;
    	if (color == Color.VERDE)
    		toReturn = "verde";
    	if (color == Color.AMARILLO)
    		toReturn = "amarillo";
    	if (color == Color.AZUL)
    		toReturn = "azul";
    	if (color == Color.VIOLETA)
    		toReturn = "violeta";
    	if (color == Color.ROSA)
    		toReturn = "rosa";
    	return toReturn;
    		
    }

    private void actualizar_imagen(int cont) {
    	if(cont > 0) {
    		cargar_imagenes_representativas("/imagenes/tdp2/" + color_en_string() +"/tdp"+ cont + "/");
        	entidad_grafica.notificarse_cambio_estado();
        }
    }

    @Override
    public void detonar() {
        timer.stop();
        entidad_grafica.notificarse_detonar();
        mi_tablero.detonar(this);
    }
    
    public void detonar_especial() {
    	super.detonar();
    }
    
    @Override
    public boolean es_posible_intercambiar(Entidad e) {
        return e.puede_recibir(this);
    }
    
	@Override
	public boolean puede_recibir(Caramelo c) {
		return true;
	}

	@Override
	public boolean puede_recibir(Glaseado g) {
		return false;
	}

	@Override
	public boolean puede_recibir(Potenciador p) {
		return false;
	}

	@Override
	public boolean puede_recibir(TdP1 tdp) {
		return false;
	}

	@Override
	public boolean puede_recibir(TdP2 tdp) {
		return false;
	}
	
	public String get_image_path() {
		return img_path;
	}
}
