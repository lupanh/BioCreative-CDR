package edu.ktlab.bionlp.cdr.nlp.wsim.distance;

public class EuclideanDistance implements Distance {
	@Override
	public float distance(float[] x, float[] y) {
		if (x.length != y.length)
			return 0f;
		float sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum += (x[i] - y[i]) * (x[i] - y[i]);
		}
		return (float) Math.sqrt(sum) / x.length;
	}
}
