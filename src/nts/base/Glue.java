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
// Filename: nts/base/Glue.java
// $Id: Glue.java,v 1.1.1.1 2001/05/17 23:27:43 ksk Exp $
package	nts.base;

import	java.io.Serializable;

/**
 * Dimension with stretchability and shrinkability for representing the \TeX\
 * glues.
 * The instances of class |Dimen| are immutable. It means that each instance
 * represents the same value during its whole life-time and all its methods
 * are free of side-effects. If you need to represent a new value create a new
 * instance. This is done if necessary by all arithmetic operation methods.
 *
 * @author	Karel Skoupy
 * @version	${VERSION}
 * @since	NTS1.0
 */
public class	Glue	implements Serializable {

    /** The normal dimension order. */
    public static final byte		NORMAL		= 0;

    /** The first order of infinity. */
    public static final byte		FIL		= 1;

    /** The second order of infinity. */
    public static final byte		FILL		= 2;

    /** The third order of infinity. */
    public static final byte		FILLL		= 3;

    /** The maximal allowed order of infinity (FILL) */
    public static final byte		MAX_ORDER	= FILLL;

    /** Null constant */
    public static final Glue	NULL = null;

    /** |Glue| constant representing |0pt plus 0pt minus 0pt| */
    public static final Glue		ZERO
	= new Glue(Dimen.ZERO, Dimen.ZERO, NORMAL, Dimen.ZERO, NORMAL);

    /** Provider of |Glue| value. */
    public interface	Provider {
	/** Provides the value of type |Glue|
	 * @return the provided |Glue| value
	 */
	Glue	getGlueValue();
    }

    /** The basic dimension (dimen). */
    private final Dimen		dimen;

    /** The stretch dimension (plus). */
    private final Dimen		stretch;

    /**
     * The order of infinity for stretch part.
     * The value must be between |NORMAL| and |MAX_ORDER|.
     */
    private final byte		strOrder;

    /** The shrink dimension (minus). */
    private final Dimen		shrink;

    /**
     * The order of infinity for shrink part.
     * The value must be between |NORMAL| and |MAX_ORDER|.
     */
    private final byte		shrOrder;

    /**
     * Creates a |Glue| with all parts fully specified.
     * @param	d the basic dimension (dimen).
     * @param	p the stretch dimension (plus).
     * @param	m the shrink dimension (minus).
     * @param	po the order of infinity for stretch part (plus order).
     * @param	po the order of infinity for shrink part (minus order).
     */
    private	Glue(Dimen d, Dimen p, byte po, Dimen m, byte mo) {
    	dimen = d;
	stretch = p; strOrder = po;
	shrink = m; shrOrder = mo;
    }

    /** The basic dimension (dimen).
     * @return	dimension part
     */
    public Dimen	getDimen() { return dimen; }

    /** The stretch dimension (plus).
     * @return	stretch part (plus)
     */
    public Dimen	getStretch() { return stretch; }

    /** The shrink dimension (minus).
     * @return	shrink part (minus)
     */
    public Dimen	getShrink() { return shrink; }

    /** The order of infinity of stretch part.
     * @return	stretch order
     */
    public byte		getStrOrder() { return strOrder; }

    /** The order of infinity of shrink part.
     * @return	shrink order
     */
    public byte		getShrOrder() { return shrOrder; }

    /**
     * Gives the |String| representation of this |Glue| without
     * any specified unit.
     * @return	the |String| representation.
     */
    public String	toString() { return toString(null); }

    /**
     * Gives the |String| representation of this |Glue| with specified
     * dimension unit. The parameter |unit| may by |null| in which case no
     * unit is used.
     * @param	unit symbol of the unit of measure.
     * @return	the |String| representation.
     */
    /* See TeXtp[178]. */
    public String	toString(String unit) {
    	StringBuffer	buf = new StringBuffer(80);
	buf.append(dimen.toString());
	if (unit != null)
	    buf.append(unit);
	if (!stretch.isZero())
	    append(buf.append(" plus "), stretch, strOrder, unit);
	if (!shrink.isZero())
	    append(buf.append(" minus "), shrink, shrOrder, unit);
	return buf.toString();
    }

