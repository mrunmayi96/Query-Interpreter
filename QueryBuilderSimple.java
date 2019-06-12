package com;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class QueryBuilderSimple {

	int row=0, column=0;			// used as indexes for the table
	Boolean isNested, isSubQuery;	// used to check if current query is nested
	
	String tb_name, col_name, operator, operand, col_op;// used to store user's inputs from combo boxes
	String fromDate, toDate;							// used to store user's inputs for dates
	String finalQuery, nestedQuery, dateQuery, subQuery;// used to store different query strings
	String datetime_col, col_list;						// used to store the column from table which has datatype 'datetime'
	
	SimpleDateFormat sdf;	// used for SQL date format ie YYYY-MM-DD.
	Date date1, date2;		// used for validating from and to dates
	
	Connection conn;		// used to store the connection to the database
	QueryInputSimple qis;	// reference to calling class used to acces all GUI components
	
	/* ------------------------------------------Constructor--------------------------------------- */
	
	public QueryBuilderSimple(QueryInputSimple qis){
		
		System.out.println("\nIn: QueryBuilderSimple -> constructor");
			this.qis = qis;
			isNested = false;
			isSubQuery = false;
			finalQuery = " ";
			nestedQuery = " ";
			subQuery = " ";
			dateQuery = " ";
			datetime_col = " ";
			col_list = " ";
			
		
	}
	
	
	/* ------------------------------------------Basic Methods------------------------------------- */
	
	// Function for getting user inputs from console
	void getConsole() {
			
		System.out.println("\nIn: QueryBuilderSimple -> getConsole()");	
			
		tb_name = (String) qis.cbx_table.getSelectedItem();
		col_name = (String) qis.cbx_column.getSelectedItem();
		col_op = (String) qis.cbx_colop.getSelectedItem();
		operator = (String) qis.cbx_operator.getSelectedItem();
		operand = qis.tf_operand.getText();
		
		date1 = qis.picker_fromDate.getDate();
		date2 = qis.picker_toDate.getDate();
		
		System.out.println("Date1 : "+date1);
		System.out.println("Date2 : "+date2);
		
		
			
	}
		
	// Function for setting combo box values on console
	void setConsole() throws ClassNotFoundException, SQLException {

		System.out.println("\nIn: QueryBuilderSimple -> setConsole()");
		
		conn = MyConnection.connectDB();
		
		qis.cbx_operator.setModel(new javax.swing.DefaultComboBoxModel<>(
				new String[] { " ", "(", ")", "=", "!=", ">", "<", "<=", ">=", "!<", "!>", "min", "max" }));

		// Code to add tables in the combo box
		qis.cbx_table.addItem(" ");
		PreparedStatement pstmt = conn.prepareStatement("show tables");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String table_name = rs.getString(1);
			qis.cbx_table.addItem(table_name);
		}

		qis.cbx_table.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			// Clear the column combo box
			qis.cbx_column.removeAllItems();
			qis.cbx_colop.removeAllItems();
			
			// Add blank entry initially
			qis.cbx_column.addItem(" ");
			qis.cbx_colop.addItem(" ");
			// Add column according to selected table
			String selected_table = (String) qis.cbx_table.getSelectedItem();
			if (selected_table != " ") {
				try {
					PreparedStatement pstmt = conn.prepareStatement("desc " + selected_table);
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						String column_name = rs.getString(1);
						qis.cbx_column.addItem(column_name);
						qis.cbx_colop.addItem(column_name);

						// Adding column name is col_list
						col_list = col_list + rs.getString(1);
						if (!rs.isLast()) {
							col_list = col_list + " , ";
						}

						if (rs.getString(2).equals("datetime")) {
							datetime_col = rs.getString(1);
						}
					}
					qis.cbx_column.showPopup();

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
		}
	});
	}
	
	
	// Function for initializing table model
	DefaultTableModel setTable() throws SQLException {

		System.out.println("\nIn: QueryBuilderSimple -> setTable()");
		
		int temp=qis.w/2+5*qis.w/24-5;

		System.out.println("In setTable() w: "+qis.w+" h: "+qis.h);

		// Add Columns to the blank table

		qis.dm.setColumnCount(0);
		qis.dm.setRowCount(0);
		qis.dm.addColumn("Condition");
		qis.dm.addColumn("Connector");
		qis.dm.addColumn("Delete");
		TableColumn tblColumn0 =
		qis.tb_condition.getTableHeader().getColumnModel().getColumn(0);
		tblColumn0.setPreferredWidth(1*temp/6);
		tblColumn0.setMaxWidth(1*temp/6);
		tblColumn0.setMinWidth(1*temp/6);
		tblColumn0.setWidth(1*temp/6);
		TableColumn tblColumn1 =
		qis.tb_condition.getTableHeader().getColumnModel().getColumn(1);
		tblColumn1.setPreferredWidth(2*temp/3);
		tblColumn1.setMaxWidth(2*temp/3);
		tblColumn1.setMinWidth(2*temp/3);
		tblColumn1.setWidth(2*temp/3);
		TableColumn tblColumn2 =

		qis.tb_condition.getTableHeader().getColumnModel().getColumn(2);
		tblColumn2.setPreferredWidth(1*temp/6);
		tblColumn2.setMaxWidth(1*temp/6);
		tblColumn2.setMinWidth(1*temp/6);
		tblColumn2.setWidth(1*temp/6);

		JTableHeader header = qis.tb_condition.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(qis.tb_condition));
		System.out.println("Width of Table Headers:	"+tblColumn0.getWidth()+" "+tblColumn1.getWidth()+"	"+tblColumn2.getWidth());

		return qis.dm;
		
	}
	
	
	// Function to determine whether user is specifying all inputs for date query
	@SuppressWarnings("deprecation")
	boolean isDate(){
		
		System.out.println("\nIn: QueryBuilderSimple -> isDate()");
		
		getConsole();
		
		if(date1!=null && date2!=null){
			
			fromDate = (date1.getYear()+1900)+"-"+(date1.getMonth()+1)+"-"+date1.getDate();
			toDate = (date2.getYear()+1900)+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
			
			return true;
			
		}
		else{
			
			return false;
		}
		
	}
	
	
	/* ------------------------------------------Methods for constructing queries------------------ */
	
	
	// Constructing and executing final query
	@SuppressWarnings("unused")
	void buildQuery() throws SQLException, ClassNotFoundException{
		
		System.out.println("\nIn: QueryBuilderSimple -> buildQuery()");
		
		Statement stmt = conn.createStatement();
				
		getConsole();
		String sampleQuery;
		if(qis.rdb_count.isSelected()){
			sampleQuery = "SELECT count(*) 'Number of Records' FROM "+tb_name+" WHERE ";
		}
		else{
			sampleQuery = "SELECT DISTINCT "+col_list+" FROM "+tb_name+" WHERE ";
		}
		sampleQuery = sampleQuery + qis.tb_condition.getValueAt(0, 1) + " ";
		
		for(int rowNum = 1; rowNum < row; rowNum++){
			
			System.out.println("\nIn: QueryBuilderSimple -> buildQuery() -> for -> row = "+rowNum);
			System.out.println("\nQuery: "+sampleQuery);
			
			sampleQuery = sampleQuery + qis.tb_condition.getValueAt(rowNum, 0) + " " + qis.tb_condition.getValueAt(rowNum, 1) + " ";
			
		}
		
		finalQuery = sampleQuery;
		System.out.println("\nFinal Query Executed : " + finalQuery +"\n\n");

		// Executing finalQuery
		ResultSet rs = stmt.executeQuery(finalQuery);
		QueryOutput qo = new QueryOutput(rs, finalQuery, qis);
		
		
	}
	
	
	// Function for constructing where clause of the sub queries
	String buildSubQuery() {
		
		System.out.println("\nIn: QueryBuilderSimple -> buildSubQuery()");
		getConsole();
		
		if(isNested){
			subQuery = " =(select "+operator+"("+col_name+") from "+tb_name+")";
			
			isSubQuery = false;
			qis.tf_operand.setEnabled(true);
		}
		else{
			subQuery = " ("+col_name+" =(select "+operator+"("+col_name+") from "+tb_name+"))";
		}
		
		return subQuery;
	}
	
		
	// Function for constructing where clause of the date query
	String buildDateQuery(){
		
		System.out.println("\nIn: QueryBuilderSimple -> buildDateQuery()");
		
		getConsole();
		
		dateQuery = " ( DATE("+col_name+") BETWEEN \""+fromDate+"\" and  \""+toDate+"\" )";
		
		System.out.println("Date Query returned : "+dateQuery);
		return dateQuery;
	}
	
	
	
	/* ------------------------------------------Methods for handling combo box events------------- */
	
	// Column CBX
	void cbx_columnActionPerformed(ActionEvent arg0) {
		
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderSimple -> Column CBX Listener ->query is nested\n Dynamically adding any selected column to table");
			
			//Show Warning
			if(qis.cbx_column.getSelectedItem().equals(datetime_col))
			{
				qis.cbx_column.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "Invalid Column Selected", "Error", JOptionPane.ERROR_MESSAGE);										
			}
			else {
			nestedQuery = nestedQuery + " " + qis.cbx_column.getSelectedItem() + " ";
			qis.dm.setValueAt(nestedQuery, row, 1);
			}
		}
		if(!qis.cbx_column.getSelectedItem().equals(" ") && !isDate() && !qis.cbx_column.getSelectedItem().equals(datetime_col)){
			
			qis.cbx_operator.showPopup();
		}
		else if(!isNested && !qis.cbx_column.getSelectedItem().equals(" "))
		{
			qis.picker_fromDate.getCalendarButton().doClick();
		}
		
		if(!qis.cbx_table.getSelectedItem().equals(" ")){
			qis.cbx_table.setEnabled(false);
		}

	}

	
	// Operator CBX
	void cbx_operatorActionPerformed(ActionEvent arg0){
		
		
		// Operand text field enable/disable for min/max operators
		if (qis.cbx_operator.getSelectedItem().equals("max") || qis.cbx_operator.getSelectedItem().equals("min")) {
			
			System.out.println("\nIn: QueryBuilderSimple -> Operator CBX Listener -> disabling text field for min/max");
			isSubQuery = true;
			qis.tf_operand.setEnabled(false);
			
		} else {
			if (!qis.cbx_operator.getSelectedItem().equals("(")) {
				qis.tf_operand.setEnabled(true);
				qis.tf_operand.grabFocus();
			}
		}
		
		// Dynamically add any selected operator to table for nested query 
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderSimple -> Operator CBX Listener ->query is nested\n Dynamically adding any selected operator to table");
			
			if(isSubQuery){
				System.out.println("\nIn: QueryBuilderSimple -> Operator CBX Listener ->query is nested -> and also subquery");
				nestedQuery = nestedQuery + buildSubQuery();
				qis.dm.setValueAt(nestedQuery, row, 1);
				qis.btn_and.setEnabled(true);
				qis.btn_or.setEnabled(true);
			}
			else{
			nestedQuery = nestedQuery + " " + qis.cbx_operator.getSelectedItem();
			qis.dm.setValueAt(nestedQuery, row, 1);
			}
		}
		
		// Initializations for nested query when '(' is selected
		if (qis.cbx_operator.getSelectedItem().equals("(")) {
			
			System.out.println("\nIn: QueryBuilderSimple -> Operator CBX Listener -> '(' operator selected");

			// Set isNested true and add a row
			isNested = true;

			// Remove ( from combo box
			qis.cbx_operator.removeItem("(");

			// Disable Add Condition and Remove Condition
			qis.btn_addCondition.setEnabled(false);
			qis.btn_rmCon.setEnabled(false);

			Object[] temp = new Object[] { null, null, Boolean.FALSE };					

			if (row == 0) {
				qis.dm.addRow(temp);
				qis.dm.setValueAt("None", row, 0);
			}

			// Add ( to nested Query and Table
			nestedQuery = " ( ";
			qis.dm.setValueAt("(", row, 1);
			
			
			qis.cbx_column.showPopup();
		}

		// Cleanup for nested queries if ')' is selected
		if (qis.cbx_operator.getSelectedItem().equals(")") && isNested) {

			System.out.println("\nIn: QueryBuilderSimple -> Operator CBX Listener -> ')' operator selected");
			
			//Grabbing Focus for Add Condition
			qis.btn_addCondition.setBackground(new Color(0, 51, 103));
			
			//Calling Add Condition Button Event Handler
			btn_addConditionActionPerformed(arg0);
			
			//Disable Add Condition Button
			qis.btn_addCondition.setEnabled(false);
			qis.btn_rmCon.setEnabled(true);
			
			// Adding ( in combo box
			qis.cbx_operator.insertItemAt("(", 1);				
			qis.btn_addCondition.setEnabled(true);
		}

	}
	
	
	// Colop CBX
	void cbx_colopActionPerformed(ActionEvent arg0){
		
		if(isNested){
		
			System.out.println("\nIn: QueryBuilderSimple -> ColOp CBX Listener ->query is nested\n Dynamically adding any selected column to table");
			
			//Show Warning
			if(qis.cbx_colop.getSelectedItem().equals(datetime_col))
			{
				qis.cbx_colop.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "Invalid Column Selected", "Error", JOptionPane.ERROR_MESSAGE);										
			}
			else {
			nestedQuery = nestedQuery + " " + qis.cbx_colop.getSelectedItem() + " ";
			qis.dm.setValueAt(nestedQuery, row, 1);
			
			qis.btn_and.setEnabled(true);
			qis.btn_or.setEnabled(true);
			
			}
		}
		else if(!qis.cbx_colop.getSelectedItem().equals(" ")){
			qis.btn_addCondition.setBackground(new Color(0, 51, 103));
		}
	}
	
	/* ------------------------------------------Methods for handling radio button events---------- */
	

	// Text RDB
	void rdb_textStateChanged(ChangeEvent arg0) {
		
		if(qis.rdb_text.isSelected()){
			System.out.println("\nIn: QueryBuilderSimple -> Text RDB listener -> selcted");
			qis.tf_operand.setVisible(true);
			qis.cbx_colop.setVisible(false);
		}
	}

	// Colop RDB
	void rdb_colopStateChanged(ChangeEvent arg0) {
		
		if(qis.rdb_column.isSelected()){
			System.out.println("\nIn: QueryBuilderSimple -> Colop RDB listener -> selcted");
			qis.tf_operand.setVisible(false);
			qis.cbx_colop.setVisible(true);
			qis.cbx_colop.showPopup();
		}
		
	}


	
	/* ------------------------------------------Methods for handling text field events------------ */
	
	// Operand TF
	void tf_operandActionPerformed(ActionEvent arg0){
		
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderSimple -> Operand TxtField Listener -> query is nested\n Dynamically adding any selected operator to table");
			
			nestedQuery = nestedQuery + " \"" + qis.tf_operand.getText() + "\" ";
			qis.dm.setValueAt(nestedQuery, row, 1);

			// Enabling And and Or Buttons
			qis.btn_and.setEnabled(true);
			qis.btn_or.setEnabled(true);
			
			//Grabbing Focus
			qis.btn_and.setBackground(new Color(0, 51, 103));
			qis.btn_or.setBackground(new Color(0, 51, 103));
		}
		
	}

	
	//Operand TF
	void tf_operandKeyPressed(KeyEvent arg0){
		
		//Grabbing Focus
		qis.btn_addCondition.setBackground(new Color(0, 51, 103));
		
	}
	
	/* ------------------------------------------Methods for handling button click events---------- */
	
	// Add Condition Button
	void btn_addConditionActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button");
		System.out.println("Row Number at start of add condition button is : "+row );
		qis.btn_addCondition.setBackground(new Color(0, 102, 204));
		
		// Error Handling
		if (isNested) {
			System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> error handling -> query is nested");
			//Removing Focus
			qis.btn_addCondition.setBackground(new Color(0, 102, 204));
			
			if (nestedQuery.charAt(nestedQuery.length() - 1) != ')') {
				System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> error handling ->  query is nested -> query is not complete");
				JOptionPane.showMessageDialog(qis, "Select Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} 
		else if (qis.cbx_operator.getSelectedItem().equals(" ")) {
			
			System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> error handling -> operator is null + query is not nested");
			
			if(isDate()){
				System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> error handling -> operator is null + query is  date");
				try{
					//getConsole();
					sdf = new SimpleDateFormat("yyyy-MM-dd");
					sdf.setLenient(false);
					date1 = sdf.parse(fromDate);
					date2 = sdf.parse(toDate);
					
					if(date1.after(date2)){
						JOptionPane.showMessageDialog(qis, "Date Range Incorrect", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					
					} catch (ParseException e) {
						//e.printStackTrace();
						JOptionPane.showMessageDialog(qis, "Invalid Date", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					} catch(IllegalArgumentException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(qis, "Invalid Date", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
				}
							
			else{
				System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> error handling -> operator is null + query is not nested or date");	
				JOptionPane.showMessageDialog(qis, "Select Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			
			}
		}
		
		
		
		//Actually adding query in table
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> row addition -> query is nested");
			System.out.println("\nNested Query added in table : "+nestedQuery);
			qis.dm.setValueAt(nestedQuery, row, 1);
			qis.dm.setValueAt(Boolean.FALSE, row, 2);
			
			// Set isNested=false
			isNested = false;
			//System.out.println("isNested in ADD : " + isNested);
		}
		else if(isDate()){
			
			System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> row addition -> query is date -> adding a row to table");
			
			Object[] row_array = { null, null, null };
			
			//If row=0 only then add a new row otherwise setValue at current row
			if (row == 0) {
				qis.dm.addRow(row_array);
				qis.dm.setValueAt("None", row, 0);
			}
			
			qis.dm.setValueAt(buildDateQuery(), row, 1);
			qis.dm.setValueAt(Boolean.FALSE, row, 2);
			//row++;
			
			System.out.println("\nDate Query added in table : "+buildDateQuery());
			System.out.println("\nRow number after adding row is : "+row);
			
			// Add code to make isDate false here (???)
			
			//Reset Date Combo Box 
			qis.picker_fromDate.setDate(null);
			qis.picker_toDate.setDate(null);
			
			//datetime_col = " ";
		}
		else if(isSubQuery){
			
			System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> row addition -> query is subquery");
			
			Object[] row_array = { null, null, null };
			
			//If row=0 only then add a new row otherwise setValue at current row
			if (row == 0) {
				qis.dm.addRow(row_array);
				qis.dm.setValueAt("None", row, 0);
			}
			
			qis.dm.setValueAt(buildSubQuery(), row, 1);
			qis.dm.setValueAt(Boolean.FALSE, row, 2);
			//row++;
			
			System.out.println("\nSub Query added in table : "+buildSubQuery());
			System.out.println("\nRow number after adding row is : "+row);
			
			isSubQuery = false;
			qis.tf_operand.setEnabled(true);
		}
		else {
			
			System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> row addition -> query is normal-> adding a row to table");

			Object[] row_array = { null, null, null };

			//System.out.println("IN ADD CONDITION");

			if (row == 0) {
				qis.dm.addRow(row_array);
				qis.dm.setValueAt("None", row, 0);
			}

			getConsole();

			//System.out.println("In ADD Button, query is not nested..." + finalQuery);
			
			if (operator.equals("min") || operator.equals("max")) {
				finalQuery = " " + operator + "(" + col_name + ")";
			}
			else if(qis.rdb_column.isSelected()){
				finalQuery = col_name + " " + operator + " " + col_op ;
			}
			else {
				finalQuery = col_name + " " + operator + " " + "\"" + operand + "\"";
			}

			qis.dm.setValueAt(finalQuery, row, 1);
			qis.dm.setValueAt(Boolean.FALSE, row, 2);
			
			System.out.println("\nNormal Query added in table : "+finalQuery);
			System.out.println("\nRow number after adding row is : "+row);
			
		}
			
		System.out.println("\nIn: QueryBuilderSimple -> Add Condition Button -> outside all conditions");

		row++;
		System.out.println("Row Number at end of add condition button is : "+row );
				
		// Disabling Add Condition Button
		qis.btn_addCondition.setEnabled(false);

		// Enabling And and Or Connector Buttons, Remove Condition Button
		qis.btn_and.setEnabled(true);
		qis.btn_or.setEnabled(true);
		qis.btn_rmCon.setEnabled(true);
		
		//Grabbing Focus
		qis.btn_and.setBackground(new Color(0, 51, 103));
		qis.btn_or.setBackground(new Color(0, 51, 103));
		qis.btn_execute.setBackground(new Color(0, 51, 103));
		
	}

		
	// AND Button
	void btn_andActionPerformed(java.awt.event.ActionEvent evt) {

			System.out.println("\nIn: QueryBuilderSimple -> AND Button");
			
			
			if (isNested) {
				
				System.out.println("\nIn: QueryBuilderSimple -> AND Button -> query is nested");
				
				nestedQuery = nestedQuery + " and ";
				qis.dm.setValueAt(nestedQuery, row, 1);

				qis.btn_and.setEnabled(false);
				qis.btn_or.setEnabled(false);
				
				//Removing Focus
				qis.btn_and.setBackground(new Color(0, 102, 204));
				qis.btn_or.setBackground(new Color(0, 102, 204));
				
				//Popping Up Column Combobox
				qis.cbx_column.showPopup();
			}

			else {
				
				System.out.println("\nIn: QueryBuilderSimple -> AND Button -> query is not nested");
			
				//Restoring original Button Color
				qis.btn_and.setBackground(new Color(0, 102, 204));
				qis.btn_or.setBackground(new Color(0, 102, 204));
				qis.btn_execute.setBackground(new Color(0, 102, 204));
				/*if ((operand == " " || operator == " ") && !isDate()) {

					JOptionPane.showMessageDialog(this, "Enter Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
				} else {*/
					
				Object[] row_array = { null, null, null };
				qis.dm.addRow(row_array);
				qis.dm.setValueAt("and", row, column);
				// row++;

				// Enabling Add Condition Button
				qis.btn_addCondition.setEnabled(true);

				// Disabling And and Or Connector Buttons
				qis.btn_and.setEnabled(false);
				qis.btn_or.setEnabled(false);
			}
			//}
		}
	
	
	// OR Button
	void btn_orActionPerformed(java.awt.event.ActionEvent evt) {

		System.out.println("\nIn: QueryBuilderSimple -> OR Button");
		
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderSimple -> OR Button -> query is nested");
			
			nestedQuery = nestedQuery + " or ";
			qis.dm.setValueAt(nestedQuery, row, 1);

			qis.btn_and.setEnabled(false);
			qis.btn_or.setEnabled(false);
			
			//Removing Focus
			qis.btn_and.setBackground(new Color(0, 102, 204));
			qis.btn_or.setBackground(new Color(0, 102, 204));
			
			//Popping Up Column Combobox
			qis.cbx_column.showPopup();
			
		}

		else {
			
			System.out.println("\nIn: QueryBuilderSimple -> OR Button -> query is not nested");
			
			//Restoring original Button Color
			qis.btn_and.setBackground(new Color(0, 102, 204));
			qis.btn_or.setBackground(new Color(0, 102, 204));
			qis.btn_execute.setBackground(new Color(0, 102, 204));
			/*if ((operand == " " || operator == " ") && !isDate()) {

				JOptionPane.showMessageDialog(this, "Enter Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
			} else {*/
				Object[] row_array = { null, null, null };
				qis.dm.addRow(row_array);
				qis.dm.setValueAt("or", row, column);
				// row++;

				// Enabling Add Condition Button
				qis.btn_addCondition.setEnabled(true);

				// Disabling And and Or Connector Buttons
				qis.btn_and.setEnabled(false);
				qis.btn_or.setEnabled(false);
			//}
		}
	}

	
	// Clear Button
	void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {

		System.out.println("\nIn: QueryBuilderSimple -> Clear Button");
		
		try {
			
			new QueryInputSimple();
			qis.dispose();
		
		} catch (SQLException  | ClassNotFoundException e) {
			e.printStackTrace();
		} 

	}

	
	// Remove Condition Button
	void btn_rmConActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderSimple -> Remove Condition Button");
		
		if (row == 0) {

			JOptionPane.showMessageDialog(qis, "No conditions to remove.", "Error", JOptionPane.ERROR_MESSAGE);

		} else {
			
			int dummy_row = row - 1;

			for (int r = dummy_row; r >= 0; r--) {
				System.out.println("Row value : "+r);
				System.out.println("Check box : "+qis.tb_condition.getValueAt(r, 2));
				if (qis.tb_condition.getValueAt(r, 2).equals(true)) {
					System.out.println("Remove button handling..inside if...row : "+row);
					qis.dm.removeRow(r);
					qis.dm.fireTableDataChanged();
					row--;
				}
			}
			if (row != 0) {
				qis.dm.setValueAt("None", 0, 0);
			} else{
				qis.btn_rmCon.setEnabled(false);
				qis.btn_and.setEnabled(false);
				qis.btn_or.setEnabled(false);
			}
				qis.btn_addCondition.setEnabled(true);
				
				isNested = false;
				isSubQuery = false;
				finalQuery = " ";
				nestedQuery = " ";
				subQuery = " ";
				dateQuery = " ";
				qis.rdb_text.setSelected(true);
				
		}
	}

	
	// Build Query Button
	void btn_executeActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderSimple -> Build Query Button");

		if (row == 0)
		{
			JOptionPane.showMessageDialog(qis, "No Query to build", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
		//Restoring original Button Color
		qis.btn_and.setBackground(new Color(0, 102, 204));
		qis.btn_or.setBackground(new Color(0, 102, 204));
		qis.btn_execute.setBackground(new Color(0, 102, 204));
		try {
			buildQuery();
			qis.setVisible(false);
		}  catch (SQLException e) {

			e.printStackTrace();
			// Dialog Box for SQL Error
			System.out.println("FINALQUERY : " + finalQuery);
			JOptionPane.showMessageDialog(qis, finalQuery, "Query Incorrect", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			 
			e.printStackTrace();
		}
		}

	}
	
	
	// Mouse Moved on Frame
	void mouseMoved(MouseEvent arg0) {
		
		qis.picker_toDate.setSelectableDateRange(qis.picker_fromDate.getDate(), null);
		if(qis.picker_toDate.getDate()!=null){
			qis.btn_addCondition.setBackground(new Color(0, 51, 103));
		}
		else{
			qis.picker_toDate.setDate(qis.picker_fromDate.getDate());
			
		}
		
	}


}
