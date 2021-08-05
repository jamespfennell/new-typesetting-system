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
// Filename: nts/base/Dimen.java
// $Id: Dimen.java,v 1.1.1.1 2001/05/17 23:27:20 ksk Exp $
package	nts.base;

/**
 * The fixed-point rational number for representing the \TeX\ dimensions.
 * The instances of class |Dimen| are immutable. It means that each instance
 * represents the same value during its whole life-time and all its methods
 * are free of side-effects. If you need to represent a new value create a new
 * instance. This is done if necessary by all arithmetic operation methods.
 *
 * @author	Karel Skoupy
 * @version	${VERSION}
 * @since	NTS1.0
 */
public final class Dimen	extends BinFraction {

    /** fixed-point position from the right */
    private static final int	POINT_SHIFT = 16;
    /** maximum value of internal representation */
    private static final int	MAX_REPR_VALUE = 0x3fffffff;
    /** maximum |double| value which is allowed to be converted to |Dimen| */
    public static final double	MAX_DOUBLE_VALUE = 20000.0;

    /** Constructs a |Dimen| of certain internal representation value
     * @param	val	the internal representation value
     */
    private Dimen(int val) { super(val); }

    protected final int		pointShift() { return POINT_SHIFT; }

    /** Null constant */
    public static final Dimen	NULL = null;
    /** |Dimen| constant representing 0 */
    public static final Dimen	ZERO = valueOf(0);
    /** |Dimen| constant representing 1 */
    public static final Dimen	UNITY = valueOf(1);
    /** The largest value */
    public static final Dimen	MAX_VALUE = new Dimen(MAX_REPR_VALUE);
    // /** The out-of-range value */
    // public static final Dimen	BAD_VALUE = new Dimen(-MAX_REPR_VALUE - 1);
    /** The largest value which can be converted from |double| */
    public static final Dimen	MAX_FROM_DOUBLE = valueOf(MAX_DOUBLE_VALUE);
    /** The largest value which can be converted from |double| */
    public static final int	REPR_UNITY = 1 << POINT_SHIFT;

    /* STRANGE
     * MAX_FROM_DOUBLE is greater than MAX_VALUE
     */

    /** Provider of |Dimen| value. */
    public interface	Provider {
	/** Provides the value of type |Dimen|
	 * @return the provided |Dimen| value
	 */
	Dimen		getDimenValue();
    }

    /** Output parameter class for the |Dimen| type.
     * Used as a medium for (optional) returning of values of type
     * |Dimen| when more return values are needed.
     */
    public static class	Par {

	/** value of the parameter */
	private Dimen		value;

	/** default constructor (default value of parameter (null)) */
	public Par() { value = Dimen.NULL; }
	
	/** Constructor for initial value of the parameter
	 * @param	val	initial value of the parameter
	 */
	public Par(Dimen val) { value = val; }

	/** Sets a new value of the parameter
	 * @param	val	new value of the parameter
	 */
	public void		set(Dimen val) { value = val; }

	/** Gets the value of the parameter
	 * @return	value of the parameter
	 */
	public Dimen		get() { return value; }

    }

    /** Null constant for the |Dimen.PAR| */
    public static final Par		NULL_PAR = null;

    /** Sets the value of the |Dimen.PAR| if the |Dimen.Par| is given
     * (non-|null|)
     * @param	par	instance of |Par.Dimen| parameter or |null|
     * @param	val	new value of the parameter
     */
    public static void		set(Par par, Dimen val)
	{ if (par != NULL_PAR) par.value = val; }

/* ***	makeRepr()	*** */

    /** Makes the internal representation of given |int|
     * @param	num	integer value to be represented
     * @return	internal representation of |num|
     */
    private static int		makeRepr(int num)
	{ return num << POINT_SHIFT; }

    /** Makes the internal representation of given fraction
     * @param	num	numerator of the fraction to be represented
     * @param	den	denominator of the fraction to be represented
     * @return	internal representation of |num| / |den|
     */
    private static int		makeRepr(int num, int den)
	{ return (int) (((long) num << POINT_SHIFT) / den); }

    /** Makes the internal representation of given |BinFraction|.
     * It is used to convert values of different subclasses of |BinFraction|
     * (with different fixed-point position).
     * @param	x	|BinFraction| to be represented
     * @return	internal representation of |x|
     */
    private static int		makeRepr(BinFraction x)
	{ return makeRepr(x, POINT_SHIFT); }

