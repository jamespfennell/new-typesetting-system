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
// Filename: nts/io/Log.java
// $Id: Log.java,v 1.1.1.1 1999/06/09 10:07:58 ksk Exp $
package	nts.io;

public interface	Log {

    Log		NULL = null;

    Log		add(char ch);
    Log		add(char ch, int count);
    Log		add(String str);
    Log		add(CharCode x);
    Log		endLine();
    Log		startLine();
    Log		flush();
    void	close();
    Log		add(boolean val);
    Log		add(int num);
    Log		addEsc();
    Log		addEsc(String str);
    Log		add(Loggable x);
    Log		add(Loggable[] array);
    Log		add(Loggable[] array, int offset, int length);
    Log		resetCount();
    int		getCount();
    Log		voidCounter();
    Log		sepRoom(int count);

}
