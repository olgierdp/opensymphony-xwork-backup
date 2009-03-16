package com.opensymphony.xwork.config;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.config.annotations.Results;
import com.opensymphony.xwork.config.annotations.Action;
import com.opensymphony.xwork.config.annotations.Result;

/**
 * DOCUMENT ME!
 * 
 * $Id: $
 * 
 * @author $Author: $
 * @version $Revision: $
 */
@Action(namespace = "/abc/def")
@Results( {
    @Result(name = "success", type = "mock", value = "/test/one/action1.vm"),
    @Result(name = "input", type = "mock", value = "/test/one/action2.vm"),
    @Result(name = "error", type = "mock", value = "/test/one/action3.vm") })
public class TestTwoAction extends ActionSupport {
  /**
   * DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws Exception
   *           DOCUMENT ME!
   */
  @Action(name = "TestTwoAction")
  public String runMe() throws Exception {
    return SUCCESS;
  }

  /**
   * DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws Exception
   *           DOCUMENT ME!
   */
  @Action(name = "TestTwoAction2", namespace = "/abc/defg")
  @Results( {
      @Result(name = "success", type = "mock", value = "TestTwoAction"),
      @Result(name = "input", type = "mock", value = "TestTwoAction") })
  public String justRunIt() throws Exception {
    return SUCCESS;
  }
}
