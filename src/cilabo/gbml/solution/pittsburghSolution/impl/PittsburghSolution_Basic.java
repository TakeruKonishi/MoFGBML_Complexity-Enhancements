package cilabo.gbml.solution.pittsburghSolution.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cilabo.data.DataSetManagerValidation;
import cilabo.data.pattern.Pattern;
import cilabo.fuzzy.classifier.Classifier;
import cilabo.gbml.objectivefunction.pittsburgh.ErrorRate;
import cilabo.gbml.solution.michiganSolution.MichiganSolution;
import cilabo.gbml.solution.michiganSolution.MichiganSolution.MichiganSolutionBuilder;
import cilabo.gbml.solution.pittsburghSolution.AbstractPittsburghSolution;
import cilabo.gbml.solution.util.attribute.ErroredPatternsAttribute;
import cilabo.main.ExperienceParameter.OBJECTIVES_FOR_PITTSBURGH;
import xml.XML_TagName;
import xml.XML_manager;

public final class PittsburghSolution_Basic <michiganSolution extends MichiganSolution<?>>
		extends AbstractPittsburghSolution<michiganSolution>{

	public PittsburghSolution_Basic(int numberOfVariables,
			int numberOfObjectives,
			int numberOfConstraints,
			MichiganSolutionBuilder<michiganSolution> michiganSolutionBuilder,
			Classifier<michiganSolution> classifier) {
		super(numberOfVariables, numberOfObjectives, numberOfConstraints,
				michiganSolutionBuilder, classifier);
		List<michiganSolution> michiganSolutionList = michiganSolutionBuilder.createMichiganSolutions(numberOfVariables);
		for(int i=0; i<this.getNumberOfVariables(); i++) {
			this.setVariable(i, michiganSolutionList.get(i));
		}
	}

	public PittsburghSolution_Basic(int numberOfObjectives,
			int numberOfConstraints,
			MichiganSolutionBuilder<michiganSolution> michiganSolutionBuilder,
			Classifier<michiganSolution> classifier,
			Element pittsburghSolution) {
		super(numberOfObjectives, numberOfConstraints,
				michiganSolutionBuilder, classifier, pittsburghSolution);
		NodeList michiganSolutionList = pittsburghSolution.getElementsByTagName(XML_TagName.michiganSolution.toString());
		for(int i=0; i<michiganSolutionList.getLength(); i++) {
			michiganSolution michiganSolution = michiganSolutionBuilder.createMichiganSolution((Element)michiganSolutionList.item(i));
			this.setVariable(i, michiganSolution);
		}
	}

	private PittsburghSolution_Basic(PittsburghSolution_Basic<michiganSolution> solution) {
	    super(solution.getNumberOfVariables(), solution.getNumberOfObjectives(), solution.getNumberOfConstraints(),
	    		solution.michiganSolutionBuilder, solution.classifier.copy());

	    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
	      setVariable(i, (michiganSolution) solution.getVariable(i).copy());
	    }

	    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
	      setObjective(i, solution.getObjective(i));
	    }

	    for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
	      setConstraint(i, solution.getConstraint(i));
	    }

	    this.attributes = new HashMap<>(solution.attributes);
	}

	@Override
	public PittsburghSolution_Basic<michiganSolution> copy() {
		return new PittsburghSolution_Basic<michiganSolution>(this);
	}

	@Override
	public michiganSolution classify(Pattern<?> pattern) {
		return this.classifier.classify(this.getVariables(), pattern);
	}

	@Override
	public String toString() {
		String ln = System.getProperty("line.separator");
		String str = "PittsburghSolution_Basic";

		str += String.format(",Objectives[%d]=,%.4f..", 0, this.getObjective(0));
		for(int i=1; i<this.getNumberOfObjectives(); i++) {
			str += String.format(",Objectives[%d]=,%.4f..", i, this.getObjective(i));
		}
		str += ln;

		for(michiganSolution tmp: this.variables) {
			str += " ->" + tmp + ln;
		}

		str += ",attributes={,";
		for (Entry<Object, Object> entry : attributes.entrySet()) {
			if(((String)entry.getKey()).equals(new ErroredPatternsAttribute<>().getAttributeId())) {
				continue;
			};
			String[] str2 = ((String)entry.getKey()).split("\\.");
		    str += String.format("%s,%s,", str2[str2.length-1], entry.getValue().toString());
		}
		str += "}" + ln + ln;
		return str;
	}

	/**
	 * population を構成するElementを返す
	 *
	 * @param xml_manager
	 * @param solutionList
	 * @return population(Element型)
	 */
	@Override
	public Element toElement() {
		Element pittsburghSolution = XML_manager.getInstance().createElement(XML_TagName.pittsburghSolution);
		for(int i=0; i<this.getNumberOfVariables(); i++) {
			Element michiganSolution = this.getVariable(i).toElement();
			XML_manager.getInstance().addElement(pittsburghSolution, michiganSolution,
					XML_TagName.id, String.valueOf(i));
		}
		//各目的関数の結果
		Element objectives = XML_manager.getInstance().createElement(XML_TagName.objectives);

			double f1 = this.getObjective(OBJECTIVES_FOR_PITTSBURGH.ErrorRateDtra.toInt());
			Element f1_ = XML_manager.getInstance().createElement(XML_TagName.objective, String.valueOf(f1));
			f1_.setAttribute(XML_TagName.id.toString(), String.valueOf(OBJECTIVES_FOR_PITTSBURGH.ErrorRateDtra.toInt()));
			f1_.setAttribute(XML_TagName.objectiveName.toString(), OBJECTIVES_FOR_PITTSBURGH.ErrorRateDtra.toString());
			XML_manager.getInstance().addElement(objectives, f1_);

			double f2 = this.getObjective(OBJECTIVES_FOR_PITTSBURGH.NumberOfRule.toInt());
			Element f2_ = XML_manager.getInstance().createElement(XML_TagName.objective, String.valueOf(f2));
			f2_.setAttribute(XML_TagName.id.toString(), String.valueOf(OBJECTIVES_FOR_PITTSBURGH.NumberOfRule.toInt()));
			f2_.setAttribute(XML_TagName.objectiveName.toString(), OBJECTIVES_FOR_PITTSBURGH.NumberOfRule.toString());
			XML_manager.getInstance().addElement(objectives, f2_);

			ErrorRate<PittsburghSolution_Basic<michiganSolution>> errorRate = new ErrorRate<>();
			double f3 = errorRate.function(this, DataSetManagerValidation.getInstance().getTests().get(0));
			Element f3_ = XML_manager.getInstance().createElement(XML_TagName.objective, String.valueOf(f3));
			f3_.setAttribute(XML_TagName.id.toString(), String.valueOf(OBJECTIVES_FOR_PITTSBURGH.ErrorRateDtst.toInt()));
			f3_.setAttribute(XML_TagName.objectiveName.toString(), OBJECTIVES_FOR_PITTSBURGH.ErrorRateDtst.toString());
			XML_manager.getInstance().addElement(objectives, f3_);

		XML_manager.getInstance().addElement(pittsburghSolution, objectives);


		Element attribute_Element = XML_manager.getInstance().createElement(XML_TagName.attributes);
		for (Entry<Object, Object> entry : attributes.entrySet()) {
			if(((String)entry.getKey()).equals(new ErroredPatternsAttribute<>().getAttributeId())) {
				continue;
			};
			String[] str2 = ((String)entry.getKey()).split("\\.");
			XML_manager.getInstance().addElement(attribute_Element, XML_TagName.attribute, entry.getValue().toString(),
					XML_TagName.attributeID, str2[str2.length-1]);
		}
		XML_manager.getInstance().addElement(pittsburghSolution, attribute_Element);
		return pittsburghSolution;
	}

}
