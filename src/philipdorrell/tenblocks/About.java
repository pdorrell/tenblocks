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
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class About extends Frame
{
    TextArea textArea = new TextArea(20, 80);
    Button okButton = new Button("OK");
    boolean developing;
    
    public About(boolean bool) {
	super("About TenBlocks");
	developing = bool;
	this.add("Center", textArea);
	textArea.setEditable(false);
	textArea.setBackground(Color.white);
	writeText();
	Panel panel = new Panel();
	okButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent actionevent) {
		About.this.dispose();
	    }
	});
	panel.add(okButton);
	this.add("South", panel);
	this.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent windowevent) {
		About.this.dispose();
	    }
	});
	this.pack();
	this.show();
    }
    
    void w(String string) {
	textArea.append(string + "\n");
    }
    
    void writeLicenceFile() {
	InputStream inputstream
	    = TenBlocks.class.getResourceAsStream("licence.txt");
	if (inputstream == null) {
	    w("WARNING: Licence resource licence.txt is missing;");
	    w("This software has been illegally altered and may not be used, ");
	    w("OR a bug in the browser prevents the software accessing resources, ");
	    w("in which case read the file http://www.1729.com/applets/licence.txt");
	} else {
	    BufferedReader bufferedreader
		= new BufferedReader(new InputStreamReader(inputstream));
	    try {
		String string;
		while ((string = bufferedreader.readLine()) != null)
		    w(string);
		bufferedreader.close();
		inputstream.close();
	    } catch (IOException ioexception) {
		w("IOException reading licence.txt resource");
		w("  " + ioexception.getMessage());
	    }
	}
    }
    
    void writeText() {
	textArea.setFont(TenBlocks.getMonospacedFont(this, 0, 12));
	w("TenBlocks version 1.1");
	w("-------------------------------------------------------");
	writeLicenceFile();
    }
}
