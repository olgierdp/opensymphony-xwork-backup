package com.opensymphony.xworkeditor;

import com.xe.xface.core.XApplication;
import com.xe.xface.core.XElement;
import com.xe.xface.Builder;

public class Application
{
	public static void main(String[] args)
	{
		try
		{
			String vUI = null;

			if (args.length > 0)
			{
				vUI = args[0];
			}

			Builder vBuilder = new Builder(vUI);

			XApplication vApplication = (XApplication)vBuilder.build("xwork-editor/xwork-editor.xfm.xml");
			vApplication.start();
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}
}
