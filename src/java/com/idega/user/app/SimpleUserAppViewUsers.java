package com.idega.user.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BackButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.SelectOption;
import com.idega.user.bean.SimpleUserPropertiesBean;
import com.idega.user.business.GroupHelperBusinessBean;
import com.idega.user.data.Group;

public class SimpleUserAppViewUsers extends SimpleUserApp {
	
	private String containerId = null;
	private String instanceId = null;
	
	private Group parentGroup = null;
	private Group groupForUsersWithoutLogin = null;
	
	private String groupTypes = null;
	private String groupTypesForChildGroups = null;
	private String roleTypesForChildGroups = null;
	
	private boolean getParentGroupsFromTopNodes = true;
	
	private GroupHelperBusinessBean groupsHelper = new GroupHelperBusinessBean();
	SimpleUserAppHelper helper = new SimpleUserAppHelper();

	public SimpleUserAppViewUsers(String instanceId, String containerId) {
		this.instanceId = instanceId;
		this.containerId = containerId;
	}
	
	public SimpleUserAppViewUsers(String instanceId, String containerId, Group parentGroup, Group groupForUsersWithoutLogin,
			String groupTypes, String groupTypesForChildGroups, String roleTypesForChildGroups, boolean getParentGroupsFromTopNodes) {
		this(instanceId, containerId);
		this.parentGroup = parentGroup;
		this.groupForUsersWithoutLogin = groupForUsersWithoutLogin;
		this.groupTypes = groupTypes;
		this.groupTypesForChildGroups = groupTypesForChildGroups;
		this.roleTypesForChildGroups = roleTypesForChildGroups;
		this.getParentGroupsFromTopNodes = getParentGroupsFromTopNodes;
	}

	public void main(IWContext iwc) {
		Layer container = new Layer();
		add(container);
		
		//	Container for group users
		Layer valuesContainer = new Layer();
		valuesContainer.setStyleClass("allUsersValuesLinesStyleClass");
		
		//	Dropdowns
		DropdownMenu groupsDropdown = new DropdownMenu();
		String parentGroupsChooserId = groupsDropdown.getId();
		
		DropdownMenu childGroupsChooser = new DropdownMenu();
		String childGroupsChooserId = childGroupsChooser.getId();
		
		DropdownMenu orderByChooser = new DropdownMenu();
		String orderByChooserId = orderByChooser.getId();
		
		DropdownMenu[] dropDowns = new DropdownMenu[3];
		dropDowns[0] = groupsDropdown;
		dropDowns[1] = childGroupsChooser;
		dropDowns[2] = orderByChooser;
		
		String[] ids = new String[5];
		ids[0] = valuesContainer.getId();
		ids[1] = parentGroupsChooserId;
		ids[2] = childGroupsChooserId;
		ids[3] = orderByChooserId;
		ids[4] = containerId;

		//	Upper part - dropdowns and description
		Layer choosersAndDescription = new Layer();
		choosersAndDescription.setStyleClass("choosersAndDescriptionStyleClass");
		container.add(choosersAndDescription);
		
		//	Dropdowns
		Layer choosersContainer = new Layer();
		choosersAndDescription.add(choosersContainer);
		choosersContainer.setStyleClass("userApplicationChoosersContainer");
		SimpleUserPropertiesBean bean = addChooserContainer(iwc, choosersContainer, dropDowns, ids);
		
		//	Description
		Layer descriptionContainer = new Layer();
		choosersAndDescription.add(descriptionContainer);
		descriptionContainer.setStyleClass("userApplicationDescriptionContainerStyleClass");
		descriptionContainer.add(new Text(getResourceBundle(iwc).getLocalizedString("user_application_view_users_descripton", "To view users in the groups first select the parent group and then the desired sub group. You can remove a user from a group by checking the checkboxes here down below and by clicking the \"Remove\" button. To add new users to a group click the \"Add Users\" button.")));
		
		//	Spacer
		choosersAndDescription.add(getSpacer());
		
		//	Lower part
		Layer lowerPart = new Layer();
		lowerPart.setStyleClass("userAppLowerPartStyleClass");
		container.add(lowerPart);
		
		//	Members list
		Layer membersList = new Layer();
		membersList.setStyleClass("membersListContainerStyleClass");
		lowerPart.add(membersList);
		addMembersList(iwc, bean, membersList, valuesContainer, containerId);
		
		lowerPart.add(getSpacer());
		
		//	Buttons
		Layer buttons = new Layer();
		container.add(buttons);
		buttons.setStyleClass("userApplicationButtonsContainerStyleClass");
		addButtons(iwc, buttons, ids);
		
		container.add(getSpacer());
	}
	
