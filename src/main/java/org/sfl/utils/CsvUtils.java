/**
 *  Copyright (C) 2014-2016 Savoir-faire Linux Inc.
 *
 *  Author : Mehdi Ait-Younes (overpex) <overpex@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA.
 */

package org.sfl.utils;

import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CsvUtils {
    public static void testWriter(@Nullable  String logPath, LinkedHashMap<Long, Double> data, int run){
        if (logPath == null) logPath = System.getProperty("user.home");
        Path toSave = Paths.get(logPath+"/measurement_run_"+run+".csv");
        List<String> lines = new ArrayList<String>();
        try {
            for (Map.Entry<Long, Double> e : data.entrySet()) {
                String dataFormat = e.getKey().toString()+","+ e.getValue().toString();
                lines.add(dataFormat);
            }
            Files.write(toSave, lines, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Measurements data saved");
    }
}
