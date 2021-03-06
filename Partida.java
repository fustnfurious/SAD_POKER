
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;


public class Partida extends Thread{
	
	protected ArrayList<ClientThread> jugadors;
//	private Joc joc_actiu;
	private boolean nou_primer_torn = true, game_active;	
	boolean exit_partida = true;
	protected int index_torn_absolut;
	
	
	public Partida() {
		this.jugadors = new ArrayList<>();
	}
		
	private int canviar_primer_torn() {
//		System.out.println(jugadors_actius.size());
		for(int i=0 ; i < jugadors.size() ; i++) {
//			System.out.println(i);
			if(jugadors.get(i).jugador.primerTorn == true) {
				jugadors.get(i).jugador.primerTorn = false;
				jugadors.get((i+1)%jugadors.size()).jugador.primerTorn = true;
				return i+1;
			}
		}
		if(nou_primer_torn == true) {
			jugadors.get(0).jugador.primerTorn = true;
			nou_primer_torn = false;
			return 0;
		}
		return -1;
	}
	
	public void run() {
		while(exit_partida) {
			for(int i=0 ; i<jugadors.size() ; i++) {
				boolean al_carrer = jugadors.get(i).comprovar_si_them_de_xutar();
				if(al_carrer == true) {
					try {
						jugadors.get(i).in_server.close();
						jugadors.get(i).out_server.close();
						jugadors.get(i).s.close();
						jugadors.get(i).run = false;
						jugadors.remove(i);
						i = i%jugadors.size();
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(jugadors.get(i).s.isClosed()) {
					jugadors.get(i).run = false;
					jugadors.remove(i);
					
				}
			}
			if(jugadors.size() >= 2 && game_active == false) {
				game_active = true;
				index_torn_absolut = canviar_primer_torn();
				Joc joc_actiu = new Joc(jugadors);
				System.out.println("Iniciant partida...");
				try {
					joc_actiu.generar_joc(index_torn_absolut);
					game_active = false;
					Thread.sleep(15000);
				} catch (SocketException | EOFException e) {
					System.out.println("ha marxat un jugador");
					for(int i=0 ; i<jugadors.size() ; i++) {
						if(jugadors.get(i).s.isClosed()) {
							jugadors.remove(i);
							jugadors.get(i).run = false;
							game_active =   false;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			} else {
				System.out.println("esperant jugadors...");
				try {
					for(int i=14; i>0; i--) {
						int xifres = (i/10<1)?1:2;
						System.out.print(i);
						if(xifres==1) {
							System.out.print(" ");
						}
						System.out.print("\u001B["+2+"D");
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
