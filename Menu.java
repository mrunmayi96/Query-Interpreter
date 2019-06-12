package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;




@SuppressWarnings("serial")
public class Menu extends JFrame {
	
	private JTextPane tp_type1;
	private JTextPane tp_type2; 
	private JTextPane tp_type3;
	
	JButton btn_type1;
	JButton btn_type2;
	JButton btn_type3;
	
	private JPanel contentPane;
	Dimension d;
	int w,h;
	
	public Menu() {
		
		initComponents();
		
		this.setSize(d.width, d.height);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	void initComponents(){
		
		// ---------------------------------------Panel / Frame Settings---------------------------
		
		d = Toolkit.getDefaultToolkit().getScreenSize();
		w = (int) d.getWidth();
		h = (int) d.getHeight();
				
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// ---------------------------------------TextPanes---------------------------------------
		
		tp_type1 = new JTextPane();
		tp_type1.setFont(new Font("Cambria", Font.BOLD, 18));
		tp_type1.setText("To be used if the desired data is present in a variety of tables in a single database. Builds normal, nested or date based(if your table has a column of datetime datatype) queries and generates required output.");
		tp_type1.setEditable(false);
		
		StyledDocument doc1 = tp_type1.getStyledDocument();
		SimpleAttributeSet center1 = new SimpleAttributeSet();
		StyleConstants.setAlignment(center1, StyleConstants.ALIGN_JUSTIFIED);
		doc1.setParagraphAttributes(0, doc1.getLength(), center1, false);
		
		tp_type2 = new JTextPane();
		tp_type2.setFont(new Font("Cambria", Font.BOLD, 18));
		tp_type2.setText("To be used if the desired data is spread across a table which is added or updated in the database regularly, and having the same structure. Builds normal, nested and date based queries and generates required output.");
		tp_type2.setEditable(false);
		
		StyledDocument doc2 = tp_type2.getStyledDocument();
		SimpleAttributeSet center2 = new SimpleAttributeSet();
		StyleConstants.setAlignment(center2, StyleConstants.ALIGN_JUSTIFIED);
		doc2.setParagraphAttributes(0, doc2.getLength(), center2, false);

				
		tp_type3 = new JTextPane();
		tp_type3.setFont(new Font("Cambria", Font.BOLD, 18));
		tp_type3.setText("Generate summarized reports according to specified requirements using aggregation on a table that is added or updated regularly in a given database.");
		tp_type3.setEditable(false);
		
		StyledDocument doc3 = tp_type1.getStyledDocument();
		SimpleAttributeSet center3 = new SimpleAttributeSet();
		StyleConstants.setAlignment(center3, StyleConstants.ALIGN_JUSTIFIED);
		doc3.setParagraphAttributes(0, doc3.getLength(), center3, false);

				
		
		// ---------------------------------------Buttons---------------------------------------
		
		btn_type1 = new JButton("TYPE 1");
		btn_type1.setForeground(new Color(255, 255, 255));
		btn_type1.setBackground(new Color(0, 102, 204));
		btn_type1.setFont(new Font("Cambria", Font.BOLD, 18));
		
		btn_type2 = new JButton("TYPE 2");
		btn_type2.setForeground(new Color(255, 255, 255));
		btn_type2.setBackground(new Color(0, 102, 204));
		btn_type2.setFont(new Font("Cambria", Font.BOLD, 18));
		
		btn_type3 = new JButton("TYPE 3");
		btn_type3.setForeground(new Color(255, 255, 255));
		btn_type3.setBackground(new Color(0, 102, 204));
		btn_type3.setFont(new Font("Cambria", Font.BOLD, 18));
		
		// ---------------------------------------Set Bounds---------------------------------------
		

		tp_type1.setBounds(w/4-w/12, h/4-h/18, 500, 150);
		tp_type2.setBounds(w/4-w/12, h/2-h/13, 500, 150);
		tp_type3.setBounds(w/4-w/12, 3*h/4-h/9, 500, 150);
		
		btn_type1.setBounds(w/2+w/12, h/4-h/18, 150, 50);
		btn_type2.setBounds(w/2+w/12, h/2-h/13, 150, 50);
		btn_type3.setBounds(w/2+w/12, 3*h/4-h/9, 150, 50);
		
		
		// ---------------------------------------Component Additions------------------------------
		
		contentPane.add(tp_type1);
		contentPane.add(tp_type2);
		contentPane.add(tp_type3);
		
		contentPane.add(btn_type1);
		contentPane.add(btn_type2);
		contentPane.add(btn_type3);
		
		// ---------------------------------------Listeners----------------------------------------
		
		btn_type1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					MyConnection.db_name = "ignition";
					new QueryInputSimple();
					Menu.this.setVisible(false);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btn_type2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					MyConnection.db_name = "ignition";
					new QueryInputComplex();
					Menu.this.setVisible(false);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btn_type3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					MyConnection.db_name = "test";
					new QueryInputIntense();
					Menu.this.setVisible(false);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
	}// end of init components
	
}// class ends
