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
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TenBlocksPanel extends Panel implements ActionListener
{
    TenBlocksGrid tenBlocksGrid = new TenBlocksGrid(30, 30, 2, 2, 4, 4);
    Button removeButton = new Button("Remove Block");
    Button giveupButton = new Button("Give up on this one for now");
    Button startAgainButton = new Button("Start Again");
    Button aboutButton = new Button("About TenBlocks ...");
    Label copyrightLabel
	= new Label("Copyright \u00a9 1999-2008 Philip John Dorrell");
    TimesChooser timesChooser
	= new TimesChooser(new Font("Courier", 1, 14), 3, 3);
    Label topLabel = new Label("Choose first problem");
    Label blocksLabel = new Label("");
    
    public TenBlocksPanel() {
	timesChooser.setActionListener(this);
	tenBlocksGrid.setActionListener(this);
	removeButton.setEnabled(tenBlocksGrid.getAnySelected());
	removeButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent actionevent) {
		tenBlocksGrid.deleteSelectedBlock();
	    }
	});
	tenBlocksGrid.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent itemevent) {
		removeButton.setEnabled(tenBlocksGrid.getAnySelected());
	    }
	});
	giveupButton.setEnabled(false);
	giveupButton.addActionListener(this);
	startAgainButton.setEnabled(false);
	startAgainButton.addActionListener(this);
	aboutButton.addActionListener(this);
	makeLayout();
    }
    
    public void actionPerformed(ActionEvent actionevent) {
	Object object = actionevent.getSource();
	if (object == timesChooser) {
	    tenBlocksGrid.setXY(timesChooser.getX(), timesChooser.getY());
	    giveupButton.setEnabled(true);
	    removeButton.setEnabled(false);
	    topLabel.setText(" " + tenBlocksGrid.getX() + " \u00d7 "
			     + tenBlocksGrid.getY() + " = ?");
	    setBlocksLabel();
	} else if (object == tenBlocksGrid) {
	    int i = actionevent.getID();
	    if (i == 1)
		setBlocksLabel();
	    else if (i == 0) {
		int i_2_ = tenBlocksGrid.getX();
		int i_3_ = tenBlocksGrid.getY();
		setBlocksLabel();
		timesChooser.sayFinished();
		boolean bool = timesChooser.allFinished();
		topLabel.setText(" " + i_2_ + " \u00d7 " + i_3_ + " = "
				 + i_2_ * i_3_ + "   "
				 + (bool ? "CONGRATULATIONS !!!!"
				    : "Choose next problem"));
		removeButton.setEnabled(false);
		giveupButton.setEnabled(false);
		startAgainButton.setEnabled(bool);
	    }
	} else if (object == giveupButton) {
	    tenBlocksGrid.setEmpty();
	    timesChooser.giveup();
	    blocksLabel.setText("");
	    topLabel.setText(" Choose next problem");
	    giveupButton.setEnabled(false);
	    removeButton.setEnabled(false);
	} else if (object == startAgainButton) {
	    tenBlocksGrid.setEmpty();
	    timesChooser.clear();
	    blocksLabel.setText("");
	    topLabel.setText(" Choose first problem");
	    giveupButton.setEnabled(false);
	    removeButton.setEnabled(false);
	} else if (object == aboutButton)
	    new About(false);
    }
    
    public static void main(String[] strings) {
	TenBlocksPanel tenblockspanel = new TenBlocksPanel();
	Frame frame = new Frame("Ten Blocks");
	frame.add(tenblockspanel);
	frame.pack();
	frame.show();
	frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent windowevent) {
		System.exit(0);
	    }
	});
    }
    
    void makeLayout() {
	this.setLayout(new BorderLayout());
	Panel panel = new Panel();
	panel.setLayout(new BorderLayout());
	topLabel.setFont(new Font("Times", 1, 18));
	panel.add("North", topLabel);
	Panel panel_4_ = new Panel();
	Panel panel_5_ = new Panel();
	panel_5_.setLayout(new BorderLayout());
	panel_5_.add("Center", tenBlocksGrid);
	blocksLabel.setFont(new Font("Times", 1, 16));
	panel_5_.add("South", blocksLabel);
	panel_4_.add(panel_5_);
	Panel panel_6_ = new Panel();
	panel_6_.setLayout(new BorderLayout());
	panel_6_.add("Center", timesChooser);
	Panel panel_7_ = new Panel();
	panel_7_.setLayout(new GridLayout(2, 1));
	Panel panel_8_ = new Panel();
	panel_8_.add(removeButton);
	panel_8_.add(startAgainButton);
	panel_7_.add(panel_8_);
	Panel panel_9_ = new Panel();
	panel_9_.add(giveupButton);
	panel_7_.add(panel_9_);
	panel_6_.add("South", panel_7_);
	panel_4_.add(panel_6_);
	panel.add("Center", panel_4_);
	this.add("Center", panel);
	Panel panel_10_ = new Panel();
	panel_10_.setLayout(new FlowLayout(0));
	copyrightLabel.setFont(new Font("Helvetica", 0, 10));
	panel_10_.add(copyrightLabel);
	aboutButton.setFont(new Font("Helvetica", 0, 10));
	panel_10_.add(aboutButton);
	this.add("South", panel_10_);
    }
    
    public void setBlocksLabel() {
	int i = tenBlocksGrid.getX();
	int i_11_ = tenBlocksGrid.getY();
	int i_12_ = tenBlocksGrid.getNumBlocks();
	int i_13_ = i * i_11_ - i_12_ * 10;
	if (i_13_ < 10)
	    blocksLabel.setText(" " + i_12_ + " ten" + (i_12_ == 1 ? "" : "s")
				+ " and " + i_13_ + " left over");
	else
	    blocksLabel.setText(" " + i_12_ + " ten" + (i_12_ == 1 ? ""
							: "s"));
    }
    
    public void setXY(int i, int i_14_) {
	tenBlocksGrid.setXY(i, i_14_);
    }
}
