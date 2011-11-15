package org.snu.ids.ha.tools;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.snu.ids.ha.dic.Dictionary;
import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.Sentence;
import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 
 * </pre>
 * @author 	Dongjoo
 * @since	2009. 10. 22
 */
public class TesterGUI
	extends JFrame
{
	public static void main(String[] args)
	{
		TesterGUI gui = new TesterGUI();
		gui.setVisible(true);
	}


	public TesterGUI()
	{
		setSize(1024, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Korean Morpheme Analyzer Tester");

		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());

		JTabbedPane tabPane = new JTabbedPane();

		// add morpheme analyzer tester
		tabPane.addTab("색인어 추출기", new KEPanel());
		tabPane.addTab("분석기", new MAPanel());

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabPane, getLogPanel());
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(600);

		c.add(sp, BorderLayout.CENTER);

		statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		statusPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		
		lineLabel = new JLabel();
		statusPanel.add(lineLabel);
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(200, 15));
		progressBar.setBorderPainted(false);
		statusPanel.add(progressBar);
		
		statusLabel = new JLabel();
		statusPanel.add(statusLabel);
		

		c.add(statusPanel, BorderLayout.SOUTH);
	}


	JTextArea			logText		= null;
	JPanel				statusPanel	= null;
	KeywordExtractor	ke			= null;
	JProgressBar		progressBar	= null;
	JLabel				lineLabel	= null;
	JLabel				statusLabel	= null;


	void createKE()
	{
		startJob("사전 읽기");
		Timer timer = new Timer();
		timer.start();
		ke = new KeywordExtractor();
		timer.stop();
		endJob(timer.getInterval());
	}
	
	
	void startJob(String job)
	{
		progressBar.setIndeterminate(true);
		progressBar.setBorderPainted(true);
		statusLabel.setText(job);
		printlog(job);
	}
	
	void endJob(double interval)
	{
		progressBar.setIndeterminate(false);
		progressBar.setBorderPainted(false);
		statusLabel.setText(interval + "초");
		printlog("완료: " + interval + "초");
	}


	public JPanel getLogPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Console"));
		logText = new JTextArea();
		logText.setTabSize(4);
		logText.setEditable(false);
		JScrollPane sp = new JScrollPane();
		sp.getViewport().add(logText);
		panel.add(sp, BorderLayout.CENTER);
		return panel;
	}


	void printlog(final String log)
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				logText.append(log + "\n");
			}
		};
		thread.start();
	}


	class MAPanel
		extends JPanel
		implements ActionListener
	{
		JTextField	inputText	= null;
		JTextArea	resultText	= null;


		public MAPanel()
		{
			this.setLayout(new BorderLayout());
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			JPanel topButtonPanel = new JPanel(new BorderLayout());
			topButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			inputText = new JTextField();
			topButtonPanel.add(inputText, BorderLayout.CENTER);

			JButton button = new JButton("분석");
			button.setActionCommand("ANALYZE");
			button.addActionListener(this);
			topButtonPanel.add(button, BorderLayout.EAST);

			button = new JButton("사전 재로딩");
			button.setActionCommand("RELOAD");
			button.addActionListener(this);

			topButtonPanel.add(button, BorderLayout.WEST);
			add(topButtonPanel, BorderLayout.NORTH);

			JPanel resultPanel = new JPanel(new BorderLayout());
			resultPanel.setBorder(BorderFactory.createTitledBorder("분석 결과"));

			resultText = new JTextArea();
			resultText.setTabSize(4);
			resultText.setEditable(false);
			JScrollPane sp = new JScrollPane();
			sp.getViewport().add(resultText);
			resultPanel.add(sp);

			add(resultPanel, BorderLayout.CENTER);
		}


		/**
		 * <pre>
		 * </pre>
		 * @author	therocks
		 * @since	2009. 09. 04
		 * @param arg0
		 */
		public void actionPerformed(ActionEvent arg0)
		{
			String cmd = arg0.getActionCommand();
			if( cmd.equals("ANALYZE") ) {
				analyze();
			} else if( cmd.equals("RELOAD") ) {
				Thread thread = new Thread()
				{
					public void run()
					{
						startJob("사전 다시 읽기");
						Timer timer = new Timer();
						timer.start();
						Dictionary.reload();
						timer.stop();
						endJob(timer.getInterval());
					}
				};
				thread.start();
			}
		}


		/**
		 * <pre>
		 * 
		 * </pre>
		 * @author	Dongjoo
		 * @since	2009. 11. 06
		 */
		void analyze()
		{
			Thread thread = new Thread()
			{
				public void run()
				{
					String str = inputText.getText();

					StringBuffer sb = new StringBuffer();
					if( ke == null ) createKE();
					try {
						Timer timer = new Timer();
						timer.start();
						List<MExpression> ret = ke.leaveJustBest(ke.postProcess(ke.analyze(str)));
						timer.stop();
						printlog("총 분석 시간: " + timer.getInterval());

						List<Sentence> stl = ke.divideToSentences(ret);
						for( int i = 0; i < stl.size(); i++ ) {
							Sentence st = stl.get(i);
							sb.append(st.getSentence() + "\n");
							for( int j = 0; j < st.size(); j++ ) {
								sb.append("\t" + st.get(j) + "\n");
							}
							sb.append("\n");
						}

						resultText.setText(sb.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread.run();
		}
	}

	class KEPanel
		extends JPanel
		implements ActionListener
	{
		JTextArea	srcText			= null;
		JCheckBox	onlyNounCheck	= null;
		JTable		table			= null;
		KeywordList	keywordList		= null;
		File		recentDir		= null;


		public KEPanel()
		{
			super(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getSrcPane(), getResultPane());
			sp.setOneTouchExpandable(true);
			sp.setDividerLocation(500);
			this.add(sp, BorderLayout.CENTER);
		}


		public JPanel getSrcPane()
		{
			JPanel panel = new JPanel(new BorderLayout());

			JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

			onlyNounCheck = new JCheckBox("명사만 추출");
			menuPanel.add(onlyNounCheck);

			JButton button = new JButton("파일 열기");
			button.setActionCommand("OPEN_FILE");
			button.addActionListener(this);
			menuPanel.add(button);

			button = new JButton("파일로 저장");
			button.setActionCommand("SAVE_TO_FILE");
			button.addActionListener(this);
			menuPanel.add(button);

			button = new JButton("분석하기");
			button.setActionCommand("ANALYZE");
			button.addActionListener(this);
			menuPanel.add(button);

			panel.add(menuPanel, BorderLayout.NORTH);

			srcText = new JTextArea();
			srcText.setTabSize(4);
			JScrollPane sp = new JScrollPane();
			sp.getViewport().add(srcText);

			JPanel srcPanel = new JPanel(new BorderLayout());
			srcPanel.setBorder(BorderFactory.createTitledBorder("Contents"));
			srcPanel.add(sp);

			panel.add(srcPanel, BorderLayout.CENTER);

			return panel;
		}


		public JPanel getResultPane()
		{
			JPanel panel = new JPanel(new BorderLayout());

			table = new JTable(new KeywordDataModel());
			table.setAutoCreateRowSorter(true);

			JScrollPane sp = new JScrollPane();
			sp.getViewport().add(table);

			panel.add(sp, BorderLayout.CENTER);

			return panel;
		}


		/**
		 * <pre>
		 * </pre>
		 * @author	Dongjoo
		 * @since	2009. 11. 06
		 * @param ae
		 */
		@Override
		public void actionPerformed(ActionEvent ae)
		{
			String cmd = ae.getActionCommand();

			if( cmd.equals("OPEN_FILE") ) {
				JFileChooser jfc = new JFileChooser();
				if( recentDir == null ) {
					File curDir = new File("");
					recentDir = curDir.getAbsoluteFile();
				}
				jfc.setCurrentDirectory(recentDir);
				if( jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
					File file = jfc.getSelectedFile();
					recentDir = file.getParentFile();
					readFile(file);
				}
			} else if( cmd.equals("SAVE_TO_FILE") ) {
				JFileChooser jfc = new JFileChooser();
				File curDir = new File("");
				jfc.setCurrentDirectory(curDir.getAbsoluteFile());
				if( jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ) {
					File file = jfc.getSelectedFile();
					recentDir = file.getParentFile();
					saveToFile(file);
				}
			} else if( cmd.equals("ANALYZE") ) {
				analyze();
			}
		}


		void readFile(File file)
		{
			BufferedReader br = null;
			try {
				printlog("READING FILE: " + file.getAbsolutePath());
				br = new BufferedReader(new FileReader(file));

				String line = null;
				cleanSrcText();
				while( (line = br.readLine()) != null ) {
					srcText.append(line + "\n");
				}
				srcText.updateUI();
				br.close();
			} catch (Exception e) {
				printlog("ERROR: " + e.toString());
			}
		}


		void saveToFile(File file)
		{
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(file);
				
				for( int i = 0, size = keywordList == null ? 0 : keywordList.size(); i < size; i++ ) {
					Keyword keyword = keywordList.get(i);
					pw.println(keyword.getIndex() + "\t" + keyword.getString() + "\t" + keyword.getTag() + "\t" + keyword.getCnt());
				}
				pw.flush();
				pw.close();
			} catch (Exception e) {
				printlog("ERROR: " + e.toString());
			}
		}


		void cleanSrcText()
		{
			srcText.setText("");
		}


		void analyze()
		{
			Thread thread = new Thread()
			{
				public void run()
				{
					String string = srcText.getText();
					if( !Util.valid(string) ) {
						printlog("분석할 문장이 없습니다.");
						return;
					}
					try {
						if( ke == null ) createKE();
						startJob("단어 추출");
						Timer timer = new Timer();
						timer.start();
						keywordList = ke.extractKeyword(progressBar, lineLabel, string, onlyNounCheck.isSelected());
						updateTableMode();
						printlog("전체 단어 수: " + keywordList.getDocLen());
						timer.stop();
						endJob(timer.getInterval());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
		}


		void updateTableMode()
		{
			table.setModel(new KeywordDataModel());
			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
			Comparator<Integer> intComparator = new Comparator<Integer>()
			{

				@Override
				public int compare(Integer arg0, Integer arg1)
				{
					return arg0.intValue() - arg1.intValue();
				}
			};
			sorter.setComparator(0, intComparator);
			sorter.setComparator(3, intComparator);
			table.setRowSorter(sorter);
			table.updateUI();
		}


		class KeywordDataModel
			extends AbstractTableModel
		{
			/**
			 * <pre>
			 * </pre>
			 * @author	Dongjoo
			 * @since	2009. 11. 06
			 * @return
			 */
			@Override
			public int getColumnCount()
			{
				return 4;
			}


			/**
			 * <pre>
			 * </pre>
			 * @author	Dongjoo
			 * @since	2009. 11. 06
			 * @return
			 */
			@Override
			public int getRowCount()
			{
				return keywordList == null ? 0 : keywordList.size();
			}


			public String getColumnName(int col)
			{
				switch (col) {
					case 0:
						return "위치";
					case 1:
						return "단어";
					case 2:
						return "품사";
					case 3:
						return "횟수";
				}
				return null;
			}


			/**
			 * <pre>
			 * </pre>
			 * @author	Dongjoo
			 * @since	2009. 11. 06
			 * @param row
			 * @param col
			 * @return
			 */
			@Override
			public Object getValueAt(int row, int col)
			{
				if( keywordList == null || row >= keywordList.size() ) return null;
				Keyword keyword = keywordList.get(row);
				switch (col) {
					case 0:
						return (int) keyword.getIndex();
					case 1:
						return keyword.getString();
					case 2:
						return keyword.getTag();
					case 3:
						return keyword.getCnt();

				}
				return null;
			}

		}
	}

}
