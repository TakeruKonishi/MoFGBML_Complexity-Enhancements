package cilabo.labo.developing.twostage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.data.DataSetManagerValidation;
import cilabo.data.Input;
import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.classification.Classification;
import cilabo.fuzzy.classifier.classification.impl.SingleWinnerRuleSelection;
import cilabo.fuzzy.classifier.impl.Classifier_basic;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.Parameters;
import cilabo.fuzzy.rule.Rule.RuleBuilder;
import cilabo.fuzzy.rule.antecedent.factory.impl.HeuristicRuleGenerationMethod;
import cilabo.fuzzy.rule.consequent.factory.impl.MoFGBML_Learning;
import cilabo.fuzzy.rule.impl.Rule_Basic;
import cilabo.gbml.objectivefunction.michigan.RuleLength;
import cilabo.gbml.objectivefunction.pittsburgh.ErrorRate;
import cilabo.gbml.operator.crossover.HybridGBMLcrossover;
import cilabo.gbml.operator.crossover.MichiganCrossover;
import cilabo.gbml.operator.crossover.PittsburghCrossover;
import cilabo.gbml.operator.mutation.PittsburghMutation;
import cilabo.gbml.problem.pittsburghFGBML_Problem.impl.PittsburghFGBML_Basic;
import cilabo.gbml.solution.michiganSolution.AbstractMichiganSolution;
import cilabo.gbml.solution.michiganSolution.MichiganSolution.MichiganSolutionBuilder;
import cilabo.gbml.solution.michiganSolution.impl.MichiganSolution_Basic;
import cilabo.gbml.solution.pittsburghSolution.impl.PittsburghSolution_Basic;
import cilabo.main.Consts;
import cilabo.utility.Output;
import cilabo.utility.Parallel;
//import xml.XML_manager;
import cilabo.utility.Random;

/**
 * @version 1.0
 *
 * 2021, November
 */
public class TwoStage_Main {
	public static void main(String[] args) throws JMetalException, FileNotFoundException {
		String sep = File.separator;

		/* ********************************************************* */
		System.out.println();
		System.out.println("==== INFORMATION ====");
		System.out.println("main: " + TwoStage_Main.class.getCanonicalName());
		String version = "1.0";
		System.out.println("version: " + version);
		System.out.println();
		System.out.println("Algorithm: Two-stage for accuracy-oriented FGBML");
		System.out.println("EMOA: NSGA-II");
		System.out.println();
		/* ********************************************************* */
		// Load consts.properties
		Consts.set("consts");
		// make result directory
		Output.mkdirs(Consts.ROOTFOLDER);

		// set command arguments to static variables
		CommandLineArgs.loadArgs(CommandLineArgs.class.getCanonicalName(), args);
		// set max number of rules to large value
		Consts.MAX_RULE_NUM = 5000;
		// Output constant parameters
		String fileName = Consts.EXPERIMENT_ID_DIR + sep + "Consts.txt";
		Output.writeln(fileName, Consts.getString(), true);
		Output.writeln(fileName, CommandLineArgs.getParamsString(), true);

		// Initialize ForkJoinPool
		Parallel.getInstance().initLearningForkJoinPool(CommandLineArgs.parallelCores);

		System.out.println("Processors: " + Runtime.getRuntime().availableProcessors() + " ");
		System.out.print("args: ");
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		System.out.println();
		System.out.println("=====================");
		System.out.println();

		/* ********************************************************* */
		System.out.println("==== EXPERIMENT =====");
		Date start = new Date();
		System.out.println("START: " + start);

		/* Random Number ======================= */
		Random.getInstance().initRandom(Consts.RAND_SEED);
		JMetalRandom.getInstance().setSeed(Consts.RAND_SEED);

		/* Load Dataset ======================== */
		Input.loadTrainValidationTestFiles_Basic(CommandLineArgs.trainFile, CommandLineArgs.validationFile, CommandLineArgs.testFile);

		/* Run MoFGBML algorithm =============== */
		DataSet train = DataSetManagerValidation.getInstance().getTrains().get(0);
		DataSet validation = DataSetManagerValidation.getInstance().getValidations().get(0);
		DataSet test = DataSetManagerValidation.getInstance().getTests().get(0);

		/** XML ファイル出力用インスタンスの生成*/
		//XML_manager.getInstance();

		twoStageMoFGBML(train, validation, test);
		/* ===================================== */

		/*try {
			XML_manager.output(Consts.EXPERIMENT_ID_DIR);
		} catch (TransformerException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}*/

		Date end = new Date();
		System.out.println("END: " + end);
		System.out.println("=====================");
		/* ********************************************************* */

		System.exit(0);
	}

