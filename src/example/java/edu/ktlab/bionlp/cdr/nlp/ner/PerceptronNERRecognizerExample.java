package edu.ktlab.bionlp.cdr.nlp.ner;

import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;

public class PerceptronNERRecognizerExample {
	public static void main(String[] args) throws Exception {
		String text = "000|t|Tricuspid valve regurgitation and lithium carbonate toxicity in a newborn infant.\n000|a|A newborn with massive tricuspid regurgitation, atrial flutter, congestive heart failure, and a high serum lithium level is described. This is the first patient to initially manifest tricuspid regurgitation and atrial flutter, and the 11th described patient with cardiac disease among infants exposed to lithium compounds in the first trimester of pregnancy. Sixty-three percent of these infants had tricuspid valve involvement. Lithium carbonate may be a factor in the increasing incidence of congenital heart disease when taken during early pregnancy. It also causes neurologic depression, cyanosis, and cardiac arrhythmia when consumed prior to delivery.";
		Document doc = CollectionFactory.loadDocumentFromString(text, false);

		MaxentNERRecognizer nerFinder = new MaxentNERRecognizer("models/ner/cdr_full.perc.model", MaxentNERFactoryExample.createFeatureGenerator());		
		for (Sentence sent : doc.getSentences()) {			
			String output = nerFinder.recognize(doc.getPmid(), sent);
			System.out.print(output);
		}
	}
}
