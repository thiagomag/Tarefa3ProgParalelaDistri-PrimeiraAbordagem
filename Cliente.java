import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Map;

public class Cliente {

    public static void main(String[] args) {
        // Array com os endereços IP dos servidores
        String[] servidores = {"192.168.1.2", "192.168.0.1", "192.168.0.13"}; // Altere com os endereços IP reais dos servidores

        // Loop para se conectar a cada servidor em paralelo
        for (String servidor : servidores) {
            Thread thread = new Thread(new ServidorHandler(servidor));
            thread.start();
        }
    }
}

class ServidorHandler implements Runnable {
    private String enderecoIP;

    public ServidorHandler(String enderecoIP) {
        this.enderecoIP = enderecoIP;
    }

    @Override
    public void run() {
        try {
            // Conecta ao servidor
            Socket socket = new Socket(enderecoIP, 12345);
            System.out.println("Conectado ao servidor: " + enderecoIP);

            // Recebe as informações de recursos do servidor
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Map<String, String> recursos = (Map<String, String>) objectInputStream.readObject();

            // Exibe as informações de recursos recebidas do servidor
            System.out.println("Recursos do servidor " + enderecoIP + ":");
            for (Map.Entry<String, String> recurso : recursos.entrySet()) {
                System.out.println(recurso.getKey() + ": " + recurso.getValue());
            }

            // Fecha a stream e o socket
            objectInputStream.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}