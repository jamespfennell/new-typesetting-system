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
// Filename: nts/noad/SuperScriptNoad.java
// $Id: SuperScriptNoad.java,v 1.1.1.1 2000/10/05 07:48:47 ksk Exp $
package	nts.noad;

import	nts.io.Log;
import	nts.io.CntxLog;
import	nts.node.MathWordBuilder;

public class	SuperScriptNoad	extends ScriptNoad {

    public SuperScriptNoad(Noad body, Field script) { super(body, script); }

    public final boolean	alreadySuperScripted() { return true; }

    protected ScriptNoad	rebodiedCopy(Noad body)
	{ return new SuperScriptNoad(body, script); }

    /* TeXtp[696] */
    public void		addOn(Log log, CntxLog cntx)
	{ body.addOnWithScripts(log, cntx, script, EmptyField.FIELD); }

    /* TeXtp[696] */
    public void		addOnWithScripts(Log log, CntxLog cntx,
					 Field sup, Field sub)
	{ body.addOnWithScripts(log, cntx, script, sub); }

    public Egg		convert(Converter conv)
	{ return body.convertWithScripts(conv, script, EmptyField.FIELD); }

    public Egg		convertWithScripts(Converter conv,
					   Field sup, Field sub)
	{ return body.convertWithScripts(conv, script, sub); }

    public Egg		wordFinishingEgg(MathWordBuilder word,
					 Converter conv) {
	return body.wordFinishingEggWithScripts(word, conv,
						script, EmptyField.FIELD);
    }

    public Egg		wordFinishingEggWithScripts(MathWordBuilder word,
						    Converter conv,
						    Field sup, Field sub)
	{ return body.wordFinishingEggWithScripts(word, conv, script, sub); }

}
