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

import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class QueryBuilderComplex {
	
	/* ------------------------------------------Variable Declarations------------------------------------- */

	int row=0, column=0;
	long fromDate, toDate;					// used to store user's inputs for dates
	Boolean isNested, isSubQuery, isSimple;	// used to check if current query is nested / sub / simple
	
	String col_name, operator, operand, col_op, col_list;// used to store user's inputs from combo boxes
	String finalQuery, normalQuery, whereClause; 		// used to store different query strings
	String nestedQuery, subQuery, indexQuery,  simpleQuery ;
	
	Date date1, date2;			// used for validating from and to dates
	
	Vector<String> tb_list;		// used to store list of table names to be queried
	
	Connection conn;			// used to store the connection to the database
	QueryInputComplex qi;		// reference to calling class used to acces all GUI components
	
	/* ------------------------------------------CONSTRUCTOR------------------------------------- */
	
	
	public QueryBuilderComplex(QueryInputComplex qi){
		
		System.out.println("\nIn: QueryBuilderComplex -> constructor");
		
		this.qi = qi;
		isNested = false;
		isSubQuery = false;
		isSimple = false;
		finalQuery = " ";
		nestedQuery = " ";
		subQuery = " ";
		normalQuery = " ";
		simpleQuery = " ";
		whereClause = " ";
		col_list = " ";
		tb_list = new Vector<String>();
	
			
	}
	
	
	
	/* ------------------------------------------Basic Methods------------------------------------- */
	
	// Function for getting user inputs from console
	void getConsole() {
			
		System.out.println("\nIn: QueryBuilderComplex -> getConsole()");	
			
		col_name = (String) qi.cbx_column.getSelectedItem();
		col_op = (String) qi.cbx_colop.getSelectedItem();
		operator = (String) qi.cbx_operator.getSelectedItem();
		operand = qi.tf_operand.getText();
		
	}
		
	// Function for setting combo box values on console
	void setConsole() throws ClassNotFoundException, SQLException {

		System.out.println("\nIn: QueryBuilderComplex -> setConsole()");
		
		qi.cbx_operator.setModel(new javax.swing.DefaultComboBoxModel<>(
				new String[] { " ", "(", ")", "=", "!=", ">", "<", "<=", ">=", "!<", "!>", "min", "max" }));

		// Code to add tables in the combo box
		qi.cbx_column.setModel(new DefaultComboBoxModel<>(
				new String[] {" ", "source", "priority", "id", "eventtype", "eventtime", "eventid", "eventflags", "displaypath"}));
		
		qi.cbx_colop.setModel(new DefaultComboBoxModel<>(
				new String[] {" ", "source", "priority", "id", "eventtype", "eventtime", "eventid", "eventflags", "displaypath"}));
		
		col_list = "id, eventid, source, displaypath, priority, eventtype, eventflags, eventtime";
		
	}
	
	// Function for getting the FROM, TO dates and tables corresponding to them
	boolean getDate(){
		
		System.out.println("\nIn: QueryBuilderComplex -> getDate()");
		
		try{
			
			tb_list.clear();
			
			date1 = qi.picker_fromDate.getDate();
			date2 = qi.picker_toDate.getDate();
			
			if(date1 == null || date2 == null){
				return false;
			}
			System.out.println("Date1 : "+date1);
			System.out.println("Date2 : "+date2);
			
			fromDate = date1.getTime() / 1000;
			toDate = date2.getTime() / 1000;
			
			System.out.println("fromEpoch: "+fromDate);
			System.out.println("toEpoch: "+toDate);
			
			indexQuery = "SELECT tbname FROM ignition.index WHERE ";
			indexQuery = indexQuery + "(start_date <= "+fromDate+" and end_date >= "+fromDate+" ) OR ";
			indexQuery = indexQuery + "(start_date >= "+fromDate+" and end_date <= "+toDate+" ) OR ";
			indexQuery = indexQuery + "(start_date <= "+toDate+" and end_date >= "+toDate+" )";
			
			System.out.println("Index Query: "+indexQuery);
			
			conn = MyConnection.connectDB();
			PreparedStatement pstmt = conn.prepareStatement(indexQuery);
			
			ResultSet rs_tables = pstmt.executeQuery();
			
			if(!rs_tables.next()){
				JOptionPane.showMessageDialog(qi, "No data found for these dates", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else{
				rs_tables.beforeFirst();
				while(rs_tables.next()){
					System.out.println("RS: "+rs_tables.getString(1));
					tb_list.add(rs_tables.getString(1));
				}
				System.out.println("Tables List: "+tb_list);
				
			}
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	// Function for initializing table model
	DefaultTableModel setTable() throws SQLException {

		System.out.println("\nIn: QueryBuilderComplex -> setTable()");
		
		int temp=qi.w/2+5*qi.w/24-5;

		System.out.println("In setTable() w: "+qi.w+" h: "+qi.h);

		// Add Columns to the blank table

		qi.dm.setColumnCount(0);
		qi.dm.setRowCount(0);
		qi.dm.addColumn("Condition");
		qi.dm.addColumn("Connector");
		qi.dm.addColumn("Delete");
		TableColumn tblColumn0 =
		qi.tb_condition.getTableHeader().getColumnModel().getColumn(0);
		tblColumn0.setPreferredWidth(1*temp/6);
		tblColumn0.setMaxWidth(1*temp/6);
		tblColumn0.setMinWidth(1*temp/6);
		tblColumn0.setWidth(1*temp/6);
		TableColumn tblColumn1 =
		qi.tb_condition.getTableHeader().getColumnModel().getColumn(1);
		tblColumn1.setPreferredWidth(2*temp/3);
		tblColumn1.setMaxWidth(2*temp/3);
		tblColumn1.setMinWidth(2*temp/3);
		tblColumn1.setWidth(2*temp/3);
		TableColumn tblColumn2 =

		qi.tb_condition.getTableHeader().getColumnModel().getColumn(2);
		tblColumn2.setPreferredWidth(1*temp/6);
		tblColumn2.setMaxWidth(1*temp/6);
		tblColumn2.setMinWidth(1*temp/6);
		tblColumn2.setWidth(1*temp/6);

		JTableHeader header = qi.tb_condition.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(qi.tb_condition));
		System.out.println("Width of Table Headers:	"+tblColumn0.getWidth()+" "+tblColumn1.getWidth()+"	"+tblColumn2.getWidth());

		return qi.dm;
		
	}
	
	
	
	/* ------------------------------------------Methods for constructing queries------------------ */
	
	
	// Constructing and executing final query
	@SuppressWarnings("unused")
	void buildQuery() throws SQLException, ClassNotFoundException{
		
		System.out.println("\nIn: QueryBuilderComplex -> buildQuery()");
		
		Statement stmt = conn.createStatement();
				
		getConsole();
		finalQuery = "";
		String sampleQuery;
		if(isSimple){		//query is simply selecting single column
			
			System.out.println("\nIn: QueryBuilderComplex -> buildQuery() -> simple query");
			
			isSimple = false;
			
			sampleQuery = "SELECT " + col_name + " FROM ";
			int i;
			for(i = 0; i<tb_list.size()-1; i++){
				finalQuery = finalQuery + sampleQuery +tb_list.get(i)+"  UNION ALL ";
			}
			//manually adding query for last table in vector
			finalQuery = finalQuery + sampleQuery +tb_list.get(i)+" "+ whereClause;
			
			System.out.println("Final Query: "+finalQuery);
			
			// Executing finalQuery
			ResultSet rs = stmt.executeQuery(finalQuery);
			QueryOutput qo = new QueryOutput(rs, finalQuery, qi);
			
			
		}
		else{		//query is not simple selecting single column
			
			System.out.println("\nIn: QueryBuilderComplex -> buildQuery() -> non simple query");
			
			whereClause = " WHERE ";
			whereClause = whereClause + qi.tb_condition.getValueAt(0, 1) + " ";
			
			for(int rowNum = 1; rowNum < row; rowNum++){
				
				System.out.println("\nIn: QueryBuilderComplex -> buildQuery() -> for -> row = "+rowNum);
				System.out.println("\nQuery: "+whereClause);
				
				whereClause = whereClause + qi.tb_condition.getValueAt(rowNum, 0) + " " + qi.tb_condition.getValueAt(rowNum, 1) + " ";
				
			}
			
			System.out.println("\nFinal Where Clause : " + whereClause +"\n\n");
			
					
			if(qi.rdb_count.isSelected()){
				sampleQuery = "SELECT count(*) 'Number of Records' FROM ";
			}
			else{
				sampleQuery = "SELECT "+col_list+" FROM ";
			}
			int i;
			for(i = 0; i<tb_list.size()-1; i++){
				
				whereClause = whereClause.replace("$TBX$", tb_list.get(i));
				
				finalQuery = finalQuery + sampleQuery +tb_list.get(i)+" "+ whereClause+" UNION ALL ";
				
				whereClause = whereClause.replace(tb_list.get(i), "$TBX$");
			}
			//manually adding query for last table in vector
			whereClause = whereClause.replace("$TBX$", tb_list.get(i));
			
			finalQuery = finalQuery + sampleQuery +tb_list.get(i)+" "+ whereClause;
			
			whereClause = whereClause.replace(tb_list.get(i), "$TBX$");
			
			System.out.println("Final Query: "+finalQuery);
			
			// Executing finalQuery
			ResultSet rs = stmt.executeQuery(finalQuery);
			QueryOutput qo = new QueryOutput(rs, finalQuery, qi);
			
		}
	}
	
	// Function for constructing where clause of the sub queries
	String buildSubQuery() {
		
		System.out.println("\nIn: QueryBuilderComplex -> buildSubQuery()");
		getConsole();
		
		if(isNested){
			
			System.out.println("\nIn: QueryBuilderComplex -> buildSubQuery() -> nested subquery");
			
			subQuery = " =(select "+operator+"("+col_name+") from $TBX$ ))";
			
			isSubQuery = false;
			qi.tf_operand.setEnabled(true);
		
		}
		else{
			
			System.out.println("\nIn: QueryBuilderComplex -> buildSubQuery() -> simple subquery");
						
			subQuery = " ("+col_name+" =(select "+operator+"("+col_name+") from $TBX$ ))";
		
		}
		
		return subQuery;
	}

		
	/* ------------------------------------------Methods for handling combo box events------------- */
	
	// Column CBX
	void cbx_columnActionPerformed(ActionEvent arg0) {
		
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderComplex -> Column CBX Listener ->query is nested");
			nestedQuery = nestedQuery + " " + qi.cbx_column.getSelectedItem() + " ";
			qi.dm.setValueAt(nestedQuery, row, 1);
			
		}
	
		if(!qi.cbx_column.getSelectedItem().equals(" ")){	
			qi.cbx_operator.showPopup();
		}
		else if(!isNested && !qi.cbx_column.getSelectedItem().equals(" "))
		{
			qi.picker_fromDate.getCalendarButton().doClick();
		}

	}

	
	// Operator CBX
	void cbx_operatorActionPerformed(ActionEvent arg0){
		
		
		// Operand text field enable/disable for min/max operators
		if (qi.cbx_operator.getSelectedItem().equals("max") || qi.cbx_operator.getSelectedItem().equals("min")) {
			
			System.out.println("\nIn: QueryBuilderComplex -> Operator CBX Listener -> disabling text field for min/max");
			isSubQuery = true;
			qi.tf_operand.setEnabled(false);
			
		} else {
			if (!qi.cbx_operator.getSelectedItem().equals("(")) {
				
				System.out.println("\nIn: QueryBuilderComplex -> Operator CBX Listener -> query is not min/max or nested");
				
				qi.tf_operand.setEnabled(true);
				qi.tf_operand.grabFocus();
			}
		}
		
		// Dynamically add any selected operator to table for nested query 
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderComplex -> Operator CBX Listener ->query is nested");
			
			if(isSubQuery){
				System.out.println("\nIn: QueryBuilderComplex -> Operator CBX Listener ->query is nested -> and also subquery");
				nestedQuery = nestedQuery + buildSubQuery();
				qi.dm.setValueAt(nestedQuery, row, 1);
				qi.btn_and.setEnabled(true);
				qi.btn_or.setEnabled(true);
			}
			else{
				
				System.out.println("\nIn: QueryBuilderComplex -> Operator CBX Listener ->query is nested -> but not subquery");
				nestedQuery = nestedQuery + " " + qi.cbx_operator.getSelectedItem();
				qi.dm.setValueAt(nestedQuery, row, 1);
			}
		}
		
		// Initializations for nested query when '(' is selected
		if (qi.cbx_operator.getSelectedItem().equals("(")) {
			
			System.out.println("\nIn: QueryBuilderComplex -> Operator CBX Listener -> '(' operator selected");

			// Set isNested true and add a row
			isNested = true;

			// Remove ( from combo box
			qi.cbx_operator.removeItem("(");

			// Disable Add Condition and Remove Condition
			qi.btn_addCondition.setEnabled(false);
			qi.btn_rmCon.setEnabled(false);
			
			getDate();
			
			Object[] temp = new Object[] { null, null, Boolean.FALSE };					

			if (row == 0) {
				qi.dm.addRow(temp);
				qi.dm.setValueAt("None", row, 0);
			}

			// Add ( to nested Query and Table
			nestedQuery = " ( ";
			qi.dm.setValueAt("(", row, 1);
			
			
			qi.cbx_column.showPopup();
		}
		
		// Cleanup for nested queries if ')' is selected
		if (qi.cbx_operator.getSelectedItem().equals(")") && isNested) {

			System.out.println("\nIn: QueryBuilderComplex -> Operator CBX Listener -> ')' operator selected");
			
			//Grabbing Focus for Add Condition
			qi.btn_addCondition.setBackground(new Color(0, 51, 103));
			
			//Calling Add Condition Button Event Handler
			btn_addConditionActionPerformed(arg0);
			
			//Disable Add Condition Button
			qi.btn_addCondition.setEnabled(false);
			qi.btn_rmCon.setEnabled(true);
			
			// Adding ( in combo box
			qi.cbx_operator.insertItemAt("(", 1);				
			qi.btn_addCondition.setEnabled(true);
		}

	}
	
	
	// Colop CBX
	void cbx_colopActionPerformed(ActionEvent arg0){
		
		if(isNested){
		
			System.out.println("\nIn: QueryBuilderComplex -> ColOp CBX Listener ->query is nested");
			
			nestedQuery = nestedQuery + " " + qi.cbx_colop.getSelectedItem() + " ";
			qi.dm.setValueAt(nestedQuery, row, 1);
			
			qi.btn_and.setEnabled(true);
			qi.btn_or.setEnabled(true);
			
		}
		else if(!qi.cbx_colop.getSelectedItem().equals(" ")){
			qi.btn_addCondition.setBackground(new Color(0, 51, 103));
		}
	}
	
	/* ------------------------------------------Methods for handling radio button events---------- */
	
	// Text RDB
	void rdb_textStateChanged(ChangeEvent arg0) {
			if(qi.rdb_text.isSelected()){
				System.out.println("\nIn: QueryBuilderComplex -> Text radio button -> selected");
				qi.tf_operand.setVisible(true);
				qi.cbx_colop.setVisible(false);
			}
		}

	
	// Colop RDB
	void rdb_colopStateChanged(ChangeEvent arg0) {
			if(qi.rdb_column.isSelected()){
				
				System.out.println("\nIn: QueryBuilderComplex -> Colop radio button -> selected");
				qi.tf_operand.setVisible(false);
				qi.cbx_colop.setVisible(true);
				qi.cbx_colop.showPopup();
			}
			
		}

	/* ------------------------------------------Methods for handling text field events------------ */
	
	// Operand TF
	void tf_operandActionPerformed(ActionEvent arg0){
		
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderComplex -> Operand TxtField Listener -> query is nested");
			
			nestedQuery = nestedQuery + " \"" + qi.tf_operand.getText() + "\" ";
			qi.dm.setValueAt(nestedQuery, row, 1);

			// Enabling And and Or Buttons
			qi.btn_and.setEnabled(true);
			qi.btn_or.setEnabled(true);
			
			//Grabbing Focus
			qi.btn_and.setBackground(new Color(0, 51, 103));
			qi.btn_or.setBackground(new Color(0, 51, 103));
		}
		
	}

	
	//Operand TF
	void tf_operandKeyPressed(KeyEvent arg0){
		
		// Grabbing Focus
		qi.btn_addCondition.setBackground(new Color(0, 51, 103));
		
	}
	
	
	/* ------------------------------------------Methods for handling button click events---------- */
	
	// Add Condition Button
	void btn_addConditionActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button");
		System.out.println("Row Number at start of add condition button is : "+row );
		qi.btn_addCondition.setBackground(new Color(0, 102, 204));
		
		// Error Handling
		if(!getDate()){
			JOptionPane.showMessageDialog(qi, "Select Dates First", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(date1==null || date2==null){
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> error handling -> dates not entered");
			JOptionPane.showMessageDialog(qi, "Select Dates First", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (isNested) {
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> error handling -> query is nested");
			//Removing Focus
			qi.btn_addCondition.setBackground(new Color(0, 102, 204));
			
			if (nestedQuery.charAt(nestedQuery.length() - 1) != ')') {
				System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> error handling ->  query is nested -> query is not complete");
				JOptionPane.showMessageDialog(qi, "Select Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} 
		else if (qi.cbx_operator.getSelectedItem().equals(" ") && row != 0) {
			
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> error handling -> operator is null + query is not nested");
			
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> error handling -> operator is null + query is not nested or date");	
			JOptionPane.showMessageDialog(qi, "Select Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
			return;
			
		}
		
		//Actually adding query in table
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> row addition -> query is nested");
			System.out.println("\nNested Query added in table : "+nestedQuery);
			qi.dm.setValueAt(nestedQuery, row, 1);
			qi.dm.setValueAt(Boolean.FALSE, row, 2);
			
			// Set isNested=false
			isNested = false;
			//System.out.println("isNested in ADD : " + isNested);
			
			// Enabling And and Or Connector Buttons, Remove Condition Button
			qi.btn_and.setEnabled(true);
			qi.btn_or.setEnabled(true);
			qi.btn_rmCon.setEnabled(true);
			
			//Grabbing Focus
			qi.btn_and.setBackground(new Color(0, 51, 103));
			qi.btn_or.setBackground(new Color(0, 51, 103));
			qi.btn_execute.setBackground(new Color(0, 51, 103));
			
			
		}
		else if(isSubQuery){
			
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> row addition -> query is subquery");
			
			Object[] row_array = { null, null, null };
			
			//If row=0 only then add a new row otherwise setValue at current row
			if (row == 0) {
				qi.dm.addRow(row_array);
				qi.dm.setValueAt("None", row, 0);
			}
			
			qi.dm.setValueAt(buildSubQuery(), row, 1);
			qi.dm.setValueAt(Boolean.FALSE, row, 2);
			//row++;
			
			System.out.println("\nSub Query added in table : "+buildSubQuery());
			System.out.println("\nRow number after adding row is : "+row);
			
			isSubQuery = false;
			qi.tf_operand.setEnabled(true);
			
			
			// Enabling And and Or Connector Buttons, Remove Condition Button
			qi.btn_and.setEnabled(true);
			qi.btn_or.setEnabled(true);
			qi.btn_rmCon.setEnabled(true);
			
			//Grabbing Focus
			qi.btn_and.setBackground(new Color(0, 51, 103));
			qi.btn_or.setBackground(new Color(0, 51, 103));
			qi.btn_execute.setBackground(new Color(0, 51, 103));
						
			
		}
		else if(qi.cbx_operator.getSelectedItem().equals(" ") && qi.tf_operand.getText().isEmpty() && row==0){
			
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> row addition -> query is simple");
			
			getConsole();
			isSimple=true;
			Object[] row_array = { null, null, null };
			simpleQuery = col_name + " FROM "+date1+" TO "+date2;
			qi.dm.addRow(row_array);
			qi.dm.setValueAt("None", row, 0);
			qi.dm.setValueAt(simpleQuery, row, 1);
			qi.dm.setValueAt(Boolean.FALSE, row, 2);
			
			qi.btn_and.setEnabled(false);
			qi.btn_or.setEnabled(false);
			qi.btn_rmCon.setEnabled(true);
			
			
		}
		else {
			
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> row addition -> query is normal");

			Object[] row_array = { null, null, null };

			//System.out.println("IN ADD CONDITION");

			if (row == 0) {
				qi.dm.addRow(row_array);
				qi.dm.setValueAt("None", row, 0);
			}

			getConsole();

			//System.out.println("In ADD Button, query is not nested..." + finalQuery);
			
			if (operator.equals("min") || operator.equals("max")) {
				normalQuery = " " + operator + "(" + col_name + ")";
			}
			else if(qi.rdb_column.isSelected()){
				normalQuery = col_name + " " + operator + " " + col_op ;
			}
			else {
				normalQuery = col_name + " " + operator + " " + "\"" + operand + "\"";
			}

			qi.dm.setValueAt(normalQuery, row, 1);
			qi.dm.setValueAt(Boolean.FALSE, row, 2);
			
			System.out.println("\nNormal Query added in table : "+normalQuery);
			System.out.println("\nRow number after adding row is : "+row);
			
			// Enabling And and Or Connector Buttons, Remove Condition Button
			qi.btn_and.setEnabled(true);
			qi.btn_or.setEnabled(true);
			qi.btn_rmCon.setEnabled(true);
			
			//Grabbing Focus
			qi.btn_and.setBackground(new Color(0, 51, 103));
			qi.btn_or.setBackground(new Color(0, 51, 103));
			qi.btn_execute.setBackground(new Color(0, 51, 103));
			
			
		}
			
		System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> outside all conditions");

		row++;
		System.out.println("Row Number at end of add condition button is : "+row );
		
		// Disabling Add Condition Button
		qi.btn_addCondition.setEnabled(false);

		
	}

		
	// AND Button
	void btn_andActionPerformed(java.awt.event.ActionEvent evt) {

			System.out.println("\nIn: QueryBuilderComplex -> AND Button");
			
			if (isNested) {
				
				System.out.println("\nIn: QueryBuilderComplex -> AND Button -> query is nested");
				
				nestedQuery = nestedQuery + " and ";
				qi.dm.setValueAt(nestedQuery, row, 1);

				qi.btn_and.setEnabled(false);
				qi.btn_or.setEnabled(false);
				
				//Removing Focus
				qi.btn_and.setBackground(new Color(0, 102, 204));
				qi.btn_or.setBackground(new Color(0, 102, 204));
				
				//Popping Up Column Combobox
				qi.cbx_column.showPopup();
			}

			else {
				
				System.out.println("\nIn: QueryBuilderComplex -> AND Button -> query is not nested");
			
				//Restoring original Button Color
				qi.btn_and.setBackground(new Color(0, 102, 204));
				qi.btn_or.setBackground(new Color(0, 102, 204));
				qi.btn_execute.setBackground(new Color(0, 102, 204));
				/*if ((operand == " " || operator == " ") && !isDate()) {

					JOptionPane.showMessageDialog(this, "Enter Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
				} else {*/
					
				Object[] row_array = { null, null, null };
				qi.dm.addRow(row_array);
				qi.dm.setValueAt("and", row, column);
				// row++;

				// Enabling Add Condition Button
				qi.btn_addCondition.setEnabled(true);

				// Disabling And and Or Connector Buttons
				qi.btn_and.setEnabled(false);
				qi.btn_or.setEnabled(false);
			}
			//}
		}
	
	
	// OR Button
	void btn_orActionPerformed(java.awt.event.ActionEvent evt) {

		System.out.println("\nIn: QueryBuilderComplex -> OR Button");
		
		if (isNested) {
			
			System.out.println("\nIn: QueryBuilderComplex -> OR Button -> query is nested");
			
			nestedQuery = nestedQuery + " or ";
			qi.dm.setValueAt(nestedQuery, row, 1);

			qi.btn_and.setEnabled(false);
			qi.btn_or.setEnabled(false);
			
			//Removing Focus
			qi.btn_and.setBackground(new Color(0, 102, 204));
			qi.btn_or.setBackground(new Color(0, 102, 204));
			
			//Popping Up Column Combobox
			qi.cbx_column.showPopup();
			
		}

		else {
			
			System.out.println("\nIn: QueryBuilderComplex -> OR Button -> query is not nested");
			
			//Restoring original Button Color
			qi.btn_and.setBackground(new Color(0, 102, 204));
			qi.btn_or.setBackground(new Color(0, 102, 204));
			qi.btn_execute.setBackground(new Color(0, 102, 204));
			/*if ((operand == " " || operator == " ") && !isDate()) {

				JOptionPane.showMessageDialog(this, "Enter Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
			} else {*/
				Object[] row_array = { null, null, null };
				qi.dm.addRow(row_array);
				qi.dm.setValueAt("or", row, column);
				// row++;

				// Enabling Add Condition Button
				qi.btn_addCondition.setEnabled(true);

				// Disabling And and Or Connector Buttons
				qi.btn_and.setEnabled(false);
				qi.btn_or.setEnabled(false);
			//}
		}
	}

	
	// Clear Button
	void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {

		System.out.println("\nIn: QueryBuilderComplex -> Clear Button");
		
		try {
			new QueryInputComplex();
			qi.dispose();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	// Remove Condition Button
	void btn_rmConActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderComplex -> Remove Condition Button");
		
		if (row == 0) {

			JOptionPane.showMessageDialog(qi, "No conditions to remove.", "Error", JOptionPane.ERROR_MESSAGE);

		} else {
			
			int dummy_row = row - 1;

			for (int r = dummy_row; r >= 0; r--) {
				System.out.println("Row value : "+r);
				System.out.println("Check box : "+qi.tb_condition.getValueAt(r, 2));
				if (qi.tb_condition.getValueAt(r, 2).equals(true)) {
					System.out.println("Remove button handling..inside if...row : "+row);
					qi.dm.removeRow(r);
					qi.dm.fireTableDataChanged();
					row--;
				}
			}
			if (row != 0) {
				qi.dm.setValueAt("None", 0, 0);
			} else{
				qi.btn_rmCon.setEnabled(false);
				qi.btn_and.setEnabled(false);
				qi.btn_or.setEnabled(false);
			}
				qi.btn_addCondition.setEnabled(true);
				
				isNested = false;
				isSubQuery = false;
				isSimple = false;
				finalQuery = " ";
				nestedQuery = " ";
				subQuery = " ";
				normalQuery = " ";
				simpleQuery = " ";
				whereClause = " ";
				qi.rdb_text.setSelected(true);
				
		}
	}

	
	// Build Query Button
	void btn_executeActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderComplex -> Build Query Button");

		if (row == 0)
		{
			JOptionPane.showMessageDialog(qi, "No Query to build", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			//Restoring original Button Color
			qi.btn_and.setBackground(new Color(0, 102, 204));
			qi.btn_or.setBackground(new Color(0, 102, 204));
			qi.btn_execute.setBackground(new Color(0, 102, 204));
			try {
				buildQuery();
				qi.setVisible(false);
			}  catch (SQLException e) {
	
				e.printStackTrace();
				
				// Dialog Box for SQL Error
				System.out.println("FINALQUERY : " + finalQuery);
				JOptionPane.showMessageDialog(qi, finalQuery, "Query Incorrect", JOptionPane.ERROR_MESSAGE);
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	// Mouse Moved on Frame
	void mouseMoved(MouseEvent arg0) {
		
		qi.picker_toDate.setSelectableDateRange(qi.picker_fromDate.getDate(), null);
		if(qi.picker_toDate.getDate()!=null){
			qi.btn_addCondition.setBackground(new Color(0, 51, 103));
		}
		else{
			qi.picker_toDate.setDate(qi.picker_fromDate.getDate());
		}
		
	}


	

}
