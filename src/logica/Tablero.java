package logica;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import entidades.*;

public class Tablero {
	
	protected Juego mi_juego;
	protected Entidad[][] entidades;
	protected ManagerObjetivos manager_objetivos;
	
	protected List<Entidad> entidades_asociadas;
	
	protected int filas;
	protected int columnas;

	protected int pos_f_jugador;
	protected int pos_c_jugador;
	
	public Tablero(Juego j) {
		mi_juego = j;
		filas = 0;
		columnas = 0;
	}

	public int get_filas() {
		return filas;
	}

	public int get_columnas() {
		return columnas;
	}

	public Entidad get_entidad(int f, int c) {
		return entidades[f][c];
	}
	
	public void resetar_tablero(int f, int c) {
		filas = f;
		columnas = c;
		pos_f_jugador = 0;
		pos_c_jugador = 0;
		entidades = new Entidad[f][c];
		entidades_asociadas = new LinkedList<Entidad>();
	}
	
	public void agregar_entidad(Entidad e) {
		entidades[e.get_fila()][e.get_columna()] = e;
	}
	
	public void fijar_jugador(int f, int c) {
		entidades[f][c].enfocar();
		entidades[pos_f_jugador][pos_c_jugador].desenfocar();
    	pos_f_jugador = f;
    	pos_c_jugador = c;
	}

	public void mover_jugador(int d) {
		switch (d) {
			case Juego.ABAJO:{
				mover_jugador_auxiliar(pos_f_jugador + 1, pos_c_jugador);
				break;
			}
			case Juego.ARRIBA: {
				mover_jugador_auxiliar(pos_f_jugador - 1, pos_c_jugador);
				break;
			}
			case Juego.IZQUIERDA: {
				mover_jugador_auxiliar(pos_f_jugador, pos_c_jugador - 1);
				break;
			}
			case Juego.DERECHA: {
				mover_jugador_auxiliar(pos_f_jugador, pos_c_jugador + 1);
				break;
			}
		}
	}

	public void intercambiar_entidades(int d) {
		switch (d) {
			case Juego.ABAJO: {
				intercambiar_auxiliar(pos_f_jugador + 1, pos_c_jugador);
				break;
			}
			case Juego.ARRIBA: {
				intercambiar_auxiliar(pos_f_jugador - 1, pos_c_jugador);
				break;
			}
			case Juego.IZQUIERDA: {
				intercambiar_auxiliar(pos_f_jugador, pos_c_jugador - 1);
				break;
			}
			case Juego.DERECHA: {
				intercambiar_auxiliar(pos_f_jugador, pos_c_jugador + 1);
				break;
			}
		}
	}

    public void reubicar(Entidad e) {
        int nueva_fila = e.get_fila();
        int nueva_columna = e.get_columna();
        entidades[nueva_fila][nueva_columna] = e;
    }
    
	private void mover_jugador_auxiliar(int nf, int nc) {
		if (en_rango(nf, nc)) {
			entidades[nf][nc].enfocar();
			entidades[pos_f_jugador][pos_c_jugador].desenfocar();
			pos_f_jugador = nf;
			pos_c_jugador = nc;
		}
	}

	private void intercambiar_auxiliar(int nf, int nc) {
		int pos_f_anterior = pos_f_jugador;
		int pos_c_anterior = pos_c_jugador;
		
		int diferencia_filas = pos_f_jugador - nf;
		int diferencia_columnas = pos_c_jugador - nc;
		boolean movimiento_vertical = false;
		
		if(diferencia_filas != 0 && diferencia_columnas == 0)
			movimiento_vertical = true;

		if(en_rango(nf, nc)) {
			if(entidades[pos_f_anterior][pos_c_anterior].es_posible_intercambiar(entidades[nf][nc])) {
				// Anima el posible intercambio de entidades
				aplicar_intercambio(pos_f_anterior, pos_c_anterior, nf, nc);

				List<Entidad> eliminar = lista_a_eliminar(nf, nc);
				List<Entidad> eliminar_2 = lista_a_eliminar(pos_f_anterior, pos_c_anterior);
				
				// Si el contador es mayor que 3 eliminamos, sino revertimos el intercambio
				if(eliminar.size() >= 3 | eliminar_2.size() >= 3 ) {
					procesar_eliminacion(eliminar, movimiento_vertical);
					procesar_eliminacion(eliminar_2, movimiento_vertical);
					nf = pos_f_jugador;
				    nc = pos_c_jugador;
					
			        // Bucle refill
			        boolean hay_nuevos_match = true;
			        while(hay_nuevos_match) {
			            
			        	List<Entidad> todas_las_entidades = new ArrayList<>();
			            for(int f = 0; f < filas; f++)
			            	for(int c = 0; c < columnas; c++)
			            		todas_las_entidades.add(entidades[f][c]);
			            
			            hay_nuevos_match = false;
			
			            for(Entidad e : todas_las_entidades) {
			            	eliminar = lista_a_eliminar(e.get_fila(), e.get_columna());
			            	//verificar tambien que no sea un glaseado
			            	if(eliminar.size() > 2) {
			            		procesar_eliminacion(eliminar, movimiento_vertical);	
			            		hay_nuevos_match = true;
				            }
			            }
	   		        }
			        pos_f_jugador = nf;
			        pos_c_jugador = nc;
			        entidades[pos_f_jugador][pos_c_jugador].enfocar();
			        mi_juego.notificarMovimiento();
				}
		        else {
		        	aplicar_intercambio(nf, nc, pos_f_anterior, pos_c_anterior);
				}
           }
        }
	}
	