	/**
	 *
	 */
	/* このメソッド全体 -> ************************************************* */
	/* TODO
	 * ステージを切り替えるように変更
	 * 1stStage用のalgorithmと2ndStage用のalgorithmを作成する方針はどうか？
	 *   案)
	 *   population = firstAlgorithm.run();
	 *   secondAlgorithm.setPopulation(population)
	 *   population = secondAlgorithm.run();
	 *   ...(現在この案を実装）
	 *  */
	public static void twoStageMoFGBML(DataSet train, DataSet validation, DataSet test) {
		Random.getInstance().initRandom(2022);
		String sep = File.separator;

		Parameters parameters = new Parameters(train);
		HomoTriangleKnowledgeFactory KnowledgeFactory = new HomoTriangleKnowledgeFactory(parameters);
		KnowledgeFactory.create2_3_4_5();

		List<Pair<Integer, Integer>> bounds_Michigan = AbstractMichiganSolution.makeBounds();
		int numberOfObjectives_Michigan = 1;
		int numberOfConstraints_Michigan = 0;

		int numberOfvariables_Pittsburgh = Consts.INITIATION_RULE_NUM;
		int numberOfObjectives_Pittsburgh = 2;
		int numberOfConstraints_Pittsburgh = 0;

		RuleBuilder<Rule_Basic, ?, ?> ruleBuilder = new Rule_Basic.RuleBuilder_Basic(
				new HeuristicRuleGenerationMethod(train),
				new MoFGBML_Learning(train));

		MichiganSolutionBuilder<MichiganSolution_Basic<Rule_Basic>> michiganSolutionBuilder
		= new MichiganSolution_Basic.MichiganSolutionBuilder_Basic<Rule_Basic>(
				bounds_Michigan,
				numberOfObjectives_Michigan,
				numberOfConstraints_Michigan,
				ruleBuilder);

		Classification<MichiganSolution_Basic<Rule_Basic>> classification = new SingleWinnerRuleSelection<MichiganSolution_Basic<Rule_Basic>>();

		Classifier<MichiganSolution_Basic<Rule_Basic>> classifier = new Classifier_basic<>(classification);

		/* MOP: Multi-objective Optimization Problem */
		Problem<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> problem =
				new PittsburghFGBML_Basic<MichiganSolution_Basic<Rule_Basic>>(
						numberOfvariables_Pittsburgh,
						numberOfObjectives_Pittsburgh,
						numberOfConstraints_Pittsburgh,
						train,
						michiganSolutionBuilder,
						classifier);

		List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> population = new ArrayList<>();
		int nowEvaluations = 0;
		/*アーカイブを初期化*/
		Set<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> ArchivePopulation = new HashSet<>();

		//TODO 1stステージの実装
		/* ======================================= */
		/* == 1st Stage                         == */
		/* ======================================= */
		FirstStageAlgorithm<?> firstAlgorithm = make1stStageAlgorithm(train, validation, test, problem);
		/* === GA RUN === */
		firstAlgorithm.run();
		/* ============== */

		/*2nd初期個体群生成方法1:第1段階の最終世代を第2段階の初期個体群とする*/
		population = (List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>) firstAlgorithm.getPopulation();
		/*2ndへの引き渡し用に1stのアーカイブを持ってくる*/
		ArchivePopulation = (Set<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>) firstAlgorithm.getArchivePopulation();
		/*初期個体群生成方法1ここまで*/

		/*2nd初期個体群生成方法2:第1段階で得られたアーカイブを用いて第2段階の初期個体群生成*/
		/*ArchivePopulation = (Set<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>) firstAlgorithm.getArchivePopulation();

		List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> TempArchivePopulationList = new ArrayList<>(ArchivePopulation);

		List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> TempPopulation = new ArrayList<>();

		for (PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>> Tempsolution : TempArchivePopulationList) {
			PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>> TempcopiedSolution = Tempsolution.copy();
		    TempPopulation.add(TempcopiedSolution);
		}*/

		/*アーカイブから非劣解を抽出（分割ありversion）*/
		//サブリスト数（暫定で100に設定）
		//int TempnumberOfSublists = 100;

		//サブリストを格納するリスト
		//List<List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>> TemppartitionedList = new ArrayList<>();

		//分割に用いるパラメータの算出
		/*int TemptotalSize = TempPopulation.size();
        int TempchunkSize = TemptotalSize / TempnumberOfSublists;
        int Tempremainder = TemptotalSize % TempnumberOfSublists;
        int Tempstart = 0;*/

        // 元のリストの要素をサブリストに分割
        /*for (int i = 0; i < TempnumberOfSublists; i++) {
            int Tempend = Tempstart + TempchunkSize + (i < Tempremainder ? 1 : 0);
            TemppartitionedList.add(new ArrayList<>(TempPopulation.subList(Tempstart, Tempend)));
            Tempstart = Tempend;
        }*/

        // partitionedList内の各サブリストにgetNonDominatedSolutionsを適用し、結果を統合
        /*List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> TempmergedList = TemppartitionedList.stream()
                .flatMap(list -> SolutionListUtils.getNonDominatedSolutions(list).stream())
                .collect(Collectors.toList());*/

        //統合後のリストから非劣解を抽出し，最終的な個体群とする
        //List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> TempNDS = SolutionListUtils.getNonDominatedSolutions(TempmergedList);

        //int TempSize = Consts.POPULATION_SIZE;

        // 最初のリスト（指定された要素数を持つ）
        //List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> TempfirstPart = new ArrayList<>();

        // 残りの要素を含むリスト
        //List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> TempsecondPart = new ArrayList<>();


        //if (TempNDS.size() <= TempSize) {
            // TempNDSの要素数がTempSizeに満たない場合、全要素をpopulationに追加
        	//population.addAll(TempNDS);
        //} else {
            // TempNDSの要素数がTempSizeより多い場合、分割を行う
            //TempfirstPart = TempNDS.subList(0, TempSize);
            //TempsecondPart = TempNDS.subList(TempSize, TempNDS.size());

            /*AOR*/
            //Replacement<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> replacement = new AccuracyOrientedReplacement<>();

            /*通常版環境選択*/
            /*DensityEstimator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    		Ranking<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> ranking = new FastNonDominatedSortRanking<>();
            Replacement<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> replacement = new RankingAndDensityEstimatorReplacement<>(
    				ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

            population = replacement.replace(TempfirstPart,TempsecondPart);
        }*/
        /*初期個体群生成方法2ここまで*/

		nowEvaluations = (int) firstAlgorithm.getEvaluations();

		/* ======================================= */

		System.out.println("== Changed stage: EVALUATIONS="+nowEvaluations+" ==");

		/* ======================================= */
		/* == 2nd Stage                         == */
		/* ======================================= */
		SecondStageAlgorithm<?> secondAlgorithm
		= make2ndStageAlgorithm(train, validation, test, problem,
								/* Set current population as the initial population for 2nd stage */
								population,
								/* Set current number of evaluations */
								nowEvaluations
								/* Set current Archivepopulation as the initial Archivepopulation for 2nd stage */
								,ArchivePopulation);
		/* === GA RUN === */
		secondAlgorithm.run();
		/* ============== */
		/* ======================================= */

		/* Non-dominated solutions in final generation */
		List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> nonDominatedSolutions = (List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>) secondAlgorithm.getResult();

		/* archive population */
		Set<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> ARC = (Set<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>) secondAlgorithm.getArchivePopulation();

		List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> ARCList = new ArrayList<>(ARC);

		/*アーカイブから非劣解を抽出（分割なしversion）*/
		//List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> nonDominatedSolutionsARC = SolutionListUtils.getNonDominatedSolutions(ARCList);

		/*アーカイブから非劣解を抽出（分割ありversion）*/
		//サブリスト数（暫定で100に設定）
		int numberOfSublists = 100;

		//サブリストを格納するリスト
		List<List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>> partitionedList = new ArrayList<>();

		//分割に用いるパラメータの算出
		int totalSize = ARCList.size();
        int chunkSize = totalSize / numberOfSublists;
        int remainder = totalSize % numberOfSublists;
        int start = 0;

        // 元のリストの要素をサブリストに分割
        for (int i = 0; i < numberOfSublists; i++) {
            int end = start + chunkSize + (i < remainder ? 1 : 0);
            partitionedList.add(new ArrayList<>(ARCList.subList(start, end)));
            start = end;
        }

        // partitionedList内の各サブリストにgetNonDominatedSolutionsを適用し、結果を統合
        List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> mergedList = partitionedList.stream()
                .flatMap(list -> SolutionListUtils.getNonDominatedSolutions(list).stream())
                .collect(Collectors.toList());

        //統合後のリストから非劣解を抽出し，最終的な個体群とする
        List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> nonDominatedSolutionsARC = SolutionListUtils.getNonDominatedSolutions(mergedList);

        //バグ含むのでコメントアウト（修正するならJmetal仕様のメソッドを書き換える）
		/*new SolutionListOutput(nonDominatedSolutions)
    	.setVarFileOutputContext(new DefaultFileOutputContext(Consts.EXPERIMENT_ID_DIR+sep+"VAR-final.csv", ","))
    	.setFunFileOutputContext(new DefaultFileOutputContext(Consts.EXPERIMENT_ID_DIR+sep+"FUN-final.csv", ","))
    	.print();*/

	    // Test data（Resultsに集約したためコメントアウト）
	    /*ArrayList<String> strs = new ArrayList<>();
	    String str = "pop,test";
	    strs.add(str);

	    for(int i = 0; i < nonDominatedSolutions.size(); i++) {
	    	PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>> solution = nonDominatedSolutions.get(i);
			ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> function1
				= new ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>();
			double errorRate = function1.function(solution, test);

	    	str = String.valueOf(i);
	    	str += "," + errorRate;
	    	strs.add(str);
	    }
	    String fileName = Consts.EXPERIMENT_ID_DIR + sep + "results.csv";
	    Output.writeln(fileName, strs, false);*/

		//outputResults(nonDominatedSolutions, train,test);

	    //Results of final generation
	    ArrayList<String> strs = new ArrayList<>();
	    String str = "pop,train,NR,RL,Cover,RW,val,test";
	    strs.add(str);

	    for(int i = 0; i < nonDominatedSolutions.size(); i++) {
            double errorRatetrain = nonDominatedSolutions.get(i).getObjective(0);
            double NR = nonDominatedSolutions.get(i).getObjective(1);
            RuleLength<MichiganSolution_Basic<Rule_Basic>> RuleLengthFunc = new RuleLength<MichiganSolution_Basic<Rule_Basic>>();
            double TotalRuleLength = 0;
            for (int j = 0; j < nonDominatedSolutions.get(i).getNumberOfVariables(); j++) {
                 double RuleLength = RuleLengthFunc.function(nonDominatedSolutions.get(i).getVariable(j));
                 TotalRuleLength += RuleLength;
            }

            double TotalCover = 0;
            for (int j = 0; j < nonDominatedSolutions.get(i).getNumberOfVariables(); j++) {

            	 double Cover = 0;
            	 List<Double> support = new ArrayList<Double>();

            	 for (int k = 0; k < train.getNdim(); k++) {
            		  if (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) != 0) {

            			  if ((nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 1) ||
            				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 2) ||
            				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 4)){
            				   support.add(1.0);
            			  }else if ((nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 3) ||
                				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 5) ||
                				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 11) ||
                				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 12) ||
                				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 13)){
                				   support.add(1.0/2);
            			  }else if ((nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 6) ||
                				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 9)){
                				   support.add(1.0/3);
            			  }else if ((nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 7) ||
                				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 8)){
           				   support.add(2.0/3);
       			          }else if ((nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 10) ||
                				  (nonDominatedSolutions.get(i).getVariable(j).getVariable(k) == 14)){
           				   support.add(1.0/4);
       			          }
            		  }
            	 }
            	 if (!support.isEmpty()) {
            	     Cover = support.stream().reduce(1.0, (a, b) -> a * b);
                   	 TotalCover += Cover;
                 }
            }

            double TotalRW = 0;
            for (int j = 0; j < nonDominatedSolutions.get(i).getNumberOfVariables(); j++) {
                double RW = (Double) nonDominatedSolutions.get(i).getVariable(j).getRuleWeight().getRuleWeightValue();
                TotalRW += RW;
            }
            double AveRW = TotalRW/(nonDominatedSolutions.get(i).getNumberOfVariables());

            ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> function1
			= new ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>();
		    double errorRatevalidation = function1.function(nonDominatedSolutions.get(i), validation);
		    double errorRatetest = function1.function(nonDominatedSolutions.get(i), test);

	    	str = String.valueOf(i);
	    	str += "," + errorRatetrain;
	    	str += "," + NR;
	    	str += "," + TotalRuleLength;
	    	str += "," + TotalCover;
	    	str += "," + AveRW;
	    	str += "," + errorRatevalidation;
	    	str += "," + errorRatetest;
	    	strs.add(str);
	    }
	    String fileName = Consts.EXPERIMENT_ID_DIR + sep + "results.csv";
	    Output.writeln(fileName, strs, false);


	    //Results of archive population
	    ArrayList<String> strsARC = new ArrayList<>();
	    String strARC = "pop,train,NR,RL,Cover,RW,val,test";
	    strsARC.add(strARC);

	    for(int i = 0; i < nonDominatedSolutionsARC.size(); i++) {
            double errorRatetrainARC = nonDominatedSolutionsARC.get(i).getObjective(0);
            double NRARC = nonDominatedSolutionsARC.get(i).getObjective(1);
            RuleLength<MichiganSolution_Basic<Rule_Basic>> RuleLengthFuncARC = new RuleLength<MichiganSolution_Basic<Rule_Basic>>();
            double TotalRuleLengthARC = 0;
            for (int j = 0; j < nonDominatedSolutionsARC.get(i).getNumberOfVariables(); j++) {
                 double RuleLengthARC = RuleLengthFuncARC.function(nonDominatedSolutionsARC.get(i).getVariable(j));
                 TotalRuleLengthARC += RuleLengthARC;
            }

            double TotalCoverARC = 0;
            for (int j = 0; j < nonDominatedSolutionsARC.get(i).getNumberOfVariables(); j++) {

            	 double CoverARC = 0;
            	 List<Double> supportARC = new ArrayList<Double>();

            	 for (int k = 0; k < train.getNdim(); k++) {
            		  if (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) != 0) {

            			  if ((nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 1) ||
            				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 2) ||
            				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 4)){
            				   supportARC.add(1.0);
            			  }else if ((nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 3) ||
                				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 5) ||
                				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 11) ||
                				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 12) ||
                				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 13)){
                				   supportARC.add(1.0/2);
            			  }else if ((nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 6) ||
                				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 9)){
                				   supportARC.add(1.0/3);
            			  }else if ((nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 7) ||
                				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 8)){
           				   supportARC.add(2.0/3);
       			          }else if ((nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 10) ||
                				  (nonDominatedSolutionsARC.get(i).getVariable(j).getVariable(k) == 14)){
           				   supportARC.add(1.0/4);
       			          }
            		  }
            	 }
            	 if (!supportARC.isEmpty()) {
            	     CoverARC = supportARC.stream().reduce(1.0, (a, b) -> a * b);
                   	 TotalCoverARC += CoverARC;
                 }
            }

            double TotalRWARC = 0;
            for (int j = 0; j < nonDominatedSolutionsARC.get(i).getNumberOfVariables(); j++) {
                double RWARC = (Double) nonDominatedSolutionsARC.get(i).getVariable(j).getRuleWeight().getRuleWeightValue();
                TotalRWARC += RWARC;
            }
            double AveRWARC = TotalRWARC/(nonDominatedSolutionsARC.get(i).getNumberOfVariables());

            ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> function1ARC
			= new ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>();
            double errorRatevalidationARC = function1ARC.function(nonDominatedSolutionsARC.get(i), validation);
		    double errorRatetestARC = function1ARC.function(nonDominatedSolutionsARC.get(i), test);

	    	strARC = String.valueOf(i);
	    	strARC += "," + errorRatetrainARC;
	    	strARC += "," + NRARC;
	    	strARC += "," + TotalRuleLengthARC;
	    	strARC += "," + TotalCoverARC;
	    	strARC += "," + AveRWARC;
	    	strARC += "," + errorRatevalidationARC;
	    	strARC += "," + errorRatetestARC;
	    	strsARC.add(strARC);
	    }
	    String fileNameARC = Consts.EXPERIMENT_ID_DIR + sep + "resultsARC.csv";
	    Output.writeln(fileNameARC, strsARC, false);

	 // //Results of final generation after MAP-Elites
