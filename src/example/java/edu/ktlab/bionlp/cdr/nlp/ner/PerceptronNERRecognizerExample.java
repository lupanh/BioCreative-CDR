package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Lists;
import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.nlp.nen.MentionNormalization;
import edu.ktlab.bionlp.cdr.nlp.rel.CIDRelationClassifier;
import edu.ktlab.bionlp.cdr.util.FileHelper;
import edu.stanford.nlp.util.Pair;

public class PerceptronNERRecognizerExample {
	public static void main(String[] args) throws Exception {
		CDRNERRecognizer nerFinder = new CDRNERRecognizer("models/ner/cdr_full.perc.model",
				MaxentNERFactoryExample.createFeatureGenerator());
		CIDRelationClassifier classifier = new CIDRelationClassifier("models/cid.full.model", "models/cid.full.wordlist");
		MentionNormalization normalizer = new MentionNormalization("models/nen/cdr_full.txt",
				"models/nen/mesh2015.gzip");
		CollectionFactory factory = new CollectionFactory(true);

		String[] lines = FileHelper.readFileAsLines("temp/test_webservice.txt");
		String text = "";
		for (int i = 0; i < lines.length; i++) {
			text += lines[i] + "\n";
			if (i % 2 == 1) {
				Document doc = factory.loadDocumentFromString(text);

				for (Sentence sent : doc.getSentences()) {
					List<Annotation> anns = nerFinder.recognize(doc, sent, normalizer);
					for (Annotation ann : anns) {
						doc.addAnnotation(ann);
						text += doc.getPmid() + "\t" + ann.getStartBaseOffset() + "\t" + ann.getEndBaseOffset() + "\t"
								+ ann.getContent() + "\t" + ann.getType() + "\t" + ann.getReference() + "\n";
					}
						
				}
				
				Set<Relation> candidateRels = doc.getRelationCandidates();
				Set<Relation> predictRels = new HashSet<Relation>();
				List<Sentence> sents = doc.getSentences();
				for (Sentence sent : sents) {
					for (Relation candidateRel : candidateRels) {
						if (sent.containRelation(candidateRel)) {					
							List<Annotation> annsChed = sent.getAnnotation(candidateRel.getChemicalID());
							List<Annotation> annsDis = sent.getAnnotation(candidateRel.getDiseaseID());

							List<Pair<Annotation, Annotation>> pairs = Lists.newArrayList();
							for (Annotation annChed : annsChed)
								for (Annotation annDis : annsDis) {
									pairs.add(new Pair<Annotation, Annotation>(annChed, annDis));
								}

							for (Pair<Annotation, Annotation> pair : pairs) {
								double score = classifier.classify(pair, sent);
								String predict = classifier.getLabel(score);
								if (predict.equals("CID"))
									predictRels.add(candidateRel);
							}
						}
					}
				}
				
				for (Relation rel : predictRels)
					text += doc.getPmid() + "\tCID\t" + rel.getChemicalID() + "\t" + rel.getDiseaseID() + "\n";	
				

				FileHelper.appendToFile(text, new File("temp/test_outservice.txt"), Charset.defaultCharset());

				text = "";
			}
		}

	}
}
