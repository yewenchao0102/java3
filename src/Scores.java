import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.sql.*;

public class Scores {
	public static void main(String [] args){
		new ScoresWindow("成绩查询系统");
	}
}

class ScoresWindow extends JFrame{
	private static final long serialVersionUID = -6924196421756933199L;
	Statement statement;
	Connection connection;
	String commandString;
	ResultSet resultSet;
	
	JButton addJButton=new JButton("确认");
	JTextField noJTextField=new JTextField(9);
	JTextField scoresJTextField=new JTextField(5);
	JPanel inputPanel=new JPanel(false);
	
	JButton queryJButton=new JButton("查询");
	JTextField queryNOJTextField=new JTextField(9);
	JTextArea queryJTextArea=new JTextArea();
	JPanel queryJPanel=new JPanel(false);
	
	JButton sortJButton=new JButton("排序");
	Object clonameObject[]={"学号","成绩"};
	Integer rows=0;
	DefaultTableModel tableModel=new DefaultTableModel(clonameObject,rows);
	JTable table=new JTable(tableModel);
	
	JPanel sortJPanel=new JPanel(false);
	
	Dimension dimension=new Dimension(100,25);
	
	public ScoresWindow(String titleString) {
		super(titleString);
		JTabbedPane jTabbedPane=new JTabbedPane();
		JComponent inputPanel=inputJComponent();
		JComponent queryPanel=queryJComponent();
		JComponent sortPanel=sortJComponent();
		jTabbedPane.addTab("输入成绩", inputPanel);
		
		jTabbedPane.addTab("查询成绩", queryPanel);
		
		jTabbedPane.addTab("成绩排序", sortPanel);
		
		setLayout(new GridLayout(1,1));
		add(jTabbedPane);
		jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setBounds(100, 100, 300, 400);
		setVisible(true);
		validate();
		
		addJButton.addActionListener(new AddAction());
		queryJButton.addActionListener(new QueryAction());
		sortJButton.addActionListener(new SortAction());
		
		getConnectDataBase();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	protected class AddAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String noString;
			try {
				noString=noJTextField.getText(0, 9);
				commandString="INSERT INTO scores VALUES('"+noString+"','"+scoresJTextField.getText()+"')";
				statement=connection.createStatement();
				statement.execute(commandString);
				statement.closeOnCompletion();
				alterJComponent(inputPanel, "添加成功！");
				statement.closeOnCompletion();
				} catch (Exception e2) {
					alterJComponent(inputPanel, "学号为9位！请重新输入！\n"+e2.toString()+"");
			}
		}
	}
	
	protected class QueryAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try {
				statement=connection.createStatement();
				String noString=queryNOJTextField.getText(0, 9);
				commandString="SELECT * FROM SCORES WHERE NUMBER='"+noString+"'";
				resultSet=statement.executeQuery(commandString);
				resultSet.first();
				queryJTextArea.append("您查询的学号为 "+resultSet.getString(1)+" 的成绩为 "+resultSet.getString(2)+"\n");
				statement.closeOnCompletion();
			} catch (Exception e2) {
				alterJComponent(queryJPanel, "学号为9位！请重新输入！\n"+e2.toString()+"");
			}
			
			
		}
	}
	
	protected class SortAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			commandString="SELECT * FROM scores ORDER BY score DESC";
			try {
				statement=connection.createStatement();
				resultSet=statement.executeQuery(commandString);
				if(tableModel.getRowCount()>0) {
					tableModel=(DefaultTableModel)table.getModel();
					tableModel.setRowCount(rows*2);
					table.setModel(tableModel);
					table.repaint();
				}
				while (resultSet.next()) {
					
					tableModel.addRow(new Object[]{resultSet.getString(1),resultSet.getString(2)});
				}
				statement.closeOnCompletion();
				}catch (Exception e2) {
				alterJComponent(sortJPanel, "错误！\n"+e2.toString()+"");
			}
		}
	}
	
	
	protected JComponent inputJComponent(){
		
		Box studentLabelBox,studentFieldBox,baseBox;
		JLabel nolJLabel=new JLabel("学号");
		JLabel scoresJLabel=new JLabel("成绩");
		
		studentLabelBox=Box.createVerticalBox();
		studentLabelBox.add(nolJLabel);
		studentLabelBox.add(Box.createVerticalGlue());
		studentLabelBox.add(scoresJLabel);
		
		studentFieldBox=Box.createVerticalBox();
		studentFieldBox.add(noJTextField);
		studentFieldBox.add(Box.createVerticalStrut(8));
		studentFieldBox.add(scoresJTextField);
		
		baseBox=Box.createHorizontalBox();
		baseBox.add(studentLabelBox);
		baseBox.add(studentFieldBox);
		
		JPanel boxJPanel=new JPanel(false);
		boxJPanel.setLayout(new FlowLayout());
		boxJPanel.add(baseBox);
		boxJPanel.add(addJButton);
		
		noJTextField.setPreferredSize(dimension);
		scoresJTextField.setPreferredSize(dimension);
		
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(boxJPanel,BorderLayout.CENTER);
		
		
		return inputPanel;
	}
	protected JComponent queryJComponent(){
		JLabel noLabel=new JLabel("学号：");
		queryNOJTextField.setPreferredSize(dimension);
		
		queryJTextArea.setEditable(false);
		
		queryJPanel.setLayout(new BorderLayout());
		JPanel northPanel=new JPanel(false);
		northPanel.add(noLabel);
		northPanel.add(queryNOJTextField);
		northPanel.add(queryJButton);
		queryJTextArea.setAlignmentX(CENTER_ALIGNMENT);
		JScrollPane scrollPane=new JScrollPane(queryJTextArea);

		queryJPanel.add(scrollPane,BorderLayout.CENTER);
		queryJPanel.add(northPanel,BorderLayout.NORTH);
		return queryJPanel;
		
	}
	protected JComponent sortJComponent(){
		sortJPanel.setLayout(new BorderLayout());
		table.setEnabled(false);
		sortJPanel.add(new JScrollPane(table),BorderLayout.CENTER);
		
		JPanel sortButtonJPanel=new JPanel(false);
		sortButtonJPanel.setLayout(new FlowLayout());
		sortButtonJPanel.add(sortJButton);
		sortJPanel.add(sortButtonJPanel,BorderLayout.NORTH);
		return sortJPanel;
	}
	
	protected JComponent alterJComponent(JComponent jComponent,String infosString){
		JOptionPane jOptionPane=new JOptionPane();
		JOptionPane.showMessageDialog(jComponent, infosString);
		return jOptionPane;
	}
	
	protected void getConnectDataBase(){
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				System.out.println("MySQL驱动成功加载！");
			} catch (ClassNotFoundException e) {
				System.out.println(e);
			}
			connection=DriverManager.getConnection("jdbc:mysql://localhost/sqlbase?useUnicode=true&characterEncoding=GBK", "root", "");
			System.out.println("已成功连接到数据库！");
			alterJComponent(inputPanel, "已成功连接到数据库！");
			
		} catch (Exception e) {
			System.out.println(e);
			alterJComponent(inputPanel, "错误！\n"+e.toString()+"");
		}
	}
}