	private void addButtons(IWContext iwc, Layer container, String[] ids) {
		BackButton back = new BackButton();
		container.add(back);
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		GenericButton removeFromGroup = new GenericButton(iwrb.getLocalizedString("remove_from_group", "Remove from group"));
		StringBuffer removeAction = new StringBuffer("removeSelectedUsers('");
		removeAction.append(iwrb.getLocalizedString("removing", "Removing...")).append(PARAMS_SEPARATOR);
		removeAction.append(iwrb.getLocalizedString("are_you_sure", "Are You sure?")).append(PARAMS_SEPARATOR);
		removeAction.append(iwrb.getLocalizedString("select_users_to_remove", "Please, select user(s) to remove firstly!"));
		removeAction.append("');");
		removeFromGroup.setOnClick(removeAction.toString());
		container.add(removeFromGroup);
	
		GenericButton addUser = new GenericButton(iwrb.getLocalizedString("add_user", "Add user"));
		String id = getGroupForUsersWithoutLogin() == null ? null : getGroupForUsersWithoutLogin().getId();
		StringBuffer addUserAction = new StringBuffer("addUserPresentationObject('").append(instanceId);
		addUserAction.append(PARAMS_SEPARATOR).append(ids[4]).append(PARAMS_SEPARATOR).append(ids[1]);
		addUserAction.append(PARAMS_SEPARATOR).append(ids[2]).append(PARAMS_SEPARATOR);
		addUserAction.append(iwrb.getLocalizedString("loading", "Loading...")).append("', ");
		addUserAction.append(helper.getJavaScriptParameter(id)).append(", null, ");
		addUserAction.append(helper.getJavaScriptParameter(getGroupTypesForChildGroups())).append(COMMA_SEPARATOR);
		addUserAction.append(helper.getJavaScriptParameter(getRoleTypesForChildGroups())).append(");");
		addUser.setOnClick(addUserAction.toString());
		container.add(addUser);
	}
	
	private void addMembersList(IWContext iwc, SimpleUserPropertiesBean bean, Layer container, Layer valuesContainer,
			String mainContainerId) {
		IWBundle bundle = getBundle(iwc);
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		Layer labels = new Layer();
		labels.setStyleClass("groupMembersListLabelsStyleClass");
		container.add(labels);
		
		Layer userNameLabelContainer = new Layer();
		userNameLabelContainer.setStyleClass("userNameLabelContainerStyleClass");
		userNameLabelContainer.add(new Text(iwrb.getLocalizedString("user.user_name", "Name")));
		labels.add(userNameLabelContainer);
		
		Layer userPersonalIdLabelContainer = new Layer();
		userPersonalIdLabelContainer.setStyleClass("userPersonalIdLabelContainerStyleClass");
		userPersonalIdLabelContainer.add(new Text(iwrb.getLocalizedString("personal_id", "Personlal ID")));
		labels.add(userPersonalIdLabelContainer);
		
		Layer changeUserLabelContainer = new Layer();
		changeUserLabelContainer.setStyleClass("changeUserLabelContainerStyleClass");
		changeUserLabelContainer.add(new Text(iwrb.getLocalizedString("change_user", "Change user")));
		labels.add(changeUserLabelContainer);
		
		Layer removeUserLabelContainer = new Layer();
		removeUserLabelContainer.setStyleClass("removeUserLabelContainerStyleClass");
		removeUserLabelContainer.add(new Text(iwrb.getLocalizedString("remove_user", "Remove user")));
		labels.add(removeUserLabelContainer);
		
		labels.add(getSpacer());
		
		container.add(valuesContainer);
		
		String image = bundle.getVirtualPathWithFileNameString(EDIT_IMAGE);
		valuesContainer.add(helper.getMembersList(iwc, bean, groupsHelper, image));
	}
	
