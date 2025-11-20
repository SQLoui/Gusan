/*Arreglos bidimensionales

tipo[renglo][columna]

ej:
mapa = char[7][6]*/
import java.util.Scanner;

class Gusano extends Thread{

	private final char [][] jardin;
	private final int c;//columnas
	private final int r;//renglones

	private boolean terminado = false;

	public boolean Terminado() {
        return terminado;
    }


	public Gusano(char [][] mapa){
		this.jardin= mapa;
		//obtener tamaños de la matriz
		c=jardin[0].length;
		r=jardin.length;
	}

	/*public void caminaY(int y){//Camina renglon
		for(int i=0;i<c;i++){
			jardin[i][y]='W';
			
		}
	}
	public void caminaX(int x){//Camina columna
		for(int i=0;i<r;i++){
		jardin[x][i]='W';
	}*/
	public void caminaY(int col) {
		for(int fila = 0; fila < r; fila++) {
	    	if (gusanicida(fila, col)) {
	            jardin[fila][col] = 'X';
	            return;
	        }
	        jardin[fila][col] = 'W';
    	}
	}
	public void caminaX(int fila) {
	    if (fila < 0 || fila >= r) return;
	    for (int col = 0; col < c; col++) {
	        if (gusanicida(fila, col)) {
	            jardin[fila][col] = 'X';
	            return;
	        }
	        jardin[fila][col] = 'W';
	    }
	}


	public void comerX(int x, int traga){
		for(int i=0;i<traga;i++){
			jardin[x][i]='C';
		}
	}
	public void comerY(int y, int traga){
		for(int i=0;i<traga;i++){
			jardin[i][y]='C';
		}
	}

	private boolean gusanicida(int row, int col) {
	    if (jardin[row][col] == 'R') {
	        terminado = true;//RIP Gusano :(
	        System.out.println("El gusano murió por una raíz en (" + row + ", " + col + ")");

	        synchronized (jardin) {
	            jardin.notifyAll();
	        }
	        return true;
	    }
	    return false;
	}

	

	@Override
	public void run(){
		int vida=10;
		int ini =0;
		while(vida>0){
			try{
				synchronized(jardin){
					caminaX(ini);
				}
				sleep(400);
				ini++;	

				/*synchronized(jardin){
					comerX(ini,2);
				}*/

				vida--;
				

			}catch(InterruptedException e){
				System.out.println("Interrupción");
			}catch(ArrayIndexOutOfBoundsException e){
				ini=0;
			}

		}

		synchronized (jardin) {
        	terminado = true;
        	jardin.notifyAll();
        }

		System.out.println("El gusano ha terminado su recorrido.");

	}

}

class MonitorM extends Thread{
	private final char [][] jardin;
	private final int c;//columnas
	private final int r;//renglones

	private final Gusano gusano;

	public MonitorM(char [][] mapa, Gusano gus){
		this.jardin= mapa;
		this.gusano = gus;
		c=jardin[0].length;
		r=jardin.length;
	}

	public void llenarMapa() {
		char espacio=' ';
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                jardin[i][j] = espacio; 
            }
        }
    }

    private void imprimirMapa() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                System.out.print("|" + jardin[i][j] + "|");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void MapaCoords() {
    	System.out.print("\033[H\033[2J");
        System.out.flush();
	    System.out.print("    ");
	    for (int j = 0; j < c; j++) {
	        System.out.printf("%2d ", j);
	    }
	    System.out.println("\n     ---------------------------   ");

	    for (int i = 0; i < r; i++) {
	        System.out.printf("%2d |", i);
	        for (int j = 0; j < c; j++) {
	            System.out.print(" " + jardin[i][j] + " ");
	        }
	        System.out.println();
	    }
	}

	public void plantarRaiz(int row, int col){
		if(row >= 0 && row < r && col >= 0 && col < c){
	        jardin[row][col] = 'R';
	    }else{
	        System.out.println("Coordenadas fuera del jardin.");
	    }
	}


	@Override
	public void run() {
		synchronized (jardin) {
            while (!gusano.Terminado()) {
                //imprimirMapa();
                MapaCoords();
                try {
                    jardin.wait(550);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

	}
}


public class Arbis{
	public static void main(String[] args){
	Scanner sc = new Scanner(System.in);
	char[][] mapa = new char[10][10];

	Gusano gus = new Gusano(mapa);
	MonitorM mon = new MonitorM(mapa,gus);

	mon.llenarMapa();

	System.out.println("¿Cuántas raíces deseas plantar?");
    int raices = sc.nextInt();

    for (int k = 0; k < raices; k++) {
    	mon.MapaCoords();
        System.out.println("\nRaíz #" + (k+1));
        System.out.print("Fila: ");
        int f = sc.nextInt();
        System.out.print("Columna: ");
        int c = sc.nextInt();
        mon.plantarRaiz(f, c);
        mon.MapaCoords();
    }

	System.out.println("\nENTER para iniciar al gusano…");
	sc.nextLine();
	sc.nextLine();
	gus.start();
	mon.start();

	}
}