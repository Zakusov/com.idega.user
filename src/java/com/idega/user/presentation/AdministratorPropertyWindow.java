/*
 * $Id: AdministratorPropertyWindow.java,v 1.9 2007/05/08 16:35:49 eiki Exp $
 *
 * Copyright (C) 2001-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.presentation;

import com.idega.presentation.IWContext;
import com.idega.presentation.TabbedPropertyPanel;

/**
 * <p>
 * This is a special implementation of the Usser property window that is only opened
 * for the Super Administrator user.
 * </p>
 * Last modified: $Date: 2007/05/08 16:35:49 $ by $Author: eiki $
 * 
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,<a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.9 $
 */
public class AdministratorPropertyWindow extends UserPropertyWindow {

	public final String SESSION_ADDRESS = "ic_admin_property_window";
	
	public AdministratorPropertyWindow() {
		super();
	}

	public String getSessionAddressString() {
		return this.SESSION_ADDRESS;
	}

	/**
	 * <p>
	 * Overriding the method to only display login tab.
	 * </p>
	 */
	public void initializePanel(IWContext iwc, TabbedPropertyPanel panel) {
		
		initializeUserAndGroup(iwc);
		
		UserLoginTab ult = new UserLoginTab();
		ult.setPanel(panel);
		ult.setUserID(getUserId());
		ult.setGroupID(getGroupId());
		
		panel.addTab(ult, 0, iwc);
	}
}