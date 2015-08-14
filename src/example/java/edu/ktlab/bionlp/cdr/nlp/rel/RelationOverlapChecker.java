package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.Set;

import com.google.common.collect.Sets;

import edu.ktlab.bionlp.cdr.data.Collection;
import edu.ktlab.bionlp.cdr.data.CollectionFactory;
import edu.ktlab.bionlp.cdr.data.Relation;

public class RelationOverlapChecker {
	public static void main(String... strings) {
		Collection colDev = CollectionFactory.loadFile("data/cdr/cdr_dev/CDR_DevelopmentSet.txt", false);
		Collection colTrain = CollectionFactory.loadFile("data/cdr/cdr_train/CDR_TrainingSet.txt", false);
		int countOverlap = 0;
		Set<Relation> uniqueRels = Sets.newHashSet();
		for (Relation rel : colDev.getRelations()) {
			if (colTrain.getRelations().contains(rel)) {
				countOverlap++;
				uniqueRels.add(rel);
			}				
		}
		System.out.println(countOverlap);
		System.out.println(uniqueRels.size());
	}

}