    /**
     * Appends the representation of stretch or shrink part of glue
     * specification to a |StringBuffer|.
     * @param	buf the buffer to append to.
     * @param	val the dimension of the converted part of glue.
     * @param	ord the correspondig order of infinity.
     * @param	unit the name of unit or |null|.
     * @return	the buffer for subsequent appends.
     */
    /* See TeXtp[177]. */
    public static StringBuffer	append(StringBuffer buf,
				       Dimen val, byte ord, String unit) {
        buf.append(val.toString());
	if (ord < NORMAL || ord > MAX_ORDER)
	    buf.append("foul");
	else if (ord > NORMAL) {
	    buf.append("fil");
	    while (ord-- > FIL)
	        buf.append('l');
	} else if (unit != null)
	    buf.append(unit);
	return buf;
    }

    /**
     * Gives string representation of stretch or shrink part of glue
     * specification.
     * @param	val the dimension of the converted part of glue.
     * @param	ord the correspondig order of infinity.
     * @param	unit the name of unit or |null|.
     * @return	string representation of the stretch or shrink part.
     */
    public static String	toString(Dimen val, byte ord, String unit)
	{ return append(new StringBuffer(30), val, ord, unit).toString(); }

    /**
     * Gives string representation of stretch or shrink part of glue
     * specification.
     * @param	val the dimension of the converted part of glue.
     * @param	ord the correspondig order of infinity.
     * @return	string representation of the stretch or shrink part.
     */
    public static String	toString(Dimen val, byte ord)
	{ return toString(val, ord, null); }

    /**
     * Creates a |Glue| with all parts fully specified.
     * @param	d the basic dimension (dimen).
     * @param	p the stretch dimension (plus).
     * @param	m the shrink dimension (minus).
     * @param	po the order of infinity for stretch part (plus order).
     * @param	po the order of infinity for shrink part (minus order).
     */
    public static Glue	valueOf(Dimen d, Dimen p, byte po, Dimen m, byte mo) {
	if (p.isZero()) po = NORMAL;
	if (m.isZero()) mo = NORMAL;
	return new Glue(d, p, po, m, mo);
    }

    /**
     * Creates a |Glue| with finite stretch and shrink parts.
     * @param	d the basic dimension (dimen).
     * @param	p the stretch dimension (plus).
     * @param	m the shrink dimension (minus).
     */
    public static Glue	valueOf(Dimen d, Dimen p, Dimen m)
	{ return new Glue(d, p, NORMAL, m, NORMAL); }

    /**
     * Creates a fixed |Glue| of given basic dimension.
     * It cannot stretch nor shrink.
     * @param	d the basic dimension (dimen).
     */
    public static Glue	valueOf(Dimen d)
	{ return new Glue(d, Dimen.ZERO, NORMAL, Dimen.ZERO, NORMAL); }

    /** Does the value represent |0pt plus 0pt minus 0pt|?
     * @return	|true| if the value is zero glue, |false| otherwise.
     */
    public boolean	isZero()
	{ return (dimen.isZero() && stretch.isZero() && shrink.isZero()); }

    /** The negative value.
     * @return	value representing the -value.
     */
    public Glue		negative() {
        return valueOf(dimen.negative(), stretch.negative(), strOrder,
					 shrink.negative(), shrOrder);
    }

/* ***	plus()	*** */

    /** Adds two dimensions according their respective orders.
     * If the orders are the same then the result is the sum of the dimensions
     * and their common order. Otherwise the dimension with higher order and
     * that order are returnned.
     * @param	x	first dimension
     * @param	y	second dimension
     * @param	xo	first dimension order
     * @param	yo	second dimension order and the resulting order
     * @return	value representing |x| + |y| respecting the orders; the
     *		resulting order is returned in |yo|
     */
    /* TeXtp[1239] */
    private static Dimen	addParts(Dimen x, Dimen y,
					 byte xo, BytePar yo) {
        if (x.isZero()) xo = NORMAL;
	if (xo == yo.get()) return x.plus(y);
	if (xo < yo.get() && !y.isZero()) return y;
	yo.set(xo); return x;
    }

    /** Sum of this |Glue| and another given |Glue|.
     * The dimension part are simply added. The stretch resp. the shrink
     * parts are added if the respective orders are the same
     * otherwise the one with the higher order is used.
     * @param	g	right hand operand of addition
     * @return	value representing value + |g|
     */
    /* TeXtp[1239] */
    public Glue		plus(Glue g) {
        BytePar		strO = new BytePar(g.strOrder);
        BytePar		shrO = new BytePar(g.shrOrder);
	Dimen		str = addParts(stretch, g.stretch, strOrder, strO);
	Dimen		shr = addParts(shrink, g.shrink, shrOrder, shrO);
	return valueOf(dimen.plus(g.dimen), str, strO.get(),
					    shr, shrO.get());
    }

