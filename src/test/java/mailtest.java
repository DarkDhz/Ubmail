import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class mailtest {

    public mailtest() throws IOException {
        Socket socket = new Socket("localhost", 25);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("hola que tal");
    }

}
