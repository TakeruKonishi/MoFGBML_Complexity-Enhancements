package cilabo.labo.developing.twostage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
//import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import cilabo.gbml.solution.michiganSolution.MichiganSolution;
import cilabo.gbml.solution.michiganSolution.impl.MichiganSolution_Basic;
import cilabo.gbml.solution.pittsburghSolution.PittsburghSolution;
import cilabo.gbml.solution.util.attribute.NumberOfWinner;
import cilabo.main.Consts;
import cilabo.utility.GeneralFunctions;
import cilabo.utility.Random;

/**
 * cilabo.gbml.operator.crossover.PittsburghCrossover を参考に作成
 * CrossoverOperatorをimplements
 *
 * 生成される2つの子個体の内、複雑な方を必ず選択
 *
 * cilabo.gbml.operator.crossover.PittsburghCrossoverでは子個体は1つしか作られていないことに注意
 *
 * 親1: {ルールA, ルールB, ルールC, ルールD}
 * 親2: {ルールa, ルールb, ルールc}
 * から子個体を作る．
 * -> PittsburghCrossoverでは...
 *     親1から{ルールA, ルールC}を受け継ぐ（ランダムに選択）
 *     親2から{ルールc}を受け継ぐ（ランダムに選択）
 *     -> 子個体Z:{ルールA, ルールC, ルールc}を生成
 *     ※※
 *     ※実際には，受け継いでいないルールを集めた
 *     ※子個体Z':{ルールB, ルールD, ルールa, ルールb}も生成されているのと同義
 *     ※-> さらに，子個体Zはルール数3，子個体Z'はルール数4
 *     ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *     ※-> 本研究では，ZとZ'のうちルール数が多い方を子個体として採用する交叉操作を実装したい※
 *     ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *
 */
