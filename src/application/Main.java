/*
Written by Jonathan Balina


*/
package application;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage)
    {
		try {
			CSVhandler itemData = new CSVhandler("inventory.csv");
			CSVhandler receiptData = new CSVhandler("receiptData.csv");
			StringProperty itemNameValue = new SimpleStringProperty();
			StringProperty itemDscrValue = new SimpleStringProperty();
			StringProperty subtotalValue = new SimpleStringProperty();
			StringProperty taxValue = new SimpleStringProperty();
			StringProperty totalValue = new SimpleStringProperty();
			StringProperty paymentValue = new SimpleStringProperty();
			StringProperty changeValue = new SimpleStringProperty();
			StringProperty receiptNumValue = new SimpleStringProperty();
			StringProperty cashierValue = new SimpleStringProperty();
			StringProperty dateValue = new SimpleStringProperty();
			HBox[] currentRow = {null};
			
			
			

	    	int rowNum = 0;
	    	if(receiptData.getNumRows()>1) {
	    		rowNum = receiptData.getNumRows()-1;
	    		int nextNum = Integer.parseInt(receiptData.readFromCsv(rowNum, 0)) + 1;
	    		receiptNumValue.set(String.valueOf(nextNum));
	    	}
	    	else {
		    	receiptNumValue.set("1000");
		    }
			
			
			StackPane root = new StackPane();
			BorderPane mainRoot = new BorderPane();
			root.getChildren().add(mainRoot);
			Scene scene = new Scene(root, 500, 400);
			HBox titleBlock = new HBox();
			Label title = new Label("Point of Sale");
			titleBlock.setAlignment(Pos.CENTER);
			title.getStyleClass().add("title");
			titleBlock.getChildren().addAll(title);
			mainRoot.setTop(titleBlock);
			
			GridPane body = new GridPane();
			
			//ITEM ABOUT SECTION **********************
			GridPane itemAbout = new GridPane();
			
			Label itemName = new Label("");
			itemName.textProperty().bind(itemNameValue);
			Label itemDscr = new Label("");
			itemDscr.textProperty().bind(itemDscrValue);
			TextField price = new TextField();
			TextField quantity = new TextField();
			TextField scanItem = new TextField();
			ImageView itemPic = new ImageView();
			itemPic.setImage(null);
			itemPic.setFitHeight(60);
			itemPic.setFitWidth(98);
			
			
			itemAbout.add(new Label("Item Name:"), 0, 0, 1, 1);
			itemAbout.add(itemName, 1, 0, 2, 1);
			itemAbout.add(itemDscr, 0, 1, 3, 1);
			itemAbout.add(new Label("Price:"), 1, 3, 1, 1);
			itemAbout.add(price, 2, 3, 1, 1);
			itemAbout.add(new Label("Quantity:"), 1, 5, 1, 1);
			itemAbout.add(quantity, 2, 5, 1, 1);
			itemAbout.add(itemPic, 0, 3, 1, 3);
			itemAbout.add(new Label("Scan Item:"), 0, 7, 1, 1);
			itemAbout.add(scanItem, 1, 7, 2, 1);
			
			
			
			for(int i = 0; i < 8; i++) {
				RowConstraints con = new RowConstraints();
				con.setPrefHeight(20);
	            itemAbout.getRowConstraints().add(con);
			}
			
			for (Node child : itemAbout.getChildren()) {
			    Integer column = GridPane.getColumnIndex(child);
			    Integer row = GridPane.getRowIndex(child);
			    Integer width = GridPane.getColumnSpan(child);
			    if (column != null && row != null) {
			    	if(width == 2) {
			    		child.getStyleClass().add("gridCellNode-2wide");
			    	}
			    	else if(width == 3) {
			    		child.getStyleClass().add("gridCellNode-3wide");
			    	}
			    	else {
			    		child.getStyleClass().add("gridCellNode");
			    	}
			    }
			}
			itemAbout.getStyleClass().add("itemAboutGrid");
	        
			//NUMPAD SECTION ***************************
			
			GridPane numPad = new GridPane();

			String[] keys =
			{
				"1", "2", "3",
				"4", "5", "6",
				"7", "8", "9",
				"0", "CLEAR", "."
			};
			
			Button[] numPadButtons = new Button[12];
	        
			for (int i = 0; i < 12; i++)
			{
				Button button = new Button(keys[i]);
				button.getStyleClass().add("num-button");
				numPadButtons[i] = button;
				numPad.add(button, i % 3, (int) Math.ceil(i / 3));
			}
			numPad.setHgap(1);
			numPad.setVgap(1);
			
			
			//PRICING SECTION **************************
			GridPane pricing = new GridPane();
			
			Label subtotal = new Label("");
			subtotal.textProperty().bind(subtotalValue);
			Label tax = new Label("");
			tax.textProperty().bind(taxValue);
			Label total = new Label("");
			total.textProperty().bind(totalValue);
			TextField payType = new TextField();
			TextField payment = new TextField();
			Label change = new Label();
			change.textProperty().bind(changeValue);
			
			pricing.add(new Label("Subtotal:"), 0, 0, 1, 1);
			pricing.add(subtotal, 1, 0, 1, 1);
			pricing.add(new Label("Tax:"), 0, 1, 1, 1);
			pricing.add(tax, 1, 1, 1, 1);
			pricing.add(new Label("Total:"), 0, 2, 1, 1);
			pricing.add(total, 1, 2, 1, 1);
			pricing.add(new Label("Pay Type:"), 0, 3, 1, 1);
			pricing.add(payType, 1, 3, 1, 1);
			pricing.add(new Label("Payment:"), 0, 4, 1, 1);
			pricing.add(payment, 1, 4, 1, 1);
			pricing.add(new Label("Change:"), 0, 5, 1, 1);
			pricing.add(change, 1, 5, 1, 1);
			
			for (Node child : pricing.getChildren()) {
			    Integer column = GridPane.getColumnIndex(child);
			    Integer row = GridPane.getRowIndex(child);
			    Integer width = GridPane.getColumnSpan(child);
			    if (column != null && row != null) {
			    	if(width == 2) {
			    		child.getStyleClass().add("gridCellNode-2wide");
			    	}
			    	else if(width == 3) {
			    		child.getStyleClass().add("gridCellNode-3wide");
			    	}
			    	else {
			    		child.getStyleClass().add("gridCellNode");
			    	}
			    }
			}
			
			
			
			//PAYMENT BUTTONS SECTION *************************
			GridPane paymentButtons = new GridPane();
			Button[] payButtonsArr = {new Button("CASH"),
					new Button("CARD"), new Button("CHECK"),
					new Button("PAYMENT"), new Button("PRINT"),
					new Button("NEXT")};
			paymentButtons.add(payButtonsArr[0], 0, 0, 1, 1);
			paymentButtons.add(payButtonsArr[1], 1, 0, 1, 1);
			paymentButtons.add(payButtonsArr[2], 2, 0, 1, 1);
			paymentButtons.add(payButtonsArr[3], 0, 1, 3, 1);
			paymentButtons.add(payButtonsArr[4], 0, 2, 3, 1);
			paymentButtons.add(payButtonsArr[5], 0, 3, 3, 1);
			
			
			for (Node child : paymentButtons.getChildren()) {
				Integer column = GridPane.getColumnIndex(child);
				Integer row = GridPane.getRowIndex(child);
				Integer width = GridPane.getColumnSpan(child);
				if (column != null && row != null) {
					if(width == 3) {
						child.getStyleClass().add("paymentNode-3wide");
			    	}
					else {
						child.getStyleClass().add("paymentNode");
					}
				}
			}
			
			paymentButtons.setHgap(1);
			paymentButtons.setVgap(1);
			
			
			//RECIEPT ******************************************
			
			ScrollPane receipt = new ScrollPane();
			GridPane receiptFrame = new GridPane();
			VBox receiptContents = new VBox();
			
			
			Label receiptNum = new Label("");
			receiptNum.textProperty().bind(receiptNumValue);
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy  hh:mma");
			Date currentDate = new Date(System.currentTimeMillis());
			Label date = new Label(formatter.format(currentDate));
			date.textProperty().bind(dateValue);
			dateValue.set(formatter.format(currentDate));
			Label cashier = new Label("");
			cashier.textProperty().bind(cashierValue);
			Label receiptSubtotal = new Label("");
			receiptSubtotal.textProperty().bind(subtotalValue);
			Label receiptTax = new Label("");
			receiptTax.textProperty().bind(taxValue);
			Label receiptTotal = new Label("");
			receiptTotal.textProperty().bind(totalValue);
			Label receiptPayment = new Label("");
			receiptPayment.textProperty().bind(paymentValue);
			Label receiptChange = new Label("");
			receiptChange.textProperty().bind(changeValue);
			
			
			receiptFrame.add(new Label(""), 0, 0, 4, 1);
			receiptFrame.add(new Label(""), 0, 1, 4, 1);
			receiptFrame.add(new Label("SALES RECEIPT"), 0, 2, 4, 1);
			receiptFrame.add(new Label("Store Name"), 0, 3, 4, 1);
			receiptFrame.add(new Label("Store Address"), 0, 4, 4, 1);
			receiptFrame.add(new Label("Store Number"), 0, 5, 4, 1);
			receiptFrame.add(new Label("Receipt #:"), 0, 6, 2, 1);
			receiptFrame.add(receiptNum, 2, 6, 2, 1);
			receiptFrame.add(new Label("Date:"), 0, 7, 2, 1);
			receiptFrame.add(date, 2, 7, 2, 1);
			receiptFrame.add(new Label("Cashier:"), 0, 8, 2, 1);
			receiptFrame.add(cashier, 2, 8, 2, 1);
			receiptFrame.add(new Label("******************************************************"), 0, 9, 4, 1);
			receiptFrame.add(new Label("Item"), 0, 10, 1, 1);
			receiptFrame.add(new Label("Qty"), 1, 10, 1, 1);
			receiptFrame.add(new Label("Price"), 2, 10, 1, 1);
			receiptFrame.add(new Label("Total"), 3, 10, 1, 1);
			receiptFrame.add(new Label("******************************************************"), 0, 11, 4, 1);
			
			
			//int[] receiptContentsRows = {0};
			receiptFrame.add(receiptContents, 0, 12, 4, 1);
			
			receiptFrame.add(new Label(""), 0, 13, 4, 1);
			receiptFrame.add(new Label(""), 0, 14, 4, 1);
			receiptFrame.add(new Label(""), 0, 15, 4, 1);
			receiptFrame.add(new Label("Subtotal:"), 1, 16, 2, 1);
			receiptFrame.add(receiptSubtotal, 3, 16, 1, 1);
			receiptFrame.add(new Label("Tax:"), 1, 17, 2, 1);
			receiptFrame.add(receiptTax, 3, 17, 1, 1);
			receiptFrame.add(new Label("Total:"), 1, 18, 2, 1);
			receiptFrame.add(receiptTotal, 3, 18, 1, 1);
			receiptFrame.add(new Label("Paid Amount:"), 1, 19, 2, 1);
			receiptFrame.add(receiptPayment, 3, 19, 1, 1);
			receiptFrame.add(new Label("Change:"), 1, 20, 2, 1);
			receiptFrame.add(receiptChange, 3, 20, 1, 1);

			
						
			
			
			
			int receiptWidth = 300;
			
			for (int i = 0; i < 4; i++) {
				ColumnConstraints column = new ColumnConstraints(receiptWidth/4-1);
				receiptFrame.getColumnConstraints().add(column);
				//receiptContents.getColumnConstraints().add(column);
			}
			
			for (Node child : receiptFrame.getChildren()) {
				Integer column = GridPane.getColumnIndex(child);
			    Integer row = GridPane.getRowIndex(child);
			    if (column != null && row != null) {
			    	Integer width = GridPane.getColumnSpan(child);
			    	if(width == 4) {
			    		child.getStyleClass().add("receiptNode-4wide");
			    	}
			    	else if(width == 2) {
			    		if(column == 0 || column == 1) {
			    			child.getStyleClass().add("receiptNode-2wide-left");
			    		}
			    		else {
			    			child.getStyleClass().add("receiptNode-2wide-right");
			    		}
			    	}
			    	else {
			    		child.getStyleClass().add("receiptNode");
			    	}
			    }
			}
			
			receipt.setPrefViewportWidth(receiptWidth);
			receipt.setPrefViewportHeight(500);
			receipt.setContent(receiptFrame);
			
			
			//LEFT**********************************************
			
			VBox left = new VBox();
			left.setSpacing(75);
			left.getStyleClass().add("leftPane");
			//left.setAlignment(Pos.CENTER_LEFT);
			Button openMenu = new Button("MENU");
			Button openSignIn = new Button("SIGN IN");
			openMenu.getStyleClass().add("leftButton");
			openSignIn.getStyleClass().add("leftButton");
			left.getChildren().addAll(openMenu, openSignIn);
			
			
			
			//BODY *********************************************
			
			pricing.setAlignment(Pos.CENTER);
			body.add(itemAbout, 0, 0, 1, 1);
			body.add(numPad, 0, 1, 1, 1);
			body.add(pricing, 1, 0, 1, 1);
			body.add(paymentButtons, 1, 1, 1, 1);
			body.add(receipt, 2, 0, 1, 2);
			
			
			
			
			body.setAlignment(Pos.CENTER);
			body.setHgap(30);
			body.setVgap(10);
			mainRoot.setCenter(body);
			mainRoot.setLeft(left);
			openCashierSignIn(root, cashierValue, scanItem);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setFullScreen(true);
			primaryStage.show();

	        
	        
	        
	        
	        
	        
	        //EVENT LISTENERS *******************************************
			
			//***********************************************************
			TextField[] TextFields = {scanItem, quantity, price, payType, payment};
			int[] oldFocus = {0};
			
			
			scanItem.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					if(newValue.length() == 4) {
						int itemsIndex = itemData.GetRowIndexBySearch(newValue, 0);
		            	
						if(itemsIndex != -1) {							
							Vector<String> tempData = itemData.GetRowByIndex(itemsIndex);
							
							addItem(itemData, tempData, itemsIndex, itemNameValue, itemDscrValue, price, quantity, itemPic, currentRow, TextFields, oldFocus, receiptContents, subtotalValue, taxValue, totalValue, scanItem);
						}
						else {
							//Item not found
							//maybe output error?
						}
					}
	            	else if(currentRow[0] != null && newValue.equals("")) {
	            		
	            	}
	            	else {
	            		itemNameValue.set("");
	            		itemDscrValue.set("");
	            		price.setText("");
	            		quantity.setText("");
	            		itemPic.setImage(null);
	            	}
				}
			});
			price.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					if(newValue.indexOf('$') == -1) {
						price.setText("$"+newValue);
					}
					else if(currentRow[0] != null) {
						if(newValue.length()>1 && newValue.charAt(newValue.length()-1) != '.') {
							Label temp = (Label)currentRow[0].getChildren().get(2);
							BigDecimal format = new BigDecimal(newValue.substring(1)).setScale(2, RoundingMode.HALF_UP);
							temp.setText(("$"+String.format("%.2f", format.doubleValue())));
							updateHBoxTotal(currentRow[0]);
							updatePrice(receiptContents, subtotalValue, taxValue, totalValue);
						}
					}
					
				}
			});
			quantity.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					if(currentRow[0] != null) {
						if(newValue.equals("0")) {
							receiptContents.getChildren().remove(currentRow[0]);
							updatePrice(receiptContents, subtotalValue, taxValue, totalValue);
							itemNameValue.set("");
		            		itemDscrValue.set("");
		            		price.setText("");
		            		quantity.setText("");
		            		itemPic.setImage(null);
							currentRow[0] = null;
							scanItem.requestFocus();
							scanItem.selectEnd();
						}
						else if(newValue.length()>0 && newValue.charAt(newValue.length()-1) != '.') {
							Label temp = (Label)currentRow[0].getChildren().get(1);
							BigDecimal format = new BigDecimal(newValue).setScale(2, RoundingMode.HALF_UP);
							temp.setText((String.format("%.2f", format.doubleValue())));
							updateHBoxTotal(currentRow[0]);
							updatePrice(receiptContents, subtotalValue, taxValue, totalValue);
						}
					}
				}
			});
			payment.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					if(newValue.indexOf('$') == -1) {
						payment.setText("$"+newValue);
					}
				}
			});
			scanItem.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
					if (newPropertyValue) {
						oldFocus[0] = 0;
					}
				}
			});
			quantity.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
					if (newPropertyValue) {
						oldFocus[0] = 1;
					}
				}
			});
			price.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
					if (newPropertyValue) {
						oldFocus[0] = 2;
					}
				}
			});
			payType.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
					if (newPropertyValue) {
						oldFocus[0] = 3;
					}
				}
			});
			payment.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
					if (newPropertyValue) {
						oldFocus[0] = 4;
					}
				}
			});
	        
			numPadButtons[0].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"1");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[1].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"2");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[2].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"3");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[3].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"4");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[4].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"5");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[5].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"6");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[6].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"7");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[7].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"8");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[8].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"9");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[9].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+"0");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[10].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText("");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			numPadButtons[11].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					TextFields[oldFocus[0]].setText(TextFields[oldFocus[0]].getText()+".");
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
				}
			});
			payButtonsArr[0].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					//paymentValue.set("Cash");
					payType.setText("Cash");
					payment.requestFocus();
					payment.selectEnd();
					oldFocus[0] = 4;
				}
			});
			payButtonsArr[1].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					payType.setText("Card");
					payment.requestFocus();
					payment.selectEnd();
					oldFocus[0] = 4;
				}
			});
			payButtonsArr[2].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					payType.setText("Check");
					payment.requestFocus();
					payment.selectEnd();
					oldFocus[0] = 4;
				}
			});
			payButtonsArr[3].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					if(payment.getText().length() > 0 && total.getText().length() > 0) {
						if(new BigDecimal(payment.getText().substring(1)).setScale(2, RoundingMode.HALF_UP).doubleValue() >= new BigDecimal(total.getText().substring(1)).setScale(2, RoundingMode.HALF_UP).doubleValue()) {
							changeValue.set("$"+String.format("%.2f", new BigDecimal(payment.getText().substring(1)).setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(total.getText().substring(1)).setScale(2, RoundingMode.HALF_UP)).doubleValue()));
							paymentValue.set("$"+String.format("%.2f", new BigDecimal(payment.getText().substring(1)).doubleValue()));
						}
					}
					scanItem.requestFocus();
					scanItem.selectEnd();
					oldFocus[0] = 0;
				}
			});
			//PRINT button
			payButtonsArr[4].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					if(currentRow[0] != null) {
						currentRow[0].setStyle("-fx-font-weight: normal");
						currentRow[0] = null;
					}
					PrinterJob job = PrinterJob.createPrinterJob();
					if (job != null && job.showPrintDialog(receiptFrame.getScene().getWindow())){
						boolean success = job.printPage(receiptFrame);
						if (success) {
							job.endJob();
						}
					}
					scanItem.requestFocus();
					scanItem.selectEnd();
					oldFocus[0] = 0;
				}
			});
			
			//Next Button
			payButtonsArr[5].setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					if((paymentValue.get() != null) && (paymentValue.get().length()>1) && (new BigDecimal(paymentValue.get().substring(1)).doubleValue() >= new BigDecimal(totalValue.get().substring(1)).doubleValue())) {
						Date currentDate = new Date(System.currentTimeMillis());
						dateValue.set(formatter.format(currentDate));
						Vector<Vector<String>> saveReceipt = new Vector<>();
						for(Node child : receiptContents.getChildren()) {
							Vector<String> receiptRowData = new Vector<>();
							receiptRowData.add(receiptNumValue.get());
							receiptRowData.add(dateValue.get());
							receiptRowData.add(cashierValue.get());
							HBox tempHBox = (HBox) child;
							for(int i = 0; i < 4; i++) {
								Label tempLabel = (Label) tempHBox.getChildren().get(i);
								receiptRowData.add(tempLabel.getText());
							}
							saveReceipt.add(receiptRowData);
						}
						try {
							receiptData.appendToCsv(saveReceipt);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						int newNum = 1 + Integer.parseInt(receiptNumValue.get());
						receiptNumValue.set(String.valueOf(newNum));
						
						receiptContents.getChildren().clear();
						updatePrice(receiptContents, subtotalValue, taxValue, totalValue);
						payment.setText("");
						payType.setText("");
						paymentValue.set("");
						changeValue.set("");
						itemNameValue.set("");
						itemDscrValue.set("");
						price.setText("");
						quantity.setText("");
						scanItem.setText("");
						
						currentRow[0]=null;
					}
					else {
						
					}

					
					scanItem.requestFocus();
					scanItem.selectEnd();
				}
			});
			openMenu.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					try {
						openItemsMenu(root, left, itemData, receiptContents, scanItem, itemNameValue, itemDscrValue, price, quantity, itemPic, currentRow, TextFields, oldFocus, subtotalValue, taxValue, totalValue);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			});
			openSignIn.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					openCashierSignIn(root, cashierValue, scanItem);
					scanItem.requestFocus();
					scanItem.selectEnd();
				}
			});
						
			
			
			
	    }
		catch(Exception e) {
			e.printStackTrace();
		}
    }
	
	private void addItem(CSVhandler itemData, Vector<String> tempData, int itemsIndex, StringProperty itemNameValue, StringProperty itemDscrValue, TextField price, TextField quantity, ImageView itemPic, HBox[] currentRow, TextField[] TextFields, int[] oldFocus, VBox receiptContents, StringProperty subtotalValue, StringProperty taxValue, StringProperty totalValue, TextField scanItem) {
		if(currentRow[0] != null) {
			currentRow[0].setStyle("-fx-font-weight: normal");//removeIf(style -> style.equals("-fx-font-weight: bold"));
			currentRow[0] = null;
		}
		
		boolean alreadyAdded = false;
		HBox existingItem = new HBox();
		for (Node child : receiptContents.getChildren()) {
			existingItem = (HBox) child;
			Label tempLabel = (Label) existingItem.getChildren().get(4);
			if(itemsIndex == Integer.parseInt(tempLabel.getText())) {
				alreadyAdded = true;
				break;
			}
		}
		
		if(alreadyAdded) {
			existingItem.setStyle("-fx-font-weight: bold");
			currentRow[0] = existingItem;
			Label tempQuantity = (Label) existingItem.getChildren().get(1);
			BigDecimal TP = new BigDecimal(tempQuantity.getText()).add(new BigDecimal(1.00)).setScale(2, RoundingMode.HALF_UP);
			tempQuantity.setText(String.format("%.2f", TP.setScale(2, RoundingMode.HALF_UP).doubleValue()));
			updateHBoxTotal(existingItem);
			itemNameValue.set(itemData.readFromCsv(itemsIndex, 1));
			itemDscrValue.set(itemData.readFromCsv(itemsIndex, 2));
			price.setText("$"+itemData.readFromCsv(itemsIndex, 3));
			quantity.setText(tempQuantity.getText());
			try {
				setImage(itemPic,"/resources/images/"+tempData.get(4));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		else {
		
			itemNameValue.set(tempData.get(1));
			itemDscrValue.set(tempData.get(2));
			price.setText("$"+tempData.get(3));
			quantity.setText("1.00");
			try {
				setImage(itemPic,"/resources/images/"+tempData.get(4));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
			Label receiptItem = new Label(tempData.get(1));
			
			Label receiptQuantity = new Label(String.format("%.2f", Double.parseDouble(quantity.getText())));
			Label receiptPrice = new Label("$"+tempData.get(3));
			BigDecimal RP = new BigDecimal(tempData.get(3)).setScale(2, RoundingMode.HALF_UP);
			BigDecimal RQ = new BigDecimal("1.00").setScale(2, RoundingMode.HALF_UP);
			Label receiptTotal = new Label("$"+String.format("%.2f", RP.doubleValue()*RQ.doubleValue()));
			receiptItem.getStyleClass().add("receiptItem");
			receiptQuantity.getStyleClass().add("receiptPrice");
			receiptPrice.getStyleClass().add("receiptPrice");
			receiptTotal.getStyleClass().add("receiptPrice");
			
			Label indexHolder = new Label(String.valueOf(itemsIndex));
			indexHolder.setVisible(false);
			
			HBox rowItems = new HBox();
			rowItems.setPrefWidth(297);
			rowItems.getChildren().addAll(receiptItem, receiptQuantity, receiptPrice, receiptTotal, indexHolder);
			
			
			/* When item in receipt is clicked, it should be "selected" and become bold.
			 * The item name, description, price, and quantity will be displayed.
			 * If a change is made to the items price or quantity, then it will also be made to the receipt.
			 * When an item gets selected, the previously selected item is no longer selected.
			 * */
			rowItems.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					if(currentRow[0] != null) {
						currentRow[0].setStyle("-fx-font-weight: normal");//removeIf(style -> style.equals("-fx-font-weight: bold"));
					}	
					rowItems.setStyle("-fx-font-weight: bold");
					currentRow[0] = rowItems;
					Label temp = (Label)currentRow[0].getChildren().get(4); //temp = itemData index
					itemNameValue.set(itemData.readFromCsv(Integer.parseInt(temp.getText()), 1));
					itemDscrValue.set(itemData.readFromCsv(Integer.parseInt(temp.getText()), 2));
					try {
						setImage(itemPic,"/resources/images/"+itemData.readFromCsv(Integer.parseInt(temp.getText()), 4));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					temp = (Label)currentRow[0].getChildren().get(2);
					price.setText(temp.getText());
					temp = (Label)currentRow[0].getChildren().get(1);
					quantity.setText(temp.getText());
					TextFields[oldFocus[0]].requestFocus();
					TextFields[oldFocus[0]].selectEnd();
					//setId(0);
				}
			});
			
			
			//////////////////////////////////////	
			rowItems.setStyle("-fx-font-weight: bold");
			Label temp = (Label)rowItems.getChildren().get(4);
			itemNameValue.set(itemData.readFromCsv(Integer.parseInt(temp.getText()), 1));
			itemDscrValue.set(itemData.readFromCsv(Integer.parseInt(temp.getText()), 2));
			price.setText("$"+itemData.readFromCsv(Integer.parseInt(temp.getText()), 3));
			temp = (Label)rowItems.getChildren().get(1);
			quantity.setText(temp.getText());
			//////////////////////////////////////
			receiptContents.getChildren().add(rowItems);//, 0, receiptContentsRows[0], 4, 1);
			currentRow[0] = rowItems;
		}
		updatePrice(receiptContents, subtotalValue, taxValue, totalValue);
		//receiptContentsRows[0]++;
		scanItem.setText("");				
	}
	
	private void updatePrice(VBox receiptContents, StringProperty subtotalValue, StringProperty taxValue, StringProperty totalValue) {
		double subtotalEnd = 0;
		
		for (Node child : receiptContents.getChildren()) {

	    	if(child instanceof HBox) {
	    		HBox tempHBox = (HBox) child;
	    		Label tempLabel = (Label) tempHBox.getChildren().get(3);
	    		//System.out.println(tempLabel.getText());
	    		subtotalEnd = new BigDecimal(tempLabel.getText().substring(1)).add(new BigDecimal(subtotalEnd)).setScale(2, RoundingMode.HALF_UP).doubleValue();
	    	}
		}
		
		
        BigDecimal stv = new BigDecimal(subtotalEnd).setScale(2, RoundingMode.HALF_UP);
		subtotalValue.setValue("$"+String.format("%.2f", stv.doubleValue()));
		double taxFactor = 0.08875;
		BigDecimal tav = new BigDecimal(stv.doubleValue()).multiply(new BigDecimal(taxFactor)).setScale(2, RoundingMode.UP);
		taxValue.setValue("$"+String.format("%.2f", tav.doubleValue()));
		BigDecimal tov = new BigDecimal(tav.doubleValue()).add(stv).setScale(2, RoundingMode.HALF_UP);
		totalValue.setValue("$"+String.format("%.2f", tov.doubleValue()));
	}
	
	
	private void updateHBoxTotal(HBox row) {
		Label temp = (Label) row.getChildren().get(1);
		double quantity = Double.parseDouble(temp.getText());
		temp = (Label) row.getChildren().get(2);
		double price = Double.parseDouble(temp.getText().substring(1));
		temp = (Label) row.getChildren().get(3);
		BigDecimal RP = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
		BigDecimal RQ = new BigDecimal(quantity).setScale(2, RoundingMode.HALF_UP);
		temp.setText("$"+String.format("%.2f", RP.doubleValue()*RQ.doubleValue()));
	}
	
	
	private void openCashierSignIn(StackPane root, StringProperty cashierName, TextField scanItem) {
		//StackPane newRoot = new StackPane();
		VBox vbox = new VBox();
		//grid.getStyleClass().add("signInPane");
		Label label = new Label("Cashier name:");
		label.maxWidthProperty().bind(vbox.maxWidthProperty());
		//label.setStyle("-fx-alignment: center-left");
		vbox.getChildren().add(label);
		HBox signIn = new HBox();
		TextField enterCashier = new TextField();
		//enterCashier.setStyle("-fx-alignment: center-left");
		Button submit = new Button("Enter");
		
		signIn.getChildren().addAll(enterCashier, submit);
		vbox.getChildren().add(signIn);
		Label error = new Label("");
		error.setStyle("-fx-text-fill: red;");
		error.maxWidthProperty().bind(vbox.maxWidthProperty());
		vbox.getChildren().add(error);
		//vbox.setPrefSize(400, 200);
		vbox.setMaxSize(275, 100);
		//vbox.setStyle("-fx-background-color: silver;");
		vbox.getStyleClass().add("signInPane");
		for (Node child : vbox.getChildren()) {
			child.getStyleClass().add("signInNode");
		}
		//grid.
		vbox.setAlignment(Pos.CENTER);
		//newRoot.getChildren().add(vbox);
		root.getChildren().get(0).setDisable(true);
		root.getChildren().add(vbox);
		enterCashier.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	if(enterCashier.getText().length() < 1) {
						error.setText("Please enter the name of the cashier.");
					}
					else {
						cashierName.set(enterCashier.getText());
						root.getChildren().get(0).setDisable(false);
						root.getChildren().remove(root.getChildren().get(1));
						scanItem.requestFocus();
					}
	            }
	        }
	    });
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(enterCashier.getText().length() < 1) {
					error.setText("Please enter the name of the cashier.");
				}
				else {
					cashierName.set(enterCashier.getText());
					root.getChildren().get(0).setDisable(false);
					root.getChildren().remove(root.getChildren().get(1));
					scanItem.requestFocus();
				}
			}
		});
	} //hello world!
	
	private void openItemsMenu(StackPane root, VBox disable, CSVhandler itemData, VBox receiptContents, TextField scanItem, StringProperty itemNameValue, StringProperty itemDscrValue, TextField price, TextField quantity, ImageView itemPic, HBox[] currentRow, TextField[] TextFields, int[] oldFocus, StringProperty subtotalValue, StringProperty taxValue, StringProperty totalValue) throws FileNotFoundException {
		disable.setDisable(true);
		VBox menuPane = new VBox();
		ScrollPane menuHolder = new ScrollPane();
		GridPane itemMenu = new GridPane();
		int col = 0;
		for(int i = 0; i < itemData.getNumRows(); i++) {
			VBox item = new VBox();
			ImageView menuPic = new ImageView();
			menuPic.setFitHeight(60);
			menuPic.setFitWidth(100);
			Vector<String> tempData = itemData.GetRowByIndex(i);
			setImage(menuPic,"/resources/images/"+itemData.readFromCsv(i, 4));
			Label itemName = new Label(itemData.readFromCsv(i, 1));
			itemName.setStyle("-fx-pref-width: 100px; -fx-alignment: center;");
			item.getChildren().addAll(menuPic, itemName);
			if(i%5==0 && i!=0) {
				col++;
			}
			itemMenu.add(item, i-(col*5), col, 1, 1);
			item.getStyleClass().add("menuItem");
			int[] itemsIndex = {i};
			item.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					addItem(itemData, tempData, itemsIndex[0], itemNameValue, itemDscrValue, price, quantity, itemPic, currentRow, TextFields, oldFocus, receiptContents, subtotalValue, taxValue, totalValue, scanItem);
				}
			});
			
		}
		Button close = new Button("Close");
		close.setStyle("-fx-pref-width: 100px; -fx-alignment: center;");
		close.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				root.getChildren().remove(root.getChildren().get(1));
					scanItem.requestFocus();
					disable.setDisable(false);
			}
		});
		HBox buttonLoc = new HBox();
		buttonLoc.setStyle("-fx-padding: 10 0 0 0;");
		buttonLoc.setAlignment(Pos.CENTER);
		buttonLoc.getChildren().add(close);
		int paneWidth=635;
		menuPane.setMaxSize(paneWidth, 550);
		menuPane.setTranslateX(-150);
		menuPane.setTranslateY(30);
		menuPane.getStyleClass().add("menuPane");
		menuHolder.setContent(itemMenu);
		menuHolder.setPrefViewportWidth(paneWidth-60);
		menuHolder.setPrefViewportHeight(500);
		menuPane.getChildren().addAll(menuHolder, buttonLoc);
		root.getChildren().add(menuPane);
	}
	
	private void setImage(ImageView imageView, String url) throws FileNotFoundException {
		if(url == null) {
			imageView.setImage(null);
		}
		else {
			InputStream stream = new FileInputStream(System.getProperty("user.dir")+url);
			Image image = new Image(stream);
			imageView.setImage(image);
		}
	}

	
	public static void main(String[] args) {
		launch(args);
	}
}
