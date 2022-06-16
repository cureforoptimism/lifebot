package com.cureforoptimism.lifebot;

import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Utilities {
  public static String simpleTableToString(SimpleTable simpleTable) {
    GridTable gridTable = simpleTable.toGrid();
    gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(baos);
    Util.print(gridTable, printStream);

    String response;
    response = baos.toString(StandardCharsets.UTF_8);

    return response;
  }
}
