package de.lmu.ifi.dbs.medmon.database.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the COMMENTS database table.
 * 
 */
@Entity
@Table(name="COMMENTS")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable=false)
	private int id;

    @Lob()
	@Column(nullable=false)
	private String comments;

	//bi-directional many-to-one association to Data
	@OneToMany(mappedBy="comment")
	private Set<Data> data;

    public Comment() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Set<Data> getData() {
		return this.data;
	}

	public void setData(Set<Data> data) {
		this.data = data;
	}
	
}