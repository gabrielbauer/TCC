package TCC;

import java.util.ArrayList;
import java.util.List;

import main.grammar.Call;
import main.grammar.Description;

public class BuscaLocal {
	
	final static int NUMERO_ITERACOES = 80;
	
	final static boolean debug = false;
	
	final static List<String> listaNomesJogos = Funcoes.carregarListaJogos();
	final static List<Description> jogosCarregados = Funcoes.carregarListaJogos(listaNomesJogos);
	final static List<String> listaAI = Funcoes.carregarListaAI();
	
	public static void printarAsteriscos() {
		System.out.println("******************************");
	}
	public static void funcaoParaTestes() {
		Description descricao = jogosCarregados.get(0);
		final List<Object> listaTerminais = Funcoes.obterListaTerminaisObjetosJogo(Funcoes.obterCallTree(descricao));
		for(int i = 0; i < 20; i++) {
			int posicao = Funcoes.escolherNumeroIntervaloLista(listaTerminais.size());
			
			final List<Object> listaTerminaisIguais = new ArrayList<Object>();
			Object elementoEscolhido = listaTerminais.get(posicao);
			
			printarAsteriscos();
			System.out.println("[TCC] Terminal ["+ posicao +"] alterado: \t\t " + ((Call) listaTerminais.get(posicao)).object().toString());
			
			for(Object elemento : listaTerminais) {
				if(elementoEscolhido.equals(elemento)) {
					listaTerminaisIguais.add(elemento);
				}
			}
			int terminalAlterado = Funcoes.escolherNumeroIntervaloLista(listaTerminaisIguais.size());
			descricao.setCallTree(Funcoes.alterarTerminalCallTree(Funcoes.obterCallTree(descricao), elementoEscolhido, terminalAlterado));
		}
		
	}
	
	public static Description alterarParametro(Description descricao) {
		//System.out.println("{\n\tkey: " + elemento.toString() + "\n\tvalue: " + elemento.split(":")[1] + ",\n},");
		
		final List<Object> listaTerminais = Funcoes.obterListaTerminaisObjetosJogo(Funcoes.obterCallTree(descricao));
		
		int posicao = Funcoes.escolherNumeroIntervaloLista(listaTerminais.size());
		//System.out.println("[TCC] Terminal ["+ posicao +"] alterado: " + listaTerminais.get(posicao));
		final List<Object> listaTerminaisIguais = new ArrayList<Object>();
		Object elementoEscolhido = listaTerminais.get(posicao);
		for(Object elemento : listaTerminais) {
			if(elementoEscolhido.equals(elemento)) {
				listaTerminaisIguais.add(elemento);
			}
		}
		int terminalAlterado = Funcoes.escolherNumeroIntervaloLista(listaTerminaisIguais.size());
		descricao.setCallTree(Funcoes.alterarTerminalCallTree(Funcoes.obterCallTree(descricao), elementoEscolhido, terminalAlterado));		
		return descricao;
	}
	
	public static void analisarJogoIndividual(Description descricao, String nomeAI) {
		final String diretorioExperimento = Funcoes.criarDiretorioExperimento();
		if(diretorioExperimento == "") {
			return;
		}
				
		hillClimbing(descricao, nomeAI, NUMERO_ITERACOES);
	}
	
	public static void realizarExperimento(String nomeAI) {
		
		for(Description descricao : jogosCarregados) {
			hillClimbing(descricao, nomeAI, NUMERO_ITERACOES); 
		}

	}
	
	public static void hillClimbing(Description descricao, String nomeAI, int numeroIteracoes) {
		final String diretorioExperimento = Funcoes.criarDiretorioExperimento();
		if(diretorioExperimento == "") {
			return;
		}
		
		Description descricaoAtual = descricao;
		Funcoes.criarAnalise(descricaoAtual, nomeAI, 15, 40);
		float scoreAtual = Funcoes.carregarScoreAvaliacao();
		
		if(Funcoes.salvarJogo(Funcoes.gerarNomeArquivo(), descricaoAtual, diretorioExperimento)) {
			
		}
		
		System.out.println("Score inicial: " + scoreAtual);
		
		float scoreBusca = 0;
		
		for(int iteracao = 0; iteracao <= numeroIteracoes; iteracao++) {
			System.out.println("Iteração " + iteracao + "/" + numeroIteracoes);
			Description descricaoBusca = alterarParametro(descricaoAtual);
			if(Funcoes.salvarJogo(Funcoes.gerarNomeArquivo(), descricaoBusca, diretorioExperimento)) {
				
			}
			
			Funcoes.criarAnalise(descricaoBusca, nomeAI, 15, 40);
			scoreBusca = Funcoes.carregarScoreAvaliacao();
		
			if(scoreBusca > scoreAtual) {
				System.out.println("[TCC] " + scoreBusca + " > " + scoreAtual);
				System.out.println("[TCC] Descricao atual atualizada");
				descricaoAtual = descricaoBusca;
				scoreAtual = scoreBusca;
			}
		}
		if(Funcoes.salvarJogo("descricao_final", descricaoAtual, diretorioExperimento)) {
			
		}
	}
	
	public static void main(String[] args) {
		String nomeAI = "UCT";
		
		if (debug) {
			nomeAI = Funcoes.selecionarAI();
		}

		long tempoInicial = System.nanoTime();
		realizarExperimento(nomeAI);
		//funcaoParaTestes();
		long tempoExecucao = System.nanoTime() - tempoInicial;
		
		System.out.println("Tempo decorrido: " + tempoExecucao/1_000_000_000 + " s");
	}

}