    /** Makes the internal representation of given floating-point number
     * @param	d	floating-point number to be represented
     * @return	internal representation of |x|
     */
    private static int		makeRepr(double d) {
	d *= REPR_UNITY;
	if (d >= MAX_REPR_VALUE) return MAX_REPR_VALUE;
	if (d <= -MAX_REPR_VALUE) return -MAX_REPR_VALUE;
	return round(d);
	// return (int) Math.round(d);
    }

    /** Rounds a floating-point number to nearest whole number.
     * It uses exactly the same algorithm as web2c implementation of \TeX.
     * @param	d	number to be rounded
     * @return	rounded value
     */
    private static int		round(double d)
	{ return (int) ((d >= 0.0) ? d + 0.5 : d - 0.5); }

/* ***	valueOf()	*** */

    /** Creates representation of a given |Dimen|.
     * @param	d	value to be represented
     * @return	value representing |d| (equal to |d|)
     */
    public static Dimen		valueOf(Dimen d)
	{ return new Dimen(d.value); }

    /** Creates representation of a given integer.
     * @param	num	value to be represented
     * @return	value representing |num|
     */
    public static Dimen		valueOf(int num)
	{ return new Dimen(makeRepr(num)); }

    /** Creates representation of a given fraction.
     * @param	num	numerator of the fraction to be represented
     * @param	den	denominator of the fraction to be represented
     * @return	value representing |num| / |den|
     */
    public static Dimen		valueOf(int num, int den)
	{ return new Dimen(makeRepr(num, den)); }

    /** Creates representation of a given |BinFraction|.
     * @param	x	value to be represented
     * @return	value representing |x|
     */
    public static Dimen		valueOf(BinFraction x)
	{ return new Dimen(makeRepr(x)); }

    /** Creates representation of a given floating-point number.
     * @param	d	value to be represented
     * @return	value representing |d|
     */
    public static Dimen		valueOf(double d)
	{ return new Dimen(makeRepr(d)); }

    /** Creates representation of a given integer after shifting it.
     * @param	num	value to be shifted and represented
     * @param	offs	offset for shifting to the left if positive or
     *			to the right if negative
     * @return	value representing |num| shifted by |offs|
     */
    public static Dimen		shiftedValueOf(int num, int offs) {
	return new Dimen( ((offs += POINT_SHIFT) < 0)
			? num >> -offs : num << offs );
    }

    /** Creates representation of the value expressed by a given string.
     * The string may contain sequence of decimal digits optionally followed
     * by dot and sequence of decimal digits. Both sequences may be empty.
     * The number should be in range which can be represented by |Dimen|.
     * @param	s	string containing the decimal number
     * @return	value representing the number expressed by |s|
     * @throws	NumberFormatException if the format is wrong or if the number
     *		is out-of-range.
     */
    public static Dimen		valueOf(String s)
					throws NumberFormatException {
        int	pointIndex = s.indexOf('.');
	if (pointIndex < 0)
	    return new Dimen(makeRepr(Integer.parseInt(s)));
	else {
	    long	val = Integer.parseInt(s.substring(0, pointIndex));
	    final int	SHIFT = POINT_SHIFT + 1;
	    int		frac = 0;
	    int		i = pointIndex + SHIFT + 1;
	    if (i > s.length()) i = s.length();
	    while (--i > pointIndex) {
	        int	digit = Character.digit(s.charAt(i), 10);
		if (digit < 0)
		    throw new NumberFormatException(s);
	        frac = (frac + (digit << SHIFT)) / 10;
	    }
	    boolean	negative = (val < 0);
	    if (negative) val = -val;
	    val <<= POINT_SHIFT;
	    val |= (frac + 1) >>> 1;
	    if (val > MAX_REPR_VALUE) throw new NumberFormatException(s);
	    return new Dimen((negative) ? (int) -val : (int) val);
	}
    }

/* ***	sign(), isZero()	*** */

    /** The integer value of sign. 
     * @return	1 if the value is greater than 0 <BR>
     *		0 if the value is equal to 0 <BR>
     *		-1 if the value is less that 0
     */
    public int		sign()
	{ return (value > 0) ? 1 : (value < 0) ? -1 : 0; }

