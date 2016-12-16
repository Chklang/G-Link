package fr.chklang.glink.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Finder;
import com.avaje.ebean.Model;

import fr.chklang.glink.application.dao.LinkDAO;

@Entity
@Table(name="T_LINK")
public class Link extends Model {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="idlink")
	private int idLink;

	@Column(name="command", length=2048)
	private String command;
	
	@Column(name="name", unique=true, length=256)
	private String name;

	@Column(name="description", length=4096)
	private String description;

	@Column(name="icon", length=2048)
	private String icon;
	
	public static LinkDAO finder = new LinkDAO();

	public Link() {
		super();
	}

	public Link(String command, String name, String description, String icon) {
		super();
		this.command = command;
		this.name = name;
		this.description = description;
		this.icon = icon;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Link other = (Link) obj;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (idLink != other.idLink)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getCommand() {
		return command;
	}

	public String getDescription() {
		return description;
	}

	public String getIcon() {
		return icon;
	}

	public int getIdLink() {
		return idLink;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + idLink;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setIdLink(int idLink) {
		this.idLink = idLink;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Link [idLink=" + idLink + ", command=" + command + ", name=" + name + ", description=" + description + ", icon=" + icon + "]";
	}
}
