package ArbolSintactico;
import CompiladoresMain.*;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List; 

public class NodoFuncionDef extends Nodo {
    private String nombre;
    private List<String> tiposRetorno; 
    private ArrayList<NodoParametro> parametros; 
    private NodoBloque cuerpo;
    private AtributosTokens atributosFuncion;
    
    public NodoFuncionDef(String nombre, List<String> tiposRetorno, ArrayList<NodoParametro> parametros, NodoBloque cuerpo, AtributosTokens attrs) {
        this.nombre = nombre;
        this.tiposRetorno = tiposRetorno;
        this.parametros = parametros;
        this.cuerpo = cuerpo;
        this.atributosFuncion = attrs; 
    }
    
    public List<String> getTiposRetorno() {
        return tiposRetorno;
    }
    
    public String getNombre(){
        return this.nombre;
    }

    public AtributosTokens getAtributos() { 
        return this.atributosFuncion;
    }
    
    public void setCuerpo(NodoBloque cuerpo) {
        this.cuerpo = cuerpo;
    }

    @Override
    public String chequear(TablaDeAmbitos TdA) {
        String mangledName = this.nombre + TdA.getMangledScope();
        
        if (AnalizadorLexico.tablaSimbolos.containsKey(mangledName)) {
            String msg = "ERROR Semantico: La funcion '" + this.nombre + "' ya fue declarada en este ambito.";
            System.err.println(msg);
            AnalizadorLexico.errores_y_warnings.add(msg);
        }

        this.atributosFuncion.setMangledName(mangledName);
        this.atributosFuncion.setUso("funcion"); // Aseguramos que sepa que es función
        AnalizadorLexico.tablaSimbolos.put(mangledName, this.atributosFuncion);

        if (AnalizadorLexico.tablaSimbolos.containsKey(this.nombre)) {
            AnalizadorLexico.tablaSimbolos.remove(this.nombre);
        }

        TdA.abrirAmbito(this.nombre);
        
        if (parametros != null) {
            for (NodoParametro p : parametros) {
                p.chequear(TdA); 
            }
        }

        // 6. Chequeamos el cuerpo de la función
        cuerpo.chequear(TdA);
        
        // 7. VALIDACIÓN DE RETORNO (La lógica que agregamos hoy)
        // Verificamos que existan returns en todos los flujos de ejecución.
        if (!this.cuerpo.tieneReturn()) {
            String msg = "ERROR Semantico: La funcion '" + this.nombre + "' debe retornar un valor en todos sus posibles flujos de ejecucion.";
            System.err.println(msg);
            AnalizadorLexico.errores_y_warnings.add(msg);
        }

        // 8. Cerramos el ámbito
        TdA.cerrarAmbito(); 
        
        return "void"; // O el tipo de retorno real si lo tienes en los atributos
    }
    
    @Override
    public void imprimir(String prefijo) {
        
        String retornos = tiposRetorno.toString();
        System.out.println(prefijo + "Definicion Funcion: " + nombre + " (Retorna: " + retornos + ")");
        if (parametros != null && !parametros.isEmpty()) {
            System.out.println(prefijo + "  " + "Parametros Formales:");
            for (NodoParametro p : parametros) {
                p.imprimir(prefijo + "    ");
            }
        }
        System.out.println(prefijo + "  " + "Cuerpo:");
        cuerpo.imprimir(prefijo + "    ");
    }

    
    @Override
    public String generarCodigo(GeneradorAssembler G, TablaDeAmbitos TdA) {
        String nombreProc = G.getNombreAsm(atributosFuncion.getMangledName());
        
        G.agregarCodigo("\n; --- Definicion de Funcion: " + this.nombre + " ---");
        G.agregarCodigo(nombreProc + " PROC");
        
        
        G.agregarCodigo("push ebp");
        G.agregarCodigo("mov ebp, esp");
        G.agregarCodigo("push edi");
        G.agregarCodigo("push esi");

        
        TdA.abrirAmbito(this.nombre);
        cuerpo.generarCodigo(G, TdA);
        TdA.cerrarAmbito();

        
        G.agregarCodigo(nombreProc + "_exit:"); 
        G.agregarCodigo("pop esi");
        G.agregarCodigo("pop edi");
        G.agregarCodigo("mov esp, ebp");
        G.agregarCodigo("pop ebp");
        G.agregarCodigo("RET");
        
        G.agregarCodigo(nombreProc + " ENDP");
        G.agregarCodigo("; --- Fin de Funcion: " + this.nombre + " ---\n");
        
        return null;
    }
}