    /** Does the value represent 0?
     * @return	|true| if the value is 0, |false| otherwise.
     */
    public boolean	isZero() { return (value == 0); }

/* ***	equals()	*** */

    /** Tests whether the value is equal to a given |Dimen|.
     * @param	d	right hand operand in equality test
     * @return	|true| if the value is equal to |d|, |false| otherwise.
     */
    public boolean	equals(Dimen d) { return (value == d.value); }

    /** Tests whether the value is equal to a given integer.
     * @param	num	right hand operand in equality test
     * @return	|true| if the value is equal to |num|, |false| otherwise.
     */
    public boolean	equals(int num) { return (value == makeRepr(num)); }

    /** Tests whether the value is equal to a given fraction.
     * @param	num	numerator of the right hand operand in equality test
     * @param	den	denominator of the right hand operand in equality test
     * @return	|true| if the value is equal to |num| / |den|, |false| otherwise.
     */
    public boolean	equals(int num, int den)
	{ return (value == makeRepr(num, den)); }

    /** Tests whether the value is equal to a given |BinFraction|.
     * @param	x	right hand operand in equality test
     * @return	|true| if the value is equal to |x|, |false| otherwise.
     */
    public boolean	equals(BinFraction x)
	{ return (value == makeRepr(x)); }

    /** Tests whether the value is equal to a given floating-point number.
     * @param	d	right hand operand in equality test
     * @return	|true| if the value is equal to |d|, |false| otherwise.
     */
    public boolean	equals(double d) { return (value == makeRepr(d)); }

/* ***	lessThan()	*** */

    /** Tests whether the value is less than a given |Dimen|.
     * @param	d	right hand operand in relation test
     * @return	|true| if the value is less than |d|, |false| otherwise.
     */
    public boolean	lessThan(Dimen d) { return (value < d.value); }

    /** Tests whether the value is less than a given integer.
     * @param	num	right hand operand in relation test
     * @return	|true| if the value is less than |num|, |false| otherwise.
     */
    public boolean	lessThan(int num) { return (value < makeRepr(num)); }

    /** Tests whether the value is less than a given fraction.
     * @param	num	numerator of the right hand operand in relation test
     * @param	den	denominator of the right hand operand in relation test
     * @return	|true| if the value is less than |num| / |den|, |false| otherwise.
     */
    public boolean	lessThan(int num, int den)
	{ return (value < makeRepr(num, den)); }

    /** Tests whether the value is less than a given |BinFraction|.
     * @param	x	right hand operand in relation test
     * @return	|true| if the value is less than |x|, |false| otherwise.
     */
    public boolean	lessThan(BinFraction x)
	{ return (value < makeRepr(x)); }

    /** Tests whether the value is less than a given floating-point number.
     * @param	d	right hand operand in relation test
     * @return	|true| if the value is less than |d|, |false| otherwise.
     */
    public boolean	lessThan(double d) { return (value < makeRepr(d)); }

/* ***	moreThan()	*** */

    /** Tests whether the value is greater than a given |Dimen|.
     * @param	d	right hand operand in relation test
     * @return	|true| if the value is greater than |d|, |false| otherwise.
     */
    public boolean	moreThan(Dimen d) { return (value > d.value); }

    /** Tests whether the value is greater than a given integer.
     * @param	num	right hand operand in relation test
     * @return	|true| if the value is greater than |num|, |false| otherwise.
     */
    public boolean	moreThan(int num) { return (value > makeRepr(num)); }

    /** Tests whether the value is greater than a given fraction.
     * @param	num	numerator of the right hand operand in relation test
     * @param	den	denominator of the right hand operand in relation test
     * @return	|true| if the value is greater than |num| / |den|, |false| otherwise.
     */
    public boolean	moreThan(int num, int den)
	{ return (value > makeRepr(num, den)); }

    /** Tests whether the value is greater than a given |BinFraction|.
     * @param	x	right hand operand in relation test
     * @return	|true| if the value is greater than |x|, |false| otherwise.
     */
    public boolean	moreThan(BinFraction x)
	{ return (value > makeRepr(x)); }

    /** Tests whether the value is greater than a given floating-point number.
     * @param	d	right hand operand in relation test
     * @return	|true| if the value is greater than |d|, |false| otherwise.
     */
    public boolean	moreThan(double d) { return (value > makeRepr(d)); }

/* ***	negative(), absolute()	*** */

