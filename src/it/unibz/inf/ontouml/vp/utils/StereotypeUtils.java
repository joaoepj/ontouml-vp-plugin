package it.unibz.inf.ontouml.vp.utils;

import java.util.HashSet;
import java.util.Set;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

public class StereotypeUtils {

	public static final String STR_CATEGORY = "category";
	public static final String STR_MIXIN = "mixin";
	public static final String STR_ROLE_MIXIN = "roleMixin";
	public static final String STR_PHASE_MIXIN = "phaseMixin";

	public static final String STR_KIND = "kind";
	public static final String STR_COLLECTIVE_KIND = "collectiveKind";
	public static final String STR_QUANTITY_KIND = "quantityKind";
	public static final String STR_RELATOR_KIND = "relatorKind";
	public static final String STR_QUALITY_KIND = "qualityKind";
	public static final String STR_MODE_KIND = "modeKind";
	public static final String STR_SUBKIND = "subKind";

	public static final String STR_ROLE = "role";
	public static final String STR_PHASE = "phase";

	/* OntoUML Association Stereotypes for Relation Types */
	public static final String STR_MATERIAL = "material";
	public static final String STR_COMPARATIVE = "comparative";
	public static final String STR_HISTORICAL = "historical";
	public static final String STR_MEDIATION = "mediation";
	public static final String STR_CHARACTERIZATION = "characterization";
	public static final String STR_EXTERNAL_DEPENDENCE = "external dependence";

	public static void removeAllModelSteryotypes(String modelType) {
		ProjectManager pm = ApplicationManager.instance().getProjectManager();
		IProject p = pm.getProject();
		IModelElement[] all_strs = pm.getSelectableStereotypesForModelType(modelType, p, true);

		for (IModelElement str : all_strs)
			str.delete();

		return;
	}

	public static void setUpOntoUMLStereotypes() {
		System.out.println("Checking stereotypes...");

		final Set<String> class_stereotypes = getOntoUMLClassStereotypeNames();

		for (String ontoUML_stereotype : class_stereotypes) {
			System.out.println("Generating stereotype �" + ontoUML_stereotype + "�.");
			final IStereotype s = IModelElementFactory.instance().createStereotype();
			s.setName(ontoUML_stereotype);
			s.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
		}

		final Set<String> association_stereotypes = getOntoUMLAssociationStereotypeNames();

		for (String missing_str_name : association_stereotypes) {
			System.out.println("Generating stereotype �" + missing_str_name + "�.");
			final IStereotype s = IModelElementFactory.instance().createStereotype();
			s.setName(missing_str_name);
			s.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
		}

		System.out.println("All OntoUML stereotypes are available.");
	}

	public static Set<String> getOntoUMLClassStereotypeNames() {
		final Set<String> str_names = new HashSet<String>();

		str_names.add(STR_CATEGORY);
		str_names.add(STR_MIXIN);
		str_names.add(STR_ROLE_MIXIN);
		str_names.add(STR_PHASE_MIXIN);

		str_names.add(STR_KIND);
		str_names.add(STR_QUANTITY_KIND);
		str_names.add(STR_RELATOR_KIND);
		str_names.add(STR_QUALITY_KIND);
		str_names.add(STR_MODE_KIND);
		str_names.add(STR_SUBKIND);

		str_names.add(STR_ROLE);
		str_names.add(STR_PHASE);

		return str_names;
	}

	public static Set<String> getOntoUMLAssociationStereotypeNames() {
		final Set<String> str_names = new HashSet<String>();

		str_names.add(STR_MATERIAL);
		str_names.add(STR_COMPARATIVE);
		str_names.add(STR_HISTORICAL);
		str_names.add(STR_MEDIATION);
		str_names.add(STR_CHARACTERIZATION);
		str_names.add(STR_EXTERNAL_DEPENDENCE);

		return str_names;
	}
}