/*  This file is part of TenBlocks.
    Copyright (C) 1999-2008 Philip Dorrell
    Email: http://www.1729.com/email.html

    TenBlocks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 3 
    as published by the Free Software Foundation.

    TenBlocks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package philipdorrell.tenblocks;
import java.awt.Component;
import java.awt.Font;

public class TenBlocks
{
    public static Font getMonospacedFont(Component component, int i,
					 int i_0_) {
	String[] strings = component.getToolkit().getFontList();
	for (int i_1_ = 0; i_1_ < strings.length; i_1_++) {
	    if (strings[i_1_].equals("Monospaced"))
		return new Font("Monospaced", i, i_0_);
	}
	return new Font("Courier", i, i_0_);
    }
    
    public static void main(String[] strings) {
	TenBlocksPanel.main(strings);
    }
}
