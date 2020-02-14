import java.util.concurrent.locks.ReentrantLock;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

class Soundcloud{

    public Map<String,Utilizador> contas; //contas de todos os utilizadores
    public Map<Integer,Musica> musicas; //musicas no servidor
    public int lastmusica;
    private ReentrantLock lockServer;

    public Soundcloud(){
		ArrayList<String> et1=new ArrayList<>();
        et1.add("Teste");
        et1.add("Rock");
        ArrayList<String> et2=new ArrayList<>();
        et2.add("Teste");
        et2.add("Jazz");
        ArrayList<String> et3=new ArrayList<>();
        et3.add("Clássica");
		ArrayList<String> et4=new ArrayList<>();
        et4.add("EDM");
        File fich1=new File("/usr/local/bin/test1");
        File fich2=new File("/usr/local/bin/test2");
        File fich3=new File("/usr/local/bin/test3");
		File file=new File("/Desktop/teste/Cloud/Realla.mp3");
        Musica m0=new Musica(1,"musica1","autor1",fich1,2019,et1);
        Musica m1=new Musica(2,"musica2","autor2",fich2,1999,et2);
        Musica m2=new Musica(3,"musica3","autor2",fich3,2005,et3);
		Musica m3=new Musica(4,"Realla","TOKiMONSTA",file,2017,et4);
        Utilizador u1=new Utilizador("user1", "pass");
        Utilizador u2=new Utilizador("user2", "pass");

        contas=new HashMap<String,Utilizador>();
        musicas=new HashMap<Integer,Musica>();

        this.contas.put("user1",u1);
        this.contas.put("user2",u2);
        this.musicas.put(1,m0);
        this.musicas.put(2,m1);
        this.musicas.put(3,m2);
		this.musicas.put(4,m3);
        this.lastmusica = 4;

        this.lockServer=new ReentrantLock();
    }

    public void lock(){
        this.lockServer.lock();
    }
    public void unlock(){
        this.lockServer.unlock();
    }

    //regista utilizador
    public Boolean regista(String nome,String pass) throws InvalidRegistration{
        Boolean r=false;
        this.lockServer.lock();
            if(this.contas.containsKey(nome)){
                this.lockServer.unlock();
                r=false;
                throw new InvalidRegistration("Nome já ocupado");
            }else{
                Utilizador u=new Utilizador(nome,pass);
                this.contas.put(nome,u);
                this.lockServer.unlock();
                r=true;
            }
        return r;
    }

    //autentica utilizador através da pass
    public Boolean autentica(String nome, String pass) throws InvalidAccount{
        Boolean r=false;
        this.lockServer.lock();
            if(this.contas.containsKey(nome)){
                Utilizador u=this.contas.get(nome);
                u.lock();
                this.lockServer.unlock();
                if(u.getPass().equals(pass)){
                    u.unlock();
                    r=true;
                }else{
                    u.unlock();
                    r=false;
                    throw new InvalidAccount("Password errada");
                }
            }else{
                this.lockServer.unlock();
                r=false;
                throw new InvalidAccount("Nome inválido");
            }
        return r;
    }

	public String procura(String et){
		String res = "";
        this.lockServer.lock();
			if(et.equals("0")){
				for(Musica mu: this.musicas.values()){
					res = res + "/" + "Nome: " + mu.getTitulo() + " - Autor " + mu.getInterprete() + " - ID:" + mu.getID() + " - N. Downloads:" + mu.getDow() + " Etiquetas: " + mu.getEtiquetas();
				}
			}
            for(Musica mu: this.musicas.values()){
				if(mu.etiquetas.contains(et)){
					res = res + "/" + "Nome: " + mu.getTitulo() + " - Autor " + mu.getInterprete() + " - ID:" + mu.getID() + " - N. Downloads:" + mu.getDow() + " Etiquetas: " + mu.getEtiquetas();
				}
            }
			this.lockServer.unlock();
        return res;
	}

	public void aviso(String m){
		System.out.println(m);
	}

	public int criaMusica(String dados, File song){
		String splitData[] = dados.split("/");
		String tags[] = splitData[3].split(",");
		int num = Integer.parseInt(tags[0]);
		ArrayList<String> eti = new ArrayList<>(num);
		for(int i = 1; i < num+1; i++)
			eti.add(tags[i]);
		this.lockServer.lock();
		Musica mus = new Musica(++this.lastmusica, splitData[0], splitData[1], song, Integer.parseInt(splitData[2]), eti);
		this.musicas.put(this.lastmusica, mus);
		this.lockServer.unlock();
		return 1;
	}

	public byte[] mToByte(int id){
		byte[] fileContent = new byte[1];
		this.lockServer.lock();
		try{
			Musica m = this.musicas.get(id);
			String title = m.getTitulo();

			Path currentRelativePath = Paths.get("");
			String path = currentRelativePath.toAbsolutePath().toString();

			path = path + "/Cloud/" + title + ".mp3";

			File file = new File(path);
			byte[] bytesArray = new byte[(int) file.length()];

			file = new File(path);
			fileContent = Files.readAllBytes(file.toPath());
			this.lockServer.unlock();
			return fileContent;
		}catch(IOException e){
				System.out.println("Nao existe esse ficheiro.");
		}
		return fileContent;
	}

	public String getMeta(int id){
		this.lockServer.lock();
		String metadados = "";
		try{
			if(this.musicas.containsKey(id)){
				Musica m=this.musicas.get(id);
				m.lock();
				this.lockServer.unlock();
				m.incrementaDownload();
				metadados = m.getTitulo() + "/" + m.getInterprete() + "/" + Integer.toString(m.getAno()) + "/" +
							m.getEtiquetas() + "/" + Integer.toString(m.getDow()) + "/";

				m.unlock();
				return metadados;
			}else{
				this.lockServer.unlock();
				throw new InvalidMusic("Id inválido.");
			}
		}catch(InvalidMusic e){
			System.out.println("Musica inexistente.");
		}
		return metadados;
	}
}
