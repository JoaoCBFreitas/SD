import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;


class Cliente{

	public void printStart(){
		System.out.println();
		System.out.println("     +-------------- Inicio --------------+");
		System.out.println("     |     1. Registar Utilizador         |");
		System.out.println("     |     2. Autenticar Utilizador       |");
		System.out.println("     |     0. Sair                        |");
		System.out.println("     +------------------------------------+");
		System.out.println();
		System.out.print("     Opção: ");
	}

	public void printMenu(){
		System.out.println();
		System.out.println("     +-------- MENU --------+");
		System.out.println("     |     1. Procurar      |");
		System.out.println("     |     2. Download      |");
		System.out.println("     |     3. Upload        |");
		System.out.println("     |     0. Logout        |");
		System.out.println("     +----------------------+");
		System.out.println();
		System.out.print("     Opção: ");
	}

	public void printMusicas(String musicas){
		if(musicas.length() == 0)
			System.out.println("\n     Nao foram encontradas musicas com essa etiqueta.");
		else{
			String m = musicas.substring(1);
			String split[] = m.split("/");
			HashSet<String> set = new HashSet<String>();
			Collections.addAll(set, split);
			System.out.println("\n------------------------");
			for (String s : set) {
			    System.out.println(s);
			}
			System.out.println("------------------------");
		}
	}

	public String printMetadados(String meta){
		String smeta[] = meta.split("/");
		System.out.println("     +Musica: " + smeta[0]);
		System.out.println("     +interprete: " + smeta[1]);
		System.out.println("     +Ano: " + smeta[2]);
		System.out.println("     +Etiquetas: " + smeta[3]);
		System.out.println("     +Numero de Donwloads: " + smeta[4]);
		return smeta[0];
	}

