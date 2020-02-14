import java.util.concurrent.locks.ReentrantLock;

class Utilizador {
    String nome;
    String pass;
    private ReentrantLock lockUtilizador;

    public Utilizador(String nome,String pass){
        this.nome=nome;
        this.pass=pass;
        this.lockUtilizador=new ReentrantLock();
    }
    public void lock(){
        this.lockUtilizador.lock();
    }
    public void unlock(){
        this.lockUtilizador.unlock();
    }
    public String getNome(){
        return this.nome;
    }
    public String getPass(){
        return this.pass;
    }

}