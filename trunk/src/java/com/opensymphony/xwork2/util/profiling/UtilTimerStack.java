/*
 * Copyright (c) 2002-2003, Atlassian Software Systems Pty Ltd All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *     * Neither the name of Atlassian Software Systems Pty Ltd nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.opensymphony.xwork2.util.profiling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A timer stack.
 * <p>
 * Usage:
 * <pre>
 * String logMessage = "Log message";
 * UtilTimerStack.push(logMessage);
 * try
 * {
 *   //do some code
 * }
 * finally
 * {
 *   UtilTimerStack.pop(logMessage); //this needs to be the same text as above
 * }
 * </pre>
 */
public class UtilTimerStack
{

    // A reference to the current ProfilingTimerBean
    private static ThreadLocal current = new ThreadLocal();

    /**
     * System property that controls whether this timer should be used or not.  Set to "true" activates
     * the timer.  Set to "false" to disactivate.
     */
    public static final String ACTIVATE_PROPERTY = "xwork.profile.activate";

    public static final String MIN_TIME = "atlassian.profile.mintime";
    
    private static final Log log = LogFactory.getLog(UtilTimerStack.class);

    public static void push(String name)
    {
        if (!isActive())
            return;

        //create a new timer and start it
        ProfilingTimerBean newTimer = new ProfilingTimerBean(name);
        newTimer.setStartTime();

        //if there is a current timer - add the new timer as a child of it
        ProfilingTimerBean currentTimer = (ProfilingTimerBean) current.get();
        if (currentTimer != null)
        {
            currentTimer.addChild(newTimer);
        }

        //set the new timer to be the current timer
        current.set(newTimer);
    }

    public static void pop(String name)
    {
        if (!isActive())
            return;

        ProfilingTimerBean currentTimer = (ProfilingTimerBean) current.get();

        //if the timers are matched up with each other (ie push("a"); pop("a"));
        if (currentTimer != null && name != null && name.equals(currentTimer.getResource()))
        {
            currentTimer.setEndTime();
            ProfilingTimerBean parent = currentTimer.getParent();
            //if we are the root timer, then print out the times
            if (parent == null)
            {
                printTimes(currentTimer);
                current.set(null); //for those servers that use thread pooling
            }
            else
            {
                current.set(parent);
            }
        }
        else
        {
            //if timers are not matched up, then print what we have, and then print warning.
            if (currentTimer != null)
            {
                printTimes(currentTimer);
                current.set(null); //prevent printing multiple times
                log.warn("Unmatched Timer.  Was expecting " + currentTimer.getResource() + ", instead got " + name);
            }
        }


    }

    private static void printTimes(ProfilingTimerBean currentTimer)
    {
        log.info(currentTimer.getPrintable(getMinTime()));
    }

    private static long getMinTime()
    {
        try
        {
            return Long.parseLong(System.getProperty(MIN_TIME, "0"));
        }
        catch (NumberFormatException e)
        {
           return -1;
        }
    }

    public static boolean isActive()
    {
        return System.getProperty(ACTIVATE_PROPERTY) != null;
    }

    public static void setActive(boolean active)
    {
        if (active)
            System.setProperty(ACTIVATE_PROPERTY, "true");
        else
            System.setProperty(ACTIVATE_PROPERTY, null);
    }

}
