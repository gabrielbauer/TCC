package TCC;

import java.util.ArrayList;
import java.util.List;

import main.grammar.Description;

public class BuscaLocal {
	
	final static boolean debug = false;
	
	final static List<String> listaNomesJogos = Funcoes.carregarListaJogos();
	final static List<String> listaAI = Funcoes.carregarListaAI();
	final static List<Description> jogosCarregados = Funcoes.carregarListaJogos(listaNomesJogos);
	
	public static Description alterarParametro(Description descricao) {
		//System.out.println("{\n\tkey: " + elemento.toString() + "\n\tvalue: " + elemento.split(":")[1] + ",\n},");
		
		final List<Object> listaTerminais = Funcoes.obterListaTerminaisJogo(Funcoes.obterCallTree(descricao));
		
		//System.out.println("**************************************");
		//System.out.println("[TCC] Call tree incial:");
		//Funcoes.percorrerCallTree(Funcoes.obterCallTree(descricao));
		int posicao = Funcoes.escolherNumeroIntervaloLista(listaTerminais.size());
		//System.out.println("[TCC] Terminal ["+ posicao +"] alterado: " + listaTerminais.get(posicao));
		final List<Object> listaTerminaisIguais = new ArrayList<Object>();
		Object elementoEscolhido = listaTerminais.get(posicao);
		for(Object elemento : listaTerminais) {
			if(elementoEscolhido.equals(elemento)) {
				listaTerminaisIguais.add(elemento);
			}
		}
		/*for(int indice = 0; indice < listaTerminais.size(); indice++) {
			if(listaTerminais.get(indice) == elementoEscolhido) {
				System.out.println("[TCC] listaTerminais [" + indice + "]: " + listaTerminais.get(indice));
			}
		}*/
		//System.out.println("[TCC] Elemento a ser alterado: " + posicao);
		int terminalAlterado = Funcoes.escolherNumeroIntervaloLista(listaTerminaisIguais.size());
		descricao.setCallTree(Funcoes.alterarTerminalCallTree(Funcoes.obterCallTree(descricao), elementoEscolhido, terminalAlterado));
		//System.out.println("[TCC] Call tree final:");
		//Funcoes.percorrerCallTree(Funcoes.obterCallTree(descricao));
		//System.out.println("**************************************");
		
		return descricao;
	}
	
	public static void analisarJogoIndividual(Description descricao, String nomeAI) {
		final String diretorioExperimento = Funcoes.criarDiretorioExperimento();
		if(diretorioExperimento == "") {
			return;
		}
		
		Description descricaoAtual = descricao;
		Funcoes.criarAnalise(descricaoAtual, nomeAI, 15, 40);
		float scoreAtual = Funcoes.carregarScoreAvaliacao();
		System.out.println("Score joguinho: " + scoreAtual);
	}
	
	public static void realizarExperimento(String nomeAI) {
		
		for(Description descricao : jogosCarregados) {
			hillClimbing(descricao, nomeAI, 20); 
		}

	}
	
	public static void hillClimbing(Description descricao, String nomeAI, int numeroIteracoes) {
		final String diretorioExperimento = Funcoes.criarDiretorioExperimento();
		if(diretorioExperimento == "") {
			return;
		}
		
		Description descricaoAtual = descricao;
		Funcoes.criarAnalise(descricaoAtual, nomeAI, 10, 40);
		float scoreAtual = Funcoes.carregarScoreAvaliacao();
		
		if(Funcoes.salvarJogo(Funcoes.gerarNomeArquivo(), descricaoAtual, diretorioExperimento)) {
			
		}
		
		System.out.println("Score inicial: " + scoreAtual);
		
		float scoreBusca = 0;
		
		for(int iteracao = 0; iteracao <= numeroIteracoes; iteracao++) {
			System.out.println("Iteração " + iteracao);
			Description descricaoBusca = alterarParametro(descricaoAtual);
			if(Funcoes.salvarJogo(Funcoes.gerarNomeArquivo(), descricaoBusca, diretorioExperimento)) {
				
			}
			
			Funcoes.criarAnalise(descricaoBusca, nomeAI, 10, 40);
			scoreBusca = Funcoes.carregarScoreAvaliacao();
		
			if(scoreBusca > scoreAtual) {
				System.out.println("[TCC] " + scoreBusca + " > " + scoreAtual);
				descricaoAtual = descricaoBusca;
				scoreAtual = scoreBusca;
			}
		}
	}
	
	public static void main(String[] args) {
		String nomeAI = "UCT";
		
		if (debug) {
			nomeAI = Funcoes.selecionarAI();
		}

		long tempoInicial = System.nanoTime();
		realizarExperimento(nomeAI);
		long tempoExecucao = System.nanoTime() - tempoInicial;
		
		System.out.println("Tempo decorrido: " + tempoExecucao/1_000_000_000 + " s");
	}

}
