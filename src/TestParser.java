import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.Trees;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;


public class TestParser extends JFrame {
	
	class PNodeInfo implements Serializable{
		String title;
		ParseTree node;
		
		public PNodeInfo(String title, ParseTree node){
			this.title = title;
			this.node = node;
		}
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public ParseTree getNode() {
			return node;
		}
		public void setNode(ParseTree node) {
			this.node = node;
		}
		
		public String toString(){
			return getTitle();
		}
		
	}
	
	private mxGraph graph;
	
	public static void main(String[] args){
		String filePath = args[0];
		TestParser parser = new TestParser();
		parser.setSize(800, 600);
		parser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		parser.setVisible(true);
		try {
			parser.parse(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TestParser(){
		graph = new mxGraph();
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphComponent.setToolTips(true);
		JScrollPane panel = new JScrollPane(graphComponent);
		setContentPane(panel);
	}
	
	public mxGraph getGraph() {
		return graph;
	}

	protected void parse(String path) throws IOException{
		FileReader reader = new FileReader(new File(path));
		ANTLRInputStream ais = new ANTLRInputStream(reader);
		mssqlLexer lexer = new mssqlLexer(ais);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		
		mssqlParser parser = new mssqlParser(tokens);
		
		mssqlParser.SelectContext ctx = parser.select();
		DefaultMutableTreeNode root = parseToTree(null, ctx, parser);
		reader.close();
		draw(root);
//		id();
	}
	
	private DefaultMutableTreeNode parseToTree(DefaultMutableTreeNode parent, ParseTree node, Parser parser){
		DefaultMutableTreeNode n = new DefaultMutableTreeNode();
		PNodeInfo info = new PNodeInfo(Trees.getNodeText(node, parser), node);
		n.setUserObject(info);
		if(null != parent)
			parent.add(n);
		if(node.getChildCount() > 0){
			for(int i = 0; i < node.getChildCount(); i++){
				parseToTree(n, node.getChild(i), parser);
			}
		}
		return n;
	}
	
	private void draw(DefaultMutableTreeNode node){
		mxGraph g = getGraph();
		
		g.getModel().beginUpdate();
		internalDraw(g, null, node);
		
		mxCompactTreeLayout layout = new mxCompactTreeLayout(g, false){
			protected int prefHozEdgeSep = 5;
			protected int prefVertEdgeOff = 10;
		};
		
        layout.execute(g.getDefaultParent());
        
        g.getModel().endUpdate();
        
	}
	final int PORT_DIAMETER = 20;

	final int PORT_RADIUS = PORT_DIAMETER / 2;
	private mxCell internalDraw(mxGraph g, mxCell parent, DefaultMutableTreeNode node){
		mxCell cell = (mxCell)g.insertVertex(g.getDefaultParent(), null, node.getUserObject(), 0, 0, 20, 50, "");
		cell.setConnectable(false);
		for(int i = 0; i < node.getChildCount(); i++){
			mxCell child = internalDraw(g, cell, (DefaultMutableTreeNode)node.getChildAt(i));
			mxCell edge = (mxCell)g.insertEdge(cell, null, "", cell, child, "strokeWidth=3;spacing=10");
			
		}
		return cell;
	}
	
	private void id(){
		mxGraph g = getGraph();
		Object parent = g.getDefaultParent();
        g.getModel().beginUpdate();
        try {
             Object v1 = g.insertVertex(parent, null, "node1", 100, 100,0,
                        0);
             Object v2 = g.insertVertex(parent, null, "node2", 0, 0,
                        0, 0);
             Object v3 = g.insertVertex(parent, null, "node3", 0, 0,
                        0, 30);
       //     graph.insertEdge(parent, null, "Edge", v1, v2);
       //     graph.insertEdge(parent, null, "Edge", v2, v3);
        } finally {
            g.getModel().endUpdate();
        }
	}
	
}
