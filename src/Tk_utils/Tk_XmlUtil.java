package Tk_utils;
import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
//import javax.xml.transform.Result;
//import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.*;
import org.xml.sax.*;
import java.io.*;

import java.net.*;

public class Tk_XmlUtil
{
	/**
	 * Convenience function for getting an XML schema for validation from the
	 * file schema_file.
	 * 
	 * @param schema_file	The file containing the xml schema.
	 * @return				A javax.xml.validation.Schema object created from
	 * 						schema_file
	 * 
	 * @throws SAXException          	thrown by SchemaFactory.newSchema
	 * @throws NullPointerException		thrown by SchemaFactory.newSchema
	 */
	public static Schema getSchema(File schema_file)
			throws SAXException, NullPointerException
	{
		SchemaFactory factory =
				SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		return factory.newSchema(schema_file);
		
	}

	/**
	 * Read the xsd file and return a new xml Schema instance from it.
	 *
	 * @param xsd	A string containing the URI for the xml schema file.
	 *
	 * @return	An xml Schema object for the schema described in xsd.
	 */
	public static Schema getSchema(String xsd_name)
	{
		/*
		 * Create a file object for the xml schema we will be validating against.
		 */
		File xsd_file = null;
		try
		{
			xsd_file = new File(xsd_name);
		}
		catch (IllegalArgumentException e)
		{
			System.out.printf
			(
				"Tk_XmlUtil.getSchema: Failed to create File object for %s.\n",
				xsd_name
			);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}
		catch (NullPointerException e)
		{
			System.out.printf
			(
				"Tk_XmlUtil.getSchema: Failed to create File object for %s.\n",
				xsd_name
			);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}
		
		/*
		 * Get the xml schema object
		 */
		Schema xml_schema = null;
		if (xsd_file.exists())
		{
			try
			{
				 xml_schema = Tk_XmlUtil.getSchema(xsd_file);
			}
			catch (SAXException e)
			{
				System.out.printf
				(
					"Tk_XmlUtil: Syntax Error in schema %s\n",
					xsd_file.getAbsolutePath()
				);
				System.out.println(e.getMessage());
				System.out.println(e.getStackTrace());
				System.exit(-1);
			}
		}
		return xml_schema;
	}

	/**
	 * Get the first descendent of Element with tag = name.
	 *
	 * @param element 	The element to search from.
	 * @param name		The tagname of the element we are looking for.
	 *
	 * @return The Element we are searching for, it it is in the tree under
	 * 			element.
	 */
	public static Element getFirstElement(Element element, String name)
	{
		NodeList nl = element.getElementsByTagName( name );
		if ( nl.getLength() < 1 ) 
		{
			//throw new RuntimeException("Element: "+element+" does not contain: "+name);
			return null;
		}
		return (Element)(nl.item(0));
	}

	/**
	 * Return the string data contained in the node with tag == name under
	 * the Element node.
	 *
	 * @param node 	The node to search under.
	 * @param name  The tagname for the element we are looking for.
	 *
	 * @return		A String containing the text in the first Element under
	 * 				node that has tagname == name.
	 */
	public static String getSimpleElementText(Element node, String name)
	{
		Element namedElement = getFirstElement( node, name);
		return getSimpleElementText(namedElement);
	}
	
	/**
	 * Get the text content of the text element node.
	 * @param node  A text element we want to extract the value of.
	 * @return      A string containing the text read from node.
	 */
	public static String getSimpleElementText(Element node)
	{
		StringBuffer sb = new StringBuffer();
		NodeList children = node.getChildNodes();
		for(int i=0; i<children.getLength(); i++)
		{
			Node child = children.item(i);
			if ( child instanceof Text )
				sb.append( child.getNodeValue() );
		}
		return sb.toString();
	}
	
