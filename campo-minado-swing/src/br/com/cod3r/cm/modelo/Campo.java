package br.com.cod3r.cm.modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Campo {

	private final int linha;
	private final int coluna;
	
	private boolean aberto;
	private boolean minado;
	private boolean marcado;
	
	private List<Campo> vizinhos = new ArrayList<>();
	private Set<CampoObservador> observadores = new HashSet<>();
	
	Campo(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}
	
	public void regristrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}
	
	private void notificarObservadores(CampoEvento evento) {
		observadores.stream().forEach(o -> o.eventoOcorreu(this, evento));
	}
	
	boolean adicionarVizinho(Campo vizinho) {
		boolean linhaDiferente = vizinho.linha != linha;
		boolean colunaDiferente = vizinho.coluna != coluna;
		boolean ehDiagonal = linhaDiferente && colunaDiferente;
		
		int deltaLinha = Math.abs(vizinho.linha - linha); 
		int deltaColuna = Math.abs(vizinho.coluna - coluna);			
		int deltaAbsoluta = Math.abs(deltaLinha + deltaColuna);
		
		boolean ehVizinhoDiagonal = ehDiagonal && deltaAbsoluta == 2;
		boolean ehVizinhoPerpendicular = !ehDiagonal && deltaAbsoluta == 1;
		
		if (ehVizinhoDiagonal || ehVizinhoPerpendicular) {
			vizinhos.add(vizinho);
			return true; 
		}
		
		return false;
	}

	public void alternarMarcacao() {
		if (!aberto) {
			marcado = !marcado;
			
			if (marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			} else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
	}
	
	public boolean abrir() {		
		if (!aberto && !marcado) {
			if (minado) {
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}
			
			setAberto(true);
			
			if (vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());;
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean vizinhancaSegura() {
		return vizinhos.stream().filter(v -> v.minado).allMatch(v -> v.marcado);
	}
	
	void minar() {
		minado = true;	
	}
	
	void desminar() {
		minado = false;
	}
	
	public boolean isMarcado() {
		return marcado;
	}
	
	void setAberto(boolean aberto) {
		this.aberto = aberto;
		
		if (aberto) notificarObservadores(CampoEvento.ABRIR);
	}

	public boolean isAberto() {
		return aberto;
	}
	
	public boolean isFechado() {
		return !aberto;
	}
	
	public boolean isMinado() {
		return minado;
	}
	
	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}
	
	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}
	
	public int minasNaVizinhanca() {
		return (int) vizinhos.stream().filter(v -> v.minado).count();
	}
	
	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
		notificarObservadores(CampoEvento.REINICIAR);
	}
	
}
