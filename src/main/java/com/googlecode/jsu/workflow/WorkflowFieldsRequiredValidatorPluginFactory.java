package com.googlecode.jsu.workflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * This class defines the parameters available for Fields Required Validator.
 * 
 * @author Gustavo Martin.
 */
public class WorkflowFieldsRequiredValidatorPluginFactory 
		extends AbstractWorkflowPluginFactory 
		implements WorkflowPluginValidatorFactory {
	
	private static final String SELECTED_FIELDS = "hidFieldsList";

	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List allFields = CommonPluginUtils.getRequirableFields();
		
		velocityParams.put("val-fieldsList", allFields);
		velocityParams.put("val-splitter", WorkflowUtils.SPLITTER);
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(
			Map velocityParams, AbstractDescriptor descriptor
	) {
		getVelocityParamsForInput(velocityParams);
		
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		velocityParams.remove("val-fieldsList");
		
		String strFieldsSelected = (String) args.get(SELECTED_FIELDS);
		Collection fieldsSelected = WorkflowUtils.getFields(strFieldsSelected, WorkflowUtils.SPLITTER);
		
		List allFields = CommonPluginUtils.getRequirableFields();
		allFields.removeAll(fieldsSelected);
		
		velocityParams.put("val-fieldsListSelected", fieldsSelected);
		velocityParams.put("val-hidFieldsList", WorkflowUtils.getStringField(fieldsSelected, WorkflowUtils.SPLITTER));
		velocityParams.put("val-fieldsList", allFields);
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(
			Map velocityParams, AbstractDescriptor descriptor
	) {
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		String strFieldsSelected = (String) args.get(SELECTED_FIELDS);
		Collection fieldsSelected = WorkflowUtils.getFields(strFieldsSelected, WorkflowUtils.SPLITTER);
		
		velocityParams.put("val-fieldsListSelected", fieldsSelected);
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map validatorParams) {
		Map<String, String> params = new HashMap<String, String>();
		String strFieldsSelected = extractSingleParam(validatorParams, SELECTED_FIELDS);
		
		if ("".equals(strFieldsSelected)) {
			throw new IllegalArgumentException("At least one field must be selected");
		}
		
		params.put(SELECTED_FIELDS, strFieldsSelected);
		
		return params;
	}
}