	private SimpleUserPropertiesBean addChooserContainer(IWContext iwc, Layer choosers, DropdownMenu[] dropDowns, String[] ids) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		String loadingMessage = iwrb.getLocalizedString("loading", "Loading...");
		
		DropdownMenu groupsDropdown = dropDowns[0];
		DropdownMenu childGroupsChooser = dropDowns[1];
		DropdownMenu orderByChooser = dropDowns[2];
		
		String groupUsersContainerId = ids[0];
		String parentGroupsChooserId = ids[1];
		String childGroupsChooserId = ids[2];
		String orderByChooserId = ids[3];
		
		//	Parent group
		Layer parentGroupLabelContainer = new Layer();
		parentGroupLabelContainer.setStyleClass("parentGroupLabelContainerStyleClass");
		choosers.add(parentGroupLabelContainer);
		parentGroupLabelContainer.add(new Text(iwrb.getLocalizedString("select_parent_group", "Select parent group")));
		Layer parentGroupChooserContainer = new Layer();
		parentGroupChooserContainer.setStyleClass("parentGroupContainerStyleClass");
		choosers.add(parentGroupChooserContainer);
		Group parentGroup = fillParentGroupChooser(iwc, groupsDropdown, parentGroupChooserContainer, ids);
		choosers.add(getSpacer());
		
		//	Child groups
		Layer childGroupsLabelContainer = new Layer();
		choosers.add(childGroupsLabelContainer);
		childGroupsLabelContainer.setStyleClass("childGroupsLabelContainerStyleClass");
		childGroupsLabelContainer.add(new Text(iwrb.getLocalizedString("select_sub_group", "Select sub group")));
		Layer childGroupChooserContainer = new Layer();
		childGroupChooserContainer.setStyleClass("childGroupChooserContainerSyleClass");
		choosers.add(childGroupChooserContainer);
		Group childGroup = fillChildGroupsChooser(iwc, childGroupChooserContainer, parentGroup, childGroupsChooser, ids);
		choosers.add(getSpacer());
		
		//	Order
		Layer orderByLabelContainer = new Layer();
		choosers.add(orderByLabelContainer);
		orderByLabelContainer.setStyleClass("orderByLabelContainerStyleClass");
		orderByLabelContainer.add(new Text(iwrb.getLocalizedString("order_by", "Order by")));
		Layer orderByChooserContainer = new Layer();
		choosers.add(orderByChooserContainer);
		orderByChooserContainer.setStyleClass("orderByChooserStyleClass");
		
		SelectOption byName = new SelectOption(iwrb.getLocalizedString("name", "Name"), USER_ORDER_BY_NAME);
		orderByChooser.addOption(byName);
		StringBuffer orderByAction = new StringBuffer("reOrderGroupUsers('").append(parentGroupsChooserId);
		orderByAction.append(PARAMS_SEPARATOR).append(childGroupsChooserId).append(PARAMS_SEPARATOR).append(orderByChooserId);
		orderByAction.append(PARAMS_SEPARATOR).append(groupUsersContainerId).append(PARAMS_SEPARATOR);
		orderByAction.append(loadingMessage).append("', ").append(getDefaultParameters(ids[1], ids[2], loadingMessage)).append(");");
		orderByChooser.setOnChange(orderByAction.toString());
		SelectOption byId = new SelectOption(iwrb.getLocalizedString("personal_id", "Personal ID"), USER_ORDER_BY_ID);
		orderByChooser.addOption(byId);
		orderByChooserContainer.add(orderByChooser);
		
