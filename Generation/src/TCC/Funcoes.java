package TCC;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Calendar;
import java.util.Collections;
import java.lang.Math;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import compiler.Compiler;

import game.Game;

import main.grammar.Description;
import main.grammar.Report;
import main.options.UserSelections;
import main.grammar.Call;
import main.grammar.Call.CallType;

import metrics.Evaluation;
import metrics.Metric;

import other.GameLoader;
import other.context.Context;
import other.move.Move;
import other.trial.Trial;

import supplementary.experiments.EvalGamesThread;


@SuppressWarnings("unused")

/**
 * Funções de utilidade para aplicar no pipeline.
 * 
 * @author Gabriel.Bauer
 */

public class Funcoes {
	final static int MAX_MOVES 							= 2000;
	final static String pastaBaseJogos					= System.getProperty("user.dir") + "/../gabriel_games_TCC/";
	final static String pastaBaseExperimentos			= System.getProperty("user.dir") + "/../gabriel_games_TCC/experimentos/";
	final static String arquivoNomesJogos 				= "listajogos.txt";
	final static String arquivoListaAI 					= "listaAI.txt";
	final static String arquivoResultadoAvaliacao		= "resultadoAvaliacao.txt";
	static int indiceExperimentos 						= obterIndiceExperimento();
	
	final static List<String> listaJogos 				= new ArrayList<String>();
	final static List<String> listaAI 					= new ArrayList<String>();
	final static List<String> listaResultados			= new ArrayList<String>();
	
	final static List<String> listaTerminaisNumericos 	= new ArrayList<String>(Arrays.asList(
	"Integer", "DimConstant", "IntConstant"));
	//, "Value", "BooleanConstant", "Boolean"
	final static List<String> listaTerminaisUnicos 		= new ArrayList<String>();
	final static List<String> evaluationOutput 			= new ArrayList<String>();
	static List<Object> listaTerminaisObjetos 			= new ArrayList<Object>();
	
	//Variáveis globais para busca local
	static List<String> listaTerminaisJogo 				= new ArrayList<String>();
	static int indiceElementoCallTree 					= 0;
	static int indiceAtualElementoCallTree 				= 0;
	static Object elementoAlterado 						= new Object();
	
	/**
	 * @param nome: nome do jogo.lud. Deve se encontrar na pasta gabriel_games_TCC.
	 * @return game.
	 **/
	
	public final static Game carregarJogoPorNome(String nomeDoJogo) {
		final String caminho = pastaBaseJogos + nomeDoJogo;
		File arquivo = new File(caminho);
		
		//TODO wrap with try catch
		Game game = GameLoader.loadGameFromFile(arquivo);
		
		return game;
	
	}
	
	public final static Game carregarJogoPorDescricao(Description description) {
		final UserSelections userSelections = new UserSelections(new ArrayList<String>());
		final Report report = new Report();
		final boolean isVerbose = false;
		Game gameCompiled = (Game)Compiler.compile(description, userSelections, report, isVerbose);
		
		return (Game) gameCompiled;
	}
	
	public final static List<String> carregarListaAI(){
		
		final String caminho = pastaBaseJogos + arquivoListaAI;
		
		try {
			File myObj = new File(caminho);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				listaAI.add(data);
			}
			myReader.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return listaAI;
	}
	
	/**
	 * @param name: nome do arquivo.
	 * @return listaJogos: Lista com os nomes dos jogos.
	**/
	
	public final static List<String> carregarListaJogos(){
		
		final String caminho = pastaBaseJogos + arquivoNomesJogos;
		
		try {
			File myObj = new File(caminho);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				listaJogos.add(data);
			}
			myReader.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return listaJogos;
	}
	
	public final static ArrayList<Description> carregarListaJogos(final List<String> ListaJogos) {
		
		ArrayList<Description> ListaDescricoesJogos = new ArrayList<Description>();
		
		for(String gameName : ListaJogos) {
			Game game = carregarJogoPorNome(gameName);
			ListaDescricoesJogos.add(game.description());
		}
		
		return ListaDescricoesJogos;
	}

	public final static List<String> carregarResultadoAvaliacao(){
		
		int numeroExperimento = indiceExperimentos;
		
		final String caminho = pastaBaseExperimentos + String.valueOf(numeroExperimento) + "/" + arquivoResultadoAvaliacao;
		
		try {
			File myObj = new File(caminho);
			Scanner myReader = new Scanner(myObj);
			System.out.println("******************************");
			System.out.println("[TCC] Resultado avaliação:");
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				System.out.println(data);
				listaResultados.add(data);
			}
			myReader.close();
			System.out.println("******************************");
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return listaResultados;
	}

	public final static void listaParametrosCallTree(Call calltree){
		
		percorrerCallTree(calltree);
		
	}
	
	public final static float carregarScoreAvaliacao(){
		
		int numeroExperimento = indiceExperimentos;
		
		float score = 0;
		
		final String caminho = pastaBaseExperimentos + String.valueOf(numeroExperimento) + "/" + arquivoResultadoAvaliacao;
		
		try {
			File myObj = new File(caminho);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				score = Float.valueOf(myReader.nextLine());
			}
			myReader.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return score;
	}
	
