import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class Cliente {

    public static void main(String[] args) {
        final var servidores = List.of("192.168.1.2", "192.168.0.1", "192.168.0.35"); // Altere com os endereços IP reais dos servidores

        servidores.forEach(servidor -> {
            Thread thread = new Thread(new ServidorHandler(servidor));
            thread.start();
        });
    }
}

class ServidorHandler implements Runnable {
    private final String enderecoIP;

    public ServidorHandler(String enderecoIP) {
        this.enderecoIP = enderecoIP;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(enderecoIP, 12345);
            System.out.println("Conectado ao servidor: " + enderecoIP);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Map<String, String> recursos = (Map<String, String>) objectInputStream.readObject();

            System.out.println("Recursos do servidor " + enderecoIP + ":");
            recursos.forEach((key, value) -> System.out.println(key + ": " + value));

            objectInputStream.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}