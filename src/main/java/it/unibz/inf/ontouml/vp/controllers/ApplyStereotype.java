package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.IStereotype;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

/**
 * 
 * Implementation of context sensitive action of change OntoUML stereotypes in
 * model elements.
 * 
 * @author Claudenir Fonseca
 *
 */
public class ApplyStereotype implements VPContextActionController {

	public static final String ACTION_ADD_STEREOTYPE_KIND = "it.unibz.inf.ontouml.vp.addKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_COLLECTIVE_KIND = "it.unibz.inf.ontouml.vp.addCollectiveKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_QUANTITY_KIND = "it.unibz.inf.ontouml.vp.addQuantityKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_RELATOR_KIND = "it.unibz.inf.ontouml.vp.addRelatorKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MODE_KIND = "it.unibz.inf.ontouml.vp.addModeKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_QUALITY_KIND = "it.unibz.inf.ontouml.vp.addQualityKindStereotype";

	public static final String ACTION_ADD_STEREOTYPE_SUBKIND = "it.unibz.inf.ontouml.vp.addSubkindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_PHASE = "it.unibz.inf.ontouml.vp.addPhaseStereotype";
	public static final String ACTION_ADD_STEREOTYPE_ROLE = "it.unibz.inf.ontouml.vp.addRoleStereotype";

	public static final String ACTION_ADD_STEREOTYPE_CATEGORY = "it.unibz.inf.ontouml.vp.addCategoryStereotype";
	public static final String ACTION_ADD_STEREOTYPE_ROLE_MIXIN = "it.unibz.inf.ontouml.vp.addRoleMixinStereotype";
	public static final String ACTION_ADD_STEREOTYPE_PHASE_MIXIN = "it.unibz.inf.ontouml.vp.addPhaseMixinStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MIXIN = "it.unibz.inf.ontouml.vp.addMixinStereotype";

	public static final String ACTION_ADD_STEREOTYPE_MATERIAL = "it.unibz.inf.ontouml.vp.addMaterialStereotype";
	public static final String ACTION_ADD_STEREOTYPE_COMPARATIVE = "it.unibz.inf.ontouml.vp.addComparativeStereotype";
	public static final String ACTION_ADD_STEREOTYPE_HISTORICAL = "it.unibz.inf.ontouml.vp.addHistoricalStereotype";

	public static final String ACTION_ADD_STEREOTYPE_MEDIATION = "it.unibz.inf.ontouml.vp.addMediationStereotype";
	public static final String ACTION_ADD_STEREOTYPE_CHARACTERIZATION = "it.unibz.inf.ontouml.vp.addCharacterizationStereotype";
	public static final String ACTION_ADD_STEREOTYPE_EXTERNAL_DEPENDENCE = "it.unibz.inf.ontouml.vp.addExternalDependenceStereotype";

	public static final String ACTION_ADD_STEREOTYPE_COMPONENT_OF = "it.unibz.inf.ontouml.vp.addComponentOfStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MEMBER_OF = "it.unibz.inf.ontouml.vp.addMemberOfStereotype";
	public static final String ACTION_ADD_STEREOTYPE_SUB_COLLECTION_OF = "it.unibz.inf.ontouml.vp.addSubCollectionStereotype";
	public static final String ACTION_ADD_STEREOTYPE_SUB_QUANTITY_OF = "it.unibz.inf.ontouml.vp.addSubQuantityStereotype";
	public static final String ACTION_ADD_STEREOTYPE_SUM = "it.unibz.inf.ontouml.vp.addSumStereotype";

	public static final Color COLOR_FUNCTIONAL_COMPLEX_KIND = new Color(255, 253, 146);
	public static final Color COLOR_COLLECTIVE_KIND = new Color(255, 253, 146);
	public static final Color COLOR_QUANTITY_KIND = new Color(255, 253, 146);
	public static final Color COLOR_RELATOR_KIND = new Color(246, 194, 137);
	public static final Color COLOR_MODE_KIND = new Color(176, 251, 162);
	public static final Color COLOR_QUALITY_KIND = new Color(156, 160, 248);

	public static final Color COLOR_FUNCTIONAL_COMPLEX_SORTAL = new Color(255, 254, 199);
	public static final Color COLOR_COLLECTIVE_SORTAL = new Color(255, 254, 199);
	public static final Color COLOR_QUANTITY_SORTAL = new Color(255, 254, 199);
	public static final Color COLOR_RELATOR_SORTAL = new Color(251, 225, 197);
	public static final Color COLOR_MODE_SORTAL = new Color(206, 252, 198);
	public static final Color COLOR_QUALITY_SORTAL = new Color(205, 205, 249);

