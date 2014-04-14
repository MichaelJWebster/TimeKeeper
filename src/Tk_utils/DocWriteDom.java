package Tk_utils;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DocWriteDom
{
	public static void main(String[] av) throws IOException
	{
		DocWriteDom dw = new DocWriteDom();
		Document doc = dw.makeDoc();
		
		// Sadly, the write() method is not in the DOM spec, so we
		// have to cast the Document to its implementing class
		// in order to call the Write method.
		//
		// WARNING
		//
		// This code therefore depends upon the particular
		// parser implementation.
		//
		//((org.apache.crimson.tree.XmlDocument)doc).write(System.out);
		try
		{
			Transformer transformer = TransformerFactory.newInstance( ).newTransformer( );
			Source source = new DOMSource(doc);
			Result output = new StreamResult( System.out );
			transformer.transform(source, output);
		}
		catch (Exception e)
		{
			System.out.println("Caught Transformer exception!");
			System.out.println(e.toString());
		}
	}
	
	/** Generate the XML document */
	protected Document makeDoc()
	{
		try
		{
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = fact.newDocumentBuilder();
			Document doc = parser.newDocument();
			Node root = doc.createElement("Poem");
			doc.appendChild(root);
			Node stanza = doc.createElement("Stanza");
			root.appendChild(stanza);
			Node line = doc.createElement("Line");
			stanza.appendChild(line);
			line.appendChild(doc.createTextNode("Once, upon a midnight dreary"));
			line = doc.createElement("Line");
			stanza.appendChild(line);
			line.appendChild(doc.createTextNode("While I pondered, weak and weary"));
			return doc;
		}
		catch (Exception ex)
		{
			System.err.println("+============================+");
			System.err.println("| XML Error |");
			System.err.println("+============================+");
			System.err.println(ex.getClass( ));
			System.err.println(ex.getMessage( ));
			System.err.println("+============================+");
			return null;
		}
	}
}
