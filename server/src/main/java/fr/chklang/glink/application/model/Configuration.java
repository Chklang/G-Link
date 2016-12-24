package fr.chklang.glink.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.chklang.glink.application.dao.ConfigurationDAO;

@Entity
@Table(name="t_configuration")
public class Configuration extends AbstractModel {

	@Id
	@Column(name="key", length=32)
	private String key;

	@Column(name="value", length=2048)
	private String value;
	
	public static ConfigurationDAO finder = new ConfigurationDAO();

	public Configuration() {
		super();
	}

	public Configuration(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Configuration other = (Configuration) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Configuration setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public String toString() {
		return "Configuration [key=" + key + ", value=" + value + "]";
	}
}
