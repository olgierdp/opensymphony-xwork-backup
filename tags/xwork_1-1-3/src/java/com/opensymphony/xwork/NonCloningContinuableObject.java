/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork;

import com.uwyn.rife.continuations.ContinuableObject;

/**
 * Implementing this interface indicates that the action should not be cloned, but instead should be re-used. This is
 * needed when you are using objects, fields, and method variables that cannot be cloned. The downside to using this is
 * that the advanced forward/backward historical support that normally automatically comes with continuations is no
 * longer available.
 */
public interface NonCloningContinuableObject extends ContinuableObject {
}
