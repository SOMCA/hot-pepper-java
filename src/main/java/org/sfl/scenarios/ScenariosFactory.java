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

package org.sfl.scenarios;

public class ScenariosFactory {

    public Scenarios scenariosInstance(String type, String[] args)
    {
        if (type.equals("Calabash"))
        {
            return new CalabashScenarios(args[0], args[1], Integer.valueOf(args[2]), args[3]);
        }
        else if (type.equals("Espresso"))
        {
            return new EspressoScenarios(args[0], args[1], Integer.valueOf(args[2]), args[3]);
        }
        return null;
    }
}
