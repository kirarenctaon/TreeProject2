package tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class AppMain extends JFrame implements TreeSelectionListener, ActionListener{
	JPanel p_west, p_center;
	JTree tree;
	JScrollPane scroll;
	DefaultMutableTreeNode root=null;
	JTextArea area;
	String path="C:/java_workspace/TreeProject/data/";
	JButton bt_play;
	String fileLocation;
	public AppMain() {
		p_west=new JPanel();
		p_center=new JPanel();
		
		//jtree �������� �ֻ������ �����ϴ� �޼��� ȣ��
		//createNode(); 
		//createDirectory();
		createMusicDir();
		
		tree=new JTree(root);
		scroll=new JScrollPane(tree);
		
		p_west.setPreferredSize(new Dimension(200, 500));
		p_west.setLayout(new BorderLayout());
		area=new JTextArea();
		
		bt_play=new JButton("���");
		
		p_west.add(scroll);
		add(p_west, BorderLayout.WEST);
		add(p_center);
		add(area);
		add(bt_play, BorderLayout.SOUTH);
		
		//tree�� ������ ����
		tree.addTreeSelectionListener(this);
		bt_play.addActionListener(this);
		
		setSize(700, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void createNode(){
		root=new DefaultMutableTreeNode("����");
		
		DefaultMutableTreeNode node1=null;
		DefaultMutableTreeNode node2=null;
		DefaultMutableTreeNode node3=null;
		DefaultMutableTreeNode node4=null;
		DefaultMutableTreeNode node5=null;
		
		//�ڽĳ�� ����
		node1=new DefaultMutableTreeNode("��纣��");
		node2=new DefaultMutableTreeNode("����");
		node3=new DefaultMutableTreeNode("���Ի� ����");
		node4=new DefaultMutableTreeNode("����");
		node5=new DefaultMutableTreeNode("�ƺ�ī��");
		
		//�ڽĳ�� ���̱�
		root.add(node1);
		root.add(node2);
		root.add(node3);
		//�ڽĿ� �ڽ� ���̱�
		node3.add(node4);
		node3.add(node5);
	}
	
	//�������� ������ �����ֱ�(���� Ž����)
	public void createDirectory(){
		root = new DefaultMutableTreeNode("����ǻ��");
		File[] drive=File.listRoots();
		//��ũ ���� ������ ǥ�����ش�. 
		FileSystemView fsv = FileSystemView.getFileSystemView();
		
		/*  ���� �������̽��� ����ڰ� �ڽ��� new���� �ʵ��� 
		  	 �ν��Ͻ��� �ٷ�  ����� �� �ֵ��� static �޼��带 �������ִ� ��찡 ����. 
		  	 Ư�� ��ȯ���� �������̽���� ���ٸ� �ν��Ͻ� ������ �����ִ� �޼����� �ǽ��غ���! 
		 */
		for(int i=0;i<drive.length;i++){
			String volum=fsv.getSystemDisplayName(drive[i]);
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(volum);
			
			root.add(node);
		}
	}
	
	public void createMusicDir(){
		root=new DefaultMutableTreeNode("��ũ�ڽ�");
		File file=new File(path);
		File[] child=file.listFiles();
		
		for(int i=0;i<child.length;i++){
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(child[i].getName());
			root.add(node);
		}	
	}
	
	//������ ����� ���Ͽ� ���� ���� �����ϱ�
	public void extract(String filename) throws FileNotFoundException{
		
		area.removeAll();
		
	    fileLocation = path+filename;
	    BodyContentHandler handler = new BodyContentHandler();
	    Metadata metadata = new Metadata();
	    FileInputStream inputstream = null;
	       
      	try {
			inputstream=new FileInputStream(new File(fileLocation));
		} catch (java.io.FileNotFoundException e1) {
			e1.printStackTrace();
		}

      	 ParseContext pcontext = new ParseContext();
	       
	     //Mp3 parser
      	 Mp3Parser  Mp3Parser = new  Mp3Parser();
	     LyricsHandler lyrics;
	       
		 try {
		     Mp3Parser.parse(inputstream, handler, metadata, pcontext);
		     lyrics = new LyricsHandler(inputstream,handler);
		     
		     while(lyrics.hasLyrics()) {
		    	 area.append(lyrics.toString());
		     }
		  } catch (IOException e) {
			  e.printStackTrace();
		  } catch (SAXException e) {
			  e.printStackTrace();
		  } catch (TikaException e) {
			  e.printStackTrace();
		  }
			      
	  	area.append("Contents of the document:" + handler.toString());
	  	area.append("Metadata of the document:");
		
	  	String[] metadataNames = metadata.names();
	   
		for(String name : metadataNames) {          
	       	area.append(name + ": " + metadata.get(name));  
		}
	}
	
	//������ MP3���� ���, JLayer
	public void playMusci(){
		FileInputStream fis=null;
		try {
			fis =new FileInputStream(new File(fileLocation));
			AdvancedPlayer player = new AdvancedPlayer(fis);
			player.play();
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	/*
	public void play(){
		try{
		    FileInputStream fis = new FileInputStream(new File(fileLocation));
		    Player playMP3 = new Player(fis);
		    playMP3.play();
		}
		catch(Exception exc){
		    exc.printStackTrace();
		    System.out.println("Failed to play the file.");
		}
	}*/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		playMusci();
		//play();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object obj=e.getSource();
		JTree tree=(JTree)obj;
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		
		try {
			extract(node.getUserObject().toString());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		/* System.out.println(node.getUserObject()); 
		
		Object obj=tree.getLastSelectedPathComponent();�� sysout���� �ص� ���� �̸��� �������� 	
		�װ� ����� ��ȯ�Ǵ°� �ƴ϶� Object Ŭ�������� �ڵ����� toString�� �ۿ��ϴ� ��
		��Ģ������ ��ü(�繰)�� ����� ����� �ƴϸ�, �������� ���Ǹ� ���� ����� �����Ǵ� ���� ���̴�.  */
	}
	
	public static void main(String[] args) {
		new AppMain();
	}

	

}
