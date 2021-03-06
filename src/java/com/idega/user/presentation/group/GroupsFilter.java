package com.idega.user.presentation.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.web2.business.JQuery;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.UserConstants;
import com.idega.user.business.group.GroupsFilterEngine;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class GroupsFilter extends Block {
	
	private String selectedGroupParameterName = "selectedGroup";
	
	private List<String> selectedGroups;
	
	private int levelsToOpen = 1;
	
	private boolean displayAllLevels;
	private boolean useRadioBox = false;
	
	private String onClickAction = null;
	
	@Autowired
	private JQuery jQuery;
	
	@Override
	public void main(IWContext iwc) {
		ELUtil.getInstance().autowire(this);
		
		IWBundle bundle = getBundle(iwc);
		List<String> files = new ArrayList<String>(4);
		files.add(jQuery.getBundleURIToJQueryLib());
		files.add(CoreConstants.DWR_ENGINE_SCRIPT);
		files.add("/dwr/interface/GroupsFilterEngine.js");
		files.add(bundle.getVirtualPathWithFileNameString("javascript/GroupsFilter.js"));
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, files);
		PresentationUtil.addStyleSheetToHeader(iwc, bundle.getVirtualPathWithFileNameString("style/user.css"));
		
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		
		Layer container = new Layer();
		add(container);
		container.setStyleClass("groupsChooserBoxStyle");
		
		Layer header = new Layer();
		header.setStyleClass("groupsChooserBoxHeaderStyle");
		container.add(header);
		
		Layer clearLayer = new Layer();
		clearLayer.setStyleClass("Clear");
		container.add(clearLayer);
		
		Layer body = new Layer();
		container.add(body);
		body.setStyleClass("groupsChooserBoxBodyStyle");
		
		TextInput filterInput = new TextInput();
		filterInput.setTitle(iwrb.getLocalizedString("enter_group_name", "Enter group's name"));
		
		boolean selectedAnything = !ListUtil.isEmpty(selectedGroups);
		StringBuilder selectedGroupsExpression = selectedAnything ? new StringBuilder("[") : new StringBuilder("null");
		if (selectedAnything) {
			for (int i = 0; i < selectedGroups.size(); i++) {
				selectedGroupsExpression.append("'").append(selectedGroups.get(i)).append("'");
				
				if ((i + 1) < selectedGroups.size()) {
					selectedGroupsExpression.append(", ");
				}
			}
			selectedGroupsExpression.append("]");
		}
		
		String changedOnClickAction = null;
		if (!StringUtil.isEmpty(onClickAction)) {
			GroupsFilterEngine filterEngine = ELUtil.getInstance().getBean(GroupsFilterEngine.SPRING_BEAN_IDENTIFIER);
			changedOnClickAction = filterEngine.getActionAppliedToBeParameter(onClickAction);
		}
		String filterAction = new StringBuilder("GroupsFilter.filterGroupsByNewInfo(['").append(filterInput.getId()).append("', '")
												.append(iwrb.getLocalizedString("searching", "Searching...")).append("', '").append(body.getId())
												.append("', '").append(selectedGroupParameterName).append("'], ").append(selectedGroupsExpression.toString())
												.append(", ").append(StringUtil.isEmpty(changedOnClickAction) ? "null" : new StringBuilder("'")
												.append(changedOnClickAction).append("'").toString()).append(", ").append(useRadioBox).append(");").toString();
		filterInput.setOnKeyPress(new StringBuilder("if (isEnterEvent(event)) {").append(filterAction).append(" return false;}").toString());
		Label filterInputLabel = new Label(iwrb.getLocalizedString("groups_filter", "Groups filter") + ":", filterInput);
		header.add(filterInputLabel);
		header.add(filterInput);
		
		GenericButton searchButton = new GenericButton(iwrb.getLocalizedString("search", "Search"));
		searchButton.setStyleClass("applicationButton");
		searchButton.setTitle(iwrb.getLocalizedString("search_for_groups", "Search for groups"));
		searchButton.setOnClick(filterAction);
		header.add(searchButton);
		GenericButton clearResults = new GenericButton(iwrb.getLocalizedString("clear", "Clear"));
		clearResults.setStyleClass("applicationButton");
		clearResults.setTitle(iwrb.getLocalizedString("clear_search_results", "Clear search results"));
		header.add(clearResults);
		clearResults.setOnClick(new StringBuilder("GroupsFilter.clearSearchResults(['").append(body.getId()).append("', '").append(filterInput.getId())
													.append("']);").toString());
		
		body.add(getFilteredGroupsBox(iwc));
	}
	
	private FilteredGroupsBox getFilteredGroupsBox(IWContext iwc) {
		FilteredGroupsBox filteredGroups = new FilteredGroupsBox();
		
		GroupsFilterEngine groupsFilter = ELUtil.getInstance().getBean(GroupsFilterEngine.SPRING_BEAN_IDENTIFIER);
		filteredGroups.setGroups(groupsFilter.getUserGroups(iwc, true));
		
		filteredGroups.setLevelsToOpen(levelsToOpen);
		filteredGroups.setDisplayAllLevels(displayAllLevels);
		filteredGroups.setOnClickAction(onClickAction);
		filteredGroups.setUseRadioBox(useRadioBox);
		
		String[] selectedInForm = iwc.getParameterValues(selectedGroupParameterName);
		if (selectedInForm != null) {
			selectedGroups = Arrays.asList(selectedInForm);
		}
		
		filteredGroups.setSelectedGroups(selectedGroups);
		filteredGroups.setSelectedGroupParameterName(selectedGroupParameterName);
		return filteredGroups;
	}
	
	@Override
	public String getBundleIdentifier() {
		return UserConstants.IW_BUNDLE_IDENTIFIER;
	}

	public List<String> getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(List<String> selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

	public String getSelectedGroupParameterName() {
		return selectedGroupParameterName;
	}

	public void setSelectedGroupParameterName(String selectedGroupParameterName) {
		this.selectedGroupParameterName = selectedGroupParameterName;
	}

	public int getLevelsToOpen() {
		return levelsToOpen;
	}

	public void setLevelsToOpen(int levelsToOpen) {
		this.levelsToOpen = levelsToOpen;
	}

	public boolean isDisplayAllLevels() {
		return displayAllLevels;
	}

	public void setDisplayAllLevels(boolean displayAllLevels) {
		this.displayAllLevels = displayAllLevels;
	}

	public String getOnClickAction() {
		return onClickAction;
	}

	public void setOnClickAction(String onClickAction) {
		this.onClickAction = onClickAction;
	}

	public boolean isUseRadioBox() {
		return useRadioBox;
	}

	public void setUseRadioBox(boolean useRadioBox) {
		this.useRadioBox = useRadioBox;
	}

}