	private void procesar_eliminacion(List<Entidad> eliminar, boolean movimiento_vertical) {
		/*if(eliminar.size() == 2) 
			for(Entidad e : eliminar) 
				e.detonar();
		else*/ if(eliminar.size() == 3) 
			match_simple(eliminar);
	  	else if(eliminar.size() == 4)
  			match_cuatro(eliminar, movimiento_vertical);
		else
  			match_multiple(eliminar);
	}
	
	private void match_multiple(List<Entidad> eliminar) {
		//Para generar el potenciador envuelto
		if(eliminar.size() > 2) {
			boolean misma_columna = true;
			boolean misma_fila = true;
			int fila_a_comparar = eliminar.get(0).get_fila();
			int columna_a_comparar = eliminar.get(0).get_columna();
			
			for(Entidad e : eliminar) {
				if(e.get_columna() != columna_a_comparar)
					misma_columna = false;
				if(e.get_fila() != fila_a_comparar)
			        misma_fila = false;
			}
			
			//Verfico que tenga forma de L, T, +
			if(!misma_columna && !misma_fila) {
				
				//Eliminacion del que va a ser reeeplazado por el potenciador
			    Entidad entidad_a_cambiar = entidades[eliminar.get(0).get_fila()][eliminar.get(0).get_columna()];
			    int fila_entidad = entidad_a_cambiar.get_fila();
			    int col_entidad = entidad_a_cambiar.get_columna();
			    Color color_entidad = entidad_a_cambiar.get_color();
			    
			    //Notificar entidad destruida
			    List<Objetivo> lista_objetivos = new ArrayList<Objetivo> (manager_objetivos.get_lista());
			    actualizar_objetivos(lista_objetivos, entidad_a_cambiar);
				
			    entidad_a_cambiar.detonar();
			    eliminar.remove(entidad_a_cambiar);
			    
			    //Creacion del nuevo potenciador
			    Potenciador potenciador_a_cambiar;
			    potenciador_a_cambiar = new Envuelto(fila_entidad, col_entidad, color_entidad, "/imagenes/envuelto/" +mi_juego.getGenerador().toString()+"-", this);
			    agregar_entidad(potenciador_a_cambiar);
			    mi_juego.asociar_entidad_logica_grafica_nueva(potenciador_a_cambiar);

			    //Creacion de los caramelos nuevos y detonacion de los viejos
			    detonacion_y_creacion(eliminar);
			}
			else
				match_simple(eliminar);
		}
	}
	
	private void match_cuatro(List<Entidad> eliminar, boolean movimiento_vertical) {
		//Para generar el potenciador rayado
		if(eliminar.size() > 2) {
			
			//Eliminacion del que va a ser reeeplazado por el potenciador
		    Entidad entidad_a_cambiar = entidades[eliminar.get(0).get_fila()][eliminar.get(0).get_columna()];
		    int fila_entidad = entidad_a_cambiar.get_fila();
		    int col_entidad = entidad_a_cambiar.get_columna();
		    Color color_entidad = entidad_a_cambiar.get_color();
		    
		    //Notificar entidad destruida
		    List<Objetivo> lista_objetivos = new ArrayList<Objetivo> (manager_objetivos.get_lista());

			entidad_a_cambiar.detonar();
		    actualizar_objetivos(lista_objetivos, entidad_a_cambiar);
		    eliminar.remove(entidad_a_cambiar);
		    
		    //Creacion del nuevo potenciador
		    Potenciador potenciador_a_cambiar;
		    if(movimiento_vertical)
		    	potenciador_a_cambiar = new RayadoVertical(fila_entidad, col_entidad,  color_entidad,  "/imagenes/rayadoVertical/"+mi_juego.getGenerador().toString()+"-", this);
		    else
		    	potenciador_a_cambiar = new RayadoHorizontal(fila_entidad, col_entidad, color_entidad,  "/imagenes/rayadoHorizontal/"+mi_juego.getGenerador().toString()+"-", this);
		    agregar_entidad(potenciador_a_cambiar);
		    mi_juego.asociar_entidad_logica_grafica_nueva(potenciador_a_cambiar);
		    
		    //Detonacion de entidades a eliminar y creacion de los caramelos nuevos
		    detonacion_y_creacion(eliminar);
		}
  }

