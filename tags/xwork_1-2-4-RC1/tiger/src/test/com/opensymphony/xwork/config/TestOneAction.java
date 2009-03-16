package com.opensymphony.xwork.config;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.config.annotations.Result;
import com.opensymphony.xwork.config.annotations.Action;
import com.opensymphony.xwork.config.annotations.Results;


/**
 * DOCUMENT ME!
 * 
 * $Id: $
 * 
 * @author $Author: $
 * @version $Revision: $
 */
@Action(name = "TestOneAction", namespace = "/abc/def")
public class TestOneAction extends ActionSupport {
  /**
   * DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws Exception
   *           DOCUMENT ME!
   */
  @Override
  @Results( {
      @Result(name = "success", type = "mock", value = "/test/one/action.vm"),
      @Result(name = "input", type = "mock", value = "/test/one/action.vm"),
      @Result(name = "error", type = "mock", value = "/test/one/action.vm") })
  public String execute() throws Exception {
    return super.execute();
  }
}
