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
import java.awt.AWTEventMulticaster;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

public class TenBlocksGrid extends Canvas implements ItemSelectable
{
    int squareWidth;
    int squareHeight;
    int lineWidth;
    int lineHeight;
    int edgeWidth;
    int edgeHeight;
    int numSquaresLeft;
    int numBlocks;
    int X;
    int Y;
    int top;
    int width;
    int height;
    int totalWidth;
    int totalHeight;
    public static final int maxRows = 9;
    public static final int maxCols = 9;
    public static final int FINISHED_ACTION = 0;
    public static final int CHANGE_ACTION = 1;
    BlockInGrid draggedBlock = null;
    boolean draggedBlockPlaceable;
    Point dragStart = null;
    Point dragEnd = null;
    Rectangle draggedRectangle = null;
    public static final Color lineColor = new Color(153, 153, 153);
    public static final Color backgroundColor = Color.yellow;
    public static final Color finishedBackgroundColor = Color.white;
    public static final Color outsideColor = new Color(204, 204, 204);
    public static final Color blockColor = Color.green;
    public static final Color finishedBlockColor = new Color(204, 153, 255);
    public static final Color selectedColor = Color.red;
    public static final Color edgeColor = Color.black;
    public static final Color dragEdgeColor = Color.blue;
    public static final Color dragBlockEdgeColor = new Color(102, 102, 255);
    public static final Color dragBlockColor = Color.cyan;
    ActionListener actionListener = null;
    BlockInGrid[] currentBlocks;
    int[][] blockPtrs;
    boolean anySelected;
    int selectedPtr;
    BlockInGrid selectedBlock = null;
    boolean finished;
    boolean empty;
    ItemListener itemListeners = null;
    public KeyListener keyListener = new KeyAdapter() {
	public void keyPressed(KeyEvent keyevent) {
	    int i = keyevent.getKeyCode();
	    if (i == 127 || i == 8)
		deleteSelectedBlock();
	}
    };
    public MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
	public void mouseDragged(MouseEvent mouseevent) {
	    if (!finished) {
		Point point = getDraggedMousePos(mouseevent);
		if (dragStart != null && point != dragEnd) {
		    dragEnd = point;
		    resetDragRectangle();
		    TenBlocksGrid.this.repaint();
		}
	    }
	}
    };
    public MouseListener mouseListener = new MouseAdapter() {
	public void mousePressed(MouseEvent mouseevent) {
	    if (!finished) {
		Point point = getNewMousePos(mouseevent);
		if (point != null) {
		    int i = blockPtrs[point.x][point.y];
		    if (i == -1) {
			dragStart = point;
			dragEnd = point;
			resetDragRectangle();
			TenBlocksGrid.this.repaint();
		    } else {
			BlockInGrid blockingrid = currentBlocks[i];
			if (anySelected) {
			    if (blockingrid == selectedBlock) {
				anySelected = false;
				blockingrid.selected = false;
				fireSelectedChange(blockingrid);
				selectedBlock = null;
			    } else {
				selectedBlock.selected = false;
				fireSelectedChange(selectedBlock);
				selectedPtr = i;
				selectedBlock = blockingrid;
				selectedBlock.selected = true;
				fireSelectedChange(selectedBlock);
			    }
			} else {
			    selectedPtr = i;
			    selectedBlock = blockingrid;
			    selectedBlock.selected = true;
			    anySelected = true;
			    fireSelectedChange(selectedBlock);
			}
			TenBlocksGrid.this.repaint();
		    }
		}
	    }
	    TenBlocksGrid.this.requestFocus();
	}
	
	public void mouseReleased(MouseEvent mouseevent) {
	    if (dragStart != null && !finished) {
		if (draggedBlock != null && draggedBlockPlaceable)
		    addBlock(draggedBlock);
		dragStart = null;
		dragEnd = null;
		draggedRectangle = null;
		draggedBlock = null;
		TenBlocksGrid.this.repaint();
	    }
	    TenBlocksGrid.this.requestFocus();
	}
    };
    Image bufferImage = null;
    Block[] blocks = new Block[16];
    Transformation swapXY = new Transformation() {
	public int[] transform(int[] is) {
	    return new int[] { is[1], is[0] };
	}
	
	public int transformPolygonX(int i, int i_0_) {
	    return -i_0_;
	}
	
	public int transformPolygonY(int i, int i_1_) {
	    return -i;
	}
    };
    Transformation reflectX = new Transformation() {
	public int[] transform(int[] is) {
	    return new int[] { -is[0], is[1] };
	}
	
	public int transformPolygonX(int i, int i_2_) {
	    return squareWidth - i;
	}
	
	public int transformPolygonY(int i, int i_3_) {
	    return i_3_;
	}
    };
    Transformation reflectY = new Transformation() {
	public int[] transform(int[] is) {
	    return new int[] { is[0], -is[1] };
	}
	
	public int transformPolygonX(int i, int i_4_) {
	    return i;
	}
	
	public int transformPolygonY(int i, int i_5_) {
	    return -squareHeight - i_5_;
	}
    };
    
    static interface Transformation
    {
	public int[] transform(int[] is);
	
	public int transformPolygonX(int i, int i_6_);
	
	public int transformPolygonY(int i, int i_7_);
    }
    
    public class Block
    {
	int[][] squares;
	int endX;
	int endY;
	Polygon edgePolygon;
	
	public Block(int i, int i_8_, int[][] is, Polygon polygon) {
	    endX = i;
	    endY = i_8_;
	    squares = is;
	    edgePolygon = polygon;
	}
	
	public boolean blockPtrsUntaken(int i, int i_9_) {
	    for (int i_10_ = 0; i_10_ < squares.length; i_10_++) {
		if (blockPtrs[i + squares[i_10_][0]][i_9_ + squares[i_10_][1]]
		    != -1)
		    return false;
	    }
	    return true;
	}
	
	public void paintEdge(Graphics graphics, int i, int i_11_,
			      Color color) {
	    graphics.setColor(color);
	    graphics.translate(i, i_11_);
	    graphics.fillPolygon(edgePolygon);
	    graphics.translate(-i, -i_11_);
	}
	
	public void paintInside(Graphics graphics, int i, int i_12_,
				Color color) {
	    graphics.setColor(color);
	    for (int i_13_ = 0; i_13_ < squares.length; i_13_++)
		fillSquare(graphics, i + squares[i_13_][0],
			   i_12_ + squares[i_13_][1]);
	}
	
	public void setBlockPtrs(int i, int i_14_, int i_15_) {
	    for (int i_16_ = 0; i_16_ < squares.length; i_16_++)
		blockPtrs[i + squares[i_16_][0]][i_14_ + squares[i_16_][1]]
		    = i_15_;
	}
	
	public Block transformed(Transformation transformation) {
	    int[] is = { endX, endY };
	    int[] is_17_ = transformation.transform(is);
	    int[][] is_18_ = new int[squares.length][];
	    for (int i = 0; i < is_18_.length; i++)
		is_18_[i] = transformation.transform(squares[i]);
	    int[] is_19_ = new int[edgePolygon.npoints];
	    int[] is_20_ = new int[edgePolygon.npoints];
	    for (int i = 0; i < edgePolygon.npoints; i++) {
		is_19_[i]
		    = transformation.transformPolygonX(edgePolygon.xpoints[i],
						       edgePolygon.ypoints[i]);
		is_20_[i]
		    = transformation.transformPolygonY(edgePolygon.xpoints[i],
						       edgePolygon.ypoints[i]);
	    }
	    return new Block(is_17_[0], is_17_[1], is_18_,
			     new Polygon(is_19_, is_20_, edgePolygon.npoints));
	}
    }
    
    class BlockInGrid
    {
	Block block;
	int x;
	int y;
	boolean selected;
	
	public BlockInGrid(Block block, int i, int i_21_) {
	    this.block = block;
	    x = i;
	    y = i_21_;
	    selected = false;
	}
	
	public boolean blockPtrsUntaken() {
	    return block.blockPtrsUntaken(x, y);
	}
	
	public void paintEdge(Graphics graphics) {
	    block.paintEdge(graphics, x * squareWidth,
			    totalHeight - y * squareHeight,
			    TenBlocksGrid.edgeColor);
	}
	
	public void paintEdge(Graphics graphics, Color color) {
	    block.paintEdge(graphics, x * squareWidth,
			    totalHeight - y * squareHeight, color);
	}
	
	public void paintInside(Graphics graphics) {
	    block.paintInside(graphics, x, y,
			      (selected ? TenBlocksGrid.selectedColor
			       : TenBlocksGrid.blockColor));
	}
	
	public void paintInside(Graphics graphics, Color color) {
	    block.paintInside(graphics, x, y, color);
	}
	
	public void setBlockPtrs(int i) {
	    block.setBlockPtrs(x, y, i);
	}
	
	void setSelected(boolean bool) {
	    selected = bool;
	}
    }
    
    public TenBlocksGrid(int i, int i_22_, int i_23_, int i_24_, int i_25_,
			 int i_26_) {
	squareWidth = i;
	squareHeight = i_22_;
	lineWidth = i_23_;
	lineHeight = i_24_;
	edgeWidth = i_25_;
	edgeHeight = i_26_;
	totalWidth = 9 * i;
	totalHeight = 9 * i_22_;
	this.setSize(totalWidth, totalHeight);
	setupBlocks();
	this.addMouseListener(mouseListener);
	this.addMouseMotionListener(mouseMotionListener);
	this.addKeyListener(keyListener);
	empty = true;
	finished = true;
    }
    
    void addBlock(BlockInGrid blockingrid) {
	int i = findUnusedBlockPtr();
	if (i != -1) {
	    blockingrid.setBlockPtrs(i);
	    currentBlocks[i] = blockingrid;
	    numSquaresLeft -= 10;
	    numBlocks++;
	    if (numSquaresLeft < 10) {
		finished = true;
		if (actionListener != null)
		    actionListener
			.actionPerformed(new ActionEvent(this, 0, "finished"));
	    } else if (actionListener != null)
		actionListener.actionPerformed(new ActionEvent(this, 1,
							       "change"));
	}
    }
    
    public void addItemListener(ItemListener itemlistener) {
	itemListeners = AWTEventMulticaster.add(itemListeners, itemlistener);
    }
    
    public Block blockOfSize(int i, int i_27_) {
	for (int i_28_ = 0; i_28_ < blocks.length; i_28_++) {
	    if (blocks[i_28_].endX == i && blocks[i_28_].endY == i_27_)
		return blocks[i_28_];
	}
	return null;
    }
    
    public void deleteSelectedBlock() {
	if (anySelected && !finished) {
	    selectedBlock.selected = false;
	    currentBlocks[selectedPtr] = null;
	    selectedBlock.setBlockPtrs(-1);
	    anySelected = false;
	    fireSelectedChange(selectedBlock);
	    numSquaresLeft += 10;
	    numBlocks--;
	    if (actionListener != null)
		actionListener.actionPerformed(new ActionEvent(this, 1,
							       "change"));
	    this.repaint();
	}
    }
    
    public void drawDraggedRect(Graphics graphics) {
	graphics.setColor(dragEdgeColor);
	graphics.fillRect(draggedRectangle.x, draggedRectangle.y, edgeWidth,
			  draggedRectangle.height);
	graphics.fillRect(draggedRectangle.x, draggedRectangle.y,
			  draggedRectangle.width, edgeHeight);
	graphics.fillRect(draggedRectangle.x,
			  (draggedRectangle.y + draggedRectangle.height
			   - edgeHeight),
			  draggedRectangle.width, edgeHeight);
	graphics.fillRect((draggedRectangle.x + draggedRectangle.width
			   - edgeWidth),
			  draggedRectangle.y, edgeWidth,
			  draggedRectangle.height);
    }
    
    public void fillSquare(Graphics graphics, int i, int i_29_) {
	graphics.fillRect(i * squareWidth,
			  totalHeight - (i_29_ + 1) * squareHeight,
			  squareWidth, squareHeight);
    }
    
    int findUnusedBlockPtr() {
	for (int i = 0; i < currentBlocks.length; i++) {
	    if (currentBlocks[i] == null)
		return i;
	}
	return -1;
    }
    
    protected void fireSelectedChange(BlockInGrid blockingrid) {
	if (itemListeners != null)
	    itemListeners.itemStateChanged(new ItemEvent(this, 701,
							 blockingrid,
							 (blockingrid.selected
							  ? 1 : 2)));
    }
    
    public boolean getAnySelected() {
	return anySelected;
    }
    
    Point getDraggedMousePos(MouseEvent mouseevent) {
	int i = mouseevent.getX() / squareWidth;
	int i_30_ = (totalHeight - mouseevent.getY()) / squareHeight;
	if (i < 0)
	    i = 0;
	else if (i >= X)
	    i = X - 1;
	if (i_30_ < 0)
	    i_30_ = 0;
	else if (i_30_ >= Y)
	    i_30_ = Y - 1;
	return new Point(i, i_30_);
    }
    
    Point getNewMousePos(MouseEvent mouseevent) {
	int i = mouseevent.getX() / squareWidth;
	int i_31_ = (totalHeight - mouseevent.getY()) / squareHeight;
	if (i >= 0 && i < X && i_31_ >= 0 && i_31_ < Y)
	    return new Point(i, i_31_);
	return null;
    }
    
    public int getNumBlocks() {
	return numBlocks;
    }
    
    public Object[] getSelectedObjects() {
	if (anySelected)
	    return new Object[] { selectedBlock };
	return null;
    }
    
    public int getX() {
	return X;
    }
    
    public int getY() {
	return Y;
    }
    
    public boolean isFocusTraversable() {
	return true;
    }
    
    public void paint(Graphics graphics) {
	graphics.setColor(outsideColor);
	if (empty)
	    graphics.fillRect(0, 0, totalWidth, totalHeight);
	else {
	    graphics.fillRect(0, 0, totalWidth, top);
	    graphics.fillRect(width, top, totalWidth - width, height);
	    Color color = finished ? finishedBackgroundColor : backgroundColor;
	    graphics.setColor(color);
	    graphics.fillRect(0, top, width, height);
	    if (finished) {
		for (int i = 0; i < currentBlocks.length; i++) {
		    if (currentBlocks[i] != null)
			currentBlocks[i].paintInside(graphics,
						     finishedBlockColor);
		}
	    } else {
		for (int i = 0; i < currentBlocks.length; i++) {
		    if (currentBlocks[i] != null)
			currentBlocks[i].paintInside(graphics);
		}
	    }
	    if (draggedBlock != null && draggedBlockPlaceable)
		draggedBlock.paintInside(graphics, dragBlockColor);
	    graphics.setColor(lineColor);
	    graphics.fillRect(0, top, lineWidth, height);
	    for (int i = 1; i < X; i++)
		graphics.fillRect(i * squareWidth - lineWidth, top,
				  lineWidth * 2, height);
	    graphics.fillRect(width - lineWidth, top, lineWidth, height);
	    graphics.fillRect(0, top, width, lineHeight);
	    for (int i = 1; i < Y; i++)
		graphics.fillRect(0, top + i * squareHeight - lineHeight,
				  width, lineHeight * 2);
	    graphics.fillRect(0, totalHeight - lineHeight, width, lineHeight);
	    for (int i = 0; i < currentBlocks.length; i++) {
		if (currentBlocks[i] != null)
		    currentBlocks[i].paintEdge(graphics);
	    }
	    if (draggedBlock != null)
		draggedBlock.paintEdge(graphics, dragBlockEdgeColor);
	    else if (draggedRectangle != null)
		drawDraggedRect(graphics);
	}
    }
    
    public void removeItemListener(ItemListener itemlistener) {
	itemlistener = AWTEventMulticaster.remove(itemListeners, itemlistener);
    }
    
    public void resetDragRectangle() {
	int i = dragEnd.x - dragStart.x;
	int i_32_ = dragEnd.y - dragStart.y;
	Block block = blockOfSize(i, i_32_);
	if (block != null) {
	    draggedBlock = new BlockInGrid(block, dragStart.x, dragStart.y);
	    draggedBlockPlaceable = draggedBlock.blockPtrsUntaken();
	    draggedRectangle = null;
	} else {
	    draggedBlock = null;
	    int i_33_ = Math.min(dragStart.x, dragEnd.x);
	    int i_34_ = Math.max(dragStart.x, dragEnd.x);
	    int i_35_ = 8 - Math.max(dragStart.y, dragEnd.y);
	    int i_36_ = 8 - Math.min(dragStart.y, dragEnd.y);
	    draggedRectangle
		= new Rectangle(i_33_ * squareWidth, i_35_ * squareHeight,
				(1 + i_34_ - i_33_) * squareWidth,
				(1 + i_36_ - i_35_) * squareHeight);
	}
    }
    
    public void setActionListener(ActionListener actionlistener) {
	actionListener = actionlistener;
    }
    
    public void setEmpty() {
	empty = true;
	finished = false;
	this.repaint();
    }
    
    public void setXY(int i, int i_37_) {
	empty = false;
	finished = false;
	X = i;
	Y = i_37_;
	top = (9 - i_37_) * squareHeight;
	width = i * squareWidth;
	height = i_37_ * squareHeight;
	blockPtrs = new int[i][i_37_];
	for (int i_38_ = 0; i_38_ < i; i_38_++) {
	    for (int i_39_ = 0; i_39_ < i_37_; i_39_++)
		blockPtrs[i_38_][i_39_] = -1;
	}
	currentBlocks = new BlockInGrid[10];
	for (int i_40_ = 0; i_40_ < currentBlocks.length; i_40_++)
	    currentBlocks[i_40_] = null;
	numSquaresLeft = i * i_37_;
	numBlocks = 0;
	anySelected = false;
	this.repaint();
    }
    
    void setupBlocks() {
	int i = edgeWidth;
	int i_41_ = edgeHeight;
	int i_42_ = squareWidth;
	int i_43_ = squareHeight;
	int i_44_ = i;
	int i_45_ = 2 * i_42_ - i;
	int i_46_ = 2 * i_42_;
	int i_47_ = -i_41_;
	int i_48_ = -(5 * i_43_ - i_41_);
	int i_49_ = -(5 * i_43_);
	blocks[0]
	    = new Block(1, 4,
			new int[][] { new int[2], { 0, 1 }, { 0, 2 }, { 0, 3 },
				      { 0, 4 }, { 1, 0 }, { 1, 1 }, { 1, 2 },
				      { 1, 3 }, { 1, 4 } },
			new Polygon(new int[] { i_44_, i_44_, i_45_, i_45_,
						i_44_, i_44_, 0, 0, i_46_,
						i_46_, 0 },
				    new int[] { 0, i_47_, i_47_, i_48_, i_48_,
						0, 0, i_49_, i_49_, 0, 0 },
				    11));
	int i_50_ = i;
	int i_51_ = 2 * i_42_;
	int i_52_ = 2 * i_42_ + i;
	int i_53_ = 3 * i_42_ - i;
	int i_54_ = 3 * i_42_;
	int i_55_ = -i_41_;
	int i_56_ = -(3 * i_43_ - i_41_);
	int i_57_ = -(3 * i_43_);
	int i_58_ = -(4 * i_43_ - i_41_);
	int i_59_ = -(4 * i_43_);
	blocks[1]
	    = new Block(2, 3,
			new int[][] { new int[2], { 0, 1 }, { 0, 2 }, { 1, 0 },
				      { 1, 1 }, { 1, 2 }, { 2, 0 }, { 2, 1 },
				      { 2, 2 }, { 2, 3 } },
			new Polygon(new int[] { i_50_, i_50_, i_53_, i_53_,
						i_52_, i_52_, i_50_, i_50_, 0,
						0, i_51_, i_51_, i_54_, i_54_,
						0 },
				    new int[] { 0, i_55_, i_55_, i_58_, i_58_,
						i_56_, i_56_, 0, 0, i_57_,
						i_57_, i_59_, i_59_, 0, 0 },
				    15));
	for (int i_60_ = 0; i_60_ < 2; i_60_++)
	    blocks[2 + i_60_] = blocks[i_60_].transformed(swapXY);
	for (int i_61_ = 0; i_61_ < 4; i_61_++)
	    blocks[4 + i_61_] = blocks[i_61_].transformed(reflectX);
	for (int i_62_ = 0; i_62_ < 8; i_62_++)
	    blocks[8 + i_62_] = blocks[i_62_].transformed(reflectY);
    }
    
    public void update(Graphics graphics) {
	if (bufferImage == null)
	    bufferImage = this.createImage(totalWidth, totalHeight);
	Graphics graphics_63_ = bufferImage.getGraphics();
	paint(graphics_63_);
	graphics_63_.dispose();
	graphics.drawImage(bufferImage, 0, 0, this);
    }
}