	private void match_simple(List<Entidad> eliminar) {
		detonacion_y_creacion(eliminar);
	}
	
	private void detonacion_y_creacion(List<Entidad> eliminar) {
		List<Objetivo> lista_objetivos = new ArrayList<Objetivo> (manager_objetivos.get_lista());
		for(Entidad e : eliminar){
	    	//Notificar entidad destruida
			actualizar_objetivos(lista_objetivos, e);
			e.detonar();
		}
		mi_juego.actualizar_objetivos();
		
		int fila_aux = -1;
		
		for(Entidad e : eliminar) {
			if(entidades[e.get_fila()][e.get_columna()].get_detonada()) {
			    subir_vacio(e.get_fila(), e.get_columna());
			    Entidad nuevo_caramelo;
		        nuevo_caramelo = new Caramelo(e.get_fila(), e.get_columna(), Color.color_random(), "/imagenes/caramelos/" + mi_juego.getGenerador().toString() + "-", this);
		        agregar_entidad(nuevo_caramelo);
		        mi_juego.asociar_entidad_logica_grafica_nueva(nuevo_caramelo, fila_aux);
		        nuevo_caramelo.caer(e.get_fila(), e.get_columna());
		        fila_aux--;
		    }
		}
	}
	
	public void detonar(RayadoVertical potenciador) {
		List<Entidad> eliminar = new ArrayList<>();
		int columna = potenciador.get_columna();
		potenciador.detonar_especial();
		for(int fila = 0; fila < filas; fila++)
			if((!entidades[fila][columna].get_detonada()) && entidades[fila][columna] != potenciador) 
				eliminar.add(entidades[fila][columna]);
		detonacion_y_creacion(eliminar);
	}

	public void detonar(RayadoHorizontal potenciador) {
		List<Entidad> eliminar = new ArrayList<>();
		int fila = potenciador.get_fila();
		potenciador.detonar_especial();
		for(int columna = 0; columna < columnas; columna++)
			if((!entidades[fila][columna].get_detonada()) && entidades[fila][columna] != potenciador) 
				eliminar.add(entidades[fila][columna]);
		detonacion_y_creacion(eliminar);
	}
	
	public void detonar(Envuelto potenciador) {
		List<Entidad> eliminar = new ArrayList<>();
		int fila = potenciador.get_fila();
		int columna = potenciador.get_columna();
		potenciador.detonar_especial();
		
		// Definimos las coordenadas relativas de los elementos circundantes.
		int[][] coordenadas_relativas = {
		    {-1, -1}, {-1, 0}, {-1, 1},
		    {0, -1},           {0, 1},
		    {1, -1},  {1, 0},  {1, 1}
		};
		for(int[] coordenada : coordenadas_relativas){
		    int nueva_fila = fila + coordenada[0];
		    int nueva_columna = columna + coordenada[1];
		    if(nueva_fila >= 0 && nueva_fila < entidades.length && nueva_columna >= 0 && nueva_columna < entidades[0].length)
		    	if(en_rango(nueva_fila, nueva_columna))
			        if(!eliminar.contains(entidades[nueva_fila][nueva_columna]))
			        	eliminar.add(entidades[nueva_fila][nueva_columna]);
		}
		match_simple(eliminar);
	}
	
	private void actualizar_objetivos(List<Objetivo> lista_objetivos, Entidad e) {
		int posicion = 0;
		boolean no_repetir = true;
		for(Objetivo objetivo : lista_objetivos) {
			objetivo.chequear_objetivo(e);
			if(no_repetir && objetivo.get_cantidad() == 0) {
				no_repetir = false;
				mi_juego.notificar_desuscripcion(posicion);
				manager_objetivos.desuscribirse(objetivo);
				mi_juego.chequeo_nivel();
			}
			posicion++;
		}
	}
	
	public void notificar_perder() {
		mi_juego.notificar_perder();
	}
	
	public void notificar_sumar_puntaje(int puntaje_a_sumar) {
		mi_juego.notificar_sumar_puntaje(puntaje_a_sumar);
	}
	