    /** The negative value.
     * @return	value representing the -value.
     */
    public Dimen	negative() { return new Dimen(-value); }

    /** The absolute value.
     * @return	value if value is greater or equal to 0, -value othervise
     */
    public Dimen	absolute()
	{ return (value < 0) ? new Dimen(-value) : this; }

/* ***	plus()	*** */

    /** Sum of the value and a given |Dimen|
     * @param	d	right hand operand of addition
     * @return	value representing value + |d|
     */
    public Dimen	plus(Dimen d)
	{ return new Dimen(value + d.value); }

    /** Sum of the value and a given integer.
     * @param	num	right hand operand of addition
     * @return	value representing value + |num|
     */
    public Dimen	plus(int num)
	{ return new Dimen(value + makeRepr(num)); }

    /** Sum of the value and a given fraction.
     * @param	num	numerator of the right hand operand of addition
     * @param	den	denominator of the right hand operand of addition
     * @return	value representing value + |num| / |den|
     */
    public Dimen	plus(int num, int den)
	{ return new Dimen(value + makeRepr(num, den)); }

    /** Sum of the value and a given |BinFraction|.
     * @param	x	right hand operand of addition
     * @return	value representing value + |x|
     */
    public Dimen	plus(BinFraction x)
	{ return new Dimen(value + makeRepr(x)); }

    /** Sum of the value and a given floating-point number.
     * @param	d	right hand operand of addition
     * @return	value representing value + |d|
     */
    public Dimen	plus(double d)
	{ return new Dimen(value + makeRepr(d)); }

/* ***	minus()	*** */

    /** Difference of the value and a given |Dimen|
     * @param	d	right hand operand of subtraction
     * @return	value representing value - |d|
     */
    public Dimen	minus(Dimen d)
	{ return new Dimen(value - d.value); }

    /** Difference of the value and a given integer.
     * @param	num	right hand operand of subtraction
     * @return	value representing value - |num|
     */
    public Dimen	minus(int num)
	{ return new Dimen(value - makeRepr(num)); }

    /** Difference of the value and a given fraction.
     * @param	num	numerator of the right hand operand of subtraction
     * @param	den	denominator of the right hand operand of subtraction
     * @return	value representing value - |num| / |den|
     */
    public Dimen	minus(int num, int den)
	{ return new Dimen(value - makeRepr(num, den)); }

    /** Difference of the value and a given |BinFraction|.
     * @param	x	right hand operand of subtraction
     * @return	value representing value - |x|
     */
    public Dimen	minus(BinFraction x)
	{ return new Dimen(value - makeRepr(x)); }

    /** Difference of the value and a given floating-point number.
     * @param	d	right hand operand of subtraction
     * @return	value representing value - |d|
     */
    public Dimen	minus(double d)
	{ return new Dimen(value - makeRepr(d)); }

/* ***	times()	*** */

    /** Product of the value and a given |Dimen|
     * @param	d	right hand operand of multiplication
     * @return	value representing value * |d|
     */
    public Dimen	times(Dimen d)
	{ return new Dimen((int) ((long) value * d.value >> POINT_SHIFT)); }

    /** Product of the value and a given integer.
     * @param	num	right hand operand of multiplication
     * @return	value representing value * |num|
     */
    public Dimen	times(int num)
	{ return new Dimen(value * num); }

    /** Product of the value and a given fraction.
     * @param	num	numerator of the right hand operand of multiplication
     * @param	den	denominator of the right hand operand of multiplication
     * @return	value representing value * |num| / |den|
     */
    public Dimen	times(int num, int den)
	{ return new Dimen((int) (((long) value * num) / den)); }

    /** Product of the value and a given |BinFraction|.
     * @param	x	right hand operand of multiplication
     * @return	value representing value * |x|
     */
    public Dimen	times(BinFraction x)
	{ return new Dimen(reprTimes(x)); }

    /** Product of the value and a given floating-point number.
     * @param	d	right hand operand of multiplication
     * @return	value representing value * |d|
     */
    public Dimen	times(double d)
	{ return new Dimen(round(value * d)); }
	// { return new Dimen((int) Math.round(value * d)); }

