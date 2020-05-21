import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sound.midi.Soundbank;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import cryptographyAlgorithm.AES;

public class MainMenu {

	private static JFrame jFrame;
	private static JPanel topPanel;
	private static JPanel bottomPanel;
	private static JPanel midPanel;
	
	static private Queue<String> filesInProcessQueue = new LinkedList<String>();
	static private Queue<String> filesNameQueue = new LinkedList<String>();
	static private String keyPathString="";
	static private JRadioButton modeEnJRadioButton;
	static private JRadioButton modeDeJRadioButton;
	
	public MainMenu() {
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getUI();

	}
	private static void getUI() {
		jFrame = new JFrame("Crytography");
		jFrame.setPreferredSize(new Dimension(900,880));
		jFrame.setResizable(false);
		topPanel = new JPanel(new BorderLayout());
		midPanel = new JPanel(new BorderLayout());
		bottomPanel = new JPanel(new BorderLayout());
		
		midPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		//===============Top Panel======================
		//North of top panel 
		JLabel topJLabel = new JLabel("Crytography V.1.0");
		topJLabel.setHorizontalAlignment(SwingConstants.CENTER);
		Font tittleFont = new Font("Serif", Font.BOLD, 24);
		topJLabel.setFont(tittleFont);
		topPanel.add(topJLabel,BorderLayout.NORTH);
		//West and East of top panel
		JLabel plainFileJLabel = new JLabel("Plain FILE");
		Font labelFont = new Font("Verdana",Font.PLAIN,18);
		plainFileJLabel.setFont(labelFont);
		plainFileJLabel.setBorder(new EmptyBorder(0, 70, 0, 0));
//		plainFileJLabel.setHorizontalAlignment(SwingConstants.CENTER);
		plainFileJLabel.setAlignmentX(10);
		topPanel.add(plainFileJLabel,BorderLayout.WEST);
		
		JLabel crytoJLabel = new JLabel("Crypto FILE");
		crytoJLabel.setFont(labelFont);
		crytoJLabel.setBorder(new EmptyBorder(0, 0, 0, 70));
		topPanel.add(crytoJLabel,BorderLayout.EAST);
		
		//===============Mid Panel======================
		
		//Left of mid panel
		Font f = new Font("Serif", Font.BOLD, 15); 
		JTextArea plainTextArea = new JTextArea(5,25);
		plainTextArea.setFont(f);
		plainTextArea.setLineWrap(true);
		plainTextArea.setSize(300, 50);
		JScrollPane plainJScrollPane = new JScrollPane(plainTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		midPanel.add(plainJScrollPane,BorderLayout.WEST);
		plainTextArea.setEditable(true);
		
		
		
		//center of mid panel
		JLabel piJLabel = new JLabel();
		piJLabel.setIcon(new ImageIcon(new ImageIcon("image/muiten.png").getImage().getScaledInstance(100, 40, Image.SCALE_DEFAULT)));
		piJLabel.setHorizontalAlignment(SwingConstants.CENTER);
		piJLabel.setVerticalAlignment(SwingConstants.CENTER);
		midPanel.add(piJLabel,BorderLayout.CENTER);
		
		//Left of mid panel
		JTextArea crytoTextArea = new JTextArea(5,25);
		crytoTextArea.setFont(f);
		crytoTextArea.setLineWrap(true);
		JScrollPane crytoJScrollPane = new JScrollPane(crytoTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		midPanel.add(crytoJScrollPane,BorderLayout.EAST);
		plainTextArea.setEditable(true);
		
		
		
		//===============Bottom Panel======================
		
		//North of Bottom Panel
		modeEnJRadioButton= new JRadioButton("Encrypt");
		modeDeJRadioButton = new JRadioButton("Decrypt");
		modeDeJRadioButton.setHorizontalAlignment(SwingConstants.CENTER);
		modeEnJRadioButton.setHorizontalAlignment(SwingConstants.CENTER);
		modeDeJRadioButton.setFont(labelFont);
		modeEnJRadioButton.setFont(labelFont);
		modeDeJRadioButton.setBorder(new EmptyBorder(0, 0, 20, 0));
		ButtonGroup group = new ButtonGroup();
		group.add(modeEnJRadioButton);
		group.add(modeDeJRadioButton);
		modeEnJRadioButton.setSelected(true);
		
		midPanel.add(modeEnJRadioButton,BorderLayout.SOUTH);
		bottomPanel.add(modeDeJRadioButton,BorderLayout.NORTH);
		
		JTextArea procJTextArea = new JTextArea(20,10);
		
		procJTextArea.setFont(new Font("Serif", Font.BOLD, 15));
		procJTextArea.setLineWrap(true);
		JScrollPane scrollV = new JScrollPane (procJTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		bottomPanel.add(scrollV,BorderLayout.SOUTH);
		procJTextArea.setEditable(false);
		
		//right of bottom panel
		JButton chooseButton = new JButton("Browser");
		chooseButton.setFont(labelFont);
//		chooseButton.setBounds(100,360,120,60);
		bottomPanel.add(chooseButton,BorderLayout.WEST);
		chooseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] listFiles =  getFiles(modeEnJRadioButton.isSelected()?true:false);
				String itemStrings = "";
				for(File file : listFiles) {
					filesInProcessQueue.add(file.getAbsolutePath());
					filesNameQueue.add(file.getName());
					itemStrings+=file.getAbsolutePath()+"\n";
					
				}
				if(modeEnJRadioButton.isSelected()) {
					plainTextArea.setText(plainTextArea.getText()+itemStrings);
				}
				else {
					crytoTextArea.setText(plainTextArea.getText()+itemStrings);
				}
				
			}
		});
		
