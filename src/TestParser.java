import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;


public class TestParser extends JFrame {
	
	private static final long serialVersionUID = -57453049356031356L;
	
	class PNodeInfo implements Serializable{
		private static final long serialVersionUID = -7484223513630476576L;
		
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
//		mxStylesheet edgeStyle = new mxStylesheet();
//	    edgeStyle.setDefaultEdgeStyle(getEdgeStyle());
		setEdgeStyle(g);
		
		g.getModel().beginUpdate();
		internalDraw(g, null, node);
		
		mxCompactTreeLayout layout = new mxCompactTreeLayout(g, false);
		
        layout.execute(g.getDefaultParent());
        
        g.getModel().endUpdate();
        
	}
	
	private void setEdgeStyle(mxGraph g){
		Map<String, Object> edge = g.getStylesheet().getDefaultEdgeStyle();
	    edge.put(mxConstants.STYLE_ROUNDED, true);
	    edge.put(mxConstants.STYLE_ORTHOGONAL, true);
	    edge.put(mxConstants.STYLE_EDGE, "elbowEdgeStyle");
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
	    edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#446299");
	}
	
	private mxCell internalDraw(mxGraph g, mxCell parent, DefaultMutableTreeNode node){
		mxCell cell = (mxCell)g.insertVertex(g.getDefaultParent(), null, node.getUserObject(), 0, 0, 20, 50, "");
		cell.setConnectable(false);
		for(int i = 0; i < node.getChildCount(); i++){
			mxCell child = internalDraw(g, cell, (DefaultMutableTreeNode)node.getChildAt(i));
			g.insertEdge(cell, null, "", cell, child, "");
		}
		g.updateCellSize(cell);
		return cell;
	}
}