    /** Product of the value and a given floating-point number limited to one
     * billion. Used in calculation of glue setting.
     * @param	d	right hand operand of limited multiplication
     * @return	value representing value * |d| if the absolute value of the
     *          product is less than 1000000000, the respective boundary
     *          otherwise.
     */
    /* TeXtp[625, 634] */
    public Dimen	limitedTimes(double d) {
	final double	limit = 1000000000.0;
	double		v = value * d;
	if (v > limit) v = limit;
	else if (v < -limit) v = -limit;
	return new Dimen(round(v));
    }

/* ***	over()	*** */

    /** Quotient of the value and a given |Dimen|
     * @param	d	right hand operand of division
     * @return	value representing value / |d|
     */
    public Dimen	over(Dimen d)
	{ return new Dimen((int) (((long) value << POINT_SHIFT) / d.value)); }

    /** Quotient of the value and a given integer.
     * @param	num	right hand operand of division
     * @return	value representing value / |num|
     */
    public Dimen	over(int num)
	{ return new Dimen(value / num); }

    /** Quotient of the value and a given fraction.
     * @param	num	numerator of the right hand operand of division
     * @param	den	denominator of the right hand operand of division
     * @return	value representing value / |num| / |den|
     */
    public Dimen	over(int num, int den)
	{ return new Dimen((int) (((long) value * den) / num)); }

    /** Quotient of the value and a given |BinFraction|.
     * @param	x	right hand operand of division
     * @return	value representing value / |x|
     */
    public Dimen	over(BinFraction x)
	{ return new Dimen(reprOver(x)); }

    /** Quotient of the value and a given floating-point number.
     * @param	d	right hand operand of division
     * @return	value representing value / |d|
     */
    public Dimen	over(double d)
	{ return new Dimen(round(value / d)); }
	// { return new Dimen((int) Math.round(value / d)); }

    /** Floating-point quotient of the value and a given floating-point number.
     * @param	d	right hand operand of division
     * @return	floating-point value representing value / |d|
     */
    public double	doubleOver(Dimen d)
	{ return ((double) value) / d.value; }

/* ***	divide(), modulo(), shifted()	*** */

    /** Rounded quotient of the value and a given integer.
     * Used in calculation of gap between boxes when outputing |\xleaders|.
     * @param	num	the divisor
     * @return	value representing rounded (value / |num|)
     */
    /* TeXtp[627] */
    public Dimen	roundDivide(int num) {
	return new Dimen((int) ( (2 * (long) value + num)
			       / (2 * (long) num) ));
    }

    /** Whole number quotient of the value and a given |Dimen|.
     * @param	d	the divisor
     * @return	integer representing value |div| |d|
     */
    public int		divide(Dimen d)
	{ return value / d.value; }

    /** Remainder after whole number division of the value and a given |Dimen|.
     * @param	d	the divisor
     * @return	integer representing value |mod| |d|
     */
    public Dimen	modulo(Dimen d)
	{ return new Dimen(value % d.value); }

    /** Bitwise shifting.
     * @param	offs	the offset to shift by
     * @return	value representing the value <BR>
     *		shifted by |offs| to the left if |offs| is larger than 0 <BR>
     *		shifted by |-offs| to the right if |offs| is smaller than 0
     */
    public Dimen	shifted(int offs)
	{ return new Dimen((offs < 0) ? value >> -offs : value << offs); }

/* ***	min(), max()	*** */

    /** Minimum of the value and a given |Dimen|
     * @param	d	the other |Dimen| to choose minimum from
     * @return	smaller from the value and |d|
     */
    public Dimen	min(Dimen d) { return (moreThan(d)) ? d : this; }

    /** Maximum of the value and a given |Dimen|
     * @param	d	the other |Dimen| to choose maximum from
     * @return	larger from the value and |d|
     */
    public Dimen	max(Dimen d) { return (lessThan(d)) ? d : this; }

    /** Half of the value. Result is the same as in \TeX.
     * @return	value / 2
     */
    /* TeXtp[100] */
    public Dimen	halved()	// good
	{ return new Dimen(((value + 1) & ~1) / 2); }

/*
    public Dimen	halved0()	// wrong
	{ return new Dimen(value / 2); }
    public Dimen	halved1()	// wrong
	{ return new Dimen((value + 1) / 2); }
    public Dimen	halved2()	// referential
	{ return new Dimen((((value & 1) != 0) ? value + 1 : value) / 2); }

    static {
	Dimen[]		dims = {
	    new Dimen(0),
	    new Dimen(7), new Dimen(8),
	    new Dimen(-7), new Dimen(-8)
	};
	for (int i = 0; i < dims.length; i++)
	    System.err.println("" + dims[i].value
			    + ":\t" + dims[i].halved().value
			    + ",\t" + dims[i].halved0().value
			    + ",\t" + dims[i].halved1().value
			    + ",\t" + dims[i].halved2().value);
    }
*/

/* ***	to*()	*** */

