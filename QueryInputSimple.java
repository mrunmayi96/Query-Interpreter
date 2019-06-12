package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class QueryInputSimple extends JFrame {

	/* -------------------------------------------Variable declarations---------------------------- */

	JLabel lbl_column;
	JLabel lbl_operator;
	JLabel lbl_table;
	JLabel lbl_FromDate;
	JLabel lbl_ToDate;
	JLabel lbl_Type1;
	
	JRadioButton rdb_text;
	JRadioButton rdb_column;
	JRadioButton rdb_count;

	ButtonGroup btn_grp;

	JDateChooser picker_fromDate;
	JDateChooser picker_toDate;

	JTextField tf_operand;

	JComboBox<String> cbx_column;
	JComboBox<String> cbx_operator;
	JComboBox<String> cbx_table;
	JComboBox<String> cbx_colop;
	
	JButton btn_addCondition;
	JButton btn_and;
	JButton btn_clear;
	JButton btn_or;
	JButton btn_rmCon;
	JButton btn_execute;
	
	JTable tb_condition;
	
	JScrollPane jScrollPane1;
	
	Dimension d;
	
	DefaultTableModel dm;
	
	int w, h;
	
	QueryBuilderSimple qbs;
	private JButton btn_back;

	/*-------------------------------------------End of variables declaration---------------------- */

	
	
	
	// Constructor
	QueryInputSimple() throws SQLException, ClassNotFoundException {
				
		System.out.println("\nIn: QI -> Constructor");
		
		initComponents();
		
		qbs = new QueryBuilderSimple(this);
		
		qbs.setConsole();
		//qbs.getConsole();

		// For Table
		tb_condition.setModel(qbs.setTable());
		
		
		// Disabling And and Or Connector Buttons, Remove Condition Button
		btn_and.setEnabled(false);
		btn_or.setEnabled(false);
		btn_rmCon.setEnabled(false);
		
		
		this.setSize(d.width, d.height);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	
	
	
	
	
	// Function for GUI settings and listeners of combo boxes + text fields
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void initComponents() throws ClassNotFoundException, SQLException {
		

		// ---------------------------------------Panel / Frame Settings---------------------------

		getContentPane().setFont(new Font("Cambria", getContentPane().getFont().getStyle() | Font.BOLD, 18));
		getContentPane().setBackground(new Color(255, 255, 255));
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new Color(255, 255, 255));
		getContentPane().setLayout(null);
		d = Toolkit.getDefaultToolkit().getScreenSize();
		w = (int) d.getWidth();
		h = (int) d.getHeight();
		int temp=w/2+5*w/24-5;
		int dist=(temp-6*w/11)/5;
		
		// ---------------------------------------Labels--------------------------------------------

		lbl_table = new javax.swing.JLabel();
		lbl_table.setFont(new Font("Cambria", Font.BOLD, 18));
		lbl_table.setText("Table Name");

		lbl_column = new javax.swing.JLabel();
		lbl_column.setFont(new Font("Cambria", Font.BOLD, 18));
		lbl_column.setText("Column Name");

		lbl_operator = new javax.swing.JLabel();
		lbl_operator.setFont(new Font("Cambria", Font.BOLD, 18));
		lbl_operator.setText("Operator");

		lbl_FromDate = new JLabel("From Date:");
		lbl_FromDate.setFont(new Font("Cambria", Font.BOLD, 18));

		lbl_ToDate = new JLabel("To Date:");
		lbl_ToDate.setFont(new Font("Cambria", Font.BOLD, 18));

		lbl_Type1 = new JLabel("TYPE 1 Query Builder");
		lbl_Type1.setFont(new Font("Cambria", Font.BOLD, 22));
		lbl_Type1.setBackground(new Color(255, 255, 255));

		//---------------------------------------Radio Buttons-------------------------------------
		
		rdb_count = new JRadioButton("Count");
		rdb_count.setFont(new Font("Cambria", Font.BOLD, 18));
		rdb_count.setBackground(new Color(255, 255, 255));
				
		rdb_text = new JRadioButton("Text");
		rdb_text.setSelected(true);
		rdb_text.setFont(new Font("Cambria", Font.BOLD, 18));
		rdb_text.setBackground(new Color(255, 255, 255));
		
		rdb_column = new JRadioButton("Column");
		rdb_column.setFont(new Font("Cambria", Font.BOLD, 18));
		rdb_column.setBackground(new Color(255, 255, 255));
				
		btn_grp = new ButtonGroup();
		btn_grp.add(rdb_text);
		btn_grp.add(rdb_column);
		
		//---------------------------------------Date Pickers--------------------------------------
		
		picker_fromDate = new JDateChooser();		
		picker_fromDate.getCalendarButton().setBackground(new Color(255, 255, 255));
		picker_fromDate.getCalendarButton().setFont(new Font("Cambria", Font.PLAIN, 18));
		picker_fromDate.setFont(new Font("Cambria", Font.PLAIN, 18));
		picker_fromDate.setBackground(new Color(255, 255, 255));
		
		picker_toDate = new JDateChooser();			
		picker_toDate.getCalendarButton().setFont(new Font("Cambria", Font.PLAIN, 18));
		picker_toDate.getCalendarButton().setBackground(new Color(255, 255, 255));
		picker_toDate.setFont(new Font("Cambria", Font.PLAIN, 18));
		picker_toDate.setBackground(new Color(255, 255, 255));
		

		// ---------------------------------------TextFields---------------------------------------

		tf_operand = new javax.swing.JTextField();
		tf_operand.setFont(new Font("Cambria", Font.PLAIN, 18));
		tf_operand.setPreferredSize(new Dimension(31, 22));


		
		// ---------------------------------------ComboBoxes---------------------------------------

		cbx_table = new javax.swing.JComboBox<>();
		cbx_table.setFont(new Font("Cambria", Font.PLAIN, 18));
		cbx_table.setBackground(new Color(255, 255, 255));
		cbx_table.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));
	
		cbx_column = new javax.swing.JComboBox<>();
		cbx_column.setFont(new Font("Cambria", Font.PLAIN, 18));
		cbx_column.setBackground(new java.awt.Color(255, 255, 255));
		cbx_column.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));

		cbx_operator = new javax.swing.JComboBox<>();
		cbx_operator.setFont(new Font("Cambria", Font.PLAIN, 18));
		cbx_operator.setBackground(new java.awt.Color(255, 255, 255));
		cbx_operator.setModel(new javax.swing.DefaultComboBoxModel<>(
				new String[] { " ", "(", ")", "=", "!=", ">", "<", "<=", ">=", "!<", "!>", "min", "max" }));
		
		cbx_colop = new javax.swing.JComboBox<>();
		cbx_colop.setFont(new Font("Cambria", Font.PLAIN, 18));
		cbx_colop.setBackground(new java.awt.Color(255, 255, 255));
		cbx_colop.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));
		cbx_colop.setVisible(false);
		
		// ---------------------------------------Buttons : Declarations and Formatting-------------

		btn_addCondition = new javax.swing.JButton();
		btn_addCondition.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_addCondition.setBackground(new Color(0, 102, 204));
		btn_addCondition.setForeground(new java.awt.Color(255, 255, 255));
		btn_addCondition.setText("Add Condition");

		btn_and = new javax.swing.JButton();
		btn_and.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_and.setBackground(new Color(0, 102, 204));
		btn_and.setForeground(new java.awt.Color(255, 255, 255));
		btn_and.setText("AND Connector");

		btn_or = new javax.swing.JButton();
		btn_or.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_or.setBackground(new Color(0, 102, 204));
		btn_or.setForeground(new java.awt.Color(255, 255, 255));
		btn_or.setText("OR Connector");

		btn_clear = new javax.swing.JButton();
		btn_clear.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_clear.setBackground(new Color(0, 102, 204));
		btn_clear.setForeground(new java.awt.Color(255, 255, 255));
		btn_clear.setText("Clear Console");

		btn_rmCon = new JButton();
		btn_rmCon.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_rmCon.setForeground(new Color(255, 255, 255));
		btn_rmCon.setBackground(new Color(0, 102, 204));
		btn_rmCon.setText("Remove Condition");

		btn_execute = new JButton();
		btn_execute.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_execute.setForeground(new Color(255, 255, 255));
		btn_execute.setBackground(new Color(0, 102, 204));
		btn_execute.setText("Build Query");
		
		btn_back = new JButton();
		btn_back.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_back.setForeground(new Color(255, 255, 255));
		btn_back.setBackground(new Color(0, 102, 204));
		btn_back.setText("Back");
		
		// ---------------------------------------Table---------------------------------------------

		tb_condition = new javax.swing.JTable();
		tb_condition.setFont(new Font("Cambria", Font.PLAIN, 18));
		tb_condition.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 18));
		tb_condition.setRowHeight(25);
		
		tb_condition.setModel(dm = new DefaultTableModel(
			new Object[][] {
				{null, null, null},
			},
			new String[] {
				"Connector", "Condition", "Delete"
			}
		) {
			Class[] columnTypes = new Class[] {
				Object.class, Object.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			public boolean isCellEditable(int row,int column){
				if(column==0 || column==1)
					return false;
				else
					return true;
			}
		});
		
		
		// ---------------------------------------Scroll Pane---------------------------------------

		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane1.getViewport().setBackground(new java.awt.Color(255, 255, 255));
		jScrollPane1.setFont(new Font("Cambria", Font.BOLD, 18));
		jScrollPane1.setBackground(new Color(255, 255, 255));
		jScrollPane1.setViewportView(tb_condition);

		
		// ---------------------------------------SetBounds-----------------------------------------

		
		int x=0;		//Denotes width i.e go on adding width of buttons in x

		lbl_Type1.setBounds(w/2-w/16, h/18, w/8, 30);
		
		btn_back.setBounds(w/24+x, h/8+20, w/11, 40);
		btn_and.setBounds(w/24+x, h/2-h/9,w/11, 40);
		x=x+w/11;
		btn_or.setBounds(w/24+x+dist, h/2-h/9, w/11, 40);
		x=x+w/11+dist;
		
		lbl_table.setBounds(w/24+x+dist, h/8, w/11, 20);
		cbx_table.setBounds(w/24+x+dist, h/8+20, w/11, 40);
		lbl_column.setBounds(w/24+x+dist, h/2-h/9-30, w/11, 20);
		cbx_column.setBounds(w/24+x+dist, h/2-h/9, w/11, 40);
		
		x=x+w/11+dist;
		
		lbl_operator.setBounds(w/24+x+dist,  h/2-h/9-30, w/11, 20);
		cbx_operator.setBounds(w/24+x+dist, h/2-h/9, w/11, 40);
		rdb_count.setBounds(w/24+x+dist+85, h/2-h/9-30, 91, 25);
		x=x+w/11+dist;
		
		tf_operand.setBounds(w/24+x+dist, h/2-h/9, w/11, 40);
		cbx_colop.setBounds(w/24+x+dist, h/2-h/9, w/11, 40);
		
		lbl_FromDate.setBounds(w/24+x+dist, h/8, 210, 20);
		picker_fromDate.setBounds(w/24+x+dist, h/8+20, w/11, 40);
				
		rdb_text.setBounds(w/24+x+dist, h/2-h/9-30, 71, 25);
		rdb_column.setBounds(w/24+x+dist+70, h/2-h/9-30, 93, 25);
		
		x=x+w/11+dist;
		btn_addCondition.setBounds(w/24+x+dist,h/2-h/9, w/11, 40);
		
		lbl_ToDate.setBounds((3*w/4)+w/24, h/8, 210, 20);
		picker_toDate.setBounds((3*w/4)+w/24, h/8+20, w/11, 40);
		
		btn_clear.setBounds((3*w/4)+w/24, h/2-h/9, 2*(w/21), 40);
		btn_rmCon.setBounds((3*w/4)+w/24, (h/2)+20, 2*(w/21), 40);
		btn_execute.setBounds((3*w/4)+w/24, (3*h/4)-h/9, 2*(w/21), 40);
		

		jScrollPane1.setBounds(w/24, h/2, w/2+5*w/24-5,300);
		
			
		
		// ---------------------------------------Component Additions------------------------------

		// --------------1. Labels----------------

		getContentPane().add(lbl_column);
		getContentPane().add(lbl_operator);
		getContentPane().add(lbl_table);
		getContentPane().add(lbl_FromDate);
		getContentPane().add(lbl_ToDate);
		getContentPane().add(lbl_Type1);
		
		// -------------2. Radio Buttons---------
		
		getContentPane().add(rdb_text);
		getContentPane().add(rdb_column);
		getContentPane().add(rdb_count);
		
		// -------------3. Date Pickers-----------
		
		getContentPane().add(picker_fromDate);
		getContentPane().add(picker_toDate);
		

		// --------------4. Text Fields-----------

		getContentPane().add(tf_operand);

		// --------------5. ComboBoxes------------

		getContentPane().add(cbx_column);
		getContentPane().add(cbx_operator);
		getContentPane().add(cbx_table);
		getContentPane().add(cbx_colop);

		// --------------6. Buttons---------------

		getContentPane().add(btn_addCondition);
		getContentPane().add(btn_and);
		getContentPane().add(btn_or);
		getContentPane().add(btn_clear);
		getContentPane().add(btn_rmCon);
		getContentPane().add(btn_execute);
		getContentPane().add(btn_back);

		// --------------7. Scroll Pane-----------

		getContentPane().add(jScrollPane1);
		
		// ---------------------------------------Buttons : Event Handling + Listeners--------------

		btn_addCondition.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbs.btn_addConditionActionPerformed(evt);
			}
		});

		btn_and.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbs.btn_andActionPerformed(evt);
			}
		});

		btn_or.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbs.btn_orActionPerformed(evt);
			}
		});

		btn_clear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbs.btn_clearActionPerformed(evt);
			}
		});

		btn_rmCon.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbs.btn_rmConActionPerformed(evt);
			}
		});

		btn_execute.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbs.btn_executeActionPerformed(evt);
			}
		});
		
		btn_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Menu();
				QueryInputSimple.this.dispose();
			}
		});
		
		
		rdb_text.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				qbs.rdb_textStateChanged(arg0);
				
			}
		});
		
		rdb_column.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				qbs.rdb_colopStateChanged(arg0);
				
			}
		});
		
		//---------------------------------------Other Listeners-------------------------------
		
		cbx_column.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				qbs.cbx_columnActionPerformed(arg0);
			}
		});
		
		cbx_colop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				qbs.cbx_colopActionPerformed(arg0);
			}
		});
		
		cbx_operator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				qbs.cbx_operatorActionPerformed(arg0);
			}
		});
		
		tf_operand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				qbs.tf_operandActionPerformed(e);
			}
		});
		
		tf_operand.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				qbs.tf_operandKeyPressed(arg0);	
			}
		});
		
		getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				qbs.mouseMoved(arg0);
				
			}
		});
		
		

		

	}// end of init components











}// class ends
