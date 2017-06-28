package binarygamelogic;

public class GameGridCell {
	private byte cellValue;
	private int rowInd;
	private int colInd;
	private boolean isFixed;
	private boolean isInASerie;
	private boolean isInValidRow;
	private boolean isInValidColumn;
	private boolean isInIdenticalRow;
	private boolean isInIdenticalColumn;
	
	public GameGridCell(byte cellValue, int rowInd, int colInd, boolean isFixed, boolean isInASerie, boolean isInValidRow, boolean isInValidColumn, boolean isInIdenticalRow, boolean isInIdenticalColumn) {
		this.cellValue = cellValue;
		this.rowInd = rowInd;
		this.colInd = colInd;
		this.isFixed = isFixed;
		this.isInASerie = isInASerie;
		this.isInValidRow = isInValidRow;
		this.isInValidColumn = isInValidColumn;
		this.isInIdenticalRow = isInIdenticalRow;
		this.isInIdenticalColumn = isInIdenticalColumn;
	}
	
	public byte getCellValue() {
		return cellValue;
	}
	
	public int getRowInd() {
		return rowInd;
	}
	
	public int getColInd() {
		return colInd;
	}
	
	public boolean isCellFixed() {
		return isFixed;
	}
	
	public boolean isCellInASerie() {
		return isInASerie;
	}
	
	public boolean isInValidRow() {
		return isInValidRow;
	}
	
	public boolean isInValidColumn() {
		return isInValidColumn;
	}
	
	public boolean isInIdenticalRow() {
		return isInIdenticalRow;
	}
	
	public boolean isInIdenticalColumn() {
		return isInIdenticalColumn;
	}
}
