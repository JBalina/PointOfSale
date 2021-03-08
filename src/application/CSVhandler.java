package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class CSVhandler {
	private String fileName;
	private Vector<Vector<String>> cells = new Vector<>();
	
	/* Constructor takes the name of a csv file.
	 * The file must be in a folder named "resources" in the current directory.
	 * CSVhandler reads the file and saves its contents as a vector of vectors of strings.
	 * */
	public CSVhandler(String newFileName) {
		try {
			File newFile = new File(System.getProperty("user.dir")+"/resources");
			newFile.mkdir();
			this.fileName = System.getProperty("user.dir")+"/resources/"+newFileName;
			newFile = new File(this.fileName);
			newFile.createNewFile();
			BufferedReader csvReader = new BufferedReader(new FileReader(this.fileName));
			String line = null;
			String value = "";
			while((line = csvReader.readLine()) != null) {
				boolean quote = false;
				Vector<String> row = new Vector<>();
				for(int i = 0; i < line.length(); i++) {
					if(line.charAt(i) == '"') {
						quote = !quote;
					}
					else if((line.charAt(i) == ',' || line.charAt(i) == '\n') && !quote) {
						row.add(value);
						value = "";
					}
					else {
						value += line.charAt(i);
					}
				}
				row.add(value);
				value = "";
				this.cells.add(row);
			}
			csvReader.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	/* Returns an string item, given its index in the csv file
	 * */
	public String readFromCsv(int row, int col) {
		return this.cells.get(row).get(col);
	}
	
	/* Replaces an item at the given indexes in the csv with the given string.
	 * This does NOT save until the save function is called.
	 * */
	public void writeToCsvCell(String item, int row, int col) {	
		this.cells.get(row).set(col, item);
	}
	
	/* This method rewrites the csv file using the stored vector of vectors.
	 * */
	public void save() throws IOException {
		FileWriter csv = new FileWriter(this.fileName);
		for(int i = 0; i < this.cells.size(); i++) {
			for(int j = 0; j < this.cells.get(0).size(); j++) {
				if (this.cells.get(i).get(j).indexOf(',') == -1 || this.cells.get(i).get(j).indexOf('"') == -1) {
					String item = "\"";
					for(int k = 0; k < this.cells.get(i).get(j).length(); k++) {
						if(this.cells.get(i).get(j).charAt(k) == '"') {
							item += "\"\"";
						}
						else {
							item += this.cells.get(i).get(j).charAt(k);
						}
					}
					item += '"';
					csv.write(item);
				}
				else {
					csv.write(this.cells.get(i).get(j));
				}
				if(j != this.cells.get(0).size()-1) {
					csv.write(',');
				}
			}
			csv.write('\n');
		}
		csv.close();
	}
	
	/* This method completely overwrites the contents of the csv with the contents of the
	 * given vector of vectors.
	 * */
	public void writeNewCsv(Vector<Vector<String>> newCsv) throws IOException {
		this.cells = newCsv;
		this.save();
	}
	
	/* Searches the contents of a certain column in the csv file.
	 * Returns the entire row if found, else returns null.
	 * */
	public Vector<String> searchByCol(String searchKey, int col) {
		for(int i = 0; i < this.cells.size(); i++) {
			if(this.cells.get(i).get(col).equals(searchKey)) {
				return this.cells.get(i);
			}
		}
		return null;
	}
	
	/* Searches the contents of a certain column in the csv file.
	 * Returns the index of row if found, else returns -1.
	 * */
	public int GetRowIndexBySearch(String searchKey, int col) {
		for(int i = 0; i < this.cells.size(); i++) {
			if(this.cells.get(i).get(col).equals(searchKey)) {
				return i;
			}
		}
		return -1;
	}
	
	/* Given an index, returns the desired row.
	 * */
	public Vector<String> GetRowByIndex(int i) {
		return this.cells.get(i);
	}
	
	/* Returns the number of rows in the csv file.
	 * */
	public int getNumRows() {
		return this.cells.size();
	}
	
	/* Returns the number of columns in the csv file.
	 * */
	public int getNumCols() {
		return this.cells.get(0).size();
	}
	
	/* Writes new items to the end of the csv file and saves.
	 * */
	public void appendToCsv(Vector<Vector<String>> items) throws IOException {
		for(int i = 0; i < items.size(); i++) {
			this.cells.add(items.get(i));
		}
		this.save();
	}
	
}