//	    ArrayList<String> strsME = new ArrayList<>();
//	    String strME = "pop,train,NR,RL,Cover,RW,val,test";
//	    strsME.add(strME);
//
//	    for(int i = 0; i < finaleliteSolutions.size(); i++) {
//            double errorRatetrainME = finaleliteSolutions.get(i).getObjective(0);
//            double NRME = finaleliteSolutions.get(i).getObjective(1);
//            RuleLength<MichiganSolution_Basic<Rule_Basic>> RuleLengthFuncME = new RuleLength<MichiganSolution_Basic<Rule_Basic>>();
//            double TotalRuleLengthME = 0;
//            for (int j = 0; j < finaleliteSolutions.get(i).getNumberOfVariables(); j++) {
//                 double RuleLengthME = RuleLengthFuncME.function(finaleliteSolutions.get(i).getVariable(j));
//                 TotalRuleLengthME += RuleLengthME;
//            }
//
//            double TotalCoverME = 0;
//            for (int j = 0; j < finaleliteSolutions.get(i).getNumberOfVariables(); j++) {
//
//            	 double CoverME = 0;
//            	 List<Double> supportME = new ArrayList<Double>();
//
//            	 for (int k = 0; k < train.getNdim(); k++) {
//            		  if (finaleliteSolutions.get(i).getVariable(j).getVariable(k) != 0) {
//
//            			  if ((finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 1) ||
//            				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 2) ||
//            				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 4)){
//            				   supportME.add(1.0);
//            			  }else if ((finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 3) ||
//                				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 5) ||
//                				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 11) ||
//                				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 12) ||
//                				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 13)){
//                				   supportME.add(1.0/2);
//            			  }else if ((finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 6) ||
//                				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 9)){
//                				   supportME.add(1.0/3);
//            			  }else if ((finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 7) ||
//                				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 8)){
//           				   supportME.add(2.0/3);
//       			          }else if ((finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 10) ||
//                				  (finaleliteSolutions.get(i).getVariable(j).getVariable(k) == 14)){
//           				   supportME.add(1.0/4);
//       			          }
//            		  }
//            	 }
//            	 if (!supportME.isEmpty()) {
//            	     CoverME = supportME.stream().reduce(1.0, (a, b) -> a * b);
//                   	 TotalCoverME += CoverME;
//                 }
//            }
//
//            double TotalRWME = 0;
//            for (int j = 0; j < finaleliteSolutions.get(i).getNumberOfVariables(); j++) {
//                double RWME = (Double) finaleliteSolutions.get(i).getVariable(j).getRuleWeight().getRuleWeightValue();
//                TotalRWME += RWME;
//            }
//            double AveRWME = TotalRWME/(finaleliteSolutions.get(i).getNumberOfVariables());
//
//            ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> function1ME
//			= new ErrorRate<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>();
//            double errorRatevalidationME = function1ME.function(finaleliteSolutions.get(i), validation);
//		    double errorRatetestME = function1ME.function(finaleliteSolutions.get(i), test);
//
//	    	strME = String.valueOf(i);
//	    	strME += "," + errorRatetrainME;
//	    	strME += "," + NRME;
//	    	strME += "," + TotalRuleLengthME;
//	    	strME += "," + TotalCoverME;
//	    	strME += "," + AveRWME;
//	    	strME += "," + errorRatevalidationME;
//	    	strME += "," + errorRatetestME;
//	    	strsME.add(strME);
//	    }
//	    String fileNameME = Consts.EXPERIMENT_ID_DIR + sep + "resultsME.csv";
//	    Output.writeln(fileNameME, strsME, false);

        return;
     }


	public static FirstStageAlgorithm<?> make1stStageAlgorithm(DataSet train, DataSet validation, DataSet test, Problem<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> problem) {
		/* Crossover: Hybrid-style GBML specific crossover operator. */
		double crossoverProbability = 1.0;

		/* Michigan operation */
		CrossoverOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> michiganX
				= new MichiganCrossover<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(Consts.MICHIGAN_CROSS_RT, train);
		/* Pittsburgh operation */
		CrossoverOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> pittsburghX
				= new PittsburghCrossoverComplexOriented<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(Consts.PITTSBURGH_CROSS_RT);
		/* Hybrid-style crossover */
		CrossoverOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> crossover
				= new HybridGBMLcrossover<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(crossoverProbability, Consts.MICHIGAN_OPE_RT, michiganX, pittsburghX);
		/* Mutation: Pittsburgh-style GBML specific mutation operator. */
		MutationOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> mutation
				= new PittsburghMutation<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(train);

		/* Termination: Number of total evaluations TODO TODO TODO */
		Termination termination = new ChangeStageTermination(CommandLineArgs.changeStageSetting);

		//knowlwdge出力用
		//XML_manager.addElement(XML_manager.getRoot(), Knowledge.getInstance().toElement());

		/* Algorithm: Hybrid-style MoFGBML with NSGA-II */
		FirstStageAlgorithm<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> algorithm
			= new FirstStageAlgorithm<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>(problem,
										Consts.POPULATION_SIZE,
										Consts.OFFSPRING_POPULATION_SIZE,
										Consts.OUTPUT_FREQUENCY,
										Consts.EXPERIMENT_ID_DIR,
										crossover,
										mutation,
										termination);

		/* Running observation */
		EvaluationObserver evaluationObserver = new EvaluationObserver(Consts.OUTPUT_FREQUENCY);
		algorithm.getObservable().register(evaluationObserver);

		/* Replacement: Accuracy-oriented replacement */
		Replacement<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> replacement = new AccuracyOrientedReplacement<>();
		algorithm.setReplacement(replacement);

		return algorithm;
	}

	/*引数にアーカイブを追加*/
	public static SecondStageAlgorithm<?> make2ndStageAlgorithm(DataSet train, DataSet validation, DataSet test, Problem<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> problem,
																				List<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> population, int nowEvaluations
																				,Set<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> Archivepopulation) {


		/* Crossover: Hybrid-style GBML specific crossover operator. */
		double crossoverProbability = 1.0;

		/* Michigan operation */
		CrossoverOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> michiganX
				= new MichiganCrossover<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(Consts.MICHIGAN_CROSS_RT, train);
		/* Pittsburgh operation */
		CrossoverOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> pittsburghX
				= new PittsburghCrossover<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(Consts.PITTSBURGH_CROSS_RT);
		/* Hybrid-style crossover */
		CrossoverOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> crossover
				= new HybridGBMLcrossover<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(crossoverProbability, Consts.MICHIGAN_OPE_RT, michiganX, pittsburghX);
		/* Mutation: Pittsburgh-style GBML specific mutation operator. */
		MutationOperator<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> mutation
				= new PittsburghMutation<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>, MichiganSolution_Basic<Rule_Basic>>(train);

		/* Termination: Number of total evaluations */
		Termination termination = new TerminationByEvaluations(Consts.TERMINATE_EVALUATION);

		//knowlwdge出力用
		//XML_manager.addElement(XML_manager.getRoot(), Knowledge.getInstance().toElement());

		/* Algorithm: Hybrid-style MoFGBML with NSGA-II （引数にアーカイブを追加）*/
		SecondStageAlgorithm<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>> algorithm
			= new SecondStageAlgorithm<PittsburghSolution_Basic<MichiganSolution_Basic<Rule_Basic>>>(problem,
										Consts.POPULATION_SIZE,
										Consts.OFFSPRING_POPULATION_SIZE,
										Consts.OUTPUT_FREQUENCY,
										Consts.EXPERIMENT_ID_DIR,
										crossover,
										mutation,
										termination,
										population, nowEvaluations
										, Archivepopulation
										);

		/* Running observation */
		EvaluationObserver evaluationObserver = new EvaluationObserver(Consts.OUTPUT_FREQUENCY);
		algorithm.getObservable().register(evaluationObserver);

		return algorithm;
	}

	// TODO TODO TODO
	/*public static void outputResults(List<PittsburghSolution<?>> nonDominatedSolutions, DataSet train, DataSet test) {
		String sep = File.separator;

		ArrayList<String> strs = new ArrayList<>();
		String str = "";*/

		/* Functions */
		/*ErrorRate errorRate = new ErrorRate();
		NumberOfRules ruleNum = new NumberOfRules();
		RuleLength ruleLength = new RuleLength();*/
		/* Header */
		/*str = "pop";
		str += "," + "errorRate_Dtra" + "," + "errorRate_Dtst";
		str += "," + "ruleNum";
		str += "," + "ruleLength";
		strs.add(str);

		for(int i = 0; i < nonDominatedSolutions.size(); i++) {
			PittsburghSolution solution = nonDominatedSolutions.get(i);
			Classifier classifier = solution.getClassifier();

			// pop
			str = String.valueOf(i);
			// Error Rate
			str += "," + errorRate.metric(classifier, train);
			str += "," + errorRate.metric(classifier, test);
			// Number of rules
			str += "," + ruleNum.metric(classifier);
			// Rule Length
			str += "," + ruleLength.metric(classifier);

			strs.add(str);
		}
		String fileName = Consts.EXPERIMENT_ID_DIR + sep + "results.csv";
		Output.writeln(fileName, strs, false);
	}*/
}
