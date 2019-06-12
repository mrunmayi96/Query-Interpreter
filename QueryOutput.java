package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


@SuppressWarnings("serial")
public class QueryOutput extends JFrame
{
	// Variables declarations  
	
	JTextPane tp_query;			// display the query executed on the database
	JButton btn_newquery;		// to create a new query
	JButton btn_back;			// to go back to the query console
	JTable tb_display;			// to display output of query
	JScrollPane jScrollPane;	// to hold the table
	DefaultTableModel dm;		// to fill the table
	Dimension d;				// to get screen dimensions for GUI placing and sizing
	
	QueryInputSimple qis;		// reference to calling class used for going back
	QueryInputComplex qic;		// reference to calling class used for going back
	QueryInputIntense qii;		// reference to calling class used for going back
	
	ResultSet rs;				// to hold the output of simple and complex queries
	Vector<ResultSet> vec_rs;	// to hold the output of intense queries
	
	int w,h;					// used for sizing GUI components
	String query;				// stores query executed on database
	
	/* ------------------------------------------Constructors-------------------------------------- */
    
    // If called by Simple Query Builder class
	public QueryOutput(ResultSet rs, String query, QueryInputSimple qis) throws SQLException
	{		
		System.out.println("\nIn: QueryOutput -> Constructor -> called by SimpleBuilder");
		
		this.rs = rs;
		this.query = query;
		this.qis = qis;
		initComponents();
		dm.setRowCount(0);
		tb_display.setModel(fillTable());
		
		
		this.setSize(d.width, d.height);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
	}

	
	// If called by Complex Query Builder class
	public QueryOutput(ResultSet rs, String query, QueryInputComplex qic) throws SQLException
	{	
		System.out.println("\nIn: QueryOutput -> Constructor -> called by ComplexBuilder");
		
		this.rs = rs;
		this.query = query;
		this.qic = qic;
		initComponents();
		dm.setRowCount(0);
		tb_display.setModel(fillTable());
		
		
		this.setSize(d.width, d.height);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
	}
	
	
	// If called by Intense Query Builder class
	@SuppressWarnings("unchecked")
	public QueryOutput(Vector<ResultSet> rs, String query, QueryInputIntense qii) throws SQLException
	{	
		System.out.println("\nIn: QueryOutput -> Constructor -> called by IntenseBuilder");
		
		vec_rs = (Vector<ResultSet>) rs.clone();
		this.query = query;
		this.qii = qii;
		initComponents();
		dm.setRowCount(0);
		tb_display.setModel(fillTableIntensely());
		
		
		this.setSize(d.width, d.height);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
	}
	
	/* ------------------------------------------Table Fill Logic---------------------------------- */
	
	// For output of intense query
	private DefaultTableModel fillTableIntensely() throws SQLException {
		
		System.out.println("\nIn: QueryOutput -> fillTable() for intense query");
		
		rs = vec_rs.firstElement();	
		
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
	//	System.out.println("Number of Columns are: "+columnCount);
		Vector<String> vec_columnLabel = new Vector<>();
		
		vec_columnLabel.addElement("Interval Number");
		
		dm.setColumnCount(0);	
		for(int i = 1; i <=columnCount; i++)
		{
			vec_columnLabel.addElement(metaData.getColumnLabel(i));
		}
		
		JTableHeader header = tb_display.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(tb_display));
	//	System.out.println("VECTOR columnlabel : "+vec_columnLabel);
		
		int j = 1;
		for(int i = 0; i <columnCount; i++)
		{
			dm.addColumn(vec_columnLabel.elementAt(i));
		}
	//	System.out.println("table column count : "+dm.getColumnCount());
		
		Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
		
