/*
 * @(#)OnStartupAdapter.java
 *
 * Copyright (c) 2003 Jon Lipsky
 * All rights reserved.
 */
package com.opensymphony.xworkeditor;

import com.xe.xface.core.adapter.XAbstractAdapter;
import com.xe.xface.core.event.ElementEvent;
import com.xe.xface.core.XFace;
import com.xe.xface.core.Showable;
import com.xe.xface.core.XApplication;

/**
 * This adapter will decide which panel to show depending on if the user
 * has agreed to the license or not.
 *
 * @author <a href="mailto:jon.lipsky@xesoft.com">Jon Lipsky</a>
 * @created September 22, 2003
 * @version $Revision$
 *
 * @xelement.name on-startup-adapter
 * @xelement.namespace xwork-editor
 */
public class OnStartupAdapter extends XAbstractAdapter
{
	public OnStartupAdapter()
	{
		super.setParentEventIds(new int[]{XFace.STARTUP});
	}

	public void handleElementEvent(ElementEvent aElementEvent)
	{
		// Do nothing
	}

	public void handleParentEvent(ElementEvent aElementEvent)
	{
		if (XApplication.getPreferences().getBoolean("xwork-editor/accept_license", false) == false)
		{
			((Showable) findElement("../license")).show();
		}
		else
		{
			((Showable) findElement("../choose_document")).show();
		}
	}
}
