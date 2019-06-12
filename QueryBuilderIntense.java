package com;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class QueryBuilderIntense implements Runnable {
	
	/* ------------------------------------------Variable Declarations------------------------------------- */

	int row=0, column=0;					// used as indexes for the table
	long fromDate, toDate, interval=1;		// used to store user's inputs for dates and time slice
	Boolean interval_popup;					// used to popup interval cbx
	
	String col_name, operator;				// used to store user's inputs from combo boxes
	String condition, finalQuery,indexQuery;// used to store different query strings
	
	Date date1, date2;						// used for validating from and to dates
	
	QueryInputIntense qii;					// reference to calling class used to acces all GUI components
	Connection conn;						// used to store the connection to the database
	
	Vector<String> tb_list, where_list;		// used to store list of tables and list of where clauses
	Vector<Long> month_start, month_end;
	Vector<ResultSet> vec_rs;				// used to store array of resul set references, passed to output frame
	Thread[] threadList;					// used to store array of threads
	
	/* ------------------------------------------CONSTRUCTOR------------------------------------- */
	
	
	public QueryBuilderIntense(QueryInputIntense qii){
		
		System.out.println("\nIn: QueryBuilderIntense -> constructor");
		
			this.qii = qii;
			condition = " ";
			finalQuery = " ";
			indexQuery = " ";
			tb_list = new Vector<String>();
			where_list = new Vector<String>();
			month_start = new Vector<Long>();
			month_end = new Vector<Long>();
			vec_rs = new Vector<ResultSet>();
			interval_popup = true;
			
	}
	
	
	
	/* ------------------------------------------Basic Methods------------------------------------- */
	
	// Function for getting user inputs from console
	void getConsole() {
			
		System.out.println("\nIn: QueryBuilderIntense -> getConsole()");	
			
		col_name = (String) qii.cbx_column.getSelectedItem();
		operator = (String) qii.cbx_operator.getSelectedItem();	
		if(qii.cbx_interval.getSelectedItem() == null){
			qii.cbx_interval.setSelectedIndex(1);
		}
		interval = (int) qii.cbx_interval.getSelectedItem();
		
	}
		
	
	// Function for setting combo box values on console
	void setConsole() throws ClassNotFoundException, SQLException {

		System.out.println("\nIn: QueryBuilderIntense -> setConsole()");
		
		qii.cbx_operator.setModel(new javax.swing.DefaultComboBoxModel<>(
				new String[] { " ", "min", "max", "avg", "sum" }));

		qii.cbx_column.setModel(new DefaultComboBoxModel<>(
			new String[] {" ", "tagid", "intvalue", "floatvalue", "stringvalue", "datevalue", "dataintegrity", "t_stamp" }));		
	
		qii.cbx_interval.setModel(new DefaultComboBoxModel<>(
				new Integer[] { null, 1, 8, 24 }));
			
		
	}
	
	
	// Function for getting the FROM, TO dates and tables corresponding to them
	@SuppressWarnings("deprecation")
	boolean getDate(){
		
		
		System.out.println("\nIn: QueryBuilderIntense -> getDate()");
		
		try{
						
			tb_list.clear();
			
			date1 = qii.picker_fromDate.getDate();
			date2 = qii.picker_toDate.getDate();
			
			if(date1 == null || date2 == null){
				return false;
			}
			date1.setHours(0);
			date1.setMinutes(0);
			date1.setSeconds(0);
			long from = date1.getTime()/1000;
			long convertedFromDate = from * 1000;
			date1.setTime(convertedFromDate);
			
			
			
			date2.setHours(23);
			date2.setMinutes(59);
			date2.setSeconds(59);
			long to = date2.getTime()/1000;
			long convertedToDate = to * 1000;
			date2.setTime(convertedToDate);
			
			System.out.println("Date1 : "+date1);
			System.out.println("Date2 : "+date2);
			
			fromDate = date1.getTime();
			toDate = date2.getTime();
			
			System.out.println("fromEpoch: "+fromDate);
			System.out.println("toEpoch: "+toDate);
			
			indexQuery = "SELECT pname, start_time, end_time FROM  test.sqlth_partitions WHERE ";
			indexQuery = indexQuery + "(start_time <= "+fromDate+" and end_time >= "+fromDate+" ) OR ";
			indexQuery = indexQuery + "(start_time >= "+fromDate+" and end_time <= "+toDate+" ) OR ";
			indexQuery = indexQuery + "(start_time <= "+toDate+" and end_time >= "+toDate+" )";
			
			System.out.println("Index Query: "+indexQuery);
			
			conn = MyConnection.connectDB();
			PreparedStatement pstmt = conn.prepareStatement(indexQuery);
			
			ResultSet rs_tables = pstmt.executeQuery();
			
			if(!rs_tables.next()){
				JOptionPane.showMessageDialog(qii, "No data found for these dates", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else{
				rs_tables.beforeFirst();
				while(rs_tables.next()){
					System.out.println("RS: "+rs_tables.getString(1));
					tb_list.add(rs_tables.getString(1));
					month_start.add(rs_tables.getLong(2));
					month_end.add(rs_tables.getLong(3));
				}
				System.out.println("Tables List: "+tb_list);
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	// Function for initializing table model
	DefaultTableModel setTable() throws SQLException {

		System.out.println("\nIn: QueryBuilderIntense -> setTable()");
		
		int temp=qii.w/2+5*qii.w/24-5;

		System.out.println("In setTable() w: "+qii.w+" h: "+qii.h);

		// Add Columns to the blank table

		qii.dm.setColumnCount(0);
		qii.dm.setRowCount(0);
		qii.dm.addColumn("Condition");
		qii.dm.addColumn("Delete");
		
		TableColumn tblColumn0 =
		qii.tb_condition.getTableHeader().getColumnModel().getColumn(0);
		tblColumn0.setPreferredWidth(3*temp/4);
		tblColumn0.setMaxWidth(3*temp/4);
		tblColumn0.setMinWidth(3*temp/4);
		tblColumn0.setWidth(3*temp/4);
		
		TableColumn tblColumn1 =
		qii.tb_condition.getTableHeader().getColumnModel().getColumn(1);
		tblColumn1.setPreferredWidth(temp/4);
		tblColumn1.setMaxWidth(temp/4);
		tblColumn1.setMinWidth(temp/4);
		tblColumn1.setWidth(temp/4);

		JTableHeader header = qii.tb_condition.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(qii.tb_condition));
		System.out.println("Width of Table Headers:	"+tblColumn0.getWidth()+" "+tblColumn1.getWidth());

		return qii.dm;
		
	}
	
	
	/* ------------------------------------------Methods for constructing queries------------------ */
	
	
	// Constructing and executing final query
	@SuppressWarnings("unused")
	void buildQuery() throws SQLException, ClassNotFoundException{
		try {	
		
			System.out.println("\nIn: QueryBuilderIntense -> buildQuery()");
		
			finalQuery = "";
		    vec_rs.removeAllElements();
			where_list.removeAllElements();
			
			//System.out.println("\nIn: QueryBuilderIntense -> buildQuery() -> non simple query");
				
			String selectClause = " SELECT ";
			int rowNum;		
			for(rowNum = 0; rowNum < row-1; rowNum++){
				
				selectClause = selectClause + qii.tb_condition.getValueAt(rowNum, 0) + ", ";
				
				System.out.println("\nIn: QueryBuilderIntense -> buildQuery() -> for -> row = "+rowNum);
				System.out.println("\nQuery: "+selectClause);
				
				
			}
			selectClause = selectClause + qii.tb_condition.getValueAt(rowNum, 0);
			
			System.out.println("\nFinal Select Clause : " + selectClause +"\n\n");	
			int j = 0;
			finalQuery = selectClause + " FROM "; 
			String tablename = tb_list.elementAt(j);
			
			interval = (int) qii.cbx_interval.getSelectedItem();
			interval = interval * 3600000;
			long lowerBound = fromDate, upperBound = fromDate + interval;
			
			System.out.println("From Epoch: "+fromDate+" To Date: "+toDate);
			System.out.println("\nLowerBound = "+lowerBound+"\nUpperBound = "+upperBound+"\nInterval = "+interval);
			
			while(upperBound <= toDate){
				
				where_list.add(tablename + " WHERE t_stamp BETWEEN "+lowerBound+" and "+upperBound);
				upperBound = upperBound + interval;
				lowerBound = lowerBound + interval;
				System.out.println("\nLowerBound = "+lowerBound+"\nUpperBound = "+upperBound);
				
				if(upperBound > month_end.elementAt(j)){
					tablename=tb_list.elementAt(++j);
				}
			}
			
			System.out.println("Where Clauses: "+where_list);
			
	
			threadList = new Thread[where_list.size()];
			
			System.out.println("\nQuery Execution Start Time: "+System.currentTimeMillis());
			
			for(int i = 0; i<where_list.size(); i++){
				
				threadList[i] = new Thread(this, finalQuery + where_list.get(i));
				threadList[i].start();
				System.out.println("\nStarting Thread No. "+i);
			}
			
			for (int i = 0; i < where_list.size(); i++) {
				threadList[i].join();
	
			}
			
			System.out.println("\nQuery Execution End Time: "+System.currentTimeMillis());
		     		
			finalQuery = selectClause + " FROM " + date1 + " TO " + date2; 
			System.out.println("Final Query: "+finalQuery);
			QueryOutput qo = new QueryOutput(vec_rs, finalQuery, qii);
			
		}catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	
	}
	
	
	// Every thread executes this method to query a single chunk
	void queryChunks(String query){
		System.out.println("\nIn: QBI -> queryChunks + \nQuery passed-> "+query);
		try {
			
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			vec_rs.add(rs);
			System.out.println(rs);
			
		
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
	}

	
	/* ------------------------------------------Methods for handling combo box events------------- */
	
	// Column CBX
	void cbx_columnActionPerformed(ActionEvent arg0) {
		getConsole();
		if(!col_name.equals(" ")){
			qii.cbx_operator.showPopup();
		}
	}

	
	// Operator CBX
	void cbx_operatorActionPerformed(ActionEvent arg0){
		// Grabbing Focus
		qii.btn_addCondition.setBackground(new Color(0, 51, 103));
	}
	
	// Interval CBX
	void cbx_intervalActionPerformed(ActionEvent arg0) {
		getConsole();
		if(interval >= 1){
			qii.cbx_column.showPopup();
		}
	}
	
	
	/* ------------------------------------------Methods for handling button click events---------- */
	
	// Add Condition Button
	void btn_addConditionActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderIntense -> Add Condition Button");
		System.out.println("Row Number at start of add condition button is : "+row );
		qii.btn_addCondition.setBackground(new Color(0, 102, 204));

		// Error Handling
		if(!getDate()){
			JOptionPane.showMessageDialog(qii, "Select Dates First", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(date1==null || date2==null){
			System.out.println("\nIn: QueryBuilderComplex -> Add Condition Button -> error handling -> dates not entered");
			JOptionPane.showMessageDialog(qii, "Select Dates First", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else if (qii.cbx_operator.getSelectedItem().equals(" ") && row != 0) {
			
			System.out.println("\nIn: QueryBuilderIntense -> Add Condition Button -> error handling -> operator is null + query is not nested");
			
			System.out.println("\nIn: QueryBuilderIntense -> Add Condition Button -> error handling -> operator is null + query is not nested or date");	
			JOptionPane.showMessageDialog(qii, "Select Valid Data", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Actually adding rows
		
		getConsole();
		
		Object[] row_array = { null, null };
		
		qii.dm.addRow(row_array);
		
		condition = operator + "(" + col_name + ")";
		
		qii.dm.setValueAt(condition, row, 0);
		qii.dm.setValueAt(Boolean.FALSE, row, 1);
		row++;
		
		qii.btn_rmCon.setEnabled(true);
		qii.btn_execute.setBackground(new Color(0, 51, 103));
		
	}

	
	// Clear Button
	void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {

		System.out.println("\nIn: QueryBuilderIntense -> Clear Button");
		
		try {
			new QueryInputIntense();
			qii.dispose();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	
	// Remove Condition Button
	void btn_rmConActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderIntense -> Remove Condition Button");
		
		if (row == 0) {
			qii.btn_rmCon.setEnabled(false);
			JOptionPane.showMessageDialog(qii, "No conditions to remove.", "Error", JOptionPane.ERROR_MESSAGE);

		} else {
			
			int dummy_row = row - 1;

			for (int r = dummy_row; r >= 0; r--) {
				System.out.println("Row value : "+r);
				System.out.println("Check box : "+qii.tb_condition.getValueAt(r, 1));
				if (qii.tb_condition.getValueAt(r, 1).equals(true)) {
					System.out.println("Remove button handling..inside if...row : "+row);
					qii.dm.removeRow(r);
					qii.dm.fireTableDataChanged();
					row--;
				}
			}
			if (row==0){
				qii.btn_rmCon.setEnabled(false);
				qii.btn_execute.setBackground(new Color(0, 102, 204));
				
			}
				qii.btn_addCondition.setEnabled(true);

				finalQuery = " ";			
		}
	}

	
	// Build Query Button
	void btn_executeActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println("\nIn: QueryBuilderIntense -> Build Query Button");

		if (row == 0)
		{
			JOptionPane.showMessageDialog(qii, "No Query to build", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			//Restoring original Button Color
			qii.btn_execute.setBackground(new Color(0, 102, 204));
			try {
				buildQuery();
				qii.setVisible(false);
			}  catch (SQLException e) {
	
				e.printStackTrace();
				
				// Dialog Box for SQL Error
				System.out.println("FINALQUERY : " + finalQuery);
				JOptionPane.showMessageDialog(qii, finalQuery, "Query Incorrect", JOptionPane.ERROR_MESSAGE);
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	// Mouse Moved on Frame
	void mouseMoved(MouseEvent arg0) {
		
		qii.picker_toDate.setSelectableDateRange(qii.picker_fromDate.getDate(), null);
		
		if(interval_popup && qii.picker_toDate.getDate() != null){
			// Grabbing Focus
			qii.cbx_interval.showPopup();
			interval_popup = false;
		}
		
		else if(qii.picker_toDate.getDate() == null){
			qii.picker_toDate.setDate(qii.picker_fromDate.getDate());
		}
		
	}



	
	
	@Override
	public void run() {
		
		System.out.println("\nIn: QBI -> run() ");
		queryChunks(Thread.currentThread().getName());
		
	}

}
