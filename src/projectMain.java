import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class projectMain {
    public Node currentNode;
    public Node prevNeighbor;
    public Node nextNeighbor ;
    public int totalCars;
    public static final int ceil = 60;
    public ObjectOutputStream outToNext;
    public ObjectOutputStream outToPrev;
    public Boolean isQuit;
    public int lastOp;
    projectMain(){
        currentNode = new Node(0, "192.168.43.153", 15000);
        prevNeighbor = new Node(2, "192.168.43.212", 15000);
        nextNeighbor = new Node(1, "192.168.43.233", 15000);
        isQuit=false;
        lastOp=0;
    }

    public void emitTokenMsg(Message m){
        try {
            System.out.println("Send token message.");
            ObjectOutputStream oos = outToNext;
            oos.writeObject(m);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        projectMain mainObj = new projectMain();

        //  Open Channels
        String hostName = mainObj.nextNeighbor.host;
        int port = mainObj.nextNeighbor.port;

        //  Start Server on Current Node
        ServerSocket listener = new ServerSocket(mainObj.currentNode.port);
        Thread.sleep(7000);

        Socket socket2Next;
        try {
            socket2Next = new Socket(hostName, port);
            System.out.println("Connected to next neighbor: " + hostName);
            mainObj.outToNext = new ObjectOutputStream(socket2Next.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        hostName = mainObj.prevNeighbor.host;
        port = mainObj.prevNeighbor.port;

        Socket socket2Prev;
        try {
            socket2Prev = new Socket(hostName, port);
            System.out.println("Connected to previous neighbor: " + hostName);
            mainObj.outToPrev = new ObjectOutputStream(socket2Prev.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message tkMsg = new tokenMsg();
        mainObj.emitTokenMsg(tkMsg);
        System.out.println("token");

        try {
            for (int i = 0; i < 2; i++) {
                // This node listens as a Server for the clients requests
                Socket socket = listener.accept();
                new msg_Handle_Thread(socket, mainObj).start();
                System.out.println("Incoming Request:" + socket.getInetAddress() + ":" + socket.getPort());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