	//public final static List<Call> carregarParametrosBuscaLocal(){
		
	//}
	
	public final static boolean salvarJogo(String nomeJogo, Description description, String pastaExperimento) {
		final String caminhoArquivo = pastaExperimento + nomeJogo + ".lud";
		try {
			FileWriter myWriter = new FileWriter(caminhoArquivo, false);
			
			for(String ludeme:description.callTree().ludemeFormat(20)) {
				myWriter.write(ludeme);
				myWriter.write("\n");
			}

			myWriter.close();
			System.out.println("[TCC] Jogo salvo com sucesso.\n");
			return true;
		} catch (IOException e) {
			System.out.println("[TCC] Erro durante salvamento do jogo.\n");
			e.printStackTrace();
			return false;
		}
	}
	
	public final static void separarAvaliacao() {
		int numeroExperimento = indiceExperimentos;
		
		final String caminho = pastaBaseExperimentos + String.valueOf(numeroExperimento) + "/" + arquivoResultadoAvaliacao;
		try {
			FileWriter myWriter = new FileWriter(caminho, true);
			myWriter.write("\n");
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final static String gerarNomeArquivo() {
		String nomeArquivo = "";
		
		Calendar calendar = Calendar.getInstance();
		
		nomeArquivo+= calendar.get(Calendar.YEAR);
		nomeArquivo+="_";
		nomeArquivo+= calendar.get(Calendar.MONTH);
		nomeArquivo+="_";
		nomeArquivo+= calendar.get(Calendar.DAY_OF_MONTH);
		nomeArquivo+="_";
		nomeArquivo+= calendar.get(Calendar.HOUR_OF_DAY);
		nomeArquivo+="_";
		nomeArquivo+= calendar.get(Calendar.MINUTE);
		nomeArquivo+="_";
		nomeArquivo+= calendar.get(Calendar.SECOND);
		
		System.out.println("[TCC] Jogo: " + nomeArquivo + ".lud\n");
		
		return nomeArquivo;
	}
	
	public final static int obterIndiceExperimento() {
		final File[] experimentos = new File(pastaBaseExperimentos).listFiles();
		
		final List<Integer> experimentosInt = new ArrayList<Integer>();
		
		for(File arquivo : experimentos) {
			if(arquivo.isDirectory()){
				experimentosInt.add(Integer.valueOf(arquivo.getName()));
			}
		}
		Collections.sort(experimentosInt);
		final int indice = experimentosInt.get(experimentosInt.size() - 1) + 1;
		return indice;
	}
	
	public final static String criarDiretorioExperimento() {
		try {
			final int indiceExperimento = indiceExperimentos;
			final String path = pastaBaseExperimentos + indiceExperimento + "/";
			Files.createDirectory(FileSystems.getDefault().getPath(path));
			return path;
		} catch(IOException e) {
			 e.printStackTrace();
			 return "";
	    }
	}
	
	public final static void avaliarJogo(Game game, final Evaluation evaluation, final Report report, final List<String> options,
		final int numberTrials, final int maxTurns, final double thinkTime, final List<Metric> metricsToEvaluate, 
		final ArrayList<Double> weights, final String AIName, final boolean useDatabaseGames) {
		
		//TODO checar no livro sobre os pesos
		for (int i = 0; i < 9; i++) {
			weights.add(1.0);
		}
		
		final String gameName = game.name();
		System.out.println("[TCC] Iniciando análise.");
		final EvalGamesThread evalThread = 	EvalGamesThread.construct
				(
					evaluation, report, game, options, AIName, 
					numberTrials, thinkTime, maxTurns,
					metricsToEvaluate, weights, useDatabaseGames
				);
		
		evalThread.setDaemon(true);
		evalThread.run();
		if(!evalThread.isAlive()) {
			System.out.println("[TCC] Avaliação terminada.\n");
			carregarResultadoAvaliacao();
		}
	}
		
	public static void mostrarListaAI() {
		if(!listaAI.isEmpty()) {
			System.out.println("[TCC] Lista IAs:");
			int indice = 1;
			for(String nomeAI : listaAI) {
				System.out.println(indice + ": " + nomeAI);
				indice++;
			}
		}
		else {
			System.out.println("[TCC] Lista de IAs não inicializada.\n");
		}
	}
	
	public static String selecionarAI(){
		mostrarListaAI();
		System.out.println("[TCC] Selecione uma IA: ");
		Scanner objeto = new Scanner(System.in);
	    int opcao = Integer.valueOf(objeto.nextLine());  // Read user input
	    objeto.close();
	    
	    return listaAI.get(opcao - 1);
	}
	
	public static void criarAnalise(Description description, String nomeAI, int numeroTentativas, int maximoTurnos) {
		final Report report = new Report();
		int indice = 0;
		
		//for (final Description description : jogosCarregados) {
		
		final Game game = Funcoes.carregarJogoPorDescricao(description);
		final Evaluation evaluation = new Evaluation();
		final List<String> options = new ArrayList<String>();
		final int numberTrials = numeroTentativas;
		final int maxTurns = maximoTurnos;
		final double thinkTime = 0.5;
		final List<Metric> metricsToEvaluate = new Evaluation().dialogMetrics();
		final ArrayList<Double> weights = new ArrayList<Double>();
		final boolean useDatabaseGames = false;
		
		avaliarJogo(game, evaluation, report, options, numberTrials, maxTurns, thinkTime,
				metricsToEvaluate, weights, nomeAI, useDatabaseGames);
		
		//}
	}
	
	public static void imprimirTokenTree(Description description) {
		System.out.println(description.tokenForest().tokenTree().toString());
	}
	
	public static void percorrerCallTree(Call root) {
		for(Call arg : root.args()) {
			if(arg.type() == CallType.Class) {
				percorrerCallTree(arg);
			}
			if(arg.type() == CallType.Array) {
				percorrerCallTree(arg);
			}
			if(arg.type() == CallType.Terminal) {
				verificarTerminalLista(arg.symbol().name());
				if(listaTerminaisNumericos.contains(arg.symbol().name())) {
					System.out.println(arg.symbol().name() + ": "+ arg.object().toString());	
				}
			}
		}
	}
	
	public static void completarListaTerminaisCallTree(Call root) {
		
		for(Call arg : root.args()) {
			if(arg.type() == CallType.Class) {
				completarListaTerminaisCallTree(arg);
			}
			if(arg.type() == CallType.Array) {
				completarListaTerminaisCallTree(arg);
			}
			if(arg.type() == CallType.Terminal) {
				verificarTerminalLista(arg.symbol().name());
				if(listaTerminaisNumericos.contains(arg.symbol().name())) {
					listaTerminaisObjetos.add(arg);
					//System.out.print(arg.toString());
					String elemento = "";
					elemento += arg.expected().getSimpleName() + ":" + arg.object().toString();
					listaTerminaisJogo.add(elemento);
				}
			}
		}
	}
	
	public static void alterarCallTree(Call root) {
		for(Call arg : root.args()) {
			if(arg.type() == CallType.Class) {
				alterarCallTree(arg);
			}
			if(arg.type() == CallType.Array) {
				alterarCallTree(arg);
			}
			if(arg.type() == CallType.Terminal) {
				if(listaTerminaisNumericos.contains(arg.symbol().name())) {
					if(arg.equals(elementoAlterado)) {
						if(indiceAtualElementoCallTree == indiceElementoCallTree) {
							//System.out.println("[TCC] Alterando " + arg.toString());
							int sinal = 1;
							final int valorSimbolo = Integer.parseInt(arg.object().toString());
							
							if(valorSimbolo < 0) {
								sinal = -1;
							}
							int novoValor = gerarInteiro(Integer.parseInt(arg.object().toString()));
							if (novoValor == 0) {
								novoValor = gerarInteiro(3)*sinal;
							}
							//System.out.println("Setting " + arg.object().toString() + " to " + novoValor);
							
							Object obj = (Object) novoValor;
							arg.setObject(obj);
							break;
						}
						else {
							indiceAtualElementoCallTree += 1;
						}
					}
				}
			}
		}
	}
	
	public static List<Object> obterListaTerminaisJogo(Call root){
		listaTerminaisObjetos.clear();
		completarListaTerminaisCallTree(root);
		return listaTerminaisObjetos;
	}
	
	public static Call alterarTerminalCallTree(Call root, Object elementoEscolhido, int posicaoElemento){
		indiceElementoCallTree = posicaoElemento;
		indiceAtualElementoCallTree = 0;
		elementoAlterado = elementoEscolhido;
		//System.out.println("Alterando o elemento na posição " + indiceElementoCallTree);
		alterarCallTree(root);
		return root;
	}
	
	public static Call obterCallTree(Description descricao) {
		return descricao.callTree();
	}
	
	public static void verificarTerminalLista(String terminal) {
		if(!listaTerminaisUnicos.contains(terminal)){
			listaTerminaisUnicos.add(terminal);
		}
	}
	
	public static void preencherListaTerminais(List<Description> listaDescricoes) {
		for (Description descricao : listaDescricoes) {
			percorrerCallTree(descricao.callTree());
		}
	}
	
	public static void mostrarListaTerminais() {
		if(listaTerminaisUnicos.isEmpty()) {
			System.out.println("[TCC] Lista de terminais não inicializada.");
		}
		else {
			System.out.println("******************************");
			System.out.println("[TCC] Lista de terminais sem repetição");
			for (String terminal : listaTerminaisUnicos) {
				System.out.println(terminal);
			}
			System.out.println("******************************");
		}
	}
	
	public static int gerarInteiro(int valorOriginal) {
		double min = valorOriginal * 0.4;
		double max = valorOriginal * 1.6;
		return (int) (Math.random() * (max - min + 1) + min);
	}
	
	public static int escolherNumeroIntervaloLista(int quantidadeElementos) {
		Random rand = new Random();
		int posicao = rand.nextInt(quantidadeElementos);
		return posicao;
	}

}