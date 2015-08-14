package edu.ktlab.bionlp.cdr.nlp.wsim;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.SimpleTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.tartarus.snowball.ext.PorterStemmer;

import bsh.StringUtil;
import edu.ktlab.bionlp.cdr.nlp.tokenizer.TokenizerMESingleton;
import edu.ktlab.bionlp.cdr.nlp.wsim.CosineWordSimilarity;
import edu.ktlab.bionlp.cdr.nlp.wsim.PhraseSimilarity;
import edu.ktlab.bionlp.cdr.nlp.wsim.W2VWordSimilarity;
import edu.ktlab.bionlp.cdr.nlp.wsim.WeightCosinePhraseSimilarity;
import edu.ktlab.ml.minorfourth.util.FileHelper;

public class PhraseSimilarityExample {

	public static void main(String[] args) throws Exception {
		System.out.print("MESH 2015 loading...");
		List<Pair<String, String[]>> meshs = new ArrayList<Pair<String, String[]>>();
		String[] lines = FileHelper.readFileAsLines("data/mesh2015.txt");
		for (String line : lines) {
			String[] f = line.split("\t");
			Pair<String, String[]> p = new MutablePair<String, String[]>(f[0], SimpleTokenizer.INSTANCE.tokenize(f[1]
					.toLowerCase()));
			meshs.add(p);
		}
		
		System.out.println("done. " + meshs.size());
		
		// PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new W2VWordSimilarity("data/embedding/glove.txt"));
		PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new CosineWordSimilarity(), true);
		String concept1 = "withdrawal-emergent rs";
		String[] tokens1 = SimpleTokenizer.INSTANCE.tokenize(concept1.toLowerCase());

		for (Pair<String, String[]> mesh : meshs) {
			float score = sim.score(tokens1, mesh.getRight());
			if (score > 0.0f)
				System.out.println(mesh.getLeft() + "\t" + score + "\t" + StringUtils.join(mesh.getRight(), " "));
		}

	}

}
