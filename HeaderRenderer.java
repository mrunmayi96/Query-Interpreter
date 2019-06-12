package com;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

class HeaderRenderer implements TableCellRenderer {

    DefaultTableCellRenderer renderer;

    public HeaderRenderer(JTable table) {
        renderer = (DefaultTableCellRenderer)
            table.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int col) {
        return renderer.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, col);
    }

	public Component getTableCellRendererComponent1(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}
}