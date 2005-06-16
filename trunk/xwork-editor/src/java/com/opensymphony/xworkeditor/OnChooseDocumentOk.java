/*
 * @(#)OnChooseDocumentOk.java
 *
 * Copyright (c) 2003 Jon Lipsky
 * All rights reserved.
 */
package com.opensymphony.xworkeditor;

import com.xe.xface.core.Showable;
import com.xe.xface.core.XDialog;
import com.xe.xface.core.XFace;
import com.xe.xface.core.XMap;
import com.xe.xface.core.adapter.XAbstractAdapter;
import com.xe.xface.core.event.ElementEvent;

import java.io.File;

/**
 * This adapter will ...
 *
 * @author <a href="mailto:jon.lipsky@xesoft.com">Jon Lipsky</a>
 * @created September 22, 2003
 * @version $Revision$
 *
 * @xelement.name on-choose-document-ok-adapter
 * @xelement.namespace xwork-editor
 */
public class OnChooseDocumentOk extends XAbstractAdapter
{
	public OnChooseDocumentOk()
	{
		super.setParentEventIds(new int[]{XFace.OK});
	}

	public void handleElementEvent(ElementEvent aElementEvent)
	{
		// Do nothing
	}

	public void handleParentEvent(ElementEvent aElementEvent)
	{
		try
		{
			XDialog vParent = (XDialog)getParent();
			File vFile = vParent.getFile();
			String vPath = vFile.getCanonicalPath();
			XMap vMap = (XMap)findElement("../../editor/variables");
			vMap.put("file",vPath);
			((Showable)findElement("../../editor")).show();
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage(),e);
		}

	}
}
