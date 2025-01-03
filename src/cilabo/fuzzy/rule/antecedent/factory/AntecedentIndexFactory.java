package cilabo.fuzzy.rule.antecedent.factory;

/** 前件部のファジィセットのインデックス配列を生成用クラス
 * @author Takigawa Hiroki
 */
public interface AntecedentIndexFactory{

	/** 1個体分のファジィセットインデックス配列をランダムに生成する
	 * @return 生成されたファジィセットインデックス配列[ファジィ集合インデックス] */
	public int[] create();

	/** 指定された個体数分のファジィセットインデックス配列を生成する
	 * @param numberOfGenerateRule 生成するルールの個体数
	 * @return 生成されたファジィセットインデックス配列[生成されたルールindex][ファジィ集合インデックス] */
	public int[][] create(int numberOfGenerateRule);

	public AntecedentIndexFactory copy();
}
