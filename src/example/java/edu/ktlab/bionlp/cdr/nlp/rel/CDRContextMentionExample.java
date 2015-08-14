package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.ktlab.bionlp.cdr.data.Annotation;
import edu.ktlab.bionlp.cdr.data.Collection;
import edu.ktlab.bionlp.cdr.data.CollectionFactory;
import edu.ktlab.bionlp.cdr.data.Document;
import edu.ktlab.bionlp.cdr.data.Sentence;

public class CDRContextMentionExample {

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadFile("data/cdr/cdr_test.txt", false);

		for (Document doc : col.getDocuments()) {
			System.out.println(doc.getPmid());
			Set<String> refs = doc.getRelationReferences();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				for (Annotation ann : sent.getAnnotations()) {
					String[] mentionRefs = StringUtils.split(ann.getReference(), "|");
					for (String ref : mentionRefs)
						if (refs.contains(ref))
							System.out.println(ann);
				}
			}

		}
	}

}
