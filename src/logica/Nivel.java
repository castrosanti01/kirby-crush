package logica;

/**
 * Esta clase modela un nivel del juego. Un nivel incluye información sobre su número, la posición
 * inicial del jugador, los objetivos, el límite de movimientos, el tiempo límite, y contadores para
 * los diferentes elementos del juego (caramelos, gelatinas, glaseados, etc.). También almacena el
 * puntaje inicial del nivel.
 * 
 */
public class Nivel {

  // Atributos del nivel
  protected int nivel_nro;
  protected int fila_inicial_jugador;
  protected int columna_inicial_jugador;
  protected int cantidad_movimientos;
  protected String objetivo_nivel;
  protected int tiempo_limite;
  protected int regla_de_match;
  protected Objetivo objetivos_verdes;
  protected Objetivo objetivos_amarillos;
  protected Objetivo objetivos_azules;
  protected Objetivo objetivos_violetas;
  protected Objetivo objetivos_rosas;
  protected Objetivo objetivos_gelatinas;
  protected Objetivo objetivos_glaseados;
  protected ManagerObjetivos manager_objetivos;

  /**
   * Clase interna que implementa el patrón Builder para construir instancias de Nivel con
   * configuraciones específicas.
   * 
   */
  public static class Builder {
    // Atributos de configuración del nivel
    private int nivel_nro;
    private int fila_inicial_jugador;
    private int columna_inicial_jugador;
    private int cantidad_movimientos;
    private String objetivo_nivel;
    private int tiempo_limite;
    private int regla_de_match;
    private Objetivo objetivos_verdes;
    private Objetivo objetivos_amarillos;
    private Objetivo objetivos_azules;
    private Objetivo objetivos_violetas;
    private Objetivo objetivos_rosas;
    private Objetivo objetivos_gelatinas;
    private Objetivo objetivos_glaseados;
    private ManagerObjetivos Mobj;
    

    // Métodos setter para configurar los atributos
    
    public Nivel.Builder nivelActual(int nivel_nro) {
      this.nivel_nro = nivel_nro;
      return this;
    }

    public Nivel.Builder filaInicial(int fila_inicial_jugador) {
      this.fila_inicial_jugador = fila_inicial_jugador;
      return this;
    }

    public Nivel.Builder columnaInicial(int columna_inicial_jugador) {
      this.columna_inicial_jugador = columna_inicial_jugador;
      return this;
    }

    public Nivel.Builder objetivo(String objetivo_nivel) {
      this.objetivo_nivel = objetivo_nivel;
      return this;
    }

    public Nivel.Builder cantidadMovimientos(int cantidad_movimientos) {
      this.cantidad_movimientos = cantidad_movimientos;
      return this;
    }

    public Nivel.Builder tiempoLimite(int tiempo_limite) {
      this.tiempo_limite = tiempo_limite;
      return this;
    }
    
    public Nivel.Builder reglaDeMatch(int regla_de_match) {
        this.regla_de_match = regla_de_match;
        return this;
      }
    
    public Nivel.Builder ObjetivoVerde(Objetivo objetivos_verdes){
    	this.objetivos_verdes = objetivos_verdes;
    	return this;
    }
    public Nivel.Builder ObjetivoAmarillo(Objetivo objetivos_amarillos){
    	this.objetivos_amarillos = objetivos_amarillos;
    	return this;
    }
    public Nivel.Builder ObjetivoAzul(Objetivo objetivos_azules){
    	this.objetivos_azules = objetivos_azules;
    	return this;
    }
    public Nivel.Builder ObjetivoVioleta(Objetivo objetivos_violetas){
    	this.objetivos_violetas = objetivos_violetas;
    	return this;
    }
    public Nivel.Builder ObjetivoRosa(Objetivo objetivos_rosas){
    	this.objetivos_rosas = objetivos_rosas;
    	return this;
    }
    public Nivel.Builder ObjetivoGelatina(Objetivo ogel){
    	this.objetivos_gelatinas = ogel;
    	return this;
    }
    public Nivel.Builder ObjetivoGlaseado(Objetivo oglas){
    	this.objetivos_glaseados = oglas;
    	return this;
    }
	public Nivel.Builder ManagerObjetivos(ManagerObjetivos Mobj) {
		this.Mobj = Mobj;
		return this;
	}

    /**
     * Construye una instancia de Nivel con la configuración establecida.
     *
     * @return Una instancia de Nivel con la configuración proporcionada.
     * 
     */
    public Nivel build() {
      return new Nivel(this);
    }

  }

  /**
   * Constructor para la clase Builder, que permite construir instancias de Nivel con
   * configuraciones específicas.
   * 
   */
  public Nivel(Builder builder) {
    this.nivel_nro = builder.nivel_nro;
    this.fila_inicial_jugador = builder.fila_inicial_jugador;
    this.columna_inicial_jugador = builder.columna_inicial_jugador;
    this.cantidad_movimientos = builder.cantidad_movimientos;
    this.objetivo_nivel = builder.objetivo_nivel;
    this.tiempo_limite = builder.tiempo_limite;
    this.objetivos_verdes = builder.objetivos_verdes;
    this.objetivos_amarillos = builder.objetivos_amarillos;
    this.objetivos_azules = builder.objetivos_azules;
    this.objetivos_violetas = builder.objetivos_violetas;
    this.objetivos_rosas = builder.objetivos_rosas;
    this.objetivos_gelatinas = builder.objetivos_gelatinas;
    this.objetivos_glaseados = builder.objetivos_glaseados;
    this.manager_objetivos = builder.Mobj;
    this.regla_de_match = builder.regla_de_match;
  }

  /**
   * Obtiene el número del nivel.
   * 
   * @return El número del nivel.
   * 
   */
  public int get_nro_nivel() {
    return nivel_nro;
  }

  /**
   * Obtiene la fila inicial del jugador.
   * 
   * @return La fila inicial del jugador.
   * 
   */
  public int get_fila_inicial_jugador() {
    return fila_inicial_jugador;
  }

  /**
   * Obtiene la columna inicial del jugador.
   * 
   * @return La columna inicial del jugador.
   * 
   */
  public int get_columna_inicial_jugador() {
    return columna_inicial_jugador;
  }

  /**
   * Obtiene la cantidad de movimientos permitidos en el nivel.
   * 
   * @return La cantidad de movimientos permitidos.
   * 
   */
  public int get_movimientos() {
    return cantidad_movimientos;
  }

  /**
   * Obtiene el objetivo del nivel.
   * 
   * @return El objetivo del nivel.
   * 
   */
  public String get_objetivo_nivel() {
    return objetivo_nivel;
  }
  
  public ManagerObjetivos get_manager_objetivos() {
	  return manager_objetivos;
  }
  
  /**
   * Obtiene el límite de tiempo en segundos.
   * 
   * @return El límite de tiempo en segundos.
   * 
   */
  public int get_tiempo_restante() {
    return tiempo_limite;
  }
  
  /**
   * Obtiene la regla del match.
   * 
   * @return Regla del match.
   * 
   */
  public int get_regla_de_match() {
    return regla_de_match;
  }

}