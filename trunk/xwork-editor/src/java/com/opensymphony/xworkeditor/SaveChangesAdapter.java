/*
 * @(#)SaveChangesAdapter.java
 *
 * Copyright (c) 2003 Jon Lipsky
 * All rights reserved.
 */
package com.opensymphony.xworkeditor;

import com.xe.xface.core.XFace;
import com.xe.xface.core.XMap;
import com.xe.xface.core.adapter.XAbstractAdapter;
import com.xe.xface.core.document.XDocument;
import com.xe.xface.core.event.ElementEvent;

/**
 * This adapter will decide which panel to show depending on if the user
 * has agreed to the license or not.
 *
 * @author <a href="mailto:jon.lipsky@xesoft.com">Jon Lipsky</a>
 * @created September 22, 2003
 * @version $Revision$
 *
 * @xelement.name save-changes-adapter
 * @xelement.namespace xwork-editor
 */
public class SaveChangesAdapter extends XAbstractAdapter
{
	public SaveChangesAdapter()
	{
		super.setParentEventIds(new int[]{XFace.YES});
	}

	public void handleElementEvent(ElementEvent aElementEvent)
	{
		// Do nothing
	}

	public void handleParentEvent(ElementEvent aElementEvent)
	{
		try
		{
			XMap vVariables = (XMap)findElement("../../variables");
			XDocument vDocument = (XDocument)vVariables.get("document");
			vDocument.save();
			System.exit(0);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(),e);
		}
	}
}
