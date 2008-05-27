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
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TimesChooser extends Canvas
{
    public static final Color mouseDownColor = Color.gray;
    public static final Color doingColor = Color.cyan;
    public static final Color doneColor = new Color(102, 255, 102);
    public static final Color notDoneColor = Color.white;
    public static final int CHOOSE_ACTION = 0;
    boolean mouseDown = false;
    ActionListener actionListener = null;
    int selectedX;
    int selectedY;
    int numToDo;
    Font font;
    int margin;
    int lineWidth;
    int ascent;
    int cellWidth;
    int cellHeight;
    int totalWidth;
    int totalHeight;
    Times[][] problems = new Times[6][8];
    Image bufferImage;
    MouseListener mouseListener = new MouseAdapter() {
	public void mousePressed(MouseEvent mouseevent) {
	    if (selectedX == -1 && !mouseDown) {
		Point point = getMousePos(mouseevent);
		if (point != null) {
		    int i = point.x;
		    int i_0_ = point.y;
		    if (problems[i][i_0_] != null && !problems[i][i_0_].done) {
			mouseDown = true;
			selectedX = i;
			selectedY = i_0_;
			Graphics graphics = TimesChooser.this.getGraphics();
			paintCell(graphics, i, i_0_);
			graphics.dispose();
		    }
		}
	    }
	}
	
	public void mouseReleased(MouseEvent mouseevent) {
	    if (mouseDown) {
		Point point = getMousePos(mouseevent);
		Graphics graphics = TimesChooser.this.getGraphics();
		if (point != null && selectedX == point.x
		    && selectedY == point.y) {
		    mouseDown = false;
		    paintCell(graphics, selectedX, selectedY);
		    if (actionListener != null)
			actionListener.actionPerformed
			    (new ActionEvent(TimesChooser.this, 0, "choose"));
		} else {
		    int i = selectedX;
		    int i_1_ = selectedY;
		    selectedX = -1;
		    selectedY = -1;
		    mouseDown = false;
		    paintCell(graphics, i, i_1_);
		}
		graphics.dispose();
	    }
	}
    };
    
    class Times
    {
	public int x;
	public int y;
	public boolean done;
	public String string;
	
	public Times(int i, int i_2_) {
	    x = i;
	    y = i_2_;
	    string = String.valueOf(i) + "\u00d7" + i_2_;
	    done = false;
	}
	
	public boolean goodProblem() {
	    return x * y >= 10 && y >= x;
	}
    }
    
    public TimesChooser(Font font, int i, int i_3_) {
	this.font = font;
	margin = i;
	lineWidth = i_3_;
	for (int i_4_ = 0; i_4_ < 6; i_4_++) {
	    for (int i_5_ = 0; i_5_ < 8; i_5_++) {
		problems[i_4_][i_5_] = new Times(i_5_ + 2, i_4_ + 4);
		if (!problems[i_4_][i_5_].goodProblem())
		    problems[i_4_][i_5_] = null;
	    }
	}
	FontMetrics fontmetrics
	    = Toolkit.getDefaultToolkit().getFontMetrics(font);
	int i_6_ = fontmetrics.stringWidth("8\u00d78");
	cellWidth = i_6_ + 2 * i + i_3_;
	totalWidth = cellWidth * 6 + i_3_;
	ascent = fontmetrics.getAscent();
	int i_7_ = ascent + fontmetrics.getDescent();
	cellHeight = i_7_ + 2 * i + i_3_;
	totalHeight = cellHeight * 8 + i_3_;
	this.setSize(totalWidth, totalHeight);
	this.addMouseListener(mouseListener);
	clear();
    }
    
    public boolean allFinished() {
	return numToDo == 0;
    }
    
    public void clear() {
	selectedX = -1;
	selectedY = -1;
	numToDo = 0;
	for (int i = 0; i < 6; i++) {
	    for (int i_8_ = 0; i_8_ < 8; i_8_++) {
		if (problems[i][i_8_] != null) {
		    problems[i][i_8_].done = false;
		    numToDo++;
		}
	    }
	}
	this.repaint();
    }
    
    Point getMousePos(MouseEvent mouseevent) {
	int i = mouseevent.getX() - lineWidth;
	int i_9_ = mouseevent.getY() - lineWidth;
	if (i >= 0 && i_9_ >= 0) {
	    int i_10_ = i / cellWidth;
	    int i_11_ = i_9_ / cellHeight;
	    if (i_10_ < 6 && i_11_ < 8)
		return new Point(i_10_, i_11_);
	}
	return null;
    }
    
    public int getX() {
	return problems[selectedX][selectedY].x;
    }
    
    public int getY() {
	return problems[selectedX][selectedY].y;
    }
    
    public void giveup() {
	if (selectedX != -1 && selectedY != -1) {
	    int i = selectedX;
	    int i_12_ = selectedY;
	    selectedX = -1;
	    selectedY = -1;
	    Graphics graphics = this.getGraphics();
	    paintCell(graphics, i, i_12_);
	    graphics.dispose();
	}
    }
    
    public void paint(Graphics graphics) {
	for (int i = 0; i < 6; i++) {
	    for (int i_13_ = 0; i_13_ < 8; i_13_++)
		paintCell(graphics, i, i_13_);
	}
	graphics.setColor(Color.black);
	graphics.fillRect(0, 0, totalWidth, lineWidth);
	graphics.fillRect(0, 0, lineWidth, totalHeight);
    }
    
    public void paintCell(Graphics graphics, int i, int i_14_) {
	if (i == selectedX && i_14_ == selectedY)
	    paintCell(graphics, i, i_14_,
		      mouseDown ? mouseDownColor : doingColor);
	else if (problems[i][i_14_] == null)
	    paintCell(graphics, i, i_14_, notDoneColor);
	else
	    paintCell(graphics, i, i_14_,
		      problems[i][i_14_].done ? doneColor : notDoneColor);
    }
    
    public void paintCell(Graphics graphics, int i, int i_15_, Color color) {
	if (bufferImage == null)
	    bufferImage = this.createImage(cellWidth, cellHeight);
	Graphics graphics_16_ = bufferImage.getGraphics();
	paintCellLocal(graphics_16_, i, i_15_, color);
	graphics_16_.dispose();
	graphics.drawImage(bufferImage, lineWidth + i * cellWidth,
			   lineWidth + i_15_ * cellHeight, this);
    }
    
    void paintCellLocal(Graphics graphics, int i, int i_17_, Color color) {
	Times times = problems[i][i_17_];
	graphics.setColor(color);
	graphics.fillRect(0, 0, cellWidth, cellHeight);
	graphics.setColor(Color.black);
	graphics.fillRect(0, cellHeight - lineWidth, cellWidth, lineWidth);
	graphics.fillRect(cellWidth - lineWidth, 0, lineWidth, cellHeight);
	if (times != null) {
	    graphics.setFont(font);
	    graphics.drawString(times.string, margin, margin + ascent);
	}
    }
    
    public void sayFinished() {
	if (selectedX != -1 && selectedY != -1) {
	    problems[selectedX][selectedY].done = true;
	    numToDo--;
	    int i = selectedX;
	    int i_18_ = selectedY;
	    selectedX = -1;
	    selectedY = -1;
	    Graphics graphics = this.getGraphics();
	    paintCell(graphics, i, i_18_);
	    graphics.dispose();
	}
    }
    
    public void setActionListener(ActionListener actionlistener) {
	actionListener = actionlistener;
    }
    
    public void update(Graphics graphics) {
	paint(graphics);
    }
}
