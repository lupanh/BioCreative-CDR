package edu.ktlab.bionlp.cdr.nlp.nen;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import opennlp.tools.tokenize.SimpleTokenizer;

import org.apache.commons.lang.StringUtils;

import edu.ktlab.bionlp.cdr.data.Annotation;
import edu.ktlab.bionlp.cdr.data.Collection;
import edu.ktlab.bionlp.cdr.data.CollectionFactory;
import edu.ktlab.bionlp.cdr.dataset.MESHSearcher;
import edu.ktlab.bionlp.cdr.nlp.wsim.CosineWordSimilarity;
import edu.ktlab.bionlp.cdr.nlp.wsim.PhraseSimilarity;
import edu.ktlab.bionlp.cdr.nlp.wsim.WeightCosinePhraseSimilarity;
import edu.ktlab.ml.minorfourth.util.FileHelper;

public class CDRMentionNormalization {
	static String inFile = "data/cdr/cdr_dev/cdr_dev.gzip";
	static File outFile = new File("temp/norm.dev.cos.txt");

	public static void main(String[] args) throws Exception {
		if (outFile.exists())
			outFile.delete();
		
		System.out.print("MESH 2015 loading...");
		MESHSearcher searcher = new MESHSearcher();
		searcher.loadMESH("data/mesh2015.txt");
		System.out.println("done.");
		
		PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new CosineWordSimilarity(), true);
		// PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new
		// W2VWordSimilarity("data/embedding/glove.txt"), false);

		Collection col = CollectionFactory.loadJsonFile(inFile);
		for (Annotation ann : col.getAnnotations()) {
			String[] tokens = ann.getStrTokens(true);

			float max = 0.01f;
			String predict = "-1";
			String info = "";
			if (tokens.length == 0) {
				System.err.println(ann.getReference());
				continue;
			}

			String query = "";
			for (String token : tokens)
				if (StringUtils.isAlphanumeric(token))
					query += token + " ";

			Map<String, String> results = searcher.searchMESH(query.trim(), 11);			
			for (String mesh : results.keySet()) {
				float score = sim.score(tokens, SimpleTokenizer.INSTANCE.tokenize(mesh.toLowerCase()));
				if (score > max) {
					max = score;
					predict = results.get(mesh);
					info = score + "\t" + mesh;
				}
			}

			String output = query + "\t" + ann.getReference() + "\t" + predict + "\t"
					+ (ann.getReference().contains(predict) + "\t" + info);
			FileHelper.appendToFile(output + "\n", outFile, Charset.forName("UTF-8"));
		}

	}

}