		for(ResultSet rs : vec_rs)
		{
			
			while(rs.next())
			{
				Vector<Object> rowData = new Vector<>();
				rowData.add(0, j);
				j++;
				for(int c=1;c<=columnCount;c++)
				{
					if(rs.getObject(c)==null){
						rowData.add("-");
					}
					else{
						rowData.add(rs.getObject(c));
					}
				}
			//	System.out.println("Row Data: "+rowData);
				tableData.add(rowData);
			//	System.out.println("Table Data: "+tableData);
							
			}
		}
		dm.setDataVector(tableData, vec_columnLabel);
	//	System.out.println("Table Model:"+dm.getDataVector());
		return dm;
	}

	
	// For output of simple and complex queries
	public DefaultTableModel fillTable() throws SQLException 
	{
		System.out.println("\nIn: QueryOutput -> fillTable() for simple/complex query");
				
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		//System.out.println("Number of Columns are: "+columnCount);
		Vector<String> vec_columnLabel = new Vector<>();
		
		
		dm.setColumnCount(0);	
		for(int i = 1; i <=columnCount; i++)
		{
			vec_columnLabel.addElement(metaData.getColumnLabel(i));
		}
		JTableHeader header = tb_display.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(tb_display));
		//System.out.println("VECTOR columnlabel : "+vec_columnLabel);
		for(int i = 0; i <columnCount; i++)
		{
			dm.addColumn(vec_columnLabel.elementAt(i));
		}
		//System.out.println("table column count : "+dm.getColumnCount());
		
		Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
		
		while(rs.next())
		{
			Vector<Object> rowData = new Vector<>();
			
			for(int c=1;c<=columnCount;c++)
			{
				rowData.add(rs.getObject(c));
			}
		//	System.out.println("Row Data: "+rowData);
			tableData.add(rowData);
		//	System.out.println("Table Data: "+tableData);
			
						
		}
		dm.setDataVector(tableData, vec_columnLabel);
		//System.out.println("Table Model:"+dm.getDataVector());
		return dm;
	}

	
	/* ------------------------------------------GUI----------------------------------------------- */
	private void initComponents()
	{
		// ---------------------------------------Panel / Frame Settings----------------------------
		
		d = Toolkit.getDefaultToolkit().getScreenSize();
		dm = new DefaultTableModel();
		w=(int) d.getWidth();
		h=(int) d.getHeight();
		
		getContentPane().setLayout(null);
		getContentPane().setBackground(new Color(255, 255, 255));

		// ---------------------------------------Text Pane---------------------------------------------

		tp_query = new JTextPane();
		tp_query.setEditable(false);

		StyledDocument doc = tp_query.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		tp_query.setText("Query : " + query);
		tp_query.setFont(new Font("Cambria", Font.BOLD, 18));
		tp_query.setForeground(new Color(0, 102, 204));

		tp_query.setBounds(w/4+w/24,h/18,w/2+w/24,h/9);
		getContentPane().add(tp_query);

		// -----------------------------------------Table---------------------------------------------

		tb_display = new JTable();
		tb_display.setFont(new Font("Cambria", Font.PLAIN, 18));
		tb_display.setRowHeight(18);
		tb_display.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 18));
		tb_display.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 18));

		tb_display.setModel(dm = new DefaultTableModel(new Object[][] {}, new String[] {}) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		// ---------------------------------------Scroll Pane---------------------------------------

		jScrollPane = new JScrollPane();
		jScrollPane.setBackground(new Color(255, 255, 255));
		jScrollPane.setColumnHeaderView(tb_display);
		jScrollPane.setViewportView(tb_display);

		jScrollPane.setBounds(w/12, h/4, w-w/6, h/2);
		getContentPane().add(jScrollPane);

		// -----------------------------------------New Query Button----------------------------------

		btn_newquery = new JButton("New Query");
		btn_newquery.setForeground(new Color(255, 255, 255));
		btn_newquery.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_newquery.setBackground(new Color(0, 102, 204));

		btn_newquery.setBounds(w/2-w/24, h-3*h/18, w/12, h/18-10);
		getContentPane().add(btn_newquery);

		btn_newquery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					
					if(qis != null){
						new QueryInputSimple();
					}
					else if(qic != null){
						new QueryInputComplex();
					}
					else if(qii != null){
						new QueryInputIntense();
					}
					QueryOutput.this.dispose();
				
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// -----------------------------------------Back Button---------------------------------------

		btn_back = new JButton("Back");
		btn_back.setForeground(new Color(255, 255, 255));
		btn_back.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_back.setBackground(new Color(0, 102, 204));

		btn_back.setBounds(w/12, h/18, w/12, h/18-10);
		getContentPane().add(btn_back);

		btn_back.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if(qis != null){
					qis.setVisible(true);
					qis.btn_rmCon.setEnabled(true);
				}
				else if(qic != null){
					qic.setVisible(true);
					qic.btn_rmCon.setEnabled(true);
				}
				else if(qii != null){
					qii.setVisible(true);
					qii.btn_rmCon.setEnabled(true);
				}
				QueryOutput.this.dispose();
			}
		});

	} // end of init components






}
