package logica;

import java.util.*;

public class ManagerObjetivos {
	protected List<Objetivo> objetivos;
	
	public ManagerObjetivos() {
		objetivos = new ArrayList<Objetivo>();
	}
	
	public List<Objetivo> get_lista(){
		return objetivos;
	}
	
	public void suscribirse(Objetivo o){
		objetivos.add(o);
	}
	
	public void desuscribirse(Objetivo o) {
		objetivos.remove(o);
	}
	
}