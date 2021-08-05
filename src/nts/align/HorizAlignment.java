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
// Filename: nts/align/HorizAlignment.java
// $Id: HorizAlignment.java,v 1.1.1.1 2001/03/21 08:17:11 ksk Exp $
package nts.align;

import nts.base.Dimen;
import nts.base.Glue;
import nts.builder.Builder;
import nts.builder.HBoxBuilder;
import nts.builder.ListBuilder;
import nts.command.Token;
import nts.command.TokenList;
import nts.io.Log;
import nts.node.AnyBoxNode;
import nts.node.AnySkipNode;
import nts.node.BoxSizes;
import nts.node.GlueSetting;
import nts.node.HBoxNode;
import nts.node.HSkipNode;
import nts.node.HorizIterator;
import nts.node.NamedHSkipNode;
import nts.node.NodeEnum;
import nts.node.NodeList;
import nts.node.SizesSummarizer;
import nts.typo.TypoCommand;

public class HorizAlignment extends Alignment {

  protected final ListBuilder builder;

  public HorizAlignment(
      Dimen size,
      boolean exactly,
      TokenList.Inserter everyCr,
      Token frzCr,
      Token frzEndt,
      ListBuilder builder) {
    super(size, exactly, everyCr, frzCr, frzEndt);
    this.builder = builder;
  }

  protected HBoxBuilder rowBuilder;
  protected HBoxBuilder spanBuilder;
  protected NodeList rowMigrations;

  /* TeXtp[786] */
  protected void pushNewRowBuilder() {
    // XXX[786] space_factor = 0
    rowBuilder = new HAlignBuilder(currLineNumber());
    rowMigrations = new NodeList();
    Builder.push(rowBuilder);
  }

  /* TeXtp[787] */
  protected void pushNewSpanBuilder() {
    spanBuilder = new HBoxBuilder(currLineNumber());
    Builder.push(spanBuilder);
  }

  protected void addNamedSkipToRow(Glue skip, String name) {
    rowBuilder.addNamedSkip(skip, name);
  }

  /* TeXtp[796] */
  protected Dimen packedSpanSize(int spanCount) {
    NodeList list = spanBuilder.getList();
    Builder.pop();
    spanBuilder = null;
    if (builder.wantsMigrations()) rowMigrations.append(list.extractedMigrations());
    SizesSummarizer pack = new SizesSummarizer();
    HorizIterator.summarize(list.nodes(), pack);
    Dimen size = pack.getBody().plus(pack.getDepth());
    BoxSizes sizes = new BoxSizes(pack.getWidth(), size, pack.getLeftX(), pack.getHeight());
    byte strOrder = pack.maxTotalStr();
    byte shrOrder = pack.maxTotalShr();
    rowBuilder.addNode(
        new AnyUnsetNode(
            sizes,
            list,
            spanCount,
            pack.getTotalStr(strOrder),
            strOrder,
            pack.getTotalShr(shrOrder),
            shrOrder));
    return size;
  }

  /* TeXtp[799] */
  protected void packRow() {
    NodeList list = rowBuilder.getList();
    Builder.pop();
    rowBuilder = null;
    TypoCommand.appendBox(
        builder,
        new AnyUnsetNode(HorizIterator.naturalSizes(list.nodes()), list),
        rowMigrations.nodes());
    rowMigrations = NodeList.NULL;
  }

  protected NodeEnum getUnsetNodes() {
    return builder.getList().nodes();
  }

  protected Dimen getRelevantSize(BoxSizes sizes) {
    return sizes.getWidth();
  }

  protected BoxSizes transformSizes(BoxSizes sizes, Dimen dim) {
    return sizes.withWidth(dim);
  }

  protected AnyBoxNode makeBox(BoxSizes sizes, GlueSetting setting, NodeList list) {
    return new HBoxNode(sizes, setting, list);
  }

  protected AnySkipNode makeSkip(Glue skip) {
    return new HSkipNode(skip);
  }

  protected AnySkipNode makeSkip(Glue skip, String name) {
    return new NamedHSkipNode(skip, name);
  }

  protected TypoCommand.AnyBoxPacker makeBoxPacker() {
    return new TypoCommand.HBoxPacker() {
      /* TeXtp[663] */
      protected void reportLocation(Log log) {
        log.add("in alignment at lines ")
            .add(builder.getStartLine())
            .add("--")
            .add(currLineNumber());
      }
    };
  }

  public void copyPrevParameters(Builder bld) {
    bld.setPrevDepth(builder.getPrevDepth());
  }
}
