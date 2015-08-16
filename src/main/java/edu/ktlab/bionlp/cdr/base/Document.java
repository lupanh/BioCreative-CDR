package edu.ktlab.bionlp.cdr.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class Document {
	private String pmid;
	private String content;
	private List<Passage> passages;
	private List<Annotation> annotations;
	private List<Relation> relations;

	public Document() {
		passages = new ArrayList<Passage>();
		annotations = new ArrayList<Annotation>();
		relations = new ArrayList<Relation>();
	}

	public String getPmid() {
		return pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}

	public boolean addPassage(Passage passage) {
		return passages.add(passage);
	}

	public boolean addAnnotation(Annotation annotation) {
		for (Passage pass : passages) {
			pass.addAnnotation(annotation);
			for (Sentence sent : pass.getSentences()) {
				if (sent.containAnnotation(annotation))
					sent.addAnnotation(annotation);
			}
		}
		return annotations.add(annotation);
	}

	public boolean addRelation(Relation relation) {
		return relations.add(relation);
	}

	public List<Passage> getPassages() {
		return passages;
	}

	public void setPassages(List<Passage> passages) {
		this.passages = passages;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public String getContent() {
		String text = "";
		for (Passage pass : passages)
			text += pass.getContent() + "\n";
		return text.substring(0, text.length() - 1);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Sentence> getSentences() {
		List<Sentence> sents = Lists.newArrayList();
		for (Passage pass : passages)
			sents.addAll(pass.getSentences());
		return sents;
	}

	public Set<Relation> getRelationCandidates() {
		Set<Relation> rels = new HashSet<Relation>();
		Set<Annotation> chemicalAnnotations = new HashSet<Annotation>();
		Set<Annotation> diseaseAnnotations = new HashSet<Annotation>();

		for (Annotation ann : annotations) {
			if (ann.getType().equals("Chemical"))
				chemicalAnnotations.add(ann);
			if (ann.getType().equals("Disease"))
				diseaseAnnotations.add(ann);
		}

		for (Annotation ched : chemicalAnnotations)
			for (Annotation dis : diseaseAnnotations)
				rels.add(new Relation("CID", ched.getReference(), dis.getReference()));

		return rels;
	}

	public Set<String> getRelationReferences() {
		Set<String> refs = new HashSet<String>();

		for (Relation rel : relations) {
			refs.add(rel.getChemicalID());
			refs.add(rel.getDiseaseID());
		}

		return refs;
	}
}
