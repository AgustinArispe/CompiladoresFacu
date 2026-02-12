package ArbolSintactico;

import java.util.ArrayList;
import java.util.HashMap;
import CompiladoresMain.TablaDeAmbitos;
import CompiladoresMain.GeneradorAssembler; 

public class NodoBloque extends Nodo {
    private ArrayList<Nodo> sentencias;
    
    public NodoBloque() {
        this.sentencias = new ArrayList<>();
    }
    
    public void agregarSentencia(Nodo sentencia) {
        if (sentencia != null) { 
            this.sentencias.add(sentencia);
        }
    }
    
    @Override
    public boolean tieneReturn() {
        for (Nodo sentencia : this.sentencias) {
            if (sentencia.tieneReturn()) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<Nodo> getSentencias() {
        return this.sentencias;
    }

    @Override
    public String chequear(TablaDeAmbitos TdA) {
        for (Nodo s : sentencias) {
            s.chequear(TdA);
        }
        return "void"; 
    }
    
    @Override
    public void imprimir(String prefijo) {
        for (Nodo s : sentencias) {
            if (s != null) s.imprimir(prefijo);
        }
    }

    
    @Override
    public String generarCodigo(GeneradorAssembler G, TablaDeAmbitos TdA) {
        for (Nodo s : sentencias) {
            s.generarCodigo(G, TdA);
        }
        return null;
    }
}
