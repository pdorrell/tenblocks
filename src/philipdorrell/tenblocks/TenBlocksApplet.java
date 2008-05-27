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
import java.applet.Applet;

public class TenBlocksApplet extends Applet
{
    public void init() {
	this.add(new TenBlocksPanel());
    }
}
