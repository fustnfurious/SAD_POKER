
import java.util.ArrayList;
import java.util.Collections;

public class Taula {
	
	public final int ESCALA_REIAL = 9;
	public final int ESCALA_COLOR = 8;
	public final int POKER = 7;
	public final int FULL = 6;
	public final int COLOR = 5;
	public final int ESCALA = 4;
	public final int TRIO = 3;
	public final int DOBLE_PARELLA = 2;
	public final int PARELLA = 1;
	public final int CARTA_ALTA = 0;
	
	protected ArrayList<Client> jugadors;
	protected ArrayList<Carta> baralla;
	protected int diners;
	protected int aposta_activa;
	protected ArrayList<Carta> cartes_sobre_taula;
	
	public Taula(){
		this.diners = 0;
		this.aposta_activa = 0;
		this.cartes_sobre_taula = new ArrayList<>();
	}	
	
	public ArrayList<ClientThread> guanyador(ArrayList<ClientThread> jugadors_finals, ArrayList<Carta> cartes_taula) {
		int guanyador=0;
		Rank guany = rankMa(jugadors_finals.get(0).jugador.getMa(), cartes_taula);
		ArrayList<ClientThread> guanyadors = new ArrayList<>();
		for(int i=1; i<jugadors_finals.size(); i++) {
			if(guany.compareTo(rankMa(jugadors_finals.get(i).jugador.getMa(), cartes_taula))<0) {
				guany=rankMa(jugadors_finals.get(i).jugador.getMa(), cartes_taula);
				guanyador=i;
			}
		}
		guanyadors.add(jugadors_finals.get(guanyador));
		for(int i=0; i<jugadors_finals.size(); i++) {
			if(guany.compareTo(rankMa(jugadors_finals.get(i).jugador.getMa(), cartes_taula))==0 && i!=guanyador) {
				guanyadors.add(jugadors_finals.get(i));
			}
		}
		//System.out.println(guany.toString());
		return guanyadors;
	}
	
	public class Rank implements Comparable<Rank>{
		protected int rank;
		protected int numCartaAlta;
		
		public Rank(int rank, int num) {
			this.rank=rank;
			this.numCartaAlta=num;
		}
		
		@Override
		public int compareTo(Rank ra) {
			if(this.rank != ra.rank) {
				return this.rank-ra.rank;
			} else {
				return this.numCartaAlta-ra.numCartaAlta;
			}
		}
		
		public String toString() {
			String ran;
			switch(this.rank) {
			case PARELLA: ran="Parella";
			break;
			case DOBLE_PARELLA: ran="Doble Parella";
			break;
			case TRIO: ran="Trio";
			break;
			case ESCALA: ran="Escala";
			break;
			case COLOR: ran="Color";
			break;
			case FULL: ran="Full";
			break;
			case POKER: ran="Poker";
			break;
			case ESCALA_COLOR: ran="Escala Color";
			break;
			case ESCALA_REIAL: ran="Escala Reial";
			break;
			default: ran="Carta Alta";
			}
			String num;
			switch(this.numCartaAlta) {
			case 14: num = "As";
			break;
			case 13: num = "K";
			break;
			case 12: num = "Q";
			break;
			case 11: num = "J";
			break;
			default: num = ""+this.numCartaAlta;
			}
			return "\nRanking -> " + ran + "  Carta Alta: " + num + "\n";
		}
	}
	
	@SuppressWarnings("unchecked")
	public Rank rankMa(Ma ma, ArrayList<Carta> pub) {
		ArrayList<Carta> tot = new ArrayList<>();
		tot.add(ma.carta1);
		tot.add(ma.carta2);
		for(int i=0; i<pub.size(); i++) {
			tot.add(pub.get(i));
		}
		
		ArrayList<Rank> ranks = new ArrayList<>();
		ranks.add(parellesTriosPokersFulls(tot));
		ranks.add(escales((ArrayList<Carta>) tot.clone()));
		ranks.add(color((ArrayList<Carta>) tot.clone()));
		Collections.sort(ranks);
		return ranks.get(ranks.size()-1);
	}
	
	
	public Rank parellesTriosPokersFulls(ArrayList<Carta> tot) {
		boolean parella=false;
		boolean dobleParella=false;
		boolean trio=false;
		boolean full=false;
		int iguals=0;
		int numCartaAlta=0;
		
		Collections.sort(tot);
		
		for(int i=0; i<tot.size()-1; i++) {
			for(int j=i+1;j<tot.size();j++) {
				if(tot.get(i).getNum()==tot.get(j).getNum()) {
					iguals++;
				} else {
					break;
				}
			}
			switch(iguals) {
			case 1: {
				if(trio) {
					full=true;
				} else if (parella) {
					dobleParella=true;
					if(numCartaAlta<tot.get(i).getNum()) {
						numCartaAlta=tot.get(i).getNum();
					}
				} else {
					parella=true;
					numCartaAlta=tot.get(i).getNum();
				}
			}
			break;
			case 2: {
				if(trio) {
					if(numCartaAlta<tot.get(i).getNum()) {
						numCartaAlta=tot.get(i).getNum();
					}
				} else {
					trio=true;
					numCartaAlta=tot.get(i).getNum();
				}
				if(parella) {
					full=true;
				}
				
			}
			break;
			case 3: {
				return new Rank(POKER, tot.get(i).getNum());
			}
			}
			i+=iguals;
			iguals=0;
			
		}
		
		if(full) {
			return new Rank(FULL, numCartaAlta);
		}
		if(trio) {
			return new Rank(TRIO, numCartaAlta);
		}
		if(dobleParella) {
			return new Rank(DOBLE_PARELLA, numCartaAlta);
		}
		if(parella) {
			return new Rank(PARELLA, numCartaAlta);
		} else {
			return new Rank(CARTA_ALTA, tot.get(6).getNum());
		}
		
	}