	/**
	 * Verify an xml document according to a schema, and parse the xml to
	 * create a Dom.
	 *
	 * @param xml 	Name of the file containing the xml.
	 * @param xsd	Name of the file containing the xml schema.
	 *
	 * @return		A document created by the parser from the xml file. 
	 */
	public static Document createDocument(String xml_name, String xsd_name)
	{
		File 	xml_file		= null;
		File	xsd_file		= null;

		/*
		 * Create a file object for the xml file we will be reading and/or writing.
		 */
		try
		{
			xml_file = new File(xml_name);
		}
		catch (IllegalArgumentException e)
		{
			System.out.printf
			(
				"Tk_XmlUtil.createDocument: Failed to create File object for %s.\n",
				xml_name
			);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}
		catch (NullPointerException e)
		{
			System.out.printf
			(
				"Tk_XmlUtil.createDocument: Failed to create File object for %s.\n",
				xml_name
			);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}

		/*
		 * Create a file object for the xml schema we will be validating against.
		 */
		try
		{
			xsd_file = new File(xsd_name);
		}
		catch (IllegalArgumentException e)
		{
			System.out.printf
			(
				"Tk_XmlUtil.createDocument: Failed to create File object for %s.\n",
				xsd_name
			);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}
		catch (NullPointerException e)
		{
			System.out.printf
			(
				"Tk_XmlUtil.createDocument: Failed to create File object for %s.\n",
				xsd_name
			);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}

		
		/*
		 * Get the xml schema object
		 */
		Schema xml_schema = null;
		if (xsd_file.exists())
		{
			try
			{
				 xml_schema = Tk_XmlUtil.getSchema(xsd_file);
			}
			catch (SAXException e)
			{
				System.out.printf
				(
					"Tk_XmlUtil: Syntax Error in schema %s\n",
					xsd_file.getAbsolutePath()
				);
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}
		
		/*
		 * Begin dealing with the xml file. 
		 */
		if (!xml_file.exists())
		{
			System.out.printf
			(
				"Error: file %s does not exist in call to Tk_XmlUtil constructor.",
				xml_file.getAbsoluteFile()
			);
			System.exit(-1);
		}	

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		dbf.setSchema(xml_schema);
		dbf.setValidating(false);

		DocumentBuilder doc_builder = null;
		try
		{
			doc_builder = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			System.out.printf
			(
				"Tk_XmlUtil.createDocument: Error in configuration of the DocBuilder instance!\n%s",
				e.getMessage()
			);
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}
	
		/*
		 * Setup and set the error handler for the parser.
		 */
		ErrorHandler errHandler = new ErrorHandler()
		{
			public void error( SAXParseException e)
			{
				System.out.println("In createDocument:");
				System.out.println(e.getMessage());
			}
			public void fatalError( SAXParseException e)
			{
				System.out.println("In createDocument:");
				System.out.println(e.getMessage());
				
			}
			public void warning( SAXParseException e)
			{
				System.out.println("In createDocument:");
				System.out.println(e.getMessage());
			}
        };
        doc_builder.setErrorHandler(errHandler);
        
        /*
         * Build the dom.
         */
        Document dom = null;
        try
        {
        	dom = doc_builder.parse(xml_file);
        }
        catch (SAXException e)
        {
        	// Error handler should have picked this up.
        	System.out.println("Tk_XmlUtil.createDocument: Error!");
        	System.out.println("NOTE: I don't think we should get here.");
        	System.out.println("TkTaskCollection.createDocument(): SAXException!");
        	System.out.printf
        	(
        		"Exception occured during parse of %s\n",
        		xml_file.getAbsolutePath()
        	);
        	System.out.printf("%s\n", e.getMessage());
        	System.out.println(e.getStackTrace());
        	System.exit(-1);
        }
        catch (IOException e)
        {
        	System.out.println("Tk_XmlUtil.createDocument(): IO Exception");
        	System.out.printf("Couldn't parse file: %s\n", xml_file.getAbsolutePath());
        	System.out.printf("%s\n", e.getMessage());
        	System.out.println(e.getStackTrace());
        	System.exit(-1);
        }
        catch (IllegalArgumentException e)
        {
        	System.out.println("TkXmlUtil.createDocument(): Illegal Argument!");
        	System.out.printf("Couldn't parse file: %s\n", xml_file.getAbsolutePath());
        	System.out.printf("%s\n", e.getMessage());
        	System.out.println(e.getStackTrace());
        	System.exit(-1);
        }
        return dom;
	}

	/**
	 * Create and return a formatted string of xml from the supplied dom.
	 *
	 * @param dom 	The Dom we want to convert to a formatted xml string.
	 *
	 * @return		A string containing reasonably formatted xml.
	 */
	public static String formatDoc(Document dom)
	{
		try
		{
			Source source = new DOMSource(dom);
			StreamResult xmlOutput = new StreamResult(new StringWriter());
			Transformer transformer = TransformerFactory.newInstance( ).newTransformer( );
			//transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "testing");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, xmlOutput);
			return xmlOutput.getWriter().toString();
		}
		catch (Exception e)
		{
			System.out.println("Caught Transformer exception!");
			System.out.println(e.getMessage());
		}
		return "";
	}
	
	/**
	 * Create an empty DOM document that conforms to the schema file xsd.
	 *
	 * @param xsd 	A URI for the schema that applies to the document.
	 *
	 * @return	An empty xml DOM Document.
	 */
	public static Document createEmptyDom(String xsd)
	{
		Schema schema = getSchema(xsd);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);
		dbf.setSchema(schema);
		
		DocumentBuilder doc_builder = null;
		try
		{
			doc_builder = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			System.out.printf
			(
				"Error: in configuration of the DocBuilder instance!\n%s",
				e.toString()
			);
			System.exit(-1);
		}

		/*
		 * Setup and set the error handler for the parser.
		 */
		ErrorHandler errHandler = new ErrorHandler()
		{
			public void error( SAXParseException e){ System.out.println(e); }
			public void fatalError( SAXParseException e) { System.out.println(e); }
			public void warning( SAXParseException e) { System.out.println(e); }
        };
        doc_builder.setErrorHandler(errHandler);
        
        // Return a document created by this doc_builder.
        return doc_builder.newDocument();
	}

	/**
	 * Create an empty DOM document that conforms to the schema file xsd.
	 *
	 * @param xsd 	A URI for the schema that applies to the document.
	 *
	 * @return	An empty xml DOM Document.
	 */
	public static Document createEmptyDom()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		
		DocumentBuilder doc_builder = null;
		try
		{
			doc_builder = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			System.out.printf
			(
				"Error: in configuration of the DocBuilder instance!\n%s",
				e.toString()
			);
			System.exit(-1);
		}

		/*
		 * Setup and set the error handler for the parser.
		 */
		ErrorHandler errHandler = new ErrorHandler()
		{
			public void error( SAXParseException e){ System.out.println(e); }
			public void fatalError( SAXParseException e) { System.out.println(e); }
			public void warning( SAXParseException e) { System.out.println(e); }
        };
        doc_builder.setErrorHandler(errHandler);
        
        // Return a document created by this doc_builder.
        return doc_builder.newDocument();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String xml_file = 
				"/Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Utils/TestData/TK_HistoryExample.xml";
				//"file:///Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Utils/TestData/TK_TaskList.xml";
		String xsd_file = 
				"/Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Utils/TestData/TK_History.xsd";
				//"file:////Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Utils/TestData/TK_TaskList.xsd";
		
		Document dom = createDocument(xml_file, xsd_file);
		String f_output = formatDoc(dom);
		
		System.out.println("The Formatted Output from the formatDoc method is:\n\n");
		System.out.printf("%s", f_output);

	}

}
