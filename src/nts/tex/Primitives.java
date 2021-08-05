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
// Filename: nts/tex/Primitives.java
// $Id: Primitives.java,v 1.1.1.1 2001/03/22 15:56:38 ksk Exp $
package nts.tex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import nts.align.*;
import nts.base.*;
import nts.builder.*;
import nts.command.*;
import nts.hyph.*;
import nts.io.CharCode;
import nts.io.Name;
import nts.math.*;
import nts.noad.*;
import nts.node.*;
import nts.typo.*;

public class Primitives
    implements Serializable,
        Command.Config,
        TypoCommand.Config,
        MathPrim.Config,
        TeXCharMapper.Config,
        TeXIOHandler.Config,
        TeXFontHandler.Config {

  private static final boolean debuging = false;

  private void debugMessage(String str) {
    if (debuging) System.err.println(str);
  }

  public static final int NUMBER_OF_LANGUAGES = 256;
  // XXX also other constants from TeXConfig

  private final LevelEqTable eqTab = new LevelEqTable();
  private final EqTable.ObjKind nameKind =
      new Command.TokKind() {
        protected Token getToken(Object key) {
          return new CtrlSeqToken((Name) key);
        }
      };
  private final EqTable.ObjKind codeKind =
      new Command.TokKind() {
        protected Token getToken(Object key) {
          return new ActiveCharToken((CharCode) key);
        }
      };

  protected transient Calendar now;

  private void init() {
    debugMessage("Primitives.init()");

    now = Calendar.getInstance();
    Command.setEqt(eqTab);
    NormalCharToken.setHandler(
        new Token.CharHandler() {
          public void handle(CharCode code, Token src) {
            BuilderCommand.handleChar(code, src);
          }
        });
    SpaceToken.setHandler(
        new Token.CharHandler() {
          public void handle(CharCode code, Token src) {
            BuilderCommand.handleSpace(src);
          }
        });
    CtrlSeqToken.setMeaninger(
        new CtrlSeqToken.Meaninger() {

          public Command get(Name name) {
            return (Command) eqTab.get(nameKind, name);
          }

          public void set(Name name, Command cmd, boolean glob) {
            if (glob) eqTab.gput(nameKind, name, cmd);
            else eqTab.put(nameKind, name, cmd);
          }
        });
    ActiveCharToken.setMeaninger(
        new ActiveCharToken.Meaninger() {

          public Command get(CharCode code) {
            return (Command) eqTab.get(codeKind, code);
          }

          public void set(CharCode code, Command cmd, boolean glob) {
            if (glob) eqTab.gput(codeKind, code, cmd);
            else eqTab.put(codeKind, code, cmd);
          }
        });
  }

  {
    init();
  }

  private void writeObject(ObjectOutputStream output) throws IOException {
    debugMessage("Primitives.writeObject()");
    output.writeInt(InteractionPrim.get());
    Relax.writeStaticData(output);
    Expandable.writeStaticData(output);
    Undefined.writeStaticData(output);
    TypoCommand.writeStaticData(output);
    BuilderCommand.writeStaticData(output);
    Page.writeStaticData(output);
    output.defaultWriteObject();
  }

  private transient boolean loaded = false;

  private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
    debugMessage("Primitives.readObject()");
    Class align_class = Alignment.class;
    // XXX maybe it would not be necessary if ColumnEnding is not inner
    InteractionPrim.set(input.readInt());
    Relax.readStaticData(input);
    Expandable.readStaticData(input);
    Undefined.readStaticData(input);
    TypoCommand.readStaticData(input);
    BuilderCommand.readStaticData(input);
    Page.readStaticData(input);
    input.defaultReadObject();
    init();
    initTime();
    initMath();
    initAlign();
    initGroups();
    initParams();
    initHyphenation();
    initMathSpacing();
    loaded = true;
  }

  protected void def(String name, Command cmd) {
    eqTab.gput(nameKind, Token.makeName(name), cmd);
  }

  protected void def(Primitive prim) {
    def(prim.getName(), prim.getCommand());
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Mode independent primitives
   */

  private final ExpandablePrim prim_fi = new EndCondPrim("fi", EndCondPrim.FI);

  private final InputPrim input = new InputPrim("input");
  private final CountPrim count = new CountPrim("count");
  private final DimenPrim dimen = new DimenPrim("dimen");
  private final SkipPrim skip = new SkipPrim("skip");
  private final MuSkipPrim muskip = new MuSkipPrim("muskip");
  private final ToksPrim toks = new ToksPrim("toks");

  {
    debugMessage("Primitives.<init(1)>");

    Relax.makeStaticData();
    Expandable.makeStaticData();
    Undefined.makeStaticData();

    def(Relax.NAME, Relax.getRelax());

    def(new ExpandAfterPrim("expandafter"));
    def(new NoExpandPrim("noexpand"));
    def(new CsNamePrim("csname"));
    def(new EndCsNamePrim("endcsname"));
    def(new NumberPrim("number"));
    def(new RomanNumeralPrim("romannumeral"));
    def(new StringPrim("string"));
    def(new MeaningPrim("meaning"));
    def(new JobNamePrim("jobname"));
    def(new ThePrim("the"));
    def(input);
    def(new EndInputPrim("endinput"));

    def(new IfNumPrim("ifnum"));
    def(new IfDimPrim("ifdim"));
    def(new IfOddPrim("ifodd"));
    def(new IfPrim("if"));
    def(new IfCatPrim("ifcat"));
    def(new IfXPrim("ifx"));
    def(new IfBoolPrim("iftrue", true));
    def(new IfBoolPrim("iffalse", false));
    def(new IfCasePrim("ifcase"));
    def(new EndCondPrim("else", EndCondPrim.ELSE));
    def(new EndCondPrim("or", EndCondPrim.OR));
    def(prim_fi);

    def(new IgnoreSpacesPrim("ignorespaces"));
    def(new LowerCasePrim("lowercase"));
    def(new UpperCasePrim("uppercase"));
    def(new MessagePrim("message"));
    def(new ErrMessagePrim("errmessage"));
    def(new ShowPrim("show"));
    def(new ShowThePrim("showthe"));
    def(new InputLineNoPrim("inputlineno"));
    def(new ImmediatePrim("immediate"));

    def(new Prefix("long", Prefix.LONG));
    def(new Prefix("outer", Prefix.OUTER));
    def(new Prefix("global", Prefix.GLOBAL));

    def(new LetPrim("let"));
    def(new FutureLetPrim("futurelet"));
    def(new DefPrim("def", false, 0));
    def(new DefPrim("edef", true, 0));
    def(new DefPrim("gdef", false, PrefixPrim.GLOBAL));
    def(new DefPrim("xdef", true, PrefixPrim.GLOBAL));

    def(count);
    def(dimen);
    def(skip);
    def(muskip);
    def(toks);

    def(new CountDefPrim("countdef", count));
    def(new DimenDefPrim("dimendef", dimen));
    def(new SkipDefPrim("skipdef", skip));
    def(new MuSkipDefPrim("muskipdef", muskip));
    def(new ToksDefPrim("toksdef", toks));

    def(new OperatorPrim("advance", OperatorPrim.ADVANCE));
    def(new OperatorPrim("multiply", OperatorPrim.MULTIPLY));
    def(new OperatorPrim("divide", OperatorPrim.DIVIDE));

    for (int i = 0; i < InteractionPrim.names.length; i++)
      def(new InteractionPrim(InteractionPrim.names[i], i));

    ReadPrim read = new ReadPrim("read");

    def(read);
    def(new OpenInPrim("openin", read));
    def(new CloseInPrim("closein", read));
    def(new IfEofPrim("ifeof", read));
  }

  private final NumParam pretolerance = new NumParam("pretolerance"),
      tolerance = new NumParam("tolerance", 10000),
      line_penalty = new NumParam("linepenalty"),
      hyphen_penalty = new NumParam("hyphenpenalty"),
      ex_hyphen_penalty = new NumParam("exhyphenpenalty"),
      club_penalty = new NumParam("clubpenalty"),
      widow_penalty = new NumParam("widowpenalty"),
      display_widow_penalty = new NumParam("displaywidowpenalty"),
      broken_penalty = new NumParam("brokenpenalty"),
      bin_op_penalty = new NumParam("binoppenalty"),
      rel_penalty = new NumParam("relpenalty"),
      pre_display_penalty = new NumParam("predisplaypenalty"),
      post_display_penalty = new NumParam("postdisplaypenalty"),
      inter_line_penalty = new NumParam("interlinepenalty"),
      double_hyphen_demerits = new NumParam("doublehyphendemerits"),
      final_hyphen_demerits = new NumParam("finalhyphendemerits"),
      adj_demerits = new NumParam("adjdemerits"),
      mag = new MagParam("mag", 1000),
      delimiter_factor = new NumParam("delimiterfactor"),
      looseness = new NumParam("looseness"),
      show_box_breadth = new NumParam("showboxbreadth"),
      show_box_depth = new NumParam("showboxdepth"),
      hbadness = new NumParam("hbadness"),
      vbadness = new NumParam("vbadness"),
      pausing = new NumParam("pausing"),
      tracing_online = new TracingOnlineParam("tracingonline"),
      tracing_macros = new NumParam("tracingmacros"),
      tracing_stats = new NumParam("tracingstats"),
      tracing_paragraphs = new NumParam("tracingparagraphs"),
      tracing_pages = new NumParam("tracingpages"),
      tracing_output = new NumParam("tracingoutput"),
      tracing_lost_chars = new NumParam("tracinglostchars"),
      tracing_commands = new NumParam("tracingcommands"),
      tracing_restores = new NumParam("tracingrestores"),
      uc_hyph = new NumParam("uchyph"),
      output_penalty = new NumParam("outputpenalty"),
      max_dead_cycles = new NumParam("maxdeadcycles", 25),
      hang_after = new NumParam("hangafter", 1),
      floating_penalty = new NumParam("floatingpenalty"),
      global_defs = new NumParam("globaldefs"),
      cur_fam = new NumParam("fam"),
      escape_char = new NumParam("escapechar", '\\'),
      default_hyphen_char = new NumParam("defaulthyphenchar"),
      default_skew_char = new NumParam("defaultskewchar"),
      end_line_char = new NumParam("endlinechar", '\r'),
      new_line_char = new NumParam("newlinechar"),
      language = new NumParam("language"),
      left_hyphen_min = new NumParam("lefthyphenmin"),
      right_hyphen_min = new NumParam("righthyphenmin"),
      holding_inserts = new NumParam("holdinginserts"),
      error_context_lines = new NumParam("errorcontextlines");
  private final DimenParam par_indent = new DimenParam("parindent"),
      math_surround = new DimenParam("mathsurround"),
      line_skip_limit = new DimenParam("lineskiplimit"),
      hsize = new DimenParam("hsize"),
      vsize = new DimenParam("vsize"),
      max_depth = new DimenParam("maxdepth"),
      split_max_depth = new DimenParam("splitmaxdepth"),
      box_max_depth = new DimenParam("boxmaxdepth"),
      hfuzz = new DimenParam("hfuzz"),
      vfuzz = new DimenParam("vfuzz"),
      delimiter_shortfall = new DimenParam("delimitershortfall"),
      null_delimiter_space = new DimenParam("nulldelimiterspace"),
      script_space = new DimenParam("scriptspace"),
      pre_display_size = new DimenParam("predisplaysize"),
      display_width = new DimenParam("displaywidth"),
      display_indent = new DimenParam("displayindent"),
      overfull_rule = new DimenParam("overfullrule"),
      hang_indent = new DimenParam("hangindent"),
      h_offset = new DimenParam("hoffset"),
      v_offset = new DimenParam("voffset"),
      emergency_stretch = new DimenParam("emergencystretch");
  private final GlueParam line_skip = new GlueParam("lineskip"),
      baseline_skip = new GlueParam("baselineskip"),
      par_skip = new GlueParam("parskip"),
      above_display_skip = new GlueParam("abovedisplayskip"),
      below_display_skip = new GlueParam("belowdisplayskip"),
      above_display_short_skip = new GlueParam("abovedisplayshortskip"),
      below_display_short_skip = new GlueParam("belowdisplayshortskip"),
      left_skip = new GlueParam("leftskip"),
      right_skip = new GlueParam("rightskip"),
      top_skip = new GlueParam("topskip"),
      split_top_skip = new GlueParam("splittopskip"),
      tab_skip = new TabSkipParam("tabskip"),
      space_skip = new GlueParam("spaceskip"),
      xspace_skip = new GlueParam("xspaceskip"),
      par_fill_skip = new GlueParam("parfillskip");
  private final MuGlueParam thin_mu_skip = new MuGlueParam("thinmuskip"),
      med_mu_skip = new MuGlueParam("medmuskip"),
      thick_mu_skip = new MuGlueParam("thickmuskip");
  private final ToksParam output_routine = new OutputParam("output"),
      every_par = new ToksParam("everypar"),
      every_math = new ToksParam("everymath"),
      every_display = new ToksParam("everydisplay"),
      every_hbox = new ToksParam("everyhbox"),
      every_vbox = new ToksParam("everyvbox"),
      every_job = new ToksParam("everyjob"),
      every_cr = new ToksParam("everycr"),
      err_help = new ToksParam("errhelp");
  private transient NumParam time, day, month, year;

  private void initTime() {
    time = new NumParam("time", now.get(Calendar.MINUTE) + 60 * now.get(Calendar.HOUR_OF_DAY));
    day = new NumParam("day", now.get(Calendar.DAY_OF_MONTH));
    month = new NumParam("month", now.get(Calendar.MONTH) + 1);
    year = new NumParam("year", now.get(Calendar.YEAR));
    def(time);
    def(day);
    def(month);
    def(year);
  }

  {
    initTime();
  }

  private final ParShapeParam par_shape = new ParShapeParam("parshape");

  private final AfterAssignment after_assignment = new AfterAssignment("afterassignment");

  {
    debugMessage("Primitives.<init(2)>");

    def(pretolerance);
    def(tolerance);
    def(line_penalty);
    def(hyphen_penalty);
    def(ex_hyphen_penalty);
    def(club_penalty);
    def(widow_penalty);
    def(display_widow_penalty);
    def(broken_penalty);
    def(bin_op_penalty);
    def(rel_penalty);
    def(pre_display_penalty);
    def(post_display_penalty);
    def(inter_line_penalty);
    def(double_hyphen_demerits);
    def(final_hyphen_demerits);
    def(adj_demerits);
    def(mag);
    def(delimiter_factor);
    def(looseness);
    def(show_box_breadth);
    def(show_box_depth);
    def(hbadness);
    def(vbadness);
    def(pausing);
    def(tracing_online);
    def(tracing_macros);
    def(tracing_stats);
    def(tracing_paragraphs);
    def(tracing_pages);
    def(tracing_output);
    def(tracing_lost_chars);
    def(tracing_commands);
    def(tracing_restores);
    def(uc_hyph);
    def(output_penalty);
    def(max_dead_cycles);
    def(hang_after);
    def(floating_penalty);
    def(global_defs);
    def(cur_fam);
    def(escape_char);
    def(default_hyphen_char);
    def(default_skew_char);
    def(end_line_char);
    def(new_line_char);
    def(language);
    def(left_hyphen_min);
    def(right_hyphen_min);
    def(holding_inserts);
    def(error_context_lines);

    def(par_indent);
    def(math_surround);
    def(line_skip_limit);
    def(hsize);
    def(vsize);
    def(max_depth);
    def(split_max_depth);
    def(box_max_depth);
    def(hfuzz);
    def(vfuzz);
    def(delimiter_shortfall);
    def(null_delimiter_space);
    def(script_space);
    def(pre_display_size);
    def(display_width);
    def(display_indent);
    def(overfull_rule);
    def(hang_indent);
    def(h_offset);
    def(v_offset);
    def(emergency_stretch);

    def(line_skip);
    def(baseline_skip);
    def(par_skip);
    def(above_display_skip);
    def(below_display_skip);
    def(above_display_short_skip);
    def(below_display_short_skip);
    def(left_skip);
    def(right_skip);
    def(top_skip);
    def(split_top_skip);
    def(tab_skip);
    def(space_skip);
    def(xspace_skip);
    def(par_fill_skip);

    def(thin_mu_skip);
    def(med_mu_skip);
    def(thick_mu_skip);

    def(output_routine);
    def(every_par);
    def(every_math);
    def(every_display);
    def(every_hbox);
    def(every_vbox);
    def(every_job);
    def(every_cr);
    def(err_help);

    def(par_shape);

    def(after_assignment);
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Typesetting  primitives
   */

  private final SimpleNumPrim badness = new SimpleNumPrim("badness");
  private final SetBoxPrim setbox = new SetBoxPrim("setbox", "box");

  {
    debugMessage("Primitives.<init(3)>");

    TypoCommand.makeStaticData();
    BuilderCommand.makeStaticData();

    // Mode independent

    def(badness);
    def("nullfont", NullFontMetric.COMMAND);
    def(new FontPrim("font"));
    def(new FontDimenPrim("fontdimen"));
    def(new FontNumPrim("hyphenchar", FontMetric.NUM_PARAM_HYPHEN_CHAR));
    def(new FontNumPrim("skewchar", FontMetric.NUM_PARAM_SKEW_CHAR));
    def(new FontNamePrim("fontname"));
    def(new ShipOutPrim("shipout"));
    def(new PatternsPrim("patterns"));
    def(new HyphenationPrim("hyphenation"));

    WritePrim write = new WritePrim("write");

    def(write);
    def(new OpenOutPrim("openout", write));
    def(new CloseOutPrim("closeout", write));
    def(new SpecialPrim("special"));

    def(setbox);
    def(new IfVoidPrim("ifvoid", setbox));
    def(new IfHBoxPrim("ifhbox", setbox));
    def(new IfVBoxPrim("ifvbox", setbox));
    def(new HtPrim("ht", setbox));
    def(new WdPrim("wd", setbox));
    def(new DpPrim("dp", setbox));
    def(new ShowBoxPrim("showbox", setbox));

    // typesetting

    BuilderCommand.defineCharHandler(VertBuilder.class, new TypoCommand.VertCharHandler());
    BuilderCommand.defineCharHandler(HorizBuilder.class, new TypoCommand.HorizCharHandler());
    BuilderCommand.defineCharHandler(MathBuilder.class, new MathPrim.MathCharHandler());

    Dimen default_rule = Command.makeDimen(26214, "sp");

    RulePrim hrule = new RulePrim("hrule", default_rule, Dimen.NULL, Dimen.ZERO, Dimen.ZERO);

    RulePrim vrule = new RulePrim("vrule", Dimen.NULL, default_rule, Dimen.NULL, Dimen.ZERO);

    def(hrule);
    hrule.defineAction(VertBuilder.class, hrule.NORMAL);
    hrule.defineAction(ParBuilder.class, hrule.FINISH_PAR);
    hrule.defineAction(HBoxBuilder.class, hrule.BAD_HRULE);
    hrule.defineAction(MathBuilder.class, MathPrim.DOLLAR);

    def(vrule);
    vrule.defineAction(VertBuilder.class, vrule.START_PAR);
    vrule.defineAction(HorizBuilder.class, vrule.NORMAL);
    vrule.defineAction(MathBuilder.class, vrule.NORMAL);
  }

  private void defVSkip(AnySkipPrim skip) {
    def(skip);
    skip.defineAction(VertBuilder.class, skip.NORMAL);
    skip.defineAction(ParBuilder.class, skip.FINISH_PAR);
    skip.defineAction(HBoxBuilder.class, skip.REJECT);
    skip.defineAction(MathBuilder.class, MathPrim.DOLLAR);
  }

  private void defHSkip(AnySkipPrim skip) {
    def(skip);
    skip.defineAction(VertBuilder.class, skip.START_PAR);
    skip.defineAction(HorizBuilder.class, skip.NORMAL);
    skip.defineAction(MathBuilder.class, skip.NORMAL);
  }

  private void defUnVCopy(AnyUnCopyPrim uncopy) {
    def(uncopy);
    uncopy.defineAction(VertBuilder.class, uncopy.NORMAL);
    uncopy.defineAction(ParBuilder.class, uncopy.FINISH_PAR);
    uncopy.defineAction(HBoxBuilder.class, uncopy.REJECT);
    uncopy.defineAction(MathBuilder.class, MathPrim.DOLLAR);
  }

  private void defUnHCopy(AnyUnCopyPrim uncopy) {
    def(uncopy);
    uncopy.defineAction(VertBuilder.class, uncopy.START_PAR);
    uncopy.defineAction(HorizBuilder.class, uncopy.NORMAL);
    uncopy.defineAction(MathBuilder.class, uncopy.NORMAL);
  }

  private void defIndent(IndentPrim indent) {
    def(indent);
    Action act = (indent.isIndenting()) ? indent.INSERT : indent.EMPTY;
    indent.defineAction(VertBuilder.class, indent.NORMAL);
    indent.defineAction(HorizBuilder.class, act);
    indent.defineAction(MathBuilder.class, act);
  }

  private void defEnd(EndPrim end) {
    def(end);
    end.defineAction(PageBuilder.class, end.NORMAL);
    end.defineAction(ParBuilder.class, end.FINISH_PAR);
    end.defineAction(HBoxBuilder.class, end.REJECT);
    end.defineAction(MathBuilder.class, MathPrim.DOLLAR);
  }

  {
    debugMessage("Primitives.<init(4)>");

    Glue fil_glue = Glue.valueOf(Dimen.ZERO, Dimen.UNITY, Glue.FIL, Dimen.ZERO, Glue.NORMAL),
        fil_neg_glue =
            Glue.valueOf(Dimen.ZERO, Dimen.UNITY.negative(), Glue.FIL, Dimen.ZERO, Glue.NORMAL),
        fill_glue = Glue.valueOf(Dimen.ZERO, Dimen.UNITY, Glue.FILL, Dimen.ZERO, Glue.NORMAL),
        ss_glue = Glue.valueOf(Dimen.ZERO, Dimen.UNITY, Glue.FIL, Dimen.UNITY, Glue.FIL);

    defVSkip(new AnySkipPrim("vskip"));
    defVSkip(new AnySkipPrim("vfil", fil_glue));
    defVSkip(new AnySkipPrim("vfilneg", fil_neg_glue));
    defVSkip(new AnySkipPrim("vfill", fill_glue));
    defVSkip(new AnySkipPrim("vss", ss_glue));

    defHSkip(new AnySkipPrim("hskip"));
    defHSkip(new AnySkipPrim("hfil", fil_glue));
    defHSkip(new AnySkipPrim("hfilneg", fil_neg_glue));
    defHSkip(new AnySkipPrim("hfill", fill_glue));
    defHSkip(new AnySkipPrim("hss", ss_glue));

    def(new KernPrim("kern"));
    def(new PenaltyPrim("penalty"));
    def(new VBoxPrim("vbox", every_vbox));
    def(new VTopPrim("vtop", every_vbox));
    def(new HBoxPrim("hbox", every_hbox));
    def(new BoxPrim("box", setbox));
    def(new CopyPrim("copy", setbox));

    def(new LeadersPrim("leaders"));
    def(new CLeadersPrim("cleaders"));
    def(new XLeadersPrim("xleaders"));

    RaisePrim raise = new RaisePrim("raise");
    LowerPrim lower = new LowerPrim("lower");
    MoveLeftPrim moveleft = new MoveLeftPrim("moveleft");
    MoveRightPrim moveright = new MoveRightPrim("moveright");

    def(raise);
    def(lower);
    def(moveleft);
    def(moveright);
    moveleft.defineAction(VertBuilder.class, moveleft.NORMAL);
    moveright.defineAction(VertBuilder.class, moveright.NORMAL);
    raise.defineAction(HorizBuilder.class, raise.NORMAL);
    lower.defineAction(HorizBuilder.class, lower.NORMAL);
    raise.defineAction(MathBuilder.class, raise.NORMAL);
    lower.defineAction(MathBuilder.class, lower.NORMAL);

    defUnVCopy(new AnyUnCopyPrim("unvcopy", setbox));
    defUnVCopy(new AnyUnBoxPrim("unvbox", setbox));
    defUnHCopy(new AnyUnCopyPrim("unhcopy", setbox));
    defUnHCopy(new AnyUnBoxPrim("unhbox", setbox));

    CharPrim char_prim = new CharPrim("char");
    ExSpacePrim ex_space = new ExSpacePrim(" ");
    NoBoundaryPrim no_boundary = new NoBoundaryPrim("noboundary");
    ItalCorrPrim ital_corr = new ItalCorrPrim("/");
    VAdjustPrim vadjust = new VAdjustPrim("vadjust");
    ParPrim par = new ParPrim("par");

    def(char_prim);
    def(ex_space);
    def(no_boundary);
    def(ital_corr);
    def(vadjust);
    def(par);

    def(new CharDefPrim("chardef", char_prim));

    char_prim.defineAction(VertBuilder.class, char_prim.START_PAR);
    ex_space.defineAction(VertBuilder.class, ex_space.START_PAR);
    no_boundary.defineAction(VertBuilder.class, no_boundary.START_PAR);
    par.defineAction(VertBuilder.class, par.RESET);

    char_prim.defineAction(HorizBuilder.class, char_prim.NORMAL);
    ex_space.defineAction(HorizBuilder.class, ex_space.NORMAL);
    no_boundary.defineAction(HorizBuilder.class, no_boundary.NORMAL);
    ital_corr.defineAction(HorizBuilder.class, ital_corr.NORMAL);
    vadjust.defineAction(HorizBuilder.class, vadjust.NORMAL);
    par.defineAction(HorizBuilder.class, par.NORMAL);

    char_prim.defineAction(MathBuilder.class, new CharMathAction(char_prim));
    ex_space.defineAction(MathBuilder.class, ex_space.NORMAL);
    no_boundary.defineAction(MathBuilder.class, no_boundary.EMPTY);
    ital_corr.defineAction(MathBuilder.class, MathPrim.ZERO_KERN);
    vadjust.defineAction(MathBuilder.class, vadjust.NORMAL);
    par.defineAction(MathBuilder.class, MathPrim.DOLLAR);

    HyphenPrim hyph = new HyphenPrim("-");
    DiscretionaryPrim disc = new DiscretionaryPrim("discretionary");
    SetLanguagePrim set_lang = new SetLanguagePrim("setlanguage");

    def(hyph);
    def(disc);
    def(set_lang);
    hyph.defineAction(VertBuilder.class, hyph.START_PAR);
    disc.defineAction(VertBuilder.class, disc.START_PAR);
    hyph.defineAction(HorizBuilder.class, hyph.NORMAL);
    disc.defineAction(HorizBuilder.class, disc.NORMAL);
    set_lang.defineAction(HorizBuilder.class, set_lang.NORMAL);
    hyph.defineAction(MathBuilder.class, hyph.NORMAL);
    disc.defineAction(MathBuilder.class, disc.NORMAL);

    defIndent(new IndentPrim("indent", true));
    defIndent(new IndentPrim("noindent", false));

    defEnd(new EndPrim("end", false));
    defEnd(new EndPrim("dump", true));
  }

  private Page.List pageList() {
    return Page.getPageList();
  }

  {
    debugMessage("Primitives.<init(5)>");

    AnyMarkPrim top_mark = new AnyMarkPrim("topmark"),
        first_mark = new AnyMarkPrim("firstmark"),
        bot_mark = new AnyMarkPrim("botmark"),
        split_first_mark = new AnyMarkPrim("splitfirstmark"),
        split_bot_mark = new AnyMarkPrim("splitbotmark");

    def(top_mark);
    def(first_mark);
    def(bot_mark);
    def(split_first_mark);
    def(split_bot_mark);

    Page.makeStaticData(count, dimen, skip, setbox, top_mark, first_mark, bot_mark);
    def(new VSplitPrim("vsplit", setbox, split_first_mark, split_bot_mark));

    def(new PageGoalPrim("pagegoal", pageList()));
    def(new PageTotalPrim("pagetotal", pageList()));
    def(new PageStretchPrim("pagestretch", pageList(), Glue.NORMAL));
    def(new PageStretchPrim("pagefilstretch", pageList(), Glue.FIL));
    def(new PageStretchPrim("pagefillstretch", pageList(), Glue.FILL));
    def(new PageStretchPrim("pagefilllstretch", pageList(), Glue.FILLL));
    def(new PageShrinkPrim("pageshrink", pageList()));
    def(new PageDepthPrim("pagedepth", pageList()));
    def(new DeadCyclesPrim("deadcycles", pageList()));
    def(new InsertPenaltiesPrim("insertpenalties", pageList()));

    def(new ShowListsPrim("showlists"));
    def(new InsertPrim("insert"));
    def(new MarkPrim("mark"));

    def(new UnSkipPrim("unskip"));
    def(new UnKernPrim("unkern"));
    def(new UnPenaltyPrim("unpenalty"));

    def(new LastBoxPrim("lastbox"));
    def(new LastSkipPrim("lastskip"));
    def(new LastKernPrim("lastkern"));
    def(new LastPenaltyPrim("lastpenalty"));

    def(new SpaceFactorPrim("spacefactor"));
    def(new PrevDepthPrim("prevdepth"));
    def(new PrevGrafPrim("prevgraf"));

    def(new IfVModePrim("ifvmode"));
    def(new IfHModePrim("ifhmode"));
    def(new IfMModePrim("ifmmode"));
    def(new IfInnerPrim("ifinner"));
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Grouping-related primitives
   */

  private final LeftBrace left_brace = new LeftBrace();
  private final RightBrace right_brace = new RightBrace();

  private final EndGroupPrim end_group = new EndGroupPrim("endgroup");
  private final BeginGroupPrim begin_group = new BeginGroupPrim("begingroup", end_group);

  {
    def(new AfterGroupPrim("aftergroup"));
    def(begin_group);
    def(end_group);
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Math  primitives
   */

  private void mActDef(MathPrim prim) {
    prim.defineAction(Builder.class, prim.DOLLAR);
    prim.defineAction(MathBuilder.class, prim.mathAction());
  }

  private void mdef(MathPrim prim) {
    def(prim);
    mActDef(prim);
  }

  private MathCompPrim math_ord = new MathOrdPrim("mathord"),
      math_op = new MathOpPrim("mathop"),
      math_bin = new MathBinPrim("mathbin"),
      math_rel = new MathRelPrim("mathrel"),
      math_open = new MathOpenPrim("mathopen"),
      math_close = new MathClosePrim("mathclose"),
      math_punct = new MathPunctPrim("mathpunct"),
      math_inner = new MathInnerPrim("mathinner");
  private ScriptPrim super_script = new SuperScriptPrim("superscript"),
      sub_script = new SubScriptPrim("subscript");
  private FamilyPrim text_font = new FamilyPrim("textfont"),
      script_font = new FamilyPrim("scriptfont"),
      script_script_font = new FamilyPrim("scriptscriptfont");

  private final OpenMath open_math = new OpenMath(every_math, every_display);

  // XXX maybe that all the EndGroupCommand-s can be constructed inside
  // XXX their owners
  private final EndGroupCommand close_math = new EndGroupCommand("internal close math"),
      close_left = new EndGroupCommand("internal close left"),
      start_eqno = new EndGroupCommand("internal start eqno"),
      start_leqno = new EndGroupCommand("internal start leqno");
  private EqNoPrim eqno = new EqNoPrim("eqno", start_eqno, every_math, false),
      leqno = new EqNoPrim("leqno", start_leqno, every_math, true);

  {
    debugMessage("Primitives.<init(6)>");

    open_math.defineAction(VertBuilder.class, BuilderPrim.START_PAR);
    open_math.defineAction(ParBuilder.class, open_math.OPEN_MATH);
    open_math.defineAction(HBoxBuilder.class, open_math.OPEN_FORMULA);
    open_math.defineAction(MathBuilder.class, new AdapterAction(close_math));
    left_brace.defineAction(MathBuilder.class, MathPrim.LEFT_BRACE);

    AdaptMathPrim right = new AdaptMathPrim("right", close_left);
    LeftPrim left = new LeftPrim("left", right);

    mdef(left);
    mdef(right);
    mdef(math_ord);
    mdef(math_op);
    mdef(math_bin);
    mdef(math_rel);
    mdef(math_open);
    mdef(math_close);
    mdef(math_punct);
    mdef(math_inner);
    mActDef(super_script);
    mActDef(sub_script);

    def(eqno);
    def(leqno);
    eqno.defineAction(DisplayBuilder.class, eqno.NORMAL);
    leqno.defineAction(DisplayBuilder.class, leqno.NORMAL);

    AccentPrim accent = new AccentPrim("accent");
    MathCharPrim math_char = new MathCharPrim("mathchar");
    MathAccentPrim math_accent = new MathAccentPrim("mathaccent");

    def(accent);
    mdef(math_char);
    mdef(math_accent);
    accent.defineAction(VertBuilder.class, accent.START_PAR);
    accent.defineAction(HorizBuilder.class, accent.NORMAL);
    accent.defineAction(MathBuilder.class, math_accent.FORCE_MATH_ACCENT);

    def(new MathCharDefPrim("mathchardef", math_char));
    def(text_font);
    def(script_font);
    def(script_script_font);

    mdef(new OverlinePrim("overline"));
    mdef(new UnderlinePrim("underline"));
    mdef(new DelimiterPrim("delimiter"));
    mdef(new RadicalPrim("radical"));
    mdef(new VCenterPrim("vcenter", every_vbox));
    mdef(new MathChoicePrim("mathchoice"));
    mdef(new FractionPrim("above", false, FractionPrim.EXPLICIT_THICKNESS));
    mdef(new FractionPrim("over", false, FractionPrim.DEFAULT_THICKNESS));
    mdef(new FractionPrim("atop", false, FractionPrim.ZERO_THICKNESS));
    mdef(new FractionPrim("abovewithdelims", true, FractionPrim.EXPLICIT_THICKNESS));
    mdef(new FractionPrim("overwithdelims", true, FractionPrim.DEFAULT_THICKNESS));
    mdef(new FractionPrim("atopwithdelims", true, FractionPrim.ZERO_THICKNESS));

    mdef(new LimitsPrim("nolimits", OpNoad.SIDE_LIMITS));
    mdef(new LimitsPrim("limits", OpNoad.USUAL_LIMITS));
    mdef(new LimitsPrim("displaylimits", OpNoad.DEFAULT_LIMITS));

    mdef(new StylePrim("displaystyle", StyleNoad.DISPLAY_STYLE));
    mdef(new StylePrim("textstyle", StyleNoad.TEXT_STYLE));
    mdef(new StylePrim("scriptstyle", StyleNoad.SCRIPT_STYLE));
    mdef(new StylePrim("scriptscriptstyle", StyleNoad.SCRIPT_SCRIPT_STYLE));

    mdef(new MSkipPrim("mskip"));
    mdef(new MKernPrim("mkern"));
    mdef(new NonScriptPrim("nonscript"));
  }

  private transient FamilyPrim[] families;

  private void initMath() {
    debugMessage("Primitives.initMath()");

    MathShiftToken.setCommand(open_math);
    SuperMarkToken.setCommand(super_script);
    SubMarkToken.setCommand(sub_script);

    families = new FamilyPrim[FormulaGroup.NUMBER_OF_FONT_SIZES];
    families[FormulaGroup.TEXT_SIZE] = text_font;
    families[FormulaGroup.SCRIPT_SIZE] = script_font;
    families[FormulaGroup.SCRIPT_SCRIPT_SIZE] = script_script_font;
  }

  {
    initMath();
  }

  private final Endv endv = new Endv();
  private final TabMarkPrim tab_mark = new TabMarkPrim("tabmark");
  private final CrPrim cr = new CrPrim("cr");
  private final VAlignPrim valign = new VAlignPrim("valign", every_cr, cr, endv);
  private final HAlignPrim halign = new HAlignPrim("halign", every_cr, cr, endv);

  private final EndGroupCommand close_column = new EndGroupCommand("internal close column");
  private final EndGroupCommand disp_halign = new EndGroupCommand("internal displayed halign");

  {
    debugMessage("Primitives.<init(7)>");

    def(cr);
    def(valign);
    def(halign);
    halign.defineAction(VertBuilder.class, halign.NORMAL);
    halign.defineAction(ParBuilder.class, halign.FINISH_PAR);
    halign.defineAction(HBoxBuilder.class, halign.REJECT);
    halign.defineAction(DisplayBuilder.class, new AdapterAction(disp_halign));
    valign.defineAction(VertBuilder.class, valign.START_PAR);
    valign.defineAction(HorizBuilder.class, valign.NORMAL);
    valign.defineAction(MathBuilder.class, MathPrim.DOLLAR);

    def(new CrCrPrim("crcr"));
    def(new SpanPrim("span"));
    def(new OmitPrim("omit"));
    def(new NoAlignPrim("noalign"));

    Action normalEndv = new AdapterAction(close_column);
    endv.defineAction(VertBuilder.class, normalEndv);
    endv.defineAction(HorizBuilder.class, normalEndv);
    endv.defineAction(MathBuilder.class, MathPrim.DOLLAR);
  }

  private void initAlign() {
    debugMessage("Primitives.initAlign()");

    TabMarkToken.setCommand(tab_mark);
  }

  {
    initAlign();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Groups initialization
   */

  private void initGroups() {
    debugMessage("Primitives.initGroups()");

    LeftBraceToken.setCommand(left_brace);
    RightBraceToken.setCommand(right_brace);

    end_group.defineClosing(SemiSimpleGroup.class, end_group.NORMAL);
    close_math.defineClosing(FormulaGroup.class, close_math.NORMAL);
    close_left.defineClosing(LeftGroup.class, close_left.NORMAL);
    close_column.defineClosing(Alignment.SpanGroup.class, Alignment.CLOSE_COLUMN);
    start_eqno.defineClosing(DisplayGroup.class, eqno.makeDisplayedClosing());
    start_leqno.defineClosing(DisplayGroup.class, leqno.makeDisplayedClosing());
    disp_halign.defineClosing(DisplayGroup.class, halign.makeDisplayedClosing());

    right_brace.defineClosing(SemiSimpleGroup.class, right_brace.EXTRA);
    right_brace.defineClosing(FormulaGroup.class, right_brace.EXTRA);
    right_brace.defineClosing(LeftGroup.class, right_brace.EXTRA);
    close_left.defineClosing(FormulaGroup.class, LeftPrim.EXTRA_RIGHT);
    right_brace.defineClosing(Alignment.SpanGroup.class, Alignment.MISSING_CR);

    GroupCommand.registerGroup(Group.class);
    GroupCommand.registerGroup(BottomGroup.class);
    GroupCommand.registerGroup(SimpleGroup.class);
    GroupCommand.registerGroup(SemiSimpleGroup.class);
    GroupCommand.registerGroup(HorizGroup.class);
    GroupCommand.registerGroup(HBoxGroup.class);
    GroupCommand.registerGroup(VertGroup.class);
    GroupCommand.registerGroup(VBoxGroup.class);
    GroupCommand.registerGroup(VTopGroup.class);
    GroupCommand.registerGroup(Page.List.OutputGroup.class);
    GroupCommand.registerGroup(InsertPrim.InsertGroup.class);
    GroupCommand.registerGroup(VAdjustPrim.VAdjustGroup.class);
    GroupCommand.registerGroup(VCenterGroup.class);
    GroupCommand.registerGroup(FormulaGroup.class);
    GroupCommand.registerGroup(DisplayGroup.class);
    GroupCommand.registerGroup(EqNoGroup.class);
    GroupCommand.registerGroup(MathGroup.class);
    GroupCommand.registerGroup(LeftGroup.class);
    GroupCommand.registerGroup(MathChoiceGroup.class);
    GroupCommand.registerGroup(AlignGroup.class);
    GroupCommand.registerGroup(DispAlignGroup.class);
    GroupCommand.registerGroup(Alignment.SpanGroup.class);
    GroupCommand.registerGroup(Alignment.NoAlignGroup.class);
  }

  {
    initGroups();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Builders initialization
   */

  {
    debugMessage("Primitives.<init(builders)>");

    BuilderCommand.registerBuilder(Builder.class);
    BuilderCommand.registerBuilder(ListBuilder.class);
    BuilderCommand.registerBuilder(VertBuilder.class);
    BuilderCommand.registerBuilder(PageBuilder.class);
    BuilderCommand.registerBuilder(VBoxBuilder.class);
    BuilderCommand.registerBuilder(OutputBuilder.class);
    BuilderCommand.registerBuilder(HorizBuilder.class);
    BuilderCommand.registerBuilder(ParBuilder.class);
    BuilderCommand.registerBuilder(HBoxBuilder.class);
    BuilderCommand.registerBuilder(MathBuilder.class);
    BuilderCommand.registerBuilder(FormulaBuilder.class);
    BuilderCommand.registerBuilder(DisplayBuilder.class);
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   */

  private final DefCodePrim
      cat_code = new DefCodePrim("catcode", TeXConfig.CAT_OTHER_CHAR, TeXConfig.MAX_CATEGORY),
      lc_code = new DefCodePrim("lccode", 0, TeXConfig.MAX_TEX_CHAR),
      uc_code = new DefCodePrim("uccode", 0, TeXConfig.MAX_TEX_CHAR),
      sf_code =
          new DefCodePrim("sfcode", TeXConfig.NORMAL_SPACE_FACTOR, TeXConfig.MAX_SPACE_FACTOR),
      math_code = new MathCodePrim("mathcode", 0x8000),
      del_code = new DelCodePrim("delcode", -1, 0xffffff);

  {
    debugMessage("Primitives.<init(8)>");

    def(cat_code);
    def(lc_code);
    def(uc_code);
    def(sf_code);
    def(math_code);
    def(del_code);

    char uc, lc;
    for (uc = 'A', lc = 'a'; uc <= 'Z'; uc++, lc++) {
      cat_code.init(uc, TeXConfig.CAT_LETTER);
      cat_code.init(lc, TeXConfig.CAT_LETTER);
      lc_code.init(uc, lc);
      uc_code.init(uc, uc);
      lc_code.init(lc, lc);
      uc_code.init(lc, uc);
      sf_code.init(uc, TeXConfig.UPCASE_SPACE_FACTOR);
      math_code.init(uc, uc + 0x7100); // XXX
      math_code.init(lc, lc + 0x7100); // XXX
    }

    for (char dig = '0'; dig <= '9'; dig++) math_code.init(dig, dig + 0x7000); // XXX

    cat_code.init('\r', TeXConfig.CAT_CAR_RET);
    cat_code.init(' ', TeXConfig.CAT_SPACER);
    cat_code.init('\\', TeXConfig.CAT_ESCAPE);
    cat_code.init('%', TeXConfig.CAT_COMMENT);
    cat_code.init(0, TeXConfig.CAT_IGNORE);
    cat_code.init(127, TeXConfig.CAT_INVALID_CHAR);
    del_code.init('.', 0);

    if (false) { // only for testing
      cat_code.init('{', TeXConfig.CAT_LEFT_BRACE);
      cat_code.init('}', TeXConfig.CAT_RIGHT_BRACE);
      cat_code.init('$', TeXConfig.CAT_MATH_SHIFT);
      cat_code.init('&', TeXConfig.CAT_TAB_MARK);
      cat_code.init('#', TeXConfig.CAT_MAC_PARAM);
      cat_code.init('~', TeXConfig.CAT_ACTIVE_CHAR);
      cat_code.init('^', TeXConfig.CAT_SUP_MARK);
      cat_code.init('_', TeXConfig.CAT_SUB_MARK);
      cat_code.init('\b', TeXConfig.CAT_SPACER);
      cat_code.init('\t', TeXConfig.CAT_SPACER);
      cat_code.init('\n', TeXConfig.CAT_SPACER);
      cat_code.init('\f', TeXConfig.CAT_SPACER);
    }
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Implementation of |Command.Config|.
   */

  private static final class ConstIntProvider implements IntProvider {
    private final int value;

    public ConstIntProvider(int val) {
      value = val;
    }

    public int intVal() {
      return value;
    }
  }

  private static final class AtLeastIntProvider implements IntProvider {
    private final IntProvider prov;
    private final int threshold;
    private final int value;

    public AtLeastIntProvider(IntProvider p, int t, int val) {
      prov = p;
      threshold = t;
      value = val;
    }

    public int intVal() {
      int i = prov.intVal();
      return (i > threshold) ? i : value;
    }
  }

  private static final class FontDimenProvider implements Dimen.Provider {
    private final int param;

    public FontDimenProvider(int par) {
      param = par;
    }

    public Dimen getDimenValue() {
      return TypoCommand.getCurrFontMetric().getDimenParam(param);
    }
  }

  private abstract static class Relation {
    protected IntProvider provider;
    protected int value;

    public Relation(IntProvider prov, int val) {
      provider = prov;
      value = val;
    }

    public abstract boolean execute();
  }

  private static class LessThan extends Relation {
    public LessThan(IntProvider prov, int val) {
      super(prov, val);
    }

    public boolean execute() {
      return (provider.intVal() < value);
    }
  }

  private static class MoreThan extends Relation {
    public MoreThan(IntProvider prov, int val) {
      super(prov, val);
    }

    public boolean execute() {
      return (provider.intVal() > value);
    }
  }

  private transient IntProvider[] intParams;
  private transient Dimen.Provider[] dimParams;
  private transient GlueParam[] glueParams;
  private transient ToksParam[] toksParams;
  private transient Relation[] boolParams;

  public int getIntParam(int param) {
    if (fits(param, intParams)) return intParams[param].intVal();
    paramError("Int", param);
    return 0;
  }

  public Dimen getDimParam(int param) {
    if (fits(param, dimParams)) return dimParams[param].getDimenValue();
    paramError("Dim", param);
    return Dimen.ZERO;
  }

  public Glue getGlueParam(int param) {
    if (fits(param, glueParams)) return glueParams[param].getGlueValue();
    paramError("Glue", param);
    return Glue.ZERO;
  }

  public String getGlueName(int param) {
    if (fits(param, glueParams)) return glueParams[param].getName();
    return null;
  }

  public TokenList getToksParam(int param) {
    if (fits(param, toksParams)) return toksParams[param].getToksValue();
    paramError("Toks", param);
    return TokenList.EMPTY;
  }

  public TokenList.Inserter getToksInserter(int param) {
    if (fits(param, toksParams)) return toksParams[param];
    paramError("Toks", param);
    return null;
  }

  public boolean getBoolParam(int param) {
    if (fits(param, boolParams)) return boolParams[param].execute();
    paramError("Bool", param);
    return false;
  }

  private boolean fits(int idx, Object array) {
    return (0 <= idx && idx < Array.getLength(array) && Array.get(array, idx) != null);
  }

  private void paramError(String which, int param) {
    throw new RuntimeException(which + "Param(" + param + ") not supported");
  }

  private void initParams() {
    debugMessage("Primitives.initParams()");

    /* Paragraph must be loaded before
     * CommandBase.max*Param is used
     */
    Class dummy;
    dummy = Paragraph.class;

    ConstIntProvider CONST_15 = new ConstIntProvider(15);
    ConstIntProvider CONST_255 = new ConstIntProvider(255);
    ConstIntProvider CONST_2_15_1 = new ConstIntProvider(0x7fff);
    ConstIntProvider CONST_2_27_1 = new ConstIntProvider(0x7ffffff);

    /* ** Int Parameters ** */

    intParams = new IntProvider[CommandBase.maxIntParam()];
    intParams[CommandBase.INTP_MAGNIFICATION] = mag;
    intParams[CommandBase.INTP_MAX_RUNAWAY_WIDTH] = new ConstIntProvider(TeXConfig.MAX_RUNAWAY_WD);
    intParams[CommandBase.INTP_MAX_TLRES_TRACE] = new ConstIntProvider(TeXConfig.MAX_TOK_LIST_RT);
    intParams[Prim.INTP_MAX_REG_CODE] = CONST_255;
    intParams[Prim.INTP_MAX_FILE_CODE] = CONST_15;
    intParams[Prim.INTP_MAX_CHAR_CODE] = CONST_255;
    intParams[MathPrim.INTP_MAX_MATH_CODE] = CONST_2_15_1;
    intParams[MathPrim.INTP_MAX_DEL_CODE] = CONST_2_27_1;
    intParams[TypoCommand.INTP_SHOW_BOX_DEPTH] = show_box_depth;
    intParams[TypoCommand.INTP_SHOW_BOX_BREADTH] = new AtLeastIntProvider(show_box_breadth, 0, 5);
    intParams[TypoCommand.INTP_HBADNESS] = hbadness;
    intParams[TypoCommand.INTP_VBADNESS] = vbadness;
    intParams[TypoCommand.INTP_OUTPUT_BOX_NUM] = CONST_255;
    intParams[Paragraph.INTP_PRETOLERANCE] = pretolerance;
    intParams[Paragraph.INTP_TOLERANCE] = tolerance;
    intParams[Paragraph.INTP_LOOSENESS] = looseness;
    intParams[Paragraph.INTP_LINE_PENALTY] = line_penalty;
    intParams[Paragraph.INTP_HYPHEN_PENALTY] = hyphen_penalty;
    intParams[Paragraph.INTP_EX_HYPHEN_PENALTY] = ex_hyphen_penalty;
    intParams[Paragraph.INTP_ADJ_DEMERITS] = adj_demerits;
    intParams[Paragraph.INTP_DOUBLE_HYPHEN_DEMERITS] = double_hyphen_demerits;
    intParams[Paragraph.INTP_FINAL_HYPHEN_DEMERITS] = final_hyphen_demerits;
    intParams[Paragraph.INTP_INTER_LINE_PENALTY] = inter_line_penalty;
    intParams[Paragraph.INTP_BROKEN_PENALTY] = broken_penalty;
    intParams[Paragraph.INTP_CLUB_PENALTY] = club_penalty;
    intParams[Paragraph.INTP_WIDOW_PENALTY] = widow_penalty;
    intParams[Page.INTP_MAX_DEAD_CYCLES] = max_dead_cycles;
    intParams[BuilderPrim.INTP_MAX_MARK_WIDTH] = new ConstIntProvider(TeXConfig.MAX_MARK_WD);
    intParams[InsertPrim.INTP_FLOATING_PENALTY] = floating_penalty;
    intParams[OpenMath.INTP_DISPLAY_WIDOW_PENALTY] = display_widow_penalty;
    intParams[FormulaGroup.INTP_DELIMITER_FACTOR] = delimiter_factor;
    intParams[DisplayGroup.INTP_PRE_DISPLAY_PENALTY] = pre_display_penalty;
    intParams[DisplayGroup.INTP_POST_DISPLAY_PENALTY] = post_display_penalty;

    /* ** Dim Parameters ** */

    dimParams = new Dimen.Provider[CommandBase.maxDimParam()];
    dimParams[CommandBase.DIMP_EM] = new FontDimenProvider(FontMetric.DIMEN_PARAM_QUAD);
    dimParams[CommandBase.DIMP_EX] = new FontDimenProvider(FontMetric.DIMEN_PARAM_X_HEIGHT);
    dimParams[TypoCommand.DIMP_HFUZZ] = hfuzz;
    dimParams[TypoCommand.DIMP_VFUZZ] = vfuzz;
    dimParams[TypoCommand.DIMP_OVERFULL_RULE] = overfull_rule;
    dimParams[TypoCommand.DIMP_LINE_SKIP_LIMIT] = line_skip_limit;
    dimParams[TypoCommand.DIMP_H_OFFSET] = h_offset;
    dimParams[TypoCommand.DIMP_V_OFFSET] = v_offset;
    dimParams[TypoCommand.DIMP_BOX_MAX_DEPTH] = box_max_depth;
    dimParams[TypoCommand.DIMP_SPLIT_MAX_DEPTH] = split_max_depth;
    dimParams[Paragraph.DIMP_EMERGENCY_STRETCH] = emergency_stretch;
    dimParams[Paragraph.DIMP_PAR_INDENT] = par_indent;
    dimParams[Page.DIMP_VSIZE] = vsize;
    dimParams[Page.DIMP_MAX_DEPTH] = max_depth;
    dimParams[EndPrim.DIMP_HSIZE] = hsize;
    dimParams[FormulaGroup.DIMP_MATH_SURROUND] = math_surround;
    dimParams[FormulaGroup.DIMP_NULL_DELIMITER_SPACE] = null_delimiter_space;
    dimParams[FormulaGroup.DIMP_DELIMITER_SHORTFALL] = delimiter_shortfall;
    dimParams[FormulaGroup.DIMP_SCRIPT_SPACE] = script_space;
    dimParams[DisplayGroup.DIMP_PRE_DISPLAY_SIZE] = pre_display_size;
    dimParams[DisplayGroup.DIMP_DISPLAY_WIDTH] = display_width;
    dimParams[DisplayGroup.DIMP_DISPLAY_INDENT] = display_indent;

    /* ** Glue Parameters ** */

    glueParams = new GlueParam[CommandBase.maxGlueParam()];
    glueParams[TypoCommand.GLUEP_SPACE] = space_skip;
    glueParams[TypoCommand.GLUEP_XSPACE] = xspace_skip;
    glueParams[TypoCommand.GLUEP_LINE_SKIP] = line_skip;
    glueParams[TypoCommand.GLUEP_BASELINE_SKIP] = baseline_skip;
    glueParams[TypoCommand.GLUEP_SPLIT_TOP_SKIP] = split_top_skip;
    glueParams[Paragraph.GLUEP_PAR_SKIP] = par_skip;
    glueParams[Paragraph.GLUEP_PAR_FILL_SKIP] = par_fill_skip;
    glueParams[Paragraph.GLUEP_LEFT_SKIP] = left_skip;
    glueParams[Paragraph.GLUEP_RIGHT_SKIP] = right_skip;
    glueParams[Page.GLUEP_TOP_SKIP] = top_skip;
    glueParams[DisplayGroup.GLUEP_ABOVE_DISPLAY_SKIP] = above_display_skip;
    glueParams[DisplayGroup.GLUEP_BELOW_DISPLAY_SKIP] = below_display_skip;
    glueParams[DisplayGroup.GLUEP_ABOVE_DISPLAY_SHORT_SKIP] = above_display_short_skip;
    glueParams[DisplayGroup.GLUEP_BELOW_DISPLAY_SHORT_SKIP] = below_display_short_skip;
    glueParams[Alignment.GLUEP_TAB_SKIP] = tab_skip;

    /* ** Toks Parameters ** */

    toksParams = new ToksParam[CommandBase.maxToksParam()];
    toksParams[Command.TOKSP_EVERY_JOB] = every_job;
    toksParams[Paragraph.TOKSP_EVERY_PAR] = every_par;
    toksParams[Page.TOKSP_OUTPUT] = output_routine;

    /* ** Bool Parameters ** */

    boolParams = new Relation[CommandBase.maxBoolParam()];
    boolParams[CommandBase.BOOLP_TRACING_TOKEN_LISTS] = new MoreThan(tracing_macros, 1);
    boolParams[CommandBase.BOOLP_TRACING_RESTORES] = new MoreThan(tracing_restores, 0);
    boolParams[PrefixPrim.BOOLP_ALWAYS_GLOBAL] = new MoreThan(global_defs, 0);
    boolParams[PrefixPrim.BOOLP_NEVER_GLOBAL] = new LessThan(global_defs, 0);
    boolParams[Macro.BOOLP_TRACING_MACROS] = new MoreThan(tracing_macros, 0);
    boolParams[Command.BOOLP_TRACING_COMMANDS] = new MoreThan(tracing_commands, 0);
    boolParams[Expandable.BOOLP_TRACING_ALL_COMMANDS] = new MoreThan(tracing_commands, 1);
    boolParams[TypoCommand.BOOLP_TRACING_LOST_CHARS] = new MoreThan(tracing_lost_chars, 0);
    boolParams[TypoCommand.BOOLP_TRACING_OUTPUT] = new MoreThan(tracing_output, 0);
    boolParams[Paragraph.BOOLP_TRACING_PARAGRAPHS] = new MoreThan(tracing_paragraphs, 0);
    boolParams[Paragraph.BOOLP_UC_HYPH] = new MoreThan(uc_hyph, 0);
    boolParams[Page.BOOLP_TRACING_PAGES] = new MoreThan(tracing_pages, 0);
    boolParams[Page.BOOLP_HOLDING_INSERTS] = new MoreThan(holding_inserts, 0);
  }

  {
    initParams();
  }

  private boolean afterAssignmentEnabled = true;

  public boolean enableAfterAssignment(boolean val) {
    boolean old = afterAssignmentEnabled;
    afterAssignmentEnabled = val;
    return old;
  }

  public void afterAssignment() {
    if (afterAssignmentEnabled) after_assignment.apply();
  }

  private final Token FROZEN_FI = new FrozenToken(prim_fi);

  public boolean enableInput(boolean val) {
    return input.enable(val);
  }

  public boolean formatLoaded() {
    return loaded;
  }

  public Token frozenFi() {
    return FROZEN_FI;
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Implementation of |TypoCommand.Config|.
   */

  public void setLastBadness(int val) {
    badness.set(val);
  }

  public void setOutputPenalty(int val) {
    output_penalty.set(Num.valueOf(val), true);
  }

  public int[] currPageNumbers() {
    int i = 9;
    while (i > 0 && count.intVal(i) == 0) i--;
    int[] nums = new int[++i];
    while (--i >= 0) nums[i] = count.intVal(i);
    return nums;
  }

  /* TeXtp[1094] */
  public void checkParagraph(Token src) {
    int disbalance = Alignment.activeColumnDisbalance();
    if (disbalance < 0 && disbalance != Alignment.NOALIGN_DISBALANCE) Command.getGrp().reject(src);
  }

  /* TeXtp[1070] */
  public void resetParagraph() {
    if (looseness.intVal() != 0) looseness.set(Num.ZERO, false);
    if (!hang_indent.get().isZero()) hang_indent.set(Dimen.ZERO, false);
    if (hang_after.intVal() != 1) hang_after.set(Num.valueOf(1), false);
    if (!par_shape.get().isEmpty()) par_shape.set(ParShape.EMPTY, false);
  }

  public void setMarginSkipsShrinkFinite() {
    left_skip.makeShrinkFinite();
    right_skip.makeShrinkFinite();
  }

  public boolean activeOutput() {
    return pageList().outputActive();
  }

  public void resetOutput() {
    pageList().resetDeadCycles();
  }

  public boolean pendingOutput() {
    return (pageList().getDeadCycles() != 0);
  }

  public LinesShape linesShape() {
    ParShape parShape = par_shape.get();
    return (!parShape.isEmpty())
        ? parShape
        : HangIndent.makeShape(hang_after.intVal(), hang_indent.get(), hsize.get());
  }

  private HashHyphenation[] hyphenations = new HashHyphenation[NUMBER_OF_LANGUAGES];

  private WordMap[] patterns = new WordMap[NUMBER_OF_LANGUAGES];

  private boolean packed = false;

  private transient HashMap hyphNodeMap;
  private transient int[] hyphStats;

  public boolean patternsAllowed() {
    return (!packed);
  }

  public void preparePatterns() {
    if (!packed) {
      /*
          if (hyphStats[0] > 0) {
      	System.err.println("hyphen nodes: " + hyphStats[0]);
      	System.err.println("shared nodes: " + hyphStats[1]
      	    + " (" + (100 * hyphStats[1] / hyphStats[0]) + "%)");
          }
      */
      for (int i = 0; i < patterns.length; i++)
        if (patterns[i] != WordMap.NULL) {
          // patterns[i] = patterns[i].packed();
          /*
              try {
          	System.err.println("patterns(" + i + "):");
          	java.io.PrintWriter		perr
          	    = new java.io.PrintWriter(System.err);
          	HyphLanguage.dumpPatterns(perr, patterns[i]);
          	perr.flush();
              } catch (IOException e) { }
          */
        }
      packed = true;
    }
  }

  private int normalizedLangNum(int ln) {
    return (ln < 0 || ln >= hyphenations.length) ? 0 : ln;
  }

  public Language getLanguage(int langNum) {
    langNum = normalizedLangNum(langNum);
    if (hyphenations[langNum] == HashHyphenation.NULL) {
      hyphenations[langNum] = new HashHyphenation();
      if (packed) patterns[langNum] = WordTrie.EMPTY;
      else patterns[langNum] = new WordTree();
    }
    return new HyphLanguage(
        langNum,
        normalHyphenMin(left_hyphen_min.intVal()),
        normalHyphenMin(right_hyphen_min.intVal()),
        hyphenations[langNum],
        patterns[langNum],
        hyphNodeMap,
        hyphStats);
  }

  public Language getLanguage() {
    return getLanguage(language.intVal());
  }

  /* STRANGE
   * Why is only the language number compared and not also hyphen_(min|max)?
   */
  public boolean languageDiffers(Language lang) {
    return !lang.sameNumberAs(normalizedLangNum(language.intVal()));
  }

  /* TeXtp[1091] */
  private int normalHyphenMin(int h) {
    return (h <= 0) ? 1 : (h >= 64) ? 63 : h;
  } // XXX

  private void initHyphenation() {
    debugMessage("Primitives.initHyphenation()");

    hyphNodeMap = new HashMap();
    hyphStats = new int[2];
  }

  {
    initHyphenation();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Implementation of |MathPrim.Config|.
   */

  public void initFormula() {
    cur_fam.set(Num.valueOf(-1), false);
  }

  public void initDisplay(Dimen preSize, Dimen width, Dimen indent) {
    cur_fam.set(Num.valueOf(-1), false);
    pre_display_size.set(preSize, false);
    display_width.set(width, false);
    display_indent.set(indent, false);
  }

  private final MathCompPrim[] mathComps = {
    math_ord, math_op, math_bin, math_rel, math_open, math_close, math_punct
  };

  /* TeXtp[1155] */
  public Noad noadForCode(int code) {
    if (code < 0 || code >= 0x8000) return Noad.NULL;
    int mathClass = code >>> 12;
    byte mathFam = (byte) (code >>> 8 & 15);
    if (mathClass >= mathComps.length) {
      int fam = cur_fam.intVal();
      if (fam >= 0 && fam < 16) mathFam = (byte) fam;
      mathClass = 0;
    }
    return mathComps[mathClass].makeNoad(new CharField(mathFam, Token.makeCharCode(code & 0xff)));
  }

  /* TeXtp[1151] */
  public CharField fieldForCode(int code) {
    if (code < 0 || code >= 0x8000) return CharField.NULL;
    int mathClass = code >>> 12;
    byte mathFam = (byte) (code >>> 8 & 15);
    if (mathClass >= mathComps.length) {
      int fam = cur_fam.intVal();
      if (fam >= 0 && fam < 16) mathFam = (byte) fam;
    }
    return new CharField(mathFam, Token.makeCharCode(code & 0xff));
  }

  public Noad noadForDelCode(int code) {
    return noadForCode(code >>> 12);
  }

  public CharField fieldForDelCode(int code) {
    return fieldForCode(code >>> 12);
  }

  /* TeXtp[1160] */
  public Delimiter delimiterForDelCode(int code) {
    if (code < 0) return Delimiter.NULL;
    int small = code >>> 12 & 0xfff;
    int large = code & 0xfff;
    byte smallFam = (byte) (small >>> 8 & 15);
    byte largeFam = (byte) (large >>> 8 & 15);
    return new Delimiter(smallFam, delCharCode(small), largeFam, delCharCode(large));
  }

  private static CharCode delCharCode(int code) {
    return (code != 0) ? Token.makeCharCode(code & 0xff) : CharCode.NULL;
  }

  public FontMetric familyFont(byte size, byte fam) {
    return families[size].get(fam);
  }

  public String familyName(byte size, byte fam) {
    return families[size].getName();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *  Math Spacing
   */

  private static class ZeroSpacer implements MathPrim.MathSpacer {
    public Glue getGlue(byte fontSize) {
      return Glue.ZERO;
    }

    public String getName() {
      return "";
    }
  }

  private static class BaseSpacer implements MathPrim.MathSpacer {
    protected final MuGlueParam param;

    public BaseSpacer(MuGlueParam param) {
      this.param = param;
    }

    public Glue getGlue(byte fontSize) {
      return param.getMuGlueValue();
    }

    public String getName() {
      return param.getName();
    }
  }

  private static class TextSpacer extends BaseSpacer {
    public TextSpacer(MuGlueParam param) {
      super(param);
    }

    public Glue getGlue(byte fontSize) {
      return (fontSize == FormulaGroup.TEXT_SIZE) ? param.getMuGlueValue() : Glue.ZERO;
    }
  }

  private transient MathPrim.MathSpacer[] mathSpacers;

  private transient MathPrim.MathSpacer ZERO_SPACER,
      THIN_SPACER,
      XTHIN_SPACER,
      MED_SPACER,
      THICK_SPACER;

  private int mathSpacingIndex(byte left, byte right) {
    return left * Noad.NUMBER_OF_SPACING_TYPES + right;
  }

  private void initMathSpacing(byte left, byte right, MathPrim.MathSpacer spacer) {
    mathSpacers[mathSpacingIndex(left, right)] = spacer;
  }

  private void msInval(byte left, byte right) {
    initMathSpacing(left, right, null);
  }

  private void msZero(byte left, byte right) {
    initMathSpacing(left, right, ZERO_SPACER);
  }

  private void msThin(byte left, byte right) {
    initMathSpacing(left, right, THIN_SPACER);
  }

  private void msTHIN(byte left, byte right) {
    initMathSpacing(left, right, XTHIN_SPACER);
  }

  private void msMed(byte left, byte right) {
    initMathSpacing(left, right, MED_SPACER);
  }

  private void msThick(byte left, byte right) {
    initMathSpacing(left, right, THICK_SPACER);
  }

  public MathPrim.MathSpacer mathSpacing(byte left, byte right) {
    MathPrim.MathSpacer spacer = mathSpacers[mathSpacingIndex(left, right)];
    if (spacer == null) throw new RuntimeException("invalid combination of spacing types");
    return spacer;
  }

  public Num mathPenalty(byte left, byte right) {
    if (right != Noad.SPACING_TYPE_REL)
      switch (left) {
        case Noad.SPACING_TYPE_BIN:
          return bin_op_penalty.getNumValue();
        case Noad.SPACING_TYPE_REL:
          return rel_penalty.getNumValue();
      }
    return Num.NULL;
  }

  private void initMathSpacing() {
    debugMessage("Primitives.MathSpacing()");

    int size = Noad.NUMBER_OF_SPACING_TYPES;
    mathSpacers = new MathPrim.MathSpacer[size * size];

    ZERO_SPACER = new ZeroSpacer();
    THIN_SPACER = new TextSpacer(thin_mu_skip);
    XTHIN_SPACER = new BaseSpacer(thin_mu_skip);
    MED_SPACER = new TextSpacer(med_mu_skip);
    THICK_SPACER = new TextSpacer(thick_mu_skip);

    msZero(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_ORD); // 0
    msTHIN(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_OP); // 2
    msMed(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_BIN); // 3
    msThick(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_REL); // 4
    msZero(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_OPEN); // 0
    msZero(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_CLOSE); // 0
    msZero(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_PUNCT); // 0
    msThin(Noad.SPACING_TYPE_ORD, Noad.SPACING_TYPE_INNER); // 1

    msTHIN(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_ORD); // 2
    msTHIN(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_OP); // 2
    msInval(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_BIN); // *
    msThick(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_REL); // 4
    msZero(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_OPEN); // 0
    msZero(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_CLOSE); // 0
    msZero(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_PUNCT); // 0
    msThin(Noad.SPACING_TYPE_OP, Noad.SPACING_TYPE_INNER); // 1

    msMed(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_ORD); // 3
    msMed(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_OP); // 3
    msInval(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_BIN); // *
    msInval(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_REL); // *
    msMed(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_OPEN); // 3
    msInval(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_CLOSE); // *
    msInval(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_PUNCT); // *
    msMed(Noad.SPACING_TYPE_BIN, Noad.SPACING_TYPE_INNER); // 3

    msThick(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_ORD); // 4
    msThick(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_OP); // 4
    msInval(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_BIN); // *
    msZero(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_REL); // 0
    msThick(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_OPEN); // 4
    msZero(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_CLOSE); // 0
    msZero(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_PUNCT); // 0
    msThick(Noad.SPACING_TYPE_REL, Noad.SPACING_TYPE_INNER); // 4

    msZero(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_ORD); // 0
    msZero(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_OP); // 0
    msInval(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_BIN); // *
    msZero(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_REL); // 0
    msZero(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_OPEN); // 0
    msZero(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_CLOSE); // 0
    msZero(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_PUNCT); // 0
    msZero(Noad.SPACING_TYPE_OPEN, Noad.SPACING_TYPE_INNER); // 0

    msZero(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_ORD); // 0
    msTHIN(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_OP); // 2
    msMed(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_BIN); // 3
    msThick(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_REL); // 4
    msZero(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_OPEN); // 0
    msZero(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_CLOSE); // 0
    msZero(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_PUNCT); // 0
    msThin(Noad.SPACING_TYPE_CLOSE, Noad.SPACING_TYPE_INNER); // 1

    msThin(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_ORD); // 1
    msThin(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_OP); // 1
    msInval(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_BIN); // *
    msThin(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_REL); // 1
    msThin(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_OPEN); // 1
    msThin(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_CLOSE); // 1
    msThin(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_PUNCT); // 1
    msThin(Noad.SPACING_TYPE_PUNCT, Noad.SPACING_TYPE_INNER); // 1

    msThin(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_ORD); // 1
    msTHIN(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_OP); // 2
    msMed(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_BIN); // 3
    msThick(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_REL); // 4
    msThin(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_OPEN); // 1
    msZero(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_CLOSE); // 0
    msThin(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_PUNCT); // 1
    msThin(Noad.SPACING_TYPE_INNER, Noad.SPACING_TYPE_INNER); // 1
  }

  {
    initMathSpacing();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Implementation of ?
   */

  // TeXCharMapper

  public int catCode(int c) {
    return cat_code.intVal(c);
  }

  public int lcNumCode(int c) {
    return lc_code.intVal(c);
  }

  public int ucNumCode(int c) {
    return uc_code.intVal(c);
  }

  public int spaceFactor(int c) {
    return sf_code.intVal(c);
  }

  public int mathCode(int c) {
    return math_code.intVal(c);
  }

  public int delCode(int c) {
    return del_code.intVal(c);
  }

  public int escapeNumCode() {
    return escape_char.intVal();
  }

  public int newLineNumCode() {
    return new_line_char.intVal();
  }

  public int endLineNumCode() {
    return end_line_char.intVal();
  }

  // TeXIOHandler

  public Calendar date() {
    return new GregorianCalendar(
        year.intVal(), month.intVal() - 1, day.intVal(), time.intVal() / 60, time.intVal() % 60);
  }

  public int errContextLines() {
    return error_context_lines.intVal() + 1;
  }

  public TokenList errHelp() {
    return err_help.get();
  }

  public boolean confirmingLines() {
    return (pausing.intVal() > 0);
  }

  public int magnification() {
    return mag.intVal();
  }

  // TeXFontHandler

  public Num defaultHyphenChar() {
    return default_hyphen_char.get();
  }

  public Num defaultSkewChar() {
    return default_skew_char.get();
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public Command.Config getCommandConfig() {
    return this;
  }

  public TypoCommand.Config getTypoConfig() {
    return this;
  }

  public MathPrim.Config getMathConfig() {
    return this;
  }

  public TeXCharMapper.Config getCharMapConfig() {
    return this;
  }

  public TeXIOHandler.Config getIOHandConfig() {
    return this;
  }

  public TeXFontHandler.Config getFontHandConfig() {
    return this;
  }
}
