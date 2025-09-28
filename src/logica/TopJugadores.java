package logica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopJugadores implements Serializable {
	private List<Jugador> ranking;
	
	public TopJugadores() {
		this.ranking = new ArrayList<Jugador>();
	}
	
	public void agregar_jugador(Jugador jugador) {
		this.ranking.add(jugador);
	}
	
    public List<Jugador> get_lista() {
        return this.ranking;
    }
    
   public  List<Jugador> get_ranking_ordenado() {
	    List<Jugador> ranking_ordenado = new ArrayList<Jugador>();
		Collections.sort(this.ranking, Collections.reverseOrder());
		int i = 0;		
		for(Jugador puntaje : this.ranking) {
			ranking_ordenado.add(new Jugador(puntaje.get_jugador(),puntaje.get_puntaje_acumulado()));
			if( i == 4 ) break;
			i++;
		}
		return ranking_ordenado;
	}

}
