package zearch.engine.similarity;

public class ComboSimilarity implements ISimilarity {

    private ISimilarity sim0;
    private ISimilarity sim1;
    public ComboSimilarity(ISimilarity sim0, ISimilarity sim1) {
        this.sim0 = sim0;
        this.sim1 = sim1;
    }

    @Override
    public double similarity(String a, String b) {
        return (sim0.similarity(a,b) + sim1.similarity(a,b))/2;
    }
}