public class PittsburghCrossoverComplexOriented <pittsburghSolution extends PittsburghSolution<michiganSolution>,michiganSolution extends MichiganSolution<?>>
    implements CrossoverOperator<pittsburghSolution> {
    private double crossoverProbability;
    private RandomGenerator<Double> crossoverRandomGenerator;
    BoundedRandomGenerator<Integer> selectRandomGenerator;

	/** Constructor */
	public PittsburghCrossoverComplexOriented(double crossoverProbability) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public PittsburghCrossoverComplexOriented(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public PittsburghCrossoverComplexOriented(
			double crossoverProbability,
			RandomGenerator<Double> crossoverRandomGenerator,
			BoundedRandomGenerator<Integer> selectRandomGenerator) {
		if(crossoverProbability < 0) {
			throw new JMetalException("Crossover probability is negative: " + crossoverProbability);
		}
		this.crossoverProbability = crossoverProbability;
		this.crossoverRandomGenerator = crossoverRandomGenerator;
		this.selectRandomGenerator = selectRandomGenerator;
	}

	/* Getter */
	@Override
	public double getCrossoverProbability() {
		return this.crossoverProbability;
	}

	/* Setter */
	public void setCrossoverProbability(double crossoverProbability) {
		this.crossoverProbability = crossoverProbability;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 1;
	}

	@Override
	public List<pittsburghSolution> execute(List<pittsburghSolution> solutions) {
		Check.isNotNull(solutions);
		Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());
		return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
	}

	/**
	 * 後件部の学習はここでは行わない
	 * @param probability
	 * @param _parent1
	 * @param _parent2
	 * @return
	 */
	public List<pittsburghSolution> doCrossover(double probability, pittsburghSolution parent1, pittsburghSolution parent2) {

		if(parent1.getNumberOfVariables() < 1) {System.err.println("incorrect input: number Of Rules is less than 1@" + this.getClass().getSimpleName());}
		if(parent2.getNumberOfVariables() < 1) {System.err.println("incorrect input: number Of Rules is less than 1@" + this.getClass().getSimpleName());}
		List<pittsburghSolution> offspring = new ArrayList<>();

		/* Do crossover */
		if(crossoverRandomGenerator.getRandomValue() < probability) {
			offspring.add((pittsburghSolution) parent1.copy());
			offspring.get(0).clearVariables();
			offspring.get(0).clearAttributes();
			/** Number of rules inherited from parent1.  */
			int N1 = selectRandomGenerator.getRandomValue(0, parent1.getNumberOfVariables());
			/** Number of rules inherited from parent2.  */
			int N2 = selectRandomGenerator.getRandomValue(0, parent2.getNumberOfVariables());

			// Replenishing lack of number of rules
			if((N1+N2) < Consts.MIN_RULE_NUM) {
				int lackNum = Consts.MIN_RULE_NUM - (N1+N2);
				for(int i = 0; i < lackNum; i++) {
					if( N1 < parent1.getNumberOfVariables() &&
						N2 < parent2.getNumberOfVariables())
					{
						if(selectRandomGenerator.getRandomValue(0, 1) == 0) {
							N1++;
						}
						else {
							N2++;
						}
					}
					else if(N1 >= parent1.getNumberOfVariables() &&
							N2 < parent2.getNumberOfVariables()) {
						N2++;
					}
					else if(N1 < parent1.getNumberOfVariables() &&
							N2 >= parent2.getNumberOfVariables()) {
						N1++;
					}
					else {
						throw new ArithmeticException("Number of Rule that both parent have is less than " + String.valueOf(Consts.MIN_RULE_NUM)
						+ " @" + this.getClass().getSimpleName());
					}
				}
			}

			if((N1+N2) < 1) {System.err.print("number of rule to be generate is less than 1");}

			// Crossover
			List<MichiganSolution> michiganPopulation1 = new ArrayList<>();	// offspring 1
			List<MichiganSolution> michiganPopulation2 = new ArrayList<>();	// offspring 2

			// Select inherited rules
			int ruleNum = parent1.getNumberOfVariables();
			Integer[][] index1 = GeneralFunctions.samplingWithoutForOption2(ruleNum, N1, Random.getInstance().getGEN());
			ruleNum = parent2.getNumberOfVariables();
			Integer[][] index2 = GeneralFunctions.samplingWithoutForOption2(ruleNum, N2, Random.getInstance().getGEN());

			String attributeId = new NumberOfWinner<MichiganSolution_Basic<?>>().getAttributeId();

			// Inheriting
			// from parent1
			for(int i = 0; i < parent1.getNumberOfVariables(); i++) {
				if(Arrays.asList(index1).contains(i)) {
					michiganPopulation1.add(parent1.getVariable(i).copy());
				}
				else {
					michiganPopulation2.add(parent1.getVariable(i).copy());
				}
			}
			// from parent2
			for(int i = 0; i < parent2.getNumberOfVariables(); i++) {
				if(Arrays.asList(index2).contains(i)) {
					michiganPopulation1.add(parent2.getVariable(i).copy());
				}
				else {
					michiganPopulation2.add(parent2.getVariable(i).copy());
				}
			}


			List<MichiganSolution> manyRules = null;
			if(michiganPopulation1.size() >= michiganPopulation2.size()) {
				manyRules = michiganPopulation1;
			}
			else {
				manyRules = michiganPopulation2;
			}

			pittsburghSolution child = (pittsburghSolution) parent1.copy();

			child.clearVariables();
			//add
			child.clearAttributes();
			//end

			for(int i=0; i<manyRules.size(); i++) {
				child.addVariable(null);
			}
			//add
			for(int i=0; i<manyRules.size(); i++) {
				((michiganSolution) manyRules.get(i)).setAttribute(attributeId, 0);
			}
			//end

			for(int i=0; i<manyRules.size(); i++) {
				child.setVariable(i, (michiganSolution) manyRules.get(i));
			}


			offspring.clear();
			offspring.add(child);
		}
		/* Don't crossover */
		else {
			offspring.add((pittsburghSolution) parent1.copy());
			offspring.add((pittsburghSolution) parent2.copy());
			// Select one offspring by random.
			int remove = selectRandomGenerator.getRandomValue(0, 1);
			offspring.remove(remove);
		}

		if(offspring.get(0).getNumberOfVariables() < 1) {System.err.println("number Of generated rules is less than 1 @" + this.getClass().getSimpleName());}
		return offspring;
	}
}