	@SuppressWarnings("unchecked")
	public Rank color(ArrayList<Carta> tot) {
		for(int i=1; i<5; i++) {
			ArrayList<Carta> aux = (ArrayList<Carta>) tot.clone();
			for(int j=0; j<aux.size(); j++) { //esborrem el que no sigui del pal
				if(aux.get(j).getPal() != i) {
					aux.remove(j);
					j--;
				}
			}
			if(aux.size()>4) {
				Collections.sort(aux);
				return new Rank(COLOR, aux.get(aux.size()-1).getNum());
			}
		}
		return new Rank(0,0);
	}
	
	public Rank escales(ArrayList<Carta> tot) {
		boolean escala=false;
		int cartaAlta;
		for(int i=0; i<tot.size();i++) { //afegit "1" si hi ha assos (as=14 en la resta de funcions)
			if(tot.get(i).getNum()==14) {
				tot.add(new Carta(1, tot.get(i).getPal()));
			}
		}
		Collections.sort(tot);
		cartaAlta = tot.get(tot.size()-1).getNum();
		ArrayList<Carta> cinc = tot;
		
		for(int i=0; i<cinc.size();i++) { //esborrar numeros repetits
			for(int j=i+1; j<cinc.size(); j++) {
				if(cinc.get(i).getNum() == cinc.get(j).getNum()) {
					cinc.remove(j);
					j--;
				} else {
					break;
				}
			}
			
		}
		//trobar escales
		for(int i=cinc.size()-1; i>cinc.size()-4;i--) { //itera nomes 3 vegades (escala de 5 en 7 cartes)
			if(cinc.size() == 7) {
				if(cinc.get(i).getNum()-4 == cinc.get(i-4).getNum()) {
					escala=true;
					cartaAlta=cinc.get(i).getNum();
					break;
				}
			}else if(cinc.size() == 6 && i > 3) {
				if(cinc.get(i).getNum()-4 == cinc.get(i-4).getNum()) {
					escala=true;
					cartaAlta=cinc.get(i).getNum();
					break;
				}
			}else if(cinc.size() == 5 && i > 3) {
				if(cinc.get(i).getNum()-4 == cinc.get(i-4).getNum()) {
					escala=true;
					cartaAlta=cinc.get(i).getNum();
					break;
				}
			}
		}
		
		if(escala) {
			ArrayList<Carta> aux = tot;
			for(int i=0; i<4; i++) { // mirem els quatre pals per separat
				for(int j=0; j<aux.size(); j++) { //esborrem el que no sigui del pal
					if(aux.get(j).getPal()!=i) {
						aux.remove(j);
						j--;
					}
				}
				if(aux.size()<5) { // si no queden com a minim 5 no cal seguir
					break;
				}
				for(int k=aux.size()-1; k>aux.size()-4;k--) { //busquem escala en el que queda com abans
					if(aux.get(k).getNum()-4 == aux.get(k-4).getNum()) {
						if(cartaAlta!=14) {
							return new Rank (ESCALA_COLOR, cartaAlta);
						} else return new Rank (ESCALA_REIAL, 14);
					}
				}
				aux=tot;
			}
		}
		if(!escala) {
			return new Rank(CARTA_ALTA, cartaAlta);
		} else {
			return new Rank(ESCALA, cartaAlta);
		}
	}
	
	
	public int afegir_diners_taula(int aposta) {
		this.diners += aposta;
		return this.diners;
	}
	
	public int get_diners_taula() {
		return this.diners;
	}
	
	public void update_aposta_activa(int aposta) {
		this.aposta_activa = aposta;
	}
	
	public int get_aposta_activa() {
		return this.aposta_activa;
	}
	
	public ArrayList<Carta> getCartesTaula(){
		return this.cartes_sobre_taula;
	}
	
	public void reset_diners_taula() {
		this.diners=0;
	}
	
}