	public static final Color COLOR_NON_SORTAL = new Color(224, 224, 224);

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		final IModelElement element = context.getModelElement();
		final IStereotype[] stereotypes = element.toStereotypeModelArray();

		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			element.removeStereotype(stereotypes[i]);
		}

		switch (action.getActionId()) {
		case ACTION_ADD_STEREOTYPE_KIND:
			element.addStereotype(StereotypeUtils.STR_KIND);
			paint(context, COLOR_FUNCTIONAL_COMPLEX_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_COLLECTIVE_KIND:
			element.addStereotype(StereotypeUtils.STR_COLLECTIVE_KIND);
			paint(context, COLOR_COLLECTIVE_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_QUANTITY_KIND:
			element.addStereotype(StereotypeUtils.STR_QUANTITY_KIND);
			paint(context, COLOR_QUANTITY_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_RELATOR_KIND:
			element.addStereotype(StereotypeUtils.STR_RELATOR_KIND);
			paint(context, COLOR_RELATOR_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_MODE_KIND:
			element.addStereotype(StereotypeUtils.STR_MODE_KIND);
			paint(context, COLOR_MODE_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_QUALITY_KIND:
			element.addStereotype(StereotypeUtils.STR_QUALITY_KIND);
			paint(context, COLOR_QUALITY_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_SUBKIND:
			element.addStereotype(StereotypeUtils.STR_SUBKIND);
			paint(context, getSpecializedColor(context));
			break;
		case ACTION_ADD_STEREOTYPE_ROLE:
			element.addStereotype(StereotypeUtils.STR_ROLE);
			paint(context, getSpecializedColor(context));
			break;
		case ACTION_ADD_STEREOTYPE_PHASE:
			element.addStereotype(StereotypeUtils.STR_PHASE);
			paint(context, getSpecializedColor(context));
			break;
		case ACTION_ADD_STEREOTYPE_CATEGORY:
			element.addStereotype(StereotypeUtils.STR_CATEGORY);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ACTION_ADD_STEREOTYPE_ROLE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_ROLE_MIXIN);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ACTION_ADD_STEREOTYPE_PHASE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_PHASE_MIXIN);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ACTION_ADD_STEREOTYPE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_MIXIN);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ACTION_ADD_STEREOTYPE_MATERIAL:
			element.addStereotype(StereotypeUtils.STR_MATERIAL);
			break;
		case ACTION_ADD_STEREOTYPE_COMPARATIVE:
			element.addStereotype(StereotypeUtils.STR_COMPARATIVE);
			break;
		case ACTION_ADD_STEREOTYPE_HISTORICAL:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL);
			break;
		case ACTION_ADD_STEREOTYPE_MEDIATION:
			element.addStereotype(StereotypeUtils.STR_MEDIATION);
			break;
		case ACTION_ADD_STEREOTYPE_CHARACTERIZATION:
			element.addStereotype(StereotypeUtils.STR_CHARACTERIZATION);
			break;
		case ACTION_ADD_STEREOTYPE_EXTERNAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
			break;
		case ACTION_ADD_STEREOTYPE_COMPONENT_OF:
			element.addStereotype(StereotypeUtils.STR_COMPONENT_OF);
			break;
		case ACTION_ADD_STEREOTYPE_MEMBER_OF:
			element.addStereotype(StereotypeUtils.STR_MEMBER_OF);
			break;
		case ACTION_ADD_STEREOTYPE_SUB_COLLECTION_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_COLLECTION_OF);
			break;
		case ACTION_ADD_STEREOTYPE_SUB_QUANTITY_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_QUANTITY_OF);
			break;
		case ACTION_ADD_STEREOTYPE_SUM:
			element.addStereotype(StereotypeUtils.STR_SUM);
			break;
		}
	}

	@Override
	public void update(VPAction action, VPContext context) {
	}

	/**
	 * 
	 * Paints the assigned diagram element with the assigned color. No effect
	 * whenever auto-coloring is disabled or color is <code>null</code>.
	 * 
	 * @param diagramElement
	 * @param color
	 */
	private void paint(VPContext context, Color color) {
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled() || color == null) {
			return;
		}
		
		
		final IModelElement _class = context.getModelElement();
		final IDiagramElement[] diagramElements =  _class.getDiagramElements();
		
		for (int i = 0; diagramElements != null && i < diagramElements.length; i++) {
			IDiagramElement diagramElement = diagramElements[i];
			if (diagramElement instanceof IShapeUIModel) {
				((IShapeUIModel) diagramElement).getFillColor().setColor1(color);
			} else {
				diagramElement.setForeground(color);
			}
		}

	}

	/**
	 * 
	 * Returns first sortal color occurring on one of the generalizations of this
	 * class. If generalization with such color is found, returns <code>null</code>.
	 * 
	 * @param context
	 * 
	 */
	private Color getSpecializedColor(VPContext context) {
		final IModelElement sourceModelElement = context.getModelElement();
		final ISimpleRelationship[] specializations = sourceModelElement.toToRelationshipArray();

		for (int i = 0; specializations != null && i < specializations.length; i++) {
			if (!(specializations[i] instanceof IGeneralization)) {
				continue;
			}

			final IModelElement superClass = specializations[i].getFrom();
			final IDiagramElement[] superDiagramElements = superClass.getDiagramElements();

			for (int j = 0; j < superDiagramElements.length; j++) {
				if (!(superDiagramElements[j] instanceof IShapeUIModel)) {
					continue;
				}

				final Color superColor = ((IShapeUIModel) superDiagramElements[j]).getFillColor().getColor1();

				if (superColor.equals(COLOR_FUNCTIONAL_COMPLEX_KIND)
						|| superColor.equals(COLOR_FUNCTIONAL_COMPLEX_SORTAL)) {
					return COLOR_FUNCTIONAL_COMPLEX_SORTAL;
				} else if (superColor.equals(COLOR_COLLECTIVE_KIND) || superColor.equals(COLOR_COLLECTIVE_SORTAL)) {
					return COLOR_COLLECTIVE_SORTAL;
				} else if (superColor.equals(COLOR_QUANTITY_KIND) || superColor.equals(COLOR_QUANTITY_SORTAL)) {
					return COLOR_QUANTITY_SORTAL;
				} else if (superColor.equals(COLOR_RELATOR_KIND) || superColor.equals(COLOR_RELATOR_SORTAL)) {
					return COLOR_RELATOR_SORTAL;
				} else if (superColor.equals(COLOR_MODE_KIND) || superColor.equals(COLOR_MODE_SORTAL)) {
					return COLOR_MODE_SORTAL;
				} else if (superColor.equals(COLOR_QUALITY_KIND) || superColor.equals(COLOR_QUALITY_SORTAL)) {
					return COLOR_QUALITY_SORTAL;
				}
			}
		}

		return null;
	}

}