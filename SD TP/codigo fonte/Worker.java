import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.lang.Object;
import java.io.DataInputStream;
import java.nio.file.*;



class Worker implements Runnable{

    Soundcloud sc;
    ServerSocket port;
    Socket clSock;

    public Worker(Soundcloud sc, ServerSocket port, Socket clSock){
        this.sc = sc;
        this.port = port;
        this.clSock = clSock;
    }

    public void run(){

        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(clSock.getInputStream())); // Abrir canais de leitura
            PrintWriter out = new PrintWriter(clSock.getOutputStream());                            // e escrita no socket
			DataOutputStream dOut = new DataOutputStream(clSock.getOutputStream());
			DataInputStream dIn = new DataInputStream(clSock.getInputStream());

            String text="";

            while((text = in.readLine()) != null && !"exit".equals(text)){       // Ler e escrever
                try{
                    String split[] = text.split(">");
                    String op = split[0];
					String res = "";
					Boolean reg = false;
                    switch(op){
                        case "1":
							String ss[] = split[1].split(",");
							reg = this.sc.regista(ss[0], ss[1]);
							if(reg){
								out.println("1");
								out.flush();
							}else{
								out.println("0");
								out.flush();
								break;
							}
                            System.out.println("Utilizador " + ss[0] + " registado, com a password: " + ss[1]);
                            out.flush();
                            break;
						case "2":
							ss = split[1].split(",");
							reg = this.sc.autentica(ss[0], ss[1]);
                            out.println("---> Server: " + reg);
                            System.out.println("Utilizador " + ss[0] + " autenticado.");
                            out.flush();
                            break;
						case "3":
							ss = split[1].split(",");
							if(Integer.parseInt(ss[0]) == 0){
								for(int i = 1; i <= sc.lastmusica; i++)
									res = res + this.sc.procura("0");
							}else{
								for(int i = 1; i <= Integer.parseInt(ss[0]); i++)
									res = res + this.sc.procura(ss[i]);
							}
							out.println(res);
							out.flush();
							break;
						case "4":
							String idS = split[1];
							String metadados = "";
							String title;
							if(sc.musicas.containsKey(Integer.parseInt(idS))){
								metadados = sc.getMeta(Integer.parseInt(idS));
								String smeta[] = metadados.split("/");
								title = smeta[0];
								out.println(title);
								out.flush();

								if(in.readLine().equals("1")){
									byte b[] = sc.mToByte(Integer.parseInt(idS));
									dOut.writeInt(b.length);
									dOut.write(b);
									dOut.flush();
									out.println(metadados);
									out.flush();
								}else{
									break;
								}
							}else{
								out.println("-1");
								out.flush();
								break;
							}
							break;
						case "5":
							String dados = split[1];
							String nome[] = dados.split("/");
							int length = dIn.readInt();
							if(length>0){
							    byte[] message = new byte[length];
							    dIn.readFully(message, 0, message.length); // read the message

								//Criar o path para onde vou copiar
								Path currentRelativePath = Paths.get("");
								String path = currentRelativePath.toAbsolutePath().toString();
								path = path + "/Cloud/" + nome[0] + ".mp3";
								System.out.println("\nO path para o download é o seguinte: " + path);

								File r=new File(path);
								//Path e ficheiro criados

								try (FileOutputStream fos = new FileOutputStream(path)) {
								   fos.write(message);
								}
								sc.criaMusica(dados, r);
							}
							break;

						default:
                    }
                }catch(InvalidRegistration | InvalidAccount e){
						out.println("0");
						out.flush();
                }
            }

            clSock.shutdownOutput(); // Fechar socket
            clSock.shutdownInput();  // e respetivos
            clSock.close();			 // canais
        }catch(IOException c){}

    }
}