    /** Conversion to integer.
     * @return	whole number part
     */
    public int		toInt() { return value >> POINT_SHIFT; }

    /** Conversion to integer which corresponds to numerator of a fraction.
     * @param	den	denominator of the fraction represented by the value
     * @return	whole number part of the numerator of the fraction represented
     *		by the value and denominated by |den|
     */
    public int		toInt(int den)
	{ return (int) ((long) value * den >> POINT_SHIFT); }

    /** Conversion to floating-point number.
     * @return	floating-point representation of the fixed-point value
     */
    public double	toDouble() { return (double) value / REPR_UNITY; }

    /** Conversion to string with a dimension unit symbol appended.
     * @param	unit	symbol of the unit of measure
     * @return	string representation of the value followed by |unit|
     */
    public String	toString(String unit) { return toString() + unit; }

    /** Conversion to string.
     * @return	string representation of the value
     */
    public String	toString() {
	StringBuffer		buf = new StringBuffer();
	int			v = value;
	final int		MASK = REPR_UNITY - 1;
	if (v < 0) { buf.append('-'); v = -v; }
	buf.append(v >>> POINT_SHIFT);
	buf.append('.');
	v = 10 * (v & MASK) + 5;
	int	delta = 10;
	do {
	    if (delta > REPR_UNITY) v += REPR_UNITY / 2 - delta / 2;
	    buf.append(Character.forDigit(v >>> POINT_SHIFT, 10));
	    v = 10 * (v & MASK);
	} while (v > (delta *= 10));
	return buf.toString();
    }

/* ***	Object methods	*** */

    /** Hash code for this |Dimen| object.
     * @return	hash code
     */
    public int	hashCode() { return 383 * POINT_SHIFT * value; }

    /** Comparison of this object against another object.
     * @param	o	object to compare to
     * @return	|true| if |o| is a |Dimen| and represents the same value,
     *		|false| otherwise.
     */
    public boolean	equals(Object o) {
	return (  o != null && o instanceof Dimen
	       && ((Dimen) o).value == value  );
    }

/* ***	badness	*** */

    /** Baddness of glue setting which still fits
     * (not more than allowed by stretchability/shrinkability)
     */
    public static final int	UNI_BAD		= 100;
    /** Unacceptable badness of glue setting */
    public static final int	INF_BAD		= 10000;
    /** Badness of glue setting which is highly unacceptable
     * but where a break still might be considered.
     */
    public static final int	DEPLORABLE	= 100000;
    /** Badness of glue setting which exceedes the stretchability */
    public static final int	OVERFULL_BAD	= 1000000;
    /** Badness of glue setting where no break was considered
     * (badness was not even computed). Used for indication.
     */
    public static final int	AWFUL_BAD	= 0x3fffffff;

    /** The badness of glue setting where the value represents the absolute
     * value of the difference from the ideal size and a limit is given.
     * @param	limit	limit (stretchability/shrinkability)
     * @return	the value of badness for value and |limit|
     *		(aprox. $100(value/|limit|)^3$ for normal cases)
     */
    /* TeXtp[108] */
    public int		badness(Dimen limit) {
	int	total = value;
	int	sum = limit.value;
	if (total == 0) return 0;
	else if (total < 0) { total = -total; sum = -sum; }
	if (sum <= 0) return INF_BAD;
	int	ratio;
	if (total <= 7230584)  ratio = total * 297 / sum;
	else if (sum >= 1663497) ratio = total / (sum / 297);
	else return INF_BAD;
	return (ratio > 1290) ? INF_BAD
	     : (ratio * ratio * ratio + 0400000) / 01000000;
    }

    /** String representation of the internal representation
     * (can be thought of scaled points (if the value is in points))
     * @return	string representing the "raw" value
     * @deprecated	only for debuging
     */
    public String	toSp() { return Integer.toString(value); }

}
