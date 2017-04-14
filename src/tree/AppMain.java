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
		
		//jtree 생성전에 최상위노드 생성하는 메서드 호출
		//createNode(); 
		//createDirectory();
		createMusicDir();
		
		tree=new JTree(root);
		scroll=new JScrollPane(tree);
		
		p_west.setPreferredSize(new Dimension(200, 500));
		p_west.setLayout(new BorderLayout());
		area=new JTextArea();
		
		bt_play=new JButton("재생");
		
		p_west.add(scroll);
		add(p_west, BorderLayout.WEST);
		add(p_center);
		add(area);
		add(bt_play, BorderLayout.SOUTH);
		
		//tree와 리스너 연결
		tree.addTreeSelectionListener(this);
		bt_play.addActionListener(this);
		
		setSize(700, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void createNode(){
		root=new DefaultMutableTreeNode("과일");
		
		DefaultMutableTreeNode node1=null;
		DefaultMutableTreeNode node2=null;
		DefaultMutableTreeNode node3=null;
		DefaultMutableTreeNode node4=null;
		DefaultMutableTreeNode node5=null;
		
		//자식노드 생성
		node1=new DefaultMutableTreeNode("블루베리");
		node2=new DefaultMutableTreeNode("레몬");
		node3=new DefaultMutableTreeNode("수입산 과일");
		node4=new DefaultMutableTreeNode("라임");
		node5=new DefaultMutableTreeNode("아보카도");
		
		//자식노드 붙이기
		root.add(node1);
		root.add(node2);
		root.add(node3);
		//자식에 자식 붙이기
		node3.add(node4);
		node3.add(node5);
	}
	
	//윈도우의 구조를 보여주기(파일 탐색기)
	public void createDirectory(){
		root = new DefaultMutableTreeNode("내컴퓨터");
		File[] drive=File.listRoots();
		//디스크 볼륨 정보를 표현해준다. 
		FileSystemView fsv = FileSystemView.getFileSystemView();
		
		/*  보통 인터페이스는 사용자가 자식을 new하지 않도고 
		  	 인스턴스를 바로  사용할 수 있도록 static 메서드를 제공해주는 경우가 많다. 
		  	 특히 반환형이 인터페이스명과 같다면 인스턴스 생성을 도와주는 메서드라고 의심해보자! 
		 */
		for(int i=0;i<drive.length;i++){
			String volum=fsv.getSystemDisplayName(drive[i]);
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(volum);
			
			root.add(node);
		}
	}
	
	public void createMusicDir(){
		root=new DefaultMutableTreeNode("쥬크박스");
		File file=new File(path);
		File[] child=file.listFiles();
		
		for(int i=0;i<child.length;i++){
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(child[i].getName());
			root.add(node);
		}	
	}
	
	//선택한 노드의 파일에 대한 정보 추출하기
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
	
	//선택한 MP3파일 재생, JLayer
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
		
		Object obj=tree.getLastSelectedPathComponent();을 sysout으로 해도 파일 이름이 찍히지만 	
		그건 제대로 반환되는게 아니라 Object 클래스에서 자동으로 toString이 작용하는 것
		원칙적으로 객체(사물)은 출력의 대상이 아니며, 개발자의 편의를 위해 출력이 지원되는 것일 뿐이다.  */
	}
	
	public static void main(String[] args) {
		new AppMain();
	}

	

}
