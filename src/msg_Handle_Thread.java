import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class msg_Handle_Thread extends Thread {
    Socket cSocket;
    projectMain mainObj;

    public msg_Handle_Thread(Socket socket, projectMain mainObj) {
        this.cSocket = socket;
        this.mainObj = mainObj;
    }

    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ObjectInputStream ois = null;
        int num = 0;
        try {
            ois = new ObjectInputStream(cSocket.getInputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while(true){
            try{
                Message msg;
                msg = (Message) Objects.requireNonNull(ois).readObject();
                synchronized (mainObj){
                    System.out.println("\nMessage "+ (++num));
                    if(msg instanceof tokenMsg){
                        System.out.println("Total cars: " + mainObj.totalCars);
                        System.out.println("Amount in token: " + ((tokenMsg) msg).amount);
                        mainObj.totalCars+=((tokenMsg) msg).amount-mainObj.lastOp;
                        ((tokenMsg) msg).amount-=mainObj.lastOp;
                        mainObj.lastOp=0;
                        if(mainObj.isQuit){
                            mainObj.emitTokenMsg(msg);
                        }else{
                            System.out.println("Cars in token message: " + ((tokenMsg) msg).amount);
                            int min=1;
                            int max=100;
                            int tag = (int)(Math.random()*(max-min)+min);
                            if(tag<40&&mainObj.totalCars<projectMain.ceil){
                                System.out.println("There is a car parking here.");
                                mainObj.totalCars++;
                                mainObj.lastOp++;
                            }else if(tag>70&&mainObj.totalCars>0){
                                System.out.println("There is a car which wants to leave.");
                                mainObj.totalCars--;
                                mainObj.lastOp--;
                            }else{
                                System.out.println("Here not waits a car.");
                            }
                            System.out.println("My current total cars after operation: " + mainObj.totalCars);
                        }
/*
                        if(!(mainObj.isQuit)){
                            BufferedReader br = new BufferedReader(new InputStreamReader(System.in)) ;
                            System.out.println("Type in quit to exit the connection, or click enter to continue:");
                            String line = br.readLine();
                            br.close();
                            if(line.compareTo("quit")==0){
                                mainObj.isQuit=true;
                                System.out.println("Quit successfully.");
                            }
                        }else{
                            BufferedReader br = new BufferedReader(new InputStreamReader(System.in)) ;
                            System.out.println("Type in back to build the connection, or click enter to continue:");
                            String line = br.readLine();
                            br.close();
                            if(line.compareTo("back")==0){
                                mainObj.isQuit=false;
                                System.out.println("Join in successfully");
                            }
                        }
*/
                        ((tokenMsg) msg).amount+=mainObj.lastOp;
                        mainObj.emitTokenMsg(msg);
                    }
                }
            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