	public static void main(String args[]) throws IOException{

		Cliente c = new Cliente();
		System.out.println("\n     Attempting to connect...");
		Socket socket = new Socket("127.0.0.1", 12345);  // Criar socket e ligaçao com o servidor
		System.out.println("     Connected!");

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Le do servidor
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in)); //Le o teclado
		PrintWriter output = new PrintWriter(socket.getOutputStream()); // Manda para o servidor
		DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		DataInputStream dIn = new DataInputStream(socket.getInputStream());

		int login = 0;
		String text = "ola";
		String ans = "";
		String op = "";
		String user = "";
		String pass = "";
		String user1 = "";
		String pass1 = "";
		String ets = "";
		while(text != null && !"0".equals(text)){       // Ler e escrever
			// Efetuar login.
			if(login == 0){
				c.printStart();
				op = teclado.readLine();
				ans = "";
				switch(op){
					case "0":
						System.exit(0);
					case "1": // Registar
						System.out.println("\n     Inserir utilizador:");
						System.out.print("     ");
						user = teclado.readLine();
						System.out.println("\n     Inserir password:");
						System.out.print("     ");
						pass = teclado.readLine();
						output.println("1>" + user + "," +  pass);
						output.flush();
						ans = in.readLine();
						if(ans.equals("0"))
							System.out.println("\n     Erro ao registar utilizador.\n");
						else
							System.out.println("\n     Utilizador registado com sucesso.\n");
						break;
					case "2": // Autenticar
						System.out.println("\n     Inserir utilizador:");
						System.out.print("     ");
						user1 = teclado.readLine();
						System.out.println("\n     Inserir password:");
						System.out.print("     ");
						pass1 = teclado.readLine();
						output.println("2>" + user + "," +  pass);
						output.flush();
						ans = in.readLine();
						if(ans.equals("0"))
							System.out.println("\n     Erro ao autenticar utilizador.");
						else{
							System.out.println("\n     Utilizador autenticado com sucesso.");
							login = 1;
						}
						break;
					default:
						System.out.println("     Comando desconhecido! Tente novamente.");

				}
			}else{
				c.printMenu();
				op = teclado.readLine();
				ans = "";
				switch(op){
					case "0": // Logout
						System.out.println("     0. Logout");
						login = 0;
						break;
					case "1": // Procurar uma musica
						int num = -1;
						while(num < 0){
							System.out.println("\n     Indicar numero de etiquetas.\n     Escolha 0 para apresentar todas a musicas");
							try{
								System.out.print("     ");
								num = Integer.parseInt(teclado.readLine());

								if(num < 0 && num != -1)
									System.out.println("     Introduzir um inteiro superior a 0");
							}catch(NumberFormatException e){
								System.out.println("     ERRO! Introduzir um inteiro.");
							}
						}
						if(num == 0){
							output.println("3>0,0");
							output.flush();
						}else{
							int j = 1;
							String tags[] = new String[num];
							for(int i = 0; i<num; i++,j++){
								System.out.println("     Inserir etiqueta numero " + j + ".");
								System.out.print("     ");
								tags[i] = teclado.readLine();
							}
							ets = "3>" + Integer.toString(num);
							for(int i = 0; i<num; i++){
								ets = ets + "," + tags[i];
							}
							output.println(ets);
							output.flush();
						}
						ans = in.readLine();
						if(ans.equals("0"))
							System.out.println("     Não existem musicas com essa etiqueta.");
						else
							c.printMusicas(ans);
						break;
					case "2": //Download de uma musica
						String id;
						String res = "";
						String metadados = "";
						System.out.println("\n     Qual o ID da musica que gostaria de descarregar?");
						System.out.print("     ");
						id = teclado.readLine();
						res = "4>" + id;

						output.println(res);
						output.flush();

						ans = in.readLine();
						if(ans.equals("-1")){
							System.out.println("     Nao existe essa musica");
							break;
						}
						String title = ans;
						System.out.println("     Pretende fazer o download da musica " + title + "?");
						System.out.print("     1.Sim          0.Nao\n     ");

						res = teclado.readLine();
						while(!res.equals("1") && !res.equals("0")){
							System.out.println("     Resposta inválida\n");
							System.out.println("     Pretende fazer o download da musica " + title + "?");
							System.out.print("     1.Sim          0.Nao\n     ");
							res = teclado.readLine();
						}
						if(res.equals("1")){
							output.println("1");
							output.flush();

							int length = dIn.readInt();
							if(length>0) {
							    byte[] message = new byte[length];
							    dIn.readFully(message, 0, message.length); // read the message

								//Criar o path para onde vou copiar
								Path currentRelativePath = Paths.get("");
								String path = currentRelativePath.toAbsolutePath().toString();
								path = path + "/Downloads/" + ans + ".mp3";
								System.out.println("\n     O ficheiro vai ficar em: " + path);

								File r=new File(path);
								//Path e ficheiro criados

								try (FileOutputStream fos = new FileOutputStream(path)) {
								   fos.write(message);

							   System.out.println("\n     -----METADADOS:-----");

   								metadados = in.readLine();
   								c.printMetadados(metadados);
   								System.out.println("\n     Download efetuado com sucesso!");
   								break;
								}
						    }else{
					 			System.out.println("Erro na transferencia.");
								break;
							}
						}else{
							output.println("0");
							output.flush();
							System.out.println("     A voltar ao menu.");
						}
						break;
					case "3": //Upload de uma musica
						String nome = "";
						String autor = "";
						int ano = -1;
						String mDados = "";
						num = -1;
						try{
							System.out.println("     Por favor preencha os metadados relativos ao seu upload.");
							System.out.print("     Nome da musica: ");
							nome = teclado.readLine();
							System.out.print("     Nome do Interprete: ");
							autor = teclado.readLine();
							while(ano <= 0){
								System.out.print("     Ano em que foi lançada: ");
								try{
									ano = Integer.parseInt(teclado.readLine());
									if(ano <= 0){
										System.out.println("     Introduzir um inteiro superior a 0\n");
									}
								}catch(NumberFormatException e){
									System.out.println("     ERRO! Introduzir um inteiro.\n");
								}
							}
							while(num <= 0){
								System.out.print("     Indique por favor o numero de etiquetas: ");
								try{
									num = Integer.parseInt(teclado.readLine());
									if(num <= 0){
										System.out.println("     Introduzir um inteiro superior a 0\n");
									}
								}catch(NumberFormatException e){
									System.out.println("     ERRO! Introduzir um inteiro.\n");
								}
							}
							String tag[] = new String[num+1];
							for(int i = 1; i < num+1; i++){
								System.out.print("     Etiqueta numero " + i + ":");
								tag[i] = teclado.readLine();
							}
							String etiquetas = Integer.toString(num);
							for(int i = 1; i<=num; i++){
								etiquetas = etiquetas + "," + tag[i];
							}
							mDados = "5>" + nome + "/" + autor + "/" + ano + "/" + etiquetas;
							output.println(mDados);
							output.flush();
						}catch(IOException e){
							e.printStackTrace();
						}

						byte[] fileContent = new byte[1];
						try{
							Path currentRelativePath = Paths.get("");
							String path = currentRelativePath.toAbsolutePath().toString();

							path = path + "/PC/" + nome + ".mp3";

							File file = new File(path);
							//init array with file length
							byte[] bytesArray = new byte[(int) file.length()];

							file = new File(path);
							fileContent = Files.readAllBytes(file.toPath());
						}catch(IOException e){
							e.printStackTrace();
						}
						dOut.writeInt(fileContent.length); // write length of the message
						dOut.write(fileContent);
						dOut.flush();
					break;

					default:
						System.out.println("     Comando desconhecido!");

				}
			}
		}
		socket.shutdownOutput();    // Fechar socket
		socket.shutdownInput();     // e respetivos
		socket.close();             // canais
	}
}
