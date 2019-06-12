package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;


@SuppressWarnings("serial")
public class QueryInputIntense extends JFrame {

	/* -------------------------------------------Variable declarations---------------------------- */

	JLabel lbl_column;
	JLabel lbl_operator;
	JLabel lbl_FromDate;
	JLabel lbl_ToDate;
	JLabel lbl_Type3;
	JLabel lbl_interval;

	JDateChooser picker_fromDate;
	JDateChooser picker_toDate;

	JComboBox<String> cbx_column;
	JComboBox<String> cbx_operator;
	JComboBox<Integer> cbx_interval;
	
	JButton btn_addCondition;
	JButton btn_clear;
	JButton btn_rmCon;
	JButton btn_execute;
	
	JTable tb_condition;
	
	JScrollPane jScrollPane1;
	
	Dimension d;
	
	DefaultTableModel dm;
	
	int w, h;
	
	QueryBuilderIntense qbi;
	private JButton btn_back;

	/*-------------------------------------------End of variables declaration---------------------- */

	
	
	
	// Constructor
	QueryInputIntense() throws SQLException, ClassNotFoundException {
				
		System.out.println("\nIn: QI -> Constructor");
		
			
		initComponents();
		
		qbi = new QueryBuilderIntense(this);
		
		qbi.setConsole();
		
		// For Table
		tb_condition.setModel(qbi.setTable());
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
		int dist=(temp-4*w/11)/3;
		
		//---------------------------------------Labels--------------------------------------------
		
		lbl_Type3 = new JLabel("Type 3 Query Builder");
		lbl_Type3.setFont(new Font("Cambria", Font.BOLD, 22));
		lbl_Type3.setBackground(new Color(255, 255, 255));
		
		lbl_FromDate = new JLabel("From Date:");
		lbl_FromDate.setFont(new Font("Cambria", Font.BOLD, 18));

		lbl_ToDate = new JLabel("To Date:");
		lbl_ToDate.setFont(new Font("Cambria", Font.BOLD, 18));
		
		lbl_interval = new JLabel("Interval");
		lbl_interval.setBackground(new Color(255, 255, 255));
		lbl_interval.setFont(new Font("Cambria", Font.BOLD, 18));
		
		lbl_column = new javax.swing.JLabel();
		lbl_column.setFont(new Font("Cambria", Font.BOLD, 18));
		lbl_column.setText("Column Name");

		lbl_operator = new javax.swing.JLabel();
		lbl_operator.setFont(new Font("Cambria", Font.BOLD, 18));
		lbl_operator.setText("Operator");

				
		
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

		//---------------------------------------Combo Boxes---------------------------------------
		
		cbx_column = new javax.swing.JComboBox<>();
		cbx_column.setFont(new Font("Cambria", Font.PLAIN, 18));
		cbx_column.setBackground(new java.awt.Color(255, 255, 255));
		cbx_column.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));

		cbx_operator = new javax.swing.JComboBox<>();
		cbx_operator.setFont(new Font("Cambria", Font.PLAIN, 18));
		cbx_operator.setBackground(new java.awt.Color(255, 255, 255));
		cbx_operator.setModel(new javax.swing.DefaultComboBoxModel<>(
				new String[] { " ", "(", ")", "=", "!=", ">", "<", "<=", ">=", "!<", "!>", "min", "max" }));
		
		cbx_interval = new JComboBox();
		cbx_interval.setBackground(new Color(255, 255, 255));
		cbx_interval.setForeground(new Color(0, 0, 0));
		cbx_interval.setFont(new Font("Cambria", Font.BOLD, 18));
		
		
		// ---------------------------------------Buttons : Declarations and Formatting-------------

		btn_addCondition = new javax.swing.JButton();
		btn_addCondition.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_addCondition.setBackground(new Color(0, 102, 204));
		btn_addCondition.setForeground(new java.awt.Color(255, 255, 255));
		btn_addCondition.setText("Add Condition");

		btn_clear = new javax.swing.JButton();
		btn_clear.setFont(new Font("Cambria", Font.BOLD, 18));
		btn_clear.setBackground(new Color(0, 102, 204));
		btn_clear.setForeground(new java.awt.Color(255, 255, 255));
		btn_clear.setText("Clear Console\r\n");

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
				{null, null},
			},
			new String[] {
				"Condition", "Delete"
			}
		) {
			Class[] columnTypes = new Class[] {
				Object.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		
		
		// ---------------------------------------Scroll Pane---------------------------------------

		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane1.getViewport().setBackground(new java.awt.Color(255, 255, 255));
		jScrollPane1.setFont(new Font("Cambria", Font.BOLD, 18));
		jScrollPane1.setBackground(new Color(255, 255, 255));
		jScrollPane1.setViewportView(tb_condition);

		
		// ---------------------------------------SetBounds-----------------------------------------

		
		int x=0;

			
		lbl_Type3.setBounds(w/2-w/16, h/18, w/8, 30);
		
		lbl_FromDate.setBounds(w/4+w/24, h/8, w/11, 20);
		picker_fromDate.setBounds(w/4, h/8+20, 240, 40);

		lbl_ToDate.setBounds(w/2+w/24, h/8, w/11, 20);
		picker_toDate.setBounds(w/2+w/24, h/8+20, 240, 40);

		btn_back.setBounds(w/24, h/8+20, w/11, 40);
		
		lbl_interval.setBounds(w/24+x, h/2-h/9-30, w/11, 20);
		cbx_interval.setBounds(w/24+x, h/2-h/9, w/11, 40);		
		x=x+w/11;		
		
		lbl_column.setBounds(w/24+x+dist, h/2-h/9-30, w/11, 20);
		cbx_column.setBounds(w/24+x+dist, h/2-h/9, w/11, 40);		
		x=x+w/11+dist;
		
		lbl_operator.setBounds(w/24+x+dist,  h/2-h/9-30, w/11, 20);
		cbx_operator.setBounds(w/24+x+dist, h/2-h/9, w/11, 40);
		x=x+w/11+dist;
		
		btn_addCondition.setBounds(w/24+x+dist,h/2-h/9, w/11, 40);
		
		btn_clear.setBounds((3*w/4)+w/24, h/2-h/9, 2*(w/21), 40);
		btn_rmCon.setBounds((3*w/4)+w/24, (h/2)+20, 2*(w/21), 40);
		btn_execute.setBounds((3*w/4)+w/24, (3*h/4)-h/9, 2*(w/21), 40);
	

		jScrollPane1.setBounds(w/24, h/2, w/2+5*w/24-5,300);
		
		
		// ---------------------------------------Component Additions------------------------------

		// --------------1. Labels----------------

		getContentPane().add(lbl_column);
		getContentPane().add(lbl_operator);
		getContentPane().add(lbl_FromDate);
		getContentPane().add(lbl_ToDate);
		getContentPane().add(lbl_Type3);
		getContentPane().add(lbl_interval);
		
		// -------------3. Date Pickers-----------
		
		getContentPane().add(picker_fromDate);
		getContentPane().add(picker_toDate);

		// --------------5. ComboBoxes------------

		getContentPane().add(cbx_column);
		getContentPane().add(cbx_operator);
		getContentPane().add(cbx_interval);
		
		// --------------6. Buttons---------------

		getContentPane().add(btn_addCondition);
		getContentPane().add(btn_clear);
		getContentPane().add(btn_rmCon);
		getContentPane().add(btn_execute);
		getContentPane().add(btn_back);

		// --------------7. Scroll Pane-----------

		getContentPane().add(jScrollPane1);
		
		
		// ---------------------------------------Buttons : Event Handling + Listeners--------------

		btn_addCondition.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbi.btn_addConditionActionPerformed(evt);
			}
		});

		btn_clear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbi.btn_clearActionPerformed(evt);
			}
		});

		btn_rmCon.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbi.btn_rmConActionPerformed(evt);
			}
		});

		btn_execute.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qbi.btn_executeActionPerformed(evt);
			}
		});
		
		btn_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Menu();
				QueryInputIntense.this.dispose();
			}
		});
		
		//---------------------------------------Other Listeners-------------------------------
		
		cbx_column.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				qbi.cbx_columnActionPerformed(arg0);
			}
		});
		
		cbx_operator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				qbi.cbx_operatorActionPerformed(arg0);
			}
		});
		
		cbx_interval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				qbi.cbx_intervalActionPerformed(arg0);
			}
		});
		
		getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				qbi.mouseMoved(arg0);
				
			}
		});
		
		

		

	}// end of init components

}// class ends
