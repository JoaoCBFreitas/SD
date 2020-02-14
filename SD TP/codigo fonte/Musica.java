import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

class Musica {
	int id;
    String titulo;
    String interprete;
    File ficheiro;
    int ano;
    int descarregamentos;
    ArrayList<String> etiquetas;
    private ReentrantLock lockMusica;

    public Musica(int id, String nome,String autor,File ficheiro,int i, ArrayList<String> et){
		this.id = id;
		this.titulo=nome;
        this.interprete=autor;
        this.ano=i;
        this.ficheiro=ficheiro;
        this.etiquetas=et;
        this.descarregamentos=0;
        this.lockMusica=new ReentrantLock();
    }
    public void lock(){
        this.lockMusica.lock();
    }
    public void unlock(){
        this.lockMusica.unlock();
    }

	public int getID(){
		return this.id;
	}
    public String getTitulo(){
        return this.titulo;
    }
    public String getInterprete(){
        return this.interprete;
    }
    public int getAno(){
        return this.ano;
    }
    public ArrayList<String> getEtiquetas(){
        return this.etiquetas;
    }
    public File getFicheiro(){
        return this.ficheiro;
    }
    public int getDow(){
        return this.descarregamentos;
    }
    public void incrementaDownload(){
        this.descarregamentos++;
    }
}