    /** Sum of the value and a given |Dimen|. The dimensions
     * are added and the stretch/shrink parts of the glue are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value + |d|
     */
    public Glue		plus(Dimen d) {
        return valueOf(dimen.plus(d), stretch, strOrder,
				      shrink, shrOrder);
    }

    /** Sum of the value and a given integer. The dimensions
     * are added and the stretch/shrink parts of the glue are copied.
     * @param	num	right hand operand of addition
     * @return	value representing value + |num|
     */
    public Glue		plus(int num) {
        return valueOf(dimen.plus(num), stretch, strOrder,
					shrink, shrOrder);
    }

    /** Sum of the value and a given fraction. The dimensions
     * are added and the stretch/shrink parts of the glue are copied.
     * @param	num	numerator of the right hand operand of addition
     * @param	den	denominator of the right hand operand of addition
     * @return	value representing value + |num| / |den|
     */
    public Glue		plus(int num, int den) {
        return valueOf(dimen.plus(num, den), stretch, strOrder,
					     shrink, shrOrder);
    }

    /** Sum of the value and a given |BinFraction|. The dimensions
     * are added and the stretch/shrink parts of the glue are copied.
     * @param	x	right hand operand of addition
     * @return	value representing value + |x|
     */
    public Glue		plus(BinFraction x) {
        return valueOf(dimen.plus(x), stretch, strOrder,
				      shrink, shrOrder);
    }

    /** Sum of the value and a given floating-point number. The dimensions
     * are added and the stretch/shrink parts of the glue are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value + |d|
     */
    public Glue		plus(double d) {
        return valueOf(dimen.plus(d), stretch, strOrder,
				      shrink, shrOrder);
    }

/* ***	minus()	*** */

    /** Difference of this |Glue| and another given |Glue|.
     * The dimension part are simply substracted. The stretch resp. the shrink
     * parts are substracted if the respective orders are the same
     * otherwise the one with the higher order is used.
     * @param	g	right hand operand of addition
     * @return	value representing value - |g|
     */
    public Glue		minus(Glue g) { return plus(g.negative()); }

    /** Difference of the value and a given |Dimen|. The dimensions
     * are subtracted and the stretch/shrink parts of the glue are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value - |d|
     */
    public Glue		minus(Dimen d) {
        return valueOf(dimen.minus(d), stretch, strOrder,
				       shrink, shrOrder);
    }

    /** Difference of the value and a given integer. The dimensions
     * are subtracted and the stretch/shrink parts of the glue are copied.
     * @param	num	right hand operand of addition
     * @return	value representing value - |num|
     */
    public Glue		minus(int num) {
        return valueOf(dimen.minus(num), stretch, strOrder,
					 shrink, shrOrder);
    }

    /** Difference of the value and a given fraction. The dimensions
     * are subtracted and the stretch/shrink parts of the glue are copied.
     * @param	num	numerator of the right hand operand of addition
     * @param	den	denominator of the right hand operand of addition
     * @return	value representing value - |num| / |den|
     */
    public Glue		minus(int num, int den) {
        return valueOf(dimen.minus(num, den), stretch, strOrder,
					      shrink, shrOrder);
    }

    /** Difference of the value and a given |BinFraction|. The dimensions
     * are subtracted and the stretch/shrink parts of the glue are copied.
     * @param	x	right hand operand of addition
     * @return	value representing value - |x|
     */
    public Glue		minus(BinFraction x) {
        return valueOf(dimen.minus(x), stretch, strOrder,
				       shrink, shrOrder);
    }

    /** Difference of the value and a given floating-point number.
     * The dimensions are subtracted and the stretch/shrink parts of the glue
     * are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value - |d|
     */
    public Glue		minus(double d) {
        return valueOf(dimen.minus(d), stretch, strOrder,
				       shrink, shrOrder);
    }

/* ***	times()	*** */

    /** Product of the value and a given |Dimen|. The dimension and the
     * stretch/shrink part are multiplied, the orders are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value * |d|
     */
    public Glue		times(Dimen d) {
        return valueOf(dimen.times(d), stretch.times(d), strOrder,
				       shrink.times(d), shrOrder);
    }

    /** Product of the value and a given integer. The dimension and the
     * stretch/shrink part are multiplied, the orders are copied.
     * @param	num	right hand operand of addition
     * @return	value representing value * |num|
     */
    public Glue		times(int num) {
        return valueOf(dimen.times(num), stretch.times(num), strOrder,
					 shrink.times(num), shrOrder);
    }

