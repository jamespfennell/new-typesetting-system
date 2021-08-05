// Copyright 2001 by
// DANTE e.V. and any individual authors listed elsewhere in this file. 
// 
// This file is part of the NTS system.
// ------------------------------------
// 
// It may be distributed and/or modified under the
// conditions of the NTS Public License (NTSPL), either version 1.0
// of this license or (at your option) any later version.
// The latest version of this license is in
//    http://www.dante.de/projects/nts/ntspl.txt
// and version 1.0 or later is part of all distributions of NTS 
// version 1.0-beta or later.
// 
// The list of all files belonging to the NTS distribution is given in
// the file `manifest.txt'. 
// 
// Filename: nts/node/SettingContext.java
// $Id: SettingContext.java,v 1.1.1.1 1999/12/06 23:13:25 ksk Exp $
package	nts.node;

import	nts.base.Dimen;

public final class	SettingContext {

    public final BoxSizes		around;
    public final GlueSetting		setting;
    public final TypeSetter.Mark	start;
    public final boolean		allowIO;

    public SettingContext(BoxSizes around, GlueSetting setting,
			    TypeSetter.Mark start, boolean allowIO) {
	this.around = around; this.setting = setting;
	this.start = start; this.allowIO = allowIO;
    }

    public SettingContext	shiftedUp(Dimen shift) {
        return new SettingContext(around.shiftedUp(shift),
				  setting, start, allowIO);
    }

    public SettingContext	shiftedLeft(Dimen shift) {
        return new SettingContext(around.shiftedLeft(shift),
				  setting, start, allowIO);
    }

    public SettingContext	allowingIO(boolean allowIO)
	{ return new SettingContext(around, setting, start, allowIO); }

}
