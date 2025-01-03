package cilabo.fuzzy.knowledge.factory;

import java.util.Objects;

import cilabo.fuzzy.knowledge.FuzzyTermTypeForMixed;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.membershipParams.Parameters;
import cilabo.main.ExperienceParameter.DIVISION_TYPE;
import jfml.term.FuzzyTermType;

/**
 * 三角形型のファジィ集合によるKnowledgeBaseを生成する．
 * @author Takigawa Hiroki
 *
 */
public class HomoTriangleKnowledgeFactory{

	/** Number of features */
	int dimension;

	/** Parameters of membership functions */
	Parameters parameters;

	/**
	 * インスタンスを生成
	 * @param parameters 分割区間のパラメータ
	 */
	public HomoTriangleKnowledgeFactory(Parameters parameters) {
		if(Objects.isNull(parameters)) {
			throw new IllegalArgumentException("argument [parameters] is null @HomoTriangleKnowledgeFactory.HomoTriangleKnowledgeFactory");
		}
		this.parameters = parameters;
		this.dimension = parameters.getNumberOfDimension();
	}

	/**
	 * 入力された分割数の三角形型のファジィ集合から構成されるKnowledgeBaseを生成
	 * @param K 分割数配列:[次元][分割数]
	 */
	public void create(int[][] K) {

		// make fuzzy sets
		FuzzyTermTypeForMixed[][] fuzzySets = new FuzzyTermTypeForMixed[dimension][];
		int shapeType = FuzzyTermType.TYPE_triangularShape;
		for(int dim_i = 0; dim_i < dimension; dim_i++) {
			int len=0; for(int k_i: K[dim_i]) {len += k_i;}
			fuzzySets[dim_i] = new FuzzyTermTypeForMixed[len + 1];

			//Don't care を追加
			fuzzySets[dim_i][0] = Knowledge.getInstance().makeDontCare();

			for(int j=1, cnt=1; j < K[dim_i].length + 1; j++) {
				float[][] param = parameters.triangle(DIVISION_TYPE.equalDivision, dim_i, K[dim_i][j-1]);
				for(int k_i=0; k_i<K[dim_i][j-1]; k_i++) {
					String name = Knowledge.makeFuzzyTermName(DIVISION_TYPE.equalDivision, shapeType, cnt);
					fuzzySets[dim_i][cnt] = new FuzzyTermTypeForMixed(name, shapeType, param[k_i], DIVISION_TYPE.equalDivision, K[dim_i][j-1], k_i);
					cnt++;
				}
			}
		}

		// Create
		Knowledge knowledge = Knowledge.getInstance();
		knowledge.setFuzzySets(fuzzySets);

		return;
	}


	/** 2-5分割分割三角形型ファジィのKBを生成 */
	public void create2_3_4_5() {
		int[][] tmp = new int[this.dimension][];
		for(int i=0; i<this.dimension; i++) {
			tmp[i] = new int[]{2, 3, 4, 5};
		}
		this.create(tmp);
		return;
	}
}
