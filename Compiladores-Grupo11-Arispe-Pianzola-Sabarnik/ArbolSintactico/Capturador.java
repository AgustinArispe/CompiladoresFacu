package ArbolSintactico;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Capturador {

    public static String capturarSalida(Runnable accion) {
        PrintStream salidaOriginal = System.out;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream salidaTemporal = new PrintStream(buffer);

        try {
            System.setOut(salidaTemporal);

            accion.run();
        } finally {
            System.setOut(salidaOriginal);
        }

        return buffer.toString();
    }
}