		SimpleUserPropertiesBean bean = new SimpleUserPropertiesBean();
		bean.setInstanceId(instanceId);
		bean.setContainerId(containerId);
		bean.setParentGroupChooserId(parentGroupsChooserId);
		bean.setGroupChooserId(childGroupsChooserId);
		bean.setDefaultGroupId(getGroupForUsersWithoutLogin() == null ? null : getGroupForUsersWithoutLogin().getId());
		bean.setGroupTypes(getGroupTypesForChildGroups());
		bean.setRoleTypes(getRoleTypesForChildGroups());
		bean.setMessage(loadingMessage);
		if (parentGroup != null) {
			bean.setParentGroupId(getParsedId(parentGroup.getId()));
		}
		if (childGroup != null) {
			bean.setGroupId(getParsedId(childGroup.getId()));
		}
		bean.setOrderBy(USER_ORDER_BY_NAME);
		
		return bean;
	}
	
	private int getParsedId(String id) {
		if (id == null) {
			return -1;
		}
		
		try {
			return Integer.valueOf(id).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	private Group fillChildGroupsChooser(IWContext iwc, Layer container, Group parent, DropdownMenu childGroups, String[] ids) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		String loadingMessage = iwrb.getLocalizedString("loading", "Loading...");
		
		List filteredChildGroups = groupsHelper.getFilteredChildGroups(iwc, parent, getGroupTypesForChildGroups(),
				getRoleTypesForChildGroups(), ",");
		if (filteredChildGroups.size() == 0) {
			container.add(new Text(iwrb.getLocalizedString("no_groups_available", "There are no groups available")));
			return null;
		}
		
		String parentGroupChooserId = ids[1];
		String groupUsersContainerId = ids[0];
		String orderByChooserId = ids[3];
		
		childGroups.addMenuElements(filteredChildGroups);
		StringBuffer onChangeChildGroupsChooserAction = new StringBuffer("selectChildGroup(this.value, '");
		onChangeChildGroupsChooserAction.append(groupUsersContainerId).append(PARAMS_SEPARATOR);
		onChangeChildGroupsChooserAction.append(parentGroupChooserId).append(PARAMS_SEPARATOR);
		onChangeChildGroupsChooserAction.append(orderByChooserId).append(PARAMS_SEPARATOR);
		onChangeChildGroupsChooserAction.append(loadingMessage).append("', ");
		onChangeChildGroupsChooserAction.append(getDefaultParameters(ids[1], ids[2], loadingMessage)).append(");");
		childGroups.setOnChange(onChangeChildGroupsChooserAction.toString());
		container.add(childGroups);
		return (Group) filteredChildGroups.get(0);
	}
	
	private String getDefaultParameters(String parentGroupChooserId, String childGroupChooserId, String message) {
		StringBuffer params = new StringBuffer("[").append(helper.getJavaScriptParameter(instanceId)).append(COMMA_SEPARATOR);
		params.append(helper.getJavaScriptParameter(containerId)).append(COMMA_SEPARATOR);
		params.append(helper.getJavaScriptParameter(childGroupChooserId)).append(COMMA_SEPARATOR);
		params.append(helper.getJavaScriptParameter(getGroupForUsersWithoutLogin() == null ? null : getGroupForUsersWithoutLogin().getId()));
		params.append(COMMA_SEPARATOR).append(helper.getJavaScriptParameter(getGroupTypesForChildGroups())).append(COMMA_SEPARATOR);
		params.append(helper.getJavaScriptParameter(getRoleTypesForChildGroups())).append(COMMA_SEPARATOR);
		params.append(helper.getJavaScriptParameter(message)).append(COMMA_SEPARATOR);
		params.append(helper.getJavaScriptParameter(parentGroupChooserId));
		params.append("]");
		
		return params.toString();
	}
	
	private Group fillParentGroupChooser(IWContext iwc, DropdownMenu groupsDropdown, Layer container, String[] ids) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		if (getParentGroup() == null) {	//	Group is not set as property
			Collection topGroups = groupsHelper.getTopGroups(iwc, iwc.getCurrentUser());
			if (!isGetParentGroupsFromTopNodes()) {
				topGroups = groupsHelper.getTopAndParentGroups(topGroups);	//	Will get top nodes and parent groups for them
			}
			
			if (topGroups.size() > 0) {
				List filteredTopGroups = new ArrayList(groupsHelper.getFilteredGroups(topGroups, getGroupTypes(), ","));
				if (filteredTopGroups.size() > 1) {
					String groupUsersContainerId = ids[0];
					String childGroupsChooserId = ids[2];
					String orderByChooserId = ids[3];
					
					groupsDropdown.addMenuElements(topGroups);
					StringBuffer action = new StringBuffer("reloadComponents('");
					action.append(iwrb.getLocalizedString("loading", "Loading...")).append(PARAMS_SEPARATOR);
					action.append(childGroupsChooserId).append(PARAMS_SEPARATOR);
					action.append(orderByChooserId).append(PARAMS_SEPARATOR);
					action.append(groupUsersContainerId).append(PARAMS_SEPARATOR);
					action.append(groupsDropdown.getId()).append("', ");
					action.append(helper.getJavaScriptParameter(getGroupTypesForChildGroups())).append(COMMA_SEPARATOR);
					action.append(helper.getJavaScriptParameter(getRoleTypesForChildGroups())).append(", this.value, ");
					action.append(helper.getJavaScriptParameter(instanceId)).append(COMMA_SEPARATOR);
					action.append(helper.getJavaScriptParameter(containerId)).append(COMMA_SEPARATOR);
					action.append(helper.getJavaScriptParameter(getGroupForUsersWithoutLogin() == null ? null : getGroupForUsersWithoutLogin().getId()));
					action.append(COMMA_SEPARATOR).append(helper.getJavaScriptParameter(ids[1])).append(");");
					groupsDropdown.setOnChange(action.toString());
					container.add(groupsDropdown);
					return (Group) filteredTopGroups.get(0);
				}
				else {
					//	Only one group available
					Object o = filteredTopGroups.get(0);
					if (o instanceof Group) {
						Group group = (Group) o;
						addGroupNameLabel(iwrb, container, group);
						return group;
					}
					return null;
				}
			}
			//	No groups found for current user
			container.add(new Text(iwrb.getLocalizedString("no_groups_available", "There are no groups available")));
			return null;
		}
		else {	//	Group is set as property
			addGroupNameLabel(iwrb, container, getParentGroup());
			return getParentGroup();
		}
	}
	
	private void addGroupNameLabel(IWResourceBundle iwrb, Layer container, Group group) {
		String groupName = group.getName() == null ? iwrb.getLocalizedString("unknown_group", "Unknown group") : group.getName();
		container.add(new Text(groupName));
	}
	
	private Layer getSpacer() {
		Layer spacer = new Layer();
		spacer.setStyleClass("spacer");
		return spacer;
	}

	public void setGroupForUsersWithoutLogin(Group groupForUsersWithoutLogin) {
		this.groupForUsersWithoutLogin = groupForUsersWithoutLogin;
	}

	public void setGroupTypes(String groupTypes) {
		this.groupTypes = groupTypes;
	}

	public void setGroupTypesForChildGroups(String groupTypesForChildGroups) {
		this.groupTypesForChildGroups = groupTypesForChildGroups;
	}

	public void setParentGroup(Group parentGroup) {
		this.parentGroup = parentGroup;
	}

	public void setRoleTypesForChildGroups(String roleTypesForChildGroups) {
		this.roleTypesForChildGroups = roleTypesForChildGroups;
	}

	public void setGetParentGroupsFromTopNodes(boolean getParentGroupsFromTopNodes) {
		this.getParentGroupsFromTopNodes = getParentGroupsFromTopNodes;
	}

	private boolean isGetParentGroupsFromTopNodes() {
		return getParentGroupsFromTopNodes;
	}

	private Group getGroupForUsersWithoutLogin() {
		return groupForUsersWithoutLogin;
	}

	private String getGroupTypes() {
		return groupTypes;
	}

	private String getGroupTypesForChildGroups() {
		return groupTypesForChildGroups;
	}

	private Group getParentGroup() {
		return parentGroup;
	}

	private String getRoleTypesForChildGroups() {
		return roleTypesForChildGroups;
	}
	
}