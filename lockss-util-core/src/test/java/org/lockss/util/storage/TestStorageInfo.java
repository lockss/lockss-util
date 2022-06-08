/*

 Copyright (c) 2019-2020 Board of Trustees of Leland Stanford Jr. University,
 all rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Except as contained in this notice, the name of Stanford University shall not
 be used in advertising or otherwise to promote the sale, use or other dealings
 in this Software without prior written authorization from Stanford University.

 */

package org.lockss.util.storage;


import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import java.math.BigInteger;
import org.junit.jupiter.api.*;
import org.lockss.log.L4JLogger;
import org.lockss.util.ListUtil;
import org.lockss.util.os.*;
import org.lockss.util.test.LockssTestCase5;

public class TestStorageInfo extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  private StorageInfo identifier;

  @Test
  public void testFromDF() throws Exception {
    String tmpdir = getTempDir().toString();
    PlatformUtil.DF df = PlatformUtil.getInstance().getDF(tmpdir);
    StorageInfo si = StorageInfo.fromDF(df);
    assertEquals(df.getSize(), biDivToLong(si.getSize(), 1024)); // From DF in KB.
    assertEquals("disk", si.getType());
    assertEquals(df.getMnt(), si.getName());
    assertEquals(df.getUsed(), biDivToLong(si.getUsed(), 1024)); // From DF in KB.
    assertEquals(df.getAvail(), biDivToLong(si.getAvail(), 1024)); // From DF in KB.
    assertEquals(df.getPercentString(), si.getPercentUsedString());

    StorageInfo si2 = StorageInfo.fromDF("notdisk", df);
    assertFalse(si.isSameDevice(si2));
  }

  static long biDivToLong(BigInteger numer, long denom) {
    return numer.divide(BigInteger.valueOf(denom)).longValue();
  }

  static boolean greaterZero(BigInteger bi) {
    return bi.compareTo(BigInteger.valueOf(0)) > 0;
  }

  @Test
  public void testFromRuntime() throws Exception {
    StorageInfo si = StorageInfo.fromRuntime();
    assertTrue(greaterZero(si.getSize()));
    assertTrue(si.getSize().compareTo(si.getAvail()) > 0);
    assertTrue(si.getSize().compareTo(si.getUsed()) > 0);
  }

  @Test
  public void testSerializeStorageInfo() throws Exception {
    StorageInfo sic1 = new StorageInfo()
      .setName("sic1")
      .setType("sic1type")
      .setSize(BigInteger.valueOf(2L<<18))
      .setUsed(2L<<20);
    StorageInfo sic2 = new StorageInfo()
      .setName("sic2")
      .setType("sic2type")
      .setSize(BigInteger.valueOf(2L<<10))
      .setUsed(2L<<11);

    StorageInfo si = new StorageInfo()
      .setName("siname")
      .setType("sitype")
      .setPath("sipath")
      .setSize(BigInteger.valueOf(2L<<40))
      .setUsed(2L<<38)
      .setAvail(123456)
      .setPercentUsedString("66%")
      .setPercentUsed(0.66)
      .setComponents(ListUtil.list(sic1, sic2));
    log.fatal("size: {}", si.getSize());

    String sistr = om().writeValueAsString(si);

    StorageInfo sitest =
      om().readValue(sistr,
                                   new TypeReference<StorageInfo>(){});
    assertEquals(si, sitest);

    si.setSize(new BigInteger("36893488147419103232"));
    assertEquals(new BigInteger("36893488147419103232"), si.getSize());
    log.fatal("size: {}", si.getSize());
    BigInteger two_to_64 = new BigInteger("18446744073709551616");
    assertTrue(si.getSize().compareTo(two_to_64) > 0);
    sistr = om().writeValueAsString(si);
    log.fatal("json: {}", sistr);
    sitest =
      om().readValue(sistr,
                                   new TypeReference<StorageInfo>(){});
    assertEquals(si, sitest);
  }

  ObjectMapper om() {
    return new ObjectMapper()
      .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
  }
}
