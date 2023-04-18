import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Servidor rodando na porta 12345...");

            while (true) {
                System.out.println("Aguardando conexão do cliente...");
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());
                Thread thread = new Thread(new ClienteHandler(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClienteHandler implements Runnable {
    private final Socket socket;

    public ClienteHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Obtém as informações de recursos da máquina
            Map<String, String> recursos = new HashMap<>();
            recursos.put("Memória Livre", String.valueOf(Runtime.getRuntime().freeMemory()));
            recursos.put("Memória Em Uso", String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            recursos.put("Modelo do Processador", System.getProperty("os.arch"));
            recursos.put("Carga do Processador", String.valueOf(Runtime.getRuntime().availableProcessors()));
            recursos.put("Armazenamento", getArmazenamento());
            recursos.put("Rede", getEnderecoIP());

            // Envia as informações de recursos para o cliente
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(recursos);
            objectOutputStream.flush();

            System.out.println("Informações de recursos enviadas para o cliente: " + socket.getInetAddress().getHostAddress());

            // Fecha as streams e o socket
            objectOutputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getArmazenamento() {
        try {
            // Executa o comando 'wmic' para obter informações de armazenamento
            Process process = Runtime.getRuntime().exec("wmic logicaldisk get DeviceID, FreeSpace, Size");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();

            // Lê a saída do comando e constrói uma string com as informações de armazenamento
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }

            bufferedReader.close();
            process.waitFor();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Informação de armazenamento não disponível";
    }

    private String getEnderecoIP() {
        // Implemente aqui a lógica para obter o endereço IP da máquina
        // Exemplo: retornando o endereço IP da primeira interface de rede disponível
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
            byte[] mac = networkInterface.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Informação de rede não disponível";
    }
}
