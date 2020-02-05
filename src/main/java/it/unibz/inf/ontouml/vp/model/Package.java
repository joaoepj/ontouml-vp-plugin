package it.unibz.inf.ontouml.vp.model;

import java.util.LinkedList;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * 
 * Implementation of ModelElement to handle IPackage objects
 * to be serialized as ontouml-schema/Package
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
 */

public class Package implements ModelElement {

	private final IPackage sourceModelElement;
	
	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private final String id;

	@SerializedName("name")
	@Expose
	private String name;
	
	@SerializedName("description")
	@Expose
	private String description;
	
	@SerializedName("propertyAssignments")
	@Expose
	private JsonObject propertyAssignments;

	@SerializedName("elements")
	@Expose
	private LinkedList<ModelElement> elements;

	public Package(IPackage source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PACKAGE;
		this.id = source.getId();
		setName(source.getName());
		setDescription(source.getDescription());
		
		final IModelElement[] children = source.toChildArray();
		for (int i = 0; children != null && i < children.length; i++) {
			final IModelElement child = children[i];

			switch (child.getModelType()) {
			case IModelElementFactory.MODEL_TYPE_PACKAGE:
				addElement(new Package((IPackage) child));
				break;
			case IModelElementFactory.MODEL_TYPE_MODEL:
				addElement(new Model((IModel) child));
				break;
			case IModelElementFactory.MODEL_TYPE_CLASS:
				addElement(new Class((IClass) child));
				break;
//			TODO Add remaining elements, maybe by adding these to relation's source's package.
//			case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
//			case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
			}
		}
		
		ITaggedValueContainer lContainer = source.getTaggedValues();
		if (lContainer != null) {
			JsonObject obj = new JsonObject();
			ITaggedValue[] lTaggedValues = lContainer.toTaggedValueArray();

			for (int i = 0; lTaggedValues != null && i < lTaggedValues.length; i++) {
				switch (lTaggedValues[i].getType()) {
				case 1:
					JsonObject reference = new JsonObject();
					reference.addProperty("type", ModelElement.toOntoUMLSchemaType(lTaggedValues[i].getValueAsElement()));
					reference.addProperty("id", lTaggedValues[i].getValueAsElement().getId());
					obj.add(lTaggedValues[i].getName(), reference);
					break;
				case 5:
					obj.addProperty(lTaggedValues[i].getName(), Integer.parseInt((String) lTaggedValues[i].getValue()));
					break;
				case 6:
					obj.addProperty(lTaggedValues[i].getName(), Float.parseFloat((String) lTaggedValues[i].getValue()));
					break;
				case 7:
					obj.addProperty(lTaggedValues[i].getName(), Boolean.parseBoolean((String) lTaggedValues[i].getValue()));
					break;
				default:
					obj.addProperty(lTaggedValues[i].getName(), (String) lTaggedValues[i].getValueAsString());
				}
			}
			setPropertyAssignments(obj);
		}
	}

	@Override
	public IPackage getSourceModelElement() {
		return sourceModelElement;
	}
	
	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}
	
	@Override
	public String getOntoUMLType() {
		return this.type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public JsonObject getPropertyAssignments() {
		return propertyAssignments;
	}

	public void setPropertyAssignments(JsonObject propertyAssignments) {
		this.propertyAssignments = propertyAssignments;
	}

	public LinkedList<ModelElement> getElements() {
		return elements;
	}

	public void setElements(LinkedList<ModelElement> elements) {
		this.elements = elements;
	}

	public void addElement(ModelElement element) {
		if(getElements() == null) {
			setElements(new LinkedList<ModelElement>());
		}
		
		this.elements.add(element);
	}

}
