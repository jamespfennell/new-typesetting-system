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
// Filename: nts/typo/FontMetricEquiv.java
// $Id: FontMetricEquiv.java,v 1.1.1.1 2000/02/01 10:53:23 ksk Exp $
package	nts.typo;

import	java.io.Serializable;
import	nts.io.Log;
import	nts.node.FontMetric;
import	nts.command.Command;

public class	FontMetricEquiv	extends Command.ExtEquiv
				implements Serializable {

    private FontMetric	value = NullFontMetric.METRIC;

    public FontMetric	get() { return value; }

    public void		set(FontMetric val, boolean glob)
	{ beforeSetting(glob); value = val; }

    public Object	getEqValue() { return value; }
    public void	setEqValue(Object val) { value = (FontMetric) val; }

    public final void	addEqDescOn(Log log)
	{ log.add("current font"); }

    public void	addEqValueOn(Log log)
	{ value.getIdent().addEscapedOn(log); }

}
