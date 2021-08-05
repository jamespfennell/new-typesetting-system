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
// Filename: nts/tex/TeXConfig.java
// $Id: TeXConfig.java,v 1.1.1.1 2001/09/13 02:25:28 ksk Exp $
package	nts.tex;

public class	TeXConfig {

    public static final String		BANNER
        = "This is NTS, Version 1.00-beta";

    public static final int		ERROR_LINE	= 79;
    public static final int		HALF_ERROR_LINE	= 50;
    public static final int		MAX_PRINT_LINE	= 79;
    public static final int		MAX_ERROR_COUNT	= 100;
    public static final int		MAX_TOK_LIST_RT	= 32;
    public static final int		MAX_RUNAWAY_WD	= ERROR_LINE - 10;
    public static final int		MAX_MARK_WD	= MAX_PRINT_LINE - 10;

    public static final char		MAX_TEX_CHAR	= 255;

    //XXX == HorizBuilder.NORMAL_SPACE_FACTOR
    public static final char	NORMAL_SPACE_FACTOR	= 1000;
    public static final char	UPCASE_SPACE_FACTOR	= 999;
    public static final char	MAX_SPACE_FACTOR	= 0x7fff;

    public static final String		LOG_EXT		= "log";
    public static final String		INPUT_EXT	= "tex";
    public static final String		READ_EXT	= "tex";
    public static final String		WRITE_EXT	= "tex";
    public static final String		TFM_EXT		= "tfm";
    public static final String		DVI_EXT		= "dvi";
    public static final String		FMT_EXT		= "nfmt";

    public static final String		EDIT_TEMPLATE	= "vi +%d %s";

    /** start of control sequence ('\\') */
    public static final int		CAT_ESCAPE		= 0;
    /** beginning of a group ('{') */
    public static final int		CAT_LEFT_BRACE		= 1;
    /** ending of a group ('}') */
    public static final int		CAT_RIGHT_BRACE		= 2;
    /** mathematics shift character ('$') */
    public static final int		CAT_MATH_SHIFT		= 3;
    /** align delimiter ('&') */
    public static final int		CAT_TAB_MARK		= 4;
    /** end of line ('\r') */
    public static final int		CAT_CAR_RET		= 5;
    /** macro parameter symbol ('#') */
    public static final int		CAT_MAC_PARAM		= 6;
    /** superscript ('^') */
    public static final int		CAT_SUP_MARK		= 7;
    /** subscript ('_') */
    public static final int		CAT_SUB_MARK		= 8;
    /** characters to ignore */
    public static final int		CAT_IGNORE		= 9;
    /** characters equivalent to blank space */
    public static final int		CAT_SPACER		= 10;
    /** characters regarded as letters ('A' .. 'Z', 'a' .. 'z') */
    public static final int		CAT_LETTER		= 11;
    /** none of the special character types */
    public static final int		CAT_OTHER_CHAR		= 12;
    /** characters that invoke macros */
    public static final int		CAT_ACTIVE_CHAR		= 13;
    /** characters that introduce comments ('%') */
    public static final int		CAT_COMMENT		= 14;
    /** characters that shouldn't appear ('\177') */
    public static final int		CAT_INVALID_CHAR	= 15;
    public static final int		MAX_CATEGORY		= 15;

}
