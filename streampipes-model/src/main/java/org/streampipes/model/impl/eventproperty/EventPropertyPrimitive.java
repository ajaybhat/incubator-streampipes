package org.streampipes.model.impl.eventproperty;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.streampipes.model.impl.quality.EventPropertyQualityDefinition;
import org.streampipes.model.util.ModelUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/", "rdfs", "http://www.w3.org/2000/01/rdf-schema#", "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"})
@RdfsClass("sepa:EventPropertyPrimitive")
@Entity
public class EventPropertyPrimitive extends EventProperty {

	private static final long serialVersionUID = 665989638281665875L;

	@RdfProperty("sepa:hasPropertyType")
	private String runtimeType;
	
	@RdfProperty("sepa:hasMeasurementUnit")
	@OneToOne(cascade = {CascadeType.ALL})
	private URI measurementUnit;
	
	@RdfProperty("sepa:valueSpecification")
	@OneToOne(cascade = {CascadeType.ALL})
	private ValueSpecification valueSpecification;
	
	public EventPropertyPrimitive()
	{
		super();
	}
	
	public EventPropertyPrimitive(EventPropertyPrimitive other)
	{
		super(other);
		this.runtimeType = other.getRuntimeType();
		this.measurementUnit = other.getMeasurementUnit();
	}
	
	public EventPropertyPrimitive(List<URI> subClassOf)
	{
		super(subClassOf);
	}

	public EventPropertyPrimitive(String runtimeType, String runtimeName,
			String measurementUnit, List<URI> subClassOf) {
		super(runtimeName, subClassOf);
		this.runtimeType = runtimeType;
		//this.measurementUnit = measurementUnit;
	}

	public EventPropertyPrimitive(String propertyType, String propertyName,
			String measurementUnit, List<URI> subClassOf, List<EventPropertyQualityDefinition> qualities) {
		super(propertyName, subClassOf, qualities);
		this.runtimeType = propertyType;
		//this.measurementUnit = measurementUnit;
	}
	
	public String getRuntimeType() {
		return runtimeType;
	}
	public void setRuntimeType(String propertyType) {
		this.runtimeType = propertyType;
	}
	
	public URI getMeasurementUnit() {
		return measurementUnit;
	}
	public void setMeasurementUnit(URI measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	public ValueSpecification getValueSpecification() {
		return valueSpecification;
	}

	public void setValueSpecification(ValueSpecification valueSpecification) {
		this.valueSpecification = valueSpecification;
	}

	@Override
	public Map<String, Object> getRuntimeFormat() {
		return getUntypedRuntimeFormat();
	}

	@Override
	public Map<String, Object> getUntypedRuntimeFormat() {
		Map<String, Object> result = new HashMap<>();
		result.put(runtimeName, ModelUtils.getPrimitiveClass(runtimeType));
		return result;
	}

	@Override
	public List<String> getFullPropertyName(String prefix) {
		List<String> result = new ArrayList<String>();
		result.add(prefix + runtimeName);
		return result;
	}
	
}