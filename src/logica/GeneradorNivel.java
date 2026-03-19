package logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import entidades.*;
import factories.AbstractFactory;

/**
 * Permite pasear el contenido de un archivo de texto, donde se genera el nivel con todas sus
 * características.
 */
public class GeneradorNivel {

  public static Nivel cargar_nivel_y_tablero(InputStream nombreArchivo, AbstractFactory generador, Tablero tablero) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(nombreArchivo));

      // Variables para las características del nivel
      int filas = 0;
      int columnas = 0;
      int movimientos = 0;
      int nivel = 1;
     
      //Objetivos
      String objetivo = "";
      int tiempo = 0;
      int verdes = 0;
      int amarillos = 0;
      int azules = 0;
      int violetas = 0;
      int rosas = 0;
      
      ManagerObjetivos manager_objetivos = new ManagerObjetivos();
      Objetivo objetivosVerdes = new Objetivo(0);
      Objetivo objetivosAmarillos = new Objetivo(0);
      Objetivo objetivosAzules = new Objetivo(0);
      Objetivo objetivosVioletas = new Objetivo(0);
      Objetivo objetivosRosas = new Objetivo(0);
      Objetivo objetivoGelatinas = new Objetivo(0);
      Objetivo objetivoGlaseados = new Objetivo(0);
      

      String linea;
      boolean leyendoTablero = false;
      int filaActual = 0; // Lleva un seguimiento de la fila actual

      while ((linea = br.readLine()) != null) {
        // Ignorar líneas de comentario
        if (linea.startsWith("#")) {
          continue;
        }
        String[] partes = linea.split(":");
        if (partes.length == 2) {
          String clave = partes[0].trim();
          String valor = partes[1].trim();
          switch (clave) {
          	case "Nivel":
          		nivel = Integer.parseInt(valor);
          		break;
            case "Filas":
              filas = Integer.parseInt(valor);
              break;
            case "Columnas":
              columnas = Integer.parseInt(valor);
              break;
            case "Movimientos":
              movimientos = Integer.parseInt(valor);
              break;
            case "Objetivo":
              objetivo = valor;
              break;
            case "VerdesFaltantes":
              verdes = Integer.parseInt(valor);
              break;
            case "AmarillosFaltantes":
              amarillos = Integer.parseInt(valor);
              break;
            case "AzulesFaltantes":
              azules = Integer.parseInt(valor);
              break;
            case "VioletasFaltantes":
              violetas = Integer.parseInt(valor);
              break;
            case "RosasFaltantes":
              rosas = Integer.parseInt(valor);
              break;
            case "Tablero":
              leyendoTablero = true;
              break;
          }
          if (leyendoTablero) {
            tablero.resetar_tablero(filas, columnas);
          }
        } else if (linea.trim().length() > 0 && leyendoTablero && filaActual < filas) {
          String[] valores = linea.split(" ");
          for (int j = 0; j < columnas; j++) {
            int entidad = Integer.parseInt(valores[j]);
            switch (entidad) {
              // CASO CARAMELOS
              case 1:
            	  tablero.agregar_entidad(generador.crearCaramelo(filaActual, j, Color.VERDE, tablero));
            	  break;
              case 2:
            	  tablero.agregar_entidad(generador.crearCaramelo(filaActual, j, Color.AMARILLO, tablero));
            	  break;
              case 3:
            	  tablero.agregar_entidad(generador.crearCaramelo(filaActual, j, Color.AZUL, tablero));
            	  break;
              case 4:
            	  tablero.agregar_entidad(generador.crearCaramelo(filaActual, j, Color.VIOLETA, tablero));
            	  break;
              case 5:
            	  tablero.agregar_entidad(generador.crearCaramelo(filaActual, j, Color.ROSA, tablero));
            	  break;
            }
          }
          filaActual++;
        }
      }

      //Cerrar el archivo
      br.close();

     //Seteo de objetivos
      if (verdes > 0) {
    	  objetivosVerdes = new Objetivo("/imagenes/caramelos/"+generador.toString()+"-1.png", verdes, Color.VERDE);
    	  manager_objetivos.suscribirse(objetivosVerdes);
      }
      if (amarillos > 0) {
    	  objetivosAmarillos = new Objetivo("/imagenes/caramelos/"+generador.toString()+"-2.png", amarillos, Color.AMARILLO);
    	  manager_objetivos.suscribirse(objetivosAmarillos);
      }
      if (azules > 0) {
    	  objetivosAzules = new Objetivo("/imagenes/caramelos/"+generador.toString()+"-3.png", azules, Color.AZUL);
    	  manager_objetivos.suscribirse(objetivosAzules);
      }
      if (violetas > 0 ) {
    	  objetivosVioletas = new Objetivo("/imagenes/caramelos/"+generador.toString()+"-4.png", violetas, Color.VIOLETA);
    	  manager_objetivos.suscribirse(objetivosVioletas);
      }
      if (rosas > 0) {
    	  objetivosRosas = new Objetivo ("/imagenes/caramelos/"+generador.toString()+"-5.png", rosas, Color.ROSA);
    	  manager_objetivos.suscribirse(objetivosRosas);
      }

     // Crear y devolver el nivel con las características y el tablero cargados 
     return new Nivel.Builder()
          .nivelActual(nivel)
          .filaInicial(filas - filas / 3)
          .columnaInicial(columnas - columnas / 2)
          .objetivo(objetivo)
          .cantidadMovimientos(movimientos)
          .tiempoLimite(tiempo)
          .ObjetivoVerde(objetivosVerdes)
          .ObjetivoAmarillo(objetivosAmarillos)
          .ObjetivoAzul(objetivosAzules)
          .ObjetivoVioleta(objetivosVioletas)
          .ObjetivoRosa(objetivosRosas)
          .ObjetivoGelatina(objetivoGelatinas)
     	  .ObjetivoGlaseado(objetivoGlaseados)
          .ManagerObjetivos(manager_objetivos)
          .build();

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}