    /** Product of the value and a given fraction. The dimension and the
     * stretch/shrink part are multiplied, the orders are copied.
     * @param	num	numerator of the right hand operand of addition
     * @param	den	denominator of the right hand operand of addition
     * @return	value representing value * |num| / |den|
     */
    public Glue		times(int num, int den) {
        return valueOf(dimen.times(num, den),
		       stretch.times(num, den), strOrder,
		       shrink.times(num, den), shrOrder);
    }

    /** Product of the value and a given |BinFraction|. The dimension and the
     * stretch/shrink part are multiplied, the orders are copied.
     * @param	x	right hand operand of addition
     * @return	value representing value * |x|
     */
    public Glue		times(BinFraction x) {
        return valueOf(dimen.times(x), stretch.times(x), strOrder,
				       shrink.times(x), shrOrder);
    }

    /** Product of the value and a given floating-point number.
     * The dimension and the stretch/shrink part are multiplied, the orders
     * are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value * |d|
     */
    public Glue		times(double d) {
        return valueOf(dimen.times(d), stretch.times(d), strOrder,
				       shrink.times(d), shrOrder);
    }

    /** Product of the value and a given floating-point number which lefts
     * infinite stretch/shrink intact. The dimensions are multiplied, the
     * stretch/shrink parts are multiplied only if the respective order is
     * finite (|NORMAL|), otherwise copied, the orders are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value * |d|
     *			(not affecting infinite stretch/shrink)
     */
    /* TeXtp[716] */
    public Glue		timesTheFinite(Dimen d) {
        return valueOf(dimen.times(d),
	    (strOrder == NORMAL) ? stretch.times(d) : stretch, strOrder,
	    (shrOrder == NORMAL) ? shrink.times(d) : shrink, shrOrder);
    }

/* ***	over()	*** */

    /** Quotient of the value and a given |Dimen|. The dimension and the
     * stretch/shrink part are divided, the orders are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value / |d|
     */
    public Glue		over(Dimen d) {
        return valueOf(dimen.over(d), stretch.over(d), strOrder,
				      shrink.over(d), shrOrder);
    }

    /** Quotient of the value and a given integer. The dimension and the
     * stretch/shrink part are divided, the orders are copied.
     * @param	num	right hand operand of addition
     * @return	value representing value / |num|
     */
    public Glue		over(int num) {
        return valueOf(dimen.over(num), stretch.over(num), strOrder,
					shrink.over(num), shrOrder);
    }

    /** Quotient of the value and a given fraction. The dimension and the
     * stretch/shrink part are divided, the orders are copied.
     * @param	num	numerator of the right hand operand of addition
     * @param	den	denominator of the right hand operand of addition
     * @return	value representing value / |num| / |den|
     */
    public Glue		over(int num, int den) {
        return valueOf(dimen.over(num, den),
		       stretch.over(num, den), strOrder,
		       shrink.over(num, den), shrOrder);
    }

    /** Quotient of the value and a given |BinFraction|. The dimension and the
     * stretch/shrink part are divided, the orders are copied.
     * @param	x	right hand operand of addition
     * @return	value representing value / |x|
     */
    public Glue		over(BinFraction x) {
        return valueOf(dimen.over(x), stretch.over(x), strOrder,
				      shrink.over(x), shrOrder);
    }

    /** Quotient of the value and a given floating-point number.
     * The dimension and the stretch/shrink part are divided, the orders
     * are copied.
     * @param	d	right hand operand of addition
     * @return	value representing value / |d|
     */
    public Glue		over(double d) {
        return valueOf(dimen.over(d), stretch.over(d), strOrder,
				      shrink.over(d), shrOrder);
    }

/* ***	variants	*** */

    /** Variant of the glue with given dimension but the same
     * stretch/shrink parts and orders.
     * @param	d	new dimension
     * @return	copy with dimension changed to |d|
     */
    public Glue		resizedCopy(Dimen d)
	{ return valueOf(d, stretch, strOrder, shrink, shrOrder); }

    /** Variant of the glue with finite shrinkage.
     * Used for corrections when an infinite shrinkage is unacceptable.
     * @return	copy with shrink order changed to |NORMAL| if it was not
     *		|NORMAL|, the same glue otherwise.
     */
    public Glue		withFiniteShrink() {
	return (shrOrder == NORMAL) ? this
	     : valueOf(dimen, stretch, strOrder, shrink, NORMAL);
    }

}
