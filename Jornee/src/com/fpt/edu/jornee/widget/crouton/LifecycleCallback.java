/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LifecycleCallback.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

/*
 * Copyright 2012 - 2014 Benjamin Weiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fpt.edu.jornee.widget.crouton;

/** Provides callback methods on major lifecycle events of a {@link Crouton}. */
public interface LifecycleCallback {
  /** Will be called when your Crouton has been displayed. */
  public void onDisplayed();

  /** Will be called when your {@link Crouton} has been removed. */
  public void onRemoved();

  //public void onCeasarDressing();
}