	private void aplicar_intercambio(int af, int ac, int nf, int nc) {
	    Entidad entidad_uno = entidades[af][ac];
	    Entidad entidad_dos = entidades[nf][nc];

	    Entidad entidad_aux = entidad_uno;
        entidad_uno.cambiar_posicion(nf, nc);
        entidad_dos.cambiar_posicion(af, ac);
        entidades[af][ac] = entidad_dos;
        entidades[nf][nc] = entidad_aux;
        pos_f_jugador = nf;
        pos_c_jugador = nc;    
	}
	
	private void aplicar_caida(int af, int ac, int nf, int nc) {
	    Entidad entidad_uno = entidades[af][ac];
	    Entidad entidad_dos = entidades[nf][nc];

        Entidad entidad_aux = entidad_uno;
        entidad_uno.caer(nf, nc);
        entidad_dos.caer(af, ac);
        entidades[af][ac] = entidad_dos;
        entidades[nf][nc] = entidad_aux;
        pos_f_jugador = nf;
        pos_c_jugador = nc;
	}
	
	private void subir_vacio(int f, int c) {
		if(f > 0) {
			aplicar_caida(f, c, f-1, c);
			subir_vacio(f-1,c);
		}
	}

	private List<Entidad> lista_a_eliminar(int f, int c) {
	    // Crear una lista para llevar un registro de las entidades visitadas
	    List<Entidad> entidadesVisitadas = new ArrayList<>();
	    entidadesVisitadas.add(entidades[f][c]);
	
	    // CASO 1 HACIA ARRIBA
	    listaAux(f - 1, c, entidades[f][c].get_color(), entidadesVisitadas, 1);
	
	    // CASO 2 HACIA ABAJO
	    listaAux(f + 1, c, entidades[f][c].get_color(), entidadesVisitadas, 2);
	
	    // Si hay más de 3 también se comprueba si hay match horizontalmente
	    int cantVertical = entidadesVisitadas.size();
	    if (cantVertical >= 3) {
	      // CASO 3 HACIA LA DERECHA
	      listaAux(f, c + 1, entidades[f][c].get_color(), entidadesVisitadas, 3);
	
	      // CASO 4 HACIA LA IZQUIERDA
	      listaAux(f, c - 1, entidades[f][c].get_color(), entidadesVisitadas, 4);
	
	      int cantHorizontal = entidadesVisitadas.size() - cantVertical;
	      if (cantHorizontal < 2) {
	        while (cantHorizontal != 0) {
	          entidadesVisitadas.remove(entidadesVisitadas.size() - 1);
	          cantHorizontal--;
	        }
	      }
	    } else {
	      // Caso contrario empezamos de vuelta solo horizontalmente
	      entidadesVisitadas.clear();
	      listaAux(f, c, entidades[f][c].get_color(), entidadesVisitadas, 3);
	      listaAux(f, c - 1, entidades[f][c].get_color(), entidadesVisitadas, 4);
	    }
	
	    // Devolver el resultado del conteo
	    return entidadesVisitadas;
	}

	private void listaAux(int f, int c, Color color, List<Entidad> entidadesVisitadas, int key) {
	    // Verificar límites de la matriz
	    if (f < 0 || f >= entidades.length || c < 0 || c >= entidades[0].length) {
	    	return; // Fuera de los límites, no hay elementos del mismo color
	    }
	
	    // Obtener la entidad actual
	    Entidad entidadActual = entidades[f][c];
	
	    // Verificar si la entidad actual ya se encuentra en la lista
	    if (entidadesVisitadas.contains(entidadActual)) {
	    	return; // Evitar duplicados
	    }
	
	    // Verificar si el color coincide con el objetivo
	    if (entidadActual.get_color() == color) {
	    	entidadesVisitadas.add(entidadActual);
	
	    	switch (key) {
		        case 1:
		          listaAux(f - 1, c, color, entidadesVisitadas, 1);
		          break;
		        case 2:
		          listaAux(f + 1, c, color, entidadesVisitadas, 2);
		          break;
		        case 3:
		          listaAux(f, c + 1, color, entidadesVisitadas, 3);
		          break;
		        case 4:
		          listaAux(f, c - 1, color, entidadesVisitadas, 4);
		          break;
		      }
	    }
	}

	private boolean en_rango(int nf, int nc) {
		return (0 <= nf && (nf < filas) && (0 <= nc) && (nc < columnas));
	}

	public void actualizar_manager_objetivos(ManagerObjetivos manager) {
		manager_objetivos = manager;
	}

}