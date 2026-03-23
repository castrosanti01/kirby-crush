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

	public Jugador existe_jugador(String nombre) {
		for (Jugador jugador : ranking) {
			if (jugador.get_jugador().equals(nombre)) {
				return jugador;
			}
		}
		return null;
	}

    public void actualizar_puntaje_jugador(Jugador get_jugador_actual) {
		for (Jugador jugador : ranking) {
			if (jugador.get_jugador().equals(get_jugador_actual.get_jugador())) {
				jugador.actualizar_puntaje_acumulado(get_jugador_actual.get_puntaje_nivel_actual());
				return;
			}
		}
	}
    
   public List<Jugador> get_ranking_ordenado() {
		List<Jugador> ranking_ordenado = new ArrayList<>(ranking);
		Collections.sort(ranking_ordenado, (j1, j2) -> Integer.compare(j2.get_puntaje_acumulado(), j1.get_puntaje_acumulado()));
		return ranking_ordenado;		
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Jugador jugador : ranking) {
			sb.append(jugador.get_jugador()).append(": ").append(jugador.get_puntaje_acumulado()).append("\n");
		}
		return sb.toString();
	}
}
