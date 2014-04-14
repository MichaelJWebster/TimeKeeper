package Tk_utils;
import javax.xml.XMLConstants;
import javax.xml.validation.*;
import org.xml.sax.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class Tk_ValidateXML
{
	public static void main( String [] args ) throws Exception
	{
		if ( args.length != 2 )
		{
			System.err.println("usage: Tk_ValidateXML xmlfile.xml xsdfile.xsd");
			System.exit(1);
		}
		
		String xmlfile = args[0], xsdfile = args[1];
		SchemaFactory factory =
				SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory.newSchema(new StreamSource(xsdfile));
		Validator validator = schema.newValidator();
		
		ErrorHandler errHandler = new ErrorHandler()
		{
			public void error( SAXParseException e){ System.out.println(e); }
			public void fatalError( SAXParseException e) { System.out.println(e); }
			public void warning( SAXParseException e) { System.out.println(e); }
        };
        
        validator.setErrorHandler( errHandler );
        try
        {
        	validator.validate( new SAXSource(new InputSource(xmlfile)));
        }
        catch ( SAXException e )
        {
        	// Invalid Document, no error handler
        	System.exit(-1);
        }
        System.out.printf("The xml file:\n\t%s\nwas validated with schema:\n\t%s\n", xmlfile, xsdfile);
        
	}
}

