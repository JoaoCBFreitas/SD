import java.net.*;
import java.io.*;

class Server{

    ServerSocket sSocket;
    Socket clSock;

    public static void main(String args[]) throws IOException, InvalidAccount{
        Server s = new Server();
		Soundcloud sc = new Soundcloud();

        try{
            s.sSocket = new ServerSocket(12345); //Criar novo server socket num dado porto
            while(true){  // Aceitar conexoes indefinidamente
                s.clSock = s.sSocket.accept(); // Bloquear ate que uma conexao seja estabelecida


                Worker w = new Worker(sc, s.sSocket, s.clSock);
                Thread t = new Thread(w);

                t.start();

            }
        }catch(IOException e){
            e.printStackTrace();
        }

        s.clSock.shutdownOutput(); // Fechar socket
        s.clSock.shutdownInput();  // e respetivos
        s.clSock.close();		   // canais



    }
}
