package TCC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import compiler.Compiler;
import game.Game;
import main.grammar.Description;
import main.grammar.Report;
import main.options.UserSelections;
import metrics.Evaluation;
import metrics.Metric;
import other.GameLoader;
import other.context.Context;
import other.move.Move;
import other.trial.Trial;
import supplementary.experiments.EvalGamesThread;

@SuppressWarnings("unused")

/**
 * Testando variações em jogos.
 * Adquirindo conhecimento sobre os mesmos.
 * 
 * @author Gabriel.Bauer
 */

public class Funcoes {
	final static int MAX_MOVES = 2000;
	final static String pastaBase = "/home/gabriel/Documents/TCC_Ludii/gabriel_games_TCC/";
	final static String arquivoNomesJogos = "listajogos.txt";
	final static String arquivoListaAI = "listaAI.txt";
	
	/**
	 * @param nome: nome do jogo.lud. Deve se encontrar na pasta gabriel_games_TCC.
	 * @return game.
	 **/
	
	public final static Game carregarJogoPorNome(String nomeDoJogo) {
		final String caminho = pastaBase + nomeDoJogo;
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
		final List<String> listaAI = new ArrayList<String>();
		
		final String caminho = pastaBase + arquivoListaAI;
		
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
		
		final String caminho = pastaBase + arquivoNomesJogos;
		
		List<String> ListaJogos = new ArrayList<String>();
		try {
			File myObj = new File(caminho);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				ListaJogos.add(data);
			}
			myReader.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return ListaJogos;
	}
	
	public final static ArrayList<Description> carregarListaJogos(final List<String> ListaJogos) {
		
		ArrayList<Description> ListaDescricoesJogos = new ArrayList<Description>();
		
		for(String gameName : ListaJogos) {
			Game game = carregarJogoPorNome(gameName);
			ListaDescricoesJogos.add(game.description());
		}
		
		return ListaDescricoesJogos;
	}
	
	public final static void avaliarJogo(Game game, final Evaluation evaluation, final Report report, final List<String> options,
		final int numberTrials, final int maxTurns, final double thinkTime, final List<Metric> metricsToEvaluate, 
		final ArrayList<Double> weights, final String AIName, final boolean useDatabaseGames) {
		
		//TODO checar no livro sobre os pesos
		for (int i = 0; i < 9; i++) {
			weights.add(1.0);
		}
		
		final String gameName = game.name();
		final EvalGamesThread evalThread = 	EvalGamesThread.construct
				(
					evaluation, report, game, options, AIName, 
					numberTrials, thinkTime, maxTurns,
					metricsToEvaluate, weights, useDatabaseGames
				);
		
		evalThread.setDaemon(true);
		evalThread.run();
		
	}
	
	public static void salvarLog() {
			
	}

	// ************************************ COPY PASTE ************************************
	
	// @return Whether a trial of the game can be played without crashing.
	public static boolean isFunctionalAndWithOnlyDecision(final Game game)
	{
		final Context context = new Context(game, new Trial(game));
		game.start(context);
		Trial trial = null;
		try
		{
			trial = game.playout
					(
						context, null, 1.0, null, 0, MAX_MOVES, 
						ThreadLocalRandom.current()
					);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
		
		if(trial == null)
			return false;
		
		for(final Move m : trial.generateCompleteMovesList())
			if(!m.isDecision())
				return false;
		
		return true;	
	}
	
	// @return Whether a trial of the game can be played without crashing.
	public static boolean isFunctional(final Game game)
	{
		final Context context = new Context(game, new Trial(game));
		game.start(context);
		Trial trial = null;
		try
		{
			trial = game.playout
					(
						context, null, 1.0, null, 0, MAX_MOVES, 
						ThreadLocalRandom.current()
					);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
		return trial != null; 		
	}

	/**
	 * @return Whether the game is basically playable, i.e. more trials are of
	 *         reasonable length than not. A trial is of reasonable length if it
	 *         lasts at least 2 * num_players moves and ends before 90% of MAX_MOVES are reached.
	 */
	public static boolean isPlayable(final Game game)
	{
		final Context context = new Context(game, new Trial(game));
		
		final int NUM_TRIALS = 10;
		
		int numResults = 0;
		
		for (int t = 0; t < NUM_TRIALS; t++)
		{		
			game.start(context);
			Trial trial = null;
			try
			{
				trial = game.playout
						(
							context, null, 1.0, null, 0, MAX_MOVES, 
							ThreadLocalRandom.current()
						);
			}
			catch(final Exception e)
			{
				e.printStackTrace();
			}
			
			//System.out.println(trial.numMoves() + " moves in " + trial.numberOfTurns() + " turns.");
			
			if (trial == null)
				return false;

			final int minMoves = 2 * game.players().count();
			final int maxMoves = MAX_MOVES * 9 / 10;
			
			if (trial.numMoves() >= minMoves && trial.numMoves() <= maxMoves)
				numResults++;
		}
		
		return numResults >= NUM_TRIALS / 2; 		
	}

}