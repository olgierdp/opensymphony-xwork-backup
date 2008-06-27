/*
 * @(#)OnEditorShowAdapter.java
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
import com.xe.xface.core.XMap;
import com.xe.xface.core.model.data.XDataModel;
import com.xe.xface.core.document.XMLDocument;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This adapter will decide which panel to show depending on if the user
 * has agreed to the license or not.
 *
 * @author <a href="mailto:jon.lipsky@xesoft.com">Jon Lipsky</a>
 * @created September 22, 2003
 * @version $Revision$
 *
 * @xelement.name on-editor-show-adapter
 * @xelement.namespace xwork-editor
 */
public class OnEditorShowAdapter extends XAbstractAdapter
{
	public OnEditorShowAdapter()
	{
		super.setParentEventIds(new int[]{XFace.SHOW});
	}

	public void handleElementEvent(ElementEvent aElementEvent)
	{
		// Do nothing
	}

	public void handleParentEvent(ElementEvent aElementEvent)
	{
		try
		{
			XMap vVariables = (XMap)findElement("../variables");
			String vPath = (String)vVariables.get("file");
			XMLDocument vDocument = new XMLDocument(new FileInputStream(vPath));
			vDocument.setPath(vPath);
			XDataModel vModel = (XDataModel)findElement("../editor/model");
			vDocument.setModel(vModel);
			vVariables.put("document",vDocument);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e.getMessage(),e);
		}
	}
}