		//center of bottom panel
		JButton procButton = new JButton("Proccess");
		procButton.setFont(labelFont);
//		procButton.setBounds(390,360,120,60);
//		procButton.setBorder(new EmptyBorder(0, 50, 0, 50));
		procButton.setMargin(new Insets(0, 20, 0, 0));
		bottomPanel.add(procButton);
		procButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int count = filesInProcessQueue.size();
				// TODO Auto-generated method stub
				if(modeEnJRadioButton.isSelected()) {
					while(!filesInProcessQueue.isEmpty()) {
						try {
							
							AES encrypAes = new AES(procJTextArea,filesInProcessQueue.poll(),filesNameQueue.poll(), keyPathString);
							try {
								encrypAes.Encrypt();
							} catch (InvalidAlgorithmParameterException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalBlockSizeException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (BadPaddingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
					}
				}
				else {
					while(!filesInProcessQueue.isEmpty()) {
						try {
							
							AES decryptAes = new AES(procJTextArea,filesInProcessQueue.poll(),filesNameQueue.poll(), keyPathString);
							try {
								decryptAes.Decrypt();
							} catch (InvalidAlgorithmParameterException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalBlockSizeException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (BadPaddingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								procJTextArea.setText(procJTextArea.getText()+"\n"+e1.getMessage());
							}
						} catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
					}
					
				}
				plainTextArea.setText("");
				crytoTextArea.setText("");
				keyPathString = "";
				if(!filesNameQueue.isEmpty()) {
					filesNameQueue.clear();
				}
				
			}
		});
		
		//left of bottom panel
		JButton loadKeyJButton = new JButton("Load Key");
		loadKeyJButton.setFont(labelFont);
		bottomPanel.add(loadKeyJButton,BorderLayout.EAST);
		loadKeyJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File[] keyFiles = getFiles(modeEnJRadioButton.isSelected()?true:false);
				
				try {
					
					keyPathString = keyFiles[0].getAbsolutePath();
					
				} catch (Exception e2) {
					// TODO: handle exception
				}
				
				
			}
		});
		
		
		//
		jFrame.add(topPanel,BorderLayout.NORTH);
		jFrame.add(midPanel,BorderLayout.CENTER);
		jFrame.add(bottomPanel,BorderLayout.SOUTH);
		jFrame.pack();
		jFrame.setVisible(true);
		
	}
	private static File[] getFiles(boolean isDir) {
		
		JFileChooser chooser = new JFileChooser("D:\\Sourcecode\\JAVA\\ASS1_Cryptography_Project\\test");
		chooser.setMultiSelectionEnabled(true);
		if (isDir) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		int tf = chooser.showOpenDialog(null);
		if(tf==JFileChooser.APPROVE_OPTION) {
			File[] listFiles = chooser.getSelectedFiles();
			return listFiles;
		}
		else {
			return null;
		}
	}

}
