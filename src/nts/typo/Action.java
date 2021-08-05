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
// Filename: nts/typo/Action.java
// $Id: Action.java,v 1.1.1.1 2000/02/15 18:47:21 ksk Exp $
package	nts.typo;

import	java.io.Serializable;
import	nts.base.Glue;
import	nts.builder.Builder;
import	nts.command.Token;
import	nts.command.CommandBase;

public abstract class	Action	extends CommandBase
				implements Serializable {

    public static final Action		NULL = null;

    public abstract void	exec(Builder bld, Token src);

    public Glue		getSkipForLeaders(Builder bld) { return Glue.NULL; }

}
