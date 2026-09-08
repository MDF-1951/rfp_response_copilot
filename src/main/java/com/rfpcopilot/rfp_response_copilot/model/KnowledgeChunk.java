package com.rfpcopilot.rfp_response_copilot.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "knowledge_chunks")
public class KnowledgeChunk {
	
	@Id
	private String id;
	
	@Field(type = FieldType.Text)
	private String text;
	
	@Field(type = FieldType.Keyword)
	private String documentId;
	
	@Field(type = FieldType.Keyword)
	private String documentName;
	
	/*@Field(type = FieldType.Keyword)
	private String section;
	*/
	
	@Field(type = FieldType.Integer)
	private Integer chunkNumber;

	@Field(type = FieldType.Dense_Vector, dims = 384, similarity = "cosine")

	private List<Float> embedding;
	
	public KnowledgeChunk() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	/*public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}*/

	public Integer getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(Integer chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public List<Float> getEmbedding() {
		return embedding;
	}

	public void setEmbedding(List<Float> embedding) {
		this.embedding = embedding;
	}
	
	

}
