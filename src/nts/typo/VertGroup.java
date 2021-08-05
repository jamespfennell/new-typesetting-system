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
// Filename: nts/typo/VertGroup.java
// $Id: VertGroup.java,v 1.1.1.1 2001/03/20 09:42:05 ksk Exp $
package	nts.typo;

import	nts.builder.Builder;
import	nts.builder.VBoxBuilder;
import	nts.command.SimpleGroup;

public class	VertGroup	extends SimpleGroup {

    protected final VBoxBuilder		builder;

    public VertGroup(VBoxBuilder builder) { this.builder = builder; }
    public VertGroup() { this(new VBoxBuilder(currLineNumber())); }

    public void		start() {
	TypoCommand.getTypoConfig().resetParagraph();
	Builder.push(builder);
    }

    public void		stop() { Paragraph.finish(); }

    public void		close() { Builder.pop(); }

}
