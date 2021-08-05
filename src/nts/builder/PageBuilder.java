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
// Filename: nts/builder/PageBuilder.java
// $Id: PageBuilder.java,v 1.1.1.1 2001/03/20 11:21:42 ksk Exp $
package	nts.builder;

import	nts.io.Log;
import	nts.base.Num;
import	nts.node.PageSplit;

public class	PageBuilder extends VertBuilder {

    public PageBuilder(int line, PageSplit list) { super(line, list); }
    protected final PageSplit	pageSplit() { return (PageSplit) list; }
    public String	modeName() { return "vertical"; }
    public boolean	willBeBroken() { return true; }
    public void		buildPage() { pageSplit().build(); }

    public boolean	needsParSkip() { return true; }
    //XXX maybe the par_skip on the top of the page is ignored anyhow, so we
    //XXX can introduce NodeList predicat like didntStartYet and use it
    //XXX instead of isEmpty.

    public boolean	canTakeLastNode()
	{ return (!pageSplit().allConsumed()); }

    protected void	specialShow(Log log, int depth, int breadth)
	{ pageSplit().show(log, depth, breadth); specialShow(log); }